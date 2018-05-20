// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

(LISTEN)
	@KBD
	D=M
	
	@SETWHITE
	D, JEQ
	
	// Default to black color
	@color
	M=-1
	@INITLOOP
	0, JMP
	
	(SETWHITE)
	@color
	M=0
	
	// Init loop index with SCREEN address
	(INITLOOP)
	@SCREEN
	D=A
	@i
	M=D

	(LOOP)
		@color
		D=M

		// Paint with color
		@i
		A=M
		M=D

		@i
		M=M+1

		// Jump back to LISTEN if we reached KDB address -- screen is fully painted
		D=M
		@KBD
		D=D-A
		@LISTEN
		D, JEQ

		@LOOP
		0, JMP

	@LISTEN
	0, JMP