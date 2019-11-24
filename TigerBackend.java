import java.io.IOException;
import java.util.*;

public class TigerBackend {
    public static void main(String[] args) throws IOException, InterruptedException {
        String sourceFile;
        // Args: -t file_name
        if (args[0].equals("-t")) {
            sourceFile = args[1];
        } else {
            sourceFile = args[0];
        }


        CFGGenerator generator = new CFGGenerator("P2Testing/" + sourceFile);
        
        generator.generateBlocks();

        // for (BasicBlock b : generator.getBlocks()) {
        //     System.out.println(b);
        // }

        generator.generateInOutSets();

        HashMap<String, BasicBlock> funcs = generator.getFunctionBlocks();

        for (BasicBlock func : funcs.values()) {
            InterferenceGraph intGraph = new InterferenceGraph(generator, func);

            intGraph.color();
            HashMap<Instruction, HashMap<String, String>> map = intGraph.generateRegisterMap();
            // map.entrySet().forEach(entry->{
            //     System.out.println("Key: " + entry.getKey() + "Value: " + entry.getValue());  
            // });
            // System.out.println("\n\n\n\n");
        }
        
        // for (Instruction i : map.keySet()) {
        //     System.out.println(String.format("%s | %s", i.toString(), map.get(i).toString()));
        // }
        // intGraph.printGraph();
        // intGraph.printNodes();


        // for (BasicBlock b : generator.getBlocks()) {
        //     System.out.println(String.format("BLOCK: %-10s  IN: %-20s | OUT: %-20s", b.getBlockName(), b.inSet, b.outSet));
        // }

        Allocator.generateMips(args);
    }
}