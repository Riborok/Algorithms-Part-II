package boggle;

public class TrieNode {
    private static final char START_LETTER = 'A';
    private static final char END_LETTER = 'Z';
    private static final int NUM_LETTERS = END_LETTER - START_LETTER + 1;

    private final TrieNode[] children = new TrieNode[NUM_LETTERS];
    private boolean isEndOfWord = false;

    public TrieNode() {
    }

    public boolean isEndOfWord() {
        return isEndOfWord;
    }

    public void setEndOfWord(boolean isEndOfWord) {
        this.isEndOfWord = isEndOfWord;
    }

    public TrieNode getChildren(char c) {
        return children[getIndexForLetter(c)];
    }

    public void createChildren(char c) {
        children[getIndexForLetter(c)] = new TrieNode();
    }

    private static int getIndexForLetter(char c) {
        return c - START_LETTER;
    }
}