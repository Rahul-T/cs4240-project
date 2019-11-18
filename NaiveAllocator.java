import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class NaiveAllocator {
    private String irFile;
    public NaiveAllocator(String irFile) {
        this.irFile = irFile;
    }

    public ArrayList<String> generatemips() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.irFile));
        String line;
        ArrayList<String> mips = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            // System.out.println(line);
            String[] lineElements = line.replaceAll(",","").trim().split(" ");
            if (lineElements.length != 0) {
                switch (lineElements[0]) {
                    case "assign":
                        if (lineElements.length == 3) {
                            generateLoad(lineElements[2], "$t0", mips);
                            mips.add("sw $t0, " + lineElements[1]);
                        } else if (lineElements.length == 4) {
                            mips.add("li $t2, 0");
                            mips.add(lineElements[1] + "_init_start:");

                            mips.add("la $t3, " + lineElements[1]);
                            mips.add("add $t0, $t3, $t2");
                            generateLoad(lineElements[3], "$t1", mips);
                            mips.add("sw $t1, ($t0)");

                            mips.add("addi $t2, 1");
                            mips.add("ble $t2, " + lineElements[2] + ", " + lineElements[1] + "_init_start:");
                        }
                        break;
                    
                    case "array_store":
                        mips.add("la $t0, " + lineElements[1]);
                        generateLoad(lineElements[3], "$t1", mips);
                        mips.add("sw $t1, " + lineElements[2] + "($t0)");
                        break;
                    
                    case "array_load":
                        mips.add("la $t0, " + lineElements[2]);
                        mips.add("lw $t1, " + lineElements[3] + "($t0)");
                        mips.add("sw $t1, " + lineElements[1]);
                        break;

                    case "add":
                    case "sub":
                    case "mult":
                    case "div":
                    case "and":
                    case "or":
                        generateLoad(lineElements[2], "$t0", mips);
                        generateLoad(lineElements[3], "$t1", mips);
                        if(lineElements[0].equals("mult")) {
                            mips.add("mul $t2, $t0, $t1");
                        } else {
                            mips.add(lineElements[0] + " $t2, $t0, $t1");
                        }
                        mips.add("sw $t2, " + lineElements[1]);
                        break;
                    
                    case "goto":
                        mips.add("j " + lineElements[1]);
                        break;

                    case "breq":
                    case "brneq":
                    case "brlt":
                    case "brgt":
                    case "brgeq":
                    case "brleq":
                        generateLoad(lineElements[1], "$t0", mips);
                        generateLoad(lineElements[2], "$t1", mips);
                        lineElements[0] = lineElements[0].replace("breq", "beq");
                        lineElements[0] = lineElements[0].replace("brneq", "bne");
                        lineElements[0] = lineElements[0].replace("brlt", "blt");
                        lineElements[0] = lineElements[0].replace("brgt", "bgt");
                        lineElements[0] = lineElements[0].replace("brgeq", "bge");
                        lineElements[0] = lineElements[0].replace("brleq", "ble");
                        mips.add(lineElements[0] + " $t0, $t1, " + lineElements[3]);
                        break;

                    case "return":
                        if(lineElements.length > 1) {
                            mips.add("lw $v0, " + lineElements[1]);
                            mips.add("jr $ra");
                        } else {
                            mips.add("jr $ra");
                        }
                        break;

                    // TODO: Figure out what to do for function calls
                    case "call":
                    case "callr":
                        mips.add(line);
                        break;

                    default: {
                        continue;
                    }
                }
            }
        }
        for(String instr: mips) {
            System.out.println(instr);
        }
        return mips;
    }

    private void generateLoad(String element, String register, ArrayList<String> mips) {
        if (isNumeric(element)) {
            mips.add("li " + register + ", " + element);
        } else {
            mips.add("lw " + register + ", " + element);
        }
    }

    private boolean isNumeric(String strNum) {
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws IOException{
        NaiveAllocator naiveAllocator = new NaiveAllocator("Testing/example.ir");
        ArrayList<String> ir = naiveAllocator.generatemips();
    }
}
