package components;

public class VM {
    public int memorySize;
    public static Memory mem;
    public CPU cpu;

    public VM() {
        // vm deve ser configurada com endereço de tratamento de interrupcoes e de chamadas de sistema
        // cria memória
        memorySize = 1024;
        int pageSize = 8;
        System.out.println("Criando memória com " + memorySize / pageSize + " frames");
        mem = new Memory(memorySize, pageSize);
        // cria cpu
        cpu = new CPU(mem, true);                   // true liga debug
    }

    public void run() {
        cpu.start();
    }
}
