package pcd.assignment02.virtualthreads;

import java.nio.file.Path;

public final class Example {
    public static void main(String[] args) throws Exception {
        Path directory = Path.of(args.length == 0 ? "." : args[0]);
        System.out.println(new FSStatLib().getFSReport(directory, 1_000_000, 10).get());
    }
}
