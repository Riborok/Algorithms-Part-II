package burrows;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    public static void transform() {
        String input = BinaryStdIn.readString();

        var bwt = new BurrowsWheelerTransformer(input);
        outputFirst(bwt);
        outputLastColumn(bwt);

        BinaryStdOut.close();
    }

    private static void outputFirst(BurrowsWheelerTransformer bwt) {
        int first = bwt.getFirst();
        BinaryStdOut.write(first);
    }

    private static void outputLastColumn(BurrowsWheelerTransformer bwt) {
        String lastColumn = bwt.getLastColumn();
        BinaryStdOut.write(lastColumn);
    }

    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String lastColumn = BinaryStdIn.readString();

        var bwit = new BurrowsWheelerInverseTransformer(lastColumn);
        outputOriginal(bwit, first);

        BinaryStdOut.close();
    }

    private static void outputOriginal(BurrowsWheelerInverseTransformer bwit, int first) {
        String original = bwit.inverseTransform(first);
        BinaryStdOut.write(original);
    }

    public static void main(String[] args) {
        if (args[0].equals("-"))
            BurrowsWheeler.transform();
        if (args[0].equals("+"))
            BurrowsWheeler.inverseTransform();
    }
}