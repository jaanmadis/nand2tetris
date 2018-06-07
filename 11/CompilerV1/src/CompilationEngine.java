import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CompilationEngine {

    private BufferedWriter bw = null;
    private final JackTokenizer tz;
    private final VMWriter vm;
    private final SymbolTable st;
    private int indent = 0;
    
    private String currentClassName = "";
    private String currentSubroutineName = "";
    private String currentSubroutineType = "";
    
    private int labelIndexIf = 0;
    private int labelIndexWhile = 0;

    public CompilationEngine(String inFile, String outFile) {
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
        writeIdentifierClassName(tz.identifier());
        currentClassName = tz.identifier();
        tz.advance();

        require(JackTokenizer.C_SYMBOL_CURLY_OPEN);
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        tz.advance();
        
        boolean done;
                
        done = false;
        while (!done) {
            if (query(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_STATIC) | 
                    query(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_FIELD)) {
                compileClassVarDec();
            } else {
                done = true;
            }
        }
        
        done = false;
        while (!done) {
            if (query(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_CONSTRUCTOR) | 
                    query(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_FUNCTION) | 
                    query(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_METHOD)) {
                compileSubroutineDec();
            } else {
                done = true;
            }
        }

        require(JackTokenizer.C_SYMBOL_CURLY_CLOSE);
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        tz.advance();
        if (tz.hasMoreTokens()) {
            err();
        }
        
        writeTagClose(JackTokenizer.C_KEYWORD_CLASS);
    }

    public void compileClassVarDec() {
        writeTagOpen("classVarDec");

        // ('staticJackTokenizer.C_SYMBOL_ORfield')                
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
            writeIdentifierClassName(tz.identifier());
            type = tz.identifier();
        } else {
            err();
        }
        tz.advance();

        // name
        boolean done = false;
        while (!done) {
            if (query(JackTokenizer.C_TYPE_IDENTIFIER)) {
                writeIdentifierDefined(tz.identifier(), type, kind);
                tz.advance();
                
                if (query(JackTokenizer.C_SYMBOL_COMMA)) {
                    writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
                    tz.advance();
                } else {
                    done = true;
                }
            }
        }

        require(JackTokenizer.C_SYMBOL_SEMICOLON);
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        tz.advance();

        writeTagClose("classVarDec");
    }

    public void compileSubroutineDec() {
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
            writeIdentifierClassName(tz.identifier());
        } else {
            err();
        }
        tz.advance();

        require(JackTokenizer.C_TYPE_IDENTIFIER);
        // subroutineName
        writeIdentifierSubName(tz.identifier());
        currentSubroutineName = tz.identifier();
        tz.advance();

        require(JackTokenizer.C_SYMBOL_ROUND_OPEN);
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        tz.advance();

        if (currentSubroutineType.equals(JackTokenizer.C_KEYWORD_METHOD)) {
            st.define(currentClassName, currentClassName, SymbolTable.C_KIND_ARG);
        }                
        
        compileParameterList();
        
        require(JackTokenizer.C_SYMBOL_ROUND_CLOSE);
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        tz.advance();

        compileSubroutineBody();
        
        writeTagClose("subroutineDec");
    }

    public void compileParameterList() {
        writeTagOpen("parameterList");
        boolean done = false;
        while (!done) {
            if (query(JackTokenizer.C_TYPE_KEYWORD)) {
                writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
                String type = tz.keyWord();
                tz.advance();

                writeIdentifierDefined(tz.identifier(), type, SymbolTable.C_KIND_ARG);
                tz.advance();

                if (query(JackTokenizer.C_SYMBOL_COMMA)) {
                    writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
                    tz.advance();
                }
            } else if (query(JackTokenizer.C_TYPE_IDENTIFIER)) {
                writeIdentifierClassName(tz.identifier());
                String type = tz.identifier();
                tz.advance();

                writeIdentifierDefined(tz.identifier(), type, SymbolTable.C_KIND_ARG);
                tz.advance();

                if (query(JackTokenizer.C_SYMBOL_COMMA)) {
                    writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
                    tz.advance();
                }
            } else {
                done = true;
            }
        }
        writeTagClose("parameterList");
    }

    public void compileSubroutineBody() {
        writeTagOpen("subroutineBody");

        require(JackTokenizer.C_SYMBOL_CURLY_OPEN);
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        tz.advance();
        
        boolean done = false;
        while (!done) {
            if (query(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_VAR)) {
                compileVarDec();
            } else {
                done = true;
            }
        }

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

        require(JackTokenizer.C_SYMBOL_CURLY_CLOSE);
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        tz.advance();

        writeTagClose("subroutineBody");
    }

    public void compileVarDec() {
        writeTagOpen("varDec");

        writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
        tz.advance();

        String type = "";
        if (query(JackTokenizer.C_TYPE_KEYWORD)) {
            writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
            type = tz.keyWord();
        } else if (query(JackTokenizer.C_TYPE_IDENTIFIER)) {
            writeIdentifierClassName(tz.identifier());
            type = tz.identifier();
        } else {
            err();
        }
        tz.advance();

        boolean done = false;
        while (!done) {
            if (query(JackTokenizer.C_TYPE_IDENTIFIER)) {
                writeIdentifierDefined(tz.identifier(), type, SymbolTable.C_KIND_LOCAL);
                tz.advance();
            
                if (query(JackTokenizer.C_SYMBOL_COMMA)) {
                    writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
                    tz.advance();
                }
            } else {
                done = true;
            }
        }

        require(JackTokenizer.C_SYMBOL_SEMICOLON);
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        tz.advance();

        writeTagClose("varDec");
    }

    public void compileStatements() {
        writeTagOpen("statements");
        
        boolean done = false;
        while (!done) {
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
        tz.advance();
        
        boolean hasArray = false;
        if (query(JackTokenizer.C_SYMBOL_SQUARE_OPEN)) {
            hasArray = true;
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
            tz.advance();
            
            compileExpression();

            require(JackTokenizer.C_SYMBOL_SQUARE_CLOSE);
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
            tz.advance();
            
            vm.writePush(st.getSegment(name), st.indexOf(name));
            vm.writeArithmetic(VMWriter.C_ADD);
        }
        
        require(JackTokenizer.C_SYMBOL_EQ);
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        tz.advance();
        
        compileExpression();
        
        require(JackTokenizer.C_SYMBOL_SEMICOLON);
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        tz.advance();
        
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

        require(JackTokenizer.C_SYMBOL_ROUND_OPEN);
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        tz.advance();
        
        compileExpression();
        
        require(JackTokenizer.C_SYMBOL_ROUND_CLOSE);
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        tz.advance();
        
        int index = labelIndexIf;
        ++labelIndexIf;

        vm.writeIf(getLabelIfTrue(index));
        vm.writeGoto(getLabelIfFalse(index));
        vm.writeLabel(getLabelIfTrue(index));
        
        require(JackTokenizer.C_SYMBOL_CURLY_OPEN);
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        tz.advance();
        
        compileStatements();

        require(JackTokenizer.C_SYMBOL_CURLY_CLOSE);
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        tz.advance();

        boolean hasElse = false;
        if (query(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_ELSE)) {
            hasElse = true;

            writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
            tz.advance();

            require(JackTokenizer.C_SYMBOL_CURLY_OPEN);
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
            tz.advance();
           
            vm.writeGoto(getLabelIfEnd(index));
            vm.writeLabel(getLabelIfFalse(index));
            
            compileStatements();

            vm.writeLabel(getLabelIfEnd(index));
        
            require(JackTokenizer.C_SYMBOL_CURLY_CLOSE);
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
            tz.advance();
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
        tz.advance();
        
        int index = labelIndexWhile;
        ++labelIndexWhile;

        vm.writeLabel(getLabelWhileExp(index));
        
        require(JackTokenizer.C_SYMBOL_ROUND_OPEN);
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        tz.advance();
        
        compileExpression();

        vm.writeArithmetic(VMWriter.C_NOT);
        vm.writeIf(getLabelWhileEnd(index));
        
        require(JackTokenizer.C_SYMBOL_ROUND_CLOSE);
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        tz.advance();

        require(JackTokenizer.C_SYMBOL_CURLY_OPEN);
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        tz.advance();
        
        compileStatements();

        require(JackTokenizer.C_SYMBOL_CURLY_CLOSE);
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        tz.advance();
        
        vm.writeGoto(getLabelWhileExp(index));
        vm.writeLabel(getLabelWhileEnd(index));

        writeTagClose("whileStatement");
    }
    
    public void compileDo() {
        writeTagOpen("doStatement");
        
        require(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_DO);
        writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
        tz.advance();
        
        compileSubroutineCall();

        require(JackTokenizer.C_SYMBOL_SEMICOLON);
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        tz.advance();
        
        vm.writePop(VMWriter.C_SEGMENT_TEMP, 0);
        
        writeTagClose("doStatement");
    }
    
    public void compileReturn() {
        writeTagOpen("returnStatement");
        
        require(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_RETURN);
        writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
        tz.advance();
        
        boolean hasExpression = !query(JackTokenizer.C_SYMBOL_SEMICOLON);
        if (hasExpression) {
            compileExpression();
        }
        require(JackTokenizer.C_SYMBOL_SEMICOLON);
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        tz.advance();

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
            
            if (query(JackTokenizer.C_SYMBOL_PLUS) | query(JackTokenizer.C_SYMBOL_MINUS) | query(JackTokenizer.C_SYMBOL_ASTERISK) | query(JackTokenizer.C_SYMBOL_SLASH) | query(JackTokenizer.C_SYMBOL_AND) | 
                    query(JackTokenizer.C_SYMBOL_OR) | query(JackTokenizer.C_SYMBOL_LT) | query(JackTokenizer.C_SYMBOL_GT) | query(JackTokenizer.C_SYMBOL_EQ)) {
                writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
                operators.add(String.valueOf(tz.symbolChar()));
                tz.advance();
            } else {
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
        
        // integer constant
        if (query(JackTokenizer.C_TYPE_INT_CONST)) {
            writeXML(JackTokenizer.C_TYPE_INT_CONST, String.valueOf(tz.intVal()));
            vm.writePush(VMWriter.C_SEGMENT_CONST, tz.intVal());
            tz.advance();
        // string constant
        } else if (query(JackTokenizer.C_TYPE_STRING_CONST)) {
            writeXML(JackTokenizer.C_TYPE_STRING_CONST, tz.stringVal());
            vm.writePush(VMWriter.C_SEGMENT_CONST, tz.stringVal().length());
            vm.writeCall("String.new", 1);
            for (int strIdx = 0; strIdx < tz.stringVal().length(); ++strIdx) {
                vm.writePush(VMWriter.C_SEGMENT_CONST, tz.stringVal().charAt(strIdx));
                vm.writeCall("String.appendChar", 2);
            }
            tz.advance();
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
            tz.advance();
        // varname[ expression ]
        } else if (query(JackTokenizer.C_TYPE_IDENTIFIER)) {
            String identifier = tz.identifier();
            if (queryAhead(JackTokenizer.C_SYMBOL_SQUARE_OPEN)) {
                // 2
                writeIdentifierUsed(identifier);
                tz.advance();
                
                writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
                tz.advance();

                compileExpression();

                require(JackTokenizer.C_SYMBOL_SQUARE_CLOSE);
                writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
                tz.advance();
                
                vm.writePush(st.getSegment(identifier), st.indexOf(identifier));
                vm.writeArithmetic("add");
                vm.writePop(VMWriter.C_SEGMENT_POINTER, 1);
                vm.writePush(VMWriter.C_SEGMENT_THAT, 0);
            // varname.call() / Class.call() / call()
            } else if (queryAhead(JackTokenizer.C_SYMBOL_PERIOD) | queryAhead(JackTokenizer.C_SYMBOL_ROUND_OPEN)) {
                compileSubroutineCall();
            // varname
            } else {
                writeIdentifierUsed(identifier);
                tz.advance();
                vm.writePush(st.getSegment(identifier), st.indexOf(identifier));
            }
        // brackets ( )
        } else if (query(JackTokenizer.C_SYMBOL_ROUND_OPEN)) {
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
            tz.advance();
            
            compileExpression();

            require(JackTokenizer.C_SYMBOL_ROUND_CLOSE);
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
            tz.advance();
        // unary
        } else if (query(JackTokenizer.C_SYMBOL_MINUS)) {
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
            tz.advance();
            compileTerm();
            vm.writeArithmetic(VMWriter.C_NEG);
        } else if (query(JackTokenizer.C_SYMBOL_TILDE)) {
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
            tz.advance();
            compileTerm();
            vm.writeArithmetic(VMWriter.C_NOT);
        }
        
        writeTagClose("term");
    }
    
    private void compileSubroutineCall() {
        require(JackTokenizer.C_TYPE_IDENTIFIER);
        
        String firstIdentifier = tz.identifier();
        String callName;
        boolean isMethodCall;
        
        // varName.subName();
        if (st.exists(firstIdentifier)) {
            writeIdentifierUsed(firstIdentifier);
            tz.advance();
    
            require(JackTokenizer.C_SYMBOL_PERIOD);
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
            tz.advance();

            require(JackTokenizer.C_TYPE_IDENTIFIER);
            writeIdentifierSubName(tz.identifier());

            callName = st.typeOf(firstIdentifier) + "." + tz.identifier();
            isMethodCall = true;
            vm.writePush(st.getSegment(firstIdentifier), st.indexOf(firstIdentifier));
            
            tz.advance();
        } else {
            // ClassName.subName();
            if (queryAhead(JackTokenizer.C_SYMBOL_PERIOD)) {
                writeIdentifierClassName(firstIdentifier);
                tz.advance();
                
                writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
                tz.advance();

                require(JackTokenizer.C_TYPE_IDENTIFIER);
                writeIdentifierSubName(tz.identifier());

                callName = firstIdentifier + "." + tz.identifier();
                isMethodCall = false;

                tz.advance();
            }
            // subName();
            else {
                writeIdentifierSubName(firstIdentifier);
                tz.advance();
                
                callName = currentClassName + "." + firstIdentifier;
                isMethodCall = true;
                vm.writePush(VMWriter.C_SEGMENT_POINTER, 0);
            }
        }
        
        require(JackTokenizer.C_SYMBOL_ROUND_OPEN);
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        tz.advance();

        int argCount = compileExpressionList();
        if (isMethodCall) {
            ++argCount;
        }

        require(JackTokenizer.C_SYMBOL_ROUND_CLOSE);
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        tz.advance();
        
        vm.writeCall(callName, argCount);
    }
    
    public int compileExpressionList() {
        int count = 0;
        writeTagOpen("expressionList");
        
        boolean done = false;
        while (!done) {
            if (!query(JackTokenizer.C_SYMBOL_ROUND_CLOSE)) {
                
                compileExpression();
                ++count;

                if (query(JackTokenizer.C_SYMBOL_COMMA)) {
                    writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
                    tz.advance();
                }
            } else {
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
    
    private void writeIdentifierClassName(String identifier) {
        String str = "<" + JackTokenizer.C_TYPE_IDENTIFIER + 
                " category=\"ClassName\"> " + 
                identifier + " </" + JackTokenizer.C_TYPE_IDENTIFIER + ">";
        write(str);
    }

    private void writeIdentifierSubName(String identifier) {
        String str = "<" + JackTokenizer.C_TYPE_IDENTIFIER + 
                " category=\"SubName\"> " + 
                identifier + " </" + JackTokenizer.C_TYPE_IDENTIFIER + ">";
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
    
    private void writeIdentifierUsed(String identifier) {
        String str = "<" + JackTokenizer.C_TYPE_IDENTIFIER + " kind=\"" + 
                String.valueOf(st.kindOf(identifier)) + "\" type=\"" + 
                String.valueOf(st.typeOf(identifier)) + "\" index=\"" + 
                String.valueOf(st.indexOf(identifier)) + "\" used=\"True\"> " + 
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
        return tz.tokenType().equals(JackTokenizer.C_TYPE_SYMBOL) & tz.symbolChar() == symbol;
    }

    private boolean queryAhead(char symbol) {
        tz.advance();
        boolean result = tz.tokenType().equals(JackTokenizer.C_TYPE_SYMBOL) & tz.symbolChar() == symbol;
        tz.backtrack();
        return result;
    }

    private boolean query(String tokenType) {
        return tz.tokenType().equals(tokenType);
    }
    
    private boolean query(String tokenType, String keyWord) {
        return tz.tokenType().equals(tokenType) & tz.keyWord().equals(keyWord);
    }

    private void err() {
        write("Error at token number: " + String.valueOf(tz.getTokenNumber()));
        throw new RuntimeException("Error at token number: " + String.valueOf(tz.getTokenNumber()));
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
