import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Test {
	public static void main(String[] args)  {
	Test.run();
	}

	public static File f;
	public static StringBuilder builder = new StringBuilder();
	public static FileWriter fw_file;
	public static BufferedWriter bw_file;
	public static NetworkTableEntry eventName, matchNumber;
	
	public static boolean loggerOn;
	
	public static double yaw;		//temp

	//***********************************************************************************
	//  There are 4 areas that need to be updated for each item that is to be logged.
	//  This includes the two below, as well as IOInfoSB() & stateSB().
	//***********************************************************************************
	public static NetworkTableEntry loggerEntry, yawEntry, RightEncoderValueEntry, LeftEncoderValueEntry, 
	leftChassisPOWEREntry, rightChassisPOWEREntry, turnValueEntry, leftOutputEntry, rightOutputEntry;
	//***********************************************************************************
	
	public static void run() { 

		NetworkTableInstance inst = NetworkTableInstance.getDefault();
		NetworkTable table = inst.getTable("SmartDashboard");
		NetworkTable fms = inst.getTable("FMSInfo");
		eventName = fms.getEntry("EventName");
		matchNumber = fms.getEntry("MatchNumber");
		
		//*******************************************************************************
		loggerEntry 				= table.getEntry("logger");
		yawEntry 					= table.getEntry("yaw");
		RightEncoderValueEntry 		= table.getEntry("Right Encoder Value");
		LeftEncoderValueEntry 		= table.getEntry("Left Encoder Value");
		leftChassisPOWEREntry 		= table.getEntry("left Chassis POWER");
		rightChassisPOWEREntry 		= table.getEntry("right Chassis POWER");
		turnValueEntry 				= table.getEntry("Turn Value");
		leftOutputEntry 			= table.getEntry("leftOutput");
		rightOutputEntry 			= table.getEntry("rightOutput");
		//*******************************************************************************
		
		inst.startServer("2337");  // team # or use inst.startClient("hostname") or similar
		//inst.startDSClient();  // recommended if running on DS computer; this gets the robot IP from the DS

	//int i=0;

		loggerOn = false;	// This value is set to 'true' in Robot.init & then false at Robot.disable.

		initSB();
		IOInfoSB();
	//loggerEntry.setBoolean(true);
		while(!loggerOn){
		loggerOn = loggerEntry.getBoolean(false);
		System.out.println("LoggerOn: " + loggerOn + "::"+table.getEntry("logger"));  // remove duplicate...
		
	//i=i+1;
	//if (i>10){ loggerEntry.setBoolean(true);}
		
		sleepy();
		}

		if(loggerOn) {System.out.println("Begin logging..");}
		while(loggerOn){
			loggerOn = loggerEntry.getBoolean(false);
			stateSB();
	//TESTstateSB();
			sleepy();
			
	//i=i+1;
	//if (i>20){loggerEntry.setBoolean(false);}
	//yaw = Math.sin(System.currentTimeMillis());
		
		}
		finalStateSB();	
		
		System.out.println("builder string: \n" + builder);		//TEMP
	}

	
	/*
	 *	Creates the time stamped file and initiates buffer writer to that file. (ok)
	 * 
	 */
	public static void initSB() {
				
    	try {
    		f = new File("/Users/Public/Documents/log" + eventName.getString("test") + (int) matchNumber.getDouble(0.0) + System.currentTimeMillis() + ".txt");
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
		addIOInfo("logger"					, "", "Input", "");
		addIOInfo("Right Encoder Value"		, "", "Input", "");
		addIOInfo("Left Encoder Value"		, "", "Input", "");
		addIOInfo("left Chassis POWER"		, "", "Input", "");
		addIOInfo("right Chassis POWER"		, "", "Input", "");
		addIOInfo("Turn Value"				, "", "Input", "");
		addIOInfo("leftOutput"				, "", "Input", "");
		addIOInfo("rightOutput"				, "", "Input", "");
		addIOInfo("Yaw"						, "", "Input", "");
		//*******************************************************************************
		
		builder.setLength(Math.max(builder.length() - 1,0));  	//remove comma from last entry.
		builder.append("\n\t]");								//close out ioinfo entry.
		builder.append("\n\"state\":[");						//start state entries.
	}


	// use to add items to IOInfo from array, or keep as is, adding all elements in one method.????

	
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
			Thread.sleep(1000);
		  } catch (InterruptedException ex) {
			System.out.println("sleep interrupted");
			return;
		  }
	}

	
		//*************************************figure out best way to write stateSB****  all in one our array to method**** */
	
	public static void stateSB() {  
		
		builder.append("\n\t{\"timestamp\":\"" + System.currentTimeMillis() + "\",\"values\":[");
		
		//***************************************************************************************
		addState("logger",				"logger"				,loggerEntry.getBoolean(false));
		addState("Right Encoder Value",	"Right Encoder Value"	,true);
		addState("Left Encoder Value",	"Right Encoder Value"	,true);
		addState("right Chassis POWER",	"left Chassis POWER"	,false);
		addState("left Chassis POWER",	"left Chassis POWER"	,false);
		addState("Turn Value",			"Turn Value"			,loggerOn);
		addState("rightOutput",			"leftOutput"			,yaw);
		addState("leftOutput",			"leftOutput"			,yaw);
		addState("yaw",					"yaw"					,yaw);
		//***************************************************************************************

		builder.setLength(Math.max(builder.length() - 1,0));  	//removes comma from last entry.
		builder.append("\n\t\t] },");							//closes out state entry.
	}	
	/*
	 *  Multiple methods to add State(s) based on info type.
	 *  i.e. boolean, double, string (not sure if can use string in viewer)
	 */

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
	
	// using to test overall program..  read network tables and and every 20ms? for actual program

	public static void TESTstateSB() {  

		builder.append("\n\t{\"timestamp\":\"" + System.currentTimeMillis() + "\",\"values\":[");
		addState("button1","driver-button1",false);
		addState("button2","driver-button2",true);
		addState("button4","driver-button4",false);
		addState("logger","loggerOn",loggerOn);
		addState("yaw","yaw",yaw);
		
		builder.setLength(Math.max(builder.length() - 1,0));  	//remove comma from last entry.
		builder.append("\n\t\t] },");							//close out state entry.
		
		try{ Thread.sleep(20);
			} catch (InterruptedException e) {
			e.printStackTrace();}
		
		
		builder.append("\n\t{\"timestamp\":\"" + System.currentTimeMillis() + "\",\"values\":[");
		addState("button1","driver-button1",false);
		addState("button2","driver-button2",false);
		addState("button4","driver-button4",false);
		addState("logger","loggerOn",loggerOn);
		addState("yaw","yaw",yaw);
		
		builder.setLength(Math.max(builder.length() - 1,0));  	//remove comma from last entry.
		builder.append("\n\t\t] },");							//close out state entry.
			
		try{ Thread.sleep(20);
		} catch (InterruptedException e) {
		e.printStackTrace();}

		
		builder.append("\n\t{\"timestamp\":\"" + System.currentTimeMillis() + "\",\"values\":[");
		addState("button1","driver-button1",true);
		addState("button2","driver-button2",true);
		addState("button4","driver-button4",true);	
		addState("logger","loggerOn",loggerOn);
		addState("yaw","yaw",yaw);
		
		builder.setLength(Math.max(builder.length() - 1,0));  	//remove comma from last entry.
		builder.append("\n\t\t] },");							//close out state entry.
	}	
=======
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
 
 
public class Test {
	public static void main(String[] args)  {
	new Test().run();
	}
	
	public static File f;
	public static StringBuilder builder = new StringBuilder();
	public static FileWriter fw_file;
	public static BufferedWriter bw_file;

    NetworkTableInstance inst = NetworkTableInstance.getDefault();
    NetworkTable table = inst.getTable("SmartDashboard");
    NetworkTableEntry loggingEntry = table.getEntry("logging");
    public NetworkTableEntry yawEntry = table.getEntry("yaw");

	
	
	public void run()  {
		
	    inst.startClientTeam(2337);  // where TEAM=190, 294, etc, or use inst.startClient("hostname") or similar
	    //inst.startDSClient();  // recommended if running on DS computer; this gets the robot IP from the DS
		
		initSB();
		IOInfoSB();
		//TEST2stateSB();
		stateSB();
		finalStateSB();
		//closeSB();			
	}
	
	
	//'name' to match name in 'state' section.
	//'parent': info only, can be anything.
	//'direction' = Input/Output. 
	//'port': info only, can be anything.
	// Add a line for every item to be logged.  Last entry has a unique ending
	
	public static void IOInfoSB() {
		
		builder.append("{\"ioinfo\":[");
		
		addIOInfo("button1", 	"button", 	"Input", 	"1");
		addIOInfo("button2", 	"button", 	"Input", 	"2");
		addIOInfo("button4", 	"button", 	"Input", 	"4");
		addIOInfo("yaw",		"yaw",		"Input",	"0");
		
		builder.setLength(Math.max(builder.length() - 1,0));  	//remove comma from last entry.
		builder.append("\n\t]");								//close out ioinfo entry.
		builder.append("\n\"state\":[");						//start state entries.
	}
	
	
	//'name' to match name in 'ioinfo' section.  'parent' is for information only.
	// Add 3(?) lines for every item to be logged, matching entries in 'ioinfo'.  
	// Use commands/methods to get 'value' for each entry.
	// Last entry has a unique ending
	
	public void stateSB() {
		
		builder.append("\n\t{\"timestamp\":\"");
		builder.append(System.currentTimeMillis());
		builder.append("\",\"values\":[");
		//builder.append("\t\t{\"name\":\"button1\",\"parent\":\"d-button1\",\"value\":\"");  //name needs to match IOInfo name.
		//builder.append(false);
		//builder.append("\"},\n");
		//builder.append("\t\t{\"name\":\"button2\",\"parent\":\"d-button2\",\"value\":\"false\"},\n");	
		//builder.append("\t\t{\"name\":\"button3\",\"parent\":\"d-button3\",\"value\":\"true\"}\n \t\t] }, \n"); // , - on last entry
		addStateDouble("name","parent",yawEntry.getDouble(0.0));  //0.0 = default value
		addStateBoolean("button1","driverbutton1",loggingEntry.getBoolean(false)); // false = default value
		addStateBoolean("button2","driverbutton1",loggingEntry.getBoolean(false)); // false = default value
		addStateBoolean("button3","driverbutton1",loggingEntry.getBoolean(true)); // false = default value
		
	}


	
	public static void initSB() {
					
    	try {
    		//f = new File("/Users/Robin/Documents/file3.txt");
    		f = new File("/Users/EngiNERD1/Documents/file" + System.currentTimeMillis() + ".txt");
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
			System.out.println("Successfully Copied INIT String to File...");
			System.out.println("builder string: \n" + builder);
		}catch (IOException e) {
		    System.err.println("Caught IOException (init bw): " + e.getMessage());
		}
	}
	
	
	public static void closeSB() {
		 try{
			if(bw_file!=null)
			 bw_file.close();
			System.out.println("Successfully Closed state String to File...");
			System.out.println("builder string: \n" + builder);
		   	}catch(Exception ex){
		       System.out.println("Error in closing the BufferedWriter"+ex);
		    }
	}
	
	public static void finalStateSB() {
		builder.setLength(Math.max(builder.length() - 1,0));  	//remove comma from last entry.
		builder.append("\n\t] \n}");								// close out JSON format
 
		 try{
			if(bw_file!=null)
			 bw_file.close();
			System.out.println("Successfully Closed state String to File...");
			System.out.println("builder string: \n" + builder);
		   	}catch(Exception ex){
		       System.out.println("Error in closing the BufferedWriter"+ex);
		    }
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
	
	
	
	public void addStateBoolean(String name, String parent, boolean value) {
		
		// have method for int, double, boolean & ???object??? et.al...
		
		String m_name = name;
		String m_parent = parent;
		boolean m_value = value;
		//boolean m_lastEntry = lastEntry;
		//boolean m_lastState = lastState;
 
		builder.append("\n\t\t{\"name\":\"");
		builder.append(m_name);
		builder.append("\",\"parent\":\""); 
		builder.append(m_parent);
		builder.append("\",\"value\":\"");
		builder.append(m_value);
		builder.append("\"},");	
	}
	
	public static void addStateDouble(String name, String parent, double value) {
				
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

	
	
	public void TEST2stateSB() {  
		
		//send boolean thru this method to identify last/final state and then pass to last entry.
		
		
		builder.append("\n\t{\"timestamp\":\"");
		builder.append("1463365197324");
		//builder.append("/n\t{\"timestamp\":\"" + System.currentTimeMillis() + "\",\"values\":[");
		builder.append("\",\"values\":[");
		addStateBoolean("button1","driver-button1",false);   //use getMethod instead of 'true/false' for value....
		addStateBoolean("button2","driver-button2",true);
		addStateBoolean("button4","driver-button4",false);
		addStateDouble("yaw","yaw",yawEntry.getDouble(0.0));
		
		builder.setLength(Math.max(builder.length() - 1,0));  	//remove comma from last entry.
		builder.append("\n\t\t] },");							//close out state entry.
		
		
		builder.append("\n\t{\"timestamp\":\"");
		builder.append("1463365197334");
		//builder.append(System.currentTimeMillis());
		builder.append("\",\"values\":[");
		addStateBoolean("button1","driver-button1",false);   //use getMethod instead of 'true/false' for value....
		addStateBoolean("button2","driver-button2",false);
		addStateBoolean("button4","driver-button4",false);
		
		builder.setLength(Math.max(builder.length() - 1,0));  	//remove comma from last entry.
		builder.append("\n\t\t] },");							//close out state entry.
			
		builder.append("\n\t{\"timestamp\":\"");
		builder.append("1463365197344");
		//builder.append(System.currentTimeMillis());
		builder.append("\",\"values\":[");
		addStateBoolean("button1","driver-button1",true);   //use getMethod instead of 'true/false' for value....
		addStateBoolean("button2","driver-button2",true);
		addStateBoolean("button4","driver-button4",true);	
		
		builder.setLength(Math.max(builder.length() - 1,0));  	//remove comma from last entry.
		builder.append("\n\t\t] },");							//close out state entry.
		
		//PRINT FILE EVERY TIME?????
		
	}
	
		public static void OLD_TESTstateSB() {  
		
			builder.append("\"state\":[\n");
			
			builder.append("\t{\"timestamp\":\"");
			builder.append("1463365197324");
			//builder.append(System.currentTimeMillis());
			builder.append("\",\"values\":[\n");
			builder.append("\t\t{\"name\":\"button1\",\"parent\":\"d-button1\",\"value\":\"");
			builder.append(false);
			builder.append("\"},\n");
			builder.append("\t\t{\"name\":\"button2\",\"parent\":\"d-button2\",\"value\":\"false\"},\n");	
			builder.append("\t\t{\"name\":\"button4\",\"parent\":\"d-button3\",\"value\":\"true\"}\n \t\t] }, \n"); // ]}, instead of , - on last entry
			
			builder.append("\t{\"timestamp\":\"");
			builder.append("1463365197334");
			//builder.append(System.currentTimeMillis());
			builder.append("\",\"values\":[\n");
			builder.append("\t\t{\"name\":\"button1\",\"parent\":\"d-button1\",\"value\":\"true\"},\n");		
			builder.append("\t\t{\"name\":\"button2\",\"parent\":\"d-button2\",\"value\":\"true\"},\n");	
			builder.append("\t\t{\"name\":\"button4\",\"parent\":\"d-button3\",\"value\":\"true\"}\n \t\t] }, \n");  // ]}, instead of , - on last entry
				
			builder.append("\t{\"timestamp\":\"");
			builder.append("1463365197344");
			//builder.append(System.currentTimeMillis());
			builder.append("\",\"values\":[\n");
			builder.append("\t\t{\"name\":\"button1\",\"parent\":\"d-button1\",\"value\":\"false\"},\n");		
			builder.append("\t\t{\"name\":\"button2\",\"parent\":\"d-button2\",\"value\":\"false\"},\n");	
			builder.append("\t\t{\"name\":\"button4\",\"parent\":\"d-button3\",\"value\":\"false\"}\n \t\t] } \n"); // , - on last entry
																											// no comma on end of last state
			
			//PRINT FILE EVERY TIME?????
			
		}
}