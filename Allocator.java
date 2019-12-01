import java.io.*;
import java.util.*;

public abstract class Allocator {
    public String irFile;
    HashMap<String, HashMap<String, String>> functionToVarsToType;
    HashMap<String, String> globalVars;
    
    int stackOffsetStart = 20;
    HashMap<String, HashMap<String, String>> functionToVarsToOffset;
    ArrayList<String> mips;
    boolean verbose;

    LinkedHashMap<Instruction, HashMap<String, String>> instrToVarRegs;

    public Allocator(String irFile, boolean isVerbose, LinkedHashMap<Instruction, HashMap<String, String>> combinedMap) {
        this.irFile = irFile;
        globalVars = new HashMap<String, String>();
        functionToVarsToType = new HashMap<String, HashMap<String, String>>();
        functionToVarsToOffset = new HashMap<String, HashMap<String, String>>();
        mips = new ArrayList<String>();
        verbose = isVerbose;
        instrToVarRegs = combinedMap;
    }

    public abstract String getAvailableRegister(String element);

    public abstract void restoreRegisters(String[] registers);

    public abstract void registersToAndFromStack(String currentFunction, String instr);

    public abstract void buildTextSection() throws IOException;

    public abstract void regularAssignInstr(String[] lineElements, String currentFunction);

    public abstract void arraystoreInstr(String[] lineElements, String currentFunction);

    public abstract void arrayloadInstr(String[] lineElements, String currentFunction);

    public abstract void opInstr(String[] lineElements, String currentFunction);

    public abstract void branchInstr(String[] lineElements, String currentFunction);

    public abstract void generateLoad(String element, String register, ArrayList<String> mips, String currentFunction);

    // Building .data section

    public void printTables() {
        System.out.println("Function To Vars");
        functionToVarsToType.entrySet().forEach(entry->{
            System.out.println(entry.getKey() + " " + entry.getValue());  
        });
        System.out.println("Global Vars");
        globalVars.entrySet().forEach(entry->{
            System.out.println(entry.getKey() + " " + entry.getValue());  
        });

        System.out.println("Stack Offsets");
        functionToVarsToOffset.entrySet().forEach(entry->{
            System.out.println(entry.getKey() + " " + entry.getValue());  
        });
    }

    public void buildDataSection() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.irFile));
        String line;
        String currentFunction = "";
        boolean isMain = false;
        HashSet<String> globalArrays = new HashSet<>();

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

                String[] paramsList = line.substring(line.indexOf("(")+1, line.indexOf(")")).split(",");
                for(String param: paramsList) {
                    if(param.length() == 0) {
                        break;
                    }
                    if(param.contains("[")) {
                        String[] typeAndNamearr = param.trim().split(" ");
                        String name = typeAndNamearr[1];
                        globalArrays.add(name);
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
                                && !globalArrays.contains(lineElements[i])) {
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
                                && !globalArrays.contains(lineElements[i])) {
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
                            && !globalArrays.contains(lineElements[i])) {
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
                            && !globalArrays.contains(lineElements[i])) {
                                globalVars.put(lineElements[i], "");
                            }
                        }
                        break;
                    case "callr":
                        if(isNumeric(lineElements[1])) {
                            continue;
                        }
                        if(!functionToVarsToType.get(currentFunction).keySet().contains(lineElements[1])) {
                            globalVars.put(lineElements[1], "");
                        }
                        for(int i=3; i<lineElements.length; i++) {
                            lineElements[i] = lineElements[i].trim();
                            if(isNumeric(lineElements[i])) {
                                continue;
                            }
                            if(!functionToVarsToType.get(currentFunction).keySet().contains(lineElements[i])
                            && !globalArrays.contains(lineElements[i])) {
                                globalVars.put(lineElements[i], "");
                            }
                        }
                        break;
                        
                }
            }
        }

        HashSet<String> trackDuplicates = new HashSet<String>();
        for (String globalVar: globalVars.keySet()) {
            if(globalVar.contains("[")) {
                trackDuplicates.add(globalVar.substring(0, globalVar.indexOf("[")));
            }
            globalVars.put(globalVar, functionToVarsToType.get("main").get(globalVar));
        }
        for(String duplicate: trackDuplicates) {
            globalVars.remove(duplicate);
        }

//         printTables();

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

        // printTables();
        
    }

    public boolean isNumeric(String strNum) {
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
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

    public void setupFunction(String currentFunction) {
        mips.add("");
        mips.add(currentFunction + ":");
        mips.add("@ " + currentFunction + " sub");
        mips.add("sw $ra, 16($sp)");
    }

    public HashSet<String> getParamsFromRegisters(String line, String currentFunction) {
        String params = line.substring(line.indexOf("(")+1, line.indexOf(")"));
        String[] paramsList = params.split(",");
        HashSet<String> allParams = new HashSet<String>();

        if (paramsList[0].length() > 0) {
            for(int i=0; i<paramsList.length; i++) {
                String[] paramNameType = paramsList[i].trim().split(" ");
                String actualParam = paramNameType[1].trim();
                allParams.add(actualParam);
                String argOffset = functionToVarsToOffset.get(currentFunction).get(actualParam);
                if(paramNameType[0].contains("[")) {
                    continue;
                }
                if(paramNameType[0].trim().equals("float")) {
                    int j = i+11;
                    mips.add("sw $f" + j + ", " + argOffset + "($sp)");
                } else {
                    mips.add("sw $a" + i + ", " + argOffset + "($sp)");
                }
                
            }
        }
        // mips.add(allParams.toString());
        return allParams;
    }

    public String getStoreInstrType(String register) {
        if(register.contains("$f")) {
            return "s.s ";
        } else {
            return "sw ";
        }
    }

    public void assignInstr(String[] lineElements, String currentFunction) {
        if (lineElements.length == 3) {
            regularAssignInstr(lineElements, currentFunction);
        } else if (lineElements.length == 4) {
            arrayAssignInstr(lineElements, currentFunction);
        }
    }

    public void arrayAssignInstr(String[] lineElements, String currentFunction) {
        String loopCounterRegister = "$t0";

        mips.add("li " + loopCounterRegister + ", 0");
        mips.add(lineElements[1].trim() + "_init_start:");

        String arrayAddressRegister = "$t1";
        mips.add("la " + arrayAddressRegister + ", " + lineElements[1]);

        String wordMultiplierRegister = "$t2";
        mips.add("li " + wordMultiplierRegister + ", 4");

        String arrayOffsetRegister = "$t3";
        mips.add("mul " + arrayOffsetRegister + ", " + loopCounterRegister + ", " + wordMultiplierRegister);
        
        mips.add("add " + arrayAddressRegister + ", " + arrayAddressRegister + ", " + arrayOffsetRegister);

        String storedValueRegister = lineElements[3].contains(".") ? "$f4" : "$t4";

        generateLoad(lineElements[3],storedValueRegister, mips, currentFunction);

        mips.add(getStoreInstrType(storedValueRegister) + storedValueRegister + ", " + "(" + arrayAddressRegister + ")");

        mips.add("addi " + loopCounterRegister + ", 1");
        mips.add("ble " + loopCounterRegister + ", " + lineElements[2] + ", " + lineElements[1] + "_init_start");
    }

    public void returnInstr(String[] lineElements, String currentFunction) {
        if(lineElements.length > 1 && lineElements[1].trim().length() > 0) {
            String element = lineElements[1].trim();
            if(isNumeric(element)) {
                if(element.contains(".")) {
                    generateLoad(element, "$f0", mips, currentFunction);
                } else {
                    generateLoad(element, "$v0", mips, currentFunction);
                }
            } else {
                String type = getVarType(element);
                if(type.equals("float")) {
                    generateLoad(element, "$f0", mips, currentFunction);
                } else {
                    generateLoad(element, "$v0", mips, currentFunction);
                }
            }
        }
        mips.add("lw $ra, 16($sp)");
        mips.add("@ " + currentFunction + " addi");
        mips.add("jr $ra");
    }

    public void callInstr(String[] lineElements, String currentFunction, int totalsize2, HashMap<String, Integer> maxAdditionalOffset) {
        int argCounter2 = -1;
        int floatArgCounter2 = 11;
        for(int i = 2; i < lineElements.length; i++) {
            argCounter2++;
            floatArgCounter2++;
            if(globalVars.containsKey(lineElements[i].trim())) {
                continue;
            }
            if(getVarType(lineElements[i].trim()).equals("float")) {
                generateLoad(lineElements[i].trim(), "$f" + floatArgCounter2, mips, currentFunction);
            } else {
                generateLoad(lineElements[i].trim(), "$a" + argCounter2, mips, currentFunction);
            }
        }
        if(lineElements[1].contains("printi")) {
            mips.add("li $v0, 1");
            mips.add("syscall");
        } else {
            registersToAndFromStack(currentFunction, "sw ");
            mips.add("jal " + lineElements[1]);
            registersToAndFromStack(currentFunction, "lw ");
            maxAdditionalOffset.put(currentFunction, Math.max(maxAdditionalOffset.get(currentFunction), totalsize2));
        }
    }

    public void callrInstr(String[] lineElements, String currentFunction) {
        int argCounter = -1;
        int floatArgCounter = 11;
        for(int i = 3; i < lineElements.length; i++) {
            argCounter++;
            floatArgCounter++;
            if(globalVars.containsKey(lineElements[i].trim())) {
                continue;
            }
            if(getVarType(lineElements[i].trim()).equals("float")) {
                generateLoad(lineElements[i].trim(), "$f" + floatArgCounter, mips, currentFunction);
            } else {
                generateLoad(lineElements[i].trim(), "$a" + argCounter, mips, currentFunction);
            }
            
        }
        
        registersToAndFromStack(currentFunction, "sw ");

        mips.add("jal " + lineElements[2]);
        String type = getVarType(lineElements[1].trim());
        if(type.equals("int")) {
            mips.add("sw " + "$v0" + ", " + getStackLocation(lineElements[1], currentFunction));
        } else {
            mips.add("s.s " + "$f0" + ", " + getStackLocation(lineElements[1], currentFunction));
        }
        
        registersToAndFromStack(currentFunction, "lw ");
    }

    public static void generateMips(String[] args, LinkedHashMap<Instruction, HashMap<String, String>> instrToVarRegs) throws IOException, InterruptedException {
        for (String fileName : args) {
            if (fileName.equals("-t"))
                continue;
            Allocator naiveAllocator = new NaiveAllocator("P2Testing/" + fileName, false);
            naiveAllocator.buildDataSection();
            naiveAllocator.buildTextSection();
            String naivefileName = fileName.replace(".ir", "_naive.s");
            naiveAllocator.createFile("P2Output/" + naivefileName);

            Allocator coloringAllocator = new ColoringAllocator("P2Testing/" + fileName, true, instrToVarRegs);
            coloringAllocator.buildDataSection();
            coloringAllocator.buildTextSection();
            String coloringFileName = fileName.replace(".ir", "_colored.s");
            coloringAllocator.createFile("P2Output/" + coloringFileName);
        }

        if(args[0].equals("-t")) {
            for (int j = 1; j < args.length; j++) {
                Runtime rt = Runtime.getRuntime();
                System.out.println("=================" + args[j] + "=================");
                args[j] = args[j].replace(".ir", ".s");
                Process pr = rt.exec("spim -keepstats -f P2Output/" + args[j]);
                BufferedReader stdInput = new BufferedReader(new
                        InputStreamReader(pr.getInputStream()));
                String s = null;
                String stats = "";
                while ((s = stdInput.readLine()) != null) {
                    if (s.contains("Stats")) {
                        String[] temp = s.split("Stats");
                        System.out.println("Result: " + temp[0]);
                        stats += "\nStats" + temp[1];

                    } else {
                        stats += s;
                    }
                }
                System.out.println(stats);
                int i = pr.waitFor();
            }
        }
    }

    public void createFile(String fileName) {
        PrintWriter fileWriter;
        try {
            fileWriter = new PrintWriter(new FileWriter(fileName));
            for (String line : this.mips) {
                fileWriter.println(line);
                if (this.verbose) {
                    System.out.println(line);
                }
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}