package components;

import domain.Opcode;
import domain.Word;

public class Memory {
    public int memorySize;
    // frameSize = pageSize
    public int pageSize;

    public int numberOfFrames;
    public Word[] memoryArray;                  // m representa a memória fisica:   um array de posicoes de memoria (word)

    public Memory(int size, int pageSize) {
        memorySize = size;
        this.pageSize = pageSize;
        this.numberOfFrames = size / pageSize;
        memoryArray = new Word[memorySize];
        for (int i = 0; i < memorySize; i++) {
            memoryArray[i] = new Word(Opcode.___, -1, -1, -1);
        }
    }

    public void dump(Word w) {        // funcoes de DUMP nao existem em hardware - colocadas aqui para facilidade
        System.out.print("[ ");
        System.out.print(w.opCode);
        System.out.print(", ");
        System.out.print(w.r1);
        System.out.print(", ");
        System.out.print(w.r2);
        System.out.print(", ");
        System.out.print(w.p);
        System.out.println("  ] ");
    }

    public void dump(int ini, int fim) {
        for (int i = ini; i < fim; i++) {
            System.out.print(i);
            System.out.print(":  ");
            dump(memoryArray[i]);
        }
    }
}

 