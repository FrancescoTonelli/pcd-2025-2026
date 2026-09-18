package pcd.assignment04.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class GameLobbyImpl extends UnicastRemoteObject implements GameLobby {
    private final ConcurrentMap<String, RemoteGameImpl> games = new ConcurrentHashMap<>();
    public GameLobbyImpl() throws RemoteException { }

    public RemoteGame createGame(String gameName, String playerName) throws RemoteException {
        if (gameName.isBlank() || playerName.isBlank()) throw new RemoteException("Names cannot be blank");
        RemoteGameImpl game = new RemoteGameImpl(gameName, playerName);
        if (games.putIfAbsent(gameName, game) != null) {
            UnicastRemoteObject.unexportObject(game, true);
            throw new RemoteException("Game already exists");
        }
        return game;
    }

    public RemoteGame joinGame(String gameName, String playerName) throws RemoteException {
        RemoteGameImpl game = games.get(gameName);
        if (game == null) throw new RemoteException("Game does not exist");
        game.join(playerName);
        return game;
    }

    public List<String> waitingGames() {
        return games.entrySet().stream().filter(entry -> entry.getValue().snapshot().status().equals("WAITING"))
                .map(java.util.Map.Entry::getKey).sorted().toList();
    }
}
