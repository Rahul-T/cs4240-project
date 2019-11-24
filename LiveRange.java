import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class LiveRange {
    public HashSet<Instruction> instructions;
    private String varName;

    public LiveRange(String varName, HashSet<Instruction> instructions) {
        this.varName = varName;
        this.instructions = instructions;
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
        return this.varName + ": " + this.instructions.toString();
    }

    @Override
    public int hashCode() {
        int code = 0;
        for (Instruction inst : this.instructions) {
            code += inst.hashCode();
        }
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LiveRange)) return false;
        HashSet<Instruction> temp = new HashSet<>();
        temp.addAll(this.instructions);
        temp.removeAll(((LiveRange)o).instructions);
        
        return temp.size() == 0;
    }
}