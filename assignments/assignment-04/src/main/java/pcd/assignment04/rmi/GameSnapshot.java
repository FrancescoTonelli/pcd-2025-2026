package pcd.assignment04.rmi;

import java.io.Serializable;
import java.util.List;

public record GameSnapshot(String name, List<Mark> board, String xPlayer, String oPlayer,
                           Mark nextTurn, String status) implements Serializable {
    public GameSnapshot { board = List.copyOf(board); }
}
