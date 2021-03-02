package NerdyLogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class NerdyJSONLog {

	public static ConfigureList configureList; 
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
	public static NetworkTable table,tempTable;
	public static NetworkTableInstance inst;

	// manually created list, must be exactly as entered into SmartDashboard.
	// working on automagically creating by reading networktable headings nad subtable headings. 
	// will store this in a configuration file as NetworkTable may not be available immediately and want to build gui to choose entries.
	// load default table config file on start, can switch to other from gui?

	public static String[] headings = { "Logger", "yaw", "Forward", "Rotation", "Strafe", 
									"Velocity/0", "Velocity/1", "Velocity/2", "Velocity/3", "Velocity X", "Velocity Y",
									"desiredState_x", "desiredState_y", "Current_x", "Current_y",
									"Feet Traveled/0", "Feet Traveled/1", "Feet Traveled/2", "Feet Traveled/3",
									"Module Angle (Degrees)/0", "Module Angle (Degrees)/1", "Module Angle (Degrees)/2", "Module Angle (Degrees)/3"};


	public static NetworkTableEntry Entry[] = new NetworkTableEntry[headings.length];

	//***********************************************************************************
	
	public static void run() { 

		//configureList.createAndShowGUI();

		inst = NetworkTableInstance.getDefault();
		table = inst.getTable("SmartDashboard");
		//fms = inst.getTable("FMSInfo");
		//eventName = fms.getEntry("EventName");
		//matchNumber = fms.getEntry("MatchNumber");

		//***************************************************************************
		// Initiate NetwrokTable entries by iterating a list array.
		for (int i = 0; i < headings.length; i++) {
			Entry[i] = table.getEntry(headings[i]);
		}
		//Velocity_XEntry   			= table.getEntry("Velocity X");

		//*******************************************************************************
		//inst.startClientTeam(2337);  // team # or use inst.startClient("hostname") or similar
		//inst.startDSClient();  // recommended if running on DS computer; this gets the robot IP from the DS
		//inst.startClient("10.23.37.2");
		inst.startClient("10.0.1.5");

		loggerOn = false;	// This value is set to 'true' in Robot.init & then false at Robot.disable.
		System.out.println("LoggerOn: " + loggerOn + " - waiting on NetworkTables and/or Robot");
		int i = 0;

		// waiting for the "Logger" entry on the SmartDashboard to read 'true'.
		while(!loggerOn){
			loggerOn = Entry[0].getBoolean(false);
			System.out.print("."); i = i + 1; if(i>100) {System.out.println("-"); i=0;}
			sleepy();
		}

		initSB();	//Initialize the stringbuilder to add info to
		IOInfoSB();	//Add initail entry to stringbuilder that Wildloger uses to recognize entries  //MAYBE MOVE THIS BEFOE PREVIOUS WHILE STATEMENT TO SAVE TIME

		i=0;
		if(loggerOn) {System.out.println("Starting to log " + headings.length + " entries...");		} // need if?? cant get here unless it is true
		while(loggerOn){														// maybe add if disconnected....so file closes properly at end of match.
			loggerOn = Entry[0].getBoolean(false);
			stateSB(); // actual logging of current states
			sleepy();  //pause 50 milliseconds
			System.out.print("."); i = i + 1; if(i>100) {System.out.println("-"); i=0;}  //visual indicator its working.  take out in later iterations
		}
		finalStateSB();	// fix end of entries so that it is in the proper JSON format and then write to a text file.

		//run();  //use this to run again.  incase robot disconnected, but also to get both auton and teleop since "Logger" boolean goes 'false' in disable
	}

	
	/*
	 *	Creates the time stamped file and initiates buffer writer to that file. (ok)
	 * 
	 */
	public static void initSB() {
				//eventName.getString("test") + (int) matchNumber.getDouble(0.0) + //maybe add to file title
    	try {
    		f = new File("/Users/Public/Documents/log" + System.currentTimeMillis() + ".txt");
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
// experimental items, working on reading available SmartDashboard 'keys' (easy) & subtable 'keys' (working on) to creat a list for "headings"

	//retrieves main headings but not subheadings in subtables.
	public static String[] retrieveKeys(){

		Set<String> sdKeys = table.getKeys();

		Set<String> sdSubTables = table.getSubTables();
		Set<String> stKeys;
 
		Object objects1[] = sdKeys.toArray();
		Object objects2[] = sdSubTables.toArray();

		String[] SDSubTablesString = Arrays.copyOf(objects2, objects2.length, String[].class);
			for (String value2 : SDSubTablesString){
				System.out.println(value2 + " stK1");
				
			}
		///get subtable keys
		for (String value : SDSubTablesString) {
			tempTable = inst.getTable("SmartDashboard/"+value);
			stKeys = tempTable.getKeys();
			Object objects3[] = stKeys.toArray();
				for (Object value3 : objects3)
					System.out.println(value3 + " stK");
		}




		Object both[] = Arrays.copyOf(objects1, objects1.length + objects2.length);
		System.arraycopy(objects2, 0, both, objects1.length, objects2.length);

		System.out.println( "both arrays:" + both.length );
		System.out.println( "size:" + sdKeys.size() );
		
		String[] headings = Arrays.copyOf(objects1, objects1.length, String[].class);

		for (Object value : both)
			System.out.println(value + " bt");

		return headings;
		}


	
}