import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class TigerBackend {
    public static void main(String[] args) throws IOException {
        String sourceFile = args[0];

        CFGGenerator generator = new CFGGenerator(sourceFile);
        
        generator.generateBlocks();
        LiveRange.mapInstructions(generator.getEntryBlock());

        // System.out.println("ENTRY:");
        // System.out.println(generator.getEntryBlock());

        // System.out.println("\n\nBLOCKS");
        // for (BasicBlock block : generator.getBlocks()) {
        //     System.out.println(block + "\n");
        // }

        generator.generateInOutSets();
        generator.createLiveRanges();
        
        HashMap<String, HashSet<LiveRange>> webs = generator.getWebs();
        for (String s : webs.keySet()) {
            for (LiveRange range : webs.get(s)) {
                System.out.println(range);
            }
        }


        // for (BasicBlock b : generator.getBlocks()) {
        //     System.out.println(String.format("BLOCK: %s | IN: %s | OUT: %s", b.getBlockName(), b.inSet, b.outSet));
        // }
    }
}