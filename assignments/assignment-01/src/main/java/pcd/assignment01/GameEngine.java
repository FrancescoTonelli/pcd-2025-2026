package pcd.assignment01;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class GameEngine implements AutoCloseable {
    private static final double FRICTION = 0.992;
    private static final double HOLE_RADIUS = 28;
    private final GameConfig config;
    private final ParallelRunner runner;
    private final List<Ball> balls = new ArrayList<>();
    private int humanScore;
    private int botScore;
    private Player forcedWinner;

    public static GameEngine sequential(GameConfig config) { return new GameEngine(config, new SequentialRunner()); }
    public static GameEngine platformThreads(GameConfig config) { return new GameEngine(config, new PlatformThreadRunner(config.workers())); }
    public static GameEngine executor(GameConfig config) { return new GameEngine(config, new ExecutorRunner(config.workers())); }

    private GameEngine(GameConfig config, ParallelRunner runner) {
        this.config = config;
        this.runner = runner;
        initialiseBalls();
    }

    private void initialiseBalls() {
        Random random = new Random(42);
        balls.add(new Ball(0, new Vec2(config.width() * 0.25, config.height() * 0.8), 15, Player.HUMAN));
        balls.add(new Ball(1, new Vec2(config.width() * 0.75, config.height() * 0.8), 15, Player.BOT));
        for (int id = 2; id < config.smallBalls() + 2; id++) {
            balls.add(new Ball(id, new Vec2(35 + random.nextDouble() * (config.width() - 70),
                    70 + random.nextDouble() * (config.height() - 105)), 5, null));
        }
    }

    public synchronized void impulse(Player player, Vec2 impulse) {
        balls.stream().filter(ball -> ball.player() == player && !ball.isPocketed()).findFirst()
                .ifPresent(ball -> ball.setVelocity(ball.velocity().add(impulse)));
    }

    public synchronized void step(double dt) {
        if (isFinished()) return;
        try {
            runner.run(balls.size(), config.workers(), (from, to) -> {
                for (int i = from; i < to; i++) integrate(balls.get(i), dt);
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        resolveCollisions();
        collectPocketedBalls();
    }

    private void integrate(Ball ball, double dt) {
        if (ball.isPocketed()) return;
        Vec2 next = ball.position().add(ball.velocity().scale(dt));
        double vx = ball.velocity().x();
        double vy = ball.velocity().y();
        if (next.x() < ball.radius() || next.x() > config.width() - ball.radius()) vx = -vx;
        if (next.y() < ball.radius() || next.y() > config.height() - ball.radius()) vy = -vy;
        next = new Vec2(Math.max(ball.radius(), Math.min(config.width() - ball.radius(), next.x())),
                Math.max(ball.radius(), Math.min(config.height() - ball.radius(), next.y())));
        ball.setPosition(next);
        Vec2 slowed = new Vec2(vx, vy).scale(FRICTION);
        ball.setVelocity(slowed.length() < 0.02 ? new Vec2(0, 0) : slowed);
    }

    private void resolveCollisions() {
        for (int i = 0; i < balls.size(); i++) {
            Ball first = balls.get(i);
            if (first.isPocketed()) continue;
            for (int j = i + 1; j < balls.size(); j++) {
                Ball second = balls.get(j);
                if (!second.isPocketed()) resolve(first, second);
            }
        }
    }

    private void resolve(Ball first, Ball second) {
        double dx = second.position().x() - first.position().x();
        double dy = second.position().y() - first.position().y();
        double distance = Math.hypot(dx, dy);
        double minimum = first.radius() + second.radius();
        if (distance <= 0 || distance >= minimum) return;
        double nx = dx / distance;
        double ny = dy / distance;
        double relative = (second.velocity().x() - first.velocity().x()) * nx
                + (second.velocity().y() - first.velocity().y()) * ny;
        if (relative < 0) {
            first.setVelocity(first.velocity().add(new Vec2(nx * relative, ny * relative)));
            second.setVelocity(second.velocity().add(new Vec2(-nx * relative, -ny * relative)));
        }
        double overlap = (minimum - distance) / 2;
        first.setPosition(first.position().add(new Vec2(-nx * overlap, -ny * overlap)));
        second.setPosition(second.position().add(new Vec2(nx * overlap, ny * overlap)));
        if (first.isPlayerBall() && !second.isPlayerBall()) second.setLastPlayerHit(first.player());
        else if (second.isPlayerBall() && !first.isPlayerBall()) first.setLastPlayerHit(second.player());
        else if (!first.isPlayerBall() && !second.isPlayerBall()) {
            first.setLastPlayerHit(null);
            second.setLastPlayerHit(null);
        }
    }

    private void collectPocketedBalls() {
        for (Ball ball : balls) {
            if (ball.isPocketed() || !inHole(ball.position())) continue;
            ball.pocket();
            if (ball.isPlayerBall()) forcedWinner = ball.player() == Player.HUMAN ? Player.BOT : Player.HUMAN;
            else if (ball.lastPlayerHit() == Player.HUMAN) humanScore++;
            else if (ball.lastPlayerHit() == Player.BOT) botScore++;
        }
    }

    private boolean inHole(Vec2 point) {
        return Math.hypot(point.x() - HOLE_RADIUS, point.y() - HOLE_RADIUS) <= HOLE_RADIUS
                || Math.hypot(point.x() - (config.width() - HOLE_RADIUS), point.y() - HOLE_RADIUS) <= HOLE_RADIUS;
    }

    public synchronized boolean isFinished() {
        return forcedWinner != null || balls.stream().filter(ball -> !ball.isPlayerBall()).allMatch(Ball::isPocketed);
    }

    public synchronized GameSnapshot snapshot() {
        List<GameSnapshot.BallView> views = balls.stream().filter(ball -> !ball.isPocketed())
                .map(ball -> new GameSnapshot.BallView(ball.position().x(), ball.position().y(), ball.radius(), ball.player()))
                .toList();
        String winner = "";
        if (forcedWinner != null) winner = forcedWinner.name();
        else if (isFinished()) winner = humanScore == botScore ? "DRAW" : humanScore > botScore ? "HUMAN" : "BOT";
        return new GameSnapshot(Collections.unmodifiableList(views), humanScore, botScore, isFinished(), winner);
    }

    public void close() { runner.close(); }
}
