
public class cpu {
	int[] registers;
	String readFile;
	
	public cpu(String input) {
		this.registers = new int[9];
		this.readFile = input;
	}
	
	// reset
	// resets all of the registers to 0
	// Does not reset the program counter (PC)
	public void reset() {
		
		// Iterate through all of the registers and set them to 0
		for (int i = 1; i < 10; i++) {
			registers[i] = 0;
		}
	}
	
	/*
	 * Set
	 * set the specified register to a hexbyte
	 * @register the register to set the value of
	 * @hexByte the value to set the value to
	 */
	public void set(String register, int hexByte) {
		this.registers[((int) register.charAt(1)) - 64] = hexByte;
	}
	
	// Dumps the CPU Information
	// returns a string
	public String dump() {
		// Create a string used to append all of the data together
		String builder;
		
		// Bump the PC
		builder = "PC: 0x" + toHex(registers[0]) + "\n";
		
		// Dump each register
		for (int i = 1; i < 10; i++) {
			builder = builder + "R" + (char) (65 + (i - 1)) + ": 0x" + toHex(registers[i]);
			if (i < 9) {
				builder = builder + "\n";
			}
		}
		
		return builder;
	}
	
	private String toHex(int convertNum) {
		return Integer.toHexString(convertNum);
	}
}
