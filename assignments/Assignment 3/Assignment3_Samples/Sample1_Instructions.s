; execution of this program proceeded by following emulator commands
; cpu reset - all registers including PC set to zero
; memory create 0x100
; memory reset - all memory set to zero
;
; before execution, relevant memory is
; Addr   00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F
; 0x0000 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
;
; after execution, relevant memory is
; Addr   00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F
; 0x0000 00 00 00 00 00 00 00 00 00 00 00 00 F3 00 00 00
;
; after execution, registers are
; PC: 0x0A
; RA: 0x06
; RB: 0x06
; RC: 0x0C
; RD: 0xF3
; RE: 0xF3
; RF: 0x00
; RG: 0x00
; RH: 0x00
;
; Entropy sample program (RA-RH start with zero)
addi $a $a 35      ; stores 0x23 in RA
mul  $a $a         ; 0x2 x 0x3 = 0x6, store in RA
.loop:
addi $b $b 1       ; increment RB by 1
beq  $a $b endloop ; when RA==RB (both 0x6) break out of loop
beq  $h $h loop    ; effective jump/goto command
.endloop:
add  $c $a $b      ; 0x6+0x6=0xC, store 0xC in RC
inv  $d $c         ; invert RC to 0xF3 and store that in RD
sw   $d $c         ; store RD at address specified by RC (0xC)
lw   $e $c         ; load from address in RC (DMEM[0xC]=0xF3) into RE
halt               ; stop execution
;
; The above code produces the following 10 instructions
; addr instr
; 0000 20023
; 0001 40000
; 0002 24801
; 0003 80105
; 0004 83F02
; 0005 08100
; 0006 6D000
; 0007 C1A00
; 0008 B0200
; 0009 E0000
