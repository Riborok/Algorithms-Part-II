package wordnet;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WordNet {
    private final Map<String, Set<Integer>> nounToSynsetIDs = new HashMap<>();
    private final Map<Integer, String> idToSynset = new HashMap<>();
    private final SAP sap;

    public WordNet(String synsets, String hypernyms) {
        In synsetsFile = new In(synsets);
        In hypernymsFile = new In(hypernyms);

        while (synsetsFile.hasNextLine()) {
            String line = synsetsFile.readLine();
            String[] pair = line.split(",");
            int id = Integer.parseInt(pair[0]);
            String synset = pair[1];

            String[] nouns = synset.split(" ");
            for (String noun : nouns) {
                if (!nounToSynsetIDs.containsKey(noun))
                    nounToSynsetIDs.put(noun, new HashSet<>());

                nounToSynsetIDs.get(noun).add(id);
            }
            idToSynset.put(id, synset);
        }

        Digraph G = new Digraph(idToSynset.size());
        while (hypernymsFile.hasNextLine()) {
            String line = hypernymsFile.readLine();
            String[] ids = line.split(",");

            int synsetID = Integer.parseInt(ids[0]);
            for (int i = 1; i < ids.length; i++)
                G.addEdge(synsetID, Integer.parseInt(ids[i]));
        }

        int rooted = 0;
        for (int i = 0; i < G.V(); i++)
            if (!G.adj(i).iterator().hasNext())
                rooted++;
        if (rooted != 1 || (new DirectedCycle(G)).hasCycle())
            throw new IllegalArgumentException("Input does not correspond to a rooted DAG");

        sap = new SAP(G);
    }

    public Iterable<String> nouns() {
        return nounToSynsetIDs.keySet();
    }

    public boolean isNoun(String word) {
        if (word == null)
            throw new IllegalArgumentException("Words cannot be null");
            
        return nounToSynsetIDs.containsKey(word);
    }

    public int distance(String nounA, String nounB) {
        checkValidity(nounA, nounB);

        Set<Integer> synsetIdA = nounToSynsetIDs.get(nounA);
        Set<Integer> synsetIdB = nounToSynsetIDs.get(nounB);
        return sap.length(synsetIdA, synsetIdB);
    }

    public String sap(String nounA, String nounB) {
        checkValidity(nounA, nounB);

        Set<Integer> synsetIdA = nounToSynsetIDs.get(nounA);
        Set<Integer> synsetIdB = nounToSynsetIDs.get(nounB);
        return idToSynset.get(sap.ancestor(synsetIdA, synsetIdB));
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet("synsets.txt", "hypernyms.txt");

        StdOut.println("WordNet nouns:");
        for (String noun : wordnet.nouns())
            StdOut.println(noun);

        String nounA = "computer";
        String nounB = "circuit";
        StdOut.println(nounA + " is a WordNet noun? - " + wordnet.isNoun(nounA));
        StdOut.println(nounB + " is a WordNet noun? - " + wordnet.isNoun(nounB));

        StdOut.println("Shortest distance between " + nounA + "' and '" + nounB + " = "
                               + wordnet.distance(nounA, nounB));
        StdOut.println("Common ancestor: " + wordnet.sap(nounA, nounB));
    }

    private void checkValidity(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("The noun is not in WordNet");
    }
}