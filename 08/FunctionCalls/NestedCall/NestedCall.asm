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
(Sys.init)
// repeating 0 times:
// calling Sys.main 0
// push returnaddress
@RETURN_Sys.main1
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
@Sys.main
0,JMP
(RETURN_Sys.main1)
// pop temp 1
@SP
M=M-1
A=M
D=M
@6
M=D
// label LOOP
(LOOP)
// goto LOOP
@LOOP
0,JMP
(Sys.main)
// repeating 0 times:
// push constant 123
@123
D=A
@SP
A=M
M=D
@SP
M=M+1
// calling Sys.add12 1
// push returnaddress
@RETURN_Sys.add122
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
@Sys.add12
0,JMP
(RETURN_Sys.add122)
// pop temp 0
@SP
M=M-1
A=M
D=M
@5
M=D
// push constant 246
@246
D=A
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
(Sys.add12)
// repeating 3 times:
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
// push 0
@SP
A=M
M=0
@SP
M=M+1
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
// push constant 12
@12
D=A
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
