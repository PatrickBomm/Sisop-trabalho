package components;

import java.util.concurrent.Semaphore;

public class Scheduler extends Thread {

    public static Semaphore schedulerSemaphore = new Semaphore(0);

    private final ProcessManager processManager;

    public Scheduler() {
        this.processManager = ProcessManager.getInstance();
    }

    @Override
    public void run() {
        while (true) {
            try {
                schedulerSemaphore.acquire();
                ProcessControlBlock next = processManager.readyProcessControlBlocks.poll();
                assert next != null;
                CPU.setCurrentProcess(next);
                CPU.execSemaphore.release();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
