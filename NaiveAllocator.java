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
            String[] lineElements = line.replaceAll(",","").trim().split(" ");
            if (lineElements.length != 0) {
                switch (lineElements[0]) {
                    case "assign": {
                        if (lineElements.length == 3) {
                            if (isNumeric(lineElements[2])) {
                                pseudoMips.add("li $t0, " + lineElements[2]);
                                pseudoMips.add("store_var " + lineElements[1] + ", $t0");
                            } else {
                                pseudoMips.add("lw $t0, " + lineElements[2]);
                                pseudoMips.add("store_var " + lineElements[1] + ", $t0");
                            }
                        } else if (lineElements.length == 4) {
                            //TODO: Fix Array instruction
                            continue;
                        } else {
                            System.out.println("Wrong instruction: " + line.trim());
                            return null;
                        }
                    } default: {
                        continue;
                    }
                }
            }
        }
        for (String l : pseudoMips) {
            System.out.println(l);
        }
        return pseudoMips;
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
