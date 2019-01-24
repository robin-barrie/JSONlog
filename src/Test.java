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
	leftChassisPOWEREntry, rightChassisPOWEREntry, TurnValueEntry, leftOutputEntry, rightOutputEntry;
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
		TurnValueEntry 				= table.getEntry("Turn Value");
		leftOutputEntry 			= table.getEntry("leftOutput");
		rightOutputEntry 			= table.getEntry("rightOutput");
		//*******************************************************************************
		
		inst.startServer("2337");  // team # or use inst.startClient("hostname") or similar
		//inst.startDSClient();  // recommended if running on DS computer; this gets the robot IP from the DS

	//int i=0;

		loggerOn = false;	// This value is set to 'true' in Robot.init & then false at Robot.disable.
		System.out.println("LoggerOn: " + loggerOn + " - waiting on NetworkTables and/or Robot");
	//loggerEntry.setBoolean(true);
		while(!loggerOn){
		loggerOn = loggerEntry.getBoolean(false);
		sleepy();
	//i=i+1;
	//if (i>10){ loggerEntry.setBoolean(true);}
		}	
		
		initSB();
		IOInfoSB();

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
		addState("Right Encoder Value",	"Right Encoder Value"	,RightEncoderValueEntry.getDouble(0.0));
		addState("Left Encoder Value",	"Right Encoder Value"	,LeftEncoderValueEntry.getDouble(0.0));
		addState("right Chassis POWER",	"left Chassis POWER"	,rightChassisPOWEREntry.getDouble(0.0));
		addState("left Chassis POWER",	"left Chassis POWER"	,leftChassisPOWEREntry.getDouble(0.0));
		addState("Turn Value",			"Turn Value"			,TurnValueEntry.getDouble(0.0));
		addState("rightOutput",			"leftOutput"			,rightOutputEntry.getDouble(0.0));
		addState("leftOutput",			"leftOutput"			,leftOutputEntry.getDouble(0.0));
		addState("yaw",					"yaw"					,yawEntry.getDouble(0.0));
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
}
