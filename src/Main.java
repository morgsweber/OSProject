import hardware.VM;
import software.Programs;
public class Main {
    public static void main(String args[]) {
        System.out.println("########### Operation System Simulator ###########");
        Programs p = new Programs();
        VM vm = new VM(p.fatorial);
        vm.run();
    }
}
