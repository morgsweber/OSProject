package software;
import java.util.concurrent.Semaphore;
import hardware.CPU;

public class Scheduler extends Thread {
    public static Semaphore SEMAPHORE = new Semaphore(0);
    private CPU cpu;

    public Scheduler(CPU cpu) {
        this.cpu = cpu;
    }

    @Override
    public void run() {
        while (true) {
            try {
                SEMAPHORE.acquire();
                if (ProcessManager.READY.size() > 0) {
                    ProcessManager.RUNNING = ProcessManager.READY.get(0);
                    ProcessManager.READY.remove(0);
                    ProcessControlBlock nextProccess = ProcessManager.RUNNING;
                    System.out.println("\n Scheduling process with id = " + nextProccess.getId() + " ["
                            + ProcessManager.findBlockedPCB(nextProccess.getId()) + "]\n");
                    cpu.loadPCB(nextProccess);
                    cpu.SEMAPHORE.release();
                }
            } catch (InterruptedException error) {
                error.printStackTrace();
            }
        }
    }
}
