package pcd.assignment04.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GameLobby extends Remote {
    RemoteGame createGame(String gameName, String playerName) throws RemoteException;
    RemoteGame joinGame(String gameName, String playerName) throws RemoteException;
    List<String> waitingGames() throws RemoteException;
}
