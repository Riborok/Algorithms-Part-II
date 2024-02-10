package wordnet;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordnet;

    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    public String outcast(String[] nouns) {
        int maxDistance = -1;
        String outcastNoun = null;

        for (String noun : nouns) {
            int distance = calcDistance(noun, nouns);

            if (distance > maxDistance) {
                maxDistance = distance;
                outcastNoun = noun;
            }
        }

        return outcastNoun;
    }

    private int calcDistance(String noun, String[] nouns) {
        int distance = 0;
        for (String other : nouns)
            if (!noun.equals(other))
                distance += wordnet.distance(noun, other);

        return distance;
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}