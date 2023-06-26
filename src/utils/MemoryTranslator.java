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

        if (pageIndex < 0 || pageIndex >= pageTable.length) {
            // Index da pagina invalido
            System.out.println("Invalid page index: " + pageIndex);
            return -1;
        }

        if (pageTable[pageIndex] < 0) {
            // Numero do frame fisico inválido
            System.out.println("Invalid physical frame number for page: " + pageIndex);
            return -1;
        }

        return getStartFrame(pageTable[pageIndex], pageSize) + offset;
    }
}
