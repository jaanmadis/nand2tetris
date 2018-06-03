// push constant 111
@111
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 333
@333
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 888
@888
D=A
@SP
A=M
M=D
@SP
M=M+1
// pop static 8
@SP
M=M-1
A=M
D=M
@STATIC_1_8
M=D
// pop static 3
@SP
M=M-1
A=M
D=M
@STATIC_1_3
M=D
// pop static 1
@SP
M=M-1
A=M
D=M
@STATIC_1_1
M=D
// push static 3
@STATIC_1_3
D=M
@SP
A=M
M=D
@SP
M=M+1
// push static 1
@STATIC_1_1
D=M
@SP
A=M
M=D
@SP
M=M+1
// sub
@SP
A=M-1
D=M
@R13
M=D
@SP
M=M-1
A=M-1
D=M
@R13
D=D-M
@SP
A=M-1
M=D
// push static 8
@STATIC_1_8
D=M
@SP
A=M
M=D
@SP
M=M+1
// add
@SP
A=M-1
D=M
@R13
M=D
@SP
M=M-1
A=M-1
D=M
@R13
D=D+M
@SP
A=M-1
M=D
