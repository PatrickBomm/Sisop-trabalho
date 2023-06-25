package components;

import domain.ProcessStatus;

import java.util.Arrays;

public class ProcessControlBlock {
    int id;
    int[] pageTable;

    int pc;

    int[] registers;

    ProcessStatus processStatus;

    public ProcessControlBlock(int id, int[] pageTable, ProcessStatus processStatus, int pc, int[] registers) {
        this.id = id;
        this.pageTable = pageTable;
        this.processStatus = processStatus;
        this.pc = pc;
        this.registers = registers;
    }

    public int getId() {
        return id;
    }

    public int[] getPageTable() {
        return pageTable;
    }

    public ProcessStatus getProcessStatus() {
        return processStatus;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPageTable(int[] pageTable) {
        this.pageTable = pageTable;
    }

    public void setProcessStatus(ProcessStatus processStatus) {
        this.processStatus = processStatus;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public void setRegisters(int[] registers) {
        this.registers = registers;
    }

    @Override
    public String toString() {
        return id + "\t\t | " + pc + "\t | " + processStatus + "\t | " + Arrays.toString(pageTable) + "\t\t\t | "
                + Arrays.toString(registers);
    }
}
