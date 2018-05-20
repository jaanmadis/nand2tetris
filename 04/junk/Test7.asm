// push returnaddress
@RETURN_Counter
D=A
@SP
A=M
M=D
@SP
M=M+1
// push LCL
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
// push ARG
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
// push THIS
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
// push THAT
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
// ARG = SP - 5 - nargs
@SP
D=M
@Integer.toString(nargs)
D=D-A
@5
D=D-A
@ARG
M=D
// LCL = SP
@SP
D=M
@LCL
M=D
// goto functionName
@functionName
0,JMP
(RETURN_Counter)







// endFrame = LCL                      
@LCL
D=M
@R13
M=D
// retAddr = *(endFrame - 5)
@R13
D=M
@5
D=D-A
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
