import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScopeNode {
    private String scopeName;

    private ArrayList<ScopeNode> children;

    private ScopeNode parent;

    private Map<String, SymbolData> scopeEntries;

    ScopeNode(String scopeName, ScopeNode parent) {
        this.scopeName = scopeName;
        this.parent = parent;
        this.children = new ArrayList<ScopeNode>();
        this.scopeEntries = new HashMap<String, SymbolData>();
    }

    public String getScopeName() {
        return scopeName;
    }

    public ScopeNode getParent() {
        return parent;
    }

    public ArrayList<ScopeNode> getChildren() {
        return children;
    }

    public Map<String, SymbolData> getScopeEntries() {
        return scopeEntries;
    }

    public void addChildren(ScopeNode newNode) {
        children.add(newNode);
    }

    public boolean addEntry(String entryName, SymbolData entryData) {
        if (containsDuplicateSymbol(entryName)) {
            return false;
        } else {
            scopeEntries.put(entryName, entryData);
            return true;
        }
    }

    public void updateEntry(String entryName, SymbolData entryData) {
        scopeEntries.put(entryName, entryData);
    }

    /**
     * Lookup a value in the tree of ScopeNodes, starting with the current scope
     * and looking up scopes until the desired item is found.
     * @param entryName String name of the desired entry in the Symbol Table.
     * @return SymbolData corresponding to the desired entry if found, null
     *         otherwise.
     */
    public SymbolData lookupEntry(String entryName) {
        ScopeNode current = this;
        SymbolData ret;
        while (current != null) {
            if ((ret = current.scopeEntries.get(entryName)) != null) {
                return ret;
            } 
            current = current.parent;
        }
        return null;
    }

    /**
     * Checks the current scope to see if it contains the symbol.
     * @param entryName Name to check for in symbol table
     * @return True if there's already an entry in the current scope for 
     *         entryName, false otherwise.
     */
    public boolean containsDuplicateSymbol(String entryName) {
        return this.scopeEntries.containsKey(entryName);
    }

    @Override
    public String toString() {
        return scopeName + "\n" + scopeEntries.toString();
    }

}
