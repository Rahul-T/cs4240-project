import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

public class BlockGenerator {
    private BasicBlock entryBlock;
    private HashMap<String, BasicBlock> functionBlocks;
    private HashSet<BasicBlock> blocks;
    private HashMap<String, BasicBlock> labelMap;
    private String sourceFile;

    public BlockGenerator(String sourceFile) {
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
            tokCurrentLine = currentLine.split(" ");

            // catch labels
            if (tokCurrentLine.length == 1) {
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
            else if (tokCurrentLine[0].equals("#")) {

                if (tokCurrentLine[1].equals("start_function")) {
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
            else if (tokCurrentLine[0].equals("goto")) {
                currentBlock.addLine(currentLine);
                label = tokCurrentLine[1];
                if (!this.labelMap.containsKey(label)) this.labelMap.put(label, genBasicBlock(label));
                targetBlock = this.labelMap.get(label);

                currentBlock.addSuccessor(targetBlock);
                targetBlock.addPredecessor(currentBlock);

                currentBlock = targetBlock;
            } else if (tokCurrentLine[0].substring(0,2).equals("br")) {
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

}