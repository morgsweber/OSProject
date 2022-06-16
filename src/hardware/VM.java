package hardware;
import software.Shell;
import software.ProcessManager;
import software.Scheduler;

public class VM {
    public Word[] p;
    public int memSize;
    public int frameSize;
    public static Word[] m;
    public CPU cpu;
    public static ProcessManager pm;
    public Shell shell;
    public Scheduler scheduler;

    public VM() {
        memSize = 1024;
        frameSize = 16;
        m = new Word[memSize];
        for (int i = 0; i < memSize; i++) {
            m[i] = new Word(Opcode.___, -1, -1, -1);
        }
        cpu = new CPU(m); //thread que fica em looping: semáforo da CPU.wait(), semáforo inicializado em 0
        shell = new Shell(cpu);
        scheduler = new Scheduler(); //seta o estado da CPU 
        pm = new ProcessManager(memSize, frameSize, cpu, scheduler);
    }


    public void run() {
        shell.start();
        cpu.start();
        scheduler.start();
    }
}
