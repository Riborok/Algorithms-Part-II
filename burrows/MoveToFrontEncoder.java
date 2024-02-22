package burrows;

public class MoveToFrontEncoder {
    private final char[] charOrder;

    public MoveToFrontEncoder() {
        charOrder = new char[Alphabet.R];
        for (char i = 0; i < Alphabet.R; i++)
            charOrder[i] = i;
    }

    public char getChar(int index) {
        return charOrder[index];
    }

    public void moveChar(char c) {
        int index = findIndex(c);
        moveToFront(c, index);
    }

    public int findIndex(char c) {
        int index = 0;
        while (charOrder[index] != c)
            index++;
        return index;
    }

    private void moveToFront(char c, int index) {
        shiftElementsRight(index);
        charOrder[0] = c;
    }

    private void shiftElementsRight(int index) {
        for (int i = index; i > 0; i--)
            charOrder[i] = charOrder[i - 1];
    }
}