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
	int status;
	int instrTime;
	boolean isHalt;
	
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
		this.status = 0;
		this.instrTime = 0;
		this.isHalt = false;
	}
	
	// reset
	// resets all of the registers to 0
	// Does not reset the program counter (PC)
	public void reset() {
		
		// Iterate through all of the registers and set them to 0
		for (int i =0; i < registers.length; i++) {
			registers[i] = 0;
		}
		
		this.isHalt = false;
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
		
		// If No Action, get the next instruction.
		if (this.status == 0) {
			int getFrom = this.inst.getAt(this.registers[0]);
			int indexVal = (this.registers[0] % 8) + 1;
			
			this.status = (this.instructions.get(getFrom))[indexVal];
			
			timer(this.status);
			
			instime = 0;
		}
		
		instime++;
		
		// If the correct amount of cycles
		if (instime >= instrTime) {
			int compTime = instrTime;
			
			entropy(status);
			
			if (compTime == instrTime)
				this.registers[0]++;
		}
			
		
		// If currently doing an action
		
		/* Action is completed
		if (instime % 5 == 0 && instime != 0) {
			
			int getFrom = this.inst.getAt(this.registers[0]);
			int indexVal = (this.registers[0] % 8) + 1;
			
			// Takes instruction from PC
			entropy((this.instructions.get(getFrom))[indexVal]);
			
			// Increment the PC
			this.registers[0] += 1;
		}
		*/
		
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
		
		return builder.toUpperCase();
	}
	
	/*
	 * entropy
	 * Perform the entropy operation with binary
	 * @binaryNum the number to change to binary
	 */
	private void entropy(int binaryNum) {
		String binString = Integer.toBinaryString(binaryNum);
		while (binString.length() < 20)
			binString = "0" + binString;
		
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
			
			this.status = 0;
			
			break;
		
		// Store Word
		// Uses Source and Target
		// Register => Data Memory
		case "110":
			String sSrc = binString.substring(6, 9);
			String sTrg = binString.substring(9, 12);
			
			int[] toInsert = new int[1];
			
			toInsert[0] = this.registers[Integer.parseInt(sSrc, 2) + 1];
			memSetter.set(0, 1, toInsert, this.registers[Integer.parseInt(sTrg, 2) + 1]);
			
			this.status = 0;
			
			break;
			
		
		// add
		// Uses source and target
		// Source + Target => Destination (8 bit two's complement)
		case "000":
			String addSrc = binString.substring(6, 9);
			String addTrg = binString.substring(9, 12);
			String addDst = binString.substring(3, 6);
			
			int addSource = this.registers[Integer.parseInt(addSrc, 2) + 1];
			int addTarget = Integer.parseInt(addTrg, 2) + 1;
			
			int trgVal = this.registers[addTarget];
			
			memSetter.instZero();
			
			int addDestination = Integer.parseInt(addDst, 2) + 1;
			
			
			this.registers[addDestination] = addSource + trgVal;
			
			this.status = 0;
			
			break;
		
		// addi
		// Uses Source and immediate values
		// Source + IV => Destination (8 bit two's complement)
		case "001":
			String addiSrc = binString.substring(6, 9);
			String addiIV = binString.substring(12, binString.length());
			String addiDst = binString.substring(3, 6);
			
			int addiSource = this.registers[Integer.parseInt(addiSrc, 2) + 1];
			int addiImmediate = Integer.parseInt(addiIV, 2);
			
			int addiDestination = Integer.parseInt(addiDst, 2) + 1;
			
			this.registers[addiDestination] = addiSource + addiImmediate;
			
			this.status = 0;
			
			break;
			
		// mul
		// Product of upper and lower 4 bits of Src register => Dst
		case "010":
			String mulSrc = binString.substring(6, 9);
			String mulDst = binString.substring(3, 6);
		
			int mulSource = this.registers[Integer.parseInt(mulSrc, 2) + 1];
			int mulDestination = Integer.parseInt(mulDst, 2) + 1;
			
			
			String mulBin = Integer.toBinaryString(mulSource);
			
			while (mulBin.length() < 8)
				mulBin = "0" + mulBin;
			
			int mul1Source = Integer.parseInt(mulBin.substring(0, 4), 2);
			int mul2Source = Integer.parseInt(mulBin.substring(4, mulBin.length()), 2);

			
			this.registers[mulDestination] = mul1Source * mul2Source;
			
			this.status = 0;
			
			break;
			
			
		// inv
		// inverts all the bits in the source register and stores in destination
		// ~(src) => Dst
		case "011":
			String invSrc = binString.substring(6, 9);
			String invDst = binString.substring(3, 6);
			
			int invSource = this.registers[Integer.parseInt(invSrc, 2) + 1];
			int invDestination = Integer.parseInt(invDst, 2) + 1;
			
			String invString = Integer.toBinaryString(~(invSource));
			invString = invString.substring(invString.length() - 8, invString.length());
			
			this.registers[invDestination] = Integer.parseInt(invString, 2);
			
			this.status = 0;
			
			break;
		
		// beq
		// If Src and Trg reg equal assign the PC to the imemory address. if not increment the PC
		case "100":
			String beqSrc = binString.substring(6, 9);
			String beqTrg = binString.substring(9, 12);
			
			int beqSource = this.registers[Integer.parseInt(beqSrc, 2) + 1];
			int beqTarget = Integer.parseInt(beqTrg, 2) + 1;
			int beqTrgVal = this.registers[beqTarget];
			
			if (beqSource == beqTrgVal) {
				if (this.instrTime == 1) {
					this.instrTime++;
				}
				
				
				
				int beqInsert = Integer.parseInt((binString.substring(12, binString.length())), 2);
				
				this.registers[0] = beqInsert;
				
				/*
				int[] beqToInsert = new int[1];
				
				beqToInsert[0] = this.registers[0];
				inst.set((int) Math.floor(beqInsert/16), 1, beqToInsert, (int) beqInsert % 16);
				*/
				
				this.status = 0;
				
				return;
			}
			
			else
				this.status = 0;
			
			break;
		
		// halt
		// Halts execution of the processor after incrementing PC
		// Ignores future clock ticks
		case "111":
			this.isHalt = true;
			
			this.status = 0;
			
			break;
			
		}
			
	}
	
	/*
	 * entropy
	 * Perform the entropy operation with binary
	 * @binaryNum the number to change to binary
	 */
	private void timer(int binaryNum) {
		String binString = Integer.toBinaryString(binaryNum);
		while (binString.length() < 20)
			binString = "0" + binString;
		
		// NNN	DDD	SSS	TTT	IIIIIIII
		// Ins  Des Src Trg	   IV
		// First Three Characters
		switch(binString.substring(0, 3)) {
		
		// Load Word
		// Uses Destination and Target
		// Data Memory => Register
		case "101":
			this.instrTime = 5;
			break;
		
		// Store Word
		// Uses Source and Target
		// Register => Data Memory
		case "110":
			this.instrTime = 5;
			break;
			
		
		// add
		// Uses source and target
		// Source + Target => Destination (8 bit two's complement)
		case "000":
			this.instrTime = 1;
			break;
		
		// addi
		// Uses Source and immediate values
		// Source + IV => Destination (8 bit two's complement)
		case "001":
			this.instrTime = 1;
			break;
			
		// mul
		// Product of upper and lower 4 bits of Src register => Dst
		case "010":
			this.instrTime = 2;
			break;
			
			
		// inv
		// inverts all the bits in the source register and stores in destination
		// ~(src) => Dst
		case "011":
			this.instrTime = 1;
			break;
		
		// beq
		// If Src and Trg reg equal assign the PC to the imemory address. if not increment the PC
		case "100":
			this.instrTime = 1;
			break;
		
		// halt
		// Halts execution of the processor after incrementing PC
		// Ignores future clock ticks
		case "111":
			this.instrTime = 1;
			break;
		}
			
	}
}
