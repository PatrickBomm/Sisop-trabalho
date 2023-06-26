import components.*;
import executable.Programas;

import java.util.*;

import static utils.MemoryTranslator.getEndFrame;
import static utils.MemoryTranslator.getStartFrame;

public class View extends Thread {
	static ProcessManager processManager = ProcessManager.getInstance();
	private Scanner in;

	public View() {
		this.in = new Scanner(System.in);
	}

	@Override
	public void run() {
		super.run();
		System.out.println("----------------------------------------------------");
		System.out.println("|               Nome dos Programas                  |");
		System.out.println("|               - fatorial                          |");
		System.out.println("|               - progMinimo                        |");
		System.out.println("|               - fibonacci10                       |");
		System.out.println("|               - fatorialTRAP                      |");
		System.out.println("|               - fibonacciTRAP                     |");
		System.out.println("|               - PB                                |");
		System.out.println("|               - PC                                |");
		System.out.println("|               - paInput                           |");
		System.out.println("|               - pbOutput                          |");
		System.out.println("----------------------------------------------------");
		System.out.println("|                 INSTRUÇOES                        |");
		System.out.println("|               - cria <nomeDoPrograma>             |");
		System.out.println("|               - listaProcessos                    |");
		System.out.println("|               - dump <id>                         |");
		System.out.println("|               - desaloca <id>                     |");
		System.out.println("|               - dumpM <inicio, fim>               |");
		System.out.println("|               - traceOn                           |");
		System.out.println("|               - traceOff                          |");
		System.out.println("|               - execAll                           |");
		System.out.println("|               - exit                              |");
		System.out.println("----------------------------------------------------");

		while (true) {
			System.out.print("comando ~: ");
			String readLine = in.nextLine();
			String command = readLine.split(" ")[0];
			List<String> programNames = Arrays.asList("fatorial", "progMinimo", "fibonacci10", "fatorialTRAP",
					"fibonacciTRAP", "PB", "PC", "paInput", "pbOutput");
			switch (command) {
				case "cria":
					String nomePrograma = readLine.split(" ")[1];
					if (!programNames.contains(nomePrograma)) {
						System.out.println("Nome do programa inválido.");
					} else {
						int processId = cria(nomePrograma);
						System.out.println("Processo " + processId + " criado");
					}
					break;

				case "listaProcessos":
					listaProcessos();
					break;

				case "dump":
					int idInt = Integer.parseInt(readLine.split(" ")[1]);
					dump(idInt);
					break;

				case "desaloca":
					int idDesaloca = Integer.parseInt(readLine.split(" ")[1]);
					desaloca(idDesaloca);
					break;

				case "dumpM":
					int inicio = Integer.parseInt(readLine.split(" ")[1]);
					int fim = Integer.parseInt(readLine.split(" ")[2]);
					dumpM(inicio, fim);
					break;

				case "traceOn":
					traceOn();
					break;

				case "traceOff":
					traceOff();
					break;

				case "execAll":
					for (String s : programNames) {
						int processId = cria(s);
						System.out.println("Processo " + processId + " criado");
					}

				case "exit":
					System.out.println("Finalizando o sistema");
					System.exit(0);
					in.close();
					break;

				default:
					System.out.println("Comando inválido");
					break;
			}
		}
	}

	static int cria(String nomePrograma) {
		Programas programas = new Programas();
		ProcessControlBlock processControlBlock = switch (nomePrograma) {
			case "fatorial" -> processManager.loadProgram(programas.fatorial);
			case "progMinimo" -> processManager.loadProgram(programas.progMinimo);
			case "fibonacci10" -> processManager.loadProgram(programas.fibonacci10);
			case "fatorialTRAP" -> processManager.loadProgram(programas.fatorialTRAP);
			case "fibonacciTRAP" -> processManager.loadProgram(programas.fibonacciTRAP);
			case "PB" -> processManager.loadProgram(programas.PB);
			case "PC" -> processManager.loadProgram(programas.PC);
			case "paInput" -> processManager.loadProgram(programas.paInput);
			case "pbOutput" -> processManager.loadProgram(programas.pbOutput);
			default -> null;
		};

		if (processControlBlock == null) {
			System.out.println("Nome do programa inválido, tente novamente");
			return 0;
		}

		if (Scheduler.schedulerSemaphore.availablePermits() == 0 && !processManager.someProcessIsRunning()) {
			Scheduler.schedulerSemaphore.release();
		}

		return processControlBlock.getId();
	}

	static void listaProcessos() {
		System.out.println("\nID \t| Status dos processos");
		System.out.println(processManager.allLoadedProcessControlBlocks.size() + " processos carregados");
		processManager.allLoadedProcessControlBlocks
				.forEach(p -> System.out.println(p.getId() + "\t| " + p.getProcessStatus()));
	}

	static void dump(int processId) {
		ProcessControlBlock processControlBlock = processManager.allLoadedProcessControlBlocks.stream()
				.filter(p -> p.getId() == processId).findFirst().orElse(null);
		if (processControlBlock == null) {
			System.out.println("Processo com id " + processId + " não encontrado");
			return;
		}

		System.out.println("\nPCB para processo " + processId);
		System.out.println("pid \t | pc \t | status \t | page table \t | registers");
		System.out.println(processControlBlock);

		System.out.println("\nDump de memória para processo " + processId);

		int pageSize = VM.mem.pageSize;
		int[] pageTable = processControlBlock.getPageTable();

		for (int i = 0; i < pageTable.length; i++) {
			int frame = pageTable[i];
			System.out.println("\n página " + i + " frame " + frame);
			VM.mem.dump(getStartFrame(frame, pageSize), getEndFrame(frame, pageSize) + 1);
		}
	}

	static void desaloca(int processId) {
		ProcessControlBlock processControlBlock = processManager.allLoadedProcessControlBlocks.stream()
				.filter(p -> p.getId() == processId).findFirst().orElse(null);
		if (processControlBlock == null) {
			System.out.println("Processo com id " + processId + " não encontrado");
			return;
		}

		processManager.deallocate(processControlBlock);
	}

	static void dumpM(int inicio, int fim) {
		VM.mem.dump(inicio, fim);
	}

	static void traceOn() {
		CPU.setDebug(true);
	}

	static void traceOff() {
		CPU.setDebug(false);
	}
}
