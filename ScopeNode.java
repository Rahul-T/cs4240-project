import java.util.ArrayList;
import java.util.Map;

public class ScopeNode {
    private String scopeName;

    private ArrayList<ScopeNode> children;

    private ScopeNode parent;

    private Map<String, SymbolData> scopeEntries;

    ScopeNode(String scopeName, ScopeNode parent) {
        this.scopeName = scopeName;
        this.parent = parent;
    }

    public String getScopeName() {
        return scopeName;
    }

    public ScopeNode getParent() {
        return parent;
    }

    public Map<String, SymbolData> getScopeEntries() {
        return scopeEntries;
    }

    public void addChildren(ScopeNode newNode) {
        children.add(newNode);
    }

    public void addEntry(String entryName, SymbolData entryData) {
        scopeEntries.put(entryName, entryData);
    }

}
