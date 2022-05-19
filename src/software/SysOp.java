package src.software;

import src.hardware.VM;
import src.hardware.Word;

public class SysOp {
	public VM vm;
	public Monitor monitor;

	public SysOp(){ 
        vm = new VM();
        monitor = new Monitor();
    }

	public void run(Word[] program){
        monitor.charge(program, vm.m);
        System.out.println("---------------------------------- program loaded ");

        monitor.dump(vm.m, 0, program.length);

        monitor.run(vm);
        System.out.println("---------------------------------- after execution ");

        monitor.dump(vm.m, 0, program.length);
    }
}
