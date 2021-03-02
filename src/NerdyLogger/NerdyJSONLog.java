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

	public static String[] headins = { "Logger", "yaw", "Forward", "Rotation", "Strafe", 
									"Velocity_0", "Velocity_1", "Velocity_2", "Velocity_3", "Velocity_X", "Velocity_Y",
									"desiredState_x", "desiredState_y", "Current_x", "Current_y"};


	public static NetworkTableEntry Entry[] = new NetworkTableEntry[headins.length];

	//***********************************************************************************
	//  There are 4 areas that need to be updated for each item that is to be logged.
	//  This includes the two below, as well as IOInfoSB() & stateSB().
	//***********************************************************************************
	/**
	public static NetworkTableEntry loggerEntry, isFieldOrientedEntry,
	yawEntry, RotationEntry, ForwardEntry, StrafeEntry, 
	Velocity_0Entry, Velocity_1Entry, Velocity_2Entry, Velocity_3Entry, Velocity_XEntry, Velocity_YEntry,
	desiredState_xEntry, desiredState_yEntry, Current_xEntry, Current_yEntry,
	testValue1Entry, testValue2Entry, testValue3Entry, testValue4Entry, testValue5Entry, testValue6Entry;
	**/
	//***********************************************************************************
	
	
	public static void run() { 

		//configureList.createAndShowGUI();

		inst = NetworkTableInstance.getDefault();
		table = inst.getTable("SmartDashboard");
		//fms = inst.getTable("FMSInfo");
		//eventName = fms.getEntry("EventName");
		//matchNumber = fms.getEntry("MatchNumber");
		//***************************************************************************
		// Add entries by iterating a list array.

		for (int i = 0; i < headins.length-1; i++) {
			Entry[i] = table.getEntry(headins[i]);
		}

		//*******************************************************************************
		/**
		loggerEntry					= table.getEntry("Logger");
		yawEntry					= table.getEntry("yaw");
		isFieldOrientedEntry		= table.getEntry("isFieldOriented");
		ForwardEntry				= table.getEntry("Forward");
		RotationEntry				= table.getEntry("Rotation");
		StrafeEntry					= table.getEntry("Strafe");
		desiredState_xEntry   		= table.getEntry("desiredState_x");
		desiredState_yEntry   		= table.getEntry("desiredState_y");
		Current_xEntry   			= table.getEntry("Current_x");
		Current_yEntry   			= table.getEntry("Current_y");
		Velocity_XEntry   			= table.getEntry("Velocity X");
		Velocity_YEntry   			= table.getEntry("Velocity Y");
		Velocity_0Entry				= table.getEntry("Velocity/0");
		Velocity_1Entry				= table.getEntry("Velocity/1");
		Velocity_2Entry				= table.getEntry("Velocity/2");
		Velocity_3Entry				= table.getEntry("Velocity/3");
		testValue1Entry				= table.getEntry("testValue1");
		testValue2Entry				= table.getEntry("testValue2");
		testValue3Entry				= table.getEntry("testValue3");
		testValue4Entry				= table.getEntry("testValue4");
		testValue5Entry				= table.getEntry("testValue5");
		testValue6Entry				= table.getEntry("testValue6");
		//*******************************************************************************
		**/
		//inst.startClientTeam(2337);  // team # or use inst.startClient("hostname") or similar
		//inst.startDSClient();  // recommended if running on DS computer; this gets the robot IP from the DS
		//inst.startClient("10.23.37.2");
		inst.startClient("10.0.1.5");

		loggerOn = false;	// This value is set to 'true' in Robot.init & then false at Robot.disable.
		System.out.println("LoggerOn: " + loggerOn + " - waiting on NetworkTables and/or Robot");
		int i = 0;

		//System.out.print( loggerEntry.getBoolean(false) );
		System.out.print( Entry[1].getBoolean(false) );

		while(!loggerOn){
			loggerOn = Entry[1].getBoolean(false);
			System.out.print("."); i = i + 1; if(i>100) {System.out.println("-"); i=0;}
			sleepy();
			//System.out.print( loggerEntry.getBoolean(false) );
			//System.out.print( yawEntry.getDouble(1) );
		}
		initSB();
		IOInfoSB();

		//retrieveKeys(); //get keys from Smartdashboard and try to automatically set up fields.


		i=0;
		if(loggerOn) {System.out.println("Begin logging..");} //need if??  
		while(loggerOn){														// maybe add if disconnected....
			loggerOn = Entry[1].getBoolean(false);
			stateSB();
			sleepy();		
			System.out.print("."); i = i + 1; if(i>100) {System.out.println("-"); i=0;}
		}
		finalStateSB();	

		//run();
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
		//  Maybe in future, read in details from an array, guarantees names match and only one place to edit****************
		
		builder.append("{\"ioinfo\":[");
		
		//*******************************************************************************
		/**
		addIOInfo("Logger"					, "", "Input", "");
		addIOInfo("isFieldOriented" 		, "", "Input", "");
		addIOInfo("yaw"						, "", "Input", "");
		addIOInfo("Forward"					, "", "Input", "");
		addIOInfo("Rotation"				, "", "Input", "");
		addIOInfo("Strafe"					, "", "Input", "");
		addIOInfo("desiredState_x"			, "", "Input", "");
		addIOInfo("desiredState_y"			, "", "Input", "");
		addIOInfo("Current_x"				, "", "Input", "");
		addIOInfo("Current_y"				, "", "Input", "");
		addIOInfo("Velocity X"		 		, "", "Input", "");
		addIOInfo("Velocity Y"				, "", "Input", "");
		addIOInfo("Velocity/0"				, "", "Input", "");
		addIOInfo("Velocity/1"				, "", "Input", "");
		addIOInfo("Velocity/2"				, "", "Input", "");
		addIOInfo("Velocity/3"				, "", "Input", "");
		addIOInfo("testValue1"				, "", "Input", "");
		addIOInfo("testValue2"				, "", "Input", "");
		addIOInfo("testValue3"				, "", "Input", "");
		addIOInfo("testValue4"				, "", "Input", "");
		addIOInfo("testValue5"				, "", "Input", "");
		addIOInfo("testValue6"				, "", "Input", "");
		*/
		//*******************************************************************************

		//***************************************************************************
		// Add entries by iterating a list array.
		for (int i = 1; i < headins.length; i++) {
			addIOInfo(headins[i]			,"", "Input", "");
		}
		
		builder.setLength(Math.max(builder.length() - 1,0));  	//remove comma from last entry.
		builder.append("\n\t]");								//close out ioinfo entry.
		builder.append("\n\"state\":[");						//start state entries.
	}


	// future:? to add items to IOInfo from array, or keep as is, adding all elements in one method.????

	
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
			System.out.println("Successfully edited JSON and closed file..(finalStateSB).");
		 }catch(Exception ex){
		       System.out.println("Error in closing the BufferedWriter"+ex);
		    }
	}

	public static void sleepy(){
		try {
			Thread.sleep(50);
		  } catch (InterruptedException ex) {
			System.out.println("sleep interrupted");
			return;
		  }
	}

		//*************************************figure out best way to write stateSB****  all in one our array to method**** */
	
	public static void stateSB() {  
		
		builder.append("\n\t{\"timestamp\":\"" + System.currentTimeMillis() + "\",\"values\":[");
		
		//***************************************************************************************
		/**
		addState("Logger",				"Logger"				,loggerEntry.getBoolean(false));
		addState("isFieldOriented",		"isFieldOriented"		,isFieldOrientedEntry.getBoolean(false));
		addState("yaw",					"yaw"					,yawEntry.getDouble(0.0));
		addState("Forward",				"Forward"				,ForwardEntry.getDouble(0.0));
		addState("Rotation",			"Rotation"				,RotationEntry.getDouble(0.0));
		addState("Strafe",				"Strafe"				,StrafeEntry.getDouble(0.0));
		addState("desiredState_x",		"desiredState_x"		,desiredState_xEntry.getDouble(0.0));
		addState("desiredState_y",		"desiredState_y"		,desiredState_yEntry.getDouble(0.0));
		addState("Current_x",			"Current_x"				,Current_xEntry.getDouble(0.0));
		addState("Current_y",			"Current_y"				,Current_yEntry.getDouble(0.0));
		addState("Velocity X",			"Velocity X"			,Velocity_XEntry.getDouble(0.0));
		addState("Velocity Y",			"Velocity Y"			,Velocity_YEntry.getDouble(0.0));
		addState("Velocity/0",			"Velocity/0"			,Velocity_0Entry.getDouble(0.0));
		addState("Velocity/1",			"Velocity/1"			,Velocity_1Entry.getDouble(0.0));
		addState("Velocity/2",			"Velocity/2"			,Velocity_2Entry.getDouble(0.0));
		addState("Velocity/3",			"Velocity/3"			,Velocity_3Entry.getDouble(0.0));
		addState("testValue1",			"testValue1"			,testValue1Entry.getDouble(0.0));
		addState("testValue2",			"testValue2"			,testValue2Entry.getDouble(0.0));
		addState("testValue3",			"testValue3"			,testValue3Entry.getDouble(0.0));
		addState("testValue4",			"testValue4"			,testValue4Entry.getDouble(0.0));
		addState("testValue5",			"testValue5"			,testValue5Entry.getDouble(0.0));
		addState("testValue6",			"testValue6"			,testValue6Entry.getDouble(0.0));
		*/
		//***************************************************************************************

		//***************************************************************************
		// Add entries by iterating a list array, but firt get first entry as it is a Boolean.  maybe set up different section for Booleans later
			addState(headins[1],		headins[1]				,Entry[1].getBoolean(false));
		for (int i = 2; i < headins.length; i++) {
			addState(headins[i],		headins[i]				,Entry[i].getDouble(0.0));
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

	//retrieves main headings but nut subheadings in subtables.
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