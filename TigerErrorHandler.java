import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;

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

    public void reset() {
        this.errorNo = 0;
        this.errorStrings = "";
    }

    public int getErrorNo() {
        return this.errorNo;
    }

    public String getErrorStrings() {
        return this.errorStrings;
    }
 }