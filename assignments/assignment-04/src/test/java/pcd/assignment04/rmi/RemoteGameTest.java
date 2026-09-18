package pcd.assignment04.rmi;

import org.junit.jupiter.api.Test;
import java.rmi.server.UnicastRemoteObject;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RemoteGameTest {
    @Test void serializesMovesAndDetectsWinner() throws Exception {
        RemoteGameImpl game = new RemoteGameImpl("test", "alice");
        try {
            assertEquals(Mark.O, game.join("bob"));
            game.makeMove("alice", 0, 0); game.makeMove("bob", 1, 0);
            game.makeMove("alice", 0, 1); game.makeMove("bob", 1, 1);
            assertEquals("X_WON", game.makeMove("alice", 0, 2).status());
        } finally { UnicastRemoteObject.unexportObject(game, true); }
    }
}
