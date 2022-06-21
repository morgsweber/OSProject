package software;
import hardware.Word;

public class ShowState {

    public static String dump(Word w) {
        String dump = "";
        dump += "[";
        dump += w.opc;
        dump += ", ";
        dump += w.r1;
        dump += ", ";
        dump += w.r2;
        dump += ", ";
        dump += w.p;
        dump += "]";
        return dump;
    }

    public static String dump(Word[] m, int ini, int fim) {
        String dump = "";
        for (int i = ini; i < fim; i++) {
            dump += i + ":" + dump(m[i]) + "\n";
        }
        return dump;
    }
}
