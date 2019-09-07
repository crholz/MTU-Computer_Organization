/*
 * The clock class
 * Holds all of the information for the clock

 */

public class clock {
	int count;
	
	// Constructor
	// Create a new clock object with a count
	public clock() {
		this.count = 0;
	}
	
	// reset
	// Resets the count of the clock to 0
	public void reset() {
		this.count = 0;
	}
	
	//@returns the tick amount only
	public int getTick() {
		return count;
	}
	
	// dump
	// dump the clock info to a String
	public String dump() {
		return "Clock: " + this.getTick();
	}
	
	// Tick function
	// 16 bit unsigned integer
	// Can never exceed 65535, and if it does, reset back to zero
	// @param tickAmount The amount of ticks to increase the clock count by
	public void tick(int tickAmount) {
		this.count += tickAmount;
		
		if (this.count > 65535) {
			this.count = this.count - 65536;
		}
	}
}
