package software;

public class MemoryManager {
    public int memorySize;
    public int pageSize;
    public int memoryFrames;
    public boolean[] frames;

    public MemoryManager(int memorySize, int pageSize) {
        this.memorySize = memorySize;
        this.pageSize = pageSize;
        this.memoryFrames = memorySize/pageSize;
        this.frames = new boolean[memoryFrames];
    }

    public int[] allocate(int wordNum){
        int pageNum = 0;
        int count = 0;

        if(wordNum < pageSize){ pageNum = 1; }
        else if(wordNum%pageSize == 0){ pageNum = wordNum/pageSize; }
        else { pageNum = wordNum/pageSize + 1; }

        int[] pagesTable = new int [pageNum];
        for (int i = 0; i < frames.length; i++) {
            if(count == pageNum){ break; }
            else if(!frames[i]){
                pagesTable[count] = i;
                count++;
                frames[i] = true;
            }
        }
        
        if(count < pageNum){
            return null;
        }
        return pagesTable;
    }

    public void deallocates(int[] tablePages){
        for (int i = 0; i < tablePages.length; i++) {
            frames[tablePages[i]] = false;
        }
    }
}
