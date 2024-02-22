package burrows;

public class BurrowsWheelerInverseTransformer {
    private final int[] next;
    private final char[] firstColumn;

    public BurrowsWheelerInverseTransformer(String lastColumn){
        next = new int[lastColumn.length()];
        firstColumn = new char[lastColumn.length()];

        FrequencyCounter frequencyCounter = new FrequencyCounter(lastColumn);
        moveData(lastColumn, frequencyCounter);
    }

    private void moveData(String lastColumn, FrequencyCounter frequencyCounter) {
        for (int i = 0; i < firstColumn.length; i++) {
            int index = frequencyCounter.get(lastColumn.charAt(i));
            firstColumn[index] = lastColumn.charAt(i);
            next[index] = i;
        }
    }

    public String inverseTransform(int first) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < next.length; i++) {
            result.append(firstColumn[first]);
            first = next[first];
        }
        return result.toString();
    }
}