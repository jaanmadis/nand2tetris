@1
D=A
@23
M=D

@2
D=A
@24
M=D

@3
D=A
@25
M=D

@26
D=A
@0
M=D

@666
(LOOP)
@0
M=M-1
A=M
//D=M
@END
M,JNE
@LOOP
0,JMP
(END)