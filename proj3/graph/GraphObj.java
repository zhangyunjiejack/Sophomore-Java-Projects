package graph;

/* See restrictions in Graph.java. */

import java.util.ArrayList;

/** A partial implementation of Graph containing elements common to
 *  directed and undirected graphs.
 *
 *  @author YunjieZhang
 */
abstract class GraphObj extends Graph {

    /** A new, empty Graph. */
    GraphObj() {
        _vertices = new ArrayList<>();
        _edges = new ArrayList<>();
        _lim = 0;

    }

    /** Return the number of vertices. */
    @Override
    public int vertexSize() {
        return _vertices.size();
    }

    @Override
    public int maxVertex() {
        int max = 0;
        for (int i:_vertices) {
            if (i > max) {
                max = i;
            }
        }
        return max;
    }

    @Override
    public int edgeSize() {
        return _edges.size();
    }

    @Override
    public abstract boolean isDirected();

    @Override
    public int outDegree(int v) {
        return getSuccessors(v).size();
    }

    @Override
    public abstract int inDegree(int v);

    @Override
    public boolean contains(int u) {
        for (int i:_vertices) {
            if (i == u) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains(int u, int v) {
        if (edgeId(u, v) == 0) {
            return false;
        }
        return true;
    }

    @Override
    public int add() {
        if (_lim == 0) {
            _vertices.add(1);
            _lim += 1;
            return _lim;
        }
        for (int i = 1; i <= _lim; i += 1) {
            if (!_vertices.contains(i)) {
                _vertices.add(i);
                return i;
            }
        }
        _lim += 1;
        _vertices.add(_lim);
        return _lim;
    }

    @Override
    public int add(int u, int v) {
        if (!contains(u) && !contains(v)) {
            return 0;
        }
        int[] newEdge = new int[2];
        newEdge[0] = u;
        newEdge[1] = v;
        if (isDirected()) {
            if (!contains(u, v)) {
                _edges.add(newEdge);
            }
        } else {
            if (!contains(u, v) && !contains(v, u)) {
                _edges.add(newEdge);
            }
        }
        return edgeId(u, v);
    }

    @Override
    public void remove(int v) {
        int index = 0;
        for (int i = 0; i < _vertices.size(); i += 1) {
            remove(_vertices.get(i), v);
            remove(v, _vertices.get(i));
            if (_vertices.get(i) == v) {
                index = i;
            }
        }
        _vertices.remove(index);

    }

    @Override
    public void remove(int u, int v) {
        for (int i = 0; i < _edges.size(); i += 1) {
            int[] cur = _edges.get(i);
            if (cur[0] == u && cur[1] == v) {
                _edges.remove(i);
                continue;
            }
            if (cur[1] == u && cur[0] == v) {
                _edges.remove(i);
                continue;
            }
        }
    }

    @Override
    public Iteration<Integer> vertices() {
        return Iteration.iteration(_vertices);
    }

    @Override
    public int successor(int v, int k) {
        ArrayList<Integer> successors = getSuccessors(v);
        if (successors.size() < k) {
            return 0;
        }
        return successors.get(k - 1);
    }

    @Override
    public abstract int predecessor(int v, int k);

    @Override
    public Iteration<Integer> successors(int v) {
        return Iteration.iteration(getSuccessors(v));
    }

    @Override
    public abstract Iteration<Integer> predecessors(int v);

    @Override
    public Iteration<int[]> edges() {
        return Iteration.iteration(_edges);
    }

    @Override
    protected void checkMyVertex(int v) {
        if (!contains(v)) {
            throw new IllegalArgumentException("vertex not from Graph");
        }
    }

    @Override
    protected int edgeId(int u, int v) {
        if (isDirected()) {
            for (int[] elem:_edges) {
                if (elem[0] == u && elem[1] == v) {
                    return _edges.indexOf(elem) + 1;
                }
            }
        } else {
            for (int[] elem:_edges) {
                if (elem[0] == u && elem[1] == v) {
                    return _edges.indexOf(elem) + 1;
                }
                if (elem[1] == u && elem[0] == v) {
                    return _edges.indexOf(elem) + 1;
                }
            }
        }
        return 0;
    }

    /** Return an arraylist of al successors of vertex V. */
    private ArrayList<Integer> getSuccessors(int v) {
        ArrayList<Integer> successorsOfV = new ArrayList<>();
        for (int[] elem:edges()) {
            if (elem[0] == v) {
                successorsOfV.add(elem[1]);
                continue;
            }
            if (!isDirected()) {
                if (elem[1] == v && elem[0] != elem[1]) {
                    successorsOfV.add(elem[0]);
                }
            }
        }
        return successorsOfV;
    }

    /** The arraylist to store all vertices. */
    private ArrayList<Integer> _vertices;
    /** The arraylist to store all edges. */
    private ArrayList<int[]> _edges;
    /** The number of the biggest vertex at present. */
    private int _lim;

}
