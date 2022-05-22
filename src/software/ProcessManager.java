package software;
import java.util.ArrayList;

import hardware.VM;
import hardware.Word;

public class ProcessManager {
    public int memSize;
    public int frameSize;
    public MemoryManager mm;
    private ArrayList<ProcessControlBlock> ready;
    private ProcessControlBlock running;

    public ProcessManager(int memSize, int frameSize){ 
        this.memSize = memSize;
        this.frameSize = frameSize;
        mm = new MemoryManager(memSize, frameSize);

    }

    public boolean createProcess(Word[] program){
        int [] frames = mm.allocate(program.length);
        if (frames == null){
            return false;
        }

        int [] positions = translate(frames);
        for (int i = 0; i < program.length; i++) {
            // System.out.println(positions[i]);
            VM.m[positions[i]].opc = program[i].opc;     
            VM.m[positions[i]].r1 = program[i].r1;     
            VM.m[positions[i]].r2 = program[i].r2;     
            VM.m[positions[i]].p = program[i].p;
        }
        ProcessControlBlock pcb = new ProcessControlBlock(positions[0],new int[10],frames);
        ready.add(pcb);
        return true;
    }

    private int [] translate(int [] frames){
        int [] positions = new int[frames.length * mm.pageSize];
        int count = 0; 
        for (int i = 0; i < frames.length; i++) {
            for (int j = 0; j < mm.pageSize; j++) {
                positions[count] = (frames[i] * mm.pageSize)+j;
                count++; 
            }
        }
        
        return positions;
    }

    public void endProcess(int id){
        mm.deallocates(running.pagTable);
        running = null;
    }    
}
