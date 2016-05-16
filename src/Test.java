
 
import java.io.FileWriter;
import java.io.IOException;
 
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
 
 
public class Test {
	
	public static JSONObject stated;
	public static JSONObject logFile;
	public static JSONArray state;
	
	public static void main(String[] args)  {
		
		stated = new JSONObject();
		logFile = new JSONObject();
		state = new JSONArray();
	
		
 
		ioinfoJSON();
		for(int i=1; i<3; i++){
		 		stateJSON();
		}
		printJSON();
		
	}
	
	
	//upfate to just print
		public static void stateJSON() {
		//JSONArray state = new JSONArray();
		JSONObject states = new JSONObject();
		
		JSONObject button3 = new JSONObject();
		 //button3 = "{\"name\":\"Driver button 3\",\"parent\":\"Driver button 3\",\"value\":\"false\"},";
		//String buttonString = "\"test";
		//button3 = JSON.parse(buttonString);
		
		
		JSONObject button1 = new JSONObject();
		button1.put("name", "button1");
		button1.put("parent", "button");
		button1.put("value", false);		
		
		JSONObject button2 = new JSONObject();
		button2.put("name", "button2");
		button2.put("parent", "button");
		button2.put("value", true);
		
		//jsonStr = '{"theTeam":[{"teamId":"1","status":"pending"},{"teamId":"2","status":"member"},{"teamId":"3","status":"member"}]}';//
		//var obj = JSON.parse(jsonStr);
		//obj['theTeam'].push({"teamId":"4","status":"pending"});
		//jsonStr = JSON.stringify(obj);
		
		
		
 
		JSONArray data = new JSONArray();
		data.add(button1);
		data.add(button2);
		data.add(button3);
		//data.add("{'name':'Driver button 5'}");
		//data.add("{'name':\'Driver button 4','parent':'Driver button 3','value':'false'}");
		//data.add(etc.....);

		
		states.put("timestamp", System.currentTimeMillis());
		states.put("values", data);
		
		state.add(states);
		
		//JSONObject stated = new JSONObject();
		//stated.put("state", state);
		//ioinfo.put("state", state);
		
 
		// try-with-resources statement based on post comment below :)
		try (FileWriter file = new FileWriter("/Users/EngiNERD1/Documents/file1.txt")) {
			file.write(state.toJSONString());
			System.out.println("Successfully Copied JSON state Object to File...");
			System.out.println("\nJSON Object: " + state);
		}catch (IOException e) {
		    System.err.println("Caught IOException (state): " + e.getMessage());
		}
	
	}
	
	public static void ioinfoJSON() {
		
		JSONObject port = new JSONObject();
		
		JSONObject button1 = new JSONObject();		//BUTTON1
		button1.put("name", "button1");
		button1.put("type", "button");
		button1.put("direction", "Input");
			port.remove("port");
			port.put("channel", 1);
		button1.put("port", port);
		
		
		JSONObject button2 = new JSONObject();		//BUTTON2,  etc....
		button2.put("name", "button2");
		button2.put("type", "button");
		button2.put("direction", "Input");
			port.remove("port");
			port.put("channel", 1);
		button2.put("port", port);	
 
		JSONArray info = new JSONArray();
		info.add(button1);
		info.add(button2);
		logFile.put("ioinfo", info);
 
		// try-with-resources statement based on post comment below :)
		try (FileWriter file = new FileWriter("/Users/EngiNERD1/Documents/file1.txt")) {
			file.write(logFile.toJSONString());
			System.out.println("Successfully Copied JSON ioinfo Object to File...");
			System.out.println("\nJSON Object: " + logFile);
		}catch (IOException e) {
		    System.err.println("Caught IOException (ioinfo): " + e.getMessage());
		}
	
	}
	
	public static void printJSON() {
		
		//stated = stated;
		
		//JSONObject fileOutput = new JSONObject();
		//fileOutput.put("state", stated);
		logFile.put("state", state);
		
 
		// try-with-resources statement based on post comment below :)
		try (FileWriter file = new FileWriter("/Users/EngiNERD1/Documents/file1.txt")) {
			file.write(logFile.toJSONString());
			System.out.println("Successfully Copied JSON LogFile Object to File...");
			System.out.println("\nJSON Object: " + logFile);
		}catch (IOException e) {
		    System.err.println("Caught IOException (state): " + e.getMessage());
		}
	}
}