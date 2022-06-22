package software;
import java.util.ArrayList;

import hardware.CPU;
import hardware.VM;
import hardware.Word;

public class ProcessManager {
    public int memSize;
    public int frameSize;
    public MemoryManager mm;
    public static  ArrayList<ProcessControlBlock> READY = new ArrayList<ProcessControlBlock>();
    public static  ArrayList<ProcessControlBlock> BLOCKED = new ArrayList<ProcessControlBlock>();;
    public static ProcessControlBlock RUNNING; 
    private CPU cpu;
    
    private int idCounter;

    public ProcessManager(int memSize, int frameSize, CPU cpu, Scheduler Scheduler){ 
        this.idCounter = 0;
        this.cpu = cpu;
        this.memSize = memSize;
        this.frameSize = frameSize;
        mm = new MemoryManager(memSize, frameSize);
    }

    public void setReady(ProcessControlBlock pcb){
        READY.add(pcb);
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
        READY.add(pcb);
        System.out.println("Process id " + id + " created");
        idCounter++;

        if (READY.size() == 1 && RUNNING == null) {
            Scheduler.SEMAPHORE.release();
        }
        return true;
    }

    public void deallocateProcess(int processId) {
        for (int i = 0; i < READY.size(); i++) {
            if (READY.get(i).getId() == processId) {
                mm.deallocates(READY.get(i).getPageTable());
                READY.remove(i);
            }
        }
    }

    public void dump(int processId){
        ProcessControlBlock aux = findPCB(processId);
        if(aux == null){ System.out.println("Process not found");}
        else{
            System.out.println("-----------");
            System.out.println("PCB");
            System.out.println("id: " + aux.getId());
            System.out.println("pc: " + aux.getPc());
            System.out.print("reg: ");
            for(int i=0; i<aux.getReg().length; i++){ System.out.print(aux.getReg()[i] + " ");}
            System.out.println();
            System.out.print("page table: ");
            for(int i=0; i<aux.getPageTable().length; i++){ System.out.print(aux.getPageTable()[i] + " ");}
            System.out.println();
            System.out.println("-----------");
        }
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

    public static ProcessControlBlock findPCB(int processId){
        ProcessControlBlock process = null;
        for (int i = 0; i < READY.size(); i++) {
            if (READY.get(i).getId() == processId) {
                process = READY.get(i);
            }
        }
        return process;
    }

    public static ProcessControlBlock findBlockedPCB(int id) {
        for (int i = 0; i < BLOCKED.size(); i++) {
            if (BLOCKED.get(i).getId() == id) {
                return BLOCKED.remove(i);
            }
        }
        return null;
    }

    public static void destroyProcess(int processId, int[] pageTable) {
        MemoryManager.deallocates(pageTable);
        for (int i = 0; i < READY.size(); i++) {
            if (READY.get(i).getId() == processId) {
                READY.remove(i);
            }
        }
        for (int i = 0; i < BLOCKED.size(); i++) {
            if (BLOCKED.get(i).getId() == processId) {
                BLOCKED.remove(i);
            }
        }
    }
}
