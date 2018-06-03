import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
  
    public static final String C_COMMENT = "C_COMMENT";
    public static final String C_ARITHMETIC = "C_ARITHMETIC";
    public static final String C_PUSH = "push";
    public static final String C_POP = "pop";
    public static final String C_LABEL = "label";
    public static final String C_GOTO = "goto";
    public static final String C_IF = "if-goto";
    public static final String C_FUNCTION = "function";
    public static final String C_RETURN = "return";
    public static final String C_CALL = "call";

    public static final String C_ADD = "add";
    public static final String C_SUB = "sub";
    public static final String C_NEG = "neg";
    public static final String C_AND = "and";
    public static final String C_OR = "or";
    public static final String C_NOT = "not";
    public static final String C_EQ = "eq";
    public static final String C_GT = "gt";
    public static final String C_LT = "lt";
    
    private BufferedReader br = null;
    private String command = null;
    private String[] split = null;

    public Parser(String file) {
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException x) {
            x.printStackTrace();
        }
    }
    
    public Boolean hasMoreCommands() {
        if (br != null) {
            try {
                command = br.readLine();
            } catch (IOException x) {
                x.printStackTrace();
            }
        }
        return command != null;
    }
            
    public void advance() {
        if (command != null) {
            command = stripComments(command);
            command = stripTabs(command);
            command = stripDoubleSpaces(command);
            split = command.split(" ");
        }
    }
    
    public String commandType() {
        String result = C_COMMENT;
        if (command != null) {
            if (split[0].equals(C_PUSH)) {
                result = C_PUSH;
            } else if (split[0].equals(C_POP)) {
                result = C_POP;
            } else if ((split[0].equals(C_ADD)) || (split[0].equals(C_SUB)) || 
                    (split[0].equals(C_SUB)) || (split[0].equals(C_NEG)) || 
                    (split[0].equals(C_EQ)) || (split[0].equals(C_GT)) || 
                    (split[0].equals(C_LT)) || (split[0].equals(C_AND)) || 
                    (split[0].equals(C_OR)) || (split[0].equals(C_NOT))) {
                result = C_ARITHMETIC;
            } else if (split[0].equals(C_LABEL)) {
                result = C_LABEL;
            } else if (split[0].equals(C_GOTO)) {
                result = C_GOTO;
            } else if (split[0].equals(C_IF)) {
                result = C_IF;
            } else if (split[0].equals(C_FUNCTION)) {
                result = C_FUNCTION;
            } else if (split[0].equals(C_RETURN)) {
                result = C_RETURN;
            } else if (split[0].equals(C_CALL)) {
                result = C_CALL;
            }
        }
        return result;
    }
    
    public String arg1() {
        String commandType = commandType();
        if (commandType.equals(C_ARITHMETIC)) {
            return split[0];
        } else if (commandType().equals(C_RETURN)) {
            return C_COMMENT;
        } else {
            return split[1];
        }
    }
    
    public int arg2() {
        String commandType = commandType();
        if (commandType.equals(C_PUSH) || commandType.equals(C_POP) || 
                commandType.equals(C_FUNCTION) || commandType.equals(C_CALL)) {
            return Integer.parseInt(split[2]);
        } else {
            return -1;
        }
    }
    
    private String stripComments(String str) {
        String result = str;
        int i = result.indexOf("//");
        if (i != -1) {
            result = result.substring(0, i);
        }
        return result;
    }
    
    private String stripTabs(String str) {
        String result = str.replace("\t", " ");
        return result;
    }

    private String stripDoubleSpaces(String str) {
        String result = str;
        while (true) {
            int i = result.indexOf("  ");
            if (i == -1) {
                break;
            }
            result = result.substring(0, i) + result.substring(i + 1);
        }
        return result;
    }
}
