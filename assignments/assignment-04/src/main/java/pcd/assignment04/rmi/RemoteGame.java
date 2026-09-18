package pcd.assignment04.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteGame extends Remote {
    Mark join(String playerName) throws RemoteException;
    GameSnapshot snapshot() throws RemoteException;
    GameSnapshot makeMove(String playerName, int row, int column) throws RemoteException;
}
