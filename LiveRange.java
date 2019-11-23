import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class LiveRange {
    public HashSet<Instruction> instructions;
    private String varName;
    
    private static HashMap<Instruction, String> instructionMap = null;

    public LiveRange(String varName, HashSet<Instruction> instructions) {
        this.varName = varName;
        this.instructions = instructions;
    }

    public static void mapInstructions(BasicBlock entry) {
        instructionMap = new HashMap<Instruction, String>();
        HashSet<BasicBlock> visited = new HashSet<BasicBlock>();
        mapHelper(entry, visited);
    }

    private static void mapHelper(BasicBlock block, HashSet<BasicBlock> visited) {
        if (visited.contains(block)) return;

        ArrayList<Instruction> lines = block.getLines();
        String bName = block.getBlockName();
        visited.add(block);

        for (int i = 0; i < lines.size(); i++) {
            instructionMap.put(lines.get(i), String.format("\t%-15s:%d\t%s\n", bName, i, lines.get(i).getText()));
        }

        for (BasicBlock succ : block.getSuccessors()) {
            mapHelper(succ, visited);
        }
    }

    public LiveRange(String varName) {
        this(varName, new HashSet<Instruction>());
    }

    public void addInstruction(Instruction inst) {
        this.instructions.add(inst);
    } 

    public void addInstructions(Collection<Instruction> coll) {
        this.instructions.addAll(coll);
    }

    public String getVarName() {
        return this.varName;
    }

    // duplicates an existing live range
    public LiveRange duplicate() {
        LiveRange temp = new LiveRange(this.varName);
        temp.addInstructions(this.instructions);
        return temp;
    }

    @Override
    public String toString() {
        String retString = "Live Range for " + this.varName + ":\n";
        if (instructionMap != null) {
            for (Instruction i : this.instructions) {
                retString += instructionMap.get(i);
            }
        } else {
            retString += this.instructions.toString();
        }

        return retString;
    }
}