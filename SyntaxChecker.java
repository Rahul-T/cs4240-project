import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ErrorNode;

/**
 * Class that will check the syntax of our Tiger program. Creates a string with
 * tuples of form <type, name> and a string of all token types.
 */
public class SyntaxChecker extends tigerBaseListener {
    private Vocabulary vocab;
    private String tokenTupleString;
    private String tokenTypeList;

    public SyntaxChecker(TigerParserWrapper wrapper) {
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