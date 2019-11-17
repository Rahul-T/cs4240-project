import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collection;

public class BasicBlock {
    private HashSet<BasicBlock> predecessors;
    private HashSet<BasicBlock> successors;
    private ArrayList<String> lines;
    private String blockName;

    public BasicBlock(String blockName) {
        this.predecessors = new HashSet<BasicBlock>();
        this.successors = new HashSet<BasicBlock>();
        this.lines = new ArrayList<String>();
        this.blockName = blockName;
    }

    public void addLine(String line) {
        this.lines.add(line);
    }

    public void addLines(ArrayList<String> lines) {
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

    public ArrayList<String> getLines() {
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
            retString += String.format("\t\t%1$2d. ", i) + lines.get(i) + "\n";
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