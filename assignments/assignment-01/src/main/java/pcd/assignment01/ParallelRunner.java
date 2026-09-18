package pcd.assignment01;

interface ParallelRunner extends AutoCloseable {
    void run(int size, int workers, RangeTask task) throws InterruptedException;
    @Override void close();

    @FunctionalInterface
    interface RangeTask { void run(int fromInclusive, int toExclusive); }
}
