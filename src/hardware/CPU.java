package hardware;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import hardware.Interruptions.TypeInterruptions;
import software.Console;
import software.MemoryManager;
import software.ProcessControlBlock;
import software.ShowState;

public class CPU extends Thread{
    private int pc; 
    private Word ir; 
    private int[] reg;
    private TypeInterruptions interruption;
    private Interruptions interruptionsMethods;
    private int[] pageTable;
    private int currentProcessId;
    public Semaphore SEMAPHORE = new Semaphore(0);

    public Word[] m; 
    private int delta;
    private MemoryManager mm;

    public CPU(Word[] _m) { 
        delta = 5;
        m = _m; 
        reg = new int[10]; 
        pageTable = null;
        interruption = TypeInterruptions.NoInterruptions;
        this.currentProcessId = -1;
        interruptionsMethods = new Interruptions(this);
    }

    public int[] getPageTable(){
        return pageTable;
    }

    public int getCurrentProcessId(){
        return currentProcessId;
    }

    public TypeInterruptions getInterruption(){
        return interruption;
    }

    public void setInterruption(TypeInterruptions interrupt){
        interruption =interrupt;
    }

    public boolean overFlowInterrupt(int value) {
        if (value > -2147483647 && value < 2147483647) {
            return true;
        }
        setInterruption(TypeInterruptions.OverFlow);
        return false;
    }

    public boolean invalidAdressInterrupt(int adress) {
        if (adress >= 0 && adress < m.length) {
            return true;
        }
        setInterruption(TypeInterruptions.InvalidAdress);
        return false;
    }

    public void setContext(int _pc) {
        pc = _pc;
    } 
    
    public void showState() {
		System.out.println("       " + pc);
		System.out.print("           ");
		for (int i = 0; i < 9; i++) {
			System.out.print("r" + i);
			System.out.print(": " + reg[i] + "     ");
		}
		System.out.println("");
		System.out.print("           ");
		System.out.println(ShowState.dump(ir));
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

    @Override
    public void run() {
        while (true){
            try {
                int countClock = 0;
                SEMAPHORE.acquire();

                while (true) {                    
                    countClock++;
                    ir = m[MemoryManager.translate(pc, pageTable)]; 
                    int physicalAddress;
                    showState();
                    if(pageTable != null){
                        ir =  m[mm.translate(pc, pageTable)];
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
                                setInterruption(TypeInterruptions.ProgramEndedInterrupt);
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
                                physicalAddress = mm.translate(ir.p, pageTable);
                                if (invalidAdressInterrupt(physicalAddress)) {
                                    m[physicalAddress].opc = Opcode.DATA;
                                    pc = m[physicalAddress].p; 
                                }
                                break;
        
                            case JMPIGM: 
                                physicalAddress = mm.translate(ir.p, pageTable);
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
                                physicalAddress = mm.translate(ir.p, pageTable);
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
                                physicalAddress = mm.translate(ir.p, pageTable);
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
                                physicalAddress = mm.translate(ir.p, pageTable);
                                if (invalidAdressInterrupt(physicalAddress) && overFlowInterrupt(m[ir.p].p)) {
                                    m[physicalAddress].opc = Opcode.DATA;
                                    reg[ir.r1] = m[physicalAddress].p;
                                    pc++;
                                }
                                break;
        
                            case LDX: 
                                physicalAddress = mm.translate(reg[ir.r2], pageTable);
                                if (invalidAdressInterrupt(reg[physicalAddress]) && overFlowInterrupt(reg[ir.r1])) {
                                    m[physicalAddress].opc = Opcode.DATA;
                                    reg[ir.r1] = m[physicalAddress].p;
                                    pc++;
                                }
                                break;
        
                            case STX: 
                                physicalAddress = mm.translate(reg[ir.r1], pageTable);
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
                                physicalAddress = mm.translate(ir.p, pageTable);
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
                                setInterruption(TypeInterruptions.SystemCall);
                                pc ++;
                                break; 
        
                            default:
                                setInterruption(TypeInterruptions.InvalidInstruction);
                        }
                    
                        if (ir.opc == Opcode.STOP) {
                            setInterruption(TypeInterruptions.ProgramEndedInterrupt);
                            break;
                        }
                    }
        
                    if (countClock  == delta) {
                        setInterruption(TypeInterruptions.ClockInterrupt);
                        break;
                    }
        
                    if(getInterruption() == TypeInterruptions.NoInterruptions){
                        if (Console.FINISHED_IO_PROCESS_IDS.size() > 0) {
                            setInterruption(TypeInterruptions.IoFinishedInterrupt);
                            break;
                        }
                        continue;
                    }
                    
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (interruption != TypeInterruptions.NoInterruptions) {
                    switch (interruption) {
                        case OverFlow:
                            System.out.println("Type Interruption: Overflow");
                            break;
    
                        case InvalidAdress:
                            System.out.println("Type Interruption: Invalid Adress");
                            break;
    
                        case InvalidInstruction:
                            System.out.println("Type Interruption: Invalid Instruction");
                            break;
    
                        case SystemCall:
                            Scanner in = new Scanner(System.in);
    
                            if (reg[8]==1){
                                int destino = reg[9];
                                System.out.println("Enter an integer value: ");
                                int value = in.nextInt();
                                m[destino].p = value;
                            }
                            if (reg[8]==2){
                                int ec = reg[9];
                                System.out.println("Return: " + m[ec].p);
                            }
                            setInterruption(TypeInterruptions.NoInterruptions);
                            continue;
    
                        case ClockInterrupt:
                            System.out.println("Type Interruption: Max CPU cycle reached");
                            interruptionsMethods.saveProcess();
                            break;
    
                        case IoFinishedInterrupt:
                            System.out.println("Type Interruption: Finished IO");
                            interruptionsMethods.ioFinishedRoutine();
                            break;
    
                        case ProgramEndedInterrupt:
                            System.out.println("Type Interruption: Program ended");
                            interruptionsMethods.endProcess();
                            break;
                            
                        default:
                            break;
                    }
                }         
            } catch (Exception ex) {
				ex.printStackTrace();
			}
        }
        
    }
}