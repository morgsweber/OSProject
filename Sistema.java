// PUCRS - Escola Politécnica - Sistemas Operacionais
// Prof. Fernando Dotti
// Código fornecido como parte da solução do projeto de Sistemas Operacionais
//
// Fase 1 - máquina virtual (vide enunciado correspondente)
//

import java.util.*;

public class Sistema {

	// -------------------------------------------------------------------------------------------------------
	// --------------------- H A R D W A R E - definicoes de HW
	// ----------------------------------------------

	// -------------------------------------------------------------------------------------------------------
	// --------------------- M E M O R I A - definicoes de opcode e palavra de
	// memoria ----------------------

	public class Word { // cada posicao da memoria tem uma instrucao (ou um dado)
		public Opcode opc; //
		public int r1; // indice do primeiro registrador da operacao (Rs ou Rd cfe opcode na tabela)
		public int r2; // indice do segundo registrador da operacao (Rc ou Rs cfe operacao)
		public int p; // parametro para instrucao (k ou A cfe operacao), ou o dado, se opcode = DADO

		public Word(Opcode _opc, int _r1, int _r2, int _p) {
			opc = _opc;
			r1 = _r1;
			r2 = _r2;
			p = _p;
		}
	}
	// -------------------------------------------------------------------------------------------------------

	// -------------------------------------------------------------------------------------------------------
	// --------------------- C P U - definicoes da CPU
	// -----------------------------------------------------

	public enum Opcode {
		DATA, ___, // se memoria nesta posicao tem um dado, usa DATA, se nao usada ee NULO ___
		JMP, JMPI, JMPIG, JMPIL, JMPIE, JMPIM, JMPIGM, JMPILM, JMPIEM, STOP, // desvios e parada
		ADDI, SUBI, ADD, SUB, MULT, // matematicos
		LDI, LDD, STD, LDX, STX, SWAP, // movimentacao
		TRAP;
	}

	public enum Interruptions {
		OverFlow, EnderecoInvalido, InstrucaoInvalida, SemInterrupcao;
	}

	public class CPU {
		// característica do processador: contexto da CPU ...
		private int pc; // ... composto de program counter,
		private Word ir; // instruction register,
		private int[] reg; // registradores da CPU
		private Interruptions interrupcao;

		private Word[] m; // CPU acessa MEMORIA, guarda referencia 'm' a ela. memoria nao muda. ee sempre
							// a mesma.

		public CPU(Word[] _m) { // ref a MEMORIA e interrupt handler passada na criacao da CPU
			m = _m; // usa o atributo 'm' para acessar a memoria.
			reg = new int[8]; // aloca o espaço dos registradores
			interrupcao = Interruptions.SemInterrupcao;
		}

		public boolean trataInterruptOverflow(int valor) {
			if (valor > Integer.MIN_VALUE && valor < Integer.MAX_VALUE) {
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
			for (int i = 0; i < 8; i++) {
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
								System.out.print(pc);
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
						if (trataInterruptOverflow(reg[ir.r1]) && trataInterruptOverflow(reg[ir.r2])
								&& trataInterruptOverflow(reg[ir.r1] + reg[ir.r2])) {
							reg[ir.r1] = reg[ir.r1] + reg[ir.r2];
							pc++;
						}
						break;

					case MULT: // Rd ← Rd * Rs Dotti
						if (trataInterruptOverflow(reg[ir.r1]) && trataInterruptOverflow(reg[ir.r2])
								&& trataInterruptOverflow(reg[ir.r1] * reg[ir.r2])) {
							reg[ir.r1] = reg[ir.r1] * reg[ir.r2];
							pc++;
						}
						break;

					case ADDI: // Rd ← Rd + k Dotti
						if (trataInterruptOverflow(reg[ir.r1]) && trataInterruptOverflow(ir.p)
								&& trataInterruptOverflow(reg[ir.r1] + ir.p)) {
							reg[ir.r1] = reg[ir.r1] + ir.p;
							pc++;
						}
						break;

					case SUB: // Rd ← Rd - Rs Dotti
						if (trataInterruptOverflow(reg[ir.r1]) && trataInterruptOverflow(reg[ir.r2])
								&& trataInterruptOverflow(reg[ir.r1] - reg[ir.r2])) {
							reg[ir.r1] = reg[ir.r1] - reg[ir.r2];
							pc++;
						}
						break;

					case SUBI: // Rd ← Rd – k
						if (trataInterruptOverflow(reg[ir.r1]) && trataInterruptOverflow(ir.p)
								&& trataInterruptOverflow(reg[ir.r1] - ir.p)) {
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
						//le os registradores 8 e 9 

					default:
						// opcode desconhecido
						// liga interrup (2)
						interrupcao = Interruptions.InstrucaoInvalida;

				}

				// VERIFICA INTERRUPÇÃO !!! - TERCEIRA FASE DO CICLO DE INSTRUÇÕES
				if (ir.opc == Opcode.STOP) {
					break; // break sai do loop da cpu

					// if int ligada - vai para tratamento da int
					// desviar para rotina java que trata int
				}

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	// ------------------ C P U - fim
	// ------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------

	// ------------------- V M - constituida de CPU e MEMORIA
	// -----------------------------------------------
	// -------------------------- atributos e construcao da VM
	// -----------------------------------------------
	public class VM {
		public int tamMem;
		public Word[] m;
		public CPU cpu;

		public VM() {
			// memória
			tamMem = 1024;
			m = new Word[tamMem]; // m ee a memoria
			for (int i = 0; i < tamMem; i++) {
				m[i] = new Word(Opcode.___, -1, -1, -1);
			}
			;
			// cpu
			cpu = new CPU(m); // cpu acessa memória
		}
	}
	// ------------------- V M - fim
	// ------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------

	// --------------------H A R D W A R E - fim
	// -------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------

	// -------------------------------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------
	// ------------------- S O F T W A R E - inicio
	// ----------------------------------------------------------

	// ------------------------------------------- funcoes de um monitor
	public class Monitor {
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

		public void carga(Word[] p, Word[] m) { // significa ler "p" de memoria secundaria e colocar na principal "m"
			for (int i = 0; i < p.length; i++) {
				m[i].opc = p[i].opc;
				m[i].r1 = p[i].r1;
				m[i].r2 = p[i].r2;
				m[i].p = p[i].p;
			}
		}

		public void executa() {
			vm.cpu.setContext(0); // monitor seta contexto - pc aponta para inicio do programa
			vm.cpu.run(); // e cpu executa
							// note aqui que o monitor espera que o programa carregado acabe normalmente
							// nao ha protecoes... o que poderia acontecer ?
		}
	}
	// -------------------------------------------

	// -------------------------------------------------------------------------------------------------------
	// ------------------- S I S T E M A
	// --------------------------------------------------------------------

	public VM vm;
	public Monitor monitor;
	public static Programas progs;

	public Sistema() { // a VM com tratamento de interrupções
		vm = new VM();
		monitor = new Monitor();
		progs = new Programas();
	}

	public void roda(Word[] programa) {
		monitor.carga(programa, vm.m);
		System.out.println("---------------------------------- programa carregado ");
		monitor.dump(vm.m, 0, programa.length);
		monitor.executa();
		System.out.println("---------------------------------- após execucao ");
		monitor.dump(vm.m, 0, programa.length);

	}

	// ------------------- S I S T E M A - fim
	// --------------------------------------------------------------
	// -------------------------------------------------------------------------------------------------------

	// -------------------------------------------------------------------------------------------------------
	// ------------------- instancia e testa sistema
	public static void main(String args[]) {
		Sistema s = new Sistema();
		s.roda(progs.pa); // "progs" significa acesso/referencia ao programa em memoria secundaria
		//s.roda(progs.pb);
		//s.roda(progs.pc);
	}

	// -------------------------------------------------------------------------------------------------------
	// --------------- TUDO ABAIXO DE MAIN É AUXILIAR PARA FUNCIONAMENTO DO SISTEMA
	// - nao faz parte

	// -------------------------------------------- programas aa disposicao para
	// copiar na memoria (vide carga)
	public class Programas {

		public Word[] progMinimo = new Word[] {
				// OPCODE R1 R2 P :: VEJA AS COLUNAS VERMELHAS DA TABELA DE DEFINICAO DE
				// OPERACOES
				// :: -1 SIGNIFICA QUE O PARAMETRO NAO EXISTE PARA A OPERACAO DEFINIDA
				new Word(Opcode.LDI, 0, -1, 999),
				new Word(Opcode.STD, 0, -1, 10),
				new Word(Opcode.STD, 0, -1, 11),
				new Word(Opcode.STD, 0, -1, 12),
				new Word(Opcode.STD, 0, -1, 13),
				new Word(Opcode.STD, 0, -1, 14),
				new Word(Opcode.STOP, -1, -1, -1) };

		public Word[] fibonacci10 = new Word[] { // mesmo que prog exemplo, so que usa r0 no lugar de r8
				new Word(Opcode.LDD, 1, -1, 0),
				new Word(Opcode.STD, 1, -1, 20), // 20 posicao de memoria onde inicia a serie de fibonacci gerada
				new Word(Opcode.LDI, 2, -1, 1),
				new Word(Opcode.STD, 2, -1, 21),
				new Word(Opcode.LDI, 0, -1, 22),
				new Word(Opcode.LDI, 6, -1, 6),
				new Word(Opcode.LDI, 7, -1, 30),
				new Word(Opcode.LDI, 3, -1, 0),
				new Word(Opcode.ADD, 3, 1, -1),
				new Word(Opcode.LDI, 1, -1, 0),
				new Word(Opcode.ADD, 1, 2, -1),
				new Word(Opcode.ADD, 2, 3, -1),
				new Word(Opcode.STX, 0, 2, -1),
				new Word(Opcode.ADDI, 0, -1, 1),
				new Word(Opcode.SUB, 7, 0, -1),
				new Word(Opcode.JMPIG, 6, 7, -1),
				new Word(Opcode.STOP, -1, -1, -1), // POS 16
				new Word(Opcode.DATA, -1, -1, -1),
				new Word(Opcode.DATA, -1, -1, -1),
				new Word(Opcode.DATA, -1, -1, -1),
				new Word(Opcode.DATA, -1, -1, -1), // POS 20
				new Word(Opcode.DATA, -1, -1, -1),
				new Word(Opcode.DATA, -1, -1, -1),
				new Word(Opcode.DATA, -1, -1, -1),
				new Word(Opcode.DATA, -1, -1, -1),
				new Word(Opcode.DATA, -1, -1, -1),
				new Word(Opcode.DATA, -1, -1, -1),
				new Word(Opcode.DATA, -1, -1, -1),
				new Word(Opcode.DATA, -1, -1, -1),
				new Word(Opcode.DATA, -1, -1, -1) // ate aqui - serie de fibonacci ficara armazenada
		};

		public Word[] fatorial = new Word[] { // este fatorial so aceita valores positivos. nao pode ser zero
												// linha coment
				new Word(Opcode.LDI, 0, -1, 6), // 0 r0 é valor a calcular fatorial
				new Word(Opcode.LDI, 1, -1, 1), // 1 r1 é 1 para multiplicar (por r0)
				new Word(Opcode.LDI, 6, -1, 1), // 2 r6 é 1 para ser o decremento
				new Word(Opcode.LDI, 7, -1, 8), // 3 r7 tem posicao de stop do programa = 8
				new Word(Opcode.JMPIE, 7, 0, 0), // 4 se r0=0 pula para r7(=8)
				new Word(Opcode.MULT, 1, 0, -1), // 5 r1 = r1 * r0
				new Word(Opcode.SUB, 0, 6, -1), // 6 decrementa r0 1
				new Word(Opcode.JMP, -1, -1, 4), // 7 vai p posicao 4
				new Word(Opcode.STD, 1, -1, 10), // 8 coloca valor de r1 na posição 10
				new Word(Opcode.STOP, -1, -1, -1), // 9 stop
				new Word(Opcode.DATA, -1, -1, -1)
		}; // 10 ao final o valor do fatorial estará na posição 10 da memória

		/*********** Programas implementados ***********/
		// PA: um programa que le um valor de uma determinada posição (carregada no
		// inicio),
		// se o número for menor que zero coloca -1 no início da posição de memória para
		// saída;
		// se for maior que zero este é o número de valores da sequencia de fibonacci a
		// serem escritos em sequencia a partir de uma posição de memória;
		public Word[] pa = new Word[] {
				new Word(Opcode.LDI, 0, -1, 5), 	// 0 carrega o valor 5 no registrador 0
				new Word(Opcode.STD, 0, -1, 37), 	// 1 pega o valor do r0 e coloca na posição 37 da memória
				new Word(Opcode.LDD, 1, -1, 37), 	// 2 pega o valor da posição 37 da memória e coloca em r1
				new Word(Opcode.SUBI, 1, -1, 1), 	// 3 subtrai 1 do valor que entrou (no caso 5), por conta do zero: 0-4 (5 valores)
				new Word(Opcode.LDI, 2, -1, 900), 	// 4 registrador que vai controlar o incremento de posição de memória
				new Word(Opcode.LDI, 7, -1, 25),	// 5 carrega o valor 25 no registrador 7
				new Word(Opcode.JMPIL, 7, 1, -1), 	// 6 verifica se o valor no r1 é menor que 0, se sim, pula p/ instrução 25, guardada no r7, se não, segue o fluxo
				new Word(Opcode.LDI, 5, -1, 1), 	// 7 carrega o primeiro valor de Fibonacci (1) no r5
				new Word(Opcode.STX, 2, 5, -1), 	// 8 pega o valor do r5 e carrega na posição 900 (valor armazenado no r2)
				new Word(Opcode.SUBI, 1, -1, 1), 	// 9 subtrai 1 dos valores do r1
				new Word(Opcode.JMPIL, 7, 1, -1), 	// 10 se o valor do r1 for menor que 0, pula para instrução 25
				new Word(Opcode.ADDI, 2, -1, 1), 	// 11 soma 1 ao valor que está no r2 (posição de memória)
				new Word(Opcode.LDI, 6, -1, 1), 	// 12 carrega o segundo valor de Fibonacci (1) no r6
				new Word(Opcode.STX, 2, 6, -1), 	// 13 carrega o valor de r6 na próxima posição da memória (r2)
				new Word(Opcode.SUBI, 1, -1, 1), 	// 14 subtrai 1 do r1
				new Word(Opcode.JMPIL, 7, 1, -1), 	// 16 se r1 menor do que 0, pula para instrução 25
				new Word(Opcode.ADDI, 2, -1, 1), 	// 17 soma 1 na posição de memória
				new Word(Opcode.ADD, 5, 6, -1), 	// 18 soma os valores de r5 e r6
				new Word(Opcode.SWAP, 5, 6, -1), 	// 19 faz um swap dos registradores r5 e r6 para manter a ordem correta para somar
				new Word(Opcode.STX, 2, 6, -1), 	// 20 carrega o valor de r6 (soma) para memória
				new Word(Opcode.LDI, 4, -1, 14), 	//21 carrega o valor 14 no registrador 4
				new Word(Opcode.JMPIG, 4, 1, -1), 	// 22 se o que tem no r1 é maior que 0, volta pra instrução 14, se não, segue
				new Word(Opcode.JMPIL, 7, 1, -1), 	// 23 se o que tem no r1 for menor que 0, pula pra instrução 26
				new Word(Opcode.LDI, 3, -1, -1), 	// 24 carrega -1 no r3
				new Word(Opcode.STX, 2, 3, -1), 	// 25 pega o que tá no r3 (-1) e coloca na posição de memória do r2 (900)
				new Word(Opcode.STOP, -1, -1, -1),  // 26 para o programa
		};

		// PB: dado um inteiro em alguma posição de memória,
		// se for negativo armazena -1 na saída; se for positivo responde o fatorial do
		// número na saída
		public Word[] pb = new Word[] {
				new Word(Opcode.LDI, 0, -1, 3), // 0 carrega o valor 3 no r0
				new Word(Opcode.STD, 0, -1, 36), // 1 carrega o valor do r0 na posição 36 da memória
				new Word(Opcode.LDD, 1, -1, 36), // 2 pega o valor da posição 36 da memória e coloca em r1 (3)
				new Word(Opcode.LDI, 5, -1, 12),	//3 carrega o valor 12 no r5
				new Word(Opcode.JMPIL, 5, 1, -1), // 4 se o que tem no registrador r1 for menor que 0, pula pra instrução guardada no r5 (12), se não, segue
				new Word(Opcode.LDD, 2, -1, 36), // 5 carrega em r2 o mesmo valor de r1 (3)
				new Word(Opcode.SUBI, 1, -1, 1), // 6 subtrai 1 do valor que tinha em r1
				new Word(Opcode.MULT, 2, 1, -1), // 7 multiplica r1 e r2 e coloca em r2 o valor
				new Word(Opcode.SUBI, 1, -1, 1), // 8 subtrai 1 do r1
				new Word(Opcode.LDI, 6, -1, 7),  // 9 carrega o valor 7 (próxima instrução) no r6
				new Word(Opcode.JMPIG, 6, 1, -1), // 10 se for maior que 0 o que tem no r1, volta pra instrução 7 se não, segue o fluxo
				new Word(Opcode.STD, 2, -1, 35), // 11 coloca na posição 35 da memória o resultado do fatorial, que está no r2
				new Word(Opcode.JMP, -1, -1, 13), // 12 pula pro stop
				new Word(Opcode.LDI, 1, -1, -1), // 13 carrega no r1 o valor -1
				new Word(Opcode.STD, 1, -1, 35), // 14 coloca na posição 35 da memória o valor -1
				new Word(Opcode.STOP, -1, -1, -1) // 15 termina a execução
		};

		// PC: para um N definido (10 por exemplo)
		// o programa ordena um vetor de N números em alguma posição de memória;
		// ordena usando bubble sort
		// loop ate que não swap nada
		// passando pelos N valores
		// faz swap de vizinhos se da esquerda maior que da direita
		public Word[] pc = new Word[] {
				new Word(Opcode.LDI, 0, -1, 9), 	//0 carrega os 3 valores na memória (9,8,7)
				new Word(Opcode.STD, 0, -1, 700),	//1
				new Word(Opcode.LDI, 0, -1, 8), 	//2
				new Word(Opcode.STD, 0, -1, 701),	//3
				new Word(Opcode.LDI, 0, -1, 7), 	//4
				new Word(Opcode.STD, 0, -1, 702),	//5
				new Word(Opcode.LDI, 0, -1, 702), 	//6	

				new Word(Opcode.LDI, 1, -1, 700), 	//7
				new Word(Opcode.STD, 1, -1, 50), 	//8 salva na memória qual é a primeira posição do array na memória
				new Word(Opcode.LDI, 2, 1, 701), 	//9
				new Word(Opcode.LDX, 4, 1, -1), 	//10 carrega nos registradores os valores que serão comparados 
				new Word(Opcode.LDX, 5, 2, -1), 	//11

				new Word(Opcode.SUB, 4, 5, -1), 	//12 subtrai os dois primeiros valores (9-8)
				new Word(Opcode.LDI, 6, -1, 25),  	//13 armazena para onde terá que ir 
				new Word(Opcode.JMPIL, 6, 4, -1), 	//14 se o que tem no r4 (1) menor que zero, pula pra próxima instrução
				new Word(Opcode.LDX, 4, 1, -1), 	//15 volta o valor de r4
				new Word(Opcode.SWAP, 4, 5, -1), 	//16 faz swap entre os registadores (8-9)
				new Word(Opcode.STX, 1, 4, -1), 	//17 salva 8 na posição 700
				new Word(Opcode.STX, 2, 5, -1), 	//18 salva 9 na posição 701
				
				new Word(Opcode.STD, 2, -1, 50), 	//19 salva na posição 50 da memória qual última posição que testou
				new Word(Opcode.SUB, 2, 0, -1), 	//20 verifica se chegou no fim do array
				new Word(Opcode.LDI, 7, -1, 7), 	//21 se chegou no fim, pula pra instrução 
				new Word(Opcode.JMPIE, 7, 2, -1),	//22 se for igual a zero, chegou ao fim, começa a comparar de novo 

				new Word(Opcode.LDD, 2, -1, 50), 	//23 volta o valor do r2
				new Word(Opcode.ADDI, 2, -1, 1), 	//24 valor anterior
				new Word(Opcode.ADDI, 1, -1, 1), 	//25 caminha
				new Word(Opcode.ADDI, 2, -1, 1), 	//26 caminha
				new Word(Opcode.LDI, 7, -1, 10), 	//27 coloca os valores em r4 e r5
			
				new Word(Opcode.JMP, -1, -1, 7), 	//28
		};
	}
}

/*
 * Interrupções:
 */
