// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

	@R2		// init result 0
	M=0
	@i		// iteration index == 0
	M=0
(LOOP)
	@R1
	D=M
	@i
	M=M+1	// iteration index++
	D=D-M	// jump to end if R1 - iteration index < 0
	@END
	D, JLT
	@R0
	D=M
	@R2
	M=M+D	// result = result + R0
	@LOOP
	0, JMP
(END)
	@END
	0, JMP