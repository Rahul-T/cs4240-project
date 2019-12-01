import java.io.*;
import java.util.*;

public class NaiveAllocator extends Allocator {
    PriorityQueue<Integer> sRegistersInactive;
    PriorityQueue<Integer> fRegistersInactive;
    LinkedHashSet<String> sRegistersActive;
    LinkedHashSet<String> fRegistersActive;
    
    public NaiveAllocator(String irFile, boolean isVerbose) {
        super(irFile, isVerbose, null);
    }

    // Register Section

    public String getIntRegister() {
        String register = "$s" + String.valueOf(sRegistersInactive.poll());
        sRegistersActive.add(register);
        return register;
    }

    public String getFloatRegister() {
        String register = "$f" + String.valueOf(fRegistersInactive.poll());
        fRegistersActive.add(register);
        return register;
    }

    @Override
    public String getAvailableRegister(String element) {
        element = element.trim();
        if(isNumeric(element)) {
            if(element.contains(".")) {
                return getFloatRegister();
            } else {
                return getIntRegister();
            }
        } else {
            String type = getVarType(element);
            if(type.contains("int")) {
                return getIntRegister();
            } else if(type.contains("float")) {
                return getFloatRegister();
            }
        }
        System.out.println("Error for variable: " + element);
        return "error";
    }

    @Override
    public void restoreRegisters(String[] registers) {
        for(String register: registers) {
            if(register.contains("$f")) {
                fRegistersInactive.add(Integer.valueOf(register.substring(2)));
            } else if(register.contains("$s")) {
                sRegistersInactive.add(Integer.valueOf(register.substring(2)));
            } else {
                System.out.println("restore error");
            }
        }
    }

    @Override
    public void registersToAndFromStack(String currentFunction, String instr) {
        int baseOffset = Integer.valueOf(functionToVarsToOffset.get(currentFunction).get("#total#"));
        int offset = 4;
        for(String reg: sRegistersActive) {
            int totalOffset = baseOffset + offset;
            mips.add(instr + reg + ", " + totalOffset + "($sp)");
            offset += 4;
        }
        for(String reg: fRegistersActive) {
            int totalOffset = baseOffset + offset;
            mips.add(instr + reg + ", " + totalOffset + "($sp)");
            offset += 4;
        }
    }

    public void setAllRegistersToInactive() {
        sRegistersInactive = new PriorityQueue<Integer>();
        fRegistersInactive = new PriorityQueue<Integer>();
        for(int i=0; i<=7; i++) {
            sRegistersInactive.add(i);
        }
        for(int i=20; i<=31; i++) {
            fRegistersInactive.add(i);
        }
        sRegistersActive = new LinkedHashSet<String>();
        fRegistersActive = new LinkedHashSet<String>();
    }

    // Instruction Section

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
                mips.add("l.s " + register + ", " + getStackLocation(element, currentFunction));
            } else {
                mips.add("lw " + register + ", " + getStackLocation(element, currentFunction));
            }
        }
    }

    @Override
    public void regularAssignInstr(String[] lineElements, String currentFunction) {
        String register = getAvailableRegister(lineElements[2]);
        generateLoad(lineElements[2], register, mips, currentFunction);
        mips.add(getStoreInstrType(register) + register + ", " + getStackLocation(lineElements[1], currentFunction));
        restoreRegisters(new String[] {register});
    }

    @Override
    public void arraystoreInstr(String[] lineElements, String currentFunction) {
        String arrayAddressRegister = "$t0";
        mips.add("la " + arrayAddressRegister + ", " + lineElements[1]);

        String arrayOffsetRegister = getAvailableRegister("0");
        generateLoad(lineElements[2], arrayOffsetRegister, mips, currentFunction);
        mips.add("mulo " + arrayOffsetRegister + ", " + arrayOffsetRegister + ", " + 4);
        mips.add("add " + arrayAddressRegister + ", " + arrayAddressRegister + ", " + arrayOffsetRegister);

        String valueRegister = getAvailableRegister(lineElements[3]);
        generateLoad(lineElements[3], valueRegister, mips, currentFunction);

        mips.add(getStoreInstrType(valueRegister) + valueRegister + ", (" + arrayAddressRegister + ")");
        
        restoreRegisters(new String[] {valueRegister, arrayOffsetRegister});
    }

    @Override
    public void arrayloadInstr(String[] lineElements, String currentFunction) {
        String arrayAddressRegister2 = "$t0";
        mips.add("la " + arrayAddressRegister2 + ", " + lineElements[2]);

        String arrayOffsetRegister2 = getAvailableRegister("0");
        generateLoad(lineElements[3], arrayOffsetRegister2, mips, currentFunction);
        mips.add("mulo " + arrayOffsetRegister2 + ", " + arrayOffsetRegister2 + ", " + 4);
        mips.add("add " + arrayAddressRegister2 + ", " + arrayAddressRegister2 + ", " + arrayOffsetRegister2);

        String valueRegister2 = getAvailableRegister(lineElements[1]);

        if(valueRegister2.contains("$f")) {
            mips.add("l.s " + valueRegister2 + ", (" + arrayAddressRegister2 + ")");
        } else {
            mips.add("lw " + valueRegister2 + ", (" + arrayAddressRegister2 + ")");
        }
        
        mips.add(getStoreInstrType(valueRegister2) + valueRegister2 + ", " + getStackLocation(lineElements[1], currentFunction));
        
        restoreRegisters(new String[] {valueRegister2, arrayOffsetRegister2});
    }

    @Override
    public void opInstr(String[] lineElements, String currentFunction) {
        String operandRegister1 = getAvailableRegister(lineElements[1]);
        generateLoad(lineElements[1], operandRegister1, mips, currentFunction);

        String operandRegister2 = getAvailableRegister(lineElements[2]);
        generateLoad(lineElements[2], operandRegister2, mips, currentFunction);
        
        String resultRegister = "";
        if(operandRegister1.contains("$f") || operandRegister2.contains("$f")) {
            resultRegister = getAvailableRegister("0.0");
        } else {
            resultRegister = getAvailableRegister("0");
        }

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

        mips.add(getStoreInstrType(resultRegister) + resultRegister + ", " + getStackLocation(lineElements[3], currentFunction));
        

        restoreRegisters(new String[] {operandRegister1, operandRegister2, resultRegister});
    }

    @Override
    public void branchInstr(String[] lineElements, String currentFunction) {
        String firstRegister = getAvailableRegister(lineElements[1]);
        generateLoad(lineElements[1], firstRegister, mips, currentFunction);
        String secondRegister = getAvailableRegister(lineElements[2]);
        generateLoad(lineElements[2], secondRegister, mips, currentFunction);

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
        }
        else {
            lineElements[0] = lineElements[0].replace("breq", "beq");
            lineElements[0] = lineElements[0].replace("brneq", "bne");
            lineElements[0] = lineElements[0].replace("brlt", "blt");
            lineElements[0] = lineElements[0].replace("brgt", "bgt");
            lineElements[0] = lineElements[0].replace("brgeq", "bge");
            lineElements[0] = lineElements[0].replace("brleq", "ble");
            mips.add(lineElements[0] + " " + firstRegister + ", " + secondRegister + ", " + lineElements[3]);
        }
        
        restoreRegisters(new String[] {firstRegister, secondRegister});
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
                setAllRegistersToInactive();
            }

            // Regular statement
            else if(line.contains(",")) {
                String[] lineElements = line.trim().split(",");

                if (lineElements.length == 0) { 
                    continue;
                }
                
                switch (lineElements[0]) {
                    case "assign":
                        assignInstr(lineElements, currentFunction);
                        break;
                    
                    case "array_store":
                        arraystoreInstr(lineElements, currentFunction);
                        break;
                    
                    case "array_load":
                        arrayloadInstr(lineElements, currentFunction);
                        break;

                    case "add":
                    case "sub":
                    case "mult":
                    case "div":
                    case "and":
                    case "or":
                        opInstr(lineElements, currentFunction);
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
                        branchInstr(lineElements, currentFunction);
                        break;

                    case "return":
                        returnInstr(lineElements, currentFunction);
                        break;

                    case "callr":
                        int totalsize = sRegistersActive.size() + fRegistersActive.size();
                        maxAdditionalOffset.put(currentFunction, Math.max(maxAdditionalOffset.get(currentFunction), totalsize));
                        callrInstr(lineElements, currentFunction);
                        break;

                    case "call":
                        int totalsize2 = sRegistersActive.size() + fRegistersActive.size();
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
                        String rawLabel = line.substring(0, line.indexOf(":")).trim();
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
