// instruction memory
typedef struct
{
	unsigned offset;    // from where to fetch
	bool     workToDo;  // is there still work to do in this cycle
	unsigned *mem; 		// pointer to instruction memory
} imem_t;

// data memory
typedef enum {DMEM_IDLE, DMEM_FETCHING} dmemState_t;
typedef struct
{
	dmemState_t    state;     // state of data memory
	unsigned       offset;    // from where to fetch
	bool           workToDo;  // is there still work to do in this cycle
	unsigned       cycle;     // what cycle of fetching are in
	unsigned char *mem;       // pointer to data memory
} dmem_t;

// CPU
typedef enum {CPU_ADD = 0, CPU_ADDI, CPU_MUL, CPU_INV, CPU_BEQ,
              CPU_LW, CPU_SW, CPU_HALT} cpuInstr_t;

typedef enum {CPU_IDLE, CPU_WAITING_ON_IMEM, CPU_WAITING_ON_DMEM} cpuState_t;
typedef struct
{
	unsigned      nextInstr; // incoming instruction from imem
	unsigned      data;      // incoming data from dmem
	bool          workToDo;  // is there still work to do in this cycle
	unsigned char PC;        // program counter
	unsigned char regs[8];   // registers RA-RH
	cpuState_t    state;     // state of CPU
} cpu_t;

// data memory functions
void dmem_start_tick();
void dmem_do_cycle_work();
void dmem_fetch_word(unsigned offset);
void dmem_reset();
void dmemm_dump(unsigned where, unsigned count);

// instruction memory functions
void imem_do_cycle_work();
void imem_start_tick();
void imem_fetch_instr(unsigned offset);
void imem_reset();

// CPU functions
void cpu_start_tick();
void cpu_do_cycle_work();
void cpu_take_inst(unsigned instruction);
void cpu_take_data(unsigned char data);
void cpu_dump();

// clock functions
void clock_do_ticks(unsigned count);
