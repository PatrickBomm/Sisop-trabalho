import components.ProcessControlBlock;
import components.Scheduler;
import components.Sistema;
import domain.ProcessStatus;
import executable.Programas;

import java.util.*;

import static utils.MemoryTranslator.getEndFrame;
import static utils.MemoryTranslator.getStartFrame;

public class Main {
    static Sistema sistema = new Sistema();
    static Scheduler scheduler = new Scheduler();
    static Scanner in = new Scanner(System.in);
    static ProcessControlBlock runningProcess = null;

    public static void main(String[] args) {
        System.out.println("----------------------------------------------------");
        System.out.println("------------------Menu de comandos------------------\n");
        System.out.println(
                "----------------------Comandos----------------------\nCria.\nlistaProcessos.\nDump.\nDesaloca.\nDumpM.\nExecuta.\nTraceOn.\nTraceOff.\nExit.");
        System.out.println("----------------------------------------------------");

        while (true) {
            System.out.print("comando ~: ");
            String readLine = in.nextLine();
            String command = readLine.split(" ")[0];
            List<String> programNames = Arrays.asList("fatorial", "progMinimo", "fibonacci10", "fatorialTRAP",
                    "fibonacciTRAP", "PB", "PC");
            switch (command) {
                case "cria":
                    String programName = readLine.split(" ")[1];
                    if (!programNames.contains(programName)) {
                        System.out.println("Invalid program name.");
                    } else {
                        int processId = cria(programName);
                        System.out.println("Processo " + processId + " criado");
                    }
                    break;

                case "listaProcessos":
                    listProcessos();
                    break;

                case "dump":
                    if (readLine.split(" ")[1] == null) {
                        System.out.println("Digite o id do processo");
                        break;
                    }
                    int idInt = Integer.parseInt(readLine.split(" ")[1]);
                    dump(idInt);
                    break;

                case "desaloca":
                    int idDesaloca = Integer.parseInt(readLine.split(" ")[1]);
                    desaloca(idDesaloca);
                    break;

                case "dumpM":
                    if (readLine.split(" ")[1] == null || readLine.split(" ")[2] == null) {
                        System.out.println("Digite o inicio e o fim");
                        break;
                    }
                    int start = Integer.parseInt(readLine.split(" ")[1]);
                    int end = Integer.parseInt(readLine.split(" ")[2]);
                    dumpM(start, end);
                    break;

                case "executa":
                    if (readLine.split(" ")[1] == null) {
                        System.out.println("Digite o id do processo");
                        break;
                    }
                    int idExec = Integer.parseInt(readLine.split(" ")[1]);
                    if (runningProcess != null) {
                        System.out.println("Processo " + runningProcess.getId() + " interrompido");
                        sistema.updatePCB(runningProcess);
                        runningProcess.setProcessStatus(ProcessStatus.READY);
                    }
                    executa(idExec);
                    scheduler.addProcess(runningProcess);
                    if (runningProcess == null && scheduler.hasProcesses()) {
                        runningProcess = scheduler.getNextProcess();
                        System.out.println("Processo " + runningProcess.getId() + " iniciado");
                        sistema.exec(runningProcess);
                        runningProcess.setProcessStatus(ProcessStatus.FINISHED);
                        runningProcess = null;
                    }

                    break;

                case "traceOn":
                    traceOn();
                    break;

                case "traceOff":
                    traceOff();
                    break;

                case "exit":
                    System.out.println("Ending system");
                    System.exit(0);
                    in.close();
                    break;

                default:
                    System.out.println("Invalid command");
                    break;
            }
        }
    }

    static int cria(String nomePrograma) {
        Programas programas = new Programas();
        ProcessControlBlock processControlBlock = switch (nomePrograma) {
            case "fatorial" -> sistema.loadProgram(programas.fatorial);
            case "progMinimo" -> sistema.loadProgram(programas.progMinimo);
            case "fibonacci10" -> sistema.loadProgram(programas.fibonacci10);
            case "fatorialTRAP" -> sistema.loadProgram(programas.fatorialTRAP);
            case "fibonacciTRAP" -> sistema.loadProgram(programas.fibonacciTRAP);
            case "PB" -> sistema.loadProgram(programas.PB);
            case "PC" -> sistema.loadProgram(programas.PC);
            default -> null;
        };

        if (processControlBlock == null) {
            System.out.println("Nome do programa inválido, tente novamente");
            return 0;
        }

        return processControlBlock.getId();
    }

    static void listProcessos() {
        System.out.println("\nID \t| Status dos processos");
        sistema.allLoadedProcessControlBlocks
                .forEach(p -> System.out.println(p.getId() + "\t| " + p.getProcessStatus()));
    }

    static void dump(int processId) {
        ProcessControlBlock processControlBlock = sistema.allLoadedProcessControlBlocks.stream()
                .filter(p -> p.getId() == processId).findFirst().orElse(null);
        if (processControlBlock == null) {
            System.out.println("Processo com id" + processId + " não encontrado");
            return;
        }

        System.out.println("\nPCB para processo " + processId);
        System.out.println("pid \t | pc \t | status \t | page table \t | registers");
        System.out.println(processControlBlock);

        System.out.println("\nDump de memória para processo " + processId);

        int pageSize = sistema.vm.mem.pageSize;
        int[] pageTable = processControlBlock.getPageTable();

        for (int i = 0; i < pageTable.length; i++) {
            int frame = pageTable[i];
            System.out.println("\n página " + i + " frame " + frame);
            sistema.vm.mem.dump(getStartFrame(frame, pageSize), getEndFrame(frame, pageSize) + 1);
        }
    }

    static void desaloca(int processId) {
        ProcessControlBlock processControlBlock = sistema.allLoadedProcessControlBlocks.stream()
                .filter(p -> p.getId() == processId).findFirst().orElse(null);
        if (processControlBlock == null) {
            System.out.println("Processo com id" + processId + " não encontrado");
            return;
        }

        sistema.memoryManager.deallocate(processControlBlock.getPageTable());
        sistema.allLoadedProcessControlBlocks.remove(processControlBlock);
        sistema.readyProcessControlBlocks.remove(processControlBlock);
    }

    static void dumpM(int inicio, int fim) {
        sistema.vm.mem.dump(inicio, fim);
    }

    static void executa(int processId) {
    ProcessControlBlock processControlBlock = sistema.allLoadedProcessControlBlocks.stream()
            .filter(p -> p.getId() == processId).findFirst().orElse(null);
    if (processControlBlock == null) {
        throw new RuntimeException("Processo com id " + processId + " não encontrado");
    }

    if (processControlBlock.getProcessStatus() != ProcessStatus.READY) {
        throw new RuntimeException(
                "Não é possível executar um processo com status " + processControlBlock.getProcessStatus());
    }

    if (runningProcess != null) {
        System.out.println("Processo " + runningProcess.getId() + " interrompido");
        sistema.updatePCB(runningProcess);
        runningProcess.setProcessStatus(ProcessStatus.READY);
        scheduler.addProcess(runningProcess);
    }

    scheduler.addProcess(processControlBlock);

    if (runningProcess == null && scheduler.hasProcesses()) {
        runningProcess = scheduler.getNextProcess();
        System.out.println("Processo " + runningProcess.getId() + " iniciado");
        sistema.exec(runningProcess);
        runningProcess.setProcessStatus(ProcessStatus.FINISHED);
        runningProcess = null;
    }
}


    static void traceOn() {
        sistema.vm.cpu.setDebug(true);
    }

    static void traceOff() {
        sistema.vm.cpu.setDebug(false);
    }
}