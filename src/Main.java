import software.Programas;
import software.Sistema;

public class Main {
    public static void main(String args[]) {
        Programas progs = new Programas();

        Sistema s = new Sistema();
        s.roda(progs.fatorial);
    }
}
