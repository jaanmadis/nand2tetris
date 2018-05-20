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
// write 7 to 25
@7
D=A
@27
M=D
@666

// push lcl 2

// prepare the address read from
@1
D=M
@2
D=D+A
A=D
D=M
// write
@0
A=M
M=D
@0
M=M+1
