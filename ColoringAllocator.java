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
        return "";
    }

    @Override
    public void restoreRegisters(String[] registers) {

    }

    @Override
    public void registersToAndFromStack(String currentFunction, String instr) {
        
    }

    // Instruction Section

    @Override
    public void regularAssignInstr(String[] lineElements, String currentFunction) {
    }

    @Override
    public void arraystoreInstr(String[] lineElements, String currentFunction) {

    }

    @Override
    public void arrayloadInstr(String[] lineElements, String currentFunction) {

    }

    @Override
    public void opInstr(String[] lineElements, String currentFunction) {

    }

    @Override
    public void branchInstr(String[] lineElements, String currentFunction) {
    }

    @Override
    public void callrInstr(String[] lineElements, String currentFunction) {

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
                        // int totalsize = sRegistersActive.size() + fRegistersActive.size();
                        // maxAdditionalOffset.put(currentFunction, Math.max(maxAdditionalOffset.get(currentFunction), totalsize));
                        callrInstr(lineElements, currentFunction);
                        break;

                    case "call":
                        // System.out.println("Line: " + line);
                        currentInstruction = getNextInstruction();
                        // System.out.println(currentInstruction.getText());
                        // int totalsize2 = sRegistersActive.size() + fRegistersActive.size();
                        // callInstr(lineElements, currentFunction, totalsize2, maxAdditionalOffset);
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