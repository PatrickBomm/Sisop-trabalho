package components;

import domain.Interrupts;
import domain.Opcode;
import domain.ProcessStatus;
import domain.Word;
import handlers.InterruptHandling;
import handlers.SysCallHandling;

import java.util.concurrent.Semaphore;

import static utils.MemoryTranslator.translate;

public class CPU extends Thread {
    private int maxInt; // valores maximo e minimo para inteiros nesta cpu
    private int minInt;
    // característica do processador: contexto da components.CPU ...
    private int pc;            // ... composto de program counter,
    private Word ir;            // instruction register,
    public int[] registers;        // registradores da components.CPU
    public Interrupts interrupt;    // durante instrucao, interrupcao pode ser sinalizada

    private Memory mem;               // mem tem funcoes de dump e o array m de memória 'fisica'
    private Word[] memoryArray;                 // components.CPU acessa MEMORIA, guarda referencia a 'm'. m nao muda. semre será um array de palavras

    private InterruptHandling interruptHandling;     // significa desvio para rotinas de tratamento de  Int - se int ligada, desvia
    private SysCallHandling sysCall;  // significa desvio para tratamento de chamadas de sistema - trap
    private static boolean debug;            // se true entao mostra cada instrucao em execucao
    private final int CLOCK;

    private int clockCycles;

    public static final Semaphore execSemaphore = new Semaphore(0);

    private static ProcessControlBlock currentProcess;

    public CPU(Memory _mem, boolean _debug) {     // ref a MEMORIA e interrupt handler passada na criacao da components.CPU
        maxInt = 32767;        // capacidade de representacao modelada
        minInt = -32767;        // se exceder deve gerar interrupcao de overflow
        mem = _mem;                // usa mem para acessar funcoes auxiliares (dump)
        memoryArray = mem.memoryArray;                // usa o atributo 'm' para acessar a memoria.
        registers = new int[10];        // aloca o espaço dos registradores - regs 8 e 9 usados somente para IO
        interruptHandling = new InterruptHandling(this);
        sysCall = new SysCallHandling(this);
        debug = _debug;        // se true, print da instrucao em execucao
        CLOCK = 5;
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            try {
                execSemaphore.acquire();
                assert currentProcess != null;
                this.setContext(currentProcess.getPc(), currentProcess.registers.clone());
                currentProcess.setProcessStatus(ProcessStatus.RUNNING);
                this.run(currentProcess);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void setCurrentProcess(ProcessControlBlock pcb) {
        currentProcess = pcb;
    }

    public boolean canAccessMemory(int e) {                             // todo acesso a memoria tem que ser verificado
        boolean enderecoDeMemoriaCorreto = e < mem.memorySize && e >= 0;
        interrupt = enderecoDeMemoriaCorreto ? interrupt : Interrupts.intEnderecoInvalido;
        return enderecoDeMemoriaCorreto;
    }

    private boolean testOverflow(int v) {                       // toda operacao matematica deve avaliar se ocorre overflow
        if ((v < minInt) || (v > maxInt)) {
            interrupt = Interrupts.intOverflow;
            return false;
        }
        return true;
    }

    public void setContext(int _pc, int[] registers) {  // no futuro esta funcao vai ter que ser
        pc = _pc;                                              // limite e pc (deve ser zero nesta versao)
        interrupt = Interrupts.noInterrupt;                         // reset da interrupcao registrada
        this.registers = registers;
    }

    public void run(ProcessControlBlock pcb) throws InterruptedException {
        clockCycles = pcb.getClockCount();
        int physicalAddress;
        if (debug) {
            System.out.println("        executando programa " + pcb.getId());
        }
        // execucao da components.CPU supoe que o contexto da components.CPU, vide acima, esta devidamente setado
        while (true) {            // ciclo de instrucoes. acaba cfe instrucao, veja cada caso.
//            sleep para criar tempo para inputs
            Thread.sleep(2500);
            // --------------------------------------------------------------------------------------------------
            // FETCH
            physicalAddress = translate(pc, pcb.getPageTable(), mem.pageSize);
            clockCycles++;
            if (canAccessMemory(physicalAddress)) {    // pc valido
                ir = memoryArray[physicalAddress];    // <<<<<<<<<<<<           busca posicao da memoria apontada por pc, guarda em ir
                if (debug) {
                    System.out.print("                               pc: " + pc + "       exec: ");
                    mem.dump(ir);
                }
                // --------------------------------------------------------------------------------------------------
                // EXECUTA INSTRUCAO NO ir
                switch (ir.opCode) {   // conforme o opcode (código de operação) executa

                    // Instrucoes de Busca e Armazenamento em Memoria
                    case LDI: // Rd ← k
                        registers[ir.r1] = ir.p;
                        pc++;
                        break;

                    case LDD: // Rd <- [A]
                        physicalAddress = translate(ir.p, pcb.getPageTable(), mem.pageSize);
                        if (canAccessMemory(physicalAddress)) {
                            registers[ir.r1] = memoryArray[physicalAddress].p;
                            pc++;
                        }
                        break;

                    case LDX: // RD <- [RS] // NOVA
                        int physicalR2 = translate(registers[ir.r2], pcb.getPageTable(), mem.pageSize);
                        if (canAccessMemory(physicalR2)) {
                            registers[ir.r1] = memoryArray[physicalR2].p;
                            pc++;
                        }
                        break;

                    case STD: // [A] ← Rs
                        physicalAddress = translate(ir.p, pcb.getPageTable(), mem.pageSize);
                        if (canAccessMemory(physicalAddress)) {
                            memoryArray[physicalAddress].opCode = Opcode.DATA;
                            memoryArray[physicalAddress].p = registers[ir.r1];
                            pc++;
                        }
                        ;
                        break;

                    case STX: // [Rd] ←Rs
                        int physicalR1 = translate(registers[ir.r1], pcb.getPageTable(), mem.pageSize);
                        if (canAccessMemory(physicalR1)) {
                            memoryArray[physicalR1].opCode = Opcode.DATA;
                            memoryArray[physicalR1].p = registers[ir.r2];
                            pc++;
                        }
                        ;
                        break;

                    case MOVE: // RD <- RS
                        registers[ir.r1] = registers[ir.r2];
                        pc++;
                        break;

                    // Instrucoes Aritmeticas
                    case ADD: // Rd ← Rd + Rs
                        registers[ir.r1] = registers[ir.r1] + registers[ir.r2];
                        testOverflow(registers[ir.r1]);
                        pc++;
                        break;

                    case ADDI: // Rd ← Rd + k
                        registers[ir.r1] = registers[ir.r1] + ir.p;
                        testOverflow(registers[ir.r1]);
                        pc++;
                        break;

                    case SUB: // Rd ← Rd - Rs
                        registers[ir.r1] = registers[ir.r1] - registers[ir.r2];
                        testOverflow(registers[ir.r1]);
                        pc++;
                        break;

                    case SUBI: // RD <- RD - k // NOVA
                        registers[ir.r1] = registers[ir.r1] - ir.p;
                        testOverflow(registers[ir.r1]);
                        pc++;
                        break;

                    case MULT: // Rd <- Rd * Rs
                        registers[ir.r1] = registers[ir.r1] * registers[ir.r2];
                        testOverflow(registers[ir.r1]);
                        pc++;
                        break;

                    // Instrucoes JUMP
                    case JMP: // PC <- k
                        pc = ir.p;
                        break;

                    case JMPIG: // If Rc > 0 Then PC ← Rs Else PC ← PC +1
                        if (registers[ir.r2] > 0) {
                            pc = registers[ir.r1];
                        } else {
                            pc++;
                        }
                        break;

                    case JMPIGK: // If RC > 0 then PC <- k else PC++
                        if (registers[ir.r2] > 0) {
                            pc = ir.p;
                        } else {
                            pc++;
                        }
                        break;

                    case JMPILK: // If RC < 0 then PC <- k else PC++
                        if (registers[ir.r2] < 0) {
                            pc = ir.p;
                        } else {
                            pc++;
                        }
                        break;

                    case JMPIEK: // If RC = 0 then PC <- k else PC++
                        if (registers[ir.r2] == 0) {
                            pc = ir.p;
                        } else {
                            pc++;
                        }
                        break;


                    case JMPIL: // if Rc < 0 then PC <- Rs Else PC <- PC +1
                        if (registers[ir.r2] < 0) {
                            pc = registers[ir.r1];
                        } else {
                            pc++;
                        }
                        break;

                    case JMPIE: // If Rc = 0 Then PC <- Rs Else PC <- PC +1
                        if (registers[ir.r2] == 0) {
                            pc = registers[ir.r1];
                        } else {
                            pc++;
                        }
                        break;

                    case JMPIM: // PC <- [A]
                        physicalAddress = translate(ir.p, pcb.getPageTable(), mem.pageSize);
                        pc = memoryArray[physicalAddress].p;
                        break;

                    case JMPIGM: // If RC > 0 then PC <- [A] else PC++
                        if (registers[ir.r2] > 0) {
                            physicalAddress = translate(ir.p, pcb.getPageTable(), mem.pageSize);
                            pc = memoryArray[physicalAddress].p;
                        } else {
                            pc++;
                        }
                        break;

                    case JMPILM: // If RC < 0 then PC <- k else PC++
                        if (registers[ir.r2] < 0) {
                            physicalAddress = translate(ir.p, pcb.getPageTable(), mem.pageSize);
                            pc = memoryArray[physicalAddress].p;
                        } else {
                            pc++;
                        }
                        break;

                    case JMPIEM: // If RC = 0 then PC <- k else PC++
                        if (registers[ir.r2] == 0) {
                            physicalAddress = translate(ir.p, pcb.getPageTable(), mem.pageSize);
                            pc = memoryArray[physicalAddress].p;
                        } else {
                            pc++;
                        }
                        break;

                    case JMPIGT: // If RS>RC then PC <- k else PC++
                        if (registers[ir.r1] > registers[ir.r2]) {
                            pc = ir.p;
                        } else {
                            pc++;
                        }
                        break;

                    // outras
                    case STOP: // por enquanto, para execucao
                        interrupt = Interrupts.intSTOP;
                        break;

                    case DATA:
                        interrupt = Interrupts.intInstrucaoInvalida;
                        break;

                    // Chamada de sistema
                    case TRAP:
                        sysCall.handle(pcb);            // <<<<< aqui desvia para rotina de chamada de sistema, no momento so temos IO
                        pc++;
                        interrupt = Interrupts.ioRequest;
                        this.updatePCB(pcb, clockCycles);
                        break;

                    // Inexistente
                    default:
                        interrupt = Interrupts.intInstrucaoInvalida;
                        break;
                }
                if (clockCycles == CLOCK) {
                    interrupt = Interrupts.clockInterrupt;
                }
                if (checkInterruption(pcb)) break;// break sai do loop da cpu
            }

        }  // FIM DO CICLO DE UMA INSTRUÇÃO
    }

    public boolean checkInterruption(ProcessControlBlock pcb) {
        if (interrupt != Interrupts.noInterrupt) {   // existe interrupção
            interruptHandling.handle(interrupt, pc, pcb); // desvia para rotina de tratamento
            if (interrupt == Interrupts.ioPronto) {
                interrupt = Interrupts.noInterrupt;
                return false;
            }
            interrupt = Interrupts.noInterrupt;
            return true;
        }
        return false;
    }

    public void updatePCB(ProcessControlBlock pcb, int clockCycles) {
        pcb.setRegisters(registers.clone());
        pcb.setPc(pc);
        pcb.setClockCount(clockCycles);
//        TODO: logs
//        System.out.println("PC: " + pc);
//        for (int i = 0; i < registers.length; i++) {
//            System.out.println("Registrador " + i + ": " + registers[i]);
//        }
    }

    public void resetClockCycles() {
        clockCycles = 0;
    }

    public int getPc() {
        return pc;
    }

    public static void setDebug(boolean value) {
        debug = value;
    }
}
// ------------------ C P U - fim ------------------------------------------------------------------------
  