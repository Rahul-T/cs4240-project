import java.io.IOException;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.Vocabulary;


/* Class that provides an easy to use interface for parsers generated by
 * ANTLR.
 */
public class TigerParserWrapper {
    private tigerParser parser;
    private tigerLexer lexer;
    private TigerErrorHandler errorHandler;

    public TigerParserWrapper() {
        this.parser = null; // set parser to null on construction, will be set on parse
        this.lexer = null;
        this.errorHandler = new TigerErrorHandler();
    }

    /**
     * Parses the passed in file using a parser for the Tiger Grammar
     * @param filePath Path to the file to parse as a string.
     * @throws IOException
     */
    public void parse(String filePath) throws IOException{
        // read file
        CharStream fileStream = CharStreams.fromFileName(filePath);

        // lex code
        this.lexer = new tigerLexer(fileStream);
        CommonTokenStream tokenStream = new CommonTokenStream(this.lexer);

        // parse code
        this.parser = new tigerParser(tokenStream);
        this.parser.setBuildParseTree(true);
        this.parser.removeErrorListeners();
        this.parser.addErrorListener(this.errorHandler);
    }

    /**
     * Gets the parse tree of the parsed program.
     * @return ParseTree of the program starting at tiger_program.
     */
    public ParseTree getParseTree() {
        if (this.parser != null) {
            return this.parser.tiger_program();
        } else {
            return null;
        }
    }

    /**
     * Gets the vocabulary of the program.
     * @return Vocabulary of tokens for the parsed program.
     */
    public Vocabulary getTigerVocabulary() {
        if (this.lexer != null) {
            return this.lexer.getVocabulary();
        } else {
            return null;
        }
    }

    /**
     * Prints the parse tree as a string.
     */
    public void printTokens() {
        if (this.parser == null) {
            System.out.println("Nothing in the parser!");
            return;
        }
        ParseTree tree = this.parser.tiger_program();
        System.out.print(tree.toStringTree(this.parser));
    }

    /**
     * Gets the number of errors found in the progam by the syntax checker.
     * @return Number of syntax errors found. 0 if no errors.
     */
    public int getErrorNumber() {
        return this.errorHandler.getErrorNo();
    }

    /**
     * Gets string messages for syntax errors found.
     * @return String of error messages. "" if no errors found.
     */
    public String getErrorStrings() {
        return this.errorHandler.getErrorStrings();
    }

}