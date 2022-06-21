package software;

import java.util.Scanner;

import hardware.CPU;
import hardware.VM;

public class Shell extends Thread{
    private CPU cpu;

    public Shell(CPU cpu) {
        this.cpu = cpu;
    }

    Scanner in = new Scanner(System.in);

    public void run() {
        System.out.println("##################################################");
        System.out.println("----------- Operation System Simulator -----------");
        System.out.println("----------- 0 - ProgMin                -----------");
        System.out.println("----------- 1 - Fibonacci              -----------");
        System.out.println("----------- 2 - Factorial              -----------");
        System.out.println("----------- 3 - PA                     -----------");
        System.out.println("----------- 4 - PB                     -----------");
        System.out.println("----------- 5 - PA Input               -----------");
        System.out.println("----------- 6 - PB Output              -----------");
        System.out.println("-----------                            -----------");
        System.out.println("###########     INSTRUCTIONS:          ###########");
        System.out.println("----------- exec <id>                  -----------");
        System.out.println("----------- dump <process id>          -----------");
        System.out.println("----------- dumpM <start> <end>        -----------");
        System.out.println("----------- deallocate <process id>    -----------");
        System.out.println("----------- exit 0                     -----------");
        System.out.println("##################################################");

        while (true) {
            String readLine = in.nextLine();
            String command = readLine.split(" ")[0];
            int id = Integer.parseInt(readLine.split(" ")[1]);
            if (command.equals("exec")) {
                if (id != 0 && id != 1 && id != 2 && id != 3 && id != 4 && id != 5 && id != 6) {
                    System.out.println("Invalid program id");
                }
            }
            switch (command) {
                case "exec":
                    createProcess(id);
                    break;
                case "dump":
                    VM.pm.dump(id);
                    break;
                case "dumpM":
                    int end = Integer.parseInt(readLine.split(" ")[2]);
                    VM.pm.dumpM(id, end);
                    break;
                case "deallocate":
                    VM.pm.deallocateProcess(id, cpu.getPageTable());
                    break;
                case "exit":
                    System.out.println("Ending system");
                    System.exit(0);
                    in.close();
                    break;
                default:
                    System.out.println("Invalid command");
                    break;
            }
        }
    }

    public void createProcess(int code) {
        boolean allocated = false;
        if (code == 0) {
            allocated = VM.pm.createProcess(new Programs().progMinimo);
            if (allocated) {
                cpu.run();
                VM.pm.setReady(cpu.unloadPCB());
            }
        }
        if (code == 1) {
            allocated = VM.pm.createProcess(new Programs().fibonacci10);
            if (allocated) {
                cpu.run();
                VM.pm.setReady(cpu.unloadPCB());
            }
        } else if (code == 2) {
            allocated = VM.pm.createProcess(new Programs().fatorial);
            if (allocated) {
                cpu.run();
                VM.pm.setReady(cpu.unloadPCB());
            }
        } else if (code == 3) {
            allocated = VM.pm.createProcess(new Programs().pa);
            if (allocated) {
                cpu.run();
                VM.pm.setReady(cpu.unloadPCB());
            }
        } else if (code == 4) {
            allocated = VM.pm.createProcess(new Programs().pb);
            if (allocated) {
                cpu.run();
                VM.pm.setReady(cpu.unloadPCB());
            }
        }else if (code == 5) {
            allocated = VM.pm.createProcess(new Programs().paInput);
            if (allocated) {
                cpu.run();
                VM.pm.setReady(cpu.unloadPCB());
            }
        }else if (code == 6) {
            allocated = VM.pm.createProcess(new Programs().pbOutput);
            if (allocated) {
                cpu.run();
                VM.pm.setReady(cpu.unloadPCB());
            }
        }
        if (!allocated) {
            System.out.println("Memory unavailable to create process");
        }
    }
}
