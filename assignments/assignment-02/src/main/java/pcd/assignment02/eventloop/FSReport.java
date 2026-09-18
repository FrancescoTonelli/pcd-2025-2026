package pcd.assignment02.eventloop;

import java.util.List;

public record FSReport(long totalFiles, List<Long> bands) {
    public FSReport {
        bands = List.copyOf(bands);
    }
}
