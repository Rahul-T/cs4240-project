import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

public class CFGGenerator {
    private BasicBlock entryBlock;
    private HashMap<String, BasicBlock> functionBlocks;
    private HashSet<BasicBlock> blocks;
    private HashSet<BasicBlock> loopBlocks;
    private HashMap<String, BasicBlock> labelMap;
    private HashMap<String, HashSet<LiveRange>> webs;
    private String sourceFile;
    private String[] intList, floatList;

    private static final String[] opArr = {"add", "sub", "mult", "div", "and", "or"};
    private static final String[] brArr = {"breq", "brneq", "brlt", "brgt", "brgeq", "brleq"};
    private static final HashSet<String> binOps = new HashSet<String>(Arrays.asList(opArr));
    private static final HashSet<String> branches = new HashSet<String>(Arrays.asList(brArr));

    private HashMap<Instruction, String> instructionMap;

    public CFGGenerator(String sourceFile) {
        this.sourceFile = sourceFile;
        this.functionBlocks = new HashMap<String, BasicBlock>();
        this.webs = new HashMap<String, HashSet<LiveRange>>();
        this.blocks = new HashSet<BasicBlock>();
        this.loopBlocks = new HashSet<BasicBlock>();
        this.labelMap = new HashMap<String, BasicBlock>();
        this.entryBlock = null;
        this.instructionMap = null;
    }

    public void generateBlocks() throws IOException {

        // create a buffered reader for the file
        FileReader fileReader = new FileReader(this.sourceFile);
        BufferedReader fileBuff = new BufferedReader(fileReader);

        int blockCount = 0;
        String lastLine=null, currentLine=null, nextLine=null, label=null;
        String[] tokCurrentLine = null, tokLastLine=null;
        BasicBlock targetBlock, newBlock;
        BasicBlock currentBlock = genBasicBlock("main");

        this.entryBlock = currentBlock;
        this.functionBlocks.put("main", this.entryBlock);


        // read header lines and fill the line pipeline

        fileBuff.readLine();
        lastLine = fileBuff.readLine();
        tokLastLine = lastLine.split(" ");
        currentLine = fileBuff.readLine(); // int list line
        nextLine = fileBuff.readLine(); // float list line

        // clean up intList and floatList lines
        String arrayPattern = "\\[\\d+\\]";
        String intListLine = currentLine.replace("int-list:", "").replaceAll(arrayPattern, "").trim();
        String floatListLine = nextLine.replace("float-list:", "").trim();

        if (intListLine.length() > 1 && intListLine.charAt(intListLine.length() - 1) == ',')
            intListLine = intListLine.substring(0, intListLine.length() - 1);

        if (floatListLine.length() > 1 && floatListLine.charAt(floatListLine.length() - 1) == ',')
            floatListLine = floatListLine.substring(0, floatListLine.length() - 1);
        
        this.intList = intListLine.split(", ");
        this.floatList = floatListLine.split(", ");

        // catch case where there are no variables;
        if (this.intList.length == 1 && this.intList[0].length() == 0) {
            this.intList = new String[0];
        }

        if (this.floatList.length == 1 && this.floatList[0].length() == 0) {
            this.floatList = new String[0];
        }

        // process the file
        while (currentLine != null) {
            tokCurrentLine = currentLine.trim().split(" ");
            
            // catch labels
            if (tokCurrentLine[0].length() == 0) {
                String[] temp = new String[tokCurrentLine.length - 1];
                for (int i = 1; i < tokCurrentLine.length; i++) {
                    temp[i-1] = tokCurrentLine[i];
                }
                tokCurrentLine = temp;
            }
            if (tokCurrentLine[0].equals("int-list:") || tokCurrentLine[0].equals("float-list:"));
            else if (tokCurrentLine.length == 1) {
                label = tokCurrentLine[0].substring(0, tokCurrentLine[0].length()-1);
                if (!this.labelMap.containsKey(label)) {
                    this.labelMap.put(label, genBasicBlock(label));
                }
                targetBlock = this.labelMap.get(label);
                if (!lastLine.split(" ")[0].equals("goto,")) {
                    currentBlock.addSuccessor(targetBlock);
                    targetBlock.addPredecessor(currentBlock);
                }
                currentBlock = targetBlock;
                currentBlock.addLine(currentLine);
            }

            // catch function starts and ends
            else if (tokCurrentLine[0].contains("#")) {

                if (tokCurrentLine[0].equals("#start_function")) {
                    currentBlock = genBasicBlock(tokCurrentLine[2]);
                    this.functionBlocks.put(currentBlock.getBlockName(), currentBlock);
                    lastLine = nextLine;                 // skip declaration line
                    currentLine = fileBuff.readLine();   // put args line
                    nextLine = fileBuff.readLine();      // read in first action line
                } else {
                    currentBlock = entryBlock;
                }

            }

            // catch branches and gotos
            else if (tokCurrentLine[0].contains("goto")) {
                currentBlock.addLine(currentLine);
                label = tokCurrentLine[1].substring(0, tokCurrentLine[1].length()-1);
                if (!this.labelMap.containsKey(label)) this.labelMap.put(label, genBasicBlock(label));
                targetBlock = this.labelMap.get(label);

                currentBlock.addSuccessor(targetBlock);
                targetBlock.addPredecessor(currentBlock);

                currentBlock = targetBlock;
            } else if (branches.contains(tokCurrentLine[0].replace(",", ""))) {
                currentBlock.addLine(currentLine);
                label = tokCurrentLine[3];
                if (!this.labelMap.containsKey(label)) this.labelMap.put(label, genBasicBlock(label));
                targetBlock = this.labelMap.get(label);

                currentBlock.addSuccessor(targetBlock);
                targetBlock.addPredecessor(currentBlock);

                newBlock = genBasicBlock(Integer.toString(blockCount++));
                currentBlock.addSuccessor(newBlock);
                newBlock.addPredecessor(currentBlock);

                currentBlock = newBlock;
            }

            // otherwise, just add the line
            else {
                currentBlock.addLine(currentLine);
            }

            lastLine = currentLine;
            currentLine = nextLine;
            nextLine = fileBuff.readLine();
        }

        fileBuff.close();
        mapInstructions(this.entryBlock);

        for (BasicBlock block : this.blocks) {
            if (block.getPredecessors().size() == 1 && block.getSuccessors().size() == 1 
                && ((BasicBlock)block.getSuccessors().toArray()[0]).getSuccessors().contains(block)) {
                // this block has only one successor, which also has this block as a successor...aka a loop!
                this.loopBlocks.add(block);
                this.loopBlocks.addAll(block.getSuccessors());
            }
        }
    }

    public void generateInOutSets() {
        // track all blocks where control terminates (i.e. no successors)
        HashSet<BasicBlock> outBlocks = new HashSet<BasicBlock>();
        for (BasicBlock cBlock : blocks) {
            if (cBlock.getSuccessors().isEmpty()) outBlocks.add(cBlock);
        }

        boolean temp;
        do {
            temp = false;
            for (BasicBlock b : blocks) {

                temp = temp || updateInOutSet(b);
            }
        } while (temp);
    }

    // calculates the in and out sets for a single block
    // returns true if the in or out set is changed.
    private boolean updateInOutSet(BasicBlock block) {
        boolean changed;
        int i,j;
        HashSet<String> tempBlockIn = new HashSet<String>();
        HashSet<String> tempBlockOut = new HashSet<String>();

        ArrayList<Instruction> lines;
        Instruction currLine;
        String[] tokenizedLine;
        String currOp;

        for (BasicBlock b : block.getSuccessors()) {
            tempBlockOut.addAll(b.inSet);
        }

        lines = block.getLines();
        for (i = lines.size()-1; i >= 0; i--) {
            HashSet<String> tempInstIn = new HashSet<String>();
            HashSet<String> tempInstOut = new HashSet<String>();
            HashSet<String> def = new HashSet<String>();
            HashSet<String> use = new HashSet<String>();

            currLine = lines.get(i);
            tokenizedLine = currLine.getText().replace(",", "").split(" ");
            currOp = tokenizedLine[0];

            if (i == lines.size() - 1)
                tempInstOut.addAll(tempBlockOut);
            else {
                tempInstOut.addAll(lines.get(i+1).inSet);
            }

            if (currOp.equals("assign")) { // handle assigns
                def.add(tokenizedLine[1]);
                addIfNotNumeric(tokenizedLine[tokenizedLine.length - 1], use);
            } else if (binOps.contains(currOp)) { // handle binary ops
                def.add(tokenizedLine[3]);
                addIfNotNumeric(tokenizedLine[1], use);
                addIfNotNumeric(tokenizedLine[2], use);
            } else if (branches.contains(currOp)) { // handle branches
                addIfNotNumeric(tokenizedLine[1], use);
                addIfNotNumeric(tokenizedLine[2], use);
            } else if (currOp.equals("return")) {
                if (tokenizedLine.length > 1)
                    addIfNotNumeric(tokenizedLine[1], use);
            } else if (currOp.equals("call")) {
                for (j = 2; j < tokenizedLine.length; j++) {
                    addIfNotNumeric(tokenizedLine[j], use);
                }
            } else if (currOp.equals("callr")) {
                addIfNotNumeric(tokenizedLine[1], def);
                for (j = 3; j < tokenizedLine.length; j++) {
                    addIfNotNumeric(tokenizedLine[j], use);
                }
            } else if (currOp.equals("array_load")) {
                addIfNotNumeric(tokenizedLine[1], def);
                addIfNotNumeric(tokenizedLine[2], use);
                addIfNotNumeric(tokenizedLine[3], use);
            } else if (currOp.equals("array_store")) {
                addIfNotNumeric(tokenizedLine[1], def);
                addIfNotNumeric(tokenizedLine[2], use);
                addIfNotNumeric(tokenizedLine[3], use);
            }

            
            // in = (out - def) U use
            tempInstIn.addAll(tempInstOut);
            tempInstIn.removeAll(def);
            tempInstIn.addAll(use);
            currLine.inSet = tempInstIn;
            currLine.outSet = tempInstOut;
            currLine.defs = def;
            currLine.uses = use;

            // System.out.println(String.format("LINE: %s | IN: %s | OUT: %s | DEF: %s | USE: %s", currLine.getText(), currLine.inSet, currLine.outSet, def, use));
        }

        // System.out.println(block.getBlockName() + ": " + lines);
        tempBlockIn = lines.get(0).inSet;
        
        changed = (!tempBlockIn.equals(block.inSet)) || (!tempBlockOut.equals(block.outSet));
        
        block.inSet = tempBlockIn;
        block.outSet = tempBlockOut;

        // System.out.println(String.format("BLOCK IN: %s | BLOCK OUT: %s | CHANGED: %s", block.inSet, block.outSet, Boolean.toString(changed)));
        return changed;
    }

    public void createLiveRanges() {
        Instruction lastUse;
        HashSet<Instruction> incInstrs = new HashSet<Instruction>();
        HashMap<String, Integer> visits = new HashMap<String, Integer>();

        for (String var : intList) {
            this.webs.put(var, new HashSet<LiveRange>());
            
        }

        for (String var : floatList) {
            this.webs.put(var, new HashSet<LiveRange>());
            
        }

        for (String var : this.webs.keySet()) {
            incInstrs.clear();
            for (BasicBlock block : blocks) {
                visits.put(block.getBlockName(), 0);
            }
            lastUse = null;
            traverseCFG(this.entryBlock, new LiveRange(var), lastUse, incInstrs, visits);
        }
    }


    private void traverseCFG(BasicBlock root, LiveRange range, 
        Instruction lastUse, HashSet<Instruction> incInstrs, 
        HashMap<String, Integer> visits) {

        String var = range.getVarName();
        
        // iterate through the block's instructions
        for (Instruction inst : root.getLines()) {
            if (inst.inSet.contains(var)) range.instructions.add(inst);
            if (inst.defs.contains(var)) {
                range = new LiveRange(var);
                range.instructions.add(inst);
                this.webs.get(var).add(range);
            }
        }

        // recursively traverse
        if (root.getSuccessors().isEmpty() 
        || visits.get(root.getBlockName()) > 1) {
            return;
        }

        visits.put(root.getBlockName(), visits.get(root.getBlockName()) + 1);

        for (BasicBlock b : root.getSuccessors()) {
            traverseCFG(b, range, lastUse, incInstrs, visits);
        }
    }

    public BasicBlock getEntryBlock() {
        return this.entryBlock;
    }

    public HashMap<String,BasicBlock> getFunctionBlocks() {
        return this.functionBlocks;
    }

    public HashSet<BasicBlock> getBlocks() {
        return this.blocks;
    }

    public HashSet<BasicBlock> getLoopBlocks() {
        return this.loopBlocks;
    }

    public HashMap<String, HashSet<LiveRange>> getWebs() {
        return this.webs;
    }

    public String[] getIntList() {
        return this.intList;
    }

    public String[] getFloatList() {
        return this.floatList;
    }

    private BasicBlock genBasicBlock(String name) {
        BasicBlock newBlock = new BasicBlock(name);
        this.blocks.add(newBlock);
        return newBlock;
    }

    private void addIfNotNumeric(String str, HashSet<String> set) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException e) {
            set.add(str);
        }
    }

    private void mapInstructions(BasicBlock entry) {
        instructionMap = new HashMap<Instruction, String>();
        HashSet<BasicBlock> visited = new HashSet<BasicBlock>();
        mapHelper(entry, visited);
    }

    private void mapHelper(BasicBlock block, HashSet<BasicBlock> visited) {
        if (visited.contains(block)) return;

        ArrayList<Instruction> lines = block.getLines();
        String bName = block.getBlockName();
        visited.add(block);

        for (int i = 0; i < lines.size(); i++) {
            instructionMap.put(lines.get(i), String.format("\t%-15s:%d\t%s", bName, i, lines.get(i).getText()));
        }

        for (BasicBlock succ : block.getSuccessors()) {
            mapHelper(succ, visited);
        }
    }

    public String lookupMappedInstruction(Instruction instr) {
        return this.instructionMap.get(instr);
    }

    public void printWebs() {
        for (String s : this.webs.keySet()) {
            System.out.println(s + ":");
            System.out.println(this.webs.get(s));
        }
    }
}