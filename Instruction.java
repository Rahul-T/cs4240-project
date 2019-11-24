import java.util.HashSet;

public class Instruction {
    private String text;
    private String block;
    private int line;
    private int absoluteNumber;
    public HashSet<String> inSet;
    public HashSet<String> outSet;
    public HashSet<String> defs;
    public HashSet<String> uses;

    private static int lineCounter = 0;

    public static void resetLineCounter() {
        Instruction.lineCounter = 0;
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