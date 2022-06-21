package hardware;

import software.Console;
import software.MemoryManager;
import software.ProcessControlBlock;
import software.ProcessManager;
import software.Scheduler;

public enum Interruptions {
    OverFlow, 
    InvalidAdress, 
    InvalidInstruction, 
    NoInterruptions, 
    SystemCall, 
    ClockInterrupt, 
    IoFinishedInterrupt, 
    ProgramEndedInterrupt;

    public CPU cpu;

    //arthur
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
}