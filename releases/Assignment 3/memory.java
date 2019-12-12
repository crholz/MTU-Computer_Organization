/*
 * Corbin Holz
 * crholz
 * Michigan Techonological University
 * Computer Organization
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class memory {
	ArrayList<int[]> mem;
	int memCap;
	
	// Constructor
	public memory() {
		this.mem = new ArrayList<int[]>();
	}
	
	/*
	 * Create
	 * Instantiate the memory
	 * @memoryCap integer that is the maximum amount that can be held by memory
	 */
	public void create(int memoryCap) {
		this.memCap = memoryCap;
	}
	
	/*
	 * set
	 * set the memory at specific addresses
	 * @memAddress the base address to set
	 * @hexAmount the amount of memory to set
	 * @params The list of data to set
	 * @loc the specific address to set
	 */
	public void set(int memAddress, int hexAmount, int[] params, int loc) {
		int found = -1;
		int amountLeft = hexAmount;
		int paramsLoc = 0;
		int newMemAdd = memAddress;
		
		// Confirm the address exists
		insert(memAddress);
		
		// Find the memory address
		for (int i = 0; i < this.mem.size(); i++) {
			if (this.mem.get(i)[0] == memAddress)
				found = i;
		}
		
		// Start filling the set address
		for (int i = 0; i < this.mem.size(); i++) {
			if (i == found) {
				for (int j = loc + 1; j < this.mem.get(i).length; j++) {
					if (amountLeft > 0) {
						this.mem.get(i)[j] = params[paramsLoc];
						paramsLoc++;
						amountLeft--;
					}
					else
						return;
				}
			}
		}
		
		// If still filling, continue to write memory addresses
		while (amountLeft > 0) {
			newMemAdd += 16;
			insert(newMemAdd);
			for (int i = 0; i < this.mem.size(); i++) {
				if (this.mem.get(i)[0] == newMemAdd) {
					for (int j = 1; j < this.mem.get(i).length; j++) {
						if (amountLeft > 0) {
							this.mem.get(i)[j] = params[paramsLoc];
							paramsLoc++;
							amountLeft--;
						}
						else
							break;
					}
				}
			}
		}
		
		
	}
	
	/*
	 * insert
	 * insert a new address to the memory
	 * does not add if the address exists
	 * @memoryAdd the address to add
	 */
	private void insert(int memoryAdd) {
		if (memoryAdd + 16 > this.memCap)
			return;
		
		
		for (int i = 0; i < this.mem.size(); i++) {
			if (this.mem.get(i)[0] == memoryAdd)
				return;
		}
		
		int[] newAdd = new int[17];
		newAdd[0] = memoryAdd;
		
		for (int i = 0; i < this.mem.size(); i++) {
			if (this.mem.get(i)[0] > memoryAdd) {
				this.mem.add(i, newAdd);
				return;
			}
		}
		
		
		this.mem.add(newAdd);
	}
	
	/*
	 * setFromFile
	 * read data to set from a file
	 * @myFile String filename/path
	 * @memAdd the address to add to memory
	 * @memLoc the specific address
	 */
	public void setFromFile(String myFile, int memAdd, int memLoc) {
		 File file = new File(myFile);
	     try {
	    	 Scanner reader = new Scanner(file);
	    	 ArrayList<String> additions = new ArrayList<String>();
	    	 while (reader.hasNextLine()) {
	    		 String line = reader.nextLine();
	    		 String[] readContents = line.split(" ");
	    		 
	    		 // Create a list of params
	    		 for (int i = 0; i < readContents.length; i++) {
	    			 additions.add(readContents[i]);
	    		 }
	    		 
	    		 // convert the list into an array
	    		 int[] setParams = new int[additions.size()];
	    		 for (int i = 0; i < additions.size(); i++) {
	    			 if (additions.get(i).split("x").length > 1)
	    				 additions.set(i ,additions.get(i).split("x")[1]);
	    			 setParams[i] = Integer.parseInt(additions.get(i), 16);
	    		 }
	    		 
	    		 set(memAdd, setParams.length, setParams, memLoc);
	    		 
	         	}
	    	 } 
	     catch (FileNotFoundException e) {
	            e.printStackTrace();
	        }
	}
	
	/*
	 * fullDump
	 * Create a full dump of the memory
	 */
	public String fullDump() {
		String builder = "Addr 00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F\n";
		
		for (int i = 0; i < this.mem.size(); i++) {
			for (int j = 0; j < this.mem.get(i).length; j++) {
				if (j == this.mem.get(i).length - 1)
					builder = builder + Integer.toHexString(this.mem.get(i)[j]) + "\n";
				else
					builder = builder + Integer.toHexString(this.mem.get(i)[j]) + " ";
			}
		}
		
		
		return builder;
	}
	
	/*
	 * dump
	 * dump the memory values for the set addresses
	 * @hexAddress the base memory address to dump
	 * @loc the specific location to dump
	 * @hexAmount the hex amount to dump
	 */
	public String dump(int hexAddress, int loc, int hexAmount) {
		// the base of the dump
		String builder = "Addr   00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F\n";
		
		// Instantiate if doesn't exist
		insert(hexAddress);
		
		int node = -1;
		int amountLeft = hexAmount;
		int additionalAdd = hexAddress;
		boolean isWriting = false;
		
		// Create additional memory slots to be dumped if it extends
		for (int i = 0; i < Math.ceil((double) hexAmount / 16); i++) {
			additionalAdd += 16;
			insert(additionalAdd);
		}
			
		// Find the first address to dump
		for (int i = 0; i < this.mem.size(); i++) {
			if (this.mem.get(i)[0] == hexAddress) {
				node = i;
				break;
			}
		}
		
		// Walk through the memory dump
		for (int i = 0; i < this.mem.size(); i++) {
			if(i == node) {
				isWriting = true;
				builder = builder + "0x" + validateAdd(Integer.toHexString(this.mem.get(i)[0]).toUpperCase()) + " ";
				for (int j = 1; j < this.mem.get(i).length; j++) {
					if (j < loc + 1) {
						builder = builder + "   ";
					}
					
					else if (j >= loc + 1 && j != this.mem.get(i).length - 1 && amountLeft > 0){
						builder = builder + validateSmall(Integer.toHexString(this.mem.get(i)[j]).toUpperCase()) + " ";
						amountLeft--;
						if (amountLeft == 0)
							builder = builder + "\n";
					}
					
					else if (j >= loc + 1 && j == this.mem.get(i).length - 1 && amountLeft > 0) {
						builder = builder + validateSmall(Integer.toHexString(this.mem.get(i)[j]).toUpperCase()) + "\n";
						amountLeft--;
						if (amountLeft == 0)
							builder = builder + "\n";
					}
				}
			}
			
			else if (i > node && amountLeft > 0) {
				builder = builder + "0x" + validateAdd(Integer.toHexString(this.mem.get(i)[0]).toUpperCase()) + " ";
				for (int k = 1; k < this.mem.get(i).length; k++) {
					if (k != this.mem.get(i).length - 1 && amountLeft > 0) {
						builder = builder + validateSmall(Integer.toHexString(this.mem.get(i)[k]).toUpperCase()) + " ";
						amountLeft--;
						if (amountLeft == 0) {
							builder = builder + "\n";
							isWriting = false;
							break;
						}	
					}
					
					else if (k == this.mem.get(i).length - 1 && amountLeft > 0) {
						builder = builder + validateSmall(Integer.toHexString(this.mem.get(i)[k]).toUpperCase()) + "\n";
						amountLeft--;
						if (amountLeft == 0) {
							isWriting = false;
							break;
						}
					}
				}
			}
			
			
			
		}
		
		if (builder.substring(builder.length() - 2, builder.length()).equals("\n\n"))
			builder = builder.substring(0, builder.length() - 1);
		
		return builder;
	}
	
	/*
	 * reset
	 * clear the memory
	 */
	public void reset() {
		while (this.mem.size() > 0) {
			this.mem.clear();
		}
	}
	
	/*
	 * validateAdd
	 * makes sure the hex is formatted with four places
	 * @hexStr a string of hex
	 */
	private String validateAdd(String hexStr) {
		if (hexStr.length() >= 4)
			return hexStr;
		else
			hexStr = "0" + hexStr;
		
		return validateAdd(hexStr);
	}
	
	/*
	 * validateSmall
	 * Makes sure each hex value has two digits
	 * @smallHex a string of hex
	 */
	private String validateSmall(String smallHex) {
		if (smallHex.length() >= 2)
			return smallHex;
		else
			smallHex = "0" + smallHex;
		
		
		return validateSmall(smallHex);
	}
	
	/*
	 * instZero
	 * make sure that the first array is instantiated
	 */
	public void instZero() {
		insert(0);
	}
	
}
