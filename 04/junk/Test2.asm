// write 5 to 25
@5
D=A
@20
M=D
// stack pointer
@21
D=A
@0
M=D
// LCL area
@25
D=A
@1
M=D
@666

// pop lcl 2

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
