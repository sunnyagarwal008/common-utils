/*
 *  @version     1.0, Jul 11, 2012
 *  @author sunny
 */
package in.bucheeng.common.utils;

import java.util.List;

public abstract class BatchProcessor<T> {
    private final int batchSize;
    private int       batchIndex;
    private int       totalItems;

    public BatchProcessor(int batchSize) {
        this.batchSize = batchSize;
    }

    /**
     * @param start
     * @param batchSize
     * @return the results for next batch
     */
    protected abstract List<T> next(int start, int batchSize);

    /**
     * processes the batchItems for a particular batch
     * 
     * @param batchItems
     * @param batchIndex
     */
    protected abstract void process(List<T> batchItems, int batchIndex);

    /**
     * starts the process pagination
     */
    public void process() {
        while (true) {
            //fetch next results
            List<T> batchItems = next(batchIndex * batchSize, batchSize);
            process(batchItems, batchIndex);
            totalItems += batchItems.size();
            //exits if no more results
            if (batchItems.size() < batchSize) {
                break;
            }
            batchIndex++;
        }
    }

    public int getBatchSize() {
        return batchSize;
    }

    public int getTotalItems() {
        return totalItems;
    }
}