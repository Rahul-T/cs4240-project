import java.io.*;
import java.util.*;

public class ColoringAllocator extends Allocator {
    Iterator instrIterator = instrToVarRegs.entrySet().iterator(); 
    Instruction currentInstruction = new Instruction("", "", 0);
    HashSet<String> currentParams;

    public ColoringAllocator(String irFile, boolean isVerbose, LinkedHashMap<Instruction, HashMap<String, String>> instrToVarRegs) {
        super(irFile, isVerbose, instrToVarRegs);
    }

    // Register Section

    @Override
    public String getAvailableRegister(String element) {
        // Todo : Fix for spills

        if(isNumeric(element)) {
            return element;
        }
        
        String register = instrToVarRegs.get(currentInstruction).get(element);
        if(register == null) {
            register = "#" + element;
        }
        if(register.equals("SPILL")) {
            register = "%" + element;
        }
        
        return register;
    }

    @Override
    public void restoreRegisters(String[] registers) {

    }

    @Override
    public void registersToAndFromStack(String currentFunction, String instr) {
        int baseOffset = Integer.valueOf(functionToVarsToOffset.get(currentFunction).get("#total#"));
        int offset = 4;
        for(String reg: instrToVarRegs.get(currentInstruction).values()) {
            int totalOffset = baseOffset + offset;
            mips.add(instr + reg + ", " + totalOffset + "($sp)");
            offset += 4;
        }
    }

    public boolean isStringInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex)
        {
            return false;
        }
    }

    public String[] checkSpills(String[] registers, String currentFunction) {
        String[] newRegs = new String[registers.length];
        for(int i=0; i<registers.length; i++) {
            String register = registers[i];
            if(register.contains("%")) {
                // if(isNumeric(register)) {
                //     mips.add("NUMERIC: " + register);
                // }
                String newReg = "";
                if(isStringInt(register) || getVarType(register.substring(1)).equals("int")) {
                    int regNum = 7 - i;
                    newReg = "$t" + regNum;
                } else {
                    int regNum = 10 - i;
                    newReg = "$f" + regNum;
                }
                String elem = register;
                if(register.charAt(0) == '%') {
                    elem = register.substring(1);
                }
                naiveLoader(getStackLocation(elem, currentFunction), newReg, mips);
                newRegs[i] = newReg;
            } else {
                newRegs[i] = register;
            }
        }
        return newRegs;
    }

    public void restoreSpills(String[] registers, String currentFunction) {
        for(int i=0; i<registers.length; i++) {
            String register = registers[i];
            if(register.contains("%")) {
                String elem = register.substring(1);
                String newReg = "";
                if(getVarType(elem).equals("int")) {
                    int regNum = 7 - i;
                    newReg = "$t" + regNum;
                } else {
                    int regNum = 10 - i;
                    newReg = "$f" + regNum;
                }
                mips.add(getStoreInstrType(newReg) + newReg + ", " + getStackLocation(elem, currentFunction));
            }
        }
    }

    public void paramInitialLoadCheck(String element, String register, String currentFunction) {
        if(currentParams.contains(element)) {
            // mips.add("LOAD PARAM HERE INTO REGISTER " + register);
            if(getVarType(element).equals("int")) {
                mips.add("lw " + register + ", " + getStackLocation(element, currentFunction));
            } else {
                mips.add("l.s " + register + ", " + getStackLocation(element, currentFunction));
            }
            currentParams.remove(element);
        }
    }

    // Instruction Section

    public String[] getInstrArray() {
        return currentInstruction.getText().replaceAll(" ", "").split(",");
    }

    public void naiveLoader(String element, String register, ArrayList<String> mips) {
        if (isNumeric(element)) {
            if(element.contains(".")) {
                mips.add("li.s " + register + ", " + element);
            } else {
                mips.add("li " + register + ", " + element);
            }
        } else {
            if(register.contains("$f")) {
                mips.add("l.s " + register + ", " + element);
            } else {
                mips.add("lw " + register + ", " + element);
            }
        }
    }

    @Override
    public void generateLoad(String element, String register, ArrayList<String> mips, String currentFunction) {
        if (isNumeric(element)) {
            if(element.contains(".")) {
                mips.add("li.s " + register + ", " + element);
            } else {
                mips.add("li " + register + ", " + element);
            }
        } else if (!register.contains("#")){
            String r2 = getAvailableRegister(element);

            String[] originalRegs = new String[] {r2};

            String[] newRegs = checkSpills(originalRegs, currentFunction);
            String reg2 = newRegs[0];

            paramInitialLoadCheck(element, reg2, currentFunction);
            
            if(!reg2.contains("#")) {
                if(register.contains("$f")) {
                    mips.add("mov.s " + register + ", " + reg2);
                } else {
                    mips.add("move " + register + ", " + reg2);
                }
            } else {
                // Load global var
                if(register.contains("$f")) {
                    mips.add("l.s " + register + ", " + reg2.substring(1));
                } else {
                    mips.add("lw " + register + ", " + reg2.substring(1));
                }
            }
            
        } else {
            // Store global var
            String r = getAvailableRegister(element);

            String[] originalRegs = new String[] {r};

            String[] newRegs = checkSpills(originalRegs, currentFunction);
            String reg = newRegs[0];

            paramInitialLoadCheck(element, reg, currentFunction);

            if(getVarType(element).equals("float")) {
                mips.add("s.s " + reg + ", " + register.substring(1));
            } else {
                mips.add("sw " + reg + ", " + register.substring(1));
            }
        }
    }

    @Override
    public void regularAssignInstr(String[] lineElements, String currentFunction) {
        String[] instr = getInstrArray();
        String reg = getAvailableRegister(instr[1]);

        String[] originalRegs = new String[] {reg};

        String[] newRegs = checkSpills(originalRegs, currentFunction);
        String register = newRegs[0];

        generateLoad(instr[2], register, mips, currentFunction);

        restoreSpills(originalRegs, currentFunction); 
    }

    @Override
    public void arraystoreInstr(String[] lineElements, String currentFunction) {
        // mips.add("FIX: " + currentInstruction.getText());

        String[] instr = getInstrArray();

        String arrayAddressRegister = "$t0";
        mips.add("la " + arrayAddressRegister + ", " + lineElements[1]);

        String arrayOffReg = getAvailableRegister(instr[2]);
        String valueReg = getAvailableRegister(instr[3]);

        String[] originalRegs = new String[] {arrayOffReg, valueReg};

        String[] newRegs = checkSpills(originalRegs, currentFunction);
        String arrayOffsetRegister = newRegs[0];
        String valueRegister = newRegs[1];

        paramInitialLoadCheck(instr[2], arrayOffsetRegister, currentFunction);
        paramInitialLoadCheck(instr[3], valueRegister, currentFunction);

        // generateLoad(lineElements[2], arrayOffsetRegister, mips, currentFunction);
        // String arrayOffsetRegister = getAvailableRegister("0");
        // generateLoad(lineElements[2], arrayOffsetRegister, mips, currentFunction);

        if(isNumeric(arrayOffsetRegister)) {
            if(arrayOffsetRegister.contains(".")) {
                mips.add("li.s $f4, " + arrayOffsetRegister);
                arrayOffsetRegister = "$f4";
            } else {
                mips.add("li $t2, " + arrayOffsetRegister);
                arrayOffsetRegister = "$t2";
            }
        }

        mips.add("mulo " + "$t1" + ", " + arrayOffsetRegister + ", " + 4);
        mips.add("add " + arrayAddressRegister + ", " + arrayAddressRegister + ", " + "$t1");

        if(isNumeric(valueRegister)) {
            if(valueRegister.contains(".")) {
                // mips.add("li.s $f5, " + valueRegister);
                valueRegister = "$f5";
            } else {
                // mips.add("li $t3, " + valueRegister);
                valueRegister = "$t3";
            }
        }

        generateLoad(instr[3], valueRegister, mips, currentFunction);
        // String valueRegister = getAvailableRegister(lineElements[3]);
        // generateLoad(lineElements[3], valueRegister, mips, currentFunction);

        mips.add(getStoreInstrType(valueRegister) + valueRegister + ", (" + arrayAddressRegister + ")");
        
        // restoreRegisters(new String[] {valueRegister, arrayOffsetRegister});
        restoreSpills(originalRegs, currentFunction); 
    }

    @Override
    public void arrayloadInstr(String[] lineElements, String currentFunction) {
        // mips.add("FIX: " + currentInstruction.getText());

        String[] instr = getInstrArray();

        String arrayAddressRegister2 = "$t0";
        mips.add("la " + arrayAddressRegister2 + ", " + lineElements[2]);

        String arrayOffReg2 = getAvailableRegister(instr[3]);
        String valueReg2 = getAvailableRegister(instr[1]);

        String[] originalRegs = new String[] {arrayOffReg2, valueReg2};

        String[] newRegs = checkSpills(originalRegs, currentFunction);
        String arrayOffsetRegister2 = newRegs[0];
        String valueRegister2 = newRegs[1];

        paramInitialLoadCheck(instr[3], arrayOffsetRegister2, currentFunction);
        paramInitialLoadCheck(instr[1], valueRegister2, currentFunction);

        if(isNumeric(arrayOffsetRegister2)) {
            mips.add("LOAD");
            if(arrayOffsetRegister2.contains(".")) {
                // mips.add("li.s $f4, " + arrayOffsetRegister2);
                arrayOffsetRegister2 = "$f4";
            } else {
                // mips.add("li $t2, " + arrayOffsetRegister2);
                arrayOffsetRegister2 = "$t2";
            }
        }

        // String arrayOffsetRegister2 = getAvailableRegister("0");
        // generateLoad(lineElements[3], arrayOffsetRegister2, mips, currentFunction);

        mips.add("mulo " + "$t1" + ", " + arrayOffsetRegister2 + ", " + 4);
        mips.add("add " + arrayAddressRegister2 + ", " + arrayAddressRegister2 + ", " + "$t1");

        
        // String valueRegister2 = getAvailableRegister(lineElements[1]);
        if(isNumeric(valueRegister2)) {
            if(valueRegister2.contains(".")) {
                mips.add("li.s $f5, " + valueRegister2);
                valueRegister2 = "$f5";
            } else {
                mips.add("li $t3, " + valueRegister2);
                valueRegister2 = "$t3";
            }
        }

        if(valueRegister2.contains("$f")) {
            mips.add("l.s " + valueRegister2 + ", (" + arrayAddressRegister2 + ")");
        } else {
            mips.add("lw " + valueRegister2 + ", (" + arrayAddressRegister2 + ")");
        }

        restoreSpills(originalRegs, currentFunction); 
        // mips.add(getStoreInstrType(valueRegister2) + valueRegister2 + ", " + getStackLocation(lineElements[1], currentFunction));
        
        // restoreRegisters(new String[] {valueRegister2, arrayOffsetRegister2});
    }

    @Override
    public void opInstr(String[] lineElements, String currentFunction) {
        String[] instr = getInstrArray();
        String opReg1 = getAvailableRegister(instr[1]);
        String opReg2 = getAvailableRegister(instr[2]);
        String resReg = getAvailableRegister(instr[3]);
        String[] originalRegs = new String[] {opReg1, opReg2, resReg};

        String[] newRegs = checkSpills(originalRegs, currentFunction);
        String operandRegister1 = newRegs[0];
        String operandRegister2 = newRegs[1];
        String resultRegister = newRegs[2];

        paramInitialLoadCheck(instr[1], operandRegister1, currentFunction);
        paramInitialLoadCheck(instr[2], operandRegister2, currentFunction);
        paramInitialLoadCheck(instr[3], resultRegister, currentFunction);

        if(lineElements[0].equals("mult")) {
            if(resultRegister.contains("$f")) {
                mips.add("mul.s " + resultRegister + ", " + operandRegister1 + ", " + operandRegister2);
            } else {
                mips.add("mul " + resultRegister + ", " + operandRegister1 + ", " + operandRegister2);
            }
        } else {
            if(resultRegister.contains("$f")) {
                mips.add(lineElements[0] + ".s " + resultRegister + ", " + operandRegister1 + ", " + operandRegister2);
            } else {
                mips.add(lineElements[0] + " " + resultRegister + ", " + operandRegister1 + ", " + operandRegister2);
            }
        }
        restoreSpills(originalRegs, currentFunction);        
    }

    @Override
    public void branchInstr(String[] lineElements, String currentFunction) {
        String[] instr = getInstrArray();
        String firstReg= getAvailableRegister(instr[1]);
        String secondReg = getAvailableRegister(instr[2]);

        String[] originalRegs = new String[] {firstReg, secondReg};

        String[] newRegs = checkSpills(originalRegs, currentFunction);
        String firstRegister = newRegs[0];
        String secondRegister = newRegs[1];

        paramInitialLoadCheck(instr[1], firstRegister, currentFunction);
        paramInitialLoadCheck(instr[2], secondRegister, currentFunction);

        if(firstRegister.contains("$f") || secondRegister.contains("$f")) {
            switch(lineElements[0].replaceAll(" ", "")) {
                case "breq":
                    mips.add("c.eq.s " + firstRegister + ", " + secondRegister);
                    mips.add("bc1t " + lineElements[3]);
                    break;
                case "brneq":
                    mips.add("c.eq.s " + firstRegister + ", " + secondRegister);
                    mips.add("bc1f " + lineElements[3]);
                    break;
                case "brlt":
                    mips.add("c.lt.s " + firstRegister + ", " + secondRegister);
                    mips.add("bc1t " + lineElements[3]);
                    break;
                case "brgt":
                    mips.add("c.le.s " + firstRegister + ", " + secondRegister);
                    mips.add("bc1f " + lineElements[3]);
                    break;
                case "brgeq":
                    mips.add("c.lt.s " + firstRegister + ", " + secondRegister);
                    mips.add("bc1f " + lineElements[3]);
                    break;
                case "brleq":
                    mips.add("c.le.s " + firstRegister + ", " + secondRegister);
                    mips.add("bc1t " + lineElements[3]);
                    break;
            }
        } else {
            lineElements[0] = lineElements[0].replace("breq", "beq");
            lineElements[0] = lineElements[0].replace("brneq", "bne");
            lineElements[0] = lineElements[0].replace("brlt", "blt");
            lineElements[0] = lineElements[0].replace("brgt", "bgt");
            lineElements[0] = lineElements[0].replace("brgeq", "bge");
            lineElements[0] = lineElements[0].replace("brleq", "ble");
            mips.add(lineElements[0] + " " + firstRegister + ", " + secondRegister + ", " + lineElements[3]);
        }

        restoreSpills(originalRegs, currentFunction);
    }

    public Instruction getNextInstruction() {
        Map.Entry<Instruction, HashMap<String, String>> instrElement = (Map.Entry<Instruction, HashMap<String, String>>)instrIterator.next(); 
        return instrElement.getKey();
    }

    // Main Section

    @Override
    public void buildTextSection() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.irFile));
        String line;
        String currentFunction = "";
        HashMap<String, Integer> maxAdditionalOffset = new HashMap<String, Integer>();

        mips.add(".text");

        while ((line = br.readLine()) != null) {
            // Comment
            if(line.contains("#")) {
                continue;
            }

            // Function header
            else if(line.contains("(") && line.contains(")")) {
                currentFunction = line.substring(line.indexOf(" ")+1, line.indexOf("("));
                maxAdditionalOffset.put(currentFunction, 0);
                setupFunction(currentFunction);
                currentParams = getParamsFromRegisters(line, currentFunction);

                currentInstruction = getNextInstruction();
                // setAllRegistersToInactive();
            }

            // Regular statement
            else if(line.contains(",")) {
                String[] lineElements = line.trim().split(",");

                if (lineElements.length == 0) { 
                    continue;
                }
                
                switch (lineElements[0]) {
                    case "assign":
                        // System.out.println("Line: " + line);
                        currentInstruction = getNextInstruction();
                        // System.out.println(currentInstruction.getText());
                        assignInstr(lineElements, currentFunction);
                        break;
                    
                    case "array_store":
                        // System.out.println("Line: " + line);
                        currentInstruction = getNextInstruction();
                        // System.out.println(currentInstruction.getText());
                        arraystoreInstr(lineElements, currentFunction);
                        break;
                    
                    case "array_load":
                        // System.out.println("Line: " + line);
                        currentInstruction = getNextInstruction();
                        // System.out.println(currentInstruction.getText());
                        arrayloadInstr(lineElements, currentFunction);
                        break;

                    case "add":
                    case "sub":
                    case "mult":
                    case "div":
                    case "and":
                    case "or":
                        // System.out.println("Line: " + line);
                        currentInstruction = getNextInstruction();
                        // System.out.println(currentInstruction.getText());
                        opInstr(lineElements, currentFunction);
                        break;
                    
                    case "goto":
                        // System.out.println("Line: " + line);
                        currentInstruction = getNextInstruction();
                        // System.out.println(currentInstruction.getText());
                        mips.add("j " + lineElements[1]);
                        break;

                    case "breq":
                    case "brneq":
                    case "brlt":
                    case "brgt":
                    case "brgeq":
                    case "brleq":
                        // System.out.println("Line: " + line);
                        currentInstruction = getNextInstruction();
                        // System.out.println(currentInstruction.getText());
                        branchInstr(lineElements, currentFunction);
                        break;

                    case "return":
                        // System.out.println("Line: " + line);
                        currentInstruction = getNextInstruction();
                        // System.out.println(currentInstruction.getText());
                        returnInstr(lineElements, currentFunction);
                        break;

                    case "callr":
                        // System.out.println("Line: " + line);
                        currentInstruction = getNextInstruction();
                        // System.out.println(currentInstruction.getText());
                        int totalsize = instrToVarRegs.get(currentInstruction).size();
                        maxAdditionalOffset.put(currentFunction, Math.max(maxAdditionalOffset.get(currentFunction), totalsize));
                        callrInstr(lineElements, currentFunction);

                        String reg = getAvailableRegister(lineElements[1].trim());

                        String[] originalRegs = new String[] {reg};

                        String[] newRegs = checkSpills(originalRegs, currentFunction);
                        String finalReg = newRegs[0];
                        if(finalReg.contains("$f")) {
                            mips.add("l.s " + finalReg + ", " + getStackLocation(lineElements[1].trim(), currentFunction));
                        } else {
                            mips.add("lw " + finalReg + ", " + getStackLocation(lineElements[1].trim(), currentFunction));
                        }
                        break;

                    case "call":
                        // System.out.println("Line: " + line);
                        currentInstruction = getNextInstruction();
                        // // System.out.println(instrToVarRegs.get(currentInstruction));
                        // System.out.println(currentInstruction.getText());
                        int totalsize2 = instrToVarRegs.get(currentInstruction).size();
                        callInstr(lineElements, currentFunction, totalsize2, maxAdditionalOffset);
                        break;
                }
            }
            // Label
            else {
                if (line.length() > 0 
                    && line.contains(":")
                    && !line.contains("int-list:")
                    && !line.contains("float-list:")) {
                        // System.out.println("Line: " + line);
                        String rawLabel = line.substring(0, line.indexOf(":")).trim();
                        currentInstruction = getNextInstruction();
                        // System.out.println(currentInstruction.getText());
                        if(!functionToVarsToType.keySet().contains(rawLabel)) {
                            mips.add(line);
                        }
                }
            }
        }

        for(int i=0; i<mips.size(); i++) {
            String[] instr = mips.get(i).split(" ");
            if(instr.length > 0 && instr[0].equals("@")) {
                String function = instr[1];
                String addiOrSub = instr[2];
                int oldOffset = Integer.valueOf(functionToVarsToOffset.get(function).get("#total#"));
                int newOffset = oldOffset + Integer.valueOf(maxAdditionalOffset.get(function)) * 4;
                String replacementInstr = addiOrSub + " $sp, $sp, " + newOffset;
                mips.set(i, replacementInstr);
            }
        }
    }
}