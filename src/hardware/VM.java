package hardware;
import software.Interface;
import software.ProcessManager;

public class VM {
    public Word[] p;
    public int memSize;
    public int frameSize;
    public static Word[] m;
    public CPU cpu;
    public static ProcessManager pm;
    public Interface i;

    public VM() {
        memSize = 1024;
        frameSize = 8;
        m = new Word[memSize];
        for (int i = 0; i < memSize; i++) {
            m[i] = new Word(Opcode.___, -1, -1, -1);
        }
        i = new Interface();
        cpu = new CPU(m);
        pm = new ProcessManager(memSize, frameSize);
    }

    public void run() {
        i.run();
        cpu.run();     
    }
}
