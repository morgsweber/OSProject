package software;

public class ProcessControlBlock {
    private int id;
    private int pc;
    private int[] reg;
    private int[] pageTable;
    private int io;


    public ProcessControlBlock (int id, int pc, int [] reg, int [] pageTable) {
        this.id = id;
        this.pc = pc;
        this.reg = reg;
        this.pageTable = pageTable;
        this.io = -1;
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

    public void setIo(int value){
        this.io = value;
    }

    public int getIo(){
        return io;
    }
}
