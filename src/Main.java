import hardware.VM;
import software.Programs;
public class Main {
    public static void main(String args[]) {
        Programs p = new Programs();
        VM vm = new VM(p.fatorial);
        vm.run();
    }
}
