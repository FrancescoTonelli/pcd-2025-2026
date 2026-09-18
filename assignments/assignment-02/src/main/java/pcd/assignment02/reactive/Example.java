package pcd.assignment02.reactive;

import java.nio.file.Path;

public final class Example {
    public static void main(String[] args) {
        Path directory = Path.of(args.length == 0 ? "." : args[0]);
        new FSStatLib().getFSReport(directory, 1_000_000, 10)
                .blockingSubscribe(System.out::println, Throwable::printStackTrace);
    }
}
