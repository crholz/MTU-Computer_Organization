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
	
	public void setFromFile(String myFile) {
		
	}
	
	public String dump() {
		String builder = "Addr\t00\t01\t02\t\03\t04\t05\t06\t07\t08\t09\t0A\t0B\t0C\t0D\t0E\t0F\n";
		
		for (int i = 0; i < this.mem.size(); i++) {
			for (int j = 0; j < this.mem.get(j).length; j++) {
				if (j == this.mem.get(j).length - 1)
					builder = builder + Integer.toHexString(this.mem.get(i)[j]) + "\n";
				else
					builder = builder + Integer.toHexString(this.mem.get(i)[j]) + "\t";
			}
		}
		
		
		return builder;
	}
	
	public void reset() {
		while (this.mem.size() > 0) {
			this.mem.clear();
		}
	}
	
}
