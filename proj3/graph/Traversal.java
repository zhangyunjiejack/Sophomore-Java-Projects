package graph;

/* See restrictions in Graph.java. */


import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.Stack;
import java.util.Iterator;
import java.util.Arrays;

/** Implements a generalized traversal of a graph.  At any given time,
 *  there is a particular collection of untraversed vertices---the "fringe."
 *  Traversal consists of repeatedly removing an untraversed vertex
 *  from the fringe, visting it, and then adding its untraversed
 *  successors to the fringe.
 *
 *  Generally, the client will extend Traversal.  By overriding the visit
 *  method, the client can determine what happens when a node is visited.
 *  By supplying an appropriate type of Queue object to the constructor,
 *  the client can control the behavior of the fringe. By overriding the
 *  shouldPostVisit and postVisit methods, the client can arrange for
 *  post-visits of a node (as in depth-first search).  By overriding
 *  the reverseSuccessors and processSuccessor methods, the client can control
 *  the addition of neighbor vertices to the fringe when a vertex is visited.
 *
 *  Traversals may be interrupted or restarted, remembering the previously
 *  marked vertices.
 *  @author YunjieZhang
 */
public abstract class Traversal {

    /** A Traversal of G, using FRINGE as the fringe. */
    protected Traversal(Graph G, Queue<Integer> fringe) {
        _G = G;
        _fringe = fringe;
        _markLog = new ArrayList<>();
        _markIndex = new ArrayList<>();
        _postVisit = new Stack<>();
        for (int i:_G.vertices()) {
            _markIndex.add(i);
            _markLog.add(false);
        }
    }

    /** Unmark all vertices in the graph. */
    public void clear() {
        _fringe.clear();
        _markLog.clear();
        _postVisit.clear();
        for (int i:_markIndex) {
            _markLog.add(false);
        }

    }

    /** Initialize the fringe to V0 and perform a traversal. */
    private void traverse(Collection<Integer> V0) {
        clear();
        Iterator<Integer> vertices = V0.iterator();
        while (vertices.hasNext()) {
            int i = vertices.next();
            _fringe.add(i);
        }
        while (!_fringe.isEmpty()) {
            int v = _fringe.remove();
            while (!_postVisit.isEmpty()) {
                int tmp = _postVisit.peek();

                if (_G.contains(tmp, v)) {
                    break;
                }
                postVisit(_postVisit.pop());
            }

            if (!marked(v)) {
                mark(v);
                visit(v);

                if (shouldPostVisit(v)) {
                    _postVisit.push(v);
                }

                Iteration<Integer> successors = _G.successors(v);

                if (!reverseSuccessors(v)) {

                    for (int i : successors) {
                        if (processSuccessor(v, i)) {
                            _fringe.add(i);
                        }
                    }

                } else {
                    ArrayList<Integer> temp = new ArrayList<>();
                    while (successors.hasNext()) {
                        int successor = successors.next();
                        if (processSuccessor(v, successor)) {
                            temp.add(successor);
                        }
                    }
                    temp.addAll(_fringe);
                    _fringe.clear();
                    _fringe.addAll(temp);
                }
            }

        }
        while (!_postVisit.isEmpty()) {
            postVisit(_postVisit.pop());
        }
    }

    /** Initialize the fringe to { V0 } and perform a traversal. */
    public void traverse(int v0) {
        traverse(Arrays.<Integer>asList(v0));
    }

    /** Returns true iff V has been marked. */
    protected boolean marked(int v) {
        for (int i = 0; i < _markIndex.size(); i += 1) {
            if (_markIndex.get(i) == v) {
                return _markLog.get(i);
            }
        }
        return false;
    }

    /** Mark vertex V. */
    protected void mark(int v) {
        for (int i = 0; i < _markIndex.size(); i += 1) {
            if (_markIndex.get(i) == v) {
                _markLog.set(i, true);
            }
        }
    }

    /** Perform a visit on vertex V.  Returns false iff the traversal is to
     *  terminate immediately. */
    protected boolean visit(int v) {
        return true;
    }

    /** Return true if we should postVisit V after traversing its
     *  successors.  (Post-visiting generally is useful only for depth-first
     *  traversals, although we define it for all traversals.) */
    protected boolean shouldPostVisit(int v) {
        return false;
    }

    /** Revisit vertex V after traversing its successors.  Returns false iff
     *  the traversal is to terminate immediately. */
    protected boolean postVisit(int v) {
        return true;
    }

    /** Return true if we should schedule successors of V in reverse order. */
    protected boolean reverseSuccessors(int v) {
        return false;
    }

    /** Process successor V to U.  Returns true iff V is then to
     *  be added to the fringe.  By default, returns true iff V is unmarked. */
    protected boolean processSuccessor(int u, int v) {
        return !marked(v);
    }

    /** The graph being traversed. */
    private final Graph _G;
    /** The fringe. */
    protected final Queue<Integer> _fringe;
    /** The index of marked vertices. */
    private ArrayList<Integer> _markIndex;
    /** To keep track of all vertices, whether it has been visited or not. */
    private ArrayList<Boolean> _markLog;
    /** ArrayList to store postvisit vertices. */
    private Stack<Integer> _postVisit;

}
