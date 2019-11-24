import java.io.*;
import java.util.*;

public class ColoringAllocator extends Allocator {

    public ColoringAllocator(String irFile, boolean isVerbose) {
        super(irFile, isVerbose);
    }

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

    @Override
    public void buildTextSection() throws IOException {

    }
}