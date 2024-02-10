package boggle;

public class TrieSET {
    private final TrieNode root = new TrieNode();

    public TrieSET() {
    }

    public TrieNode getRoot() {
        return root;
    }

    public void insertWord(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray())
            node = moveToNextNode(node, c);
        markEndOfWord(node);
    }

    private TrieNode moveToNextNode(TrieNode node, char c) {
        if (node.getChildren(c) == null)
            node.createChildren(c);
        return node.getChildren(c);
    }

    private void markEndOfWord(TrieNode node) {
        node.setEndOfWord(true);
    }

    public boolean contains(String key) {
        TrieNode node = get(root, key, 0);
        return node != null && node.isEndOfWord();
    }

    private TrieNode get(TrieNode node, String key, int d) {
        if (node == null)
            return null;
        if (d == key.length())
            return node;
        return get(node.getChildren(key.charAt(d)), key, d+1);
    }
}