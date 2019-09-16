import java.util.*;

public class memory {
	ArrayList<Object> mem;
	int memCap;
	
	public memory() {
		this.mem = new ArrayList<Object>();
	}
	
	public void create(int memoryCap) {
		this.memCap = memoryCap;
	}
	
	public void set(int memAddress, int hexAmount, int[] params) {
		for (int i = 0; i < this.mem.size(); i++) {
			if ((this.mem.get(i))[0] == memAddress) {
				
			}
		}
		int[] test = new int[5];
		mem.add(test);
	}
	
	public void reset() {
		
	}
	
}
