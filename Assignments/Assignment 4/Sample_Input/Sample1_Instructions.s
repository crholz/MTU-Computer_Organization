; before execution, CPU regs all zero, memory is
; Addr   00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F
; 0x0000 01 02 03 04 05 06 07 08 00 00 00 00 00 00 00 00
;
; after execution, memory is
; Addr   00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F
; 0x0000 01 08 07 06 05 04 03 02 01 00 00 00 00 00 00 00
;
lw   $a $h ; a = mem[0], cache miss, load cache w/ mem[0-7] (5 ticks)
lw   $b $a ; b = mem[1], cache hit (1 tick)
lw   $c $b ; c = mem[2], cache hit (1 tick)
lw   $d $c ; d = mem[3], cache hit (1 tick)
lw   $e $d ; e = mem[4], cache hit (1 tick)
lw   $f $e ; f = mem[5], cache hit (1 tick)
lw   $g $f ; g = mem[6], cache hit (1 tick)
lw   $h $g ; h = mem[7], cache hit (1 tick)
sw   $a $h ; mem[8] = a, cache miss, invalidate, store a to cache only (1 tick)
sw   $b $g ; mem[7] = b, cache miss, flush cache (update mem[8]) (5 ticks)
sw   $c $f ; mem[6] = c, cache hit, store only to cache (1 tick)
sw   $d $e ; mem[5] = d, cache hit, store only to cache (1 tick)
sw   $e $d ; mem[4] = e, cache hit, store only to cache (1 tick)
sw   $f $c ; mem[3] = f, cache hit, store only to cache (1 tick)
sw   $g $b ; mem[2] = g, cache hit, store only to cache (1 tick)
sw   $h $a ; mem[1] = h, cache hit, store only to cache (1 tick)
addi $a $a 254 ; a += 254, setting at to 255 (1 tick)
sw   $a $a ; mem[FF] = FF - flush cache, write to mem[1-7] (5 ticks)
halt ; (1 tick)
