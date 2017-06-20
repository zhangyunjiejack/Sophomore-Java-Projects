package graph;

import org.junit.Test;
import ucb.junit.textui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/* You MAY add public @Test methods to this class.  You may also add
 * additional public classes containing "Testing" in their name. These
 * may not be part of your graph package per se (that is, it must be
 * possible to remove them and still have your package work). */

/** Unit tests for the graph package.  This class serves to dispatch
 *  other test classes, which are listed in the argument to runClasses.
 *  @author YunjieZhang
 */
public class UnitTest {

    /** Run all JUnit tests in the graph package. */
    public static void main(String[] ignored) {
        System.exit(textui.runClasses(graph.GraphTesting.class));
    }

    /** Test an empty graph. */
    @Test
    public void emptyGraph() {
        DirectedGraph g = new DirectedGraph();
        assertEquals("Initial graph has vertices", 0, g.vertexSize());
        assertEquals("Initial graph has edges", 0, g.edgeSize());
    }

    /** Test add and contain method. */
    @Test
    public void addContainTest() {
        UndirectedGraph g = new UndirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 2);
        g.add(2, 3);
        g.add(3, 4);
        g.add(2, 4);
        assertEquals("Edge should be contained 1", true, g.contains(2, 4));
        assertEquals("Edge can't be contained 2", false, g.contains(4, 4));
        assertEquals("Edge should be contained 3", true, g.contains(4, 2));
        assertEquals("Edge can't be contained 4", false, g.contains(1, 4));
        assertEquals("Vertex should be contained 1", true, g.contains(1));
        assertEquals("Vertex should not be contained 2", false, g.contains(5));
        DirectedGraph t = new DirectedGraph();
        t.add();
        t.add();
        t.add();
        t.add();
        t.add(1, 2);
        t.add(2, 3);
        t.add(3, 4);
        t.add(2, 4);
        assertEquals("Edge should be contained", true, t.contains(2, 4));
        assertEquals("Edge should not be contained", false, t.contains(4, 2));
        assertEquals("Edge should not be contained", false, t.contains(4, 4));
        assertEquals("Edge should not be contained", false, t.contains(1, 4));
        assertEquals("Vertex should be contained 1", true, t.contains(1));
        assertEquals("Vertex should not be contained 2", false, t.contains(0));
    }

    /** Test remove edge method. Undirected. */
    @Test
    public void removeEdgeTestUndirected() {
        UndirectedGraph g = new UndirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 2);
        g.add(2, 3);
        g.add(3, 4);
        g.add(2, 4);
        g.remove(4);
        assertEquals("U-Edge can't be contained 1", false, g.contains(2, 4));
        assertEquals("U-Edge can't be contained 2", false, g.contains(4, 2));
        assertEquals("U-Vertex can't be contained", false, g.contains(4));
        g.remove(2, 3);
        assertEquals("U-Edge can't be contained 3", false, g.contains(2, 3));
        assertEquals("U-Edge can't be contained 4", false, g.contains(3, 2));
        assertEquals("U-Edge should be contained 1", true, g.contains(1, 2));
        assertEquals("U-Vertice should be contained", true, g.contains(3));
        g.add();
        assertEquals("U-Vertice should be contaiend *", true, g.contains(4));
        g.remove(2);
        assertFalse(g.contains(2));
        g.add();
        assertTrue(g.contains(2));
    }

    /** Test directed graph with remove method. */
    @Test
    public void testRemoveDirected() {
        DirectedGraph t = new DirectedGraph();
        t.add();
        t.add();
        t.add();
        t.add();
        t.add(1, 2);
        t.add(2, 3);
        t.add(3, 4);
        t.add(2, 4);
        t.remove(4);
        assertEquals("D-Edge can't be contained 1", false, t.contains(2, 4));
        assertEquals("D-Edge can't be contained 2", false, t.contains(4, 2));
        assertEquals("D-Edge can't be contained 3", false, t.contains(3, 4));
        assertEquals("D-Vertex can't be contained", false, t.contains(4));
        t.remove(2, 3);
        assertEquals("D-Edge can't be contained 3", false, t.contains(2, 3));
        assertEquals("D-Edge can't be contained 4", false, t.contains(3, 2));
        assertEquals("D-Edge should be contained 1", true, t.contains(1, 2));
        assertEquals("D-Vertice should be contained", true, t.contains(3));
        t.add();
        assertEquals("U-Vertice should be contaiend *", true, t.contains(4));
        t.remove(2);
        assertFalse(t.contains(2));
        t.add();
        assertTrue(t.contains(2));
    }

    /** Test successors and predecessors. Undirected. */
    @Test
    public void testSuccessorPredecessorU() {
        UndirectedGraph g = new UndirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 2);
        g.add(2, 3);
        g.add(3, 4);
        g.add(2, 4);
        assertEquals("S num incorrect 1", 3, g.outDegree(2));
        assertEquals("Successor incorrect 1", 3, g.successor(2, 2));
        assertEquals("P num incorrect 1", 3, g.inDegree(2));
        assertEquals("Predecessor incorrect 1", 3, g.predecessor(2, 2));
        g.remove(4);
        assertEquals("Successor incorrect 2", 0, g.successor(2, 3));
        assertEquals("Predecessor incorrect 2", 0, g.successor(2, 3));
        g.add();
        assertEquals("Successor incorrect 3", 0, g.successor(2, 3));
        assertEquals("Predecessor incorrect 3", 0, g.successor(2, 3));
        g.add(4, 2);
        assertEquals("S num incorrect 4", 3, g.outDegree(2));
        assertEquals("Successor incorrect 4", 4, g.successor(2, 3));
        assertEquals("P num incorrect 4", 3, g.outDegree(2));
        assertEquals("Predecessor incorrect 4", 4, g.successor(2, 3));
        g.add(4, 4);
        assertEquals("S", 2, g.outDegree(4));

    }

    /** Test successors and predecessors. Directed. */
    @Test
    public void testSuccessorPredecessorD() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 2);
        g.add(2, 3);
        g.add(3, 4);
        g.add(2, 4);
        assertEquals("S num incorrect 1", 2, g.outDegree(2));
        assertEquals("Successor incorrect 1", 4, g.successor(2, 2));
        assertEquals("P num incorrect 1", 1, g.inDegree(2));
        assertEquals("Predecessor incorrect 1", 1, g.predecessor(2, 1));
        assertEquals("Predecessor incorrect 1 - 1", 0, g.predecessor(2, 2));
        g.remove(4);
        assertEquals("S num incorrect 2", 1, g.outDegree(2));
        assertEquals("Successor incorrect 2", 0, g.successor(2, 2));
        assertEquals("P num incorrect 2", 1, g.inDegree(2));
        assertEquals("Predecessor incorrect 2", 1, g.predecessor(2, 1));
        assertEquals("Predecessor incorrect 2", 0, g.predecessor(2, 2));
        g.add();
        assertEquals("S num incorrect 3", 1, g.outDegree(2));
        assertEquals("Successor incorrect 3", 0, g.successor(2, 2));
        assertEquals("P num incorrect 3", 1, g.inDegree(2));
        assertEquals("Predecessor incorrect 3", 0, g.predecessor(2, 2));
        g.add(4, 2);
        assertEquals("S num incorrect 4", 1, g.outDegree(2));
        assertEquals("Successor incorrect 4", 0, g.successor(2, 3));
        assertEquals("p num incorrect 4", 2, g.inDegree(2));
        assertEquals("Predecessor incorrect 4", 4, g.predecessor(2, 2));
        g.add(4, 4);
        assertEquals("S", 2, g.outDegree(4));
        assertEquals("P", 1, g.inDegree(4));
    }

}
