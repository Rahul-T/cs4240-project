import java.io.File;
import java.io.IOException;

import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class TigerCompiler {
    public static void main(String[] args) throws IOException {
        // make sure they gave us a file
        if (args.length != 1) {
            System.out.println("Tiger Compiler Usage: java TigerCompiler <filename>");
            return;
        }
        String fileName = args[0];
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println(String.format("File \"%s\" not found.", fileName));
            return;
        }

        System.out.println(String.format("Compiling file \"%s\"\n", fileName));
        TigerParserWrapper wrapper = new TigerParserWrapper();

        wrapper.parse(fileName);

        ParseTreeWalker walker = new ParseTreeWalker();
        TerminalPrinter printer = new TerminalPrinter(wrapper);
        walker.walk(printer, wrapper.getParseTree());
    
        if (wrapper.getErrorNumber() == 0) {
            System.out.println("Successful Parse\n\n" 
                + printer.getTokenTupleString() + "\n" 
                + printer.getTokenTypeList());
        } else {
            System.out.println("Unsuccessful Parse\n" 
                + wrapper.getErrorStrings() + "\n");
        }
    }   
}