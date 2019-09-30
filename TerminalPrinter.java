import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ErrorNode;

public class TerminalPrinter extends tigerBaseListener {
    private Vocabulary vocab;
    private String tokenTupleString;
    private String tokenTypeList;

    public TerminalPrinter(TigerParserWrapper wrapper) {
        this.vocab = wrapper.getTigerVocabulary();
        this.tokenTupleString = "";
        this.tokenTypeList = "";
    }

    @Override
    public void visitTerminal(TerminalNode term) {
        this.tokenTypeList = this.tokenTypeList 
            + this.vocab.getSymbolicName(term.getSymbol().getType()) + " ";
        this.tokenTupleString = this.tokenTupleString + "< " 
            + this.vocab.getSymbolicName(term.getSymbol().getType()) + " , " +
            term.getText() + " >\n";
    }

    public String getTokenTypeList() {
        return this.tokenTypeList;
    }

    public String getTokenTupleString() {
        return this.tokenTupleString;
    }

}