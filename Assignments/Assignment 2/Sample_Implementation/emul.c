// This is an INCOMPLETE sample implementation of Assignment 2.
// It demonstrates a "state machine" approach to device implementation.

#include <stdio.h>   // printf
#include <stdbool.h> // booleans - true, false, bool type
#include <stdlib.h>  // malloc
#include <string.h>  // memset
#include "emul.h"

// delcare our three objects/devices
dmem_t dmem;
imem_t imem;
cpu_t  cpu;

// main routine that drives the system
// accepts the number of clock cycles to do
void clock_do_ticks(unsigned count)
{
	// keep looping while working on cycles
	while (count--)
	{
		// tell every device to start the clock cycle
		// note it SHOULD work no matter what order devices execute

		bool workToDo = true;
		dmem_start_tick();
		imem_start_tick();
		cpu_start_tick();

		// call each device to see if they have work to do on this cycle
		// again, order should not make a difference
		while (workToDo)
		{
				dmem_do_cycle_work();
				imem_do_cycle_work();
				cpu_do_cycle_work();

				// do any of the devices still have work for this cycle?
				workToDo = cpu.workToDo || dmem.workToDo || imem.workToDo;
		}
	}
}

// data memory functions

// reset data memory, seed with fixed values from Assignment 2
void dmem_reset()
{
		dmem.workToDo = false;
		dmem.offset = 0;
		dmem.mem = malloc(256);
		memset(dmem.mem, 0, 256);
		// seed mem 0-7 with values 1-8 (from sample 1, Assignment 2)
		dmem.mem[0] = 1;
		dmem.mem[1] = 2;
		dmem.mem[2] = 3;
		dmem.mem[3] = 4;
		dmem.mem[4] = 5;
		dmem.mem[5] = 6;
		dmem.mem[6] = 7;
		dmem.mem[7] = 8;

		dmem.cycle  = 0;
		dmem.state  = DMEM_IDLE;
}

// handle start of tick for data memory
void dmem_start_tick()
{
	// if we are in fetch mode, signal we MAY have work to do
	if (dmem.state == DMEM_FETCHING)
	{
		// we've started another cycle
		dmem.cycle++;
		dmem.workToDo = true;
	}
}

// called when someone wants us to fetch data
void dmem_fetch_word(unsigned offset)
{
	// remember values, and go to fetch state, signal work to do
	dmem.offset = offset;
	dmem.workToDo = true;
	dmem.cycle=1;
	dmem.state = DMEM_FETCHING;
}

// do any work for the cycle that needs doing
void dmem_do_cycle_work()
{
	if (dmem.workToDo)
	{
		// assume only fetch - add code to implement "store"
		if (dmem.cycle == 5)
		{
			// read from memory finished, give to CPU, go idle
			cpu_take_data(dmem.mem[dmem.offset]);
			dmem.offset = 0;
			dmem.cycle = 0;
			dmem.state = DMEM_IDLE;
		}
		dmem.workToDo = false; // nothing for this cycle
	}
}

// show memory contents
void dmem_dump(unsigned where, unsigned count)
{
	unsigned i;
	printf("ADDR   00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F\n");
	while (count)
	{
		printf("0x%04X ", where);
		for (i=0; count && i < 16; i++, count--)
			printf("%02X ", dmem.mem[where++]);
		printf("\n");
	}
	printf("\n");
}

// instruction memory functions

// reset instruction memory, seed half of example 1 from Assignment 2
void imem_reset()
{
		imem.workToDo = false;
		imem.mem = malloc(256 * 4);
		memset(imem.mem, 0, 256*4);
		imem.mem[0] = 0xA0700;      // lw $a $h
		imem.mem[1] = 0xA4000;      // lw $b $a
		imem.mem[2] = 0xA8100;      // lw $c $b
		imem.mem[3] = 0xAC200;      // lw $d $c
		imem.mem[4] = 0xB0300;      // lw $e $d
		imem.mem[5] = 0xB4400;      // lw $f $e
		imem.mem[6] = 0xB8500;      // lw $g $f
		imem.mem[7] = 0xBC600;      // lw $h $g
}

// instruction memory single cycle, nothing to do
void imem_start_tick()
{
}

// called when someone wants data from instruction memory
void imem_fetch_instr(unsigned offset)
{
		// remember values, schedule work
		imem.offset = offset;
		imem.workToDo = true;
}

// do any work for the cycle that needs doing
void imem_do_cycle_work()
{
	if (imem.workToDo)
	{
		// only work we know how to do is fetch instruction
		// grab data, give it to CPU
		cpu_take_inst(imem.mem[imem.offset]);

		// all done, nothing left to do
		imem.offset = 0;
		imem.workToDo = false;
	}
}

// CPU functions

// put CPU in known state
void cpu_reset()
{
	cpu.state = CPU_IDLE;
	cpu.PC = 0;
	memset(cpu.regs, 0, sizeof(cpu.regs));
	cpu.data = 0;
	cpu.nextInstr = 0;
	cpu.workToDo = false;
}

// handle clock tick
void cpu_start_tick()
{
	// if idle, fetch & wait for instruction memory
	if (cpu.state == CPU_IDLE)
	{
		imem_fetch_instr(cpu.PC);
		cpu.state = CPU_WAITING_ON_IMEM;

		// all done, wait for memory
		cpu.workToDo = false;
	}
}

// accept data from instruction memory
void cpu_take_inst(unsigned instruction)
{
	cpu.nextInstr = instruction;
	cpu.workToDo  = true;
}

// accept data from data memory
void cpu_take_data(unsigned char data)
{
	cpu.data = data;
	cpu.workToDo  = true;
}

// do any work for the cycle that needs doing
void cpu_do_cycle_work()
{
	// someone gave us work
	if (cpu.workToDo)
	{
		// if waiting on imem, must be our instruction!
		if (cpu.state == CPU_WAITING_ON_IMEM)
		{
			cpuInstr_t instr;
			instr = cpu.nextInstr >> 17;  // should save & reuse this

			// is it a load word?
			if (instr == CPU_LW)
			{
				// yup, extract target reg from instruction
				unsigned char targetReg;
				targetReg = (cpu.nextInstr >> 8) & 0x7;

				// ask dmem to get value inside of targetReg 
				dmem_fetch_word(cpu.regs[targetReg]);

				// all done, wait for answer
				cpu.state = CPU_WAITING_ON_DMEM;
				cpu.workToDo = false;
			}

		// if waiting on dmem, must be our data!
		} else if (cpu.state == CPU_WAITING_ON_DMEM)
		{
			cpuInstr_t instr;
			instr = cpu.nextInstr >> 17; // should reuse from above

			// is it a load word?
			if (instr == CPU_LW)
			{
				// yup, extract destination register, and put data there
				unsigned char destReg;
				destReg = (cpu.nextInstr >> 14) & 0x7;
				cpu.regs[destReg] = cpu.data;

				// all done, go back to idle
				cpu.workToDo = false;
				cpu.state = CPU_IDLE;
				cpu.data = 0;
				cpu.PC++;  // instruction complete
				cpu.nextInstr = 0;
			}
		}
	}
}

// show CPU registers, including PC
void cpu_dump()
{
	unsigned i;
	printf("PC 0x%02x\n", cpu.PC);
	for (i=0; i<8; i++)
		printf("R%c 0x%02x\n", 'A'+i, cpu.regs[i]);
	printf("\n");
}

int main(int argc, char *argv[])
{
	// reset all devices
	cpu_reset();
	dmem_reset();
	imem_reset();

	printf("Before\n");
	dmem_dump(0, 16);
	cpu_dump();

	// run 40 ticks
	clock_do_ticks(40);

	printf("After\n");
	dmem_dump(0, 16);
	cpu_dump();
	return 0;
}
