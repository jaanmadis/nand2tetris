// R2 = R0 % R1

@R0
D=M

@rem
M=D

(LOOP)
	@rem
	D=M
	
	@ZERO
	D, JEQ
	
	@NEG
	D, JLT

	@R1
	D=M
	
	@rem
	M=M-D

	@LOOP
	0, JMP
	
(ZERO)
	@R2
	M=0

	@END
	0, JMP
	
(NEG)
	@rem
	D=M

	@R1
	D=D+M

	@R2
	M=D
	
(END)
	@END
	0, JMP