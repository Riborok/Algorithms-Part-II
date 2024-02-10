package boggle;

public class ScoringSystem {
    private static final int[] points = {0, 0, 0, 1, 1, 2, 3, 5, 11};

    private ScoringSystem() {
    }

    public static int getScore(int wordLength) {
        return (wordLength >= points.length) ? points[points.length - 1] : points[wordLength];
    }
}