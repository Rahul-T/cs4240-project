import java.io.File;
import java.io.IOException;

import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class TigerCompiler {
    public static void main(String[] args) throws IOException {
        // Validate args
        if (args.length != 1) {
            System.out.println("Tiger Compiler Usage: java TigerCompiler <filename>");
            return;
        }

        // Check for file
        String fileName = args[0];
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println(String.format("File \"%s\" not found.", fileName));
            return;
        }

        // Parse file, check for errors, and print tokens
        System.out.println(String.format("Compiling file \"%s\"\n", fileName));
        TigerParserWrapper wrapper = new TigerParserWrapper();

        wrapper.parse(fileName);

        
        ParseTreeWalker walker = new ParseTreeWalker();
        SyntaxChecker synChecker = new SyntaxChecker(wrapper);
        walker.walk(synChecker, wrapper.getParseTree());
    
        if (wrapper.getErrorNumber() == 0) {
            System.out.println("Successful Parse\n\n" 
                + synChecker.getTokenTupleString() + "\n" 
                + synChecker.getTokenTypeList());
        } else {
            System.out.println("Unsuccessful Parse\n" 
                + wrapper.getErrorStrings() + "\n");
            return;
        }

        wrapper.reset();
        
        SemanticChecker semChecker = new SemanticChecker();
        String result = semChecker.visit(wrapper.getParseTree());
        System.out.print(result);

    }   
}