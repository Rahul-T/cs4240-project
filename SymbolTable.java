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
        this.addFunction("printi", "int", i);

        // add flush
        this.addFunction("flush", "int", null);

        // add getChar
        this.addFunction("getchar", "string", null);
    }

    @Override
    public String toString() {
        return toStringHelper(this.head, 0, "");
    }

    private String toStringHelper(ScopeNode current, int indentNo, String retString) {
        String indents = "";
        for (int i = 0; i < indentNo; i++) {
            indents += "\t";
        }

        SymbolData entry;
        retString += indents + current.getScopeName() + ":\n";
        for (String key : current.getScopeEntries().keySet()) {
            entry = current.lookupEntry(key);
            retString += String.format("\t%s%s, %s, %s\n", indents, key, 
                entry.getClassification(), entry.getType());
        }
        for (ScopeNode node : current.getChildren()) {
            retString = toStringHelper(node, indentNo + 1, retString);
        }
        return retString;
    }

    void openScope() {
        ScopeNode newNode = new ScopeNode("scope " + scopeAmount++, current);
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

    public boolean containsSymbol(String entryName) {
        // if lookup symbol returns null, it's not in the symbol table.
        SymbolData sd = current.lookupEntry(entryName);
        return (sd != null);
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
        SymbolData lookupEntry = current.lookupEntry(entryName);
        if(lookupEntry == null) {
            return "";
        }
        return lookupEntry.getType();
    }

    public boolean isArray(String entryName) {
        SymbolData sd = current.lookupEntry(entryName);
        if(sd == null) {
            return false;
        }
        return current.lookupEntry(entryName).isArray();
    }
}
