public class VMTranslator {
    public static void main(String[] args) {
        if (args.length == 1) {
            Parser parser = new Parser(args[0]);
            CodeWriter cw = new CodeWriter(args[0].replace("vm", "asm"));
            while (parser.hasMoreCommands()) {
                parser.advance();
                String commandType = parser.commandType();
                if (commandType.equals(Parser.C_ARITHMETIC)) {
                    cw.writeArithmetic(parser.arg1());
                } else if ((commandType.equals(Parser.C_PUSH)) || 
                        (commandType.equals(Parser.C_POP))) {
                    cw.writePushPop(commandType, parser.arg1(), parser.arg2());
                }
            }
            cw.done();
        }
    }
}
