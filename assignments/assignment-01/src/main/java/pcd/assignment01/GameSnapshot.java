package pcd.assignment01;

import java.util.List;

public record GameSnapshot(List<BallView> balls, int humanScore, int botScore, boolean finished, String winner) {
    public record BallView(double x, double y, double radius, Player player) { }
}
