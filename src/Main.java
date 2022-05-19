import software.Programs;
import software.SysOp;

public class Main {
    public static void main(String args[]) {
        Programs progs = new Programs();

        SysOp s = new SysOp();
        s.run(progs.fatorial);
    }
}
