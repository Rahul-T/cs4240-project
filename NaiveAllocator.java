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

    public ArrayList<String> generatePseudoMIPS() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.irFile));
        String line;
        ArrayList<String> pseudoMips = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            // System.out.println(line);
            String[] lineElements = line.replaceAll(",","").trim().split(" ");
            if (lineElements.length != 0) {
                switch (lineElements[0]) {
                    case "assign":
                        if (lineElements.length == 3) {
                            generateLoad(lineElements[2], "$t0", pseudoMips);
                            pseudoMips.add("sw $t0, " + lineElements[1]);
                        } else if (lineElements.length == 4) {
                            // TODO: Array initialization to value
                        }
                        break;
                    
                    case "array_store":
                        pseudoMips.add("la $t0, " + lineElements[1]);
                        generateLoad(lineElements[3], "$t1", pseudoMips);
                        pseudoMips.add("sw $t1, " + lineElements[2] + "($t0)");
                        break;
                    
                    case "array_load":
                        pseudoMips.add("la $t0, " + lineElements[2]);
                        pseudoMips.add("lw $t1, " + lineElements[3] + "($t0)");
                        pseudoMips.add("sw $t1, " + lineElements[1]);
                        break;

                    case "add":
                    case "sub":
                    case "mult":
                    case "div":
                    case "and":
                    case "or":
                        generateLoad(lineElements[2], "$t0", pseudoMips);
                        generateLoad(lineElements[3], "$t1", pseudoMips);
                        if(lineElements[0].equals("mult")) {
                            pseudoMips.add("mul $t2, $t0, $t1");
                        } else {
                            pseudoMips.add(lineElements[0] + " $t2, $t0, $t1");
                        }
                        pseudoMips.add("sw $t2, " + lineElements[1]);
                        break;
                    
                    case "goto":
                        pseudoMips.add("j " + lineElements[1]);
                        break;

                    case "breq":
                    case "brneq":
                    case "brlt":
                    case "brgt":
                    case "brgeq":
                    case "brleq":
                        generateLoad(lineElements[1], "$t0", pseudoMips);
                        generateLoad(lineElements[2], "$t1", pseudoMips);
                        lineElements[0] = lineElements[0].replace("breq", "beq");
                        lineElements[0] = lineElements[0].replace("brneq", "bne");
                        lineElements[0] = lineElements[0].replace("brlt", "blt");
                        lineElements[0] = lineElements[0].replace("brgt", "bgt");
                        lineElements[0] = lineElements[0].replace("brgeq", "bge");
                        lineElements[0] = lineElements[0].replace("brleq", "ble");
                        pseudoMips.add(lineElements[0] + " $t0, $t1, " + lineElements[3]);
                        break;

                    case "return":
                        if(lineElements.length > 1) {
                            pseudoMips.add("lw $v0, " + lineElements[1]);
                            pseudoMips.add("jr $ra");
                        } else {
                            pseudoMips.add("jr $ra");
                        }
                        break;

                    // TODO: Figure out what to do for function calls
                    case "call":
                    case "callr":
                        pseudoMips.add(line);
                        break;
                    default: {
                        if (lineElements[0].charAt(lineElements[0].length() - 1) == ':') {
                            pseudoMips.add(lineElements[0]);
                        }
                        continue;
                    }
                }
            }
        }
        for(String instr: pseudoMips) {
            System.out.println(instr);
        }
        return pseudoMips;
    }

    private void generateLoad(String element, String register, ArrayList<String> pseudoMips) {
        if (isNumeric(element)) {
            pseudoMips.add("li " + register + ", " + element);
        } else {
            pseudoMips.add("lw " + register + ", " + element);
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
        ArrayList<String> ir = naiveAllocator.generatePseudoMIPS();
    }
}
