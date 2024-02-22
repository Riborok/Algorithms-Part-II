package burrows;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    public static void encode() {
        var moveToFrontEncoder = new MoveToFrontEncoder();

        while (!BinaryStdIn.isEmpty())
            encodeCharacter(moveToFrontEncoder, BinaryStdIn.readChar());

        BinaryStdOut.close();
    }

    private static void encodeCharacter(MoveToFrontEncoder encoder, char c) {
        int index = encoder.findIndex(c);
        BinaryStdOut.write((char) index);
        encoder.moveChar(c);
    }

    public static void decode() {
        var moveToFrontEncoder = new MoveToFrontEncoder();

        while (!BinaryStdIn.isEmpty())
            decodeCharacter(moveToFrontEncoder, BinaryStdIn.readChar());

        BinaryStdOut.close();
    }

    private static void decodeCharacter(MoveToFrontEncoder encoder, int index) {
        char c = encoder.getChar(index);
        BinaryStdOut.write(c);
        encoder.moveChar(c);
    }

    public static void main(String[] args) {
        if (args[0].equals("-"))
            MoveToFront.encode();
        if (args[0].equals("+"))
            MoveToFront.decode();
    }
}