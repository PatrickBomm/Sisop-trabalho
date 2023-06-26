package components;

import domain.Opcode;

import static utils.MemoryTranslator.getEndFrame;
import static utils.MemoryTranslator.getStartFrame;

public class MemoryManager {

    private Memory memory;
    private boolean[] framesAvailable;

    MemoryManager(Memory memory) {
        this.memory = memory;
        this.framesAvailable = new boolean[memory.numberOfFrames];

        for (int i = 0; i < memory.numberOfFrames; i++) {
            framesAvailable[i] = true;
        }
    }

    public boolean allocate(int wordNumber, int[] pagesTable) {
        int framesNeeded = Math.ceilDiv(wordNumber, this.memory.pageSize);
        if (framesNeeded > countAvailableFrames()) {
            return false;
        }

        int[] availableFrames = getAvailableFramesIndexes();
        for (int i = 0; i < framesNeeded; i++) {
            int frameIndex = availableFrames[i];
            pagesTable[i] = frameIndex;
            framesAvailable[frameIndex] = false;
        }
        return true;
    }

    // cada posição i do vetor de saída “pagesTable” informa em que frame a página i deve ser hospedada
    public void deallocate(int[] pagesTable) {
        for (int frame : pagesTable) {
            for (int j = getStartFrame(frame, memory.pageSize); j <= getEndFrame(frame, memory.pageSize); j++) {
                desallocatePosition(j);
            }
            framesAvailable[frame] = true;
        }
    }
    // simplesmente libera os frames alocados

    private int countAvailableFrames() {
        int count = 0;
        for (boolean frame : framesAvailable) {
            if (frame) count++;
        }
        return count;
    }

    private int[] getAvailableFramesIndexes() {
        int[] available = new int[countAvailableFrames()];
        int j = 0;
        for (int i = 0; i < framesAvailable.length; i++) {
            if (framesAvailable[i]) {
                available[j] = i;
                j++;
            }
            ;
        }
        return available;
    }

    private void desallocatePosition(int position) {
        memory.memoryArray[position].opCode = Opcode.___;
        memory.memoryArray[position].r1 = -1;
        memory.memoryArray[position].r2 = -1;
        memory.memoryArray[position].p = -1;
    }

}