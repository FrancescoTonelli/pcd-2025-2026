package pcd.assignment01;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

final class PooolView extends JPanel {
    private volatile GameSnapshot snapshot = new GameSnapshot(java.util.List.of(), 0, 0, false, "");

    PooolView(Consumer<Vec2> impulse) {
        setPreferredSize(new Dimension(900, 600));
        setBackground(new Color(35, 115, 60));
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent event) {
                Vec2 value = switch (event.getKeyCode()) {
                    case KeyEvent.VK_UP -> new Vec2(0, -80);
                    case KeyEvent.VK_DOWN -> new Vec2(0, 80);
                    case KeyEvent.VK_LEFT -> new Vec2(-80, 0);
                    case KeyEvent.VK_RIGHT -> new Vec2(80, 0);
                    default -> null;
                };
                if (value != null) impulse.accept(value);
            }
        });
    }

    void render(GameSnapshot value) { snapshot = value; repaint(); }

    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;
        g.setColor(Color.BLACK);
        g.fillOval(0, 0, 56, 56);
        g.fillOval(getWidth() - 56, 0, 56, 56);
        for (GameSnapshot.BallView ball : snapshot.balls()) {
            g.setColor(ball.player() == Player.HUMAN ? Color.BLUE : ball.player() == Player.BOT ? Color.RED : Color.WHITE);
            int r = (int) ball.radius();
            g.fillOval((int) ball.x() - r, (int) ball.y() - r, r * 2, r * 2);
        }
        g.setFont(g.getFont().deriveFont(Font.BOLD, 20));
        g.setColor(Color.BLUE); g.drawString("Human: " + snapshot.humanScore(), 70, 30);
        g.setColor(Color.RED); g.drawString("Bot: " + snapshot.botScore(), getWidth() - 150, 30);
        if (snapshot.finished()) {
            g.setColor(Color.YELLOW); g.drawString("Winner: " + snapshot.winner(), getWidth() / 2 - 70, 30);
        }
    }
}
