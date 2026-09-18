package pcd.assignment02.reactive;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

/** Rx version: the directory tree is a cold stream and aggregation is an operator pipeline. */
public final class FSStatLib {
    public Single<FSReport> getFSReport(Path directory, long maxFileSize, int numberOfBands) {
        if (maxFileSize <= 0 || numberOfBands <= 0) return Single.error(new IllegalArgumentException("Parameters must be positive"));
        return files(directory)
                .map(path -> Files.size(path))
                .subscribeOn(Schedulers.io())
                .reduce(empty(numberOfBands), (report, size) -> add(report, size, maxFileSize, numberOfBands));
    }

    private Flowable<Path> files(Path directory) {
        return Flowable.using(() -> Files.list(directory),
                stream -> Flowable.fromStream(stream)
                        .concatMap(path -> Files.isDirectory(path) ? files(path) : Flowable.just(path)),
                stream -> stream.close());
    }

    private static FSReport empty(int bands) {
        return new FSReport(0, Collections.nCopies(bands + 1, 0L));
    }

    private static FSReport add(FSReport report, long size, long max, int bands) {
        var counts = new java.util.ArrayList<>(report.bands());
        int index = size > max ? bands : size == max ? bands - 1 : (int) (((double) size / max) * bands);
        counts.set(index, counts.get(index) + 1);
        return new FSReport(report.totalFiles() + 1, counts);
    }
}
