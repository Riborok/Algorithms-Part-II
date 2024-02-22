package burrows;

import java.util.Arrays;

public class CircularSuffixArray {
    private final int length;
    private final Integer[] indexArr;

    public CircularSuffixArray(String s) {
        if (s == null)
            throw new IllegalArgumentException();

        length = s.length();
        indexArr = new Integer[length];

        initializeIndexArr();
        sortIndexArr(s);
    }

    private void initializeIndexArr() {
        for (int i = 0; i < length; i++)
            indexArr[i] = i;
    }

    private void sortIndexArr(String s) {
        Arrays.sort(indexArr, (x, y) -> {
            for (int i = 0; i < length; i++) {
                char cx = s.charAt((i + x) % length);
                char cy = s.charAt((i + y) % length);

                if (cx != cy)
                    return Character.compare(cx, cy);
            }
            return 0;
        });
    }

    public int length() {
        return length;
    }

    public int index(int i) {
        if (isOutOfBounds(i))
            throw new IllegalArgumentException();

        return indexArr[i];
    }

    private boolean isOutOfBounds(int i) {
        return i < 0 || i >= length;
    }

    public static void main(String[] args) {
    }
}