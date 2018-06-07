public class SymbolTableData {
    
    private final String name;
    private final String type;
    private final int kind;
    private final int index;

    public SymbolTableData(String aname, String atype, int akind, int aindex) {
        name = aname;
        type = atype;
        kind = akind;
        index = aindex;
    }
    
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getKind() {
        return kind;
    }

    public int getIndex() {
        return index;
    }
}
