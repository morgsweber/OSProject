package software;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import hardware.CPU;
import hardware.Interruptions;

public class Console extends Thread {

    public static Semaphore SEMAPHORE = new Semaphore(0);

    private CPU cpu;
    private Scanner reader;
    public Interruptions interrupt;

    public static ArrayList<IORequest> IO_REQUESTS = new ArrayList<>();
    public static ArrayList<Integer> FINISHED_IO_PROCESS_IDS = new ArrayList<>();

    public Console(CPU cpu) {
        this.cpu = cpu;
        this.reader = new Scanner(System.in);
        this.interrupt = new Interruptions(cpu);
    }

    @Override
    public void run() {
        while (true) {
            try {
                SEMAPHORE.acquire();
                IORequest ioRequest = IO_REQUESTS.get(0);
                IO_REQUESTS.remove(0);
                if (ioRequest.getOperationType() == IORequest.OperationTypes.READ) {
                    read(ioRequest.getProcess());
                } else {
                    write(ioRequest.getProcess());
                }
            } catch (InterruptedException error) {
                error.printStackTrace();
            }
        }
    }

    private void read(ProcessControlBlock process) {
        System.out.println(
                "\n\n[Process with ID = " + process.getId() + " - READ] [ALERT: waiting input] \n");
        Integer input = reader.nextInt();
        System.out.println("\nReceived input\n");

        process.setIo(input);
        addFinishedIOProcessId(process.getId());
        removeIORequest(process.getId());
        if (ProcessManager.READY.size() <= 0) {
            interrupt.noOtherProcessRunningRoutine();
        }
    }

    private void write(ProcessControlBlock process) {
        System.out.println("\n\n[Process with ID = " + process.getId() + " - WRITE]\n");
        int physicalAddress = MemoryManager.translate(process.getReg()[9], process.getPageTable());
        int output = cpu.m[physicalAddress].p;
        process.setIo(output);
        addFinishedIOProcessId(process.getId());
        removeIORequest(process.getId());
        if (ProcessManager.READY.size() <= 0) {
            interrupt.noOtherProcessRunningRoutine();
        }
    }

    private static void removeIORequest(int processId) {
        for (int i = 0; i < IO_REQUESTS.size(); i++) {
            if (IO_REQUESTS.get(i).getProcess().getId() == processId) {
                IO_REQUESTS.remove(i);
            }
        }
    }

    public static void addFinishedIOProcessId(int id) {
        FINISHED_IO_PROCESS_IDS.add(id);
    }

    public static int getFirstFinishedIOProcessId() {
        int result = FINISHED_IO_PROCESS_IDS.get(0);
        FINISHED_IO_PROCESS_IDS.remove(0);

        return result;
    }
}
