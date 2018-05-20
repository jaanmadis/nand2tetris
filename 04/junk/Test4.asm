// write 5 to 25
@5
D=A
@18
M=D

// write 5 to 25
@6
D=A
@19
M=D

// write 5 to 25
@8
D=A
@20
M=D

// write 5 to 25
@8
D=A
@21
M=D

// stack pointer
@22
D=A
@0
M=D

// LCL area
@25
D=A
@1
M=D

// write 7 to 25
@7
D=A
@27
M=D
@666

// push ptr 0/1
@THIS/THAT
D=M
@0
A=M
M=D
@0
M=M+1

//  pts 0/1
@0
M=M-1
A=M
D=M
@THIS/THAT
M=D


push pointer 0
push pointer 1
	*sp=this/that, sp++

pop pointer 0
pop pointer 1
	sp--,this/that=*sp

accessing pointer 0 should give this
accessing pointer 1 should give that

// prepare the address to write
@1
D=M
@2
D=D+A
@13
M=D

// decrease SP, get value
@0
M=M-1
A=M
D=M

// write to address
@13
A=M
M=D



//eq
@0
A=M-1
D=M
@13
M=D
@0
M=M-1
A=M-1
D=M
@13
D=D-M
@COMP
D,JEQ
D=-1
@END
0,JMP
(COMP)
D=0
(END)
@0
A=M-1
M=D


// neg

@0
A=M-1
D=M
M=-D


// add

@0
A=M-1
D=M
@13
M=D
@0
M=M-1
A=M-1
D=M
@13
D=D+M
@0
A=M-1
M=D