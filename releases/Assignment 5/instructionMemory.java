/*
 * Corbin Holz
 * crholz
 * Michigan Techonological University
 * Computer Organization
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class instructionMemory {
	ArrayList<int[]> iMem;
	int iMemCap;
	
	// Constructor
	public instructionMemory() {
		this.iMem = new ArrayList<int[]>();
	}
	
	/*
	 * Create
	 * Instantiate the iMemory
	 * @iMemoryCap integer that is the maximum amount that can be held by iMemory
	 */
	public void create(int iMemoryCap) {
		this.iMemCap = iMemoryCap;
	}
	
	public int getAt(int add) {
		for (int i = 0; i < this.iMem.size(); i++) {
			// If the Address exists
			if (this.iMem.get(i)[0] == add - (add % 8))
				return i;
			
		}
		
		insert(add - (add % 8));
		
		return getAt(add);

	}
	
	/*
	 * set
	 * set the iMemory at specific addresses
	 * @iMemAddress the base address to set
	 * @hexAmount the amount of iMemory to set
	 * @params The list of data to set
	 * @loc the specific address to set
	 */
	public void set(int iMemAddress, int hexAmount, int[] params, int loc) {
		int found = -1;
		int amountLeft = hexAmount;
		int paramsLoc = 0;
		int newMemAdd = iMemAddress;
		
		// Confirm the address exists
		insert(iMemAddress);
		
		// Find the iMemory address
		for (int i = 0; i < this.iMem.size(); i++) {
			if (this.iMem.get(i)[0] == iMemAddress)
				found = i;
		}
		
		// Start filling the set address
		for (int i = 0; i < this.iMem.size(); i++) {
			if (i == found) {
				for (int j = loc + 1; j < this.iMem.get(i).length; j++) {
					if (amountLeft > 0) {
						this.iMem.get(i)[j] = params[paramsLoc];
						paramsLoc++;
						amountLeft--;
					}
					else
						return;
				}
			}
		}
		
		// If still filling, continue to write iMemory addresses
		while (amountLeft > 0) {
			newMemAdd += 8;
			insert(newMemAdd);
			for (int i = 0; i < this.iMem.size(); i++) {
				if (this.iMem.get(i)[0] == newMemAdd) {
					for (int j = 1; j < this.iMem.get(i).length; j++) {
						if (amountLeft > 0) {
							this.iMem.get(i)[j] = params[paramsLoc];
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
	 * insert a new address to the iMemory
	 * does not add if the address exists
	 * @iMemoryAdd the address to add
	 */
	private void insert(int iMemoryAdd) {
		if (iMemoryAdd + 8 > this.iMemCap)
			return;
		
		
		for (int i = 0; i < this.iMem.size(); i++) {
			if (this.iMem.get(i)[0] == iMemoryAdd)
				return;
		}
		
		int[] newAdd = new int[9];
		newAdd[0] = iMemoryAdd;
		
		for (int i = 0; i < this.iMem.size(); i++) {
			if (this.iMem.get(i)[0] > iMemoryAdd) {
				this.iMem.add(i, newAdd);
				return;
			}
		}
		
		
		this.iMem.add(newAdd);
	}
	
	/*
	 * setFromFile
	 * read data to set from a file
	 * @myFile String filename/path
	 * @iMemAdd the address to add to iMemory
	 * @iMemLoc the specific address
	 */
	public void setFromFile(String myFile, int iMemAdd, int iMemLoc) {
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
	    		 
	    		 set(iMemAdd, setParams.length, setParams, iMemLoc);
	    		 
	         	}
	    	 
	    	 reader.close();
	    	 } 
	     catch (FileNotFoundException e) {
	            e.printStackTrace();
	        }
	}
	
	/*
	 * fullDump
	 * Create a full dump of the iMemory
	 */
	public String fullDump() {
		String builder = "Addr       1     2     3     4     5     6     7\n";
		
		for (int i = 0; i < this.iMem.size(); i++) {
			for (int j = 0; j < this.iMem.get(i).length; j++) {
				if (j == this.iMem.get(i).length - 1)
					builder = builder + Integer.toHexString(this.iMem.get(i)[j]) + "\n";
				else
					builder = builder + Integer.toHexString(this.iMem.get(i)[j]) + " ";
			}
		}
		
		
		return builder;
	}
	
	/*
	 * dump
	 * dump the iMemory values for the set addresses
	 * @hexAddress the base iMemory address to dump
	 * @loc the specific location to dump
	 * @hexAmount the hex amount to dump
	 */
	public String dump(int hexAddress, int loc, int hexAmount) {
		// the base of the dump
		String builder = "Addr       0     1     2     3     4     5     6     7\n";
		
		insert(hexAddress);
		
		int node = -1;
		int amountLeft = hexAmount;
		int additionalAdd = hexAddress;
		
		// Create additional iMemory slots to be dumped if it extends
		for (int i = 0; i < Math.ceil((double) hexAmount / 8); i++) {
			additionalAdd += 8;
			insert(additionalAdd);
		}
			
		// Find the first address to dump
		for (int i = 0; i < this.iMem.size(); i++) {
			if (this.iMem.get(i)[0] == hexAddress) {
				node = i;
				break;
			}
		}
		
		// Walk through the iMemory dump
		for (int i = 0; i < this.iMem.size(); i++) {
			if(i == node) {
				builder = builder + "0x" + validateAdd(Integer.toHexString(this.iMem.get(i)[0]).toUpperCase()) + " ";
				for (int j = 1; j < this.iMem.get(i).length; j++) {
					if (j < loc + 1) {
						builder = builder + "   ";
					}
					
					else if (j >= loc + 1 && j != this.iMem.get(i).length - 1 && amountLeft > 0){
						builder = builder + validateSmall(Integer.toHexString(this.iMem.get(i)[j]).toUpperCase()) + " ";
						amountLeft--;
						if (amountLeft == 0)
							builder = builder + "\n";
					}
					
					else if (j >= loc + 1 && j == this.iMem.get(i).length - 1 && amountLeft > 0) {
						builder = builder + validateSmall(Integer.toHexString(this.iMem.get(i)[j]).toUpperCase()) + "\n";
						amountLeft--;
						if (amountLeft == 0)
							builder = builder + "\n";
					}
				}
			}
			
			else if (i > node && amountLeft > 0) {
				builder = builder + "0x" + validateAdd(Integer.toHexString(this.iMem.get(i)[0]).toUpperCase()) + " ";
				for (int k = 1; k < this.iMem.get(i).length; k++) {
					if (k != this.iMem.get(i).length - 1 && amountLeft > 0) {
						builder = builder + validateSmall(Integer.toHexString(this.iMem.get(i)[k]).toUpperCase()) + " ";
						amountLeft--;
						if (amountLeft == 0) {
							builder = builder + "\n";
							break;
						}	
					}
					
					else if (k == this.iMem.get(i).length - 1 && amountLeft > 0) {
						builder = builder + validateSmall(Integer.toHexString(this.iMem.get(i)[k]).toUpperCase()) + "\n";
						amountLeft--;
						if (amountLeft == 0) {
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
	 * clear the iMemory
	 */
	public void reset() {
		while (this.iMem.size() > 0) {
			this.iMem.clear();
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
		if (smallHex.length() >= 5)
			return smallHex;
		else
			smallHex = "0" + smallHex;
		
		
		return validateSmall(smallHex);
	}
	
}
