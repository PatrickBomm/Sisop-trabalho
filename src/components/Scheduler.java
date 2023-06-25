package components;

import java.util.LinkedList;
import java.util.Queue;

public class Scheduler {
    private Queue<ProcessControlBlock> readyQueue;

    public Scheduler() {
        readyQueue = new LinkedList<>();
    }

    public void addProcess(ProcessControlBlock pcb) {
        readyQueue.add(pcb);
    }

    public ProcessControlBlock getNextProcess() {
        return readyQueue.poll();
    }

    public boolean hasProcesses() {
        return !readyQueue.isEmpty();
    }
}
