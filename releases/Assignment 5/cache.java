/*
 * The cache class
 * Holds all of the information for the cache

 */
/*
 * Corbin Holz
 * crholz
 * Michigan Techonological University
 * Computer Organization
 */

public class cache {
	boolean cacheStatus;
	boolean isSet;
	int cacheCLO;
	memory linkedMemory;
	int[] data;
	char[] flags;
	int[] writeLoc;
	
	/*
	 * Cache Constructor
	 * @linked the linked memory with the cache
	 */
	public cache(memory linked) {
		this.data = new int[8];
		this.flags = new char[8];
		this.writeLoc = new int[8];
		this.cacheCLO = 0;
		this.cacheStatus = false;
		this.isSet = false;
		this.linkedMemory = linked;
		
		for (int i = 0; i < this.data.length; i++) {
			this.data[i] = 0;
		}
		
		for (int i = 0; i < this.flags.length; i++) {
			this.flags[i] = 'I';
		}
		
		for (int i = 0; i < this.writeLoc.length; i++) {
			this.writeLoc[i] = 0;
		}
	}
	
	/*
	 * reset
	 * clear and reset the cache
	 */
	public void reset() {
		this.cacheStatus = false;
		for (int i = 0; i < this.data.length; i++) {
			this.data[i] = 0;
			this.flags[i] = 'I';
			this.isSet = false;
			this.writeLoc[i] = 0;
		}
	}
	
	/*
	 * cacheOn
	 * Turn on the Cache
	 */
	public void cacheOn() {
		this.cacheStatus = true;
	}
	
	/*
	 * cacheOff
	 * Turn off the Cache
	 */
	public void cacheOff() {
		flush();
		this.cacheStatus = false;
	}
	
	/*
	 * getStatus
	 * @returns if the cache is on or off
	 */
	public boolean getStatus() {
		return this.cacheStatus;
	}
	
	/*
	 * getSet
	 * @returns if the cache as been set
	 */
	public boolean getSet() {
		return this.isSet;
	}
	
	// Flush
	// Flushes the Cache
	public void flush() {
		// Flush the Cache to the specified locations
		for (int i = 0; i < this.data.length; i++) {
			if (this.flags[i] == 'W') {
				int[] toInsert = new int[1];
				toInsert[0] = this.data[i];
				linkedMemory.set(0, 1, toInsert, writeLoc[i]);
				this.flags[i] = 'V';
			}
		}
		
	}
	
	/*
	 * allInvalid
	 * Determines if the data in the cache is marked as not valid
	 * @returns true or false if the data is not valid
	 */
	public boolean allInvalid() {
		boolean notValid = true;
		
		for (int i = 0; i < this.flags.length; i++)
			if (this.flags[i] == 'V' || this.flags[i] == 'W')
				notValid = false;
		
		return notValid;
	}
	
	/*
	 * allValid
	 * Determines if the data in the cache is marked as valid
	 * @returns true or false if the data is valid
	 */
	public boolean allValid() {
		boolean isValid = true;
			
		for (int i = 0; i < this.flags.length; i++)
			if (this.flags[i] == 'I')
				isValid = false;
			
			return isValid;
		}
	
	/*
	 * needsWrite
	 * Determines if there is a write that needs to occur
	 * @returns true or false if the cache needs to be written
	 */
	public boolean needWrite() {
		boolean isWrites = false;
		
		for (int i = 0; i < this.flags.length; i++) {
			if (this.flags[i] == 'W')
				isWrites = true;
		}
		
		return isWrites;
	}
		
	
	/*
	 * checkCLO
	 * Checks to see if the cache is still in the correct CLO
	 * @returns true or false
	 */
	public boolean checkCLO(int amount) {
		int divis = (int) amount / 8;
		
		
		
		return this.cacheCLO == divis;
	}
	
	/*
	 * dump
	 * Dumps the contents of the cache
	 * @returns String of the dump
	 */
	public String dump() {
		String builder = "";
		builder = builder + "CLO        : 0x" + toHex(this.cacheCLO) + "\n";
		
		// Cache Data Array
		builder = builder + "cache data : ";
		for (int i = 0; i < data.length; i++) {
			if (i == data.length - 1) 
				builder = builder + "0x" + toHex(data[i]) + "\n";
			
			else
				builder = builder + "0x" + toHex(data[i]) + " ";
		}
		
		// Cache Flag Array
		builder = builder + "Flags      : ";
		for (int i = 0; i < flags.length; i++) {
			if (i == flags.length - 1)
				builder = builder + "  " + flags[i] + "\n";
				
			else
				builder = builder + "  " + flags[i] + "  ";
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
}
