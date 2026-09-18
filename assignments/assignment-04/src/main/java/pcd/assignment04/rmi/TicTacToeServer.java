package pcd.assignment04.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public final class TicTacToeServer {
    public static final String SERVICE_NAME = "TicTacToeLobby";
    public static void main(String[] args) throws Exception {
        int port = args.length == 0 ? 1099 : Integer.parseInt(args[0]);
        Registry registry = LocateRegistry.createRegistry(port);
        registry.rebind(SERVICE_NAME, new GameLobbyImpl());
        System.out.println("Tic-Tac-Toe RMI server listening on port " + port);
    }
}
