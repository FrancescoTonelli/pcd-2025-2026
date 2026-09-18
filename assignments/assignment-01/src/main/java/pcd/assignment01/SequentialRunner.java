package pcd.assignment01;

final class SequentialRunner implements ParallelRunner {
    public void run(int size, int workers, RangeTask task) { task.run(0, size); }
    public void close() { }
}
