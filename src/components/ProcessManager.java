package components;

import domain.ProcessStatus;
import domain.Word;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static utils.MemoryTranslator.translate;


public class ProcessManager {

    private static ProcessManager instance = null;

    private static int processId = 0;

    public MemoryManager memoryManager;

    public List<ProcessControlBlock> allLoadedProcessControlBlocks;

    public Queue<ProcessControlBlock> readyProcessControlBlocks;

    public Queue<ProcessControlBlock> blockedProcessControlBlocks;

    public static synchronized ProcessManager getInstance() {
        if (instance == null)
            instance = new ProcessManager();

        return instance;
    }


    private ProcessManager() {
        this.memoryManager = new MemoryManager(VM.mem);
        this.allLoadedProcessControlBlocks = new LinkedList<>();
        this.readyProcessControlBlocks = new LinkedList<>();
        this.blockedProcessControlBlocks = new LinkedList<>();
    }

    public boolean someProcessIsRunning() {
        return this.allLoadedProcessControlBlocks.stream().anyMatch(process -> process.getProcessStatus() == ProcessStatus.RUNNING);
    }

    private int nextProcessId() {
        return processId++;
    }

    public ProcessControlBlock loadProgram(Word[] p, String name) {
        int neededFrames = Math.ceilDiv(p.length, VM.mem.pageSize);
        int[] pageTable = new int[neededFrames];
        boolean canLoad = this.memoryManager.allocate(p.length, pageTable);

        if (!canLoad) {
            System.out.println("Memória insuficiente para carregar o programa");
            return null;
        }

        for (int i = 0; i < p.length; i++) {
            int physicalAddress = translate(i, pageTable, VM.mem.pageSize);
            VM.mem.memoryArray[physicalAddress].opCode = p[i].opCode;
            VM.mem.memoryArray[physicalAddress].r1 = p[i].r1;
            VM.mem.memoryArray[physicalAddress].r2 = p[i].r2;
            VM.mem.memoryArray[physicalAddress].p = p[i].p;
        }

        ProcessControlBlock processControlBlock = new ProcessControlBlock(nextProcessId(), pageTable, ProcessStatus.READY, 0, new int[10], name);
        this.allLoadedProcessControlBlocks.add(processControlBlock);
        this.readyProcessControlBlocks.add(processControlBlock);
        return processControlBlock;
    }

    public void deallocate(ProcessControlBlock processControlBlock) {
        memoryManager.deallocate(processControlBlock.getPageTable());
        allLoadedProcessControlBlocks.remove(processControlBlock);
        readyProcessControlBlocks.remove(processControlBlock);
    }

}

