import java.io.File;
import java.io.IOException;

import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class TigerCompiler {
    private static boolean verbosePrint = true;
    private static boolean noIrOutput = false;
    private static String outFileName = null;
    private static final String usageString = "Tiger Compiler Usage: java TigerCompiler <filename> [-no-print] [-no-ir-file] [-outfile=<filename>]";


    public static void main(String[] args) throws IOException {
        // Validate args
        if (args.length >= 2) {
            int count = 0;
            for (String arg : args) {
                if (arg.equals("-no-print")) 
                    verbosePrint = false;
                else if (arg.equals("-no-ir-file")) {
                    noIrOutput = true;
                    if (outFileName != null)
                        System.out.println("WARNING: Output file specified and '-no-ir-file' option used. No output file will be generated.");
                }
                else if (arg.contains("-outfile=") && !noIrOutput)
                    outFileName = arg.split("=")[1];
                else if (arg.contains("-outfile="))
                    System.out.println("WARNING: Output file specified and '-no-ir-file' option used. No output file will be generated.");
                else if (count != 0) {
                    // filename shouldn't cause error
                    System.out.println("Unrecognized flag \'" + arg + "\'\n" + usageString);
                    System.exit(1);
                }
                count++;
            }
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
        } else if (outFileName == null)
            outFileName = fileName + ".ir";

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
            if (verbosePrint) System.out.println("Unsuccessful Parse\n"); 
            System.out.println(wrapper.getErrorStrings() + "\n");
            return;
        }

        wrapper.reset();
        
        SemanticChecker semChecker = new SemanticChecker();
        String result = semChecker.visit(wrapper.getParseTree());
        if (verbosePrint) {
            System.out.println("\nSuccessful Compile\n");
            semChecker.printSymbolTable();
        }

        wrapper.reset();
        IRCodeGenerator irCodeGenerator = new IRCodeGenerator(noIrOutput ? null : outFileName, verbosePrint);
        String res = irCodeGenerator.visit(wrapper.getParseTree());

    }   
}