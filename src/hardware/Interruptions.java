package hardware;

import software.Console;
import software.MemoryManager;
import software.ProcessControlBlock;
import software.ProcessManager;
import software.Scheduler;

public class Interruptions {
    public static enum TypeInterruptions {
        OverFlow,
        InvalidAdress,
        InvalidInstruction,
        NoInterruptions,
        SystemCall,
        ClockInterrupt,
        IoFinishedInterrupt,
        ProgramEndedInterrupt;
    }

    public CPU cpu;
        
    public Interruptions(CPU cpu) {
        this.cpu = cpu;
    }

    public void noOtherProcessRunningRoutine() {
        int finishedIOProcessId = Console.getFirstFinishedIOProcessId();
        ProcessControlBlock finishedIOProcess = ProcessManager.findPCB(finishedIOProcessId);
        int physicalAddress = MemoryManager.translate(finishedIOProcess.getReg()[8], finishedIOProcess.getPageTable());
        if (finishedIOProcess.getReg()[7] == 1) {
            cpu.m[physicalAddress].opc = Opcode.DATA;
            cpu.m[physicalAddress].p = finishedIOProcess.getIo();
        } else {
            System.out.println(
                    "\n[Output from process with ID = " + finishedIOProcess.getId() + " - "
                            + ProcessManager.findPCB(finishedIOProcess.getId()) + "] "
                            + finishedIOProcess.getIo()
                            + "\n");
        }
        ProcessManager.READY.add(0, finishedIOProcess);
        // Libera escalonador.
        if (Scheduler.SEMAPHORE.availablePermits() == 0 && ProcessManager.RUNNING == null) {
            Scheduler.SEMAPHORE.release();
        }
    }

    public void saveProcess() {
        ProcessManager.RUNNING = null;
        ProcessControlBlock process = cpu.unloadPCB();
        ProcessManager.READY.add(process);
        cpu.setInterruption(TypeInterruptions.NoInterruptions);
        if (Scheduler.SEMAPHORE.availablePermits() == 0 && ProcessManager.RUNNING == null) {
            Scheduler.SEMAPHORE.release();
        }
    }

    public void endProcess() {
        if (ProcessManager.RUNNING != null) {
            ProcessManager.destroyProcess(cpu.getCurrentProcessId(), cpu.getPageTable());
            ProcessManager.RUNNING = null;
        }
        cpu.setInterruption(TypeInterruptions.NoInterruptions);
        if (Scheduler.SEMAPHORE.availablePermits() == 0) {
            Scheduler.SEMAPHORE.release();
        }
    }

    public void ioFinishedRoutine() {
        ProcessManager.RUNNING = null;
        int finishedIOProcessId = Console.getFirstFinishedIOProcessId();
        ProcessControlBlock finishedIOProcess = ProcessManager.findBlockedPCB(finishedIOProcessId);
        ProcessControlBlock interruptedProcess = cpu.unloadPCB();
        ProcessManager.READY.add(interruptedProcess);
        cpu.setInterruption(TypeInterruptions.NoInterruptions);
        ProcessManager.READY.add(finishedIOProcess);

        int physicalAddress = MemoryManager.translate(finishedIOProcess.getReg()[8], finishedIOProcess.getPageTable());
        if (finishedIOProcess.getReg()[7] == 1) {
            cpu.m[physicalAddress].opc = Opcode.DATA;
            cpu.m[physicalAddress].p = finishedIOProcess.getIo();
        } else {
            System.out.println(
                    "\n[Output from process with ID = " + finishedIOProcess.getId() + " - "
                            + ProcessManager.findPCB(finishedIOProcess.getId()) + "] "
                            + finishedIOProcess.getIo()
                            + "\n");
        }

        if (Scheduler.SEMAPHORE.availablePermits() == 0 && ProcessManager.RUNNING == null) {
            Scheduler.SEMAPHORE.release();
        }
    }
}