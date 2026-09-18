package pcd.assignment01;

public final class PerformanceCheck {
    private PerformanceCheck() { }
    public static void main(String[] args) {
        int balls = args.length > 0 ? Integer.parseInt(args[0]) : 2_000;
        int steps = args.length > 1 ? Integer.parseInt(args[1]) : 100;
        GameConfig config = new GameConfig(2_000, 1_200, balls,
                Math.max(1, Runtime.getRuntime().availableProcessors()));
        measure("sequential", GameEngine.sequential(config), steps);
        measure("platform-threads", GameEngine.platformThreads(config), steps);
        measure("executor", GameEngine.executor(config), steps);
    }

    private static void measure(String name, GameEngine engine, int steps) {
        try (engine) {
            long start = System.nanoTime();
            for (int i = 0; i < steps; i++) engine.step(0.016);
            double millis = (System.nanoTime() - start) / 1_000_000.0;
            System.out.printf("%s: %.2f ms%n", name, millis);
        }
    }
}
