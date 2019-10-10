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

    void addSymbol(String name, String classification, String type, boolean isArray, int size) {
        SymbolData newDataNode = new SymbolData(classification, type, isArray, size);
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
