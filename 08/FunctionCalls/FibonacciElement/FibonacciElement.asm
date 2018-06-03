@256
D=A
@SP
M=D
// calling Sys.init 0
// push returnaddress
@RETURN_Sys.init0
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
// ARG = SP - 5 - args
@SP
D=M
@0
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
@Sys.init
0,JMP
(RETURN_Sys.init0)
(Main.fibonacci)
// repeating 0 times:
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
// push constant 2
@2
D=A
@SP
A=M
M=D
@SP
M=M+1
// lt
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
@COMP0
D,JLT
D=0
@END0
0,JMP
(COMP0)
D=-1
(END0)
@SP
A=M-1
M=D
// if IF_TRUE
@SP
M=M-1
A=M
D=M
@IF_TRUE
D,JNE
// goto IF_FALSE
@IF_FALSE
0,JMP
// label IF_TRUE
(IF_TRUE)
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
// label IF_FALSE
(IF_FALSE)
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
// push constant 2
@2
D=A
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
// calling Main.fibonacci 1
// push returnaddress
@RETURN_Main.fibonacci1
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
// ARG = SP - 5 - args
@SP
D=M
@1
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
@Main.fibonacci
0,JMP
(RETURN_Main.fibonacci1)
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
// push constant 1
@1
D=A
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
// calling Main.fibonacci 1
// push returnaddress
@RETURN_Main.fibonacci2
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
// ARG = SP - 5 - args
@SP
D=M
@1
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
@Main.fibonacci
0,JMP
(RETURN_Main.fibonacci2)
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
@256
D=A
@SP
M=D
// calling Sys.init 0
// push returnaddress
@RETURN_Sys.init3
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
// ARG = SP - 5 - args
@SP
D=M
@0
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
@Sys.init
0,JMP
(RETURN_Sys.init3)
(Sys.init)
// repeating 0 times:
// push constant 4
@4
D=A
@SP
A=M
M=D
@SP
M=M+1
// calling Main.fibonacci 1
// push returnaddress
@RETURN_Main.fibonacci4
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
// ARG = SP - 5 - args
@SP
D=M
@1
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
@Main.fibonacci
0,JMP
(RETURN_Main.fibonacci4)
// label WHILE
(WHILE)
// goto WHILE
@WHILE
0,JMP
