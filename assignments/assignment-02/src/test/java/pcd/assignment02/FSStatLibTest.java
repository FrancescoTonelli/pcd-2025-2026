package pcd.assignment02;

import io.vertx.core.Vertx;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FSStatLibTest {
    @TempDir Path root;

    @Test void allThreeVersionsProduceTheSameDistribution() throws Exception {
        Files.write(root.resolve("empty"), new byte[0]);
        Path nested = Files.createDirectory(root.resolve("nested"));
        Files.write(nested.resolve("ten"), new byte[10]);
        Files.write(nested.resolve("large"), new byte[21]);
        List<Long> expected = List.of(1L, 1L, 1L);

        Vertx vertx = Vertx.vertx();
        try {
            var eventLoop = new pcd.assignment02.eventloop.FSStatLib(vertx)
                    .getFSReport(root, 20, 2).toCompletionStage().toCompletableFuture().get(5, TimeUnit.SECONDS);
            assertEquals(3, eventLoop.totalFiles()); assertEquals(expected, eventLoop.bands());
        } finally { vertx.close().toCompletionStage().toCompletableFuture().get(5, TimeUnit.SECONDS); }

        var reactive = new pcd.assignment02.reactive.FSStatLib().getFSReport(root, 20, 2).blockingGet();
        assertEquals(3, reactive.totalFiles()); assertEquals(expected, reactive.bands());
        var virtual = new pcd.assignment02.virtualthreads.FSStatLib().getFSReport(root, 20, 2).get(5, TimeUnit.SECONDS);
        assertEquals(3, virtual.totalFiles()); assertEquals(expected, virtual.bands());
    }
}
