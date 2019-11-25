import java.util.HashMap;
import java.util.HashSet;

public class Instruction {
    private String text;
    public String block;
    private int line;
    public int absoluteNumber;
    public HashSet<String> inSet;
    public HashSet<String> outSet;
    public HashSet<String> defs;
    public HashSet<String> uses;

    private static int lineCounter = 0;
    public static HashMap<Integer, Instruction> absoluteMap = null;

    public static void resetLineCounter() {
        Instruction.lineCounter = 0;
    }

    public static int getLineCounter() {
        return lineCounter;
    }

    public static void resetAbsoluteMap() {
        absoluteMap = new HashMap<Integer, Instruction>();
    }

    public Instruction(String text, String block, int line) {
        this.text = text;
        this.block = block;
        this.line = line;
        this.absoluteNumber = Instruction.lineCounter++;
        this.inSet = new HashSet<String>();
        this.outSet = new HashSet<String>();
        this.defs = new HashSet<String>();
        this.uses = new HashSet<String>();
        if (Instruction.absoluteMap == null) Instruction.resetAbsoluteMap();
        Instruction.absoluteMap.put(this.absoluteNumber, this);
    }

    public String getText() {return this.text;}

    @Override
    public String toString() {
        return String.format("%s:%d %s", this.block, this.line, this.text);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Instruction)) return false;
        Instruction temp = (Instruction)o;
        return this.text.equals(temp.text) && this.line == temp.line && this.block.equals(temp.block);
    }

    @Override
    public int hashCode() {
        return this.text.hashCode() ^ this.line ^ this.block.hashCode();
    }
}