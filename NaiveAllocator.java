import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class NaiveAllocator {
    private String irFile;
    HashMap<String, HashMap<String, String>> functionToVars;
    HashMap<String, String> globalVars;
    PriorityQueue<Integer> tRegistersInactive;
    PriorityQueue<Integer> fRegistersInactive;
    HashSet<String> tRegistersActive;
    HashSet<String> fRegistersActive;

    public NaiveAllocator(String irFile) {
        this.irFile = irFile;
        globalVars = new HashMap<String, String>();
        functionToVars = new HashMap<String, HashMap<String, String>>();
    }

    public ArrayList<String> buildDataSection() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.irFile));
        String line;
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

        System.out.println("Function To Vars");
        functionToVars.entrySet().forEach(entry->{
            System.out.println(entry.getKey() + " " + entry.getValue());  
        });
        System.out.println("Global Vars");
        globalVars.entrySet().forEach(entry->{
            System.out.println(entry.getKey() + " " + entry.getValue());  
        });

        for(String data: dataSection) {
            System.out.println(data);
        }

        return dataSection;
        
    }

    public String getVarType(String var) {
        for(String function: functionToVars.keySet()) {
            if(functionToVars.get(function).containsKey(var)) {
                String type = functionToVars.get(function).get(var);
                return type;
            }
        }
        System.out.println("NOTFOUND: " + var);
        return "";
    }

    public String getAvailableRegister(String element) {
        element = element.trim();
        if(isNumeric(element)) {
            if(element.contains(".")) {
                String register = "$f" + String.valueOf(fRegistersInactive.poll());
                fRegistersActive.add(register);
                return register;
            } else {
                String register = "$t" + String.valueOf(tRegistersInactive.poll());
                tRegistersActive.add(register);
                return register;
            }
        } else {
            String type = getVarType(element);
            if(type.contains("int")) {
                String register = "$t" + String.valueOf(tRegistersInactive.poll());
                tRegistersActive.add(register);
                return register;
            } else if(type.contains("float")) {
                String register = "$f" + String.valueOf(fRegistersInactive.poll());
                fRegistersActive.add(register);
                return register;
            }
        }
        System.out.println("Error for variable: " + element);
        return "error";
    }

    public void restoreRegister(String register) {
        if(register.contains("$f")) {
            fRegistersInactive.add(Integer.valueOf(register.substring(2)));
        } else if(register.contains("$t")) {
            tRegistersInactive.add(Integer.valueOf(register.substring(2)));
        } else {
            System.out.println("restore error");
        }
    }








    public ArrayList<String> buildTextSection() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.irFile));
        String line;
        ArrayList<String> mips = new ArrayList<>();
        String currentFunction = "";

        mips.add(".text");

        while ((line = br.readLine()) != null) {
            // Comment
            if(line.contains("#")) {
                continue;
            }
            // Function header
            else if(line.contains("(") && line.contains(")")) {
                currentFunction = line.substring(line.indexOf(" ")+1, line.indexOf("("));
                tRegistersInactive = new PriorityQueue<Integer>();
                fRegistersInactive = new PriorityQueue<Integer>();
                for(int i=0; i<=7; i++) {
                    tRegistersInactive.add(i);
                }
                for(int i=4; i<=10; i++) {
                    fRegistersInactive.add(i);
                }
                tRegistersActive = new HashSet<String>();
                fRegistersActive = new HashSet<String>();
            }
            // Regular statement
            else if(line.contains(",")) {

                String[] lineElements = line.trim().split(",");

                if (lineElements.length != 0) {
                    switch (lineElements[0]) {
                        case "assign":
                            if (lineElements.length == 3) {
                                String register = getAvailableRegister(lineElements[2]);
                                generateLoad(lineElements[2], register, mips);
                                mips.add("sw " + register + ", " + lineElements[1]);
                                restoreRegister(register);
                            } else if (lineElements.length == 4) {
                                String loopCounterRegister = getAvailableRegister("0");
                                mips.add("li " + loopCounterRegister + ", 0");
                                mips.add(lineElements[1] + "_init_start:");

                                String arrayAddressRegister = getAvailableRegister("0");
                                mips.add("la " + arrayAddressRegister + ", " + lineElements[1]);

                                String wordMultiplierRegister = getAvailableRegister("4");
                                mips.add("li " + wordMultiplierRegister + ", 4");

                                String arrayOffsetRegister = getAvailableRegister("0");
                                mips.add("mul " + arrayOffsetRegister + ", " + loopCounterRegister + ", " + wordMultiplierRegister);
                                
                                mips.add("add " + arrayAddressRegister + ", " + arrayAddressRegister + ", " + arrayOffsetRegister);

                                String storedValueRegister = getAvailableRegister(lineElements[3]);
                                generateLoad(lineElements[3],storedValueRegister, mips);
                                mips.add("sw " + storedValueRegister + ", " + "(" + arrayAddressRegister + ")");


                                mips.add("addi " + loopCounterRegister + ", 1");
                                mips.add("ble " + loopCounterRegister + ", " + lineElements[2] + ", " + lineElements[1] + "_init_start:");
                                
                                restoreRegister(loopCounterRegister);
                                restoreRegister(arrayAddressRegister);
                                restoreRegister(wordMultiplierRegister);
                                restoreRegister(arrayOffsetRegister);
                                restoreRegister(storedValueRegister);
                            }
                            break;
                        
                        case "array_store":
                            String arrayAddressRegister = getAvailableRegister("0");
                            mips.add("la " + arrayAddressRegister + ", " + lineElements[1]);
                            // mips.add("la $t0, " + lineElements[1]);

                            String arrayOffsetRegister = getAvailableRegister("0");
                            generateLoad(lineElements[2], arrayOffsetRegister, mips);

                            mips.add("add " + arrayAddressRegister + ", " + arrayAddressRegister + ", " + arrayOffsetRegister);

                            String valueRegister = getAvailableRegister(lineElements[3]);
                            generateLoad(lineElements[3], valueRegister, mips);
                            // generateLoad(lineElements[3], "$t1", mips);

                            mips.add("sw " + valueRegister + ", (" + arrayAddressRegister + ")");
                            // mips.add("sw $t1, " + lineElements[2] + "($t0)");
                            restoreRegister(arrayAddressRegister);
                            restoreRegister(valueRegister);
                            restoreRegister(arrayOffsetRegister);
                            break;
                        
                        case "array_load":
                            String arrayAddressRegister2 = getAvailableRegister("0");
                            mips.add("la " + arrayAddressRegister2 + ", " + lineElements[2]);
                            // mips.add("la $t0, " + lineElements[2]);

                            String arrayOffsetRegister2 = getAvailableRegister("0");
                            generateLoad(lineElements[3], arrayOffsetRegister2, mips);

                            mips.add("add " + arrayAddressRegister2 + ", " + arrayAddressRegister2 + ", " + arrayOffsetRegister2);

                            String valueRegister2 = getAvailableRegister(lineElements[1]);
                            mips.add("lw " + valueRegister2 + ", (" + arrayAddressRegister2 + ")");
                            // mips.add("lw $t1, " + lineElements[3] + "($t0)");

                            mips.add("sw " + valueRegister2 + ", " + lineElements[1]);
                            // mips.add("sw $t1, " + lineElements[1]);

                            restoreRegister(arrayAddressRegister2);
                            restoreRegister(valueRegister2);
                            restoreRegister(arrayOffsetRegister2);
                            break;

                        case "add":
                        case "sub":
                        case "mult":
                        case "div":
                        case "and":
                        case "or":
                            String operandRegister1 = getAvailableRegister(lineElements[1]);
                            generateLoad(lineElements[1], operandRegister1, mips);
                            // generateLoad(lineElements[1], "$t0", mips);

                            String operandRegister2 = getAvailableRegister(lineElements[2]);
                            generateLoad(lineElements[2], operandRegister2, mips);
                            // generateLoad(lineElements[2], "$t1", mips);
                            
                            String resultRegister = "";
                            if(operandRegister1.contains("$f") || operandRegister2.contains("$f")) {
                                resultRegister = getAvailableRegister("0.0");
                            } else {
                                resultRegister = getAvailableRegister("0");
                            }

                            if(lineElements[0].equals("mult")) {
                                mips.add("mul " + resultRegister + ", " + operandRegister1 + ", " + operandRegister2);
                            } else {
                                mips.add(lineElements[0] + " " + resultRegister + ", " + operandRegister1 + ", " + operandRegister2);
                            }
                            // if(lineElements[0].equals("mult")) {
                            //     mips.add("mul $t2, $t0, $t1");
                            // } else {
                            //     mips.add(lineElements[0] + " $t2, $t0, $t1");
                            // }

                            mips.add("sw " + resultRegister + ", " + lineElements[1]);
                            // mips.add("sw $t2, " + lineElements[1]);


                            restoreRegister(operandRegister1);
                            restoreRegister(operandRegister2);
                            restoreRegister(resultRegister);
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
                            String firstRegister = getAvailableRegister(lineElements[1]);
                            generateLoad(lineElements[1], firstRegister, mips);
                            String secondRegister = getAvailableRegister(lineElements[2]);
                            generateLoad(lineElements[2], secondRegister, mips);
                            lineElements[0] = lineElements[0].replace("breq", "beq");
                            lineElements[0] = lineElements[0].replace("brneq", "bne");
                            lineElements[0] = lineElements[0].replace("brlt", "blt");
                            lineElements[0] = lineElements[0].replace("brgt", "bgt");
                            lineElements[0] = lineElements[0].replace("brgeq", "bge");
                            lineElements[0] = lineElements[0].replace("brleq", "ble");
                            mips.add(lineElements[0] + " " + firstRegister + ", " + secondRegister + ", " + lineElements[3]);

                            restoreRegister(firstRegister);
                            restoreRegister(secondRegister);
                            break;

                        case "return":
                            if(lineElements.length > 1 && lineElements[1].trim().length() > 0) {
                                String element = lineElements[1];
                                if(isNumeric(element)) {
                                    if(element.contains(".")) {
                                        generateLoad(lineElements[1], "$f0", mips);
                                    } else {
                                        generateLoad(lineElements[1], "$v0", mips);
                                    }
                                } else {
                                    String type = getVarType(element);
                                    if(type.equals("float")) {
                                        generateLoad(lineElements[1], "$f0", mips);
                                    } else {
                                        generateLoad(lineElements[1], "$v0", mips);
                                    }
                                }
                                mips.add("jr $ra");
                            } else {
                                mips.add("jr $ra");
                            }
                            break;

                        case "callr":
                            int argCounter = 0;
                            for(int i = 3; i < lineElements.length; i++) {
                                generateLoad(lineElements[i], "$a" + argCounter, mips);
                                argCounter++;
                            }
                            mips.add("j " + lineElements[2]);
                            String type = getVarType(lineElements[1]);
                            if(type.equals("int")) {
                                mips.add("sw " + lineElements[1] + ", " + "$v0");
                            } else {
                                mips.add("sw " + lineElements[1] + ", " + "$f0");
                            }
                            break;

                        case "call":
                            int argCounter2 = 0;
                            for(int i = 2; i < lineElements.length; i++) {
                                generateLoad(lineElements[i], "$a" + argCounter2, mips);
                                argCounter2++;
                            }
                            if(lineElements[1].contains("printi")) {
                                mips.add("li $v0, 1");
                                mips.add("syscall");
                            } else {
                                mips.add("j " + lineElements[1]);
                            }
                            break;
                    }
                }
            }
            // Label
            else {
                if (line.length() > 0 
                    && line.contains(":")
                    && !line.contains("int-list:")
                    && !line.contains("float-list:")) {
                    mips.add(line);
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
        NaiveAllocator naiveAllocator = new NaiveAllocator("Testing/factorial.ir");
        // ArrayList<String> ir = naiveAllocator.generatemips();
        ArrayList<String> dataSection = naiveAllocator.buildDataSection();
        ArrayList<String> textSection = naiveAllocator.buildTextSection();
    }
}
