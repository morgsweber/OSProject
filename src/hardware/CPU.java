package hardware;
import java.util.Scanner;

public class CPU extends Thread{
    // característica do processador: contexto da CPU ...
    private int pc; // ... composto de program counter,
    private Word ir; // instruction register,
    private int[] reg; // registradores da CPU
    private Interruptions interrupcao;

    private Word[] m; // CPU acessa MEMORIA, guarda referencia 'm' a ela. memoria nao muda. ee sempre
                        // a mesma.

    public CPU(Word[] _m) { // ref a MEMORIA e interrupt handler passada na criacao da CPU
        m = _m; // usa o atributo 'm' para acessar a memoria.
        reg = new int[10]; // aloca o espaço dos registradores
        interrupcao = Interruptions.SemInterrupcao;
    }

    public boolean trataInterruptOverflow(int valor) {
        if (valor > -2147483647 && valor < 2147483647) {
            return true;
        }
        interrupcao = Interruptions.OverFlow;
        return false;
    }

    public boolean trataInterruptEndInv(int endereco) {
        if (endereco >= 0 && endereco < m.length) {
            return true;
        }
        interrupcao = Interruptions.EnderecoInvalido;
        return false;
    }

    public void setContext(int _pc) { // no futuro esta funcao vai ter que ser
        pc = _pc; // limite e pc (deve ser zero nesta versao)
        System.out.println("** aqui");
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

    public void run() { // execucao da CPU supoe que o contexto da CPU, vide acima, esta devidamente
                        // setado
        while (true) { // ciclo de instrucoes. acaba cfe instrucao, veja cada caso.

            if (interrupcao != Interruptions.SemInterrupcao) {
                switch (interrupcao) {
                    case OverFlow:
                        System.out.println("----------Interrupção do tipo: Overflow----------");
                        break;
                    case EnderecoInvalido:
                        System.out.println("----------Interrupção do tipo: Endereço Inválido----------");
                        break;
                    case InstrucaoInvalida:
                        System.out.println("----------Interrupção do tipo: Instrução Inválida----------");
                        break;
                    case ChamadaDeSistema:
                        //lê do teclado
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
                        interrupcao = Interruptions.SemInterrupcao;
                        continue;
                }
                break;
            }
            // FETCH
            ir = m[pc]; // busca posicao da memoria apontada por pc, guarda em ir
            // if debug
            showState();
            // EXECUTA INSTRUCAO NO ir
            switch (ir.opc) { // para cada opcode, sua execução

                /*********** Instruções JUMP ***********/
                case JMP: // PC ← k Dotti
                    if (trataInterruptEndInv(ir.p)) {
                        pc = ir.p;
                    }
                    break;

                case JMPIG: // If Rc > 0 Then PC ← Rs Else PC ← PC +1 Dotti
                    if (trataInterruptEndInv(reg[ir.r1])) {
                        if (reg[ir.r2] > 0) {
                            pc = reg[ir.r1];
                        } else {
                            pc++;
                        }
                    }
                    break;

                case JMPIE: // If Rc = 0 Then PC ← Rs Else PC ← PC +1 Dotti
                    if (trataInterruptEndInv(reg[ir.r1])) {
                        if (reg[ir.r2] == 0) {
                            pc = reg[ir.r1];
                        } else {
                            pc++;
                        }
                    }
                    break;

                case STOP: // por enquanto, para execucao Dotti
                    break;

                case JMPI: // PC ← Rs
                    if (trataInterruptEndInv(reg[ir.r1])) {
                        pc = reg[ir.r1];
                    }
                    break;

                case JMPIL: // Rc < 0 then PC ← Rs else PC ← PC +1
                    if (trataInterruptEndInv(reg[ir.r1])) {
                        if (reg[ir.r2] < 0) {
                            pc = reg[ir.r1];
                        } else {
                            pc++;
                        }
                    }
                    break;

                case JMPIM: // PC ← [A]
                    if (trataInterruptEndInv(ir.p)) {
                        m[ir.p].opc = Opcode.DATA;
                        pc = m[ir.p].p; // ?? ou Opcode.DATA???
                    }
                    break;

                case JMPIGM: // if Rc > 0 then PC ← [A] else PC ← PC +1
                    if (trataInterruptEndInv(ir.p)) {
                        if (reg[ir.r2] > 0) {
                            m[ir.p].opc = Opcode.DATA;
                            pc = m[ir.p].p;
                        } else {
                            pc++;
                        }
                    }
                    break;

                case JMPILM: // if Rc < 0 then PC ← [A] else PC ← PC +1
                    if (trataInterruptEndInv(ir.p)) {
                        if (reg[ir.r2] < 0) {
                            m[ir.p].opc = Opcode.DATA;
                            pc = m[ir.p].p;
                        } else {
                            pc++;
                        }
                    }
                    break;

                case JMPIEM: // if Rc = 0 then PC ← [A] else PC ← PC +1
                    if (trataInterruptEndInv(ir.p)) {
                        if (reg[ir.r2] == 0) {
                            m[ir.p].opc = Opcode.DATA;
                            pc = m[ir.p].p;
                        } else {
                            pc++;
                        }
                    }
                    break;

                /********** Instruções aritméticas ***********/
                case ADD: // Rd ← Rd + Rs
                    int aux = reg[ir.r1] + reg[ir.r2];
                    if (trataInterruptOverflow(reg[ir.r1]) && trataInterruptOverflow(reg[ir.r2])
                            && trataInterruptOverflow(aux)){
                        reg[ir.r1] = reg[ir.r1] + reg[ir.r2];
                        pc++;
                    }
                    break;

                case MULT: // Rd ← Rd * Rs Dotti
                    int aux2 = reg[ir.r1] * reg[ir.r2];
                    if (trataInterruptOverflow(reg[ir.r1]) && trataInterruptOverflow(reg[ir.r2])
                            && trataInterruptOverflow(aux2)){
                        reg[ir.r1] = reg[ir.r1] * reg[ir.r2];
                        pc++;
                    }
                    break;

                case ADDI: // Rd ← Rd + k Dotti
                    int aux3 =  reg[ir.r1] + ir.p;
                    if (trataInterruptOverflow(reg[ir.r1]) && trataInterruptOverflow(ir.p)
                            && trataInterruptOverflow(aux3)) {
                        reg[ir.r1] = reg[ir.r1] + ir.p;
                        pc++;
                    }
                    break;

                case SUB: // Rd ← Rd - Rs Dotti
                    int aux4 = reg[ir.r1] - reg[ir.r2];
                    if (trataInterruptOverflow(reg[ir.r1]) && trataInterruptOverflow(reg[ir.r2])
                            && trataInterruptOverflow(aux4)) {
                        reg[ir.r1] = reg[ir.r1] - reg[ir.r2];
                        pc++;
                    }
                    break;

                case SUBI: // Rd ← Rd – k
                    int aux5 = reg[ir.r1] - ir.p;
                    if (trataInterruptOverflow(reg[ir.r1]) && trataInterruptOverflow(ir.p)
                            && trataInterruptOverflow(aux5)) {
                        reg[ir.r1] = reg[ir.r1] - ir.p;
                        pc++;
                    }
                    break;

                /*********** Instruções de movimentação ***********/
                case LDD: // Rd ← [A]
                    if (trataInterruptEndInv(ir.p) && trataInterruptOverflow(m[ir.p].p)) {
                        m[ir.p].opc = Opcode.DATA;
                        reg[ir.r1] = m[ir.p].p;
                        pc++;
                    }
                    break;

                case LDX: // Rd ← [Rs]
                    if (trataInterruptEndInv(reg[ir.r2]) && trataInterruptOverflow(reg[ir.r1])) {
                        m[ir.r2].opc = Opcode.DATA;
                        reg[ir.r1] = m[reg[ir.r2]].p;
                        pc++;
                    }
                    break;

                case STX: // [Rd] ←Rs Dotti
                    if (trataInterruptEndInv(reg[ir.r1]) && trataInterruptOverflow(reg[ir.r2])) {
                        m[reg[ir.r1]].opc = Opcode.DATA;
                        m[reg[ir.r1]].p = reg[ir.r2];
                        pc++;
                    }
                    break;

                case LDI: // Rd ← k Dotti
                    if (trataInterruptOverflow(ir.p)) {
                        reg[ir.r1] = ir.p;
                        pc++;
                    }
                    break;

                case STD: // [A] ← Rs Dotti
                    if (trataInterruptEndInv(ir.p) && trataInterruptOverflow(m[ir.p].p)) {
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
                    interrupcao = Interruptions.ChamadaDeSistema;
                    pc ++;
                    break; 

                default:
                    // opcode desconhecido
                    interrupcao = Interruptions.InstrucaoInvalida;
            }

            // VERIFICA INTERRUPÇÃO !!! - TERCEIRA FASE DO CICLO DE INSTRUÇÕES
            if (ir.opc == Opcode.STOP) {
                break; // break sai do loop da cpu

                // if int ligada - vai para tratamento da int
                // desviar para rotina java que trata int
            }

            //Adicionado para facilitar o controle do que está acontecendo 
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}