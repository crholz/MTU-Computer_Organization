import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class cs3421_emul {
	memory myMemory = new memory();
	cpu myCpu = new cpu(myMemory.mem);
	clock myClock = new clock(myCpu, myMemory);
	
	
	private void readFile(String fileName) {
		 File file = new File(fileName);
	     try {
	    	 Scanner reader = new Scanner(file);
	    	 while (reader.hasNextLine()) {
	    		 String line = reader.nextLine();
	    		 parse(line);
	         	}
	    	 } 
	     catch (FileNotFoundException e) {
	            e.printStackTrace();
	        }
		
	}
	
	private void parse(String command) {
		String[] commandLine = command.split(" ");
		
		switch(commandLine[0].toLowerCase()) {
		
		case "cpu":
			
			switch(commandLine[1].toLowerCase()) {
			case "reset":
				myCpu.reset();
				
			case "set":
				myCpu.set(commandLine[2], parseHex(commandLine[3]));
				
			case "dump":
				System.out.println(myCpu.dump());
				
			}
		
		case "memory":
			
			switch(commandLine[1].toLowerCase()) {
			case "create":
				myMemory.create(parseHex(commandLine[2]));
				
			case "reset":
				
				
			case "dump":
				System.out.println(myMemory.dump());
				
			case "set":
				
				
			}
			
		case "clock":
			
			switch (commandLine[1].toLowerCase()) {
			case "reset":
				myClock.reset();
				
			case "tick":
				myClock.tickSet(Integer.parseInt(commandLine[3]));
				
			case "dump":
				System.out.println(myClock.dump());
				
			}
		}
	}
	
	private int parseHex(String hexString) {
		hexString = hexString.split("x")[1];
		return Integer.parseInt(hexString, 16);
	}
	
	/*
	 * Description: Main method, takes in the string arguments the program is ran with
	 * @param1: The data file that is being taken in. this file will hold commands
	 */
	public static void main(String Args[]) {
		
		// Convert the data file argument to a string
		String dataFile = (String) Args[0];
		
		cs3421_emul run = new cs3421_emul();
		
		run.readFile(dataFile);
	}
}
