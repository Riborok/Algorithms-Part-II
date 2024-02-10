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
    private SAP sap;

    public WordNet(String synsets, String hypernyms) {
        processSynsets(synsets);
        Digraph G = buildHypernymsGraph(hypernyms);
        validateGraph(G);
        initializeSAP(G);
    }

    private void processSynsets(String synsetsFile) {
        In synsetsInput = new In(synsetsFile);
        while (synsetsInput.hasNextLine()) {
            String line = synsetsInput.readLine();
            processSynsetLine(line);
        }
    }

    private void processSynsetLine(String line) {
        String[] pair = line.split(",");
        int id = Integer.parseInt(pair[0]);
        String synset = pair[1];

        String[] nouns = synset.split(" ");
        for (String noun : nouns)
            processNoun(id, noun);

        idToSynset.put(id, synset);
    }

    private void processNoun(int id, String noun) {
        if (!nounToSynsetIDs.containsKey(noun))
            nounToSynsetIDs.put(noun, new HashSet<>());
        nounToSynsetIDs.get(noun).add(id);
    }

    private Digraph buildHypernymsGraph(String hypernymsFile) {
        In hypernymsInput = new In(hypernymsFile);
        Digraph G = new Digraph(idToSynset.size());
        while (hypernymsInput.hasNextLine()) {
            String line = hypernymsInput.readLine();
            processHypernymsLine(line, G);
        }
        return G;
    }

    private void processHypernymsLine(String line, Digraph G) {
        String[] ids = line.split(",");
        int synsetID = Integer.parseInt(ids[0]);
        for (int i = 1; i < ids.length; i++) {
            G.addEdge(synsetID, Integer.parseInt(ids[i]));
        }
    }

    private void validateGraph(Digraph G) {
        if (!isRootedDAG(G))
            throw new IllegalArgumentException("Input does not correspond to a rooted DAG");
    }

    private boolean isRootedDAG(Digraph G) {
        int rooted = 0;
        for (int i = 0; i < G.V(); i++)
            if (!G.adj(i).iterator().hasNext())
                rooted++;
        return rooted == 1 && !new DirectedCycle(G).hasCycle();
    }

    private void initializeSAP(Digraph G) {
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

        Set<Integer> synsetIdA = getSynsetIDs(nounA);
        Set<Integer> synsetIdB = getSynsetIDs(nounB);

        return calcSapLength(synsetIdA, synsetIdB);
    }

    private int calcSapLength(Set<Integer> synsetIdA, Set<Integer> synsetIdB) {
        return sap.length(synsetIdA, synsetIdB);
    }

    public String sap(String nounA, String nounB) {
        checkValidity(nounA, nounB);

        Set<Integer> synsetIdA = getSynsetIDs(nounA);
        Set<Integer> synsetIdB = getSynsetIDs(nounB);

        int ancestorID = findAncestorID(synsetIdA, synsetIdB);
        return getSynsetByID(ancestorID);
    }

    private Set<Integer> getSynsetIDs(String noun) {
        return nounToSynsetIDs.get(noun);
    }

    private int findAncestorID(Set<Integer> synsetIdA, Set<Integer> synsetIdB) {
        return sap.ancestor(synsetIdA, synsetIdB);
    }

    private String getSynsetByID(int synsetID) {
        return idToSynset.get(synsetID);
    }

    private void checkValidity(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("The noun is not in WordNet");
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
}