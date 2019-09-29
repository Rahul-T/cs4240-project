import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.Vocabulary;

public class TerminalPrinter extends tigerBaseListener {
    private Vocabulary vocab;

    public TerminalPrinter(TigerParserWrapper wrapper) {
        this.vocab = wrapper.getTigerVocabulary();
    }
    @Override
    public void visitTerminal(TerminalNode term) {
        System.out.print(this.vocab.getSymbolicName(term.getSymbol().getType()) + " ");
    }

}