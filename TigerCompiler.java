import java.io.File;
import java.io.IOException;

import org.antlr.v4.runtime.tree.ParseTree;
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

        System.out.println(String.format("Compiling file \"%s\"", fileName));
        TigerParserWrapper wrapper = new TigerParserWrapper();

        wrapper.parse(fileName);

        System.out.println();

        ParseTreeWalker walker = new ParseTreeWalker();
        TerminalPrinter printer = new TerminalPrinter(wrapper);

        walker.walk(printer, wrapper.getParseTree());
        System.out.println();
    }   
}