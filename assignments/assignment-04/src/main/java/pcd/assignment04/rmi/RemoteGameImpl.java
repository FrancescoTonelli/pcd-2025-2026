package pcd.assignment04.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

final class RemoteGameImpl extends UnicastRemoteObject implements RemoteGame {
    private final String name;
    private final Mark[] board = new Mark[9];
    private final String xPlayer;
    private String oPlayer;
    private Mark turn = Mark.X;
    private String status = "WAITING";

    RemoteGameImpl(String name, String creator) throws RemoteException {
        this.name = name; this.xPlayer = creator;
        Arrays.fill(board, Mark.EMPTY);
    }

    public synchronized Mark join(String playerName) throws RemoteException {
        if (oPlayer != null) throw new RemoteException("Game is full");
        if (xPlayer.equals(playerName)) throw new RemoteException("Players must have different names");
        oPlayer = playerName; status = "ACTIVE";
        return Mark.O;
    }

    public synchronized GameSnapshot snapshot() { return snapshotValue(); }

    public synchronized GameSnapshot makeMove(String playerName, int row, int column) throws RemoteException {
        if (!status.equals("ACTIVE")) throw new RemoteException("Game is not active");
        Mark player = xPlayer.equals(playerName) ? Mark.X : oPlayer != null && oPlayer.equals(playerName) ? Mark.O : Mark.EMPTY;
        if (player == Mark.EMPTY) throw new RemoteException("Unknown player");
        if (player != turn) throw new RemoteException("Not your turn");
        if (row < 0 || row > 2 || column < 0 || column > 2) throw new RemoteException("Move outside the board");
        int cell = row * 3 + column;
        if (board[cell] != Mark.EMPTY) throw new RemoteException("Cell is occupied");
        board[cell] = player;
        if (wins(player)) status = player + "_WON";
        else if (Arrays.stream(board).noneMatch(mark -> mark == Mark.EMPTY)) status = "DRAW";
        else turn = player == Mark.X ? Mark.O : Mark.X;
        return snapshotValue();
    }

    private boolean wins(Mark mark) {
        int[][] lines = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
        return Arrays.stream(lines).anyMatch(line -> board[line[0]] == mark && board[line[1]] == mark && board[line[2]] == mark);
    }

    private GameSnapshot snapshotValue() {
        return new GameSnapshot(name, Arrays.asList(board.clone()), xPlayer, oPlayer, turn, status);
    }
}
