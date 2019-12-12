; before execution, CPU regs all zero, memory is
; Addr   00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F
; 0x0000 01 02 03 04 05 06 07 08 00 00 00 00 00 00 00 00
;
; after execution, memory is (via external I/O write to mem[1] of 0x27
; Addr   00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F
; 0x0000 01 27 03 04 05 06 07 08 00 00 00 00 00 00 00 00
;
; use 10 ticks to keep reading memory address zero
; on tick 7, the i/o device begins to modify memory address 1 to be 0x27,
; which is completed by tick 12
lw   $a $h ; a = mem[0], cache miss, load cache w/ mem[0-7] (5 ticks)
lw   $a $h ; a = mem[0], cache hit, (1 tick)
lw   $a $h ; a = mem[0], cache hit, (1 tick)
lw   $a $h ; a = mem[0], cache hit, (1 tick)
lw   $a $h ; a = mem[0], cache hit, (1 tick)
lw   $a $h ; a = mem[0], cache hit, (1 tick)
lw   $a $h ; a = mem[0], cache hit, (1 tick)
lw   $a $h ; a = mem[0], cache hit, (1 tick)
lw   $a $h ; a = mem[0], cache hit, (1 tick)

; read memory address 1 into $b
; will get 2 from cache, even though memory now holds 0x27
lw   $b $a ; b = mem[1], cache hit, (1 tick)

; parser script dumps CPU, memory, & cache at this point

; now invalidate cache
addi $h $h -1 ; h += 0xFF in 2's complement, setting $h to 0xFF (1 tick)
lw   $g $h    ; read address 0xFF (255) which invalidates the cache (1 tick)

; read from mem[1] again
; should bypass cache, and go to memory, get 0x27, and seed cache again
lw   $b $a ; b = mem[1], cache miss, (5 tick)

halt
; parser script dumps CPU, memory, & cache at this point
