import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collection;

public class BasicBlock {
    private HashSet<BasicBlock> predecessors;
    private HashSet<BasicBlock> successors;
    private ArrayList<Instruction> lines;
    private String blockName;


    // in and out sets are public to minimize function calls and set passing.
    public HashSet<String> inSet;
    public HashSet<String> outSet;

    public BasicBlock(String blockName) {
        this.predecessors = new HashSet<BasicBlock>();
        this.successors = new HashSet<BasicBlock>();
        this.inSet = new HashSet<String>();
        this.outSet = new HashSet<String>();
        this.lines = new ArrayList<Instruction>();
        this.blockName = blockName;
    }

    public void addLine(Instruction line) {
        this.lines.add(line);
    }

    public void addLine(String line) {
        this.lines.add(new Instruction(line.trim()));
    }

    public void addLines(ArrayList<Instruction> lines) {
        this.lines.addAll(lines);
    }

    public void addSuccessor(BasicBlock successor) {
        this.successors.add(successor);
    }

    public void addSuccessors(Collection<BasicBlock> collection) {
        this.successors.addAll(collection);
    }

    public void addPredecessor(BasicBlock predecessor) {
        this.predecessors.add(predecessor);
    }

    public void addPredecessors(Collection<BasicBlock> collection) {
        this.predecessors.addAll(collection);
    }

    public ArrayList<Instruction> getLines() {
        return this.lines;
    }

    public HashSet<BasicBlock> getPredecessors() {
        return this.predecessors;
    }

    public HashSet<BasicBlock> getSuccessors() {
        return this.successors;
    }

    public String getBlockName() {
        return blockName;
    }

    @Override
    public String toString() {
        String retString = "===== START BASIC BLOCK " + this.blockName + " =====\n\t--LINES--\n";
        for (int i = 0; i < this.lines.size(); i++) {
            retString += String.format("\t\t%1$2d. ", i) + lines.get(i).toString() + "\n";
        }
        
        retString += "\t--PREDECESSORS--\n";
        for (BasicBlock current : this.predecessors) {
            retString += "\t\t Block " + current.blockName + "\n";
        }

        retString += "\t--SUCCESSORS--\n";
        for (BasicBlock current : this.successors) {
            retString += "\t\t Block " + current.blockName + "\n";
        }

        retString += "===== END BASIC BLOCK " + this.blockName + " =====";
        return retString;
    }
}