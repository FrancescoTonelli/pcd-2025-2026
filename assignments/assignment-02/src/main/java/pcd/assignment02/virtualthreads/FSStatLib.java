package pcd.assignment02.virtualthreads;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** Virtual-thread version: straightforward blocking code, one cheap virtual thread per file. */
public final class FSStatLib {
    public CompletableFuture<FSReport> getFSReport(Path directory, long maxFileSize, int numberOfBands) {
        if (maxFileSize <= 0 || numberOfBands <= 0) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Parameters must be positive"));
        }
        CompletableFuture<FSReport> result = new CompletableFuture<>();
        Thread.startVirtualThread(() -> {
            try { result.complete(scan(directory, maxFileSize, numberOfBands)); }
            catch (Throwable error) { result.completeExceptionally(error); }
        });
        return result;
    }

    private FSReport scan(Path directory, long max, int bands) throws Exception {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor(); var paths = Files.walk(directory)) {
            List<Future<Long>> sizes = paths.filter(Files::isRegularFile)
                    .map(path -> executor.submit(() -> Files.size(path))).toList();
            long[] counts = new long[bands + 1];
            for (Future<Long> size : sizes) {
                long value;
                try { value = size.get(); }
                catch (ExecutionException error) { throw new Exception(error.getCause()); }
                int index = value > max ? bands : value == max ? bands - 1 : (int) (((double) value / max) * bands);
                counts[index]++;
            }
            return new FSReport(sizes.size(), java.util.Arrays.stream(counts).boxed().toList());
        }
    }
}
