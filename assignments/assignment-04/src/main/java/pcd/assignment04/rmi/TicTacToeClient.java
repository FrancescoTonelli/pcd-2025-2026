package pcd.assignment04.rmi;

import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public final class TicTacToeClient {
    public static void main(String[] args) throws Exception {
        if (args.length < 5) throw new IllegalArgumentException("Usage: <host> <port> <create|join> <game> <player>");
        GameLobby lobby = (GameLobby) LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]))
                .lookup(TicTacToeServer.SERVICE_NAME);
        String action = args[2], gameName = args[3], playerName = args[4];
        RemoteGame game = action.equals("create") ? lobby.createGame(gameName, playerName) : lobby.joinGame(gameName, playerName);
        try (Scanner input = new Scanner(System.in)) {
            print(game.snapshot());
            while (true) {
                System.out.print("Command (show, move <row> <column>, quit): ");
                String command = input.nextLine().trim();
                if (command.equals("quit")) return;
                if (command.equals("show")) print(game.snapshot());
                else if (command.startsWith("move ")) {
                    String[] parts = command.split("\\s+");
                    if (parts.length != 3) System.out.println("Expected: move <row> <column>");
                    else print(game.makeMove(playerName, Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
                } else System.out.println("Unknown command");
            }
        }
    }

    private static void print(GameSnapshot snapshot) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                Mark mark = snapshot.board().get(row * 3 + column);
                System.out.print((mark == Mark.EMPTY ? "." : mark.name()) + (column == 2 ? "\n" : " "));
            }
        }
        System.out.println("Status: " + snapshot.status() + ", next: " + snapshot.nextTurn());
    }
}
