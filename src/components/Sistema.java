package components;// PUCRS - Escola Politécnica - Sistemas Operacionais

// Prof. Fernando Dotti
// Código fornecido como parte da solução do projeto de Sistemas Operacionais
//
// components.VM
//    HW = memória, cpu
//    SW = tratamento int e chamada de sistema
// Funcionalidades de carga, execução e dump de memória

import domain.ProcessStatus;
import domain.Word;
import handlers.InterruptHandling;
import handlers.SysCallHandling;

import java.util.LinkedList;
import java.util.List;

import static utils.MemoryTranslator.translate;

public class Sistema {
    private static int processId = 0;
    public VM vm;
    public InterruptHandling interruptHandling;
    public SysCallHandling sysCall;

    public MemoryManager memoryManager;

    public List<ProcessControlBlock> allLoadedProcessControlBlocks;

    public List<ProcessControlBlock> readyProcessControlBlocks;

    public Sistema() { // a components.VM com tratamento de interrupções
        interruptHandling = new InterruptHandling();
        sysCall = new SysCallHandling();
        vm = new VM(interruptHandling, sysCall);
        memoryManager = new MemoryManager(vm.mem);
        sysCall.setVM(vm);
        allLoadedProcessControlBlocks = new LinkedList<>();
        readyProcessControlBlocks = new LinkedList<>();
    }

    public ProcessControlBlock loadProgram(Word[] p) {
        int neededFrames = Math.ceilDiv(p.length, this.vm.mem.pageSize);
        int[] pageTable = new int[neededFrames];
        boolean canLoad = this.memoryManager.allocate(p.length, pageTable);

        if (!canLoad) {
            System.out.println("Memória insuficiente para carregar o programa");
            return null;
        }

        for (int i = 0; i < p.length; i++) {
            int physicalAddress = translate(i, pageTable, vm.mem.pageSize);
            vm.mem.memoryArray[physicalAddress].opCode = p[i].opCode;
            vm.mem.memoryArray[physicalAddress].r1 = p[i].r1;
            vm.mem.memoryArray[physicalAddress].r2 = p[i].r2;
            vm.mem.memoryArray[physicalAddress].p = p[i].p;
        }

        ProcessControlBlock processControlBlock = new ProcessControlBlock(nextProcessId(), pageTable,
                ProcessStatus.READY, 0, new int[10]);
        this.allLoadedProcessControlBlocks.add(processControlBlock);
        this.readyProcessControlBlocks.add(processControlBlock);
        return processControlBlock;
    }

    public void exec(ProcessControlBlock processControlBlock) {
        vm.cpu.setContext(0, vm.memorySize - 1, 0); // seta estado da cpu ]
        processControlBlock.setProcessStatus(ProcessStatus.RUNNING);
        vm.cpu.run(processControlBlock.pageTable); // cpu roda programa ate parar
        updatePCB(processControlBlock);
    }

    public void updatePCB(ProcessControlBlock pcb) {
        pcb.setRegisters(vm.cpu.registers);
        pcb.setPc(vm.cpu.getPc());
    }

    public void loadAndExec(Word[] p) {
        ProcessControlBlock processControlBlock = loadProgram(p); // carga do programa na memoria
        System.out.println("---------------------------------- programa carregado na memoria");
        vm.mem.dump(0, p.length); // dump da memoria nestas posicoes
        vm.cpu.setContext(0, vm.memorySize - 1, 0); // seta estado da cpu ]
        System.out.println("---------------------------------- inicia execucao ");
        exec(processControlBlock);
        System.out.println("---------------------------------- memoria após execucao ");
        vm.mem.dump(0, p.length); // dump da memoria com resultado
        System.out.println("---------------------------------- memoria após desalocação ");
        this.memoryManager.deallocate(processControlBlock.pageTable);
        vm.mem.dump(0, p.length); // dump da memoria sem o processo
    }

    private int nextProcessId() {
        return processId++;
    }
}
