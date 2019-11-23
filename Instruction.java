import java.util.HashSet;

public class Instruction {
    private String text;
    public HashSet<String> inSet;
    public HashSet<String> outSet;

    public Instruction(String text) {
        this.text = text;
        this.inSet = new HashSet<String>();
        this.outSet = new HashSet<String>();
    }

    public String getText() {return this.text;}

    @Override
    public String toString() {
        return this.getText();
    }
}