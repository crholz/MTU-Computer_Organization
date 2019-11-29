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
	int cacheCLO;
	int[] data;
	char[] flags;
	
	public cache() {
		this.data = new int[8];
		this.flags = new char[8];
		this.cacheCLO = 0;
		this.cacheStatus = false;
		
		for (int i = 0; i < this.data.length; i++) {
			this.data[i] = 0;
		}
		
		for (int i = 0; i < this.flags.length; i++) {
			this.flags[i] = 'I';
		}
	}
	
	public void reset() {
		this.cacheStatus = false;
	}
	
	public void cacheOn() {
		this.cacheStatus = true;
	}
	
	public void cacheOff() {
		this.cacheStatus = false;
	}
	
	public String dump() {
		String builder = "";
		builder = builder + "CLO        : " + toHex(this.cacheCLO) + "\n";
		
		// Cache Data Array
		builder = builder + "cache data : ";
		for (int i = 0; i < data.length; i++) {
			if (i == data.length - 1) 
				builder = builder + toHex(data[i]) + "\n";
			
			else
				builder = builder + toHex(data[i]) + " ";
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
