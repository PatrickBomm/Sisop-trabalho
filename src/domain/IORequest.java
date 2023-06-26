package domain;

public class IORequest {
    IOType type;
    int memoryAddress;

    public IORequest(IOType type, int memoryAddress) {
        this.type = type;
        this.memoryAddress = memoryAddress;
    }

    public IOType getType() {
        return type;
    }

    public int getMemoryAddress() {
        return memoryAddress;
    }
}
