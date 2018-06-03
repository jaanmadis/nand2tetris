import java.io.File;
import java.util.ArrayList;

public class VMTranslator {
    public static void main(String[] args) {
        if (args.length == 1) {
            File input = new File(args[0]);
            ArrayList inFiles = new ArrayList<>();
            String outFile = "";
            Boolean needsBootstrap = false;
            if (input.isFile()) {
                inFiles.add(args[0]);
                outFile = args[0].replace("vm", "asm");
            } else if (input.isDirectory()) {
                outFile = args[0] + "\\" + input.getName() + ".asm";
                needsBootstrap = true;
                File[] list = input.listFiles();
                for (int i = 0; i < list.length; ++i) {
                    if (list[i].isFile()) {
                        if (list[i].getName().indexOf(".vm") != -1) {
                            inFiles.add(list[i]);
                        }
                    }
                }
            }
            outFile = outFile.replace("\\", "//");
            CodeWriter cw = new CodeWriter(outFile);
            for (int j = 0; j < inFiles.size(); ++j) {
                Parser parser = new Parser(inFiles.get(j).toString());
                if (needsBootstrap) {
                    cw.writeBootstrap();
                }
                cw.next();
                while (parser.hasMoreCommands()) {
                    parser.advance();
                    String commandType = parser.commandType();
                    if (commandType.equals(Parser.C_ARITHMETIC)) {
                        cw.writeArithmetic(parser.arg1());
                    } else if ((commandType.equals(Parser.C_PUSH)) || 
                            (commandType.equals(Parser.C_POP))) {
                        cw.writePushPop(commandType, parser.arg1(), parser.arg2());
                    } else if (commandType.equals(Parser.C_LABEL)) {
                        cw.writeLabel(parser.arg1());
                    } else if (commandType.equals(Parser.C_GOTO)) {
                        cw.writeGoto(parser.arg1());
                    } else if (commandType.equals(Parser.C_IF)) {
                        cw.writeIf(parser.arg1());
                    } else if (commandType.equals(Parser.C_FUNCTION)) {
                        cw.writeFunction(parser.arg1(), parser.arg2());
                    } else if (commandType.equals(Parser.C_RETURN)) {
                        cw.writeReturn();
                    } else if (commandType.equals(Parser.C_CALL)) {
                        cw.writeCall(parser.arg1(), parser.arg2());
                    }
                }
            }
            cw.done();
        }
    }
}
