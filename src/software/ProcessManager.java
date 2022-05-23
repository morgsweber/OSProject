package software;
import java.util.ArrayList;

import hardware.CPU;
import hardware.VM;
import hardware.Word;

public class ProcessManager {
    public int memSize;
    public int frameSize;
    public MemoryManager mm;
    private static ArrayList<ProcessControlBlock> ready;
    private ProcessControlBlock running;
    private CPU cpu;
    private int idCounter;

    public ProcessManager(int memSize, int frameSize, CPU cpu){ 
        this.idCounter = 1;
        this.cpu = cpu;
        this.memSize = memSize;
        this.frameSize = frameSize;
        this.ready = new ArrayList<ProcessControlBlock>();
        mm = new MemoryManager(memSize, frameSize);
    }

    public boolean createProcess(Word[] program){
        //reserva espaço na memória 
        int[] pageTable = mm.allocate(program.length);
        if (pageTable == null){
            return false;
        }

        //insere programa no endereço físico 
        for (int i = 0; i < program.length; i++) {
            int physicalAddress = mm.translate(i, pageTable);
            VM.m[physicalAddress].opc = program[i].opc;
            VM.m[physicalAddress].r1 = program[i].r1;
            VM.m[physicalAddress].r2 = program[i].r2;
            VM.m[physicalAddress].p = program[i].p;
        }
        int id = idCounter;
        ProcessControlBlock pcb = new ProcessControlBlock(id, 0, new int[10], pageTable);
        ready.add(pcb);
        cpu.loadPCB(pcb); //coloca processo na cpu 
        idCounter++;
        return true;
    }


    public void deallocateProcess(int processId, int[] pageTable) {
        mm.deallocates(pageTable);
        for (int i = 0; i < ready.size(); i++) {
            if (ready.get(i).getId() == processId) {
                ready.remove(i);
            }
        }
    }

    public void dump(int processId){
        ProcessControlBlock aux = ready.get(processId);
        System.out.println("PCB");
        System.out.println("id: " + aux.getId());
        System.out.println("pc: " + aux.getPc());
        System.out.print("reg: ");
        for(int i=0; i<aux.getReg().length; i++){ System.out.print(aux.getReg()[i] + " ");}
        System.out.println();
        System.out.print("page table: ");
        for(int i=0; i<aux.getPageTable().length; i++){ System.out.print(aux.getPageTable()[i] + " ");}
        System.out.println();
    }

    public void dumpM(int start, int end){
        for(int i=start; i<end; i++){
            System.out.print("p: " + VM.m[i].p);
            System.out.print(" r1: " + VM.m[i].r1);
            System.out.print(" r2: " + VM.m[i].r2);
            System.out.println();
        }
        System.out.println();
    }
}
