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

    void addArray(String name, String type, int size) {
        SymbolData newDataNode = new SymbolData("var", type, size);
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

    @Override
    public String toString() {
        String retString = "";
        ScopeNode curr = current;
        while (curr != null) {
            retString = curr.toString() + "\n" + retString;
        }
        curr = curr.getParent();
        return retString;
    }
}
