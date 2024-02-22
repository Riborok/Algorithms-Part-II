package burrows;

public class BurrowsWheelerTransformer {
    private final CircularSuffixArray csa;
    private final String original;

    public BurrowsWheelerTransformer(String original) {
        this.original = original;
        csa = new CircularSuffixArray(original);
    }

    public int getFirst() {
        return findFirst();
    }

    private int findFirst() {
        for (int i = 0; i < csa.length(); i++)
            if (csa.index(i) == 0)
                return i;

        return -1;
    }

    public String getLastColumn() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < original.length(); i++) {
            int circularPreviousIndex = getCircularPreviousIndex(csa.index(i), original.length());
            result.append(original.charAt(circularPreviousIndex));
        }
        return result.toString();
    }

    private static int getCircularPreviousIndex(int index, int length) {
        int circularPreviousIndex = index - 1;
        if (circularPreviousIndex < 0)
            circularPreviousIndex = length - 1;

        return circularPreviousIndex;
    }
}