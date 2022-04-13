public class Monitor {
    public VM vm;
    public void dump(Word w) {
        System.out.print("[ ");
        System.out.print(w.opc);
        System.out.print(", ");
        System.out.print(w.r1);
        System.out.print(", ");
        System.out.print(w.r2);
        System.out.print(", ");
        System.out.print(w.p);
        System.out.println("  ] ");
    }

    public void dump(Word[] m, int ini, int fim) {
        for (int i = ini; i < fim; i++) {
            System.out.print(i);
            System.out.print(":  ");
            dump(m[i]);
        }
    }

    public void carga(Word[] p, Word[] m) {         // significa ler "p" de memoria secundaria e colocar na principal "m"
        for (int i = 0; i < p.length; i++) {
            m[i].opc = p[i].opc;
            m[i].r1 = p[i].r1;
            m[i].r2 = p[i].r2;
            m[i].p = p[i].p;
        }
    }

    public void executa() {
        vm.cpu.setContext(0);                       // monitor seta contexto - pc aponta para inicio do programa
        vm.cpu.run();                               // e cpu executa
                                                    // note aqui que o monitor espera que o programa carregado acabe normalmente
                                                    // nao ha protecoes... o que poderia acontecer ?
    }
}