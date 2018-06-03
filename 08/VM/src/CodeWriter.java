import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
    
    public static final String C_SP = "SP";
    
    public static final String C_LCL = "LCL";
    public static final String C_ARG = "ARG";
    public static final String C_THIS = "THIS";
    public static final String C_THAT = "THAT";
    
    public static final String C_CONST = "constant";
    public static final String C_STATIC = "static";
    public static final String C_TEMP = "temp";
    public static final String C_PTR = "pointer";
    public static final int C_TEMP_OFFSET = 5;
    
    public static final String C_R13 = "R13";
    public static final String C_R14 = "R14";
    public static final String C_R15 = "R15";
        
    private int compLabelCounter = 0;
    private int retLabelCounter = 0;
    private int nextCounter = 0;
    private BufferedWriter bw = null;
    
    public CodeWriter(String file) {
        try {
            bw = new BufferedWriter(new FileWriter(file));
        } catch (IOException x) {
            x.printStackTrace();
        }
    }
    
    public void writeArithmetic(String command) {
        write("// " + command);
        if (command.equals(Parser.C_ADD)) {
            writeAdd();
        } else if (command.equals(Parser.C_SUB)) {
            writeSub();
        } else if (command.equals(Parser.C_NEG)) {
            writeNeg();
        } else if (command.equals(Parser.C_AND)) {
            writeAnd();
        } else if (command.equals(Parser.C_OR)) {
            writeOr();
        } else if (command.equals(Parser.C_NOT)) {
            writeNot();
        } else if (command.equals(Parser.C_EQ)) {
            writeEq();
        } else if (command.equals(Parser.C_GT)) {
            writeGt();
        } else if (command.equals(Parser.C_LT)) {
            writeLt();
        }
    }
    
    public void writePushPop(String command, String segment, int index) {
        write("// " + command + 
                " " + segment + 
                " " + Integer.toString(index));
        String sg = getSegment(segment);
        if (command.equals(Parser.C_POP)) {
            if ((sg.equals(C_LCL)) || (sg.equals(C_ARG)) || 
                    (sg.equals(C_THIS)) || (sg.equals(C_THAT))) {
                writeGenericPop(sg, index);
            } else if (sg.equals(C_STATIC)) {
                writeStaticPop(index);
            } else if (sg.equals(C_TEMP)) {
                writeTempPop(index);
            } else if (sg.equals(C_PTR)) {
                writePointerPop(index);
            }
        } else if (command.equals(Parser.C_PUSH)) {
            if (sg.equals(C_CONST)) {
                writeConstPush(index);
            } else if ((sg.equals(C_LCL)) || (sg.equals(C_ARG)) || 
                    (sg.equals(C_THIS)) || (sg.equals(C_THAT))) {
                writeGenericPush(sg, index);
            } else if (sg.equals(C_STATIC)) {
                writeStaticPush(index);
            } else if (sg.equals(C_TEMP)) {
                writeTempPush(index);
            } else if (sg.equals(C_PTR)) {
                writePointerPush(index);
            }
        }
    }
    
    public void writeBootstrap() {
        write("@256");
        write("D=A");
        write("@" + C_SP);
        write("M=D");
        writeCall("Sys.init", 0);
    }
    
    public void writeLabel(String label) {
        write("// label " + label);
        write("(" + label + ")");
    }
    
    public void writeGoto(String label) {
        write("// goto " + label);
        write("@" + label);
        write("0,JMP");
    }
    
    public void writeIf(String label) {
        write("// if " + label);
        write("@" + C_SP);
        write("M=M-1");
        write("A=M");
        write("D=M");
        write("@" + label);
        write("D,JNE");
    }
    
    public void writeFunction(String name, int vars) {
        write("(" + name + ")");
        write("// repeating " + Integer.toString(vars) + " times:");
        for (int i = 0; i < vars; ++i) {
            write("// push 0");
            write("@" + C_SP);
            write("A=M");
            write("M=0");
            write("@" + C_SP);
            write("M=M+1");
        }    
    }
    
    public void writeReturn() {
        write("// return");
        write("// endFrame = LCL");                     
        write("@" + C_LCL);
        write("D=M");
        write("@" + C_R13);
        write("M=D");
        write("// retAddr = *(endFrame - 5)");
        write("@" + C_R13);
        write("D=M");
        write("@5");
        write("A=D-A");
        write("D=M");
        write("@" + C_R14);
        write("M=D");
        write("// *ARG = pop()");
        write("@" + C_SP);
        write("M=M-1");
        write("A=M");
        write("D=M");
        write("@" + C_ARG);
        write("A=M");
        write("M=D");
        write("// SP = ARG + 1");
        write("@" + C_ARG);
        write("D=M");
        write("D=D+1");
        write("@" + C_SP);
        write("M=D");
        write("// THAT = *(endFrame - 1)");
        write("@" + C_R13);
        write("D=M");
        write("@1");
        write("D=D-A");
        write("A=D");
        write("D=M");
        write("@" + C_THAT);
        write("M=D");
        write("// THIS = *(endFrame - 2)");
        write("@" + C_R13);
        write("D=M");
        write("@2");
        write("D=D-A");
        write("A=D");
        write("D=M");
        write("@" + C_THIS);
        write("M=D");
        write("// ARG = *(endFrame - 3)");
        write("@" + C_R13);
        write("D=M");
        write("@3");
        write("D=D-A");
        write("A=D");
        write("D=M");
        write("@" + C_ARG);
        write("M=D");
        write("// LCL = *(endFrame - 4)");
        write("@" + C_R13);
        write("D=M");
        write("@4");
        write("D=D-A");
        write("A=D");
        write("D=M");
        write("@" + C_LCL);
        write("M=D");
        write("// goto retAddr");
        write("@" + C_R14);
        write("A=M");
        write("0,JMP");
    }
    
    public void writeCall(String name, int args) {
        write("// calling " + name + " " + Integer.toString(args));
        write("// push returnaddress");
        write("@RETURN_" + name + Integer.toString(retLabelCounter));
        write("D=A");
        write("@" + C_SP);
        write("A=M");
        write("M=D");
        write("@" + C_SP);
        write("M=M+1");
        write("// push LCL");
        write("@" + C_LCL);
        write("D=M");
        write("@" + C_SP);
        write("A=M");
        write("M=D");
        write("@" + C_SP);
        write("M=M+1");
        write("// push ARG");
        write("@" + C_ARG);
        write("D=M");
        write("@" + C_SP);
        write("A=M");
        write("M=D");
        write("@" + C_SP);
        write("M=M+1");
        write("// push THIS");
        write("@" + C_THIS);
        write("D=M");
        write("@" + C_SP);
        write("A=M");
        write("M=D");
        write("@" + C_SP);
        write("M=M+1");
        write("// push THAT");
        write("@" + C_THAT);
        write("D=M");
        write("@" + C_SP);
        write("A=M");
        write("M=D");
        write("@" + C_SP);
        write("M=M+1");
        write("// ARG = SP - 5 - args");
        write("@" + C_SP);
        write("D=M");
        write("@" + Integer.toString(args));
        write("D=D-A");
        write("@5");
        write("D=D-A");
        write("@" + C_ARG);
        write("M=D");
        write("// LCL = SP");
        write("@" + C_SP);
        write("D=M");
        write("@" + C_LCL);
        write("M=D");
        write("// goto functionName");
        write("@" + name);
        write("0,JMP");
        write("(RETURN_" + name + Integer.toString(retLabelCounter) + ")");
        retLabelCounter++;
    }
    
    public void next() {
        nextCounter++;
    }
    
    public void done() {
        if (bw != null) {
            try {
                bw.close();
            } catch (IOException x) {
                x.printStackTrace();
            }
        }
    }
    
    private String getSegment(String str) {
        if (str.equals("local")) {
            return C_LCL;
        } else if (str.equals("argument")) {
            return C_ARG;
        } else if (str.equals("this")) {
            return C_THIS;
        } else if (str.equals("that")) {
            return C_THAT;
        } else {
            return str;
        }
    }
    
    private void write(String str) {
        if (bw != null) {
            try {
                bw.write(str + "\r\n");
            } catch (IOException x) {
                x.printStackTrace();
            }
        }
//        System.out.println(str);
    }
    
    private void writeAdd() {
        write("@" + C_SP);
        write("A=M-1");
        write("D=M");
        write("@" + C_R13);
        write("M=D");
        write("@" + C_SP);
        write("M=M-1");
        write("A=M-1");
        write("D=M");
        write("@" + C_R13);
        write("D=D+M");
        write("@" + C_SP);
        write("A=M-1");
        write("M=D");     
    }
    
    private void writeSub() {
        write("@" + C_SP);
        write("A=M-1");
        write("D=M");
        write("@" + C_R13);
        write("M=D");
        write("@" + C_SP);
        write("M=M-1");
        write("A=M-1");
        write("D=M");
        write("@" + C_R13);
        write("D=D-M");
        write("@" + C_SP);
        write("A=M-1");
        write("M=D");     
    }

    private void writeNeg() {
        write("@" + C_SP);
        write("A=M-1");
        write("D=M");
        write("M=-D");
    }

    private void writeEq() {
        write("@" + C_SP);
        write("A=M-1");
        write("D=M");
        write("@" + C_R13);
        write("M=D");
        write("@" + C_SP);
        write("M=M-1");
        write("A=M-1");
        write("D=M");
        write("@" + C_R13);
        write("D=D-M");
        write("@COMP" + Integer.toString(compLabelCounter));
        write("D,JEQ");
        write("D=0");
        write("@END" + Integer.toString(compLabelCounter));
        write("0,JMP");
        write("(COMP" + Integer.toString(compLabelCounter) + ")");
        write("D=-1");
        write("(END" + Integer.toString(compLabelCounter) + ")");
        write("@" + C_SP);
        write("A=M-1");
        write("M=D");
        compLabelCounter++;
    }

    private void writeGt() {
        write("@" + C_SP);
        write("A=M-1");
        write("D=M");
        write("@" + C_R13);
        write("M=D");
        write("@" + C_SP);
        write("M=M-1");
        write("A=M-1");
        write("D=M");
        write("@" + C_R13);
        write("D=D-M");
        write("@COMP" + Integer.toString(compLabelCounter));
        write("D,JGT");
        write("D=0");
        write("@END" + Integer.toString(compLabelCounter));
        write("0,JMP");
        write("(COMP" + Integer.toString(compLabelCounter) + ")");
        write("D=-1");
        write("(END" + Integer.toString(compLabelCounter) + ")");
        write("@" + C_SP);
        write("A=M-1");
        write("M=D");
        compLabelCounter++;
    }

    private void writeLt() {
        write("@" + C_SP);
        write("A=M-1");
        write("D=M");
        write("@" + C_R13);
        write("M=D");
        write("@" + C_SP);
        write("M=M-1");
        write("A=M-1");
        write("D=M");
        write("@" + C_R13);
        write("D=D-M");
        write("@COMP" + Integer.toString(compLabelCounter));
        write("D,JLT");
        write("D=0");
        write("@END" + Integer.toString(compLabelCounter));
        write("0,JMP");
        write("(COMP" + Integer.toString(compLabelCounter) + ")");
        write("D=-1");
        write("(END" + Integer.toString(compLabelCounter) + ")");
        write("@" + C_SP);
        write("A=M-1");
        write("M=D");
        compLabelCounter++;
    }

    private void writeAnd() {
        write("@" + C_SP);
        write("A=M-1");
        write("D=M");
        write("@" + C_R13);
        write("M=D");
        write("@" + C_SP);
        write("M=M-1");
        write("A=M-1");
        write("D=M");
        write("@" + C_R13);
        write("D=D&M");
        write("@" + C_SP);
        write("A=M-1");
        write("M=D");     
    }

    private void writeOr() {
        write("@" + C_SP);
        write("A=M-1");
        write("D=M");
        write("@" + C_R13);
        write("M=D");
        write("@" + C_SP);
        write("M=M-1");
        write("A=M-1");
        write("D=M");
        write("@" + C_R13);
        write("D=D|M");
        write("@" + C_SP);
        write("A=M-1");
        write("M=D");     
    }

    private void writeNot() {
        write("@" + C_SP);
        write("A=M-1");
        write("D=M");
        write("M=!D");
    }

    private void writeGenericPop(String segment, int index) {
        write("@" + segment);
        write("D=M");
        write("@" + Integer.toString(index));
        write("D=D+A");
        write("@" + C_R13);
        write("M=D");
        write("@" + C_SP);
        write("M=M-1");
        write("A=M");
        write("D=M");
        write("@" + C_R13);
        write("A=M");
        write("M=D");
    }

    private void writeStaticPop(int index) {
        write("@" + C_SP);
        write("M=M-1");
        write("A=M");
        write("D=M");
        write("@STATIC_" + Integer.toString(nextCounter) + "_" + Integer.toString(index));
        write("M=D");
    }
    
    private void writeTempPop(int index) {
        write("@" + C_SP);
        write("M=M-1");
        write("A=M");
        write("D=M");
        write("@" + Integer.toString(index + C_TEMP_OFFSET));
        write("M=D");
    }
    
    private void writePointerPop(int index) {
        write("@" + C_SP);
        write("M=M-1");
        write("A=M");
        write("D=M");
        if (index == 0) {
            write("@" + C_THIS);
        } else {
            write("@" + C_THAT);
        }
        write("M=D");
    }

    private void writeConstPush(int index) {
        write("@" + Integer.toString(index));
        write("D=A");
        write("@" + C_SP);
        write("A=M");
        write("M=D");
        write("@" + C_SP);
        write("M=M+1");
    }

    private void writeGenericPush(String segment, int index) {
        write("@" + segment);
        write("D=M");
        write("@" + Integer.toString(index));
        write("D=D+A");
        write("A=D");
        write("D=M");
        write("@" + C_SP);
        write("A=M");
        write("M=D");
        write("@" + C_SP);
        write("M=M+1");
    }
                 
    private void writeStaticPush(int index) {
        write("@STATIC_" + Integer.toString(nextCounter) + "_" + Integer.toString(index));
        write("D=M");
        write("@" + C_SP);
        write("A=M");
        write("M=D");
        write("@" + C_SP);
        write("M=M+1");
    }

    private void writeTempPush(int index) {
        write("@" + Integer.toString(index + C_TEMP_OFFSET));
        write("D=M");
        write("@" + C_SP);
        write("A=M");
        write("M=D");
        write("@" + C_SP);
        write("M=M+1");
    }

    private void writePointerPush(int index) {
        if (index == 0) {
            write("@" + C_THIS);
        } else {
            write("@" + C_THAT);
        }
        write("D=M");
        write("@" + C_SP);
        write("A=M");
        write("M=D");
        write("@" + C_SP);
        write("M=M+1");
    }
}
