package handlers;

import components.CPU;
import components.ProcessControlBlock;
import components.ProcessManager;
import components.Scheduler;
import domain.Interrupts;
import domain.ProcessStatus;

public class InterruptHandling {

    private final ProcessManager processManager;
    private final CPU cpu;

    public InterruptHandling(CPU cpu) {
        this.processManager = ProcessManager.getInstance();
        this.cpu = cpu;
    }

    public void handle(Interrupts interrupt, int pc, ProcessControlBlock pcb) {   // avisa todas interrupcoes neste momento finalizam o programa
        System.out.println("                                               Interrupcao " + interrupt + "   pc: " + pc);
        switch (interrupt) {
            case clockInterrupt -> handleClock(pc, pcb);
            case intSTOP -> handleStop(pc, pcb);
            case ioPronto -> handleIoPronto();
            default -> pcb.setProcessStatus(ProcessStatus.FINISHED);
        }
    }

    public void handleIoPronto() {
        ProcessControlBlock pcb = processManager.blockedProcessControlBlocks.poll();
        assert pcb != null;
        pcb.setProcessStatus(ProcessStatus.READY);
        processManager.readyProcessControlBlocks.add(pcb);

        // o único processo pronto é este que voltou do IO, não tem nada rodando
        if (Scheduler.schedulerSemaphore.availablePermits() == 0 && !processManager.someProcessIsRunning()) {
            Scheduler.schedulerSemaphore.release();
        }
    }

    public void handleClock(int pc, ProcessControlBlock pcb) {
        cpu.updatePCB(pcb, 0);
        pcb.setProcessStatus(ProcessStatus.READY);
        processManager.readyProcessControlBlocks.add(pcb);
        cpu.resetClockCycles();
        if (Scheduler.schedulerSemaphore.availablePermits() == 0 && !processManager.readyProcessControlBlocks.isEmpty()) {
            Scheduler.schedulerSemaphore.release();
        }
    }

    public void handleStop(int pc, ProcessControlBlock pcb) {
        pcb.setProcessStatus(ProcessStatus.FINISHED);
        this.processManager.deallocate(pcb);
        if (Scheduler.schedulerSemaphore.availablePermits() == 0 && !processManager.readyProcessControlBlocks.isEmpty()) {
            Scheduler.schedulerSemaphore.release();
        }
    }
}