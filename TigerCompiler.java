import java.io.File;
import java.io.IOException;

import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class TigerCompiler {
    private static boolean verbosePrint = true;
    private static boolean noIrOutput = false;
    public static void main(String[] args) throws IOException {
        // Validate args
        if (args.length >= 2) {
            for (String arg : args) {
                if (arg.equals("-no-print"))
                    verbosePrint = false;
                if (arg.equals("-no-ir-file"))
                    noIrOutput = true;
            }
            verbosePrint = false;
        } else if (args.length != 1) {
            System.out.println("Tiger Compiler Usage: java TigerCompiler <filename> [-no-print] [-no-ir-file]");
            System.exit(1);
        }

        // Check for file
        String fileName = args[0];
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println(String.format("File \"%s\" not found.", fileName));
            System.exit(1);
        }

        // Parse file, check for errors, and print tokens
        if (verbosePrint)
            System.out.println(String.format("Compiling file \"%s\"\n", fileName));
        TigerParserWrapper wrapper = new TigerParserWrapper();

        wrapper.parse(fileName);

        
        ParseTreeWalker walker = new ParseTreeWalker();
        SyntaxChecker synChecker = new SyntaxChecker(wrapper);
        walker.walk(synChecker, wrapper.getParseTree());
    
        if (wrapper.getErrorNumber() == 0) {
            if (verbosePrint)
                System.out.println("Successful Parse\n\n" 
                    + synChecker.getTokenTupleString() + "\n" 
                    + synChecker.getTokenTypeList());
        } else {
            if (verbosePrint)
            System.out.println("Unsuccessful Parse\n"); 
            System.out.println(wrapper.getErrorStrings() + "\n");
            return;
        }

        wrapper.reset();
        
        SemanticChecker semChecker = new SemanticChecker();
        String result = semChecker.visit(wrapper.getParseTree());

        wrapper.reset();
        IRCodeGenerator irCodeGenerator = new IRCodeGenerator(noIrOutput ? null : (fileName + ".ir"), verbosePrint);
        String res = irCodeGenerator.visit(wrapper.getParseTree());
        // semChecker.printSymbolTable();

    }   
}