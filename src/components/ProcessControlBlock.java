package components;

import domain.ProcessStatus;

import java.util.Arrays;

public class ProcessControlBlock {
    int id;
    int[] pageTable;

    int pc;
    String name;
    int[] registers;

    int clockCount;

    ProcessStatus processStatus;

    public ProcessControlBlock(int id, int[] pageTable, ProcessStatus processStatus, int pc, int[] registers, String name) {
        this.id = id;
        this.pageTable = pageTable;
        this.processStatus = processStatus;
        this.pc = pc;
        this.registers = registers;
        this.name = name;
    }

    public int getClockCount() {
        return clockCount;
    }

    public void setClockCount(int clockCount) {
        this.clockCount = clockCount;
    }

    public int getId() {
        return id;
    }

    public int[] getPageTable() {
        return pageTable;
    }

    public int getPc() {
        return pc;
    }

    public ProcessStatus getProcessStatus() {
        return processStatus;
    }

    public String getName() {
        return "Processo: " + name;
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
        return id + "\t\t | " + pc + "\t | " + processStatus + "\t | " + Arrays.toString(pageTable) + "\t\t\t | " + Arrays.toString(registers);
    }
}
