import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class TigerBackend {
    public static void main(String[] args) throws IOException {
        String sourceFile = args[0];

        CFGGenerator generator = new CFGGenerator(sourceFile);
        
        generator.generateBlocks();


        // System.out.println("ENTRY:");
        // System.out.println(generator.getEntryBlock());

        // System.out.println("\n\nBLOCKS");
        // for (BasicBlock block : generator.getBlocks()) {
        //     System.out.println(block + "\n");
        // }

        generator.generateInOutSets();
        generator.createLiveRanges();
        
        InterferenceGraph intGraph = new InterferenceGraph(generator);
        // generator.printWebs();
        intGraph.color();
        HashMap<Instruction, HashMap<String, String>> map = intGraph.generateRegisterMap();
        // for (Instruction i : map.keySet()) {
        //     System.out.println(String.format("%s | %s", i.toString(), map.get(i).toString()));
        // }
        // intGraph.printGraph();
        // intGraph.printNodes();


        // for (BasicBlock b : generator.getBlocks()) {
        //     System.out.println(String.format("BLOCK: %s | IN: %s | OUT: %s", b.getBlockName(), b.inSet, b.outSet));
        // }
    }
}