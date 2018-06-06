import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CompilationEngine {

    private BufferedWriter bw = null;
    private JackTokenizer tz;
    private int indent = 0;

    public CompilationEngine(String inFile, String outFile) {
        outFile = outFile.replace("\\", "//");

        try {
            bw = new BufferedWriter(new FileWriter(outFile));
        } catch (IOException x) {
            x.printStackTrace();
        }

        tz = new JackTokenizer(inFile);
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
    
    public void compileClass() {
        tz.advance();
        require(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_CLASS);
        writeTagOpen(JackTokenizer.C_KEYWORD_CLASS);

        writeXML(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_CLASS);
        
        tz.advance();
        require(JackTokenizer.C_TYPE_IDENTIFIER);
        writeXML(JackTokenizer.C_TYPE_IDENTIFIER, tz.identifier());
        
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
                
                writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
                
                tz.advance();
                if (query(JackTokenizer.C_TYPE_KEYWORD)) {
                    writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
                } else if (query(JackTokenizer.C_TYPE_IDENTIFIER)) {
                    writeXML(JackTokenizer.C_TYPE_IDENTIFIER, tz.identifier());
                } else {
                    err();
                }

                boolean doneInner = false;
                while (!doneInner) {
                    tz.advance();
                    require(JackTokenizer.C_TYPE_IDENTIFIER);
                    writeXML(JackTokenizer.C_TYPE_IDENTIFIER, tz.identifier());

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
                writeTagOpen("subroutineDec");
                writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());

                tz.advance();
                if (query(JackTokenizer.C_TYPE_KEYWORD)) {
                    writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
                } else if (query(JackTokenizer.C_TYPE_IDENTIFIER)) {
                    writeXML(JackTokenizer.C_TYPE_IDENTIFIER, tz.identifier());
                } else {
                    err();
                }

                tz.advance();
                require(JackTokenizer.C_TYPE_IDENTIFIER);
                writeXML(JackTokenizer.C_TYPE_IDENTIFIER, tz.identifier());

                tz.advance();
                require('(');
                writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());

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
                
                tz.advance();
                writeXML(JackTokenizer.C_TYPE_IDENTIFIER, tz.identifier());
                
                tz.advance();
                if (query(',')) {
                    writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
                } else {
                    tz.unadvance();
                }
            } else if (query(JackTokenizer.C_TYPE_IDENTIFIER)) {
                writeXML(JackTokenizer.C_TYPE_IDENTIFIER, tz.identifier());
                
                tz.advance();
                writeXML(JackTokenizer.C_TYPE_IDENTIFIER, tz.identifier());
                
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
                if (query(JackTokenizer.C_TYPE_KEYWORD)) {
                    writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
                } else if (query(JackTokenizer.C_TYPE_IDENTIFIER)) {
                    writeXML(JackTokenizer.C_TYPE_IDENTIFIER, tz.identifier());
                } else {
                    err();
                }

                boolean doneInner = false;
                while (!doneInner) {
                    tz.advance();
                    require(JackTokenizer.C_TYPE_IDENTIFIER);
                    writeXML(JackTokenizer.C_TYPE_IDENTIFIER, tz.identifier());

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
        writeXML(JackTokenizer.C_TYPE_IDENTIFIER, tz.identifier());
        
        tz.advance();
        if (query('[')) {
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
            
            compileExpression();
    
            tz.advance();
            require(']');
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
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
        
        tz.advance();
        require('{');
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        
        compileStatements();

        tz.advance();
        require('}');
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        
        tz.advance();
        if (query(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_ELSE)) {
            writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());

            tz.advance();
            require('{');
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());

            compileStatements();

            tz.advance();
            require('}');
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        } else {
            tz.unadvance();
        }
        
        writeTagClose("ifStatement");
    }

    public void compileWhile() {
        writeTagOpen("whileStatement");
        
        require(JackTokenizer.C_TYPE_KEYWORD, JackTokenizer.C_KEYWORD_WHILE);
        writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
        
        tz.advance();
        require('(');
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        
        compileExpression();

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
        
        writeTagClose("returnStatement");
    }

    public void compileExpression() {
        writeTagOpen("expression");
        
        boolean done = false;
        while (!done) {
            compileTerm();
            tz.advance();
            if (query('+') | query('-') | query('*') | query('/') | query('&') | 
                    query('|') | query('<') | query('>') | query('=')) {
                writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
            } else {
                tz.unadvance();
                done = true;
            }
        }

        writeTagClose("expression");
    }
    
    public void compileTerm() {
        writeTagOpen("term");
        
        tz.advance();
        if (query(JackTokenizer.C_TYPE_INT_CONST)) {
            writeXML(JackTokenizer.C_TYPE_INT_CONST, String.valueOf(tz.intVal()));
        } else if (query(JackTokenizer.C_TYPE_STRING_CONST)) {
            writeXML(JackTokenizer.C_TYPE_STRING_CONST, tz.stringVal());
        } else if (query(JackTokenizer.C_TYPE_KEYWORD)) {
            writeXML(JackTokenizer.C_TYPE_KEYWORD, tz.keyWord());
        } else if (query(JackTokenizer.C_TYPE_IDENTIFIER)) {
            writeXML(JackTokenizer.C_TYPE_IDENTIFIER, tz.keyWord());
            tz.advance();
            if (query('[')) {
                writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
                compileExpression();
                tz.advance();
                require(']');
                writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
            } else {
                tz.unadvance();
            }
            tz.advance();
            if (query('.')) {
                writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
                compileSubroutineCall();
            } else {
                tz.unadvance();
            }
        } else if (query('(')) {
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
            compileExpression();
            tz.advance();
            require(')');
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        } else if (query('-') | query('~')) {
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
            compileTerm();
        }
        
        writeTagClose("term");
    }
   
    private void compileSubroutineCall() {
        tz.advance();
        require(JackTokenizer.C_TYPE_IDENTIFIER);
        writeXML(JackTokenizer.C_TYPE_IDENTIFIER, tz.identifier());

        tz.advance();
        if (query('.')) {
            writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());

            tz.advance();
            require(JackTokenizer.C_TYPE_IDENTIFIER);
            writeXML(JackTokenizer.C_TYPE_IDENTIFIER, tz.identifier());
        } else {
            tz.unadvance();
        }

        tz.advance();
        require('(');
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
        
        compileExpressionList();        

        tz.advance();
        require(')');
        writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
    }
    
    public void compileExpressionList() {
        writeTagOpen("expressionList");
        
        tz.advance();
        boolean done = query(')');
        tz.unadvance();
        
        while (!done) {
            compileExpression();
            tz.advance();
            if (query(',')) {
                writeXML(JackTokenizer.C_TYPE_SYMBOL, tz.symbolStr());
            } else {
                tz.unadvance();
                done = true;
            }
        }
        
        writeTagClose("expressionList");
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
}
