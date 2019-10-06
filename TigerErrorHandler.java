import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;

/**
 * Handler for Tiger syntax errors. Tracks the number of errors and gives string
 * messages for each error.
 */
public class TigerErrorHandler extends BaseErrorListener {

    private int errorNo;
    private String errorStrings;

    public TigerErrorHandler() {
        this.errorNo = 0;
        this.errorStrings = "";
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
        int line, int charPositionInLine, String msg, RecognitionException e) {
        
        this.errorNo++;
        this.errorStrings = this.errorStrings + "\nline " + line + ":" 
            + charPositionInLine + " " + msg;
    }

    /**
     * Reset the number of errors and the error string for this syntax error
     * handler.
     */
    public void reset() {
        this.errorNo = 0;
        this.errorStrings = "";
    }

    /**
     * Gets the number of syntax errors found.
     * @return Number of syntax errors. 0 if no errors found.
     */
    public int getErrorNo() {
        return this.errorNo;
    }

    /**
     * Gets all string messages for errors found.
     * @return A string of all error messages, one per line. Returns "" if no
     *         errors.
     */
    public String getErrorStrings() {
        return this.errorStrings;
    }
 }