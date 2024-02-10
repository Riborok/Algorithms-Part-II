package wordnet;

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    private final Digraph G;

    public SAP(Digraph G) {
        if (G == null)
            throw new IllegalArgumentException("Digraph cannot be null");
        this.G = new Digraph(G);
    }

    public int length(int v, int w) {
        checkValidity(v, w);
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);

        int shortestAncestor = findShortestAncestor(bfsV, bfsW);
        return shortestAncestor == -1 ? -1 : bfsV.distTo(shortestAncestor) + bfsW.distTo(shortestAncestor);
    }

    public int ancestor(int v, int w) {
        checkValidity(v, w);
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);

        return findShortestAncestor(bfsV, bfsW);
    }

    private void checkValidity(int v, int w) {
        if (isOutside(v) || isOutside(w))
            throw new IllegalArgumentException("Vertex is outside the range");
    }

    private boolean isOutside(int v) {
        return v < 0 || v >= G.V();
    }

    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        checkValidity(v, w);
        if (!areIterablesNotEmpty(v, w))
            return -1;

        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);

        int shortestAncestor = findShortestAncestor(bfsV, bfsW);
        return shortestAncestor == -1 ? -1 : bfsV.distTo(shortestAncestor) + bfsW.distTo(shortestAncestor);
    }

    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        checkValidity(v, w);
        if (!areIterablesNotEmpty(v, w))
            return -1;

        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);

        return findShortestAncestor(bfsV, bfsW);
    }

    private void checkValidity(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException("Vertices cannot be null");

        for (Integer vertex : v)
            checkValidity(vertex);

        for (Integer vertex : w)
            checkValidity(vertex);
    }

    private void checkValidity(Integer v) {
        if (v == null)
            throw new IllegalArgumentException("Vertex cannot be null");
    }

    private boolean areIterablesNotEmpty(Iterable<Integer> v, Iterable<Integer> w) {
        return v.iterator().hasNext() && w.iterator().hasNext();
    }

    private int findShortestAncestor(BreadthFirstDirectedPaths bfsV, BreadthFirstDirectedPaths bfsW) {
        int shortestAncestor = -1;
        int shortestLength = Integer.MAX_VALUE;

        for (int ancestor = 0; ancestor < G.V(); ancestor++) {
            if (isCommonAncestor(bfsV, bfsW, ancestor)) {
                int length = calculateLength(bfsV, bfsW, ancestor);
                if (length < shortestLength) {
                    shortestLength = length;
                    shortestAncestor = ancestor;
                }
            }
        }

        return shortestAncestor;
    }

    private boolean isCommonAncestor(BreadthFirstDirectedPaths bfsV, BreadthFirstDirectedPaths bfsW, int ancestor) {
        return bfsV.hasPathTo(ancestor) && bfsW.hasPathTo(ancestor);
    }

    private int calculateLength(BreadthFirstDirectedPaths bfsV, BreadthFirstDirectedPaths bfsW, int ancestor) {
        return bfsV.distTo(ancestor) + bfsW.distTo(ancestor);
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}