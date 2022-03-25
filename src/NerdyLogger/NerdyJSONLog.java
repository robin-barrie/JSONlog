// Working with JavaSE-1.8

package NerdyLogger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class NerdyJSONLog {

	public static ConfigureList configureList;
	static Menu menu = new Menu();


	public static void main(String[] args)  {

		configureList = new ConfigureList();

		NerdyJSONLog.run();
	}

	public static File f;
	public static StringBuilder builder = new StringBuilder();
	public static FileWriter fw_file;
	public static BufferedWriter bw_file;
	//public static NetworkTableEntry eventName, matchNumber;
	
	public static boolean loggerOn;
	public static NetworkTable table,tempTable,fms;
	public static NetworkTableInstance inst;

	// created list of headings from text file, must be exactly as entered into SmartDashboard.
	// working on automagically creating by reading networktable headings nad subtable headings. 
	// will store this in a configuration file as NetworkTable may not be available immediately and want to build gui to choose entries.
	// load default table config file on start, can switch to other from gui?

	/*
	public static String[] headings = { "Logger", 
									"yaw", "Forward", "Rotation", "Strafe", 
									"Velocity/0", "Velocity/1", "Velocity/2", "Velocity/3", 
									"Velocity X", "Velocity Y",
									"desiredState_x", "desiredState_y", "Current_x", "Current_y",
									"Feet Traveled/0", "Feet Traveled/1", "Feet Traveled/2", "Feet Traveled/3",
									"Module Angle (Degrees)/0", "Module Angle (Degrees)/1", "Module Angle (Degrees)/2", "Module Angle (Degrees)/3", "curTime", "NO"};

	*/
	public static String[] headings;
	
	public static NetworkTableEntry Entry[];

	public static NetworkTableEntry eventNameEntry, matchNumberEntry, matchTypeEntry, isRedAllianceEntry, replayNumberEntry, stationNumberEntry;

	//==============================================================================================================================================================
	
	public static void run() { 

		System.out.println("test0");

		Menu.createAndShowGUI();

           System.out.println("test1");
		String action = null;
		while(true){
           System.out.print(menu.getAction());
		if(menu.getAction()==null){action = "";} else {action = menu.getAction().toString();}
		System.out.print(action);
		if (action == "Start_Logger"){
			System.out.println("started");
            //NerdyJSONLog.runLogger();
        }

		//if(action == null){ System.out.print(".");}
		if(action != ""){

			System.out.print("action does not = null but = ");
			System.out.println(menu.getAction());

		switch(action){
			case "Start_Logger":
			   System.out.println("starting");
			   menu.setAction(null);
			   break;
			case "Save":
			   //do logic
			   break;
			case "Exit":
			   System.out.println("exiting");
			   System.exit(0);
			   break;
	    }
	    }

	    }
	}

	public static void runLogger(){

		builder.delete(0, builder.length()); 	//empty buffer if previously used
		headings = readLines();					// get headings from text file
		Entry = new NetworkTableEntry[headings.length];

		//configureList.createAndShowGUI(); //stole some code to try to create a menu system

		inst = NetworkTableInstance.getDefault();
		table = inst.getTable("SmartDashboard");
		fms = inst.getTable("FMSInfo");

		// Initiate NetworkTable entries by iterating a list array.
		for (int i = 0; i < headings.length; i++) {
			Entry[i] = table.getEntry(headings[i]);
		}

		//  Connect to SmartDashboard server
		//**********************************
		//inst.startClientTeam(2337);  		// team # or use inst.startClient("hostname") or similar
		//inst.startDSClient();  			// recommended if running on DS computer; this gets the robot IP from the DS
		//inst.startClient("10.23.37.2");  	//SkillzBot
		inst.startClient("10.0.1.81");  	//Robin's laptop running simulator

			loggerOn = false;	// This value is set to 'true' in Robot.init & then false at Robot.disable.
		System.out.println("LoggerOn: " + loggerOn + " - waiting on NetworkTables and/or Robot");
		int i = 0;

		//initSB();   //moved here to save time but creates empty file if logger doesnt start. Do we care?

		// waiting for the "Logger" entry on the SmartDashboard to read 'true'.
		while(!loggerOn){
			loggerOn = Entry[0].getBoolean(false);
			System.out.print("."); i = i + 1; if(i>100) {System.out.println("-"); i=0;}
			sleepy();
		}

		retrieveKeys(); //???+++++++++++++++++++++++  need here, after server connects, to get info.  Plan is to deveop menu and use to set up default file for later use

		initSB();	//Initialize the stringbuilder to add info to
		IOInfoSB();	//Add initail entry to stringbuilder that Wildloger uses to recognize entries  //MAYBE MOVE THIS BEFOE PREVIOUS WHILE STATEMENT TO SAVE TIME

		i=0;

		if(loggerOn) {System.out.println("Starting to log " + headings.length + " entries...");		} // need if??
		while(loggerOn){				
			stateSB(); // actual logging of current states
			sleepy(20);  //pause x milliseconds, 50 by default
			System.out.print("."); i = i + 1; if(i>100) {System.out.println("-"); i=0;}  //visual indicator its working.  take out in later iterations
			loggerOn = Entry[0].getBoolean(false); // check to see if robot is disabled, to end logging.
			if(!inst.isConnected()) {				// also stop logging if disconnected, like at the end of a match.
				loggerOn = false;
				Entry[0].setValue(false);
				System.out.println("Closing file. Disconnected from NetworkTable.");
			}
		}
		finalStateSB();	// Modify the end of the entries so that it is in the proper JSON format and then write to a text file.


		//run();  		//use this to run again.  incase robot disconnected, but also to get both auton and teleop since "Logger" boolean goes 'false' in disable
	}
//==============================================================================================================================================================
	
	/*
	 *	Creates the time stamped file and initiates buffer writer to that file. (ok)
	 * 
	 */
	public static void initSB() {
		String alliance;

		// Get certain FMS info to add to file name for easier retrieval later.
		eventNameEntry = fms.getEntry("EventName");
			String eventName = eventNameEntry.getString("");
				if (eventName == "") {eventName = "Premier";}
		matchNumberEntry = fms.getEntry("MatchNumber");
			double matchNumber = matchNumberEntry.getDouble(0.0);
		matchTypeEntry = fms.getEntry("MatchType");
			double matchType = matchTypeEntry.getDouble(0.0);
		replayNumberEntry = fms.getEntry("ReplayNumber");
			double replayNumber = replayNumberEntry.getDouble(0.0);
		isRedAllianceEntry = fms.getEntry("isRedAlliance");
			boolean isRedAlliance = isRedAllianceEntry.getBoolean(false);
				if (isRedAlliance) {alliance = "Red";} else {alliance = "Blue";}
		stationNumberEntry = fms.getEntry("StationNumber");
			double stationNumber = stationNumberEntry.getDouble(0.0);

		LocalDateTime now = LocalDateTime.now();
			int month = now.getMonthValue();
			int day = now.getDayOfMonth();
			int hour = now.getHour();
			int minute = now.getMinute();
			int second = now.getSecond();

    	try {
			//would like to add a folder but need to catch if folder doesnt exist
			f = new File("/Users/Public/Documents/logs/log_" 
							+ month  + "-"+ day + "_" + hour + "-" + minute + "-" + second +"_"
							+ eventName +"_mN"+ matchNumber +"_mT"+ matchType +"_rN"+ replayNumber +"_"+ alliance +"_sN"+ stationNumber
							+ ".txt");  
    		if(!f.exists()){
    			f.createNewFile();
    		}
			fw_file = new FileWriter(f);
		} catch (IOException e) {
			System.err.println("Caught IOException (init fw): " + e.getMessage());
		}
    	bw_file = new BufferedWriter(fw_file);
    	
		try  {
			bw_file.write(builder.toString());
			System.out.println();
			System.out.println("Successfully Created File..."+f);
		}catch (IOException e) {
		    System.err.println("Caught IOException (init bw): " + e.getMessage());
		}
	}	

	/*
	 *	IOInfo:
	 *	Initial entry that defines what 'can' be logged later
	 */
	public static void IOInfoSB() {
		
		//'name' (1st item) to match name in 'state' section.  
		// 'direction'  (3rd item)= Input/Output only.  
		// The (2nd and 4th item) are just for information( i.e type and then port).
		// Add a line for every item to be logged.  
		// Last entry has a unique ending
		//
		
		builder.append("{\"ioinfo\":[");
		
		// Add entries by iterating a list array.
		for (int i = 0; i < headings.length; i++) {
			addIOInfo(headings[i]			,"", "Input", "");
		}
		
		builder.setLength(Math.max(builder.length() - 1,0));  	//remove comma from last entry.
		builder.append("\n\t]");								//close out ioinfo entry.
		builder.append("\n\"state\":[");						//start state entries.
	}

	public static void addIOInfo(String name, String type, String direction, String port) {
		
		String m_name = name;
		String m_type = type;
		String m_direction = direction;
		String m_port = port;
 
		builder.append("\n\t{\"name\":\"");
		builder.append(m_name);
		builder.append("\",\"type\":\""); 
		builder.append(m_type);
		builder.append("\",\"direction\":\"");
		builder.append(m_direction);
		builder.append("\",\"port\":\"");
		builder.append(m_port);
		builder.append("\"}");	

		builder.append(",");	
	}

	
	/*
	 *	Edits end of "builder" to complete JSON format.
	 * 	Writes "builder" to bufferWriter(bw) and then closes bw so that it writes to file.
	 * 
	 */
		public static void finalStateSB() {
		builder.setLength(Math.max(builder.length() - 1,0));  	// removes comma from last entry.
		builder.append("\n\t] \n}");							// closes out JSON format
 
		 try{
			if(bw_file!=null)
			bw_file.write(builder.toString());
			 bw_file.close();
			 System.out.println();
			System.out.println("Successfully edited JSON and closed file..(finalStateSB).");
		 }catch(Exception ex){
		       System.out.println("Error in closing the BufferedWriter"+ex);
		    }
	}

	/*
	 *	adds slight pause between State entries while actively logging.
	 * 
	 */
	public static void sleepy(){
		try {
			Thread.sleep(50);
		  } catch (InterruptedException ex) {
			System.out.println("sleep interrupted");
			return;
		  }
	}
	public static void sleepy(int milliseconds){
		try {
			Thread.sleep(milliseconds);
		  } catch (InterruptedException ex) {
			System.out.println("sleep interrupted");
			return;
		  }
	}

	/*
	 *	Adds actual log entries of the current states while "Logger" is 'true'
	 * 
	 */
	public static void stateSB() {  
		
		builder.append("\n\t{\"timestamp\":\"" + System.currentTimeMillis() + "\",\"values\":[");
		
		// Add entries by iterating a list array, but get first entry ("Logger") as it is a Boolean.  maybe set up different section for Booleans later
			addState(headings[0],		headings[0]				,Entry[0].getBoolean(false));

		for (int i = 1; i < headings.length; i++) {
			addState(headings[i],		headings[i]				,Entry[i].getDouble(0.0));
		}

		builder.setLength(Math.max(builder.length() - 1,0));  	//removes comma from last entry.
		builder.append("\n\t\t] },");							//closes out state entry.
	}	
	/*
	 *  Multiple methods to add State(s) based on info type.
	 *  i.e. boolean, double, string (not sure if can use string in viewer)
	 */
	
	// For Doubles
	public static void addState(String name, String parent, double value) {
		
		String m_name = name;
		String m_parent = parent;
		double m_value = value;
 
		builder.append("\n\t\t{\"name\":\"");
		builder.append(m_name);
		builder.append("\",\"parent\":\""); 
		builder.append(m_parent);
		builder.append("\",\"value\":\"");
		builder.append(m_value);
		builder.append("\"},");	
	}
	
	//For Booleans
	public static void addState(String name, String parent, boolean value) {
				
		String m_name = name;
		String m_parent = parent;
		boolean m_value = value;

		builder.append("\n\t\t{\"name\":\"");
		builder.append(m_name);
		builder.append("\",\"parent\":\""); 
		builder.append(m_parent);
		builder.append("\",\"value\":\"");
		builder.append(m_value);
		builder.append("\"},");	
	}
	
	//For Strings, not sure we can do strings.
	public static void addState(String name, String parent, String value) {
		
		String m_name = name;
		String m_parent = parent;
		String m_value = value;
 
		builder.append("\n\t\t{\"name\":\"");
		builder.append(m_name);
		builder.append("\",\"parent\":\""); 
		builder.append(m_parent);
		builder.append("\",\"value\":\"");
		builder.append(m_value);
		builder.append("\"},");	
	}

	// ==========================================================================================
	// Retrieve available SmartDashboard headings & subtable headings as a String[] array.
	//  integrate into a menu system to create default file that can be loaded.

	//public static String[] retrieveKeys(){
		public static void retrieveKeys(){

		// Retrieve SmartDashboard headings
		Set<String> sdKeysSet = table.getKeys();
		Object sdKeysObject[] = sdKeysSet.toArray();

		// Convert Objects to Strings so that it can be used to call the subtables
		String[] sdKeysString = Arrays.copyOf(sdKeysObject, sdKeysObject.length, String[].class);

		// Retrieve names of subtables
		Set<String> sdSubTablesSet = table.getSubTables();
		Object sdSubTableObject[] = sdSubTablesSet.toArray();

		// Convert Objects to Strings so that they may be used to call the subtable headings
		String[] SDSubTablesString = Arrays.copyOf(sdSubTableObject, sdSubTableObject.length, String[].class);

		//Set<String> stKeysSet;
		// Create ArrayList that will initially hold all the heading names, from the main tbale and all subtables.
		ArrayList<String> sdFullHeadingList = new ArrayList<String>();

		// For each subtable, retrieve the headings.  Add the subtable name to each heading along with a "/", and then add tht to the ArrayList
		for (String value : SDSubTablesString) {
			tempTable = inst.getTable("SmartDashboard/"+value);
			Set<String> stKeysSet = tempTable.getKeys();
			Object stKeysObjects[] = stKeysSet.toArray();
				for (Object value3 : stKeysObjects){
					sdFullHeadingList.add(value + "/"+ value3);
				}
		}

		// Add SmartDashboard main headings to ArrayList.
		for (String value : sdKeysString) { sdFullHeadingList.add(value); }

		// Convert ArrayList to a array of Objects
		Object[] headingsObjects = sdFullHeadingList.toArray();

		// Convert array of Objects to an array of Strings
		String[] headings2 = Arrays.copyOf(headingsObjects, headingsObjects.length, String[].class);

		// Print for confirmation
		//for (String value : headings2) { System.out.println(value); }
		//System.out.println(sdFullHeadingList.size());
		System.out.println(headings2.length);

	//return headings2;
	}
	//public static void readLines() throws IOException {
	//	readLines("/Users/Public/Documents/LOGGER/default_list.txt");
	//}

	public static String[] readLines() {
		try {
        	FileReader fileReader = new FileReader("/Users/Public/Documents/LOGGER/default_list.txt");
         
        	BufferedReader bufferedReader = new BufferedReader(fileReader);
        	List<String> lines = new ArrayList<String>();
        	String line = null;
         
        	while ((line = bufferedReader.readLine()) != null) {
				lines.add(line);
        	}
         
        	bufferedReader.close();
         
			return lines.toArray(new String[lines.size()]);

		} catch (IOException e) {
			System.err.println("Caught IOException (init fw): " + e.getMessage());
			String[] fallback = { "Logger", "yaw", "Forward", "Rotation", "Strafe", "FALLBACK ENTRIES"};
			return fallback;
	 	}
    }   
	



}