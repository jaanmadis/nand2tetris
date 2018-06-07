import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class JackTokenizer {
    
    public static final String C_TYPE_KEYWORD = "keyword";
    public static final String C_TYPE_SYMBOL = "symbol";
    public static final String C_TYPE_IDENTIFIER = "identifier";
    public static final String C_TYPE_INT_CONST = "integerConstant";
    public static final String C_TYPE_STRING_CONST = "stringConstant";

    public static final String C_KEYWORD_CLASS = "class";
    public static final String C_KEYWORD_CONSTRUCTOR = "constructor";
    public static final String C_KEYWORD_FUNCTION = "function";
    public static final String C_KEYWORD_METHOD = "method";
    public static final String C_KEYWORD_FIELD = "field";
    public static final String C_KEYWORD_STATIC = "static";
    public static final String C_KEYWORD_VAR = "var";
    public static final String C_KEYWORD_INT = "int";
    public static final String C_KEYWORD_CHAR = "char";
    public static final String C_KEYWORD_BOOLEAN = "boolean";
    public static final String C_KEYWORD_VOID = "void";
    public static final String C_KEYWORD_TRUE = "true";
    public static final String C_KEYWORD_FALSE = "false";
    public static final String C_KEYWORD_NULL = "null";
    public static final String C_KEYWORD_THIS = "this";
    public static final String C_KEYWORD_LET = "let";
    public static final String C_KEYWORD_DO = "do";
    public static final String C_KEYWORD_IF = "if";
    public static final String C_KEYWORD_ELSE = "else";
    public static final String C_KEYWORD_WHILE = "while";
    public static final String C_KEYWORD_RETURN = "return";
    
    private static final String C_BLOCK_BEGIN = "/*";
    private static final String C_BLOCK_END = "*/";
    
    private BufferedReader br = null;
    private BufferedWriter bw = null;

    private HashSet<String> keywords;
    private HashSet<Character> symbols;
    private ArrayList<String> tokens = null;
    private int tokenPtr; 
    
    public JackTokenizer(String file) {
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException x) {
            x.printStackTrace();
        }
        
        file = file.replace(".jack", "T.xml");
        file = file.replace("\\", "//");

        try {
            bw = new BufferedWriter(new FileWriter(file));
        } catch (IOException x) {
            x.printStackTrace();
        }

        InitKeywords();
        InitSymbols();
        tokens = new ArrayList<String>();
        scan();
        writeTokens();
        done();
        tokenPtr = -1;
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
    
    public void writeTokens() {
        tokenPtr = -1;
        write("<tokens>");
        while (hasMoreTokens()) {
            advance();
            String tokenType = tokenType();
            if (tokenType.equals(C_TYPE_KEYWORD)) {
                writeXML(C_TYPE_KEYWORD, keyWord());
            } else if (tokenType.equals(C_TYPE_SYMBOL)) {
                writeXML(C_TYPE_SYMBOL, String.valueOf(symbolStr()));
            } else if (tokenType.equals(C_TYPE_IDENTIFIER)) {
                writeXML(C_TYPE_IDENTIFIER, identifier());
            } else if (tokenType.equals(C_TYPE_INT_CONST)) {
                writeXML(C_TYPE_INT_CONST, String.valueOf(intVal()));
            } else if (tokenType.equals(C_TYPE_STRING_CONST)) {
                writeXML(C_TYPE_STRING_CONST, stringVal());
            }
        }
        write("</tokens>");
    }
    
    public boolean hasMoreTokens() {
        return tokenPtr < tokens.size() - 1;
    }
    
    public void advance() {
        ++tokenPtr;
    }
    
    public void unadvance() {
        --tokenPtr;
    }

    public String tokenType() {
        String token = tokens.get(tokenPtr);
        if (keywords.contains(token)) {
            return C_TYPE_KEYWORD;
        } else if (symbols.contains(token.charAt(0))) {
            return C_TYPE_SYMBOL;
        } else if (token.matches("\\d+")) {
            return C_TYPE_INT_CONST;
        } else if (token.charAt(0) == '"' & token.charAt(token.length() - 1) == '"') {
            return C_TYPE_STRING_CONST;
        } else {
            return C_TYPE_IDENTIFIER;
        }
    }
    
    public String keyWord() {
        return tokens.get(tokenPtr);
    }
    
    public char symbolChar() {
        return tokens.get(tokenPtr).charAt(0);
    }
    
    public String symbolStr() {
        String token = tokens.get(tokenPtr);
        if (token.equals(">")) {
            token = "&gt;";
        } else if (token.equals("<")) {
            token = "&lt;";
        } else if (token.equals("&")) {
            token = "&amp;";
        }
        return token;
    }
                  
    public String identifier() {
        return tokens.get(tokenPtr);
    }
    
    public int intVal() {
        return Integer.parseInt(tokens.get(tokenPtr));
    }
    
    public String stringVal() {
        String token = tokens.get(tokenPtr);
//        token = token.substring(1, token.length() - 1);
//        token = "&quot;" + token + "&quot;";
//        return token;
        return token.substring(1, token.length() - 1);
    }
    
    public int getTokenNumber() {
        return tokenPtr + 1;
    }
    
    private void InitKeywords() {
        keywords = new HashSet<String>();
        keywords.add(C_KEYWORD_CLASS);
        keywords.add(C_KEYWORD_CONSTRUCTOR);
        keywords.add(C_KEYWORD_FUNCTION);
        keywords.add(C_KEYWORD_METHOD);
        keywords.add(C_KEYWORD_FIELD);
        keywords.add(C_KEYWORD_STATIC);
        keywords.add(C_KEYWORD_VAR);
        keywords.add(C_KEYWORD_INT);
        keywords.add(C_KEYWORD_CHAR);
        keywords.add(C_KEYWORD_BOOLEAN);
        keywords.add(C_KEYWORD_VOID);
        keywords.add(C_KEYWORD_TRUE);
        keywords.add(C_KEYWORD_FALSE);
        keywords.add(C_KEYWORD_NULL);
        keywords.add(C_KEYWORD_THIS);
        keywords.add(C_KEYWORD_LET);
        keywords.add(C_KEYWORD_DO);
        keywords.add(C_KEYWORD_IF);
        keywords.add(C_KEYWORD_ELSE);
        keywords.add(C_KEYWORD_WHILE);
        keywords.add(C_KEYWORD_RETURN);
    }
    
    private void InitSymbols() {
        symbols = new HashSet<Character>();
        symbols.add('{');
        symbols.add('}');
        symbols.add('(');
        symbols.add(')');
        symbols.add('[');
        symbols.add(']');
        symbols.add('.');
        symbols.add(',');
        symbols.add(';');
        symbols.add('+');
        symbols.add('-');
        symbols.add('*');
        symbols.add('/');
        symbols.add('&');
        symbols.add('|');
        symbols.add('<');
        symbols.add('>');
        symbols.add('=');
        symbols.add('~');
    }
    
    private String getLine() {
        String result = null;
        if (br != null) {
            try {
                result = br.readLine();
            } catch (IOException x) {
                x.printStackTrace();
            }
        }
        return result;
    }
    
    private void scan() {
        for (String line = getLine(); line != null; line = getLine()) {
            line = stripBlockComment(line);
            line = stripInlineComment(line);
            line = stripTabs(line);
            line = stripDoubleSpaces(line);
            line = line.trim();
            if (line.length() > 0) {
                ScanLine(line);
            }
        }
    }
    
    private void ScanLine(String line) {
        int i1 = 0;
        boolean inStringConst = false;
        for (int i2 = 0; i2 < line.length(); ++i2) {
            if (inStringConst) {
                if (line.charAt(i2) == '"') {
                    addToken(line.substring(i1, i2 + 1));
                    i1 = i2 + 1;
                    inStringConst = false;
                }
            } else if (line.charAt(i2) == '"') {
                if (!inStringConst) {
                    addToken(line.substring(i1, i2));
                    i1 = i2;
                    inStringConst = true;
                }
            } else if (line.charAt(i2) == ' ') {
                addToken(line.substring(i1, i2));
                i1 = i2 + 1;
            } else if (symbols.contains(line.charAt(i2))) {
                addToken(line.substring(i1, i2));
                addToken(line.substring(i2, i2 + 1));
                i1 = i2 + 1;
            }
        }
        addToken(line.substring(i1, line.length()));
    }
    
    private void addToken(String token) {
        if (token.length() > 0) {
            tokens.add(token);
        }
    }
    
    private String stripBlockComment(String str) {
        String result = str;
        int blockBegin = result.indexOf(C_BLOCK_BEGIN);
        int blockEnd;
        while (blockBegin >= 0) {
            blockEnd = result.indexOf(C_BLOCK_END);
            if (blockEnd >= 0) {
                result = result.substring(0, blockBegin + C_BLOCK_BEGIN.length()) + result.substring(blockEnd, result.length());
                result = result.replace(C_BLOCK_BEGIN + C_BLOCK_END, " ");
                blockBegin = result.indexOf(C_BLOCK_BEGIN);
            } else {
                result = result + " " + getLine();
            }
        }
        return result;
    }

    private String stripInlineComment(String str) {
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

    private void write(String str) {
        if (bw != null) {
            try {
                bw.write(str + "\r\n");
            } catch (IOException x) {
                x.printStackTrace();
            }
        }
    }

    private void writeXML(String tag, String str) {
        str = "<" + tag + ">" + str + "</" + tag + ">";
        write(str);
    }
}