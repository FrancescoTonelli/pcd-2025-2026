package pcd.assignment01;

/** Small, finite model intended for JPF state-space exploration. */
public final class ScoreModel {
    private int humanScore;
    private int botScore;
    public synchronized void pocket(Player lastPlayerHit) {
        if (lastPlayerHit == Player.HUMAN) humanScore++;
        if (lastPlayerHit == Player.BOT) botScore++;
        assert humanScore >= 0 && botScore >= 0;
    }
}
