package utils;

public class MemoryTranslator {

    public static int getStartFrame(int frameIndex, int pageSize) {
        return frameIndex * pageSize;
    }

    public static int getEndFrame(int frameIndex, int pageSize) {
        return (frameIndex + 1) * pageSize - 1;
    }

    public static int translate(int logicalAddress, int[] pageTable, int pageSize) {
        int pageIndex = logicalAddress / pageSize;
        int offset = logicalAddress % pageSize;
        if (pageIndex < 0 || pageIndex >= pageTable.length) 
            return -1; // ou outro valor de retorno adequado
        return getStartFrame(pageTable[pageIndex], pageSize) + offset;
    }
}
