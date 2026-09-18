package pcd.assignment01;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameEngineTest {
    @Test void allImplementationsAdvanceWithoutLosingBalls() {
        GameConfig config = new GameConfig(500, 400, 20, 2);
        for (GameEngine engine : new GameEngine[]{GameEngine.sequential(config),
                GameEngine.platformThreads(config), GameEngine.executor(config)}) {
            try (engine) {
                engine.impulse(Player.HUMAN, new Vec2(10, 0));
                engine.step(0.016);
                assertEquals(22, engine.snapshot().balls().size());
                assertFalse(engine.snapshot().finished());
            }
        }
    }
}
