package software;

import hardware.VM;
import hardware.Word;

public class Sistema {
	public VM vm;
	public Monitor monitor;

	public Sistema(){   // a VM com tratamento de interrupções
        vm = new VM();
        monitor = new Monitor();
    }

	public void roda(Word[] programa){
        monitor.carga(programa, vm.m);
        System.out.println("---------------------------------- programa carregado ");

        monitor.dump(vm.m, 0, programa.length);

        monitor.executa();
        System.out.println("---------------------------------- após execucao ");

        monitor.dump(vm.m, 0, programa.length);
    }
}
