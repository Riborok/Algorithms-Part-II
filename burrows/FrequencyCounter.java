package burrows;

public class FrequencyCounter {
    private final int[] count = new int[Alphabet.R + 1];

    public FrequencyCounter(String lastColumn) {
        computeFrequencyCounts(lastColumn);
        computeCumulates();
    }

    private void computeFrequencyCounts(String lastColumn) {
        for (int i = 0; i < lastColumn.length(); i++)
            count[lastColumn.charAt(i) + 1]++;
    }

    private void computeCumulates() {
        for (int i = 0; i < count.length - 1; i++)
            count[i + 1] += count[i];
    }

    public int get(int index) {
        return count[index]++;
    }
}