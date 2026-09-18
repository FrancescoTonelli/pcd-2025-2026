package pcd.assignment01;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class ExecutorRunner implements ParallelRunner {
    private final ExecutorService executor;

    ExecutorRunner(int workers) { executor = Executors.newFixedThreadPool(workers); }

    public void run(int size, int workers, RangeTask task) throws InterruptedException {
        int chunk = Math.max(1, (size + workers - 1) / workers);
        List<Runnable> jobs = new ArrayList<>();
        for (int from = 0; from < size; from += chunk) {
            int start = from;
            int end = Math.min(size, from + chunk);
            jobs.add(() -> task.run(start, end));
        }
        executor.invokeAll(jobs.stream().<java.util.concurrent.Callable<Void>>map(job -> () -> {
            job.run();
            return null;
        }).toList());
    }

    public void close() { executor.shutdownNow(); }
}
