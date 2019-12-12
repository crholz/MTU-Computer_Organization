/*
 * Corbin Holz
 * crholz
 * Michigan Techonological University
 * Computer Organization
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class cs3421_emul {
	
	/*
	 * Creating all of the objects for the program
	 * Includes Memory, CPU, and Clock
	 * 
	 */
	memory myMemory = new memory();
	instructionMemory myIMem = new instructionMemory();
	cpu myCpu = new cpu(myMemory, myIMem, myIMem.iMem);
	clock myClock = new clock(myCpu, myMemory);
	
	
	/*
	 * readFile
	 * Method to read a file that instructions will be retrieved from
	 * @fileName String of the filename and path
	 */
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
	
	/*
	 * parse
	 * Takes in a line from the file and splits it into smaller commands.
	 * Then issues the command through a switch statement to determine what will be ran.
	 * @command String of the line that is being ran.
	 */
	private void parse(String command) {
		String[] commandLine = command.split("\n");
		commandLine = commandLine[0].split(" ");
		
		switch(commandLine[0].toLowerCase()) {
		
		// Start of the CPU Commands
		case "cpu":
			
			switch(commandLine[1].toLowerCase()) {
			
			// Reset all registers of the CPU
			case "reset":
				myCpu.reset();
				break;
				
			// Set a register value in the CPU
			case "set":
				myCpu.set(commandLine[3], parseHex(commandLine[4]));
				break;
				
			// Dump the values in the CPU
			case "dump":
				System.out.println(myCpu.dump());
				break;
			
			}
			break;
		
		// Start of the Memory Commands
		case "memory":
			
			switch(commandLine[1].toLowerCase()) {
			
			// Initiate the Memory
			case "create":
				myMemory.create(parseHex(commandLine[2]));
				break;
				
			// Reset the Memory
			case "reset":
				myMemory.reset();
				break;
				
			// Dump the Memory
			case "dump":
				String baseAdd = commandLine[2];
				
				// Find the base Address
				baseAdd = baseAdd.substring(0, baseAdd.length() - 1);
				baseAdd = baseAdd + "0";
				
				// Parse the location
				String locAdd = commandLine[2].substring(commandLine[2].length() - 1);
				
				int amount = parseHex(commandLine[3]);
				System.out.println(myMemory.dump(parseHex(baseAdd), parseHex(locAdd), amount));
				
				break;
			
			// Set a value in the Memory
			case "set":
				
				// Get the Address to Set
				String setMem = commandLine[2];
				
				// Initiate the Location to Set
				int loc = 0;
				
				// Find the base Address
				if (commandLine[2].split("x").length > 1) {
					setMem = commandLine[2].split("x")[1];
				}
				
				// Find the location
				loc = parseHex(setMem.substring(setMem.length() - 1));
				if (setMem.length() > 1)
					setMem = setMem.substring(0, setMem.length() - 1) + "0";
				else
					setMem = "0";
				
				// If reading from file
				if (commandLine[3].toLowerCase().equals("file")) {
					myMemory.setFromFile(commandLine[4] , parseHex(setMem), loc);
					break;
				}
				
				// If running through command
				int[] myParamSet = new int[parseHex(commandLine[3])];
				for (int i = 4; i < commandLine.length; i++)
					myParamSet[i-4] = parseHex(commandLine[i]);
				
				myMemory.set(parseHex(setMem), parseHex(commandLine[3]), myParamSet, loc);
				break;
				
			}
			break;
		
			// Start of the instruction memory Commands
			case "imemory":
						
				switch(commandLine[1].toLowerCase()) {
						
					// Initiate the Memory
					case "create":
						myIMem.create(parseHex(commandLine[2]));
						break;
							
					// Reset the Memory
					case "reset":
						myIMem.reset();
						break;
							
					// Dump the Memory
					case "dump":
						String baseAdd = commandLine[2];
							
						// Find the base Address
						baseAdd = baseAdd.substring(0, baseAdd.length() - 1);
						baseAdd = baseAdd + "0";
							
						// Parse the location
						String locAdd = commandLine[2].substring(commandLine[2].length() - 1);
							
						int amount = parseHex(commandLine[3]);
						System.out.println(myIMem.dump(parseHex(baseAdd), parseHex(locAdd), amount));
						break;
						
					// Set a value in the Memory
					case "set":
							
						// Get the Address to Set
						String setMem = commandLine[2];
							
						// Initiate the Location to Set
						int loc = 0;
							
						// Find the base Address
						if (commandLine[2].split("x").length > 1) {
							setMem = commandLine[2].split("x")[1];
						}
							
						// Find the location
						loc = parseHex(setMem.substring(setMem.length() - 1));
						if (setMem.length() > 1)
							setMem = setMem.substring(0, setMem.length() - 1) + "0";
						else
							setMem = "0";
							
						// If reading from file
						if (commandLine[3].toLowerCase().equals("file")) {
							myIMem.setFromFile(commandLine[4] , parseHex(setMem), loc);
							break;
							}
							
						// If running through command
						int[] myParamSet = new int[parseHex(commandLine[3])];
						for (int i = 4; i < commandLine.length; i++)
							myParamSet[i-4] = parseHex(commandLine[i]);
							
						myIMem.set(parseHex(setMem), parseHex(commandLine[3]), myParamSet, loc);
						break;
							
				}
				
				break;
			
		// Start of the clock Commands
		case "clock":
			
			switch (commandLine[1].toLowerCase()) {
			
			// Reset the clock
			case "reset":
				myClock.reset();
				break;
			
			// Send out a tick pulse
			case "tick":
				myClock.tickSet(Integer.parseInt(commandLine[2]));
				break;
				
			// Dump the clock values
			case "dump":
				System.out.println(myClock.dump());
				break;
				
			}
			break;
		}
	}
	
	/*
	 * parseHex
	 * Parses a hex value into an integer to work with the number
	 * easily in java.
	 * @hexString String input of a hex value.
	 */
	private int parseHex(String hexString) {
		if ((hexString.split("x")).length > 1)
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
