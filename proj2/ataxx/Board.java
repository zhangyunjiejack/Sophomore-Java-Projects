package ataxx;

/* Author: P. N. Hilfinger, (C) 2008. */

import static ataxx.GameException.error;
import static ataxx.PieceColor.BLUE;
import static ataxx.PieceColor.RED;
import static ataxx.PieceColor.EMPTY;
import static ataxx.PieceColor.BLOCKED;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Observable;
import java.util.Stack;
import static java.lang.Math.abs;
import java.util.Arrays;

/** Ataxx board. The squarestars are labeled by column (a char value between
 *  'a' - 2 and 'g' + 2) and row (a char value between '1' - 2 and '7'
 *  + 2) or by linearized index, an integer described below.  Values of
 *  the column outside 'a' and 'g' and of the row outside '1' to '7' denote
 *  two layers of border squares, which are always blocked.
 *  This artificial border (which is never actually printed) is a common
 *  trick that allows one to avoid testing for edge conditions.
 *  For example, to look at all the possible moves from a square, sq,
 *  on the normal board (i.e., not in the border region), one can simply
 *  look at all squares within two rows and columns of sq without worrying
 *  about going off the board. Since squares in the border region are
 *  blocked, the normal logic that prevents moving to a blocked square
 *  will apply.
 *
 *  For some purposes, it is useful to refer to squares using a single
 *  integer, which we call its "linearized index".  This is simply the
 *  number of the square in row-major order (counting from 0).
 *
 *  Moves on this board are denoted by Moves.
 *  @author YunjieZhang
 */
class Board extends Observable {

    /** Number of squares on a side of the board. */
    static final int SIDE = 7;
    /** Length of a side + an artificial 2-deep border region. */
    static final int EXTENDED_SIDE = SIDE + 4;

    /** Number of non-extending moves before game ends. */
    static final int JUMP_LIMIT = 25;

    /** A new, cleared board at the start of the game. */
    Board() {
        _board = new PieceColor[EXTENDED_SIDE * EXTENDED_SIDE];
        _whoseMove = RED;
        for (int i = 0; i < _board.length; i += 1) {
            _board[i] = EMPTY;
        }
        _numBlock = 0;
        initBlock();
        set('a', '1', BLUE);
        set('a', '7', RED);
        set('g', '1', RED);
        set('g', '7', BLUE);
        _redNum = 2;
        _blueNum = 2;
        _jumps = 0;
        _allBoards = new Stack<PieceColor[]>();
        _numBlue = new Stack<Integer>();
        _numRed = new Stack<Integer>();
        _allmoves = new Stack<Move>();
        _allBlocks = new ArrayList<Integer>();

        _allBoards.push(_board.clone());
        _numRed.push(_redNum);
        _numBlue.push(_redNum);
    }

    /** A copy of B. */
    @SuppressWarnings("unchecked")
    Board(Board b) {
        _board = b._board.clone();
        _jumps = b.numJumps();
        _whoseMove = b.whoseMove();
        _blueNum = b.bluePieces();
        _redNum = b.redPieces();
        _numBlock = b.blockPieces();
        _allBlocks = new ArrayList<Integer>(b.getAllBlocks());

        _allmoves = (Stack<Move>) b.allMoves().clone();
        _numBlue = (Stack<Integer>) b.getNumBlue().clone();
        _numRed = (Stack<Integer>) b.getNumRed().clone();
        _allBoards = (Stack<PieceColor[]>) b.getAllBoards().clone();
    }

    /** Return the linearized index of square COL ROW. */
    static int index(char col, char row) {
        return (row - '1' + 2) * EXTENDED_SIDE + (col - 'a' + 2);
    }

    /** Return the linearized index of the square that is DC columns and DR
     *  rows away from the square with index SQ. */
    static int neighbor(int sq, int dc, int dr) {
        return sq + dc + dr * EXTENDED_SIDE;
    }

    /** Clear me to my starting state, with pieces in their initial
     *  positions and no blocks. */
    void clear() {
        _whoseMove = RED;
        for (int i = 0; i < _board.length; i += 1) {
            _board[i] = EMPTY;
        }
        _numBlock = 0;
        initBlock();
        set('a', '1', BLUE);
        set('a', '7', RED);
        set('g', '1', RED);
        set('g', '7', BLUE);
        _redNum = 2;
        _blueNum = 2;
        _jumps = 0;
        _allmoves = new Stack<>();
        setChanged();
        notifyObservers();
    }

    /** Return true iff the game is over: i.e., if neither side has
     *  any moves, if one side has no pieces, or if there have been
     *  MAX_JUMPS consecutive jumps without intervening extends. */
    boolean gameOver() {
        if (numJumps() == JUMP_LIMIT) {
            return true;
        }
        if (redPieces() == 0 || bluePieces() == 0) {
            return true;
        }
        if (redPieces() + bluePieces() + _numBlock == SQ_NUMBER) {
            return true;
        }
        if (!canMove(RED) && !canMove(BLUE)) {
            return true;
        }
        return false;
    }

    /** Return number of red pieces on the board. */
    int redPieces() {
        return numPieces(RED);
    }

    /** Return number of blue pieces on the board. */
    int bluePieces() {
        return numPieces(BLUE);
    }

    /** Return number of blocks on the board. */
    int blockPieces() {
        return _numBlock;
    }

    /** Return the arraylist that stores sq of all blocks. */
    ArrayList<Integer> getAllBlocks() {
        return _allBlocks;
    }


    /** Return number of COLOR pieces on the board. */
    int numPieces(PieceColor color) {
        switch (color) {
        case RED:
            return _redNum;
        case BLUE:
            return _blueNum;
        case BLOCKED:
            return _numBlock;
        default:
            return 0;
        }
    }

    /** Increment numPieces(COLOR) by K. */
    private void incrPieces(PieceColor color, int k) {
        switch (color) {
        case RED:
            _redNum += k;
            break;
        case BLUE:
            _blueNum += k;
            break;
        case BLOCKED:
            _numBlock += k;
            break;
        default:
            break;
        }
    }

    /** The current contents of square CR, where 'a'-2 <= C <= 'g'+2, and
     *  '1'-2 <= R <= '7'+2.  Squares outside the range a1-g7 are all
     *  BLOCKED.  Returns the same value as get(index(C, R)). */
    PieceColor get(char c, char r) {
        return _board[index(c, r)];
    }

    /** Return the current contents of square with linearized index SQ. */
    PieceColor get(int sq) {
        return _board[sq];
    }

    /** Set get(C, R) to V, where 'a' <= C <= 'g', and
     *  '1' <= R <= '7'. */
    private void set(char c, char r, PieceColor v) {
        set(index(c, r), v);
    }

    /** Set square with linearized index SQ to V.  This operation is
     *  undoable. */
    private void set(int sq, PieceColor v) {
        _board[sq] = v;
    }

    /** Return true iff MOVE is legal on the current board. */
    boolean legalMove(Move move) {
        if (move == null) {
            return false;
        }
        if (move.isPass()) {
            if (canMove(_whoseMove)) {
                return false;
            }
            return true;
        }
        PieceColor cur = get(move.col0(), move.row0());
        if (cur.equals(EMPTY) || !cur.equals(_whoseMove)) {
            return false;
        }
        PieceColor destination = get(move.col1(), move.row1());
        if (destination != EMPTY) {
            return false;
        }
        int rowDif = abs(move.row1() - move.row0());
        int colDif = abs(move.col1() - move.col0());
        if (rowDif > 2 || colDif > 2) {
            return false;
        }
        if (rowDif == 0 && colDif == 0) {
            return false;
        }
        return true;
    }

    /** Return true iff player WHO can move, ignoring whether it is
     *  that player's move and whether the game is over. */
    boolean canMove(PieceColor who) {
        for (int i = 0; i < _board.length; i += 1) {
            if (_board[i] == who) {
                for (int c = -2; c <= 2; c += 1) {
                    for (int r = -2; r <= 2; r += 1) {
                        int index = neighbor(i, c, r);
                        if (_board[index] == EMPTY) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /** Return the color of the player who has the next move.  The
     *  value is arbitrary if gameOver(). */
    PieceColor whoseMove() {
        return _whoseMove;
    }

    /** Return total number of moves and passes since the last
     *  clear or the creation of the board. */
    int numMoves() {
        return _allmoves.size();
    }

    /** Return number of non-pass moves made in the current game since the
     *  last extend move added a piece to the board (or since the
     *  start of the game). Used to detect end-of-game. */
    int numJumps() {
        return _jumps;
    }

    /** Perform the move C0R0-C1R1, or pass if C0 is '-'.  For moves
     *  other than pass, assumes that legalMove(C0, R0, C1, R1). */
    void makeMove(char c0, char r0, char c1, char r1) {
        if (c0 == '-') {
            makeMove(Move.pass());
        } else {
            makeMove(Move.move(c0, r0, c1, r1));
        }
    }

    /** Make the MOVE on this Board, assuming it is legal. */
    void makeMove(Move move) {
        assert legalMove(move);
        if (move.isPass()) {
            pass();
            _jumps = 0;
            return;
        }
        if (move.isExtend()) {
            PieceColor cur = get(move.col0(), move.row0());
            set(index(move.col1(), move.row1()), cur);
            _jumps = 0;
            incrPieces(_whoseMove, 1);


        } else {
            PieceColor cur = get(move.col0(), move.row0());
            set(index(move.col0(), move.row0()), EMPTY);
            set(index(move.col1(), move.row1()), cur);
            _jumps += 1;
        }
        int sq = index(move.col1(), move.row1());
        for (int c = -1; c <= 1; c += 1) {
            for (int r = -1; r <= 1; r += 1) {
                int index = neighbor(sq, c, r);
                if (_board[index].equals(_whoseMove.opposite())) {
                    set(index, _whoseMove);
                    incrPieces(_whoseMove, 1);
                    incrPieces(_whoseMove.opposite(), -1);
                }
            }
        }
        _allmoves.push(move);
        _allBoards.push(_board.clone());
        _numBlue.push(_blueNum);
        _numRed.push(_blueNum);
        PieceColor opponent = _whoseMove.opposite();
        _whoseMove = opponent;
        setChanged();
        notifyObservers();
    }

    /** Update to indicate that the current player passes, assuming it
     *  is legal to do so.  The only effect is to change whoseMove(). */
    void pass() {
        assert !canMove(_whoseMove);
        _allmoves.push(Move.PASS);
        _allBoards.push(_board.clone());
        _numBlue.push(_blueNum);
        _numRed.push(_redNum);
        _whoseMove = _whoseMove.opposite();
        setChanged();
        notifyObservers();
    }

    /** Undo the last move. */
    void undo() {
        Move lastMove = _allmoves.pop();
        PieceColor[] lastBoard = _allBoards.pop();
        PieceColor[] toRecover = _allBoards.peek();
        if (lastMove == Move.PASS) {
            _jumps -= 1;
        }
        for (int i = 0; i < toRecover.length; i += 1) {
            _board[i] = toRecover[i];
        }
        _numRed.pop();
        _numBlue.pop();
        _whoseMove = _whoseMove.opposite();
        setChanged();
        notifyObservers();
    }

    /** Return true iff it is legal to place a block at C R. */
    boolean legalBlock(char c, char r) {
        PieceColor cur = _board[index(c, r)];
        return (cur != RED && cur != BLUE);
    }

    /** Return true iff it is legal to place a block at CR. */
    boolean legalBlock(String cr) {
        return legalBlock(cr.charAt(0), cr.charAt(1));
    }

    /** Set a block on the square C R and its reflections across the middle
     *  row and/or column, if that square is unoccupied and not
     *  in one of the corners. Has no effect if any of the squares is
     *  already occupied by a block.  It is an error to place a block on a
     *  piece. */
    void setBlock(char c, char r) {
        if (!legalBlock(c, r)) {
            throw error("illegal block placement");
        }
        int mirrorC = 'g' - c + INDEX;
        char mirrorCol = (char) mirrorC;
        int mirrorR = '7' - r + '1';
        char mirrorRow = (char) mirrorR;
        _board[index(c, r)] = BLOCKED;
        if (!_allBlocks.contains(index(c, r))) {
            _allBlocks.add(index(c, r));
            incrPieces(BLOCKED, 1);
        }
        _board[index(mirrorCol, r)] = BLOCKED;
        if (!_allBlocks.contains(index(mirrorCol, r))) {
            _allBlocks.add(index(mirrorCol, r));
            incrPieces(BLOCKED, 1);
        }
        _board[index(c, mirrorRow)] = BLOCKED;
        if (!_allBlocks.contains(index(c, mirrorRow))) {
            _allBlocks.add(index(c, mirrorRow));
            incrPieces(BLOCKED, 1);
        }
        _board[index(mirrorCol, mirrorRow)] = BLOCKED;
        if (!_allBlocks.contains(index(mirrorCol, mirrorRow))) {
            _allBlocks.add(index(mirrorCol, mirrorRow));
            incrPieces(BLOCKED, 1);
        }

        _allBoards.push(_board.clone());
        setChanged();
        notifyObservers();
    }

    /** Place a block at CR. */
    void setBlock(String cr) {
        setBlock(cr.charAt(0), cr.charAt(1));
    }

    /** Initialize the state where the outside 2 layers are blocks. */
    void initBlock() {
        for (int i = 0; i < _board.length; i += 1) {
            _board[i] = BLOCKED;
        }
        for (char i = 'a'; i <= 'g'; i += 1) {
            for (char j = '1'; j <= '7'; j += 1) {
                _board[index(i, j)] = EMPTY;
            }
        }
    }

    /** Return a list of all moves made since the last clear (or start of
     *  game). */
    Stack<Move> allMoves() {
        return _allmoves;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    /** .equals used only for testing purposes. */
    @Override
    public boolean equals(Object obj) {
        Board other = (Board) obj;
        return Arrays.equals(_board, other._board);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(_board);
    }

    /** Return a text depiction of the board (not a dump).  If LEGEND,
     *  supply row and column numbers around the edges. */
    String toString(boolean legend) {
        Formatter out = new Formatter();
        for (char r = '7'; r >= '1'; r -= 1) {
            if (legend) {
                out.format(Character.toString(r));
            }
            for (char c = 'a'; c <= 'g'; c += 1) {
                out.format(" ");
                PieceColor temp = _board[index(c, r)];
                if (temp.equals(RED)) {
                    out.format("r");
                }
                if (temp.equals(BLUE)) {
                    out.format("b");
                }
                if (temp.equals(BLOCKED)) {
                    out.format("X");
                }
                if (temp.equals(EMPTY)) {
                    out.format("-");
                }
            }
            out.format("%n");
        }
        if (legend) {
            out.format("  a b c d e f g%n");
        }
        return out.toString();
    }



    /** Return _numRed. */
    Stack<Integer> getNumRed() {
        return _numRed;
    }

    /** Return _numBlue. */
    Stack<Integer> getNumBlue() {
        return _numBlue;
    }

    /** Get _allBoards.
     * @return  returns all baords stored. */
    Stack<PieceColor[]> getAllBoards() {
        return _allBoards;
    }

    /** Return the last move made. */
    Move getLastMove() {
        return _allmoves.peek();
    }

    /** Return number of blocks. */
    int getNumBlock() {
        return _numBlock;
    }


    /** For reasons of efficiency in copying the board,
     *  we use a 1D array to represent it, using the usual access
     *  algorithm: row r, column c => index(r, c).
     *
     *  Next, instead of using a 7x7 board, we use an 11x11 board in
     *  which the outer two rows and columns are blocks, and
     *  row 2, column 2 actually represents row 0, column 0
     *  of the real board.  As a result of this trick, there is no
     *  need to special-case being near the edge: we don't move
     *  off the edge because it looks blocked.
     *
     *  Using characters as indices, it follows that if 'a' <= c <= 'g'
     *  and '1' <= r <= '7', then row c, column r of the board corresponds
     *  to board[(c -'a' + 2) + 11 (r - '1' + 2) ], or by a little
     *  re-grouping of terms, board[c + 11 * r + SQUARE_CORRECTION]. */
    private PieceColor[] _board;

    /** Player that is on move. */
    private PieceColor _whoseMove;
    /** Number of jumps made by players. */
    private int _jumps;
    /** Total number of red pieces, blue pieces, blocks and empty squares. */
    private int _redNum, _blueNum, _numBlock;
    /** To keep track of all blocks, which should not be undone. */
    private ArrayList<Integer> _allBlocks;
    /** The stack to store all moves. */
    private Stack<Move> _allmoves;
    /** *The stack to store all number of red pieces. */
    private Stack<Integer> _numRed;
    /** The stack to store all number of blue pieces. */
    private Stack<Integer> _numBlue;
    /** The stack to store all boards. */
    private Stack<PieceColor[]> _allBoards;
    /** A board has 49 squares. */
    static final int SQ_NUMBER = 49;
    /** The index of A. */
    static final int INDEX = 97;

}
