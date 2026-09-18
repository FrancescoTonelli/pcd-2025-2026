package pcd.assignment01;

public final class Ball {
    private final int id;
    private final double radius;
    private final Player player;
    private Vec2 position;
    private Vec2 velocity = new Vec2(0, 0);
    private Player lastPlayerHit;
    private boolean pocketed;

    public Ball(int id, Vec2 position, double radius, Player player) {
        this.id = id;
        this.position = position;
        this.radius = radius;
        this.player = player;
    }

    public int id() { return id; }
    public double radius() { return radius; }
    public Player player() { return player; }
    public Vec2 position() { return position; }
    public Vec2 velocity() { return velocity; }
    public Player lastPlayerHit() { return lastPlayerHit; }
    public boolean isPlayerBall() { return player != null; }
    public boolean isPocketed() { return pocketed; }
    public void setPosition(Vec2 value) { position = value; }
    public void setVelocity(Vec2 value) { velocity = value; }
    public void setLastPlayerHit(Player value) { lastPlayerHit = value; }
    public void pocket() { pocketed = true; velocity = new Vec2(0, 0); }
}
