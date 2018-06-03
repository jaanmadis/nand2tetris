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
(Class1.set)
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
// pop static 0
@SP
M=M-1
A=M
D=M
@STATIC_1_0
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
// pop static 1
@SP
M=M-1
A=M
D=M
@STATIC_1_1
M=D
// push constant 0
@0
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
(Class1.get)
// repeating 0 times:
// push static 0
@STATIC_1_0
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
@RETURN_Sys.init1
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
(RETURN_Sys.init1)
(Class2.set)
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
// pop static 0
@SP
M=M-1
A=M
D=M
@STATIC_2_0
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
// pop static 1
@SP
M=M-1
A=M
D=M
@STATIC_2_1
M=D
// push constant 0
@0
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
(Class2.get)
// repeating 0 times:
// push static 0
@STATIC_2_0
D=M
@SP
A=M
M=D
@SP
M=M+1
// push static 1
@STATIC_2_1
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
@256
D=A
@SP
M=D
// calling Sys.init 0
// push returnaddress
@RETURN_Sys.init2
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
(RETURN_Sys.init2)
(Sys.init)
// repeating 0 times:
// push constant 6
@6
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 8
@8
D=A
@SP
A=M
M=D
@SP
M=M+1
// calling Class1.set 2
// push returnaddress
@RETURN_Class1.set3
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
@2
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
@Class1.set
0,JMP
(RETURN_Class1.set3)
// pop temp 0
@SP
M=M-1
A=M
D=M
@5
M=D
// push constant 23
@23
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 15
@15
D=A
@SP
A=M
M=D
@SP
M=M+1
// calling Class2.set 2
// push returnaddress
@RETURN_Class2.set4
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
@2
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
@Class2.set
0,JMP
(RETURN_Class2.set4)
// pop temp 0
@SP
M=M-1
A=M
D=M
@5
M=D
// calling Class1.get 0
// push returnaddress
@RETURN_Class1.get5
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
@Class1.get
0,JMP
(RETURN_Class1.get5)
// calling Class2.get 0
// push returnaddress
@RETURN_Class2.get6
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
@Class2.get
0,JMP
(RETURN_Class2.get6)
// label WHILE
(WHILE)
// goto WHILE
@WHILE
0,JMP
