package hardware;
import java.util.Scanner;

import software.MemoryManager;
import software.ProcessControlBlock;

public class CPU {
    private int pc; 
    private Word ir; 
    private int[] reg;
    private Interruptions interruption;
    private int[] pageTable;
    private int currentProcessId;

    private Word[] m; 

    public CPU(Word[] _m) { 
        m = _m; 
        reg = new int[10]; 
        pageTable = null;
        interruption = Interruptions.NoInterruptions;
        this.currentProcessId = -1;
    }

    public int[] getPageTable(){
        return pageTable;
    }

    public boolean overFlowInterrupt(int valor) {
        if (valor > -2147483647 && valor < 2147483647) {
            return true;
        }
        interruption = Interruptions.OverFlow;
        return false;
    }

    public boolean invalidAdressInterrupt(int adress) {
        if (adress >= 0 && adress < m.length) {
            return true;
        }
        interruption = Interruptions.InvalidAdress;
        return false;
    }

    public void setContext(int _pc) {
        pc = _pc;
    }

    private void dump(Word w) {
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

    private void showState() {
        System.out.println("       " + pc);
        System.out.print("           ");
        for (int i = 0; i < 10; i++) {
            System.out.print("r" + i);
            System.out.print(": " + reg[i] + "     ");
        }
        ;
        System.out.println("");
        System.out.print("           ");
        dump(ir);
    }

    public void run() {
        while (true) {
            if (interruption != Interruptions.NoInterruptions) {
                switch (interruption) {
                    case OverFlow:
                        System.out.println("----------Interrupção do tipo: Overflow----------");
                        break;
                    case InvalidAdress:
                        System.out.println("----------Interrupção do tipo: Endereço Inválido----------");
                        break;
                    case InvalidInstruction:
                        System.out.println("----------Interrupção do tipo: Instrução Inválida----------");
                        break;
                    case SystemCall:
                        Scanner in = new Scanner(System.in);

                        if (reg[8]==1){
                            int destino = reg[9];
                            System.out.println("Insira um número inteiro: ");
                            int value = in.nextInt();
                            m[destino].p = value;
                        }
                        if (reg[8]==2){
                            int ec = reg[9];
                            System.out.println("Retorno: " + m[ec].p);
                        }
                        interruption = Interruptions.NoInterruptions;
                        continue;
                }
                break;
            }
            int physicalAddress;
            ir =  m[MemoryManager.translate(pc, pageTable)];; 
            //showState();
            switch (ir.opc) {
                case JMP:
                    if (invalidAdressInterrupt(ir.p)) {
                        pc = ir.p;
                    }
                    break;

                case JMPIG:
                    if (invalidAdressInterrupt(reg[ir.r1])) {
                        if (reg[ir.r2] > 0) {
                            pc = reg[ir.r1];
                        } else {
                            pc++;
                        }
                    }
                    break;

                case JMPIE:
                    if (invalidAdressInterrupt(reg[ir.r1])) {
                        if (reg[ir.r2] == 0) {
                            pc = reg[ir.r1];
                        } else {
                            pc++;
                        }
                    }
                    break;

                case STOP:
                    break;

                case JMPI: 
                    if (invalidAdressInterrupt(reg[ir.r1])) {
                        pc = reg[ir.r1];
                    }
                    break;

                case JMPIL: 
                    if (invalidAdressInterrupt(reg[ir.r1])) {
                        if (reg[ir.r2] < 0) {
                            pc = reg[ir.r1];
                        } else {
                            pc++;
                        }
                    }
                    break;

                case JMPIM:
                    physicalAddress = MemoryManager.translate(ir.p, pageTable);
                    if (invalidAdressInterrupt(physicalAddress)) {
                        m[physicalAddress].opc = Opcode.DATA;
                        pc = m[physicalAddress].p; 
                    }
                    break;

                case JMPIGM: 
                    physicalAddress = MemoryManager.translate(ir.p, pageTable);
                    if (invalidAdressInterrupt(physicalAddress)) {
                        if (reg[ir.r2] > 0) {
                            m[physicalAddress].opc = Opcode.DATA;
                            pc = m[physicalAddress].p;
                        } else {
                            pc++;
                        }
                    }
                    break;

                case JMPILM: 
                    physicalAddress = MemoryManager.translate(ir.p, pageTable);
                    if (invalidAdressInterrupt(physicalAddress)) {
                        if (reg[ir.r2] < 0) {
                            m[physicalAddress].opc = Opcode.DATA;
                            pc = m[physicalAddress].p;
                        } else {
                            pc++;
                        }
                    }
                    break;

                case JMPIEM: 
                    physicalAddress = MemoryManager.translate(ir.p, pageTable);
                    if (invalidAdressInterrupt(physicalAddress)) {
                        if (reg[ir.r2] == 0) {
                            m[physicalAddress].opc = Opcode.DATA;
                            pc = m[physicalAddress].p;
                        } else {
                            pc++;
                        }
                    }
                    break;

                case ADD: 
                    int aux = reg[ir.r1] + reg[ir.r2];
                    if (overFlowInterrupt(reg[ir.r1]) && overFlowInterrupt(reg[ir.r2])
                            && overFlowInterrupt(aux)){
                        reg[ir.r1] = reg[ir.r1] + reg[ir.r2];
                        pc++;
                    }
                    break;

                case MULT: 
                    int aux2 = reg[ir.r1] * reg[ir.r2];
                    if (overFlowInterrupt(reg[ir.r1]) && overFlowInterrupt(reg[ir.r2])
                            && overFlowInterrupt(aux2)){
                        reg[ir.r1] = reg[ir.r1] * reg[ir.r2];
                        pc++;
                    }
                    break;

                case ADDI: 
                    int aux3 =  reg[ir.r1] + ir.p;
                    if (overFlowInterrupt(reg[ir.r1]) && overFlowInterrupt(ir.p)
                            && overFlowInterrupt(aux3)) {
                        reg[ir.r1] = reg[ir.r1] + ir.p;
                        pc++;
                    }
                    break;

                case SUB: 
                    int aux4 = reg[ir.r1] - reg[ir.r2];
                    if (overFlowInterrupt(reg[ir.r1]) && overFlowInterrupt(reg[ir.r2])
                            && overFlowInterrupt(aux4)) {
                        reg[ir.r1] = reg[ir.r1] - reg[ir.r2];
                        pc++;
                    }
                    break;

                case SUBI: 
                    int aux5 = reg[ir.r1] - ir.p;
                    if (overFlowInterrupt(reg[ir.r1]) && overFlowInterrupt(ir.p)
                            && overFlowInterrupt(aux5)) {
                        reg[ir.r1] = reg[ir.r1] - ir.p;
                        pc++;
                    }
                    break;

                case LDD: 
                    physicalAddress = MemoryManager.translate(ir.p, pageTable);
                    if (invalidAdressInterrupt(physicalAddress) && overFlowInterrupt(m[ir.p].p)) {
                        m[physicalAddress].opc = Opcode.DATA;
                        reg[ir.r1] = m[physicalAddress].p;
                        pc++;
                    }
                    break;

                case LDX: 
                    physicalAddress = MemoryManager.translate(reg[ir.r2], pageTable);
                    if (invalidAdressInterrupt(reg[physicalAddress]) && overFlowInterrupt(reg[ir.r1])) {
                        m[physicalAddress].opc = Opcode.DATA;
                        reg[ir.r1] = m[physicalAddress].p;
                        pc++;
                    }
                    break;

                case STX: 
                    physicalAddress = MemoryManager.translate(reg[ir.r1], pageTable);
                    if (invalidAdressInterrupt(physicalAddress) && overFlowInterrupt(reg[ir.r2])) {
                        m[physicalAddress].opc = Opcode.DATA;
                        m[physicalAddress].p = reg[ir.r2];
                        pc++;
                    }
                    break;

                case LDI: 
                    if (overFlowInterrupt(ir.p)) {
                        reg[ir.r1] = ir.p;
                        pc++;
                    }
                    break;

                case STD: 
                    physicalAddress = MemoryManager.translate(ir.p, pageTable);
                    if (invalidAdressInterrupt(physicalAddress) && overFlowInterrupt(m[physicalAddress].p)) {
                        m[physicalAddress].opc = Opcode.DATA;
                        m[physicalAddress].p = reg[ir.r1];
                        pc++;
                    }
                    break;

                case SWAP:
                    int t = reg[ir.r1];
                    reg[ir.r1] = reg[ir.r2];
                    reg[ir.r2] = t;
                    pc++;
                    break;

                case TRAP:
                    interruption = Interruptions.SystemCall;
                    pc ++;
                    break; 

                default:
                    interruption = Interruptions.InvalidInstruction;
            }

            if (ir.opc == Opcode.STOP) {
                break; 
            }
            /*try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
    }

    public void loadPCB(ProcessControlBlock pcb) {
        this.currentProcessId = pcb.getId();
		this.pc = pcb.getPc();
		this.reg = pcb.getReg().clone();
		this.pageTable = pcb.getPageTable().clone();
	}

	public ProcessControlBlock unloadPCB() {
		return new ProcessControlBlock(currentProcessId, pc, reg.clone(), pageTable.clone());
	}
}