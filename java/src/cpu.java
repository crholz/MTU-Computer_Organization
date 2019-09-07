
public class cpu {
	int[] registers;
	
	public cpu() {
		this.registers = new int[9];
	}
	
	// reset
	// resets all of the registers to 0
	public void reset() {
		
		// Iterate through all of the registers and set them to 0
		for (int i = 0; i < 10; i++) {
			registers[i] = 0;
		}
	}
}
