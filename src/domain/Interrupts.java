package domain;

public enum Interrupts { // possiveis interrupcoes que esta components.CPU gera
    noInterrupt, intEnderecoInvalido, intInstrucaoInvalida, intOverflow, intSTOP;
}