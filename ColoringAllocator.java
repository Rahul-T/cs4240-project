import java.io.*;
import java.util.*;

public class ColoringAllocator extends Allocator {
    Iterator instrIterator = instrToVarRegs.entrySet().iterator(); 
    Instruction currentInstruction = new Instruction("", "", 0);

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
        return instrToVarRegs.get(currentInstruction).get(element);
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

    // Instruction Section

    public String[] getInstrArray() {
        return currentInstruction.getText().replaceAll(" ", "").split(",");
    }

    @Override
    public void generateLoad(String element, String register, ArrayList<String> mips, String currentFunction) {
        if (isNumeric(element)) {
            if(element.contains(".")) {
                mips.add("li.s " + register + ", " + element);
            } else {
                mips.add("li " + register + ", " + element);
            }
        } else {
            if(register.contains("$f")) {
                mips.add("l.s " + register + ", " + getAvailableRegister(element));
            } else {
                mips.add("move " + register + ", " + getAvailableRegister(element));
            }
        }
    }

    @Override
    public void regularAssignInstr(String[] lineElements, String currentFunction) {
        String[] instr = getInstrArray();
        String register = getAvailableRegister(instr[1]);
        generateLoad(instr[2], register, mips, currentFunction);
    }

    @Override
    public void arraystoreInstr(String[] lineElements, String currentFunction) {
        // String arrayAddressRegister = "$t0";
        // mips.add("la " + arrayAddressRegister + ", " + lineElements[1]);

        // String arrayOffsetRegister = getAvailableRegister("0");
        // generateLoad(lineElements[2], arrayOffsetRegister, mips, currentFunction);
        // mips.add("mulo " + arrayOffsetRegister + ", " + arrayOffsetRegister + ", " + 4);
        // mips.add("add " + arrayAddressRegister + ", " + arrayAddressRegister + ", " + arrayOffsetRegister);

        // String valueRegister = getAvailableRegister(lineElements[3]);
        // generateLoad(lineElements[3], valueRegister, mips, currentFunction);

        // mips.add(getStoreInstrType(valueRegister) + valueRegister + ", (" + arrayAddressRegister + ")");
        
        // restoreRegisters(new String[] {valueRegister, arrayOffsetRegister});
    }

    @Override
    public void arrayloadInstr(String[] lineElements, String currentFunction) {
        // String arrayAddressRegister2 = "$t0";
        // mips.add("la " + arrayAddressRegister2 + ", " + lineElements[2]);

        // String arrayOffsetRegister2 = getAvailableRegister("0");
        // generateLoad(lineElements[3], arrayOffsetRegister2, mips, currentFunction);
        // mips.add("mulo " + arrayOffsetRegister2 + ", " + arrayOffsetRegister2 + ", " + 4);
        // mips.add("add " + arrayAddressRegister2 + ", " + arrayAddressRegister2 + ", " + arrayOffsetRegister2);

        // String valueRegister2 = getAvailableRegister(lineElements[1]);

        // if(valueRegister2.contains("$f")) {
        //     mips.add("l.s " + valueRegister2 + ", (" + arrayAddressRegister2 + ")");
        // } else {
        //     mips.add("lw " + valueRegister2 + ", (" + arrayAddressRegister2 + ")");
        // }
        
        // mips.add(getStoreInstrType(valueRegister2) + valueRegister2 + ", " + getStackLocation(lineElements[1], currentFunction));
        
        // restoreRegisters(new String[] {valueRegister2, arrayOffsetRegister2});
    }

    @Override
    public void opInstr(String[] lineElements, String currentFunction) {
        String[] instr = getInstrArray();
        String operandRegister1 = getAvailableRegister(instr[1]);
        String operandRegister2 = getAvailableRegister(instr[2]);
        String resultRegister = getAvailableRegister(instr[3]);

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
    }

    @Override
    public void branchInstr(String[] lineElements, String currentFunction) {
        String[] instr = getInstrArray();
        String firstRegister = getAvailableRegister(instr[1]);
        String secondRegister = getAvailableRegister(instr[2]);

        lineElements[0] = lineElements[0].replace("breq", "beq");
        lineElements[0] = lineElements[0].replace("brneq", "bne");
        lineElements[0] = lineElements[0].replace("brlt", "blt");
        lineElements[0] = lineElements[0].replace("brgt", "bgt");
        lineElements[0] = lineElements[0].replace("brgeq", "bge");
        lineElements[0] = lineElements[0].replace("brleq", "ble");

        mips.add(lineElements[0] + " " + firstRegister + ", " + secondRegister + ", " + lineElements[3]);
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
                getParamsFromRegisters(line, currentFunction);
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
                        break;

                    case "call":
                        // System.out.println("Line: " + line);
                        currentInstruction = getNextInstruction();
                        // System.out.println(instrToVarRegs.get(currentInstruction));
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