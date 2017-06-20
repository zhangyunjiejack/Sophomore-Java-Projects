package graph;

/* See restrictions in Graph.java. */

import java.util.ArrayList;

/** Represents a general unlabeled directed graph whose vertices are denoted by
 *  positive integers. Graphs may have self edges.
 *
 *  @author YunjieZhang
 */
public class DirectedGraph extends GraphObj {

    /** Return if the graph is directed. */
    @Override
    public boolean isDirected() {
        return true;
    }

    /** Return the indegree of a vertex. */
    @Override
    public int inDegree(int v) {
        ArrayList<Integer> predecessors = getPredecessor(v);
        return predecessors.size();
    }

    /** Get the predecessor of a vertex. */
    @Override
    public int predecessor(int v, int k) {
        ArrayList<Integer> predecessors = getPredecessor(v);
        if (inDegree(v) == 0 || inDegree(v) < k) {
            return 0;
        }
        return predecessors.get(k - 1);
    }

    /** An iteration of predecessors. */
    @Override
    public Iteration<Integer> predecessors(int v) {
        return Iteration.iteration(getPredecessor(v));
    }

    /** Return the arraylist of predecessors of vertex V. */
    private ArrayList<Integer> getPredecessor(int v) {
        ArrayList<Integer> predecessors = new ArrayList<>();
        for (int[] edge:edges()) {
            if (edge[1] == v) {
                predecessors.add(edge[0]);
            }
        }
        return predecessors;
    }
}
