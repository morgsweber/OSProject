package software;

import java.util.ArrayList;

public class MemoryManager {
    public int memorySize;
    public static int pageSize;
    public int memoryFrames;
    public static boolean[] frames;

    public MemoryManager(int memorySize, int pageSize) {
        this.memorySize = memorySize;
        this.pageSize = pageSize;
        this.memoryFrames = memorySize/pageSize;
        this.frames = new boolean[memoryFrames];
        frames[2] = true;
        frames[3] = true;
        frames[4] = true;
        frames[10] = true; 
        frames[11] = true;
    }

    public int[] allocate(int wordNum){
        int pageNum = 0;
        int count = 0;

        if(wordNum < pageSize){ pageNum = 1; }
        else if(wordNum%pageSize == 0){ pageNum = wordNum/pageSize; }
        else { pageNum = wordNum/pageSize + 1; }

        int[] pagesTable = new int [pageNum];
        ArrayList<Integer> aux = new ArrayList<>();
        for (int i = 0; i < frames.length; i++) {
            if(count == pageNum){ break; }
            else if(!frames[i]){
                aux.add(i);
                count++;
            }
        }
        if(count == pageNum){
            for(int i=0; i < aux.size(); i++){
                pagesTable[i] = aux.get(i);
                frames[aux.get(i)] = true;
            }
        }

        if(count < pageNum){
            return null;
        }
        return pagesTable;
    }

    public static int translate(int logicAddress, int[] pageTable) {
        int pageIndex = logicAddress/pageSize;
        int offset = logicAddress % pageSize;
        int physicalAddress = (pageTable[pageIndex] * pageSize) + offset;

        return physicalAddress;
    }

    public void deallocates(int[] tablePages){
        for (int i = 0; i < tablePages.length; i++) {
            frames[tablePages[i]] = false;
        }
    }
}
