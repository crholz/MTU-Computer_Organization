; before execution, CPU regs all zero, memory is
; Addr   00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F
; 0x0000 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
;
; after execution, memory is
; Addr   00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F
; 0x0000 2A 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00

; use 5 ticks to keep writing memory address zero
; on tick 4, i/o device starts to reads from mem[0], done by tick 9
; it does not get the value 42, since still only in cache
addi $a $a 42 ; a = 42 (1 tick)
sw   $a $b    ; mem[0] = 42 cache hit (1 tick)
sw   $a $b    ; mem[0] = 42 cache hit (1 tick)
sw   $a $b    ; mem[0] = 42 cache hit (1 tick)
sw   $a $b    ; mem[0] = 42 cache hit (1 tick)
sw   $a $b    ; mem[0] = 42 cache hit (1 tick)
sw   $a $b    ; mem[0] = 42 cache hit (1 tick)
sw   $a $b    ; mem[0] = 42 cache hit (1 tick)
sw   $a $b    ; mem[0] = 42 cache hit (1 tick)
sw   $a $b    ; mem[0] = 42 cache hit (1 tick)

; parser script dumps memory, cache, and i/o dev at this point

; force cache flush
addi $h $h -1 ; h += 0xFF in 2's complement, setting $h to 0xFF (1 tick)
sw   $g $h    ; write to mem[0xFF] (255) which forces cache flush (5 ticks)
halt

; on tick 20, i/o device starts to read memory, done by 25
; parser script dumps memory, cache, and i/o dev at this point
