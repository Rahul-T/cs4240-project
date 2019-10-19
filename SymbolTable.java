public class SymbolTable {
    private ScopeNode head;

    // Scope that is currently under management.
    private ScopeNode current;

    // How many scopes are currently under managament in the ST.
    private int scopeAmount = 1;

    public SymbolTable() {
        this.head = new ScopeNode("scope " + scopeAmount++, null);
        this.current = this.head;

        // add prints function
        String[] s = new String[1];
        s[0] = "string";
        this.addFunction("prints", "int", s);

        // add printi function
        String[] i = new String[1];
        i[0] = "int";
        this.addFunction("prints", "int", i);

        // add flush
        this.addFunction("flush", "int", null);

        // add getChar
        this.addFunction("getchar", "string", null);
    }

    void openScope() {
        ScopeNode newNode = new ScopeNode("scope" + scopeAmount++, current);
        current.addChildren(newNode);
        current = newNode;
    }

    void closeScope() {
        current = current.getParent();
    }

    /**
     * @return True on successful add, false if symbol already declared in scope.
     */
    boolean addSymbol(String name, String classification, String type, 
        boolean isArray, int size, String[] paramList) {
        SymbolData newDataNode = new SymbolData(classification, type, isArray, 
            size, paramList);
        return current.addEntry(name, newDataNode);
    }

    /**
     * @return True on successful add, false if symbol already declared in scope.
     */
    boolean addArray(String name, String classification, String type, int size) {
        SymbolData newDataNode = new SymbolData(classification, type, size);
        return current.addEntry(name, newDataNode);
    }

    /**
     * @return True on successful add, false if symbol already declared in scope.
     */
    boolean addVariable(String name, String type) {
        SymbolData newDataNode = new SymbolData("var", type);
        return current.addEntry(name, newDataNode);
    }

    /**
     * @return True on successful add, false if symbol already declared in scope.
     */
    boolean addFunction(String name, String retType, String[] paramList) {
        SymbolData newDataNode = new SymbolData("func", retType, paramList);
        return current.addEntry(name, newDataNode);
    }

    /**
     * @return True on successful add, false if symbol already declared in scope.
     */
    boolean addType(String name, String type) {
        SymbolData newDataNode = new SymbolData("type", type);
        return current.addEntry(name, newDataNode);
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

    public boolean containsSymbol(String entryName) {
        return current.containsDuplicateSymbol(entryName);
    }

    /**
     * Looks up an entry in the symbol table
     * @param entryName Name of symbol to lookup
     * @return Associated SymbolData for the lookup symbol. Null if symbol is 
     *         not found.
     */
    public SymbolData lookupSymbol(String entryName) {
        return current.lookupEntry(entryName);
    }

    public String getType(String entryName) {
        return current.lookupEntry(entryName).getType();
    }

    public boolean isArray(String entryName) {
        return current.lookupEntry(entryName).isArray();
    }
}
