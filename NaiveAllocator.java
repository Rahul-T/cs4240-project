import java.io.*;
import java.util.*;

public class NaiveAllocator {
    private String irFile;
    HashMap<String, HashMap<String, String>> functionToVarsToType;
    HashMap<String, String> globalVars;
    PriorityQueue<Integer> tRegistersInactive;
    PriorityQueue<Integer> fRegistersInactive;
    HashSet<String> tRegistersActive;
    HashSet<String> fRegistersActive;
    
    int stackOffsetStart = 20;
    HashMap<String, HashMap<String, String>> functionToVarsToOffset;
    ArrayList<String> mips;
    boolean verbose;

    public NaiveAllocator(String irFile, boolean isVerbose) {
        this.irFile = irFile;
        globalVars = new HashMap<String, String>();
        functionToVarsToType = new HashMap<String, HashMap<String, String>>();
        functionToVarsToOffset = new HashMap<String, HashMap<String, String>>();
        mips = new ArrayList<String>();
        verbose = isVerbose;
    }

    public void buildDataSection() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.irFile));
        String line;
        String currentFunction = "";
        boolean isMain = false;
        HashSet<String> globals = new HashSet<>();

        while ((line = br.readLine()) != null) {
            // Comment
            if(line.contains("#")) {
                continue;
            }
            // Function header
            else if(line.contains("(") && line.contains(")")) {
                isMain = line.contains("void main()");

                HashMap<String, String> declaredVarsToType = new HashMap<String, String>();
                currentFunction = line.substring(line.indexOf(" ")+1, line.indexOf("("));
                functionToVarsToType.put(currentFunction, declaredVarsToType);

                int stackOffset = stackOffsetStart;
                HashMap<String, String> declaredVarsToOffset = new HashMap<String, String>();
                functionToVarsToOffset.put(currentFunction, declaredVarsToOffset);

                globals = new HashSet<>();
                String[] paramsList = line.substring(line.indexOf("(")+1, line.indexOf(")")).split(",");
                for(String param: paramsList) {
                    if(param.length() == 0) {
                        break;
                    }
                    if(param.contains("[")) {
                        String[] typeAndNamearr = param.trim().split(" ");
                        String name = typeAndNamearr[1];
                        globals.add(name);
                        continue;
                    }
                    String[] typeAndName = param.trim().split(" ");
                    declaredVarsToType.put(typeAndName[1], typeAndName[0]);
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
                        declaredVarsToType.put(param, "intarray");
                    } else {
                        declaredVarsToType.put(param, "int");
                        declaredVarsToOffset.put(param, String.valueOf(stackOffset));
                        stackOffset += 4;
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
                        declaredVarsToType.put(param, "floatarray");
                    } else {
                        declaredVarsToType.put(param, "float");
                        declaredVarsToOffset.put(param, String.valueOf(stackOffset));
                        stackOffset += 4;
                    }
                }
                declaredVarsToOffset.put("#total#", String.valueOf(stackOffset-4));
            }
            // Regular statements
            else if (!line.contains(":") && !isMain) {
                String[] lineElements = line.replace(" ", "").split(",");
                
                switch (lineElements[0].trim()) {
                    case "assign":
                        for(int i=1; i<lineElements.length; i++) {
                            lineElements[i] = lineElements[i].trim();
                            if(isNumeric(lineElements[i])) {
                                continue;
                            }
                            if(!functionToVarsToType.get(currentFunction).keySet().contains(lineElements[i])
                                && !globals.contains(lineElements[i])) {
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
                            lineElements[i] = lineElements[i].trim();
                            if(isNumeric(lineElements[i])) {
                                continue;
                            }
                            if(!functionToVarsToType.get(currentFunction).keySet().contains(lineElements[i])
                                && !globals.contains(lineElements[i])) {
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
                            lineElements[i] = lineElements[i].trim();
                            if(isNumeric(lineElements[i])) {
                                continue;
                            }
                            if(!functionToVarsToType.get(currentFunction).keySet().contains(lineElements[i])
                            && !globals.contains(lineElements[i])) {
                                // System.out.println(Arrays.toString(lineElements));
                                // System.out.println(lineElements[i]);
                                globalVars.put(lineElements[i], "");
                            }
                        }
                        break;
                    case "call":
                        for(int i=2; i<lineElements.length; i++) {
                            lineElements[i] = lineElements[i].trim();
                            if(isNumeric(lineElements[i])) {
                                continue;
                            }
                            if(!functionToVarsToType.get(currentFunction).keySet().contains(lineElements[i])
                            && !globals.contains(lineElements[i])) {
                                // System.out.println(lineElements[i]);
                                globalVars.put(lineElements[i], "");
                            }
                        }
                        break;
                    case "callr":
                        if(isNumeric(lineElements[1])) {
                            continue;
                        }
                        if(!functionToVarsToType.get(currentFunction).keySet().contains(lineElements[1])) {
                            // System.out.println(lineElements[1]);
                            globalVars.put(lineElements[1], "");
                        }
                        for(int i=3; i<lineElements.length; i++) {
                            lineElements[i] = lineElements[i].trim();
                            if(isNumeric(lineElements[i])) {
                                continue;
                            }
                            if(!functionToVarsToType.get(currentFunction).keySet().contains(lineElements[i])
                            && !globals.contains(lineElements[i])) {
                                // System.out.println(lineElements[i]);
                                globalVars.put(lineElements[i], "");
                            }
                        }
                        break;
                        
                }
            }
        }
        for (String globalVar: globalVars.keySet()) {
            globalVars.put(globalVar, functionToVarsToType.get("main").get(globalVar));
        }
        

        // System.out.println("Function To Vars");
        // functionToVarsToType.entrySet().forEach(entry->{
        //     System.out.println(entry.getKey() + " " + entry.getValue());  
        // });
        // System.out.println("Global Vars");
        // globalVars.entrySet().forEach(entry->{
        //     System.out.println(entry.getKey() + " " + entry.getValue());  
        // });

        // System.out.println("Stack Offsets");
        // functionToVarsToOffset.entrySet().forEach(entry->{
        //     System.out.println(entry.getKey() + " " + entry.getValue());  
        // });

        mips.add(".data");
        for(String globalVar: globalVars.keySet()) {
            if(globalVars.get(globalVar).contains("array")) {
                int arraySpace = Integer.valueOf(globalVar.substring(globalVar.indexOf("[")+1, globalVar.indexOf("]"))) * 4;
                mips.add(globalVar.substring(0, globalVar.indexOf("[")) + ": .space " + arraySpace);
            } else {
                mips.add(globalVar + ": .space 4");
            }
        }
        mips.add("");

        // System.out.println("Function To Vars");
        // functionToVarsToType.entrySet().forEach(entry->{
        //     System.out.println(entry.getKey() + " " + entry.getValue());  
        // });
        // System.out.println("Global Vars");
        // globalVars.entrySet().forEach(entry->{
        //     System.out.println(entry.getKey() + " " + entry.getValue());  
        // });

        // System.out.println("Stack Offsets");
        // functionToVarsToOffset.entrySet().forEach(entry->{
        //     System.out.println(entry.getKey() + " " + entry.getValue());  
        // });

        System.out.println("");
        
    }

    public String getVarType(String var) {
        for(String function: functionToVarsToType.keySet()) {
            if(functionToVarsToType.get(function).containsKey(var)) {
                String type = functionToVarsToType.get(function).get(var);
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

    public String getStackLocation(String var, String currentFunction) {
        var = var.trim();
        if(!isNumeric(var)) {
            String offset = functionToVarsToOffset.get(currentFunction).get(var);
            
            // Global variable
            if(offset == null) {
                return var;
            }

            var = offset + "($sp)";
        }
        return var;
    }



    public void buildTextSection() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.irFile));
        String line;
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
                mips.add("");
                mips.add(currentFunction + ":");
                mips.add("sub $sp, $sp, " + functionToVarsToOffset.get(currentFunction).get("#total#"));
                mips.add("sw $ra, 16($sp)");

                String params = line.substring(line.indexOf("(")+1, line.indexOf(")"));
                String[] paramsList = params.split(",");

                if (paramsList[0].length() > 0) {
                    for(int i=0; i<paramsList.length; i++) {
                        String[] paramNameType = paramsList[i].trim().split(" ");
                        String actualParam = paramNameType[1].trim();
                        // System.out.println(Arrays.toString(paramsList));
                        // System.out.println(actualParam);
                        String argOffset = functionToVarsToOffset.get(currentFunction).get(actualParam);
                        if(paramNameType[0].contains("[")) {
                            continue;
                        }
                        mips.add("sw $a" + i + ", " + argOffset + "($sp)");
                    }
                }

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
                                generateLoad(lineElements[2], register, mips, currentFunction);

                                mips.add("sw " + register + ", " + getStackLocation(lineElements[1], currentFunction));
                                restoreRegister(register);
                            } else if (lineElements.length == 4) {
                                String loopCounterRegister = getAvailableRegister("0");
                                mips.add("li " + loopCounterRegister + ", 0");
                                mips.add(lineElements[1].trim() + "_init_start:");

                                String arrayAddressRegister = getAvailableRegister("0");
                                mips.add("la " + arrayAddressRegister + ", " + lineElements[1]);

                                String wordMultiplierRegister = getAvailableRegister("4");
                                mips.add("li " + wordMultiplierRegister + ", 4");

                                String arrayOffsetRegister = getAvailableRegister("0");
                                mips.add("mul " + arrayOffsetRegister + ", " + loopCounterRegister + ", " + wordMultiplierRegister);
                                
                                mips.add("add " + arrayAddressRegister + ", " + arrayAddressRegister + ", " + arrayOffsetRegister);

                                String storedValueRegister = getAvailableRegister(lineElements[3]);
                                generateLoad(lineElements[3],storedValueRegister, mips, currentFunction);
                                mips.add("sw " + storedValueRegister + ", " + "(" + arrayAddressRegister + ")");


                                mips.add("addi " + loopCounterRegister + ", 1");
                                mips.add("ble " + loopCounterRegister + ", " + lineElements[2] + ", " + lineElements[1] + "_init_start");
                                
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
                            generateLoad(lineElements[2], arrayOffsetRegister, mips, currentFunction);
                            mips.add("mulo " + arrayOffsetRegister + ", " + arrayOffsetRegister + ", " + 4);
                            mips.add("add " + arrayAddressRegister + ", " + arrayAddressRegister + ", " + arrayOffsetRegister);

                            String valueRegister = getAvailableRegister(lineElements[3]);
                            generateLoad(lineElements[3], valueRegister, mips, currentFunction);
                            // generateLoad(lineElements[3], "$t1", mips, currentFunction);

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
                            generateLoad(lineElements[3], arrayOffsetRegister2, mips, currentFunction);
                            mips.add("mulo " + arrayOffsetRegister2 + ", " + arrayOffsetRegister2 + ", " + 4);
                            mips.add("add " + arrayAddressRegister2 + ", " + arrayAddressRegister2 + ", " + arrayOffsetRegister2);

                            String valueRegister2 = getAvailableRegister(lineElements[1]);
                            mips.add("lw " + valueRegister2 + ", (" + arrayAddressRegister2 + ")");
                            // mips.add("lw $t1, " + lineElements[3] + "($t0)");

                            mips.add("sw " + valueRegister2 + ", " + getStackLocation(lineElements[1], currentFunction));
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
                            generateLoad(lineElements[1], operandRegister1, mips, currentFunction);
                            // generateLoad(lineElements[1], "$t0", mips, currentFunction);

                            String operandRegister2 = getAvailableRegister(lineElements[2]);
                            generateLoad(lineElements[2], operandRegister2, mips, currentFunction);
                            // generateLoad(lineElements[2], "$t1", mips, currentFunction);
                            
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

                            mips.add("sw " + resultRegister + ", " + getStackLocation(lineElements[3], currentFunction));


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
                            generateLoad(lineElements[1], firstRegister, mips, currentFunction);
                            String secondRegister = getAvailableRegister(lineElements[2]);
                            generateLoad(lineElements[2], secondRegister, mips, currentFunction);
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
                                String element = lineElements[1].trim();
                                if(isNumeric(element)) {
                                    if(element.contains(".")) {
                                        generateLoad(lineElements[1], "$f0", mips, currentFunction);
                                    } else {
                                        generateLoad(lineElements[1], "$v0", mips, currentFunction);
                                    }
                                } else {
                                    String type = getVarType(element);
                                    if(type.equals("float")) {
                                        generateLoad(lineElements[1], "$f0", mips, currentFunction);
                                    } else {
                                        generateLoad(lineElements[1], "$v0", mips, currentFunction);
                                    }
                                }
                            }
                            mips.add("lw $ra, 16($sp)");
                            mips.add("addi $sp, $sp, " + functionToVarsToOffset.get(currentFunction).get("#total#"));
                            mips.add("jr $ra");
                            
                            break;

                        case "callr":
                            int argCounter = -1;
                            for(int i = 3; i < lineElements.length; i++) {
                                argCounter++;
                                if(globalVars.containsKey(lineElements[i].trim())) {
                                    continue;
                                }
                                generateLoad(lineElements[i], "$a" + argCounter, mips, currentFunction);
                            }
                            mips.add("jal " + lineElements[2]);
                            String type = getVarType(lineElements[1].trim());
                            if(type.equals("int")) {
                                mips.add("sw " + "$v0" + ", " + getStackLocation(lineElements[1], currentFunction));
                            } else {
                                mips.add("sw " + "$f0" + ", " + getStackLocation(lineElements[1], currentFunction));
                            }
                            break;

                        case "call":
                            int argCounter2 = -1;
                            for(int i = 2; i < lineElements.length; i++) {
                                argCounter2++;
                                if(globalVars.containsKey(lineElements[i].trim())) {
                                    continue;
                                }
                                generateLoad(lineElements[i], "$a" + argCounter2, mips, currentFunction);
                            }
                            if(lineElements[1].contains("printi")) {
                                mips.add("li $v0, 1");
                                mips.add("syscall");
                            } else {
                                mips.add("jal " + lineElements[1]);
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
                        String rawLabel = line.substring(0, line.indexOf(":")).trim();
                        if(!functionToVarsToType.keySet().contains(rawLabel)) {
                            mips.add(line);
                        }
                }
            }
        }
    }

    public void createFile(String fileName) {
        PrintWriter fileWriter;
        try {
            fileWriter = new PrintWriter(new FileWriter(fileName));
            for (String line : this.mips) {
                System.out.println(line);
                if (this.verbose) {
                    fileWriter.println(line);
                }
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateLoad(String element, String register, ArrayList<String> mips, String currentFunction) {
        if (isNumeric(element)) {
            mips.add("li " + register + ", " + element);
        } else {
            mips.add("lw " + register + ", " + getStackLocation(element, currentFunction));
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
        NaiveAllocator naiveAllocator = new NaiveAllocator("Testing/factorial.ir", true);
        // ArrayList<String> ir = naiveAllocator.generatemips();
        naiveAllocator.buildDataSection();
        naiveAllocator.buildTextSection();
        naiveAllocator.createFile("Testing/testFactorial.s");
    }
}
