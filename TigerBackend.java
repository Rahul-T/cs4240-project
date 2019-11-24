import java.io.IOException;
import java.util.*;

public class TigerBackend {
    public static void main(String[] args) throws IOException {
        String sourceFile = args[0];

        CFGGenerator generator = new CFGGenerator(sourceFile);
        
        generator.generateBlocks();
        generator.generateInOutSets();
        generator.createLiveRanges();
        
        InterferenceGraph intGraph = new InterferenceGraph(generator);

        intGraph.color();
        HashMap<Instruction, HashMap<String, String>> map = intGraph.generateRegisterMap();
        map.entrySet().forEach(entry->{
            System.out.println(entry.getKey() + " " + entry.getValue());  
        });
        // for (Instruction i : map.keySet()) {
        //     System.out.println(String.format("%s | %s", i.toString(), map.get(i).toString()));
        // }
        // intGraph.printGraph();
        // intGraph.printNodes();


        // for (BasicBlock b : generator.getBlocks()) {
        //     System.out.println(String.format("BLOCK: %-10s  IN: %-20s | OUT: %-20s", b.getBlockName(), b.inSet, b.outSet));
        // }
    }
}