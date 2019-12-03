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

        generator.generateInOutSets();

        HashMap<String, BasicBlock> funcs = generator.getFunctionBlocks();

        LinkedHashMap<Instruction, HashMap<String, String>> combinedMap = new LinkedHashMap<Instruction, HashMap<String, String>>();

        for (BasicBlock func : funcs.values()) {
            InterferenceGraph intGraph = new InterferenceGraph(generator, func);

            intGraph.color();
            HashMap<Instruction, HashMap<String, String>> map = intGraph.generateRegisterMap();
            map.entrySet().forEach(entry->{
                combinedMap.put(entry.getKey(), entry.getValue());
                // System.out.println(String.format("%-40s\t%s", entry.getKey(), entry.getValue()));
            });
            // System.out.println("\n\n\n");

            // Janky fix to add "return, , ," instruction at end of main
            // for(Instruction i: map.keySet()) {
            //     if(i.block.equals("main_FUNCTION")) {
            //         combinedMap.put(new Instruction("return,,,", "", 0), new HashMap<String, String>());
            //         break;
            //     }
            // }
        }
        
        // combinedMap.entrySet().forEach(entry->{
        //     System.out.println("Key: " + entry.getKey().getText() + " Value: " + entry.getValue());  
        // });

        // for (Instruction i : map.keySet()) {
        //     System.out.println(String.format("%s | %s", i.toString(), map.get(i).toString()));
        // }
        // intGraph.printGraph();
        // intGraph.printNodes();


        // for (BasicBlock b : generator.getBlocks()) {
        //     System.out.println(String.format("BLOCK: %-10s  IN: %-20s | OUT: %-20s", b.getBlockName(), b.inSet, b.outSet));
        //     for (BasicBlock pred : b.getPredecessors()) {
        //         System.out.println("\tPRED: " + pred.getBlockName());
        //     }
        //     for (BasicBlock succ : b.getSuccessors()) {
        //         System.out.println("\tSUCC: " + succ.getBlockName());
        //     }
        //     // System.out.println(String.format("BLOCK: %s INTS: %s FLOATS: %s", b.getBlockName(), b.ints, b.floats));
        // }

        Allocator.generateMips(args, combinedMap);

        // TEST ALL LINES HAVE VARS

        // HashMap<String, String> tempMap = new HashMap<>();
        // HashSet<String> tempSet = new HashSet<>();
        // HashSet<String> missing = new HashSet<>();
        // for (Instruction i : Instruction.absoluteMap.values()) {
        //     tempMap = combinedMap.get(i);
        //     tempSet.clear();
        //     tempSet.addAll(i.uses);
        //     tempSet.addAll(i.defs);
        //     tempSet.addAll(i.inSet);


        //     for (String var : tempSet) {
        //         if (!tempMap.containsKey(var) && !i.getText().contains("array_store")) {
        //             // System.out.println(String.format("Map for line %s does not contain %s. Map:", i.toString(), var, tempMap.get(var)));
        //             missing.add(var);
        //         }
        //     }
        // }
        // System.out.println("Missing variables for " + sourceFile + ": " + missing);
    }
}