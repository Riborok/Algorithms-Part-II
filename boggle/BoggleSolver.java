package boggle;

import java.util.HashSet;
import java.util.Set;

public class BoggleSolver {
    private static final char Q_SPECIAL_CASE = 'Q';
    private static final char U_SPECIAL_CASE = 'U';
    private static final String QU_SPECIAL_STRING = String.valueOf(Q_SPECIAL_CASE) + U_SPECIAL_CASE;

    private final TrieSET trieSET = new TrieSET();

    public BoggleSolver(String[] dictionary) {
        for (var word : dictionary)
            trieSET.insertWord(word);
    }

    public Iterable<String> getAllValidWords(BoggleBoard board) {
        BoggleSolverState boggleSolverState = new BoggleSolverState(board);

        int rows = board.rows();
        int cols = board.cols();
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                findValidWordsDFS(i, j, trieSET.getRoot(), "", boggleSolverState);

        return boggleSolverState.validWords;
    }

    private void findValidWordsDFS(int row, int col, TrieNode node, String word, BoggleSolverState boggleSolverState) {
        if (isOutOfBounds(row, col, boggleSolverState.board) || boggleSolverState.visited[row][col])
            return;

        char letter = boggleSolverState.board.getLetter(row, col);

        node = getTrieNodeInBoggleFormat(node, letter);

        if (node == null)
            return;

        word += getCharInBoggleFormat(letter);

        if (isValidWord(node, word))
            boggleSolverState.validWords.add(word);

        boggleSolverState.visited[row][col] = true;
        exploreNeighbors(row, col, node, word, boggleSolverState);
        boggleSolverState.visited[row][col] = false;
    }

    private boolean isOutOfBounds(int row, int col, BoggleBoard board) {
        return row < 0 || row >= board.rows() || col < 0 || col >= board.cols();
    }

    private TrieNode getTrieNodeInBoggleFormat(TrieNode node, char letter) {
        node = node.getChildren(letter);
        if (node != null && isQuSpecialCase(letter))
            node = node.getChildren(U_SPECIAL_CASE);
        return node;
    }

    private String getCharInBoggleFormat(char letter) {
        return isQuSpecialCase(letter) ? QU_SPECIAL_STRING : String.valueOf(letter);
    }

    private boolean isQuSpecialCase(char letter) {
        return letter == Q_SPECIAL_CASE;
    }

    private void exploreNeighbors(int row, int col, TrieNode node, String word, BoggleSolverState boggleSolverState) {
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++)
                if (i != 0 || j != 0)
                    findValidWordsDFS(row + i, col + j, node, word, boggleSolverState);
    }

    private boolean isValidWord(TrieNode node, String word) {
        return node.isEndOfWord() && word.length() > 2;
    }

    public int scoreOf(String word) {
        return hasWord(word) ? ScoringSystem.getScore(word.length()) : 0;
    }
    
    private boolean hasWord(String word) {
        return trieSET.contains(word);
    }

    private static class BoggleSolverState {
        private final BoggleBoard board;
        private final boolean[][] visited;
        private final Set<String> validWords = new HashSet<>();
        private BoggleSolverState(BoggleBoard board) {
            this.board = board;

            int rows = board.rows();
            int cols = board.cols();
            this.visited = new boolean[rows][cols];
        }
    }
}