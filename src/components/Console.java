package components;

import domain.IORequest;
import domain.Interrupts;
import domain.Opcode;
import domain.Word;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Console extends Thread {
    public static Semaphore consoleSemaphore = new Semaphore(0);
    public static Queue<IORequest> requests = new LinkedList<>();

    private CPU cpu;
    private ProcessManager processManager;

    Scanner scanner;

    public Console(CPU cpu) {
        this.cpu = cpu;
        this.processManager = ProcessManager.getInstance();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            try {
                consoleSemaphore.acquire();
                IORequest request = requests.poll();
                switch (Objects.requireNonNull(request).getType()) {
                    case READ -> this.read(request);
                    case WRITE -> this.write(request);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void read(IORequest request) {
        System.out.println("(aguardando entrada do teclado)");
        // bug with scanner
        int resultado = Integer.parseInt(scanner.next());
        VM.mem.memoryArray[request.getMemoryAddress()] = new Word(Opcode.DATA, -1, -1, resultado);
        signalIOFinished();
    }

    private void write(IORequest request) {
        Word resultado = VM.mem.memoryArray[request.getMemoryAddress()];
        System.out.println(resultado.p);
        signalIOFinished();
    }

    private void signalIOFinished() {
        // fim do IO, comunicando CPU que IO está pronto
        this.cpu.interrupt = Interrupts.ioPronto;
        if (!processManager.someProcessIsRunning()) {
            cpu.checkInterruption(null);
        }
    }
}
