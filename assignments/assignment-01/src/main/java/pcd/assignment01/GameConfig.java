package pcd.assignment01;

public record GameConfig(double width, double height, int smallBalls, int workers) {
    public GameConfig {
        if (width < 200 || height < 200 || smallBalls < 1 || workers < 1) {
            throw new IllegalArgumentException("Invalid game configuration");
        }
    }

    public static GameConfig standard() {
        return new GameConfig(900, 600, 800, Math.max(1, Runtime.getRuntime().availableProcessors()));
    }
}
