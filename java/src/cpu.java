import java.util.ArrayList;

public class cpu {
	int[] registers;
	String readFile;
	ArrayList<int[]> getFrom;
	int location;
	int memLoc;
	
	public cpu(ArrayList<int[]> memory) {
		this.registers = new int[9];
		this.registers[0] = 0;
		this.getFrom = memory;	
		this.location = 0;
		this.memLoc = 1;
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
	
	public void cycle() {
		if (getFrom.size() > 0) {
			this.registers[0]++;
			if (this.location < getFrom.size()) {
				if (this.memLoc < getFrom.get(location).length) {
					for (int i = 1; i < this.registers.length - 1; i++)
						this.registers[1 + i] = this.registers[i];
					
					this.registers[1] = getFrom.get(this.location)[this.memLoc];
					this.memLoc++;
				}
				
				else if (memLoc > getFrom.get(location).length) {
					this.memLoc = 1;
					this.location++;
					
					if (this.location < getFrom.size()) {
						for (int i = 1; i < this.registers.length - 1; i++)
							this.registers[1 + i] = this.registers[i];
						
						this.registers[1] = getFrom.get(this.location)[this.memLoc];
						this.memLoc++;
					}
				}
			}	
		}
		
		else
			this.registers[0]++;
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
