@20
D=A
@SP
M=D
@666
@3
D=A
@END
D,JEQ
(LOOP)
@SP
A=M
M=1
@SP
M=M+1
D=D-1
@LOOP
D,JNE
(END)


// (functionName)
(functionName)
// repeat vars times:
// push 0
@SP
A=M
M=0
@SP
M=M+1