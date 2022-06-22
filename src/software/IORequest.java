package software;

public class IORequest {

    public static enum OperationTypes {
        READ, WRITE
    }

    private ProcessControlBlock process;
    private OperationTypes operationType;

    public IORequest(ProcessControlBlock process, OperationTypes operationType) {
        this.process = process;
        this.operationType = operationType;
    }

    public ProcessControlBlock getProcess() {
        return process;
    }

    public OperationTypes getOperationType() {
        return operationType;
    }
}

// Scanner in = new Scanner(System.in);

// if (reg[8]==1){
// int destino = reg[9];
// System.out.println("Enter an integer value: ");
// int value = in.nextInt();
// m[destino].p = value;
// }
// if (reg[8]==2){
// int ec = reg[9];
// System.out.println("Return: " + m[ec].p);
// }
// setInterruption(TypeInterruptions.NoInterruptions);
