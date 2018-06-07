import java.util.ArrayList;

public class SymbolTable {
    
    public static final int C_KIND_STATIC = 0;
    public static final int C_KIND_FIELD = 1;
    public static final int C_KIND_ARG = 2;
    public static final int C_KIND_LOCAL = 3;
    
    private int indexStatic;
    private int indexField;
    private int indexArg;
    private int indexLocal;
    
    private final ArrayList<SymbolTableData> subTable;
    private final ArrayList<SymbolTableData> classTable;
    
    public SymbolTable() {
        indexStatic = 0;
        indexField = 0;
        indexArg = 0;
        indexLocal = 0;
        subTable = new ArrayList<SymbolTableData>();
        classTable = new ArrayList<SymbolTableData>();
    }
    
    public void startSubroutine() {
        subTable.clear();
        indexArg = 0;
        indexLocal = 0;
    }
    
    public void define(String name, String type, int kind) {
        switch (kind) {
            case C_KIND_STATIC:
                classTable.add(new SymbolTableData(name, type, C_KIND_STATIC, indexStatic));
                ++indexStatic;
                break;
            case C_KIND_FIELD:
                classTable.add(new SymbolTableData(name, type, C_KIND_FIELD, indexField));
                ++indexField;
                break;
            case C_KIND_ARG:
                subTable.add(new SymbolTableData(name, type, C_KIND_ARG, indexArg));
                ++indexArg;
                break;
            case C_KIND_LOCAL:
                subTable.add(new SymbolTableData(name, type, C_KIND_LOCAL, indexLocal));
                ++indexLocal;
                break;
        }
    }
    
    public int varCount(int kind) {
        int result = 0;
        switch (kind) {
            case C_KIND_STATIC: 
                result = indexStatic;
                break;
            case C_KIND_FIELD:  
                result = indexField;
                break;
            case C_KIND_ARG:    
                result = indexArg;
                break;
            case C_KIND_LOCAL:  
                result = indexLocal;
                break;
        }
        return result;
    }
    
    public String getSegment(String name) {
        String result = "err";
        int kind = kindOf(name);
        switch (kind) {
            case C_KIND_STATIC:  
                result = VMWriter.C_SEGMENT_STATIC;
                break;
            case C_KIND_FIELD:  
                result = VMWriter.C_SEGMENT_THIS;
                break;
            case C_KIND_ARG:    
                result = VMWriter.C_SEGMENT_ARG;
                break;
            case C_KIND_LOCAL:  
                result = VMWriter.C_SEGMENT_LOCAL;
                break;
        }
        return result;
    }
    
    public int kindOf(String name) {
        SymbolTableData data = lookup(name);
        if (data != null) {
            return data.getKind();
        } else {
            return -1;
        }
    }
    
    public String typeOf(String name) {
        SymbolTableData data = lookup(name);
        if (data != null) {
            return data.getType();
        } else {
            return "";
        }
    }
    
    public int indexOf(String name) {
        SymbolTableData data = lookup(name);
        if (data != null) {
            return data.getIndex();
        } else {
            return -1;
        }
    }
    
    public boolean exists(String name) {
        return lookup(name) != null;
    }
    
    private SymbolTableData lookup(String name) {
        for (int i = 0; i < classTable.size(); ++i) {
            if (classTable.get(i).getName().endsWith(name)) {
                return classTable.get(i);
            }
        }
        for (int i = 0; i < subTable.size(); ++i) {
            if (subTable.get(i).getName().endsWith(name)) {
                return subTable.get(i);
            }
        }
        return null;
    }
}
