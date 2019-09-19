/*
 * The clock class
 * Holds all of the information for the clock

 */

public class clock {
	int count;
	cpu linkedCPU;
	memory linkedMemory;
	
	// Constructor
	// Create a new clock object with a count
	public clock(cpu newCPU, memory newMemory) {
		this.count = 0;
		this.linkedCPU = newCPU;
		this.linkedMemory = newMemory;
	}
	
	// reset
	// Resets the count of the clock to 0
	public void reset() {
		this.count = 0;
	}
	
	//@returns the tick amount only
	public int getTick() {
		return this.count;
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
	public void tickSet(int tickAmount) {
		if (this.count > 65535)
			reset();
		
		for(int i = 0; i < tickAmount; i++) {
			tick();
		}
	}
	
	public void tick() {
		this.count += 1;
		this.linkedCPU.cycle();
	}
	
	
}
