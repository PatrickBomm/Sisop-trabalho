package handlers;

import domain.Interrupts;

public class InterruptHandling {
    public void handle(Interrupts interrupt, int pc) { // apenas avisa - todas interrupcoes neste momento finalizam o
                                                       // programa
        System.out.println("            Interrupcao " + interrupt + "   pc: " + pc);
    }
}