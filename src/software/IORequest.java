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
