package hardware;

public class VM {
    public int memSize;
    public Word[] m;
    public CPU cpu;

    public VM() {
        memSize = 1024;
        m = new Word[memSize];
        for (int i = 0; i < memSize; i++) {
            m[i] = new Word(Opcode.___, -1, -1, -1);
        }
        cpu = new CPU(m);
    }

    public void run() {
        cpu.start();
    }
}
