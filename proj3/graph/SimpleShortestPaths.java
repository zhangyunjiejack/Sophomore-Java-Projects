package graph;

/* See restrictions in Graph.java. */

import java.util.ArrayList;

/** A partial implementation of ShortestPaths that contains the weights of
 *  the vertices and the predecessor edges.   The client needs to
 *  supply only the two-argument getWeight method.
 *  @author YunjieZhang
 */
public abstract class SimpleShortestPaths extends ShortestPaths {

    /** The shortest paths in G from SOURCE. */
    public SimpleShortestPaths(Graph G, int source) {
        this(G, source, 0);
    }

    /** A shortest path in G from SOURCE to DEST. */
    public SimpleShortestPaths(Graph G, int source, int dest) {
        super(G, source, dest);
        _weights = new ArrayList<>();
        _vertices = new ArrayList<>();
        _predecessors = new ArrayList<>();
        Iteration<Integer> vertices = G.vertices();
        while (vertices.hasNext()) {
            _vertices.add(vertices.next());
            _weights.add(Double.POSITIVE_INFINITY);
            _predecessors.add(0);
        }
    }

    /** Returns the current weight of edge (U, V) in the graph.  If (U, V) is
     *  not in the graph, returns positive infinity. */
    @Override
    protected abstract double getWeight(int u, int v);

    /** Get the weight of a vertex. */
    @Override
    public double getWeight(int v) {
        int index = _vertices.indexOf(v);
        return _weights.get(index);
    }

    /** Set the weight of vertex. */
    @Override
    protected void setWeight(int v, double w) {
        int index = _vertices.indexOf(v);
        _weights.set(index, w);
    }

    /** Get the predecessor of vertex. */
    @Override
    public int getPredecessor(int v) {
        int index = _vertices.indexOf(v);
        return _predecessors.get(index);
    }

    @Override
    protected void setPredecessor(int v, int u) {
        int index = _vertices.indexOf(v);
        _predecessors.set(index, u);
    }

    /** The arraylist of vertices. */
    private ArrayList<Integer> _vertices;
    /** Arraylist of weights of vertices. */
    private ArrayList<Double> _weights;
    /** ArrayList of predecessors of vertices. */
    private ArrayList<Integer> _predecessors;

}
