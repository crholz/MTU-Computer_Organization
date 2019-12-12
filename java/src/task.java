
public class task {
	int clockTick;
	boolean task;		// False for read, True for write
	int address;		// Address Stored
	int value;			// Value if write task
	
	public task(int tick, boolean tk, int add, int val) {
		this.clockTick = tick;
		this.task = tk;
		this.address = add;
		this.value = val;
	}

}
