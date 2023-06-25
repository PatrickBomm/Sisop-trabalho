package components;

import domain.Word;
import handlers.InterruptHandling;
import handlers.SysCallHandling;

public class VM {
    public int memorySize;
    public Word[] memoryArray;
    public Memory mem;
    public CPU cpu;

    public VM(InterruptHandling ih, SysCallHandling sysCall) {
        // vm deve ser configurada com endereço de tratamento de interrupcoes e de
        // chamadas de sistema
        // cria memória
        memorySize = 1024;
        int pageSize = 8;
        System.out.println("Criando memória com " + memorySize / pageSize + " frames");
        mem = new Memory(memorySize, pageSize);
        memoryArray = mem.memoryArray;
        // cria cpu
        cpu = new CPU(mem, ih, sysCall, true); // true liga debug
    }
}
