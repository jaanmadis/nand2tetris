// push constant 123
@123
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 456
@456
D=A
@SP
A=M
M=D
@SP
M=M+1
// calling Call.fn 2
// push returnaddress
@RETURN_Call.fn0
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
@Call.fn
0,JMP
(RETURN_Call.fn0)
// label loop
(loop)
// goto loop
@loop
0,JMP
(Call.fn)
// repeating 0 times:
// label halt
(halt)
// goto halt
@halt
0,JMP
