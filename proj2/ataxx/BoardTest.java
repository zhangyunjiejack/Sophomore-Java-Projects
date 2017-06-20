package ataxx;

import org.junit.Test;

import static org.junit.Assert.*;

/** Tests of the Board class.
 *  @author YunjieZhang
 */
public class BoardTest {

    /** Moves for test. */
    private static final String[]
        GAME1 = { "a7-b7", "a1-a2",
                  "a7-a6", "a2-a3",
                  "a6-a5", "a3-a4" };
    /** Also moves for tests. */
    private static final String[]
        GAME2 = { "a7-b5", "a1-b3",
                  "b5-b4"};

    /** Also moves for tests. */
    private static final String[]
        GAME3 = { "a7-b7", "a1-b1",
                  "b7-b6", "b1-b2"};


    /** The method to make moves. */
    private static void makeMoves(Board b, String[] moves) {
        for (String s : moves) {
            b.makeMove(s.charAt(0), s.charAt(1),
                       s.charAt(3), s.charAt(4));
        }
    }


    /** Test if extend works fine. */
    @Test
    public void testExtend() {
        Board b0 = new Board();
        b0.setBlock("d4");
        makeMoves(b0, GAME3);
        assertEquals(1, b0.getNumBlock());
        assertEquals(4, b0.redPieces());
        assertEquals(4, b0.bluePieces());
    }

    /** Test if undo works fine. */
    @Test
    public void testUndo() {
        Board b0 = new Board();
        Board b1 = new Board(b0);
        makeMoves(b0, GAME1);
        Board b2 = new Board(b0);
        for (int i = 0; i < GAME1.length; i += 1) {
            b0.undo();
        }
        assertEquals("failed to return to start", b1, b0);
        makeMoves(b0, GAME1);
        assertEquals("second pass failed to reach same position", b2, b0);
    }

    /** Test if jump works fine. */
    @Test
    public void testJump() {
        Board b0 = new Board();
        Board b1 = new Board(b0);
        makeMoves(b0, GAME2);
        Board b2 = new Board(b0);
        assertEquals("Number of Red pieces incorrect", 4, b0.redPieces());
        assertEquals("Number of Blue pieces incorrect", 1, b0.bluePieces());
        for (int i = 0; i < GAME2.length; i += 1) {
            b0.undo();
        }
        assertEquals("failed to return to start - 2", b1, b0);
        makeMoves(b0, GAME2);
        assertEquals("second pass failed to reach same position", b2, b0);
    }

    /** Test if setblocks works fine. */
    @Test
    public void testBlock() {
        Board b0 = new Board();
        b0.setBlock('c', '4');
        Board b1 = new Board(b0);
        makeMoves(b0, GAME2);
        Board b2 = new Board(b0);
        assertEquals("Number of blocks incorrect", 2, b0.getNumBlock());
        for (int i = 0; i < GAME2.length; i += 1) {
            b0.undo();
        }
        assertEquals("failed to return to start - 2", b1, b0);
        makeMoves(b0, GAME2);
        assertEquals("second pass failed to reach same position", b2, b0);
        assertEquals("not good", b2.getNumBlock(), b0.getNumBlock());
    }

    /** Test if gameover works fine. */
    @Test
    public void testGameOver() {
        Board b0 = new Board();
        assertEquals(false, b0.gameOver());
        Board b1 = new Board();
        b1.setBlock("a2");
        b1.setBlock("a3");
        b1.setBlock("b1");
        b1.setBlock("b2");
        b1.setBlock("b3");
        b1.setBlock("c1");
        b1.setBlock("c2");
        b1.setBlock("c3");
        assertEquals(true, b1.gameOver());
    }

    /** Test toString method. */
    @Test
    public void testToString() {
        Board b0 = new Board();
        b0.setBlock('c', '4');
        makeMoves(b0, GAME2);
        System.out.println(b0.toString(true));
        System.out.println();
        System.out.print(b0.toString(false));
    }
}
