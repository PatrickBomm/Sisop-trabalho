package executable;

// -------------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------------
// --------------- P R O G R A M A S  - não fazem parte do sistema
// esta classe representa programas armazenados (como se estivessem em disco)
// que podem ser carregados para a memória (load faz isto)

import domain.Opcode;
import domain.Word;

public class Programas {
    public Word[] fatorial = new Word[]{ // resultado no registrador 1
            new Word(Opcode.LDI, 0, -1, 4),      // 0   	r0 é valor a calcular fatorial
            new Word(Opcode.LDI, 1, -1, 1),      // 1   	r1 é 1 para multiplicar (por r0)
            new Word(Opcode.LDI, 6, -1, 1),      // 2   	r6 é 1 para ser o decremento
            new Word(Opcode.LDI, 7, -1, 8),      // 3   	r7 tem posicao de stop do programa = 8
            new Word(Opcode.JMPIE, 7, 0, 0),     // 4   	se r0=0 pula para r7(=8)
            new Word(Opcode.MULT, 1, 0, -1),     // 5   	r1 = r1 * r0
            new Word(Opcode.SUB, 0, 6, -1),      // 6   	decrementa r0 1 
            new Word(Opcode.JMP, -1, -1, 4),     // 7   	vai p posicao 4
            new Word(Opcode.STD, 1, -1, 10),     // 8   	coloca valor de r1 na posição 10
            new Word(Opcode.STOP, -1, -1, -1),   // 9   	stop
            new Word(Opcode.DATA, -1, -1, -1)}; // 10   ao final o valor do fatorial estará na posição 10 da memória                                    

    public Word[] progMinimo = new Word[]{
            new Word(Opcode.LDI, 0, -1, 999),
            new Word(Opcode.STD, 0, -1, 10),
            new Word(Opcode.STD, 0, -1, 11),
            new Word(Opcode.STD, 0, -1, 12),
            new Word(Opcode.STD, 0, -1, 13),
            new Word(Opcode.STD, 0, -1, 14),
            new Word(Opcode.STOP, -1, -1, -1)};

    public Word[] fibonacci10 = new Word[]{ // resultado no registrador 2
            new Word(Opcode.LDI, 1, -1, 0),
            new Word(Opcode.STD, 1, -1, 20),
            new Word(Opcode.LDI, 2, -1, 1),
            new Word(Opcode.STD, 2, -1, 21),
            new Word(Opcode.LDI, 0, -1, 22),
            new Word(Opcode.LDI, 6, -1, 6),
            new Word(Opcode.LDI, 7, -1, 31),
            new Word(Opcode.LDI, 3, -1, 0),
            new Word(Opcode.ADD, 3, 1, -1),
            new Word(Opcode.LDI, 1, -1, 0),
            new Word(Opcode.ADD, 1, 2, -1),
            new Word(Opcode.ADD, 2, 3, -1),
            new Word(Opcode.STX, 0, 2, -1),
            new Word(Opcode.ADDI, 0, -1, 1),
            new Word(Opcode.SUB, 7, 0, -1),
            new Word(Opcode.JMPIG, 6, 7, -1),
            new Word(Opcode.STOP, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),   // POS 20
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1)
    }; // ate aqui - serie de fibonacci ficara armazenada

    public Word[] paInput = new Word[]{
            new Word(Opcode.LDI, 8, -1, 1), // 0
            new Word(Opcode.LDI, 9, -1, 21), // 1
            new Word(Opcode.TRAP, -1, -1, -1), // 2
            new Word(Opcode.LDD, 0, -1, 21), // 3 carrega o valor 5 no registrador 0
            new Word(Opcode.STD, 0, -1, 17), // 4 pega o valor do r0 e coloca na posição 37 da memória
            new Word(Opcode.LDD, 1, -1, 17), // 5 pega o valor da posição 37 da memória e coloca em r1
            new Word(Opcode.SUBI, 1, -1, 1), // 6 subtrai 1 do valor que entrou (no caso 5), por conta do
            // zero: 0-4
            // (5 valores)
            new Word(Opcode.LDI, 2, -1, 41), // 7 registrador que vai controlar o incremento de posição de
            // memória
            new Word(Opcode.LDI, 7, -1, 27), // 8 carrega o valor 28 no registrador 7
            new Word(Opcode.JMPIL, 7, 1, -1), // 9 verifica se o valor no r1 é menor que 0, se sim, pula p/
            // instrução 25, guardada no r7, se não, segue o fluxo
            new Word(Opcode.LDI, 5, -1, 1), // 10 carrega o primeiro valor de Fibonacci (1) no r5
            new Word(Opcode.STX, 2, 5, -1), // 11pega o valor do r5 e carrega na posição 900 (valor
            // armazenado no
            // r2)
            new Word(Opcode.SUBI, 1, -1, 1), // 12 subtrai 1 dos valores do r1
            new Word(Opcode.LDI, 7, -1, 29), // 13
            new Word(Opcode.JMPIL, 7, 1, -1), // 14 se o valor do r1 for menor que 0, pula para instrução 25
            new Word(Opcode.ADDI, 2, -1, 1), // 15 soma 1 ao valor que está no r2 (posição de memória)
            new Word(Opcode.LDI, 6, -1, 1), // 16 carrega o segundo valor de Fibonacci (1) no r6
            new Word(Opcode.STX, 2, 6, -1), // 17 carrega o valor de r6 na próxima posição da memória (r2)
            new Word(Opcode.SUBI, 1, -1, 1), // 18 subtrai 1 do r1
            new Word(Opcode.JMPIL, 7, 1, -1), // 19 se r1 menor do que 0, pula para instrução 25
            new Word(Opcode.ADDI, 2, -1, 1), // 20 soma 1 na posição de memória
            new Word(Opcode.ADD, 5, 6, -1), // 21 soma os valores de r5 e r6
            // correta
            // para somar
            new Word(Opcode.STX, 2, 6, -1), // 23 carrega o valor de r6 (soma) para memória
            new Word(Opcode.LDI, 4, -1, 18), // 24 carrega o valor 14 no registrador 4
            new Word(Opcode.JMPIG, 4, 1, -1), // 25 se o que tem no r1 é maior que 0, volta pra instrução
            // 14, se
            // não, segue
            new Word(Opcode.JMPIL, 7, 1, -1), // 26 se o que tem no r1 for menor que 0, pula pra instrução
            // 26
            new Word(Opcode.LDI, 3, -1, -1), // 27 carrega -1 no r3
            new Word(Opcode.STX, 2, 3, -1), // 28 pega o que tá no r3 (-1) e coloca na posição de memória do
            // r2
            // (900)
            new Word(Opcode.STOP, -1, -1, -1), // 29 para o programa
            new Word(Opcode.DATA, -1, -1, -1), // ...
            new Word(Opcode.DATA, -1, -1, -1), // ...
            new Word(Opcode.DATA, -1, -1, -1), // ...
            new Word(Opcode.DATA, -1, -1, -1), // ...
            new Word(Opcode.DATA, -1, -1, -1), // ...
            new Word(Opcode.DATA, -1, -1, -1), // ...
            new Word(Opcode.DATA, -1, -1, -1) // ...
    };

    public Word[] pbOutput = new Word[]{
            new Word(Opcode.LDI, 0, -1, 3), // 0 carrega o valor 3 no r0
            new Word(Opcode.STD, 0, -1, 26), // 1 carrega o valor do r0 na posição 36 da memória
            new Word(Opcode.LDD, 1, -1, 26), // 2 pega o valor da posição 36 da memória e coloca em r1 (3)
            new Word(Opcode.LDI, 5, -1, 13), // 3 carrega o valor 12 no r5
            new Word(Opcode.JMPIL, 5, 1, -1), // 4 se o que tem no registrador r1 for menor que 0, pula pra
            // instrução guardada no r5 (12), se não, segue
            new Word(Opcode.LDD, 2, -1, 26), // 5 carrega em r2 o mesmo valor de r1 (3)
            new Word(Opcode.SUBI, 1, -1, 1), // 6 subtrai 1 do valor que tinha em r1
            new Word(Opcode.MULT, 2, 1, -1), // 7 multiplica r1 e r2 e coloca em r2 o valor
            new Word(Opcode.SUBI, 1, -1, 1), // 8 subtrai 1 do r1
            new Word(Opcode.LDI, 6, -1, 7), // 9 carrega o valor 7 (próxima instrução) no r6
            new Word(Opcode.JMPIG, 6, 1, -1), // 10 se for maior que 0 o que tem no r1, volta pra instrução
            // 7 se
            // não, segue o fluxo
            new Word(Opcode.STD, 2, -1, 25), // 11 coloca na posição 35 da memória o resultado do fatorial,
            // que está
            // no r2
            new Word(Opcode.JMP, -1, -1, 15), // 12 pula pro stop
            new Word(Opcode.LDI, 1, -1, -1), // 13 carrega no r1 o valor -1
            new Word(Opcode.STD, 1, -1, 25), // 14 coloca na posição 35 da memória o valor -1
            new Word(Opcode.LDI, 8, -1, 2), // 15 coloca 2 em reg 8 para criar um trap de out
            new Word(Opcode.LDI, 9, -1, 25), // 16 coloca 6 no reg 9, ou seja a posição onde será feita a
            // leitura
            new Word(Opcode.TRAP, -1, -1, -1), // 17 faz o output da posição 10
            new Word(Opcode.STOP, -1, -1, -1), // 18 termina a execução
            new Word(Opcode.DATA, -1, -1, -1),
    };

    public Word[] fatorialTRAP = new Word[]{
            new Word(Opcode.LDI, 0, -1, 7),// numero para colocar na memoria
            new Word(Opcode.STD, 0, -1, 17),
            new Word(Opcode.LDD, 0, -1, 17),
            new Word(Opcode.LDI, 1, -1, -1),
            new Word(Opcode.LDI, 2, -1, 13),// SALVAR POS STOP
            new Word(Opcode.JMPIL, 2, 0, -1),// caso negativo pula pro STD
            new Word(Opcode.LDI, 1, -1, 1),
            new Word(Opcode.LDI, 6, -1, 1),
            new Word(Opcode.LDI, 7, -1, 13),
            new Word(Opcode.JMPIE, 7, 0, 0), //POS 9 pula pra STD (Stop-1)
            new Word(Opcode.MULT, 1, 0, -1),
            new Word(Opcode.SUB, 0, 6, -1),
            new Word(Opcode.JMP, -1, -1, 9),// pula para o JMPIE
            new Word(Opcode.STD, 1, -1, 18),
            new Word(Opcode.LDI, 8, -1, 2),// escrita
            new Word(Opcode.LDI, 9, -1, 18),//endereco com valor a escrever
            new Word(Opcode.TRAP, -1, -1, -1),
            new Word(Opcode.STOP, -1, -1, -1), // POS 17
            new Word(Opcode.DATA, -1, -1, -1)};//POS 18

    public Word[] fibonacciTRAP = new Word[]{ // mesmo que prog exemplo, so que usa r0 no lugar de r8
            new Word(Opcode.LDI, 8, -1, 1),// leitura
            new Word(Opcode.LDI, 9, -1, 45),//endereco a guardar
            new Word(Opcode.TRAP, -1, -1, -1),
            new Word(Opcode.LDD, 7, -1, 45),// numero do tamanho do fib
            new Word(Opcode.LDI, 3, -1, 0),
            new Word(Opcode.ADD, 3, 7, -1),
            new Word(Opcode.LDI, 4, -1, 36),//posicao para qual ira pular (stop) *
            new Word(Opcode.LDI, 1, -1, -1),// caso negativo
            new Word(Opcode.STD, 1, -1, 41),
            new Word(Opcode.JMPIL, 4, 7, -1),//pula pra stop caso negativo *
            new Word(Opcode.JMPIE, 4, 7, -1),//pula pra stop caso 0
            new Word(Opcode.ADDI, 7, -1, 41),// fibonacci + posição do stop
            new Word(Opcode.LDI, 1, -1, 0),
            new Word(Opcode.STD, 1, -1, 41),    // 25 posicao de memoria onde inicia a serie de fibonacci gerada
            new Word(Opcode.SUBI, 3, -1, 1),// se 1 pula pro stop
            new Word(Opcode.JMPIE, 4, 3, -1),
            new Word(Opcode.ADDI, 3, -1, 1),
            new Word(Opcode.LDI, 2, -1, 1),
            new Word(Opcode.STD, 2, -1, 42),
            new Word(Opcode.SUBI, 3, -1, 2),// se 2 pula pro stop
            new Word(Opcode.JMPIE, 4, 3, -1),
            new Word(Opcode.LDI, 0, -1, 43),
            new Word(Opcode.LDI, 6, -1, 25),// salva posição de retorno do loop
            new Word(Opcode.LDI, 5, -1, 0),//salva tamanho
            new Word(Opcode.ADD, 5, 7, -1),
            new Word(Opcode.LDI, 7, -1, 0),//zera (inicio do loop)
            new Word(Opcode.ADD, 7, 5, -1),//recarrega tamanho
            new Word(Opcode.LDI, 3, -1, 0),
            new Word(Opcode.ADD, 3, 1, -1),
            new Word(Opcode.LDI, 1, -1, 0),
            new Word(Opcode.ADD, 1, 2, -1),
            new Word(Opcode.ADD, 2, 3, -1),
            new Word(Opcode.STX, 0, 2, -1),
            new Word(Opcode.ADDI, 0, -1, 1),
            new Word(Opcode.SUB, 7, 0, -1),
            new Word(Opcode.JMPIG, 6, 7, -1),//volta para o inicio do loop
            new Word(Opcode.STOP, -1, -1, -1),   // POS 36
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),   // POS 41
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1),
            new Word(Opcode.DATA, -1, -1, -1)
    };

    public Word[] PB = new Word[]{
            //dado um inteiro em alguma posição de memória,
            // se for negativo armazena -1 na saída; se for positivo responde o fatorial do número na saída
            new Word(Opcode.LDI, 0, -1, -51),// numero para colocar na memoria
            new Word(Opcode.STD, 0, -1, 16),
            new Word(Opcode.LDD, 0, -1, 16),
            new Word(Opcode.LDI, 1, -1, -1),
            new Word(Opcode.LDI, 2, -1, 13),// SALVAR POS STOP
            new Word(Opcode.JMPIL, 2, 0, -1),// caso negativo pula pro STD
            new Word(Opcode.LDI, 1, -1, 1),
            new Word(Opcode.LDI, 6, -1, 1),
            new Word(Opcode.LDI, 7, -1, 13),
            new Word(Opcode.JMPIE, 7, 0, 0), //POS 9 pula pra STD (Stop-1)
            new Word(Opcode.MULT, 1, 0, -1),
            new Word(Opcode.SUB, 0, 6, -1),
            new Word(Opcode.JMP, -1, -1, 9),// pula para o JMPIE
            new Word(Opcode.STD, 1, -1, 15),
            new Word(Opcode.STOP, -1, -1, -1), // POS 14
            new Word(Opcode.DATA, -1, -1, -1), //POS 15
            new Word(Opcode.DATA, -1, -1, -1)}; //POS 16

    public Word[] PC = new Word[]{
            //Para um N definido (10 por exemplo)
            //o programa ordena um vetor de N números em alguma posição de memória;
            //ordena usando bubble sort
            //loop ate que não swap nada
            //passando pelos N valores
            //faz swap de vizinhos se da esquerda maior que da direita
            new Word(Opcode.LDI, 7, -1, 5),// TAMANHO DO BUBBLE SORT (N)
            new Word(Opcode.LDI, 6, -1, 5),//aux N
            new Word(Opcode.LDI, 5, -1, 46),//LOCAL DA MEMORIA
            new Word(Opcode.LDI, 4, -1, 47),//aux local memoria
            new Word(Opcode.LDI, 0, -1, 4),//colocando valores na memoria
            new Word(Opcode.STD, 0, -1, 46),
            new Word(Opcode.LDI, 0, -1, 3),
            new Word(Opcode.STD, 0, -1, 47),
            new Word(Opcode.LDI, 0, -1, 5),
            new Word(Opcode.STD, 0, -1, 48),
            new Word(Opcode.LDI, 0, -1, 1),
            new Word(Opcode.STD, 0, -1, 49),
            new Word(Opcode.LDI, 0, -1, 2),
            new Word(Opcode.STD, 0, -1, 50),//colocando valores na memoria até aqui - POS 13
            new Word(Opcode.LDI, 3, -1, 25),// Posicao para pulo CHAVE 1
            new Word(Opcode.STD, 3, -1, 57),
            new Word(Opcode.LDI, 3, -1, 22),// Posicao para pulo CHAVE 2
            new Word(Opcode.STD, 3, -1, 56),
            new Word(Opcode.LDI, 3, -1, 38),// Posicao para pulo CHAVE 3
            new Word(Opcode.STD, 3, -1, 55),
            new Word(Opcode.LDI, 3, -1, 25),// Posicao para pulo CHAVE 4 (não usada)
            new Word(Opcode.STD, 3, -1, 54),
            new Word(Opcode.LDI, 6, -1, 0),//r6 = r7 - 1 POS 22
            new Word(Opcode.ADD, 6, 7, -1),
            new Word(Opcode.SUBI, 6, -1, 1),//ate aqui
            new Word(Opcode.JMPIEM, -1, 6, 55),//CHAVE 3 para pular quando r7 for 1 e r6 0 para interomper o loop de vez do programa
            new Word(Opcode.LDX, 0, 5, -1),//r0 e r1 pegando valores das posições da memoria POS 26
            new Word(Opcode.LDX, 1, 4, -1),
            new Word(Opcode.LDI, 2, -1, 0),
            new Word(Opcode.ADD, 2, 0, -1),
            new Word(Opcode.SUB, 2, 1, -1),
            new Word(Opcode.ADDI, 4, -1, 1),
            new Word(Opcode.SUBI, 6, -1, 1),
            new Word(Opcode.JMPILM, -1, 2, 57),//LOOP chave 1 caso neg procura prox
            new Word(Opcode.STX, 5, 1, -1),
            new Word(Opcode.SUBI, 4, -1, 1),
            new Word(Opcode.STX, 4, 0, -1),
            new Word(Opcode.ADDI, 4, -1, 1),
            new Word(Opcode.JMPIGM, -1, 6, 57),//LOOP chave 1 POS 38
            new Word(Opcode.ADDI, 5, -1, 1),
            new Word(Opcode.SUBI, 7, -1, 1),
            new Word(Opcode.LDI, 4, -1, 0),//r4 = r5 + 1 POS 41
            new Word(Opcode.ADD, 4, 5, -1),
            new Word(Opcode.ADDI, 4, -1, 1),//ate aqui
            new Word(Opcode.JMPIGM, -1, 7, 56),//LOOP chave 2
            new Word(Opcode.STOP, -1, -1, -1), // POS 45
            new Word(Opcode.DATA, -1, -1, -1), // 46
            new Word(Opcode.DATA, -1, -1, -1), // 47
            new Word(Opcode.DATA, -1, -1, -1), //48
            new Word(Opcode.DATA, -1, -1, -1), // 49
            new Word(Opcode.DATA, -1, -1, -1), // 50
            new Word(Opcode.DATA, -1, -1, -1), // 51
            new Word(Opcode.DATA, -1, -1, -1), // 52
            new Word(Opcode.DATA, -1, -1, -1), // 53

            new Word(Opcode.DATA, -1, -1, -1), // 54
            new Word(Opcode.DATA, -1, -1, -1), // 55
            new Word(Opcode.DATA, -1, -1, -1), // 56
            new Word(Opcode.DATA, -1, -1, -1)}; // 57
}