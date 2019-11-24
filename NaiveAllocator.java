import java.io.*;
import java.util.*;

public class NaiveAllocator extends Allocator {
    PriorityQueue<Integer> sRegistersInactive;
    PriorityQueue<Integer> fRegistersInactive;
    LinkedHashSet<String> sRegistersActive;
    LinkedHashSet<String> fRegistersActive;
    
    public NaiveAllocator(String irFile, boolean isVerbose) {
        super(irFile, isVerbose);
    }

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
