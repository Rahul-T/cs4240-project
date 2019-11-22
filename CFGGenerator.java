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
    private HashMap<String, BasicBlock> labelMap;
    private String sourceFile;

    private static final String[] opArr = {"add", "sub", "mult", "div", "and", "or"};
    private static final String[] brArr = {"breq", "brneq", "brlt", "brgt", "brgeq", "brleq"};
    private static final HashSet<String> binOps = new HashSet<String>(Arrays.asList(opArr));
    private static final HashSet<String> branches = new HashSet<String>(Arrays.asList(brArr));

    public CFGGenerator(String sourceFile) {
        this.sourceFile = sourceFile;
        this.functionBlocks = new HashMap<String, BasicBlock>();
        this.blocks = new HashSet<BasicBlock>();
        this.labelMap = new HashMap<String, BasicBlock>();
        this.entryBlock = null;
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
        currentLine = fileBuff.readLine();
        nextLine = fileBuff.readLine();

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
                if (!lastLine.split(" ")[0].equals("goto")) {
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
                // temp = temp || treeRecurser(b, new HashSet<BasicBlock>(), temp);
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
                def.add(tokenizedLine[1]);
                addIfNotNumeric(tokenizedLine[2], use);
                addIfNotNumeric(tokenizedLine[3], use);
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

            // System.out.println(String.format("LINE: %s | IN: %s | OUT: %s | DEF: %s | USE: %s", currLine.getText(), currLine.inSet, currLine.outSet, def, use));
        }

        tempBlockIn = lines.get(0).inSet;
        
        changed = (!tempBlockIn.equals(block.inSet)) || (!tempBlockOut.equals(block.outSet));
        
        block.inSet = tempBlockIn;
        block.outSet = tempBlockOut;

        // System.out.println(String.format("BLOCK IN: %s | BLOCK OUT: %s | CHANGED: %s", block.inSet, block.outSet, Boolean.toString(changed)));
        return changed;
    }

    // private boolean treeRecurser(BasicBlock leafNode, HashSet<BasicBlock> visited, boolean val) {
    //     if (leafNode.getPredecessors().isEmpty()) return updateInOutSet(leafNode);

    //     for (BasicBlock pred : leafNode.getPredecessors()) {
    //         if (!visited.contains(pred)) {
    //             visited.add(pred);
    //             val = val || updateInOutSet(pred);
    //             treeRecurser(pred, visited, val);
    //         }
    //     }
    //     return val;
    // }

    public BasicBlock getEntryBlock() {
        return this.entryBlock;
    }

    public HashMap<String,BasicBlock> getFunctionBlocks() {
        return this.functionBlocks;
    }

    public HashSet<BasicBlock> getBlocks() {
        return this.blocks;
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
}