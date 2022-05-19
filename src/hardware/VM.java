package hardware;
import software.ProcessManager;

public class VM {
    public Word[] p;
    public int memSize;
    public int frameSize;
    public Word[] m;
    public CPU cpu;
    public ProcessManager pm;

    public VM(Word[] program) {
        this.p= program;
        memSize = 1024;
        frameSize = 8;
        m = new Word[memSize];
        for (int i = 0; i < memSize; i++) {
            m[i] = new Word(Opcode.___, -1, -1, -1);
        }
        cpu = new CPU(m);
        pm = new ProcessManager(memSize, frameSize);
        pm.createProcess(p);
    }

    public void run() {
        System.out.println("#           Running Virtual Machine              #");
        cpu.run();
    }
}
