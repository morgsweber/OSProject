package software;

public class ProcessControlBlock {
    public int pc;
    public int[] reg;
    public int [] pagTable;
        
    public ProcessControlBlock (int pc, int [] reg, int [] pagTable) {
        this.pc = pc;
        this.reg = reg;
        this.pagTable = pagTable;
    }    
}
