import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class VMWriter {

    public static final String C_SEGMENT_CONST = "constant";
    public static final String C_SEGMENT_ARG = "argument";
    public static final String C_SEGMENT_LOCAL = "local";
    public static final String C_SEGMENT_STATIC = "static";
    public static final String C_SEGMENT_THIS = "this";
    public static final String C_SEGMENT_THAT = "that";
    public static final String C_SEGMENT_POINTER = "pointer";
    public static final String C_SEGMENT_TEMP = "temp";

    public static final String C_ADD = "add";
    public static final String C_SUB = "sub";
    public static final String C_NEG = "neg";
    public static final String C_AND = "and";
    public static final String C_OR = "or";
    public static final String C_NOT = "not";
    public static final String C_EQ = "eq";
    public static final String C_GT = "gt";
    public static final String C_LT = "lt";

    public static final String C_PUSH = "push";
    public static final String C_POP = "pop";
    public static final String C_LABEL = "label";
    public static final String C_GOTO = "goto";
    public static final String C_IF = "if-goto";
    public static final String C_FUNCTION = "function";
    public static final String C_RETURN = "return";
    public static final String C_CALL = "call";
    
    private BufferedWriter bw = null;
    
    public VMWriter(String file) {
        file = file.replace(".jack", ".vm");
        file = file.replace("\\", "//");

        try {
            bw = new BufferedWriter(new FileWriter(file));
        } catch (IOException x) {
            x.printStackTrace();
        }
    }
    
    public void writePush(String segment, int index) {
        write(C_PUSH + " " + segment + " " + String.valueOf(index));
    }
    
    public void writePop(String segment, int index) {
        write(C_POP + " " + segment + " " + String.valueOf(index));
    }
    
    public void writeArithmetic(String command) {
        if (command.charAt(0) == '+') {
            write("add");
        } else if (command.charAt(0) == '-') {
            write("sub");
        } else if (command.charAt(0) == '*') {
            write("call Math.multiply 2");
        } else if (command.charAt(0) == '/') {
            write("call Math.divide 2");
        } else if (command.charAt(0) == '&') {
            write("and");
        } else if (command.charAt(0) == '|') {
            write("or");
        } else if (command.charAt(0) == '<') {
            write("lt");
        } else if (command.charAt(0) == '>') {
            write("gt");
        } else if (command.charAt(0) == '=') {
            write("eq");
        } else {
            write(command);
        }
    }

    public void writeLabel(String label) {
        write(C_LABEL + " " + label);
    }
    
    public void writeGoto(String label) {
        write(C_GOTO + " " + label);
    }
    
    public void writeIf(String label) {
        write(C_IF + " " + label);
    }
    
    public void writeCall(String name, int args) {
        write(C_CALL + " " + name + " " + String.valueOf(args));
    }
    
    public void writeFunction(String name, int args) {
        write(C_FUNCTION + " " + name + " " + String.valueOf(args));
    }
    
    public void writeReturn() {
        write(C_RETURN);
    }
    
    public void close() {
        if (bw != null) {
            try {
                bw.close();
            } catch (IOException x) {
                x.printStackTrace();
            }
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
    }
}
