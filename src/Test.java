
 
import java.io.FileWriter;
import java.io.IOException;
 
 
public class Test {
	
	public static StringBuilder builder = new StringBuilder();
	
	
	public static void main(String[] args)  {
		
	
		testIOInfoSB(builder);
		testStateSB(builder);
		testFinalStateSB(builder);
			
	}
	
	
	public static void testIOInfoSB(StringBuilder stuff) {
		
		builder = stuff;
		
		
		builder.append("{\"ioinfo\":[\n");
		builder.append("\t{\"name\":\"button1\",\"type\":\"button\",\"direction\":\"input\",\"port\":\"01\"},\n");		
		builder.append("\t{\"name\":\"button2\",\"type\":\"button\",\"direction\":\"input\",\"port\":\"02\"},\n");	
		builder.append("\t{\"name\":\"button3\",\"type\":\"button\",\"direction\":\"input\",\"port\":\"03\"}\n\t],\n"); // ], - on last entry!!!

	}
	
	
	public static void testStateSB(StringBuilder stuff) {
		
		builder = stuff;
		

		builder.append("\"state\":[\n");
		
		builder.append("\t{\"timestamp\":\"");
		builder.append("1463365197324");
		//builder.append(System.currentTimeMillis());
		builder.append("\",\"values\":[\n");
		builder.append("\t\t{\"name\":\"button1\",\"parent\":\"d-button1\",\"value\":\"");
		builder.append(false);
		builder.append("\"},\n");
		builder.append("\t\t{\"name\":\"button2\",\"parent\":\"d-button2\",\"value\":\"false\"},\n");	
		builder.append("\t\t{\"name\":\"button3\",\"parent\":\"d-button3\",\"value\":\"true\"}\n \t\t] }, \n"); // , - on last entry
		
		builder.append("\t{\"timestamp\":\"");
		builder.append("1463365197334");
		//builder.append(System.currentTimeMillis());
		builder.append("\",\"values\":[\n");
		builder.append("\t\t{\"name\":\"button1\",\"parent\":\"d-button1\",\"value\":\"true\"},\n");		
		builder.append("\t\t{\"name\":\"button2\",\"parent\":\"d-button2\",\"value\":\"true\"},\n");	
		builder.append("\t\t{\"name\":\"button3\",\"parent\":\"d-button3\",\"value\":\"true\"}\n \t\t] }, \n"); // , - on last entry
			
		builder.append("\t{\"timestamp\":\"");
		builder.append("1463365197344");
		//builder.append(System.currentTimeMillis());
		builder.append("\",\"values\":[\n");
		builder.append("\t\t{\"name\":\"button1\",\"parent\":\"d-button1\",\"value\":\"false\"},\n");		
		builder.append("\t\t{\"name\":\"button2\",\"parent\":\"d-button2\",\"value\":\"false\"},\n");	
		builder.append("\t\t{\"name\":\"button3\",\"parent\":\"d-button3\",\"value\":\"false\"}\n \t\t] } \n"); // , - on last entry
		
		
		//PRINT FILE EVERY TIME?????
		
	}
	
	
	public static void testFinalStateSB(StringBuilder stuff) {
		
		builder = stuff;
		
		builder.append("\t] \n}");			// close out JSON format
 
		// try
		try (FileWriter file = new FileWriter("/Users/Robin/Documents/file2.txt")) {
			file.write(builder.toString());
			System.out.println("Successfully Copied state String to File...");
			System.out.println("builder string: \n" + builder);
		}catch (IOException e) {
		    System.err.println("Caught IOException (ioinfo): " + e.getMessage());
		}	
		
	
		
	}
	
	

}