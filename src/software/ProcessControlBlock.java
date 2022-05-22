package software;

public class ProcessControlBlock {
    public int pc;
    public int[] reg;
    public int [] pageTable;
        
    public ProcessControlBlock (int pc, int [] reg, int [] pageTable) {
        this.pc = pc;
        this.reg = reg;
        this.pageTable = pageTable;
    }    
}
