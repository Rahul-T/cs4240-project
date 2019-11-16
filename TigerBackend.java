import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class TigerBackend {
    public static void main(String[] args) throws IOException {
        String sourceFile = args[0];

        BlockGenerator generator = new BlockGenerator(sourceFile);
        
        generator.generateBlocks();

        System.out.println("ENTRY:");
        System.out.println(generator.getEntryBlock());

        System.out.println("\n\nBLOCKS");
        for (BasicBlock block : generator.getBlocks()) {
            System.out.println(block + "\n");
        }
    }
}