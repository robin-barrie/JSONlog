
 
import java.io.FileWriter;
import java.io.IOException;
 
 
public class Test {
	
	public static StringBuilder builder = new StringBuilder();
	
	
	public static void main(String[] args)  {
		
	
		testIOInfoSB();
		TEST2stateSB();
		finalStateSB();
			
	}
	
	public static void testIOInfoSB() {
		
		//'name' to match name in 'state' section.  'direction' = Input/Output.  All others for information only.
		// Add a line for every item to be logged.  Last entry has a unique ending
		
		builder.append("{\"ioinfo\":[\n");
		
		addIOInfo("button1", "button", "Input", "1", false);
		addIOInfo("button2", "button", "Input", "2", false);
		addIOInfo("button4", "button", "Input", "4", true);  // last entry should be 'true', all others should be 'false'
		
	}
	
	
	public static void stateSB() {
		
		//'name' to match name in 'ioinfo' section.  'parent' is for information only.
		// Add 3(?) lines for every item to be logged, matching entries in 'ioinfo'.  
		// Use commands/methods to get 'value' for each entry.
		// Last entry has a unique ending

		builder.append("\"state\":[\n");
		
		builder.append("\t{\"timestamp\":\"");
		builder.append(System.currentTimeMillis());
		builder.append("\",\"values\":[\n");
		builder.append("\t\t{\"name\":\"button1\",\"parent\":\"d-button1\",\"value\":\"");  //name needs to match IOInfo name.
		builder.append(false);
		//builder.append(subsystem.getvalue());  //get actual 'value'(s) instead of using false....
		builder.append("\"},\n");
		builder.append("\t\t{\"name\":\"button2\",\"parent\":\"d-button2\",\"value\":\"false\"},\n");	
		builder.append("\t\t{\"name\":\"button3\",\"parent\":\"d-button3\",\"value\":\"true\"}\n \t\t] }, \n"); // , - on last entry
		
		
		//PRINT FILE EVERY TIME?????
		
	}
	
	
	public static void finalStateSB() {
		
		builder.append("\t] \n}");			// close out JSON format
 
		// try
		try (FileWriter file = new FileWriter("/Users/Robin/Documents/file3.txt")) {
			file.write(builder.toString());
			System.out.println("Successfully Copied state String to File...");
			System.out.println("builder string: \n" + builder);
		}catch (IOException e) {
		    System.err.println("Caught IOException (ioinfo): " + e.getMessage());
		}	
		
	}
		
	
	public static void addIOInfo(String name, String type, String direction, String port, boolean lastEntry) {
		
		String m_name = name;
		String m_type = type;
		String m_direction = direction;
		String m_port = port;
		boolean m_lastEntry = lastEntry;
 
		builder.append("\t{\"name\":\"");
		builder.append(m_name);
		builder.append("\",\"type\":\""); 
		builder.append(m_type);
		builder.append("\",\"direction\":\"");
		builder.append(m_direction);
		builder.append("\",\"port\":\"");
		builder.append(m_port);
		builder.append("\"}");	
		if (m_lastEntry) {
			builder.append("\n\t]"); // ], - on last entry!!!
			}
		builder.append(",\n");
		
		
	}
	
	public static void addStateBoolean(String name, String parent, boolean value, boolean lastEntry, boolean lastState) {
		
		// have method for int, double, boolean & ???object??? et.al...
		
		String m_name = name;
		String m_parent = parent;
		boolean m_value = value;
		boolean m_lastEntry = lastEntry;
		boolean m_lastState = lastState;
 
		builder.append("\t\t{\"name\":\"");
		builder.append(m_name);
		builder.append("\",\"parent\":\""); 
		builder.append(m_parent);
		builder.append("\",\"value\":\"");
		builder.append(m_value);
		builder.append("\"}");	
		if (m_lastEntry) {
			builder.append("\n \t\t] }"); 	// ]}, - on last entry!!!
			if (!m_lastState) {
				builder.append(",");			// no comma on last entry in last state
				} 
			}
		builder.append("\n");
		
	}
	
	public static void TEST2stateSB() {  
		
		//send boolean thru this method to identify last/final state and then pass to last entry.
		
		builder.append("\"state\":[\n");
		
		builder.append("\t{\"timestamp\":\"");
		builder.append("1463365197324");
		//builder.append("\t{\"timestamp\":\"" + System.currentTimeMillis() + "\",\"values\":[\n");
		builder.append("\",\"values\":[\n");
		addStateBoolean("button1","driver-button1",false, false,false);   //use getMethod instead of 'true/false' for value....
		addStateBoolean("button2","driver-button2",true, false,false);
		addStateBoolean("button4","driver-button4",false, true,false);
		
		
		builder.append("\t{\"timestamp\":\"");
		builder.append("1463365197324");
		//builder.append(System.currentTimeMillis());
		builder.append("\",\"values\":[\n");
		addStateBoolean("button1","driver-button1",false, false,false);   //use getMethod instead of 'true/false' for value....
		addStateBoolean("button2","driver-button2",false, false,false);
		addStateBoolean("button4","driver-button4",false, true,false);
			
		builder.append("\t{\"timestamp\":\"");
		builder.append("1463365197324");
		//builder.append(System.currentTimeMillis());
		builder.append("\",\"values\":[\n");
		addStateBoolean("button1","driver-button1",true, false,false);   //use getMethod instead of 'true/false' for value....
		addStateBoolean("button2","driver-button2",true, false,false);
		addStateBoolean("button4","driver-button4",true, true,true);	//use passed boolean from method, to identify last state 'true' here....
		
		//PRINT FILE EVERY TIME?????
		
	}
	
		public static void TESTstateSB() {  
		
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