import java.util.ArrayList;

public class memory {
	int[][] mem;
	int memCap;
	
	public memory() {
		this.mem = new int[17][1];
	}
	
	public void create(int memoryCap) {
		this.memCap = memoryCap;
	}
	
	
}
