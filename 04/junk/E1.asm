(INIT)
	@3
	D=A
	@three
	M=D
	
	@5
	D=A
	@five
	M=D
	
	@R1
	M=0
	
	@counter
	M=0

(LOOP)
	@R0
	D=M
	@counter
	D=D-M
	@END
	D, JEQ
	
	@counter
	D=M
	@moddividend
	M=D

	@three
	D=M
	@moddivisor
	M=D
	@MODENDTHREE
	D=A
	@modendptr
	M=D
	@MODBEGIN
	0, JMP
	(MODENDTHREE)

	@modresult
	D=M
	@WRITERESULTBEGIN
	D, JEQ
	
	@five
	D=M
	@moddivisor
	M=D
	@MODENDFIVE
	D=A
	@modendptr
	M=D
	@MODBEGIN
	0, JMP
	(MODENDFIVE)

	@modresult
	D=M
	@WRITERESULTBEGIN
	D, JEQ
	
	(WRITERESULTEND)
	@counter
	M=M+1
	
	@LOOP
	0, JMP
	
(WRITERESULTBEGIN)
	@counter
	D=M
	@R1
	M=M+D
	@WRITERESULTEND
	0, JMP
	
// modresult = moddividend % moddivisor

(MODBEGIN)
	@moddividend
	D=M
	@modrem
	M=D
	
(MODLOOP)
	@modrem
	D=M
	@MODZERO
	D, JEQ
	@MODNEG
	D, JLT
	@moddivisor
	D=M
	@modrem
	M=M-D
	@MODLOOP
	0, JMP
	
(MODZERO)
	@modresult
	M=0
	@MODEND
	0, JMP
	
(MODNEG)
	@modrem
	D=M
	@moddivisor
	D=D+M
	@modresult
	M=D
	@MODEND
	0, JMP

(MODEND)
	@modendptr
	A=M
	0, JMP
	
(END)
	@END
	0, JMP
	