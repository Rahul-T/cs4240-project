import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class NaiveAllocator {
    private String irFile;
    public NaiveAllocator(String irFile) {
        this.irFile = irFile;
    }

    public ArrayList<String> buildDataSection() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.irFile));
        String line;
        HashMap<String, String> globalVars = new HashMap<String, String>();
        HashMap<String, HashMap<String, String>> functionToVars = new HashMap<String, HashMap<String, String>>();
        String currentFunction = "";
        boolean isMain = false;

        while ((line = br.readLine()) != null) {
            // Comment
            if(line.contains("#")) {
                continue;
            }
            // Function header
            else if(line.contains("(") && line.contains(")")) {
                isMain = line.contains("void main()");

                HashMap<String, String> declaredVars = new HashMap<String, String>();
                currentFunction = line.substring(line.indexOf(" ")+1, line.indexOf("("));
                functionToVars.put(currentFunction, declaredVars);

                String[] paramsList = line.substring(line.indexOf("(")+1, line.indexOf(")")).split(",");
                for(String param: paramsList) {
                    if(param.length() == 0) {
                        break;
                    }
                    String[] typeAndName = param.trim().split(" ");
                    declaredVars.put(typeAndName[1], typeAndName[0]);
                }

                // int-list 
                line = br.readLine();
                String[] intList = line.substring(line.indexOf(":")+1).split(",");
                for(String param: intList) {
                    param = param.trim();
                    if(param.length() == 0) {
                        break;
                    }
                    if(param.contains("[")) {
                        globalVars.put(param, "intarray");
                        declaredVars.put(param, "intarray");
                    } else {
                        declaredVars.put(param, "int");
                    }
                }

                // float-list 
                line = br.readLine();
                String[] floatList = line.substring(line.indexOf(":")+1).split(",");
                for(String param: floatList) {
                    param = param.trim();
                    if(param.length() == 0) {
                        break;
                    }
                    if(param.contains("[")) {
                        globalVars.put(param, "floatarray");
                        declaredVars.put(param, "floatarray");
                    } else {
                        declaredVars.put(param, "float");
                    }
                }
            }
            // Regular statements
            else if (!line.contains(":") && !isMain) {
                String[] lineElements = line.trim().split(",");
                
                // System.out.println(Arrays.toString(lineElements));
                switch (lineElements[0]) {
                    case "assign":
                    case "add":
                    case "sub":
                    case "mult":
                    case "div":
                    case "and":
                    case "or":
                    case "return":
                    case "array_store":
                    case "array_load":
                        for(int i=1; i<lineElements.length; i++) {
                            if(isNumeric(lineElements[i])) {
                                continue;
                            }
                            if(!functionToVars.get(currentFunction).keySet().contains(lineElements[i])) {
                                // System.out.println(lineElements[i]);
                                globalVars.put(lineElements[i], "");
                            }
                        }
                        break;
                    case "breq":
                    case "brneq":
                    case "brlt":
                    case "brgt":
                    case "brgeq":
                    case "brleq":
                        for(int i=1; i<lineElements.length-1; i++) {
                            if(isNumeric(lineElements[i])) {
                                continue;
                            }
                            if(!functionToVars.get(currentFunction).keySet().contains(lineElements[i])) {
                                // System.out.println(lineElements[i]);
                                globalVars.put(lineElements[i], "");
                            }
                        }
                        break;
                    case "call":
                        for(int i=2; i<lineElements.length; i++) {
                            if(isNumeric(lineElements[i])) {
                                continue;
                            }
                            if(!functionToVars.get(currentFunction).keySet().contains(lineElements[i])) {
                                // System.out.println(lineElements[i]);
                                globalVars.put(lineElements[i], "");
                            }
                        }
                        break;
                    case "callr":
                        if(isNumeric(lineElements[1])) {
                            continue;
                        }
                        if(!functionToVars.get(currentFunction).keySet().contains(lineElements[1])) {
                            // System.out.println(lineElements[1]);
                            globalVars.put(lineElements[1], "");
                        }
                        for(int i=3; i<lineElements.length; i++) {
                            if(isNumeric(lineElements[i])) {
                                continue;
                            }
                            if(!functionToVars.get(currentFunction).keySet().contains(lineElements[i])) {
                                // System.out.println(lineElements[i]);
                                globalVars.put(lineElements[i], "");
                            }
                        }
                        break;
                        
                }
            }
        }
        for (String globalVar: globalVars.keySet()) {
            globalVars.put(globalVar, functionToVars.get("main").get(globalVar));
        }

        ArrayList<String> dataSection = new ArrayList<String>();
        dataSection.add(".data");
        for(String globalVar: globalVars.keySet()) {
            if(globalVars.get(globalVar).contains("array")) {
                int arraySpace = Integer.valueOf(globalVar.substring(globalVar.indexOf("[")+1, globalVar.indexOf("]"))) * 4;
                dataSection.add(globalVar.substring(0, globalVar.indexOf("[")) + ": .space " + arraySpace);
            } else {
                dataSection.add(globalVar + ": .space 4");
            }
        }
        dataSection.add("");

        // System.out.println("Function To Vars");
        // functionToVars.entrySet().forEach(entry->{
        //     System.out.println(entry.getKey() + " " + entry.getValue());  
        // });
        // System.out.println("Global Vars");
        // globalVars.entrySet().forEach(entry->{
        //     System.out.println(entry.getKey() + " " + entry.getValue());  
        // });

        for(String data: dataSection) {
            System.out.println(data);
        }

        return dataSection;
        
    }

    public ArrayList<String> generatemips() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.irFile));
        String line;
        ArrayList<String> mips = new ArrayList<>();

        mips.add(".text");

        while ((line = br.readLine()) != null) {
            // System.out.println(line);
            String[] lineElements = line.trim().split(",");
            System.out.println(Arrays.toString(lineElements));
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
                            generateLoad(lineElements[1], "$v0", mips);
                            mips.add("jr $ra");
                        } else {
                            mips.add("jr $ra");
                        }
                        break;

                    // TODO: Figure out what to do for function calls
                    case "callr":
                        // TODO: Figure stack for function calls
                        for(int i = 0; i < lineElements.length - 3; i++) {
                            generateLoad(lineElements[i + 3], "$a" + i, mips);
                        }
                        // TODO: Add return address from stack
                        mips.add("j " + lineElements[2]);
                        mips.add("sw " + lineElements[1] + ", " + "$v0");
                        break;

                    case "call":
                        // TODO: Figure stack for function calls
                        for(int i = 0; i < lineElements.length - 2; i++) {
                            generateLoad(lineElements[i + 2], "$a" + i, mips);
                        }
                        mips.add("j " + lineElements[1]);
                        break;
                    default: {
                        if (lineElements[0].length() > 0 
                            && lineElements[0].charAt(lineElements[0].length() - 1) == ':'
                            && !lineElements[0].equals("int-list:")
                            && !lineElements[0].equals("float-list:")) {
                            mips.add(lineElements[0]);
                        }
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
        NaiveAllocator naiveAllocator = new NaiveAllocator("Testing/test1.ir");
        // ArrayList<String> ir = naiveAllocator.generatemips();
        ArrayList<String> dataSection = naiveAllocator.buildDataSection();
        ArrayList<String> ir = naiveAllocator.generatemips();
    }
}
