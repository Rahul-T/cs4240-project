import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

public class CFGGenerator {
    private BasicBlock entryBlock;
    private HashMap<String, BasicBlock> functionBlocks;
    private HashMap<String, int[]> functionMap;
    private HashSet<BasicBlock> blocks;
    private HashSet<BasicBlock> loopBlocks;
    // private HashSet<Instruction> allInstructions; 
    private HashMap<String, BasicBlock> labelMap;
    private HashMap<String, HashSet<LiveRange>> webs;
    private String sourceFile;
    // private String[] intList, floatList;

    private static final String[] opArr = {"add", "sub", "mult", "div", "and", "or"};
    private static final String[] brArr = {"breq", "brneq", "brlt", "brgt", "brgeq", "brleq"};
    private static final HashSet<String> binOps = new HashSet<String>(Arrays.asList(opArr));
    private static final HashSet<String> branches = new HashSet<String>(Arrays.asList(brArr));

    private HashMap<Instruction, String> instructionMap;

    public CFGGenerator(String sourceFile) {
        this.sourceFile = sourceFile;
        this.functionBlocks = new LinkedHashMap<String, BasicBlock>();
        this.functionMap = new HashMap<String, int[]>();
        this.blocks = new HashSet<BasicBlock>();
        this.loopBlocks = new HashSet<BasicBlock>();
        // this.allInstructions = new HashSet<Instruction>();
        this.labelMap = new HashMap<String, BasicBlock>();
        this.entryBlock = null;
        this.instructionMap = null;
    }

    public void generateBlocks() throws IOException {

        // create a buffered reader for the file
        FileReader fileReader = new FileReader(this.sourceFile);
        BufferedReader fileBuff = new BufferedReader(fileReader);
        BasicBlock functionBlock = null;

        int blockCount = 0;
        String lastLine=null, currentLine=null, nextLine=null, label=null;
        String[] tokCurrentLine = null, tokLastLine=null;
        BasicBlock targetBlock, newBlock, currentBlock=null;

        String arrayPattern = ",.+\\[\\d+\\]";

        currentLine = fileBuff.readLine();
        nextLine = fileBuff.readLine();
        // process the file
        while (currentLine != null) {
            currentLine = currentLine.trim().replace("# start_function", "#start_function").replace("# end_function", "#end_function");
            tokCurrentLine = currentLine.replace(",", ", ").trim().split("\\s+");
            // System.out.println(tokCurrentLine[0]);
            // catch labels

            if (tokCurrentLine[0].length() == 0) {
                String[] temp = new String[tokCurrentLine.length - 1];
                for (int i = 1; i < tokCurrentLine.length; i++) {
                    temp[i-1] = tokCurrentLine[i];
                }
                tokCurrentLine = temp;
            }
            if (tokCurrentLine.length <= 0) {
                // skip this line
            }
            else if (tokCurrentLine[0].equals("int-list:")) {
                String intListLine = currentLine.replace("int-list:", "").trim();
                String[] intStrings = intListLine.split(",\\s*");
                currentBlock.ints = new HashSet<String>();
                for (String var : intStrings) {
                    if (!var.contains("[")) currentBlock.ints.add(var);
                }
                // System.out.println(String.format("current: %s | function: %s | current int: %s | current float: %s | func int: %s | func float: %s", currentBlock.getBlockName(), functionBlock.getBlockName(), currentBlock.ints, currentBlock.floats, functionBlock.ints, functionBlock.floats));
            }
            else if (tokCurrentLine[0].equals("float-list:")) {
                String floatListLine = currentLine.replace("float-list:", "").trim();
                String[] floatStrings = floatListLine.split(",\\s*");
                currentBlock.floats = new HashSet<String>();
                for (String var : floatStrings) {
                    if (!var.contains("[")) currentBlock.ints.add(var);
                }
            }
            else if (tokCurrentLine.length == 1) {
                label = tokCurrentLine[0].substring(0, tokCurrentLine[0].length()-1);
                if (!this.labelMap.containsKey(label)) {
                    this.labelMap.put(label, genBasicBlock(label));
                    this.labelMap.get(label).ints = functionBlock.ints;
                    this.labelMap.get(label).floats = functionBlock.floats;
                }
                targetBlock = this.labelMap.get(label);
                if (!lastLine.split(" ")[0].contains("goto")) {
                    currentBlock.addSuccessor(targetBlock);
                    targetBlock.addPredecessor(currentBlock);
                }
                currentBlock = targetBlock;
                currentBlock.addLine(currentLine);
            }

            // catch function starts and ends

            else if (tokCurrentLine[0].equals("#start_function")) {
                
                currentBlock = genBasicBlock(tokCurrentLine[1] + "_FUNCTION");
                // System.out.println(String.format("l: %s | c: %s | n: %s", lastLine, currentLine, nextLine));
                functionBlock = currentBlock;
                this.functionBlocks.put(currentBlock.getBlockName(), currentBlock);
                int[] arr = {Instruction.getLineCounter(), -1};
                this.functionMap.put(currentBlock.getBlockName(), arr);
                // lastLine = currentLine;                 
                // currentLine = nextLine;              
                // nextLine = fileBuff.readLine();    
                
                // System.out.println(String.format("l: %s | c: %s | n: %s", lastLine, currentLine, nextLine));
            } else if (tokCurrentLine[0].equals("#end_function")) {
                int[] newArr = this.functionMap.get(tokCurrentLine[1] + "_FUNCTION");
                newArr[1] = Instruction.getLineCounter();
                // skip this line
            } 

            // catch branches and gotos
            else if (tokCurrentLine[0].contains("goto")) {
                currentBlock.addLine(currentLine);
                label = tokCurrentLine[1];
                // System.out.println(currentLine + " \"" + label + "\"");
                if (!this.labelMap.containsKey(label)) this.labelMap.put(label, genBasicBlock(label));
                targetBlock = this.labelMap.get(label);

                targetBlock.ints = functionBlock.ints;
                targetBlock.floats = functionBlock.floats;

                currentBlock.addSuccessor(targetBlock);
                targetBlock.addPredecessor(currentBlock);

                currentBlock = targetBlock;
            } else if (branches.contains(tokCurrentLine[0].replace(",", ""))) {
                currentBlock.addLine(currentLine);
                label = tokCurrentLine[3];
                // System.out.println(currentLine + " \"" + label + "\"");
                if (!this.labelMap.containsKey(label)) this.labelMap.put(label, genBasicBlock(label));
                targetBlock = this.labelMap.get(label);

                targetBlock.ints = functionBlock.ints;
                targetBlock.floats = functionBlock.floats;

                currentBlock.addSuccessor(targetBlock);
                targetBlock.addPredecessor(currentBlock);

                newBlock = genBasicBlock(Integer.toString(blockCount++));

                targetBlock.ints = functionBlock.ints;
                targetBlock.floats = functionBlock.floats;

                currentBlock.addSuccessor(newBlock);
                newBlock.addPredecessor(currentBlock);

                currentBlock = newBlock;
            }

            // otherwise, just add the line
            else {
                if (currentBlock == null) {
                    System.out.println("NULL BLOCK! " + currentLine);
                }
                currentBlock.addLine(currentLine);
            }

            lastLine = currentLine;
            currentLine = nextLine;
            nextLine = fileBuff.readLine();
        }

        fileBuff.close();

        this.entryBlock = this.functionBlocks.get("main_FUNCTION");

        for (BasicBlock block : this.blocks) {
            if (block.getPredecessors().size() == 1 && block.getSuccessors().size() == 1 
                && ((BasicBlock)block.getSuccessors().toArray()[0]).getSuccessors().contains(block)) {
                // this block has only one successor, which also has this block as a successor...aka a loop!
                this.loopBlocks.add(block);
                this.loopBlocks.addAll(block.getSuccessors());
            }

            if (this.functionBlocks.values().contains(block) && !block.getBlockName().equals(this.entryBlock.getBlockName())) {
                block.ints.addAll(this.entryBlock.ints);
                block.floats.addAll(this.entryBlock.floats);
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
            // boolean printLn = false;
            HashSet<String> tempInstIn = new HashSet<String>();
            HashSet<String> tempInstOut = new HashSet<String>();
            HashSet<String> def = new HashSet<String>();
            HashSet<String> use = new HashSet<String>();

            currLine = lines.get(i);
            tokenizedLine = currLine.getText().replace(",", " ").split("\\s+");
            currOp = tokenizedLine[0];

            if (i == lines.size() - 1)
                tempInstOut.addAll(tempBlockOut);
            else {
                tempInstOut.addAll(lines.get(i+1).inSet);
            }

            if (currLine.getText().contains("(")) { // function header
                String[] declArr = currLine.getText().replaceAll("\\):", "").split("\\(");
                if (declArr.length > 1) {
                    String[] argsAndTypeArr = declArr[1].split(",\\s*");
                    String[] argsArr = new String[argsAndTypeArr.length];
                    for (int k = 0; k < argsAndTypeArr.length; k++) {
                        argsArr[k] = argsAndTypeArr[k].split(" ")[1];
                    }
                    for (String s : argsArr) {
                        addIfNotNumeric(s, def);
                    }
                }
                // printLn = true;
            } else if (currOp.equals("assign")) { // handle assigns
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

            
            // if (printLn) System.out.println(String.format("LINE: %s | IN: %s | OUT: %s | DEF: %s | USE: %s", currLine.getText(), currLine.inSet, currLine.outSet, def, use));
        }

        // System.out.println(block.getBlockName() + ": " + lines);
        if (lines.size() > 0) {
            tempBlockIn = lines.get(0).inSet;
        } else {
            tempBlockIn.addAll(tempBlockOut);
        }

        
        changed = (!tempBlockIn.equals(block.inSet)) || (!tempBlockOut.equals(block.outSet));
        
        block.inSet = tempBlockIn;
        block.outSet = tempBlockOut;

        // System.out.println(String.format("BLOCK IN: %s | BLOCK OUT: %s | CHANGED: %s", block.inSet, block.outSet, Boolean.toString(changed)));
        return changed;
    }

    public HashMap<String, HashSet<LiveRange>> createLiveRanges(BasicBlock rootNode) {
        // System.out.println(rootNode.getBlockName());
        Instruction lastUse;
        HashMap<String, Integer> visits = new HashMap<>();
        HashMap<String, HashSet<LiveRange>> web = new HashMap<>();

        // System.out.println(rootNode.getBlockName());
        for (String var : rootNode.ints) {
            if (!web.containsKey(var)) web.put(var, new HashSet<LiveRange>());
            // System.out.println("\t" + var);
            
        }

        for (String var : rootNode.floats) {
            if (!web.containsKey(var)) web.put(var, new HashSet<LiveRange>());
            // System.out.println("\t" + var);
            
        }

        

        for (String var : web.keySet()) {
            for (BasicBlock block : blocks) {
                visits.put(block.getBlockName(), 0);
            }
            lastUse = null;
            LiveRange firstRange = new LiveRange(var);
            web.get(var).add(firstRange);
            traverseCFG(rootNode, firstRange, visits, web);

            // System.out.println("========WEB FOR " + var + " ++++++++++++++++++");
            // for (LiveRange rng : web.get(var)) {
            //     for (Instruction i : rng.instructions) {
            //         System.out.println("\t" + i.getText());
            //     }
            // }
        }

        return web;
    }


    private void traverseCFG(BasicBlock root, LiveRange range, 
        HashMap<String, Integer> visits, HashMap<String, HashSet<LiveRange>> web) {

        String var = range.getVarName();
        if (!web.get(var).contains(range)) {
            // System.out.println("ADDING LOST RANGE! For var " + var);
            // System.out.println(range.instructions);
            web.get(var).add(range);
        }
        // iterate through the block's instructions
        for (Instruction inst : root.getLines()) {
            // if (var.equals("r_st_1_0")) System.out.println(inst + " " + inst.defs + " var:" + var);
            if (inst.inSet.contains(var)) {
                // if (var.equals("i")) System.out.println("Adding line \"" + inst.getText() + "\"" + " to live range for " + var );
                // incInstrs.add(inst);
                range.addInstruction(inst);
            }
            else if (inst.uses.contains(var)) {
                // lastUse = inst;
                range.addInstruction(inst);
                // incInstrs.clear();
            }
            else if (inst.defs.contains(var)) {
                // System.out.println("Adding " + var + " to live range at " + inst);
                range = new LiveRange(var);
                // incInstrs.add(inst);
                range.addInstruction(inst);
                web.get(var).add(range);
            }
        }

        // recursively traverse
        if (root.getSuccessors().isEmpty() 
        || visits.get(root.getBlockName()) > 5) {
            return;
        }

        visits.put(root.getBlockName(), visits.get(root.getBlockName()) + 1);

        for (BasicBlock b : root.getSuccessors()) {
            traverseCFG(b, range, visits, web);
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

    public HashMap<String, int[]> getFunctionMap() {
        return this.functionMap;
    }
}