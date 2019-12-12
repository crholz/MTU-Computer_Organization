#include <stdio.h>
#include <string.h>

// Sample program that parses Assignment 1 input files
// and shows "got" followed by the parsed line.

// See the man page for "scanf" for details on its use
// Note, while this program uses fscanf(data, "%[^ \n\r]",
// I belive it works just as well with a fscanf(data, "%s"
// The "%[^" format string is show how you can stop
// scanf at certain characters

FILE *data; // input data file

// read a file that has memory values, and fill memory
// wiht those values starting at address "addr"
void readMemoryFile(unsigned addr)
{
	char fileName[100];
	FILE *memFile;
	unsigned memValue;
	// get name of file & open it
	fscanf(data, "%s", fileName);
	printf("%s\n", fileName);
	memFile = fopen(fileName, "r");

	// while we stil read a value, keep going
	while (fscanf(memFile, "%x", &memValue) > 0)
		;// mem[addr++] = memValue

	// clean up opened file
	fclose(memFile);
	return;
}

// manage the clock commands
void doClock()
{
	char     subcmd[10];
	unsigned tickCount;

	// read everything up to a space, newline, or carriage return
	fscanf(data, "%[^ \n\r]", subcmd);

	if (!strcmp(subcmd, "reset"))
	{
		printf("got clock reset\n");
		 // reset clock
	}
	else if (!strcmp(subcmd, "dump"))
	{
		printf("got clock dump\n");
		 // dump clock
	}
	else if (!strcmp(subcmd, "tick"))
	{
		fscanf(data, "%d", &tickCount);
		printf("got clock tick %d\n", tickCount);
		// doTick(tickCount);
	}
	else
		;// error
	
	return;
}

// manage the cpu commands
void doCpu()
{
	char subcmd[10];

	// read everything up to a space, newline, or carriage return
	fscanf(data, "%[^ \n\r]", subcmd);

	if (!strcmp(subcmd, "reset"))
	{
		printf("got cpu reset\n");
		 // reset cpu
	}
	else if (!strcmp(subcmd, "dump"))
	{
		printf("got cpu dump\n");
		 // dump cpu
	}
	else if (!strcmp(subcmd, "set"))
	{
		unsigned regNum, regValue;
		char regName[3];
		fscanf(data, " reg %s %x", regName, &regValue);
		if (!strcmp(regName, "PC"))
			; // cpuPC = regValue;
		else
		{
			regNum = regName[1] - 'A';
			// cpuRegs[regNum] = regValue;
		}
		printf("got set cpu reg %s 0x%X\n", regName, regValue);
	}
	else
		; // error

	return;
}

// manage the memory commands
void doMemory()
{
	char subcmd[10];

	// read everything up to a space, newline, or carriage return
	fscanf(data, "%[^ \n\r]", subcmd);

	if (!strcmp(subcmd, "reset"))
	{
		printf("got memory reset\n");
		 ; // reset memory
	}
	else if (!strcmp(subcmd, "create"))
	{
		unsigned memorySize;
		fscanf(data, "%x", &memorySize);
		printf("got memory create 0x%X\n", memorySize);
		 ; // create memory
	}
	else if (!strcmp(subcmd, "dump"))
	{
		unsigned addr, count;
		fscanf(data, "%x %x", &addr, &count);
		printf("got memory dump 0x%X 0x%X\n", addr, count);
		; // dump memory
	}
	else if (!strcmp(subcmd, "set"))
	{
		unsigned addr, count;
		char fileOrCount[10];

		// read input as number & string
		fscanf(data, "%x %s ", &addr, fileOrCount);

		// if string is NOT "file", process the line
		if (strcmp(fileOrCount, "file"))
		{
			// not a file, so process rest of line
			int i;
			// get count of how many values
			sscanf(fileOrCount, "%x", &count);
			printf("got memory set 0x%X 0x%0X ", addr, count);

			// loop, reading those values
			for (i=0; i<count; i++)
			{
				unsigned memValue;
				fscanf(data, "%x", &memValue);
				printf("0x%X ", memValue);
				// mem[addr++] = memValue;
			}
			printf("\n");
		}
		else
		{
			// we have a file, process it
			printf("got memory set 0x%X %s ", addr, fileOrCount);
			readMemoryFile(addr);
		}
	}
	else
		; // error
	return;
}

int main(int argc, char *argv[])
{
	int num;
	data = fopen(argv[1], "r");

	if (!data)
	{
		perror("cannot open data file");
		return 1;
	}

	while (1)
	{
		int count;
		char cmd[10];

		// scan a line, and eat space after command
		count = fscanf(data, "%s ", cmd);

		// scanf returns number of assigned vars; exit on 0
		if (count < 1)
			break;

		if (!strcmp(cmd, "clock"))
			doClock();
		else if (!strcmp(cmd, "cpu"))
			doCpu();
		else if (!strcmp(cmd, "memory"))
			doMemory();
		else
			printf("Bad device '%s'\n", cmd);
	}

	// be nice & clean up open files
	fclose(data);
	return 0;
}
