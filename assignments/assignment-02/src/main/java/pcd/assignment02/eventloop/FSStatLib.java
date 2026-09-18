package pcd.assignment02.eventloop;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Event-loop version: every filesystem operation is asynchronous and handlers never block. */
public final class FSStatLib {
    private final Vertx vertx;

    public FSStatLib(Vertx vertx) { this.vertx = vertx; }

    public Future<FSReport> getFSReport(Path directory, long maxFileSize, int numberOfBands) {
        validate(maxFileSize, numberOfBands);
        return scan(directory.toAbsolutePath().toString(), maxFileSize, numberOfBands);
    }

    private Future<FSReport> scan(String path, long max, int bands) {
        return vertx.fileSystem().readDir(path).compose(entries -> {
            List<Future<FSReport>> pending = entries.stream()
                    .map(entry -> inspect(entry, max, bands)).toList();
            if (pending.isEmpty()) return Future.succeededFuture(empty(bands));
            return Future.all(new ArrayList<>(pending)).map(results -> merge(results, bands));
        });
    }

    private Future<FSReport> inspect(String path, long max, int bands) {
        return vertx.fileSystem().props(path).compose(properties -> properties.isDirectory()
                ? scan(path, max, bands)
                : Future.succeededFuture(single(properties.size(), max, bands)));
    }

    private static FSReport merge(CompositeFuture results, int bands) {
        long[] totals = new long[bands + 1];
        long files = 0;
        for (int i = 0; i < results.size(); i++) {
            FSReport report = results.resultAt(i);
            files += report.totalFiles();
            for (int band = 0; band < totals.length; band++) totals[band] += report.bands().get(band);
        }
        return new FSReport(files, java.util.Arrays.stream(totals).boxed().toList());
    }

    private static FSReport single(long size, long max, int bands) {
        long[] counts = new long[bands + 1];
        counts[index(size, max, bands)] = 1;
        return new FSReport(1, java.util.Arrays.stream(counts).boxed().toList());
    }

    private static FSReport empty(int bands) {
        return new FSReport(0, java.util.Collections.nCopies(bands + 1, 0L));
    }

    static int index(long size, long max, int bands) {
        if (size > max) return bands;
        if (size == max) return bands - 1;
        return (int) (((double) size / max) * bands);
    }

    private static void validate(long max, int bands) {
        if (max <= 0 || bands <= 0) throw new IllegalArgumentException("maxFileSize and numberOfBands must be positive");
    }
}
