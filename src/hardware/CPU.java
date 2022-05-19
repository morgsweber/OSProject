package hardware;
import java.util.Scanner;

public class CPU extends Thread{
    private int pc; 
    private Word ir; 
    private int[] reg;
    private Interruptions interruption;

    private Word[] m; 

    public CPU(Word[] _m) { 
        m = _m; 
        reg = new int[10]; 
        interruption = Interruptions.NoInterruptions;
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
            ir = m[pc]; 
            showState();
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
                    if (invalidAdressInterrupt(ir.p)) {
                        m[ir.p].opc = Opcode.DATA;
                        pc = m[ir.p].p; 
                    }
                    break;

                case JMPIGM: 
                    if (invalidAdressInterrupt(ir.p)) {
                        if (reg[ir.r2] > 0) {
                            m[ir.p].opc = Opcode.DATA;
                            pc = m[ir.p].p;
                        } else {
                            pc++;
                        }
                    }
                    break;

                case JMPILM: 
                    if (invalidAdressInterrupt(ir.p)) {
                        if (reg[ir.r2] < 0) {
                            m[ir.p].opc = Opcode.DATA;
                            pc = m[ir.p].p;
                        } else {
                            pc++;
                        }
                    }
                    break;

                case JMPIEM: 
                    if (invalidAdressInterrupt(ir.p)) {
                        if (reg[ir.r2] == 0) {
                            m[ir.p].opc = Opcode.DATA;
                            pc = m[ir.p].p;
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
                    if (invalidAdressInterrupt(ir.p) && overFlowInterrupt(m[ir.p].p)) {
                        m[ir.p].opc = Opcode.DATA;
                        reg[ir.r1] = m[ir.p].p;
                        pc++;
                    }
                    break;

                case LDX: 
                    if (invalidAdressInterrupt(reg[ir.r2]) && overFlowInterrupt(reg[ir.r1])) {
                        m[ir.r2].opc = Opcode.DATA;
                        reg[ir.r1] = m[reg[ir.r2]].p;
                        pc++;
                    }
                    break;

                case STX: 
                    if (invalidAdressInterrupt(reg[ir.r1]) && overFlowInterrupt(reg[ir.r2])) {
                        m[reg[ir.r1]].opc = Opcode.DATA;
                        m[reg[ir.r1]].p = reg[ir.r2];
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
                    if (invalidAdressInterrupt(ir.p) && overFlowInterrupt(m[ir.p].p)) {
                        m[ir.p].opc = Opcode.DATA;
                        m[ir.p].p = reg[ir.r1];
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
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}