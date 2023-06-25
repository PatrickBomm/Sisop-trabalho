package components;

import domain.Interrupts;
import domain.Opcode;
import domain.Word;
import handlers.InterruptHandling;
import handlers.SysCallHandling;

import static utils.MemoryTranslator.translate;

public class CPU {

    private int maxInt;
    private int minInt;
    // Características do processador: contexto da CPU composto por:
    private int pc; // Program Counter
    private Word ir; // Instruction Register
    public int[] registers; // Registradores da CPU
    public Interrupts interrupt; // Interrupção pode ser sinalizada durante a execução de uma instrução
    private int base; // Base e limite de acesso na memória
    private int limite;

    // Até aqui: contexto da CPU - tudo o que é necessário para executar um processo
    // Nas próximas versões, isso pode ser modificado

    // Acessa a memória e guarda referência ao array de palavras
    private Memory mem;
    private Word[] memoryArray;

    // Desvia para rotinas de tratamento de interrupção, se a interrupção estiver
    // ligada
    private InterruptHandling interruptHandling;

    // Desvia para tratamento de chamadas de sistema
    private SysCallHandling sysCall;

    // Se true, mostra cada instrução em execução
    private boolean debug;

    // Cria uma nova CPU com referência à memória e ao interrupt handler
    public CPU(Memory _mem, InterruptHandling _ih, SysCallHandling _sysCall, boolean _debug) {
        maxInt = 32767;
        minInt = -32767;
        mem = _mem;
        memoryArray = mem.memoryArray;
        registers = new int[10]; // Registradores 8 e 9 são usados somente para IO
        interruptHandling = _ih;
        sysCall = _sysCall;
        debug = _debug;
    }

    // Verifica se é possível acessar a memória no endereço especificado
    public boolean canAccessMemory(int e) {
        boolean enderecoDeMemoriaCorreto = e < mem.memorySize && e >= 0;
        interrupt = enderecoDeMemoriaCorreto ? interrupt : Interrupts.intEnderecoInvalido;
        return enderecoDeMemoriaCorreto;
    }

    // Verifica se ocorre overflow em operações matemáticas
    private boolean testOverflow(int v) {
        if ((v < minInt) || (v > maxInt)) {
            interrupt = Interrupts.intOverflow;
            return false;
        }
        return true;
    }

    // Define o contexto de execução da CPU, incluindo base, limite e PC
    public void setContext(int _base, int _limite, int _pc) {
        base = _base;
        limite = _limite;
        pc = _pc;
        interrupt = Interrupts.noInterrupt; // Reseta a interrupção registrada
    }

    public void run(int[] pageTable) {
        int physicalAddress;
        // execucao da components.CPU supoe que o contexto da components.CPU, vide
        // acima, esta devidamente setado
        while (true) { // ciclo de instrucoes. acaba cfe instrucao, veja cada caso.
            // --------------------------------------------------------------------------------------------------
            // FETCH
            physicalAddress = translate(pc, pageTable, mem.pageSize);
            if (canAccessMemory(physicalAddress)) { // pc valido
                ir = memoryArray[physicalAddress]; // <<<<<<<<<<<< busca posicao da memoria apontada por pc, guarda em
                                                   // ir
                if (debug) {
                    System.out.print("          pc: " + pc + "       exec: ");
                    mem.dump(ir);
                }
                // --------------------------------------------------------------------------------------------------
                // EXECUTA INSTRUCAO NO ir
                switch (ir.opCode) { // conforme o opcode (código de operação) executa

                    // Instrucoes de Busca e Armazenamento em Memoria
                    case LDI: // Rd ← k
                        registers[ir.r1] = ir.p;
                        pc++;
                        break;

                    case LDD: // Rd <- [A]
                        physicalAddress = translate(ir.p, pageTable, mem.pageSize);
                        if (canAccessMemory(physicalAddress)) {
                            registers[ir.r1] = memoryArray[physicalAddress].p;
                            pc++;
                        }
                        break;

                    case LDX: // RD <- [RS] // NOVA
                        int physicalR2 = translate(registers[ir.r2], pageTable, mem.pageSize);
                        if (canAccessMemory(physicalR2)) {
                            registers[ir.r1] = memoryArray[physicalR2].p;
                            pc++;
                        }
                        break;

                    case STD: // [A] ← Rs
                        physicalAddress = translate(ir.p, pageTable, mem.pageSize);
                        if (canAccessMemory(physicalAddress)) {
                            memoryArray[physicalAddress].opCode = Opcode.DATA;
                            memoryArray[physicalAddress].p = registers[ir.r1];
                            pc++;
                        }
                        ;
                        break;

                    case STX: // [Rd] ←Rs
                        int physicalR1 = translate(registers[ir.r1], pageTable, mem.pageSize);
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
                        physicalAddress = translate(ir.p, pageTable, mem.pageSize);
                        pc = memoryArray[physicalAddress].p;
                        break;

                    case JMPIGM: // If RC > 0 then PC <- [A] else PC++
                        if (registers[ir.r2] > 0) {
                            physicalAddress = translate(ir.p, pageTable, mem.pageSize);
                            pc = memoryArray[physicalAddress].p;
                        } else {
                            pc++;
                        }
                        break;

                    case JMPILM: // If RC < 0 then PC <- k else PC++
                        if (registers[ir.r2] < 0) {
                            physicalAddress = translate(ir.p, pageTable, mem.pageSize);
                            pc = memoryArray[physicalAddress].p;
                        } else {
                            pc++;
                        }
                        break;

                    case JMPIEM: // If RC = 0 then PC <- k else PC++
                        if (registers[ir.r2] == 0) {
                            physicalAddress = translate(ir.p, pageTable, mem.pageSize);
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
                        sysCall.handle(); // <<<<< aqui desvia para rotina de chamada de sistema, no momento so temos IO
                        pc++;
                        break;

                    // Inexistente
                    default:
                        interrupt = Interrupts.intInstrucaoInvalida;
                        break;
                }
            }
            // --------------------------------------------------------------------------------------------------
            // VERIFICA INTERRUPÇÃO !!! - TERCEIRA FASE DO CICLO DE INSTRUÇÕES
            if (!(interrupt == Interrupts.noInterrupt)) { // existe interrupção
                interruptHandling.handle(interrupt, pc); // desvia para rotina de tratamento
                break; // break sai do loop da cpu
            }
        } // FIM DO CICLO DE UMA INSTRUÇÃO
    }

    public int getPc() {
        return pc;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
// ------------------ C P U - fim
// ------------------------------------------------------------------------
