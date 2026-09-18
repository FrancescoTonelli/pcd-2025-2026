package pcd.assignment02.eventloop;

import io.vertx.core.Vertx;
import java.nio.file.Path;

public final class Example {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        Path directory = Path.of(args.length == 0 ? "." : args[0]);
        new FSStatLib(vertx).getFSReport(directory, 1_000_000, 10)
                .onSuccess(System.out::println)
                .onFailure(Throwable::printStackTrace)
                .eventually(() -> vertx.close());
    }
}
