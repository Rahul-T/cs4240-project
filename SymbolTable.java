public class SymbolTable {
    private ScopeNode head;

    // Scope that is currently under management.
    private ScopeNode current;

    // How many scopes are currently under managament in the ST.
    private int scopeAmount = 1;

    public SymbolTable() {
        this.head = new ScopeNode("scope" + scopeAmount++, null);
        this.current = this.head;
    }

    void openScope() {
        ScopeNode newNode = new ScopeNode("scope" + scopeAmount++, current);
        current.addChildren(newNode);
        current = newNode;
    }

    void closeScope() {
        current = current.getParent();
    }

    void addSymbol(String name, String classification, String type, 
        boolean isArray, int size, String[] paramList) {
        SymbolData newDataNode = new SymbolData(classification, type, isArray, 
            size, paramList);
        current.addEntry(name, newDataNode);
    }

    void addArray(String name, String classification, String type, int size) {
        SymbolData newDataNode = new SymbolData(classification, type, size);
        current.addEntry(name, newDataNode);
    }

    void addVariable(String name, String type) {
        SymbolData newDataNode = new SymbolData("var", type);
        current.addEntry(name, newDataNode);
    }

    void addFunction(String name, String retType, String[] paramList) {
        SymbolData newDataNode = new SymbolData("func", retType, paramList);
        current.addEntry(name, newDataNode);
    }

    void addType(String name, String type) {
        SymbolData newDataNode = new SymbolData("type", type);
        current.addEntry(name, newDataNode);
    }

    @Override
    public String toString() {
        String retString = head.toString() + "\n";
        for (ScopeNode curr : head.getChildren()) {
            retString = toStringHelper(curr, retString);
        }
        return retString;
    }

    private String toStringHelper(ScopeNode parent, String retString) {
        retString = retString + "\n" + parent.toString();
        for (ScopeNode curr : parent.getChildren()) {
            retString = retString + "\n" + curr.toString() + "\n";
        }
        return retString;
    }
}
