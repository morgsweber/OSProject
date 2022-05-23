package software;

public class ProcessControlBlock {
    private int id;
    private int pc;
    private int[] reg;
    private int[] pageTable;
        
    public ProcessControlBlock (int id, int pc, int [] reg, int [] pageTable) {
        this.id = id;
        this.pc = pc;
        this.reg = reg;
        this.pageTable = pageTable;
    }
    
    public int getId() {
        return id;
    }

    public int getPc(){
        return pc;
    }

    public int[] getReg(){
        return reg;
    }

    public int[] getPageTable(){
        return pageTable;
    }
}
