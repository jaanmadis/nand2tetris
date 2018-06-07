import java.io.File;
import java.util.ArrayList;

public class JackCompiler {
    public static void main(String[] args) {
        if (args.length == 1) {
            File input = new File(args[0]);
            ArrayList<String> inFiles = new ArrayList<String>();
            if (input.isFile()) {
                inFiles.add(args[0]);
            } else if (input.isDirectory()) {
                File[] list = input.listFiles();
                for (int i = 0; i < list.length; ++i) {
                    if (list[i].isFile()) {
                        if (list[i].getName().contains(".jack")) {
                            inFiles.add(input.getAbsolutePath() + File.separator + list[i].getName());
                        }
                    }
                }
            }
            for(int i = 0; i < inFiles.size(); ++i) {
                CompilationEngine ce = new CompilationEngine(inFiles.get(i), inFiles.get(i).replace("jack", "xml"));
                ce.compileClass();
                ce.done();
            }
        }
    }
}
