/*
 * The I/O Device class
 * Holds all of the information for the I/O Device

 */
/*
 * Corbin Holz
 * crholz
 * Michigan Techonological University
 * Computer Organization
 */

import java.util.ArrayList;

public class iodev {
	int register;
	boolean hasTask;
	int currentTask;
	int cycles;
	memory memEdit;
	ArrayList<task> schedule;
	
	public iodev(memory linkedMem) {
		this.memEdit = linkedMem;
		this.register = 0;
		this.currentTask = 0;
		this.schedule = new ArrayList<task>();
		this.hasTask = false;
		this.cycles = 0;
	}
	
	public void reset() {
		this.schedule.clear();
		this.register = 0;
		this.currentTask = 0;
		this.hasTask = false;
		this.cycles = 0;
	}
	
	public String dump() {
		return "IO Device: 0x" + validateSmall(Integer.toHexString(this.register)).toUpperCase();
	}
	
	/*
	 * validateSmall
	 * Makes sure each hex value has two digits
	 * @smallHex a string of hex
	 */
	private String validateSmall(String smallHex) {
		if (smallHex.length() >= 2)
			return smallHex;
		else
			smallHex = "0" + smallHex;
		
		
		return validateSmall(smallHex);
	}

}
