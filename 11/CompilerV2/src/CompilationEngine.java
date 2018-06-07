import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CompilationEngine {
    
    private static final String C_CLASS_NAME = "classname";
    private static final String C_SUB_NAME = "subname";

    private BufferedWriter bw = null;
    private JackTokenizer tz;
    private VMWriter vm;
    private SymbolTable st;
    private int indent = 0;
    
    private String currentClassName = "";
    private String currentSubroutineName = "";
    private String currentSubroutineType = "";
    
    private int labelIndexIf = 0;
    private int labelIndexWhile = 0;

    public CompilationEngine(String inFile, String outFile) {
        outFile = outFile.replace("\\", "//");

        try {
            bw = new BufferedWriter(new FileWriter(outFile));
        } catch (IOException x) {
            x.printStackTrace();
        }

        tz = new JackTokenizer(inFile);
        vm = new VMWriter(inFile);
        st = new SymbolTable();
    }
    
    public void done() {
        if (bw != null) {
            try {
                bw.close();
            } catch (IOException x) {
                x.printStackTrace();
            }
        }
        vm.close();
    }
    
    public void compileClass() {
        tz.advance();
        require(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_CLASS);
        writeTagOpen(JackTokenizer.C_KEYWORD_CLASS);

        writeXML(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_CLASS);
        
        tz.advance();
        require(JackTokenizer.C_TYPE_IDENTIFIER);
        
        writeIdentifierNamed(tz.identifier(), C_CLASS_NAME);
        currentClassName = tz.identifier();
        
        tz.advance();
        require('{');
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        
        compileClassVarDec();
        compileSubroutineDec();

        tz.advance();
        require('}');
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        
        writeTagClose(JackTokenizer.C_KEYWORD_CLASS);
    }

    public void compileClassVarDec() {
        boolean doneOuter = false;
        while (!doneOuter) {
            tz.advance();
            if (query(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_STATIC) | 
                    query(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_FIELD)) {
                writeTagOpen("classVarDec");
                
                // ('static'|'field')                
                writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
                
                int kind = -1;
                if (tz.keyWord().equals(JackTokenizer.C_KEYWORD_STATIC)) {
                    kind = SymbolTable.C_KIND_STATIC;
                } else if (tz.keyWord().equals(JackTokenizer.C_KEYWORD_FIELD)) {
                    kind = SymbolTable.C_KIND_FIELD;
                }
                
                tz.advance();
                
                // type
                String type = "";
                if (query(JackTokenizer.C_TYPE_KEYWORD)) {
                    writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
                    type = tz.keyWord();
                } else if (query(JackTokenizer.C_TYPE_IDENTIFIER)) {
                    writeIdentifierNamed(tz.identifier(), C_CLASS_NAME);
                    type = tz.identifier();
                } else {
                    err();
                }

                // name
                boolean doneInner = false;
                while (!doneInner) {
                    tz.advance();
                    require(JackTokenizer.C_TYPE_IDENTIFIER);
                    
                    writeIdentifierDefined(tz.identifier(), type, kind);

                    tz.advance();
                    if (query(',')) {
                        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
                    } else {
                        tz.unadvance();
                        doneInner = true;
                    }
                }

                tz.advance();
                require(';');
                writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());

                writeTagClose("classVarDec");
            } else {
                tz.unadvance();
                doneOuter = true;
            }
        }
    }

    public void compileSubroutineDec() {
        boolean done = false;
        while (!done) {
            tz.advance();
            if (query(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_CONSTRUCTOR) | 
                    query(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_FUNCTION) | 
                    query(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_METHOD)) {
                st.startSubroutine();
                this.labelIndexIf = 0;
                this.labelIndexWhile = 0;
                
                writeTagOpen("subroutineDec");
                writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
                currentSubroutineType = tz.keyWord();

                tz.advance();
                
                // ('void'|type)
                if (query(JackTokenizer.C_TYPE_KEYWORD)) {
                    writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
                } else if (query(JackTokenizer.C_TYPE_IDENTIFIER)) {
                    writeIdentifierNamed(tz.identifier(), C_CLASS_NAME);
                } else {
                    err();
                }

                tz.advance();
                require(JackTokenizer.C_TYPE_IDENTIFIER);
                
                // subroutineName
                writeIdentifierNamed(tz.identifier(), C_SUB_NAME);
                currentSubroutineName = tz.identifier();

                tz.advance();
                require('(');
                writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());

                if (currentSubroutineType.equals(JackTokenizer.C_KEYWORD_METHOD)) {
                    st.define(currentClassName, currentClassName, SymbolTable.C_KIND_ARG);
                }                
                
                compileParameterList();

                tz.advance();
                require(')');
                writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());

                compileSubroutineBody();

                writeTagClose("subroutineDec");
            } else {
                tz.unadvance();
                done = true;
            }
        }
    }

    public void compileParameterList() {
        writeTagOpen("parameterList");
        
        boolean done = false;
        while (!done) {
            tz.advance();
            if (query(JackTokenizer.C_TYPE_KEYWORD)) {
                writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
                
                String type = tz.keyWord();

                tz.advance();
                writeIdentifierDefined(tz.identifier(), type, SymbolTable.C_KIND_ARG);
                
                tz.advance();
                if (query(',')) {
                    writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
                } else {
                    tz.unadvance();
                }
            } else if (query(JackTokenizer.C_TYPE_IDENTIFIER)) {
                writeIdentifierNamed(tz.identifier(), C_CLASS_NAME);
                
                String type = tz.identifier();
                
                tz.advance();
                writeIdentifierDefined(tz.identifier(), type, SymbolTable.C_KIND_ARG);
                
                tz.advance();
                if (query(',')) {
                    writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
                } else {
                    tz.unadvance();
                }
            } else {
                tz.unadvance();
                done = true;
            }
        }

        writeTagClose("parameterList");
    }

    public void compileSubroutineBody() {
        writeTagOpen("subroutineBody");

        tz.advance();
        require('{');
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        
        compileVarDec();

        vm.writeFunction(currentClassName + "." + currentSubroutineName, st.varCount(SymbolTable.C_KIND_LOCAL));
        if (currentSubroutineType.equals(JackTokenizer.C_KEYWORD_CONSTRUCTOR)) {
            vm.writePush(VMWriter.C_SEGMENT_CONST, st.varCount(SymbolTable.C_KIND_FIELD));
            vm.writeCall("Memory.alloc", 1);
            vm.writePop(VMWriter.C_SEGMENT_POINTER, 0);
        } else if (currentSubroutineType.equals(JackTokenizer.C_KEYWORD_METHOD)) {
            vm.writePush(VMWriter.C_SEGMENT_ARG, 0);
            vm.writePop(VMWriter.C_SEGMENT_POINTER, 0);
        }
      
        compileStatements();

        tz.advance();
        require('}');
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        
        writeTagClose("subroutineBody");
    }

    public void compileVarDec() {
        boolean doneOuter = false;
        while (!doneOuter) {
            tz.advance();
            if (query(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_VAR)) {
                writeTagOpen("varDec");
                writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
                
                tz.advance();
                
                String type = "";
                if (query(JackTokenizer.C_TYPE_KEYWORD)) {
                    writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
                    type = tz.keyWord();
                } else if (query(JackTokenizer.C_TYPE_IDENTIFIER)) {
                    writeIdentifierNamed(tz.identifier(), C_CLASS_NAME);
                    type = tz.identifier();
                } else {
                    err();
                }

                boolean doneInner = false;
                while (!doneInner) {
                    tz.advance();
                    require(JackTokenizer.C_TYPE_IDENTIFIER);
                    writeIdentifierDefined(tz.identifier(), type, SymbolTable.C_KIND_LOCAL);

                    tz.advance();
                    if (query(',')) {
                        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
                    } else {
                        tz.unadvance();
                        doneInner = true;
                    }
                }

                tz.advance();
                require(';');
                writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
                
                writeTagClose("varDec");
            } else {
                tz.unadvance();
                doneOuter = true;
            }
        }
    }

    public void compileStatements() {
        writeTagOpen("statements");
        
        boolean done = false;
        while (!done) {
            tz.advance();
            if (query(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_LET)) {
                compileLet();
            } else if (query(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_IF)) {
                compileIf();
            } else if (query(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_WHILE)) {
                compileWhile();
            } else if (query(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_DO)) {
                compileDo();
            } else if (query(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_RETURN)) {
                compileReturn();
            } else {
                tz.unadvance();
                done = true;
            }
        }
        
        writeTagClose("statements");
    }

    public void compileLet() {
        writeTagOpen("letStatement");

        require(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_LET);
        writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
        
        tz.advance();
        require(JackTokenizer.C_TYPE_IDENTIFIER);
        writeIdentifierUsed(tz.identifier());
        
        String name = tz.identifier();
        
        boolean hasArray = false;
        tz.advance();
        if (query('[')) {
            hasArray = true;
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
            
            compileExpression();
    
            tz.advance();
            require(']');
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
            
            vm.writePush(st.getSegment(name), st.indexOf(name));
            vm.writeArithmetic("add");
        } else {
            tz.unadvance();
        }
        
        tz.advance();
        require('=');
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());

        compileExpression();
        
        tz.advance();
        require(';');
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        
        if (hasArray) {
            vm.writePop(VMWriter.C_SEGMENT_TEMP, 0);
            vm.writePop(VMWriter.C_SEGMENT_POINTER, 1);
            vm.writePush(VMWriter.C_SEGMENT_TEMP, 0);
            vm.writePop(VMWriter.C_SEGMENT_THAT, 0);
        } else {
            vm.writePop(st.getSegment(name), st.indexOf(name));
        } 
        
        writeTagClose("letStatement");
    }

    public void compileIf() {
        
        writeTagOpen("ifStatement");
        
        require(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_IF);
        writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
        
        tz.advance();
        require('(');
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        
        compileExpression();
        
        tz.advance();
        require(')');
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        
        int index = labelIndexIf;
        ++labelIndexIf;

        vm.writeIf(getLabelIfTrue(index));
        vm.writeGoto(getLabelIfFalse(index));
        vm.writeLabel(getLabelIfTrue(index));
        
        tz.advance();
        require('{');
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        
        compileStatements();
        
        tz.advance();
        require('}');
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        
        boolean hasElse = false;
        tz.advance();
        if (query(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_ELSE)) {
            writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
            hasElse = true;

            tz.advance();
            require('{');
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
           
            vm.writeGoto(getLabelIfEnd(index));
            vm.writeLabel(getLabelIfFalse(index));
            
            compileStatements();

            vm.writeLabel(getLabelIfEnd(index));
        
            tz.advance();
            require('}');
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        } else {
            tz.unadvance();
        }
        
        if (!hasElse) {
            vm.writeLabel(getLabelIfFalse(index));
        }
        
        writeTagClose("ifStatement");
    }

    public void compileWhile() {
        writeTagOpen("whileStatement");
        
        require(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_WHILE);
        writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
        
        int index = labelIndexWhile;
        ++labelIndexWhile;

        vm.writeLabel(getLabelWhileExp(index));
        
        tz.advance();
        require('(');
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        
        compileExpression();

        vm.writeArithmetic("not");
        vm.writeIf(getLabelWhileEnd(index));
        
        tz.advance();
        require(')');
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());

        tz.advance();
        require('{');
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        
        compileStatements();
        
        tz.advance();
        require('}');
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        
        vm.writeGoto(getLabelWhileExp(index));
        vm.writeLabel(getLabelWhileEnd(index));

        writeTagClose("whileStatement");
    }

    public void compileDo() {
        writeTagOpen("doStatement");
        
        require(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_DO);
        writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
        
        compileSubroutineCall();
        
        tz.advance();
        require(';');
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        
        vm.writePop(VMWriter.C_SEGMENT_TEMP, 0);
        
        writeTagClose("doStatement");
    }

    public void compileReturn() {
        writeTagOpen("returnStatement");
        
        require(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_RETURN);
        writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
        
        tz.advance();
        boolean hasExpression = !query(';');
        tz.unadvance();
        
        if (hasExpression) {
            compileExpression();
        }
        
        tz.advance();
        require(';');
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        
        if (!hasExpression) {
            vm.writePush(VMWriter.C_SEGMENT_CONST, 0);
        }
        vm.writeReturn();
        
        writeTagClose("returnStatement");
    }

    public void compileExpression() {
        writeTagOpen("expression");
        
        ArrayList<String> operators = new ArrayList<String>();
        
        boolean done = false;
        while (!done) {
            compileTerm();
            tz.advance();
            if (query('+') | query('-') | query('*') | query('/') | query('&') | 
                    query('|') | query('<') | query('>') | query('=')) {
                writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
                operators.add(String.valueOf(tz.symbolChar()));
            } else {
                tz.unadvance();
                done = true;
            }
        }
        
        for(int i = 0; i < operators.size(); ++i) {
            vm.writeArithmetic(operators.get(i));
        }

        writeTagClose("expression");
    }
    
    public void compileTerm() {
        writeTagOpen("term");
        
        tz.advance();
        
        // integer constant
        if (query(JackTokenizer.C_TYPE_INT_CONST)) {
            writeXML(JackTokenizer.C_TYPE_INT_CONST, String.valueOf(tz.intVal()));
            vm.writePush(VMWriter.C_SEGMENT_CONST, tz.intVal());
        
        // string constant
        } else if (query(JackTokenizer.C_TYPE_STRING_CONST)) {
            writeXML(JackTokenizer.C_TYPE_STRING_CONST, tz.stringVal());
            vm.writePush(VMWriter.C_SEGMENT_CONST, tz.stringVal().length());
            vm.writeCall("String.new", 1);
            for (int strIdx = 0; strIdx < tz.stringVal().length(); ++strIdx) {
                vm.writePush(VMWriter.C_SEGMENT_CONST, tz.stringVal().charAt(strIdx));
                vm.writeCall("String.appendChar", 2);
            }
        // keyword constant
        } else if (query(JackTokenizer.C_TYPE_KEYWORD)) {
            writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
            if (tz.keyWord().endsWith(JackTokenizer.C_KEYWORD_TRUE)) {
                vm.writePush(VMWriter.C_SEGMENT_CONST, 0);
                vm.writeArithmetic("not");
            } else if (tz.keyWord().endsWith(JackTokenizer.C_KEYWORD_FALSE)) {
                vm.writePush(VMWriter.C_SEGMENT_CONST, 0);
            } else if (tz.keyWord().endsWith(JackTokenizer.C_KEYWORD_NULL)) {
                vm.writePush(VMWriter.C_SEGMENT_CONST, 0);
            } else if (tz.keyWord().endsWith(JackTokenizer.C_KEYWORD_THIS)) {
                vm.writePush(VMWriter.C_SEGMENT_POINTER, 0);
            }
            
        // 1) varname | 2) varname[ expression ] | subroutine call // 3) varname.call() or 4) Class.call() or 5) call()
        } else if (query(JackTokenizer.C_TYPE_IDENTIFIER)) {
            String identifier = tz.identifier();
            
            tz.advance();
            if (query('[')) {
                // 2
                writeIdentifierUsed(identifier);
                writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
                
                compileExpression();
                
                tz.advance();
                require(']');
                writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
                
                vm.writePush(st.getSegment(identifier), st.indexOf(identifier));
                vm.writeArithmetic("add");
                vm.writePop(VMWriter.C_SEGMENT_POINTER, 1);
                vm.writePush(VMWriter.C_SEGMENT_THAT, 0);
            } else if (query('.')) {
                // 3/4
                tz.unadvance();
                tz.unadvance();
                compileSubroutineCall();
            } else if (query('(')) {
                // 5
                tz.unadvance();
                tz.unadvance();
                compileSubroutineCall();
            } else {
                // 1
                writeIdentifierUsed(identifier);
                vm.writePush(st.getSegment(identifier), st.indexOf(identifier));
                tz.unadvance();
            }
        // brackets ( )
        } else if (query('(')) {
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
            compileExpression();
            tz.advance();
            require(')');
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        
        // unary
        } else if (query('-')) {
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
            compileTerm();
            vm.writeArithmetic("neg");
        } else if (query('~')) {
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
            compileTerm();
            vm.writeArithmetic("not");
        }
        
        writeTagClose("term");
    }
   
    private void compileSubroutineCall() {
        tz.advance();
        require(JackTokenizer.C_TYPE_IDENTIFIER);
        
        String identifier = tz.identifier();
        String name;
        String segment = "";
        int index = -1; 
        boolean isMethodCall;
        if (st.exists(identifier)) {
            writeIdentifierUsed(identifier);
            name = st.typeOf(identifier);
            isMethodCall = true;
            segment = st.getSegment(identifier);
            index = st.indexOf(identifier);
        } else {
            tz.advance();
            if (query('.')) {
                writeIdentifierNamed(identifier, C_CLASS_NAME);
                name = identifier;
                isMethodCall = false;
            }
            else {
                writeIdentifierNamed(identifier, C_SUB_NAME);
                name = currentClassName + "." + identifier;
                isMethodCall = true;
                segment = VMWriter.C_SEGMENT_POINTER;
                index = 0;
            }
            tz.unadvance();
        }
        
        tz.advance();
        if (query('.')) {
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
            name = name + ".";

            tz.advance();
            require(JackTokenizer.C_TYPE_IDENTIFIER);
            writeIdentifierNamed(tz.identifier(), C_SUB_NAME);
            name = name + tz.identifier();
        } else {
            tz.unadvance();
        }

        tz.advance();
        require('(');
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());

        if (isMethodCall) {
            vm.writePush(segment, index);
        }
        int argCount = compileExpressionList();
        if (isMethodCall) {
//            vm.writePush(segment, index);
            ++argCount;
        }

        tz.advance();
        require(')');
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        
        vm.writeCall(name, argCount);
    }
    
    public int compileExpressionList() {
        int count = 0;
        
        writeTagOpen("expressionList");
        
        tz.advance();
        boolean done = query(')');
        tz.unadvance();
        
        while (!done) {
            compileExpression();
            ++count;
            tz.advance();
            if (query(',')) {
                writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
            } else {
                tz.unadvance();
                done = true;
            }
        }
        
        writeTagClose("expressionList");
        
        return count;
    }
    
    private void write(String str) {
        for (int i = 0; i < indent; ++i) {
            str = "  " + str;
        }
        if (bw != null) {
            try {
                bw.write(str + "\r\n");
            } catch (IOException x) {
                x.printStackTrace();
            }
        }
        // System.out.println(str);
    }
    
    private void writeXML(String tag, String str) {
        str = "<" + tag + "> " + str + " </" + tag + ">";
        write(str);
    }
    
    private void writeTagOpen(String tag) {
        write("<" + tag + ">");
        ++indent;
    }
    
    private void writeTagClose(String tag) {
        --indent;
        write("</" + tag + ">");
    }
    
    private void writeIdentifierUsed(String identifier) {
        String str = "<" + JackTokenizer.C_TYPE_IDENTIFIER + " kind=\"" + 
                String.valueOf(st.kindOf(identifier)) + "\" type=\"" + 
                String.valueOf(st.typeOf(identifier)) + "\" index=\"" + 
                String.valueOf(st.indexOf(identifier)) + "\" used=\"True\"> " + 
                identifier + 
                " </" + JackTokenizer.C_TYPE_IDENTIFIER + ">";
        write(str);
    }
    
    private void writeIdentifierNamed(String identifier, String category) {
        String str = "<" + JackTokenizer.C_TYPE_IDENTIFIER + " category=\"" + 
                category + "\" named=\"True\"> " + 
                identifier + 
                " </" + JackTokenizer.C_TYPE_IDENTIFIER + ">";
        write(str);
    }

    private void writeIdentifierDefined(String identifier, String type, int kind) {
        st.define(identifier, type, kind);
        String str = "<" + JackTokenizer.C_TYPE_IDENTIFIER + " kind=\"" + 
                String.valueOf(st.kindOf(identifier)) + "\" type=\"" + 
                String.valueOf(st.typeOf(identifier)) + "\" index=\"" + 
                String.valueOf(st.indexOf(identifier)) + "\" defined=\"True\"> " + 
                identifier + 
                " </" + JackTokenizer.C_TYPE_IDENTIFIER + ">";
        write(str);
    }
    
    private void require(char symbol) {
        if (!query(symbol)) {
            err();        
        }
    }
    
    private void require(String tokenType) {
        if (!query(tokenType)) {
            err();        
        }
    }
    
    private void require(String tokenType, String keyWord) {
        if (!query(tokenType, keyWord)) {
            err();        
        }
    }
    
    private boolean query(char symbol) {
        if (tz.tokenType().equals(tz.C_TYPE_SYMBOL) & tz.symbolChar() == symbol) {
            return true;
        } else {
            return false;
        }
    }

    private boolean query(String tokenType) {
        if (tz.tokenType().equals(tokenType)) {
            return true;
        } else {
            return false;
        }
    }
    
    private boolean query(String tokenType, String keyWord) {
        if (tz.tokenType().equals(tokenType) & tz.keyWord().equals(keyWord)) {
            return true;
        } else {
            return false;
        }
    }

    private void err() {
        write("Error at token number: " + String.valueOf(tz.getTokenNumber()));
//        throw new RuntimeException("Error at token number: " + String.valueOf(tz.getTokenNumber()));
    }
    
    private String getLabelIfTrue(int index) {
        return "IF_TRUE" + index;
    }

    private String getLabelIfFalse(int index) {
        return "IF_FALSE" + index;
    }
    
    private String getLabelIfEnd(int index) {
        return "IF_END" + index;
    }

    private String getLabelWhileExp(int index) {
        return "WHILE_EXP" + index;
    }
    
    private String getLabelWhileEnd(int index) {
        return "WHILE_END" + index;
    }
}
