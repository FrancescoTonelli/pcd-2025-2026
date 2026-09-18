package pcd.assignment01;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

final class PlatformThreadRunner implements ParallelRunner {
    private final WorkMonitor monitor = new WorkMonitor();
    private final List<Thread> workers = new ArrayList<>();

    PlatformThreadRunner(int workerCount) {
        for (int i = 0; i < workerCount; i++) {
            Thread worker = new Thread(this::workLoop, "poool-worker-" + i);
            worker.start();
            workers.add(worker);
        }
    }

    private void workLoop() {
        try {
            while (true) {
                Runnable job = monitor.take();
                if (job == null) return;
                try { job.run(); } finally { monitor.completed(); }
            }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    public void run(int size, int workerCount, RangeTask task) throws InterruptedException {
        int chunk = Math.max(1, (size + workerCount - 1) / workerCount);
        for (int from = 0; from < size; from += chunk) {
            int start = from;
            int end = Math.min(size, from + chunk);
            monitor.submit(() -> task.run(start, end));
        }
        monitor.awaitAllSubmitted();
    }

    public void close() {
        monitor.stop(workers.size());
        for (Thread worker : workers) {
            try { worker.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    private static final class WorkMonitor {
        private final Queue<Runnable> jobs = new ArrayDeque<>();
        private int submitted;
        private int completed;
        private boolean stopping;

        synchronized void submit(Runnable job) {
            jobs.add(job);
            submitted++;
            notifyAll();
        }

        synchronized Runnable take() throws InterruptedException {
            while (jobs.isEmpty() && !stopping) wait();
            return jobs.poll();
        }

        synchronized void completed() {
            completed++;
            notifyAll();
        }

        synchronized void awaitAllSubmitted() throws InterruptedException {
            while (completed < submitted) wait();
        }

        synchronized void stop(int workerCount) {
            stopping = true;
            notifyAll();
        }
    }
}
