package domain;

// possiveis interrupcoes que esta components.CPU gera
public enum Interrupts {
    noInterrupt, intEnderecoInvalido, intInstrucaoInvalida, intOverflow, intSTOP, clockInterrupt, ioRequest, ioPronto;
}