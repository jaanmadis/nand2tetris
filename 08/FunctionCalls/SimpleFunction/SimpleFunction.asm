(SimpleFunction.test)
// repeating 2 times:
// push 0
@SP
A=M
M=0
@SP
M=M+1
// push 0
@SP
A=M
M=0
@SP
M=M+1
// push local 0
@LCL
D=M
@0
D=D+A
A=D
D=M
@SP
A=M
M=D
@SP
M=M+1
// push local 1
@LCL
D=M
@1
D=D+A
A=D
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
// not
@SP
A=M-1
D=M
M=!D
// push argument 0
@ARG
D=M
@0
D=D+A
A=D
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
// push argument 1
@ARG
D=M
@1
D=D+A
A=D
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
// return
// endFrame = LCL
@LCL
D=M
@R13
M=D
// retAddr = *(endFrame - 5)
@R13
D=M
@5
A=D-A
D=M
@R14
M=D
// *ARG = pop()
@SP
M=M-1
A=M
D=M
@ARG
A=M
M=D
// SP = ARG + 1
@ARG
D=M
D=D+1
@SP
M=D
// THAT = *(endFrame - 1)
@R13
D=M
@1
D=D-A
A=D
D=M
@THAT
M=D
// THIS = *(endFrame - 2)
@R13
D=M
@2
D=D-A
A=D
D=M
@THIS
M=D
// ARG = *(endFrame - 3)
@R13
D=M
@3
D=D-A
A=D
D=M
@ARG
M=D
// LCL = *(endFrame - 4)
@R13
D=M
@4
D=D-A
A=D
D=M
@LCL
M=D
// goto retAddr
@R14
A=M
0,JMP
