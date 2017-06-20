package graph;

/* See restrictions in Graph.java. */


/** Represents an undirected graph.  Out edges and in edges are not
 *  distinguished.  Likewise for successors and predecessors.
 *
 *  @author YunjieZhang
 */
public class UndirectedGraph extends GraphObj {

    /** Show if the graph is directed. */
    @Override
    public boolean isDirected() {
        return false;
    }

    /** Return the indegree of vertex v. */
    @Override
    public int inDegree(int v) {
        return outDegree(v);

    }

    /** Return the predecessor of vertex v. */
    @Override
    public int predecessor(int v, int k) {
        return successor(v, k);
    }

    /** Return an iteration of predecessors. */
    @Override
    public Iteration<Integer> predecessors(int v) {
        return successors(v);
    }
}
