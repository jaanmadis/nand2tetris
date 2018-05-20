@MODENDFIVE
	D=A
	@modendptr
	M=D
	@MODBEGIN
	0, JMP
	(MODENDFIVE)
	D=0

(MODBEGIN)	
	D=0
	
(MODEND)
	@modendptr
	A=M
	0, JMP
