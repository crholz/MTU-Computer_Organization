import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class memory {
	ArrayList<int[]> mem;
	int memCap;
	
	public memory() {
		this.mem = new ArrayList<int[]>();
	}
	
	public void create(int memoryCap) {
		this.memCap = memoryCap;
	}
	
	public void set(int memAddress, int hexAmount, int[] params) {
		int found = -1;
		for (int i = 0; i < this.mem.size(); i++) {
			if ((this.mem.get(i))[0] == memAddress) {
				for (int j = 1; j < hexAmount; j++) {
					this.mem.get(i)[j] = params[j - 1];
				}
				
				
				found = i;
				break;
			}
		}
		
		if (found < 0) {
			int[] newArr = new int[17];
			newArr[0] = memAddress;
			for (int i = 0; i < hexAmount; i++) {
				newArr[i + 1] = params[i];
			}
			
			boolean added = false;
			
			for (int i = 0; i < mem.size(); i++) {
				if (this.mem.get(i)[0] > memAddress) {
					this.mem.add(i, newArr);
					added = true;
					break;
				}
			}
			
			if (!added)
				this.mem.add(newArr);
		}
	}
	
	public void setFromFile(String myFile, int memAdd) {
		 File file = new File(myFile);
	     try {
	    	 Scanner reader = new Scanner(file);
	    	 ArrayList<String> additions = new ArrayList<String>();
	    	 while (reader.hasNextLine()) {
	    		 String line = reader.nextLine();
	    		 String[] readContents = line.split(" ");
	    		 for (int i = 0; i < readContents.length; i++) {
	    			 additions.add(readContents[i]);
	    		 }
	    		 
	    		 int[] setParams = new int[additions.size()];
	    		 for (int i = 0; i < additions.size(); i++) {
	    			 if (additions.get(i).split("x").length > 1)
	    				 additions.set(i ,additions.get(i).split("x")[1]);
	    			 setParams[i] = Integer.parseInt(additions.get(i), 16);
	    		 }
	    		 
	    		 set(memAdd, setParams.length, setParams);
	    		 
	         	}
	    	 } 
	     catch (FileNotFoundException e) {
	            e.printStackTrace();
	        }
	}
	
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
	
	public String dump(int hexAddress, int loc, int hexAmount) {
		String builder = "Addr   00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F\n";
		
		int node = -1;
		int amountLeft = hexAmount;
		boolean isWriting = false;
		
		for (int i = 0; i < this.mem.size(); i++) {
			if (this.mem.get(i)[0] == hexAddress) {
				node = i;
				break;
			}
		}
		
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
		
		
		return builder;
	}
	
	public void reset() {
		while (this.mem.size() > 0) {
			this.mem.clear();
		}
	}
	
	private String validateAdd(String hexStr) {
		if (hexStr.length() >= 4)
			return hexStr;
		else
			hexStr = "0" + hexStr;
		
		return validateAdd(hexStr);
	}
	
	private String validateSmall(String smallHex) {
		if (smallHex.length() >= 2)
			return smallHex;
		else
			smallHex = "0" + smallHex;
		
		
		return validateSmall(smallHex);
	}
	
}
