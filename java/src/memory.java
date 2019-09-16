public class memory {
	int[][] mem;
	int memCap;
	
	public memory() {
		this.mem = new int[17][1];
	}
	
	public void create(int memoryCap) {
		this.memCap = memoryCap;
	}
	
	public void set(int memAddress) {
		
		boolean exists = false;
		
		for(int i = 0; i < mem[1].length; i++) {
			if (memAddress == mem[1][i])
				exists = true;
		}
		
		if (!exists) {
			add();
		}
		
		else {
			return 0;
		}
	}
	
	public void reset() {
		
	}
	
	public void add() {
		
	}
	
	
}
