/*
 * Corbin Holz
 * crholz
 * Michigan Techonological University
 * Computer Organization
 */

import java.util.ArrayList;

public class cpu {
	int[] registers;
	String readFile;
	ArrayList<int[]> getFrom;
	ArrayList<int[]> instructions;
	memory memSetter;
	instructionMemory inst;
	int location;
	int memLoc;
	int instime;
	
	public cpu(memory memSrc, instructionMemory fetch, ArrayList<int[]> iMem) {
		this.registers = new int[9];
		this.registers[0] = 0;
		this.getFrom = memSrc.mem;
		this.instructions = iMem;
		this.memSetter = memSrc;
		this.location = 0;
		this.memLoc = 1;
		this.inst = fetch;
		this.instime = 0;
	}
	
	// reset
	// resets all of the registers to 0
	// Does not reset the program counter (PC)
	public void reset() {
		
		// Iterate through all of the registers and set them to 0
		for (int i =0; i < registers.length; i++) {
			registers[i] = 0;
		}
	}
	
	/*
	 * Cycle
	 * Cycle the CPU through the memory and the registers
	 */
	public void cycle() {
		if (getFrom.size() > 0) {
			this.registers[0] += 1;
			if (this.location < this.getFrom.size()) {
				if (this.memLoc < this.getFrom.get(this.location).length) {
					for (int i = this.registers.length - 1; i > 1; i--)
						this.registers[i] = this.registers[i - 1];
					
					this.registers[1] = getFrom.get(this.location)[this.memLoc];
					this.memLoc++;
				}
				
				else if (memLoc > getFrom.get(location).length) {
					this.memLoc = 1;
					this.location++;
					
					if (this.location < getFrom.size()) {
						for (int i = this.registers.length - 1; i > 1; i--)
							this.registers[i] = this.registers[i - 1];
						
						this.registers[1] = getFrom.get(this.location)[this.memLoc];
						this.memLoc++;
					}
				}
			}	
		}
		
		else
			this.registers[0] += 1;
	}
	
	/*
	 * Set
	 * set the specified register to a hexbyte
	 * @register the register to set the value of
	 * @hexByte the value to set the value to
	 */
	public void set(String register, int hexByte) {
		if (register.toUpperCase().equals("PC")) {
			this.registers[0] = hexByte;
			this.instime = 0;
		}
		else
			this.registers[((int) register.charAt(1)) - 64] = hexByte;
	}
	
	/*
	 * eCycle
	 * Operates the cpu as described by entropy assembler details
	 */
	public void eCycle() {
		
		instime++;
		
		// Action is completed
		if (instime % 5 == 0 && instime != 0) {
			
			int getFrom = this.inst.getAt(this.registers[0]);
			int indexVal = (this.registers[0] % 8) + 1;
			
			// Takes instruction from PC
			entropy((this.instructions.get(getFrom))[indexVal]);
			
			// Increment the PC
			this.registers[0] += 1;
		}
		
	}
	
	// Dumps the CPU Information
	// returns a string
	public String dump() {
		// Create a string used to append all of the data together
		String builder;
		
		// Bump the PC
		builder = "PC: 0x" + toHex(this.registers[0]) + "\n";
		
		// Dump each register
		for (int i = 1; i < this.registers.length; i++) {
			builder = builder + "R" + (char) (65 + (i - 1)) + ": 0x" + toHex(this.registers[i]).toUpperCase();
			if (i < 9) {
				builder = builder + "\n";
			}
		}
		
		return builder;
	}
	
	/*
	 * Change a Value to Hex
	 * @convertNum Integer that is converted to Hex
	 */
	private String toHex(int convertNum) {
		String builder = Integer.toHexString(convertNum);
		if (builder.length() < 2)
			builder = "0" + builder;
		
		return builder;
	}
	
	/*
	 * entropy
	 * Perform the entropy operation with binary
	 * @binaryNum the number to change to binary
	 */
	private void entropy(int binaryNum) {
		String binString = Integer.toBinaryString(binaryNum);
		if (binString.length() < 20)
			return;
		
		// NNN	DDD	SSS	TTT	IIIIIIII
		// Ins  Des Src Trg	   IV
		// First Three Characters
		switch(binString.substring(0, 3)) {
		
		// Load Word
		// Uses Destination and Target
		// Data Memory => Register
		case "101":
			String lDst = binString.substring(3, 6);
			String lTrg = binString.substring(9, 12);
			
			int loadTarget = Integer.parseInt(lTrg, 2) + 1;
			int loadDestination = Integer.parseInt(lDst, 2) + 1;
			
			memSetter.instZero();
			
			this.registers[loadDestination] = getFrom.get(0)[this.registers[loadTarget] + 1];
			
			break;
		
		// Store Word
		// Uses Source and Target
		// Register => Data Memory
		case "110":
			String sSrc = binString.substring(6, 9);
			String sTrg = binString.substring(9, 12);
			
			int[] toInsert = new int[1];
			
			toInsert[0] =this.registers[Integer.parseInt(sSrc, 2) + 1];
			memSetter.set(0, 1, toInsert, Integer.parseInt(sTrg, 2) + 1);
			
			break;
			
		}
		
	}
}
