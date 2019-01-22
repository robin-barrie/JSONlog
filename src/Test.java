
 
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
 
 
public class Test {
	
	public static File f;
	public static StringBuilder builder = new StringBuilder();
	public static FileWriter fw_file;
	public static BufferedWriter bw_file;

	
	
	
	public static void main(String[] args)  {
		
		initSB();
		IOInfoSB();
		TEST2stateSB();
		finalStateSB();
		closeSB();
			
	}

	/*
	 *	Creates time stamped file and initiates buffer writer to that file. (ok)
	 * 
	 */
	public static void initSB() {
					
    	try {
    		f = new File("/Users/Public/Documents/file" + System.currentTimeMillis() + ".txt");
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
		// 'direction'  (4th item)= Input/Output only.  
		// The 2nd and 4th item are just for information( i.e type and then port).
		// Add a line for every item to be logged.  
		// Last entry has a unique ending
		//
		//  Maybe in future, read in details from an array, gaurantees names match and only one place to edit
		
		builder.append("{\"ioinfo\":[");
		
		addIOInfo("button1", "button", "Input", "1");
		addIOInfo("button2", "button", "Input", "2");
		addIOInfo("button4", "button", "Input", "4");
		
		builder.setLength(Math.max(builder.length() - 1,0));  	//remove comma from last entry.
		builder.append("\n\t]");								//close out ioinfo entry.
		builder.append("\n\"state\":[");						//start state entries.
	}
	
	
	public static void stateSB() {
		
		//'name' to match name in 'ioinfo' section.  'parent' is for information only.
		// Add 3(?) lines for every item to be logged, matching entries in 'ioinfo'.  
		// Use commands/methods to get 'value' for each entry.
		// Last entry has a unique ending
		
		builder.append("\n\t{\"timestamp\":\"");
		builder.append(System.currentTimeMillis());
		builder.append("\",\"values\":[");
		builder.append("\t\t{\"name\":\"button1\",\"parent\":\"d-button1\",\"value\":\"");  //name needs to match IOInfo name.
		builder.append(false);
		//builder.append(subsystem.getvalue());  //get actual 'value'(s) instead of using false....
		builder.append("\"},\n");
		builder.append("\t\t{\"name\":\"button2\",\"parent\":\"d-button2\",\"value\":\"false\"},\n");	
		builder.append("\t\t{\"name\":\"button3\",\"parent\":\"d-button3\",\"value\":\"true\"}\n \t\t] }, \n"); // , - on last entry
		
		
		
		
		//PRINT FILE EVERY TIME?????
		
	}


	
	public static void closeSB() {
		try  {
			bw_file.write(builder.toString());
			bw_file.flush();
			fw_file.flush();
			System.out.println("Successfully Flushed File..."+f);
		}catch (IOException e) {
		    System.err.println("Caught IOException (closeSB): " + e.getMessage());
		}
		 try{
			if(bw_file!=null)
			 bw_file.close();
			 fw_file.close();
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
			bw_file.write(builder.toString());
			 bw_file.close();
			System.out.println("Successfully Closed bw state String to File..(finalStateSB).");
			System.out.println("builder string: \n" + builder);
		   	}catch(Exception ex){
		       System.out.println("Error in closing the BufferedWriter"+ex);
		    }
		
		// try
		/*
		try (FileWriter file = new FileWriter("/Users/Robin/Documents/file3.txt")) {
			file.write(builder.toString());
			System.out.println("Successfully Copied state String to File...");
			System.out.println("builder string: \n" + builder);
		}catch (IOException e) {
		    System.err.println("Caught IOException (ioinfo): " + e.getMessage());
		}
		*/
		 /*
		try  {
			bw_file.write(builder.toString());
			System.out.println("Successfully Copied final String to File...");
			System.out.println("builder string: \n" + builder);
		}catch (IOException e) {
		    System.err.println("Caught IOException (final): " + e.getMessage());
		}
		*/
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
	
	public static void addStateBoolean(String name, String parent, boolean value) {
		
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
		
		/*
		if (m_lastEntry) {
			builder.append("\n \t\t] }"); 	// ]}, - on last entry!!!
			if (!m_lastState) {
				builder.append(",");			// no comma on last entry in last state
				} 
			} else {
				builder.append(",");
			}
		 */
		
	}
	
	public static void TEST2stateSB() {  
		
		//send boolean thru this method to identify last/final state and then pass to last entry.
		
		
		//builder.append("\n\t{\"timestamp\":\"");
		//builder.append("1463365197324");
		//builder.append("\",\"values\":[");
		builder.append("\n\t{\"timestamp\":\"" + System.currentTimeMillis() + "\",\"values\":[");
		addStateBoolean("button1","driver-button1",false);   //use getMethod instead of 'true/false' for value....
		addStateBoolean("button2","driver-button2",true);
		addStateBoolean("button4","driver-button4",false);
		
		builder.setLength(Math.max(builder.length() - 1,0));  	//remove comma from last entry.
		builder.append("\n\t\t] },");							//close out state entry.
		
		try{ Thread.sleep(20);
			} catch (InterruptedException e) {
			e.printStackTrace();}
		
		//builder.append("\n\t{\"timestamp\":\"");
		//builder.append("1463365197334");
		//builder.append("\",\"values\":[");
		builder.append("\n\t{\"timestamp\":\"" + System.currentTimeMillis() + "\",\"values\":[");
		addStateBoolean("button1","driver-button1",false);   //use getMethod instead of 'true/false' for value....
		addStateBoolean("button2","driver-button2",false);
		addStateBoolean("button4","driver-button4",false);
		
		builder.setLength(Math.max(builder.length() - 1,0));  	//remove comma from last entry.
		builder.append("\n\t\t] },");							//close out state entry.
			
		try{ Thread.sleep(20);
		} catch (InterruptedException e) {
		e.printStackTrace();}

		//builder.append("\n\t{\"timestamp\":\"");
		//builder.append("1463365197344");
		//builder.append("\",\"values\":[");
		builder.append("\n\t{\"timestamp\":\"" + System.currentTimeMillis() + "\",\"values\":[");
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
			//builder.append("1463365197324");
			builder.append(System.currentTimeMillis());
			builder.append("\",\"values\":[\n");
			builder.append("\t\t{\"name\":\"button1\",\"parent\":\"d-button1\",\"value\":\"");
			builder.append(false);
			builder.append("\"},\n");
			builder.append("\t\t{\"name\":\"button2\",\"parent\":\"d-button2\",\"value\":\"false\"},\n");	
			builder.append("\t\t{\"name\":\"button4\",\"parent\":\"d-button3\",\"value\":\"true\"}\n \t\t] }, \n"); // ]}, instead of , - on last entry
			
			builder.append("\t{\"timestamp\":\"");
			//builder.append("1463365197334");
			builder.append(System.currentTimeMillis());
			builder.append("\",\"values\":[\n");
			builder.append("\t\t{\"name\":\"button1\",\"parent\":\"d-button1\",\"value\":\"true\"},\n");		
			builder.append("\t\t{\"name\":\"button2\",\"parent\":\"d-button2\",\"value\":\"true\"},\n");	
			builder.append("\t\t{\"name\":\"button4\",\"parent\":\"d-button3\",\"value\":\"true\"}\n \t\t] }, \n");  // ]}, instead of , - on last entry
				
			builder.append("\t{\"timestamp\":\"");
			//builder.append("1463365197344");
			builder.append(System.currentTimeMillis());
			builder.append("\",\"values\":[\n");
			builder.append("\t\t{\"name\":\"button1\",\"parent\":\"d-button1\",\"value\":\"false\"},\n");		
			builder.append("\t\t{\"name\":\"button2\",\"parent\":\"d-button2\",\"value\":\"false\"},\n");	
			builder.append("\t\t{\"name\":\"button4\",\"parent\":\"d-button3\",\"value\":\"false\"}\n \t\t] } \n"); // , - on last entry
																											// no comma on end of last state
			
			//PRINT FILE EVERY TIME?????
			
		}
	
}