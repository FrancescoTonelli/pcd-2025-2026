package pcd.assignment01;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

public final class PooolApplication {
    private PooolApplication() { }

    public static void main(String[] args) {
        GameConfig config = GameConfig.standard();
        String mode = args.length == 0 ? "threads" : args[0];
        GameEngine engine = mode.equalsIgnoreCase("executor")
                ? GameEngine.executor(config) : GameEngine.platformThreads(config);
        SwingUtilities.invokeLater(() -> show(engine, mode));
    }

    private static void show(GameEngine engine, String mode) {
        PooolView view = new PooolView(impulse -> engine.impulse(Player.HUMAN, impulse));
        JFrame frame = new JFrame("Poool - " + mode);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.add(view); frame.pack(); frame.setLocationRelativeTo(null); frame.setVisible(true);
        Timer timer = new Timer(16, event -> { engine.step(0.016); view.render(engine.snapshot()); });
        timer.start();
        Thread bot = new Thread(() -> {
            Random random = new Random();
            while (!engine.isFinished() && frame.isDisplayable()) {
                engine.impulse(Player.BOT, new Vec2(random.nextDouble(-70, 70), random.nextDouble(-70, 70)));
                try { Thread.sleep(700); } catch (InterruptedException e) { return; }
            }
        }, "poool-bot");
        bot.start();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent event) { timer.stop(); bot.interrupt(); engine.close(); }
        });
        view.requestFocusInWindow();
    }
}
