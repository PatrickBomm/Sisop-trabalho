package handlers;

import components.VM;
import domain.Interrupts;
import domain.Opcode;
import domain.Word;

import java.util.Scanner;

public class SysCallHandling {
    private VM vm;

    public void setVM(VM _vm) {
        vm = _vm;
    }

    public void handle() { // apenas avisa - todas interrupcoes neste momento finalizam o programa
        int operacao = vm.cpu.registers[8];
        int enderecoMemoria = vm.cpu.registers[9];

        if (vm.cpu.canAccessMemory(enderecoMemoria)) {
            if (operacao == 1) {
                // leitura
                Scanner teclado = new Scanner(System.in);
                System.out.println("(aguardando entrada do teclado)");
                int resultado = teclado.nextInt();
                vm.mem.memoryArray[enderecoMemoria] = new Word(Opcode.DATA, -1, -1, resultado);

            } else if (operacao == 2) {
                // escrita
                Word resultado = vm.mem.memoryArray[enderecoMemoria];
                System.out.println(resultado.p);
            } else {
                vm.cpu.interrupt = Interrupts.intInstrucaoInvalida;
            }
        }
        System.out.println(
                "           Chamada de components.Sistema com op  /  par:  "
                        + vm.cpu.registers[8] + " / " + vm.cpu.registers[9]);
    }
}
