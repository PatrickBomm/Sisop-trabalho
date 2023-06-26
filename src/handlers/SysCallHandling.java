package handlers;

import components.*;
import domain.*;

public class SysCallHandling {

    private final CPU cpu;
    private final ProcessManager processManager;

    public SysCallHandling(CPU cpu) {
        this.cpu = cpu;
        this.processManager = ProcessManager.getInstance();
    }

    public void handle(ProcessControlBlock pcb) {   // apenas avisa - todas interrupcoes neste momento finalizam o programa
        int operation = cpu.registers[8];
        int memoryAddress = cpu.registers[9];

        if (operation != 1 && operation != 2) {
            cpu.interrupt = Interrupts.intInstrucaoInvalida;
            return;
        }

        if (cpu.canAccessMemory(memoryAddress)) {
            // requeriu IO, entrando para fila de bloqueados
            pcb.setProcessStatus(ProcessStatus.BLOCKED);
            processManager.blockedProcessControlBlocks.add(pcb);

            // pedir IO
            IOType requestType = operation == 1 ? IOType.READ : IOType.WRITE;
            IORequest request = new IORequest(requestType, memoryAddress);
            Console.requests.add(request);
            Console.consoleSemaphore.release();

            if (Scheduler.schedulerSemaphore.availablePermits() == 0 && !processManager.readyProcessControlBlocks.isEmpty()) {
                Scheduler.schedulerSemaphore.release();
            }
        }
        System.out.println("                                               Chamada de components.Sistema com op  /  par:  " + cpu.registers[8] + " / " + cpu.registers[9]);
    }
}
