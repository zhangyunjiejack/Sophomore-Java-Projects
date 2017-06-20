package cube;

import java.util.Observable;

import static java.lang.Math.abs;
import static java.lang.System.arraycopy;

/** Models an instance of the Cube puzzle: a cube with color on some sides
 *  sitting on a cell of a square grid, some of whose cells are colored.
 *  Any object may register to observe this model, using the (inherited)
 *  addObserver method.  The model notifies observers whenever it is modified.
 *  @author P. N. Hilfinger
 */
class CubeModel extends Observable {

    /** A blank cube puzzle of size 4. */
    CubeModel() {
        initialize(4, 0, 0, new boolean[4][4], new boolean[6]);
    }

    /** A copy of CUBE. */
    CubeModel(CubeModel cube) {
        initialize(cube);
    }

    /** Initialize puzzle of size SIDExSIDE with the cube initially at
     *  ROW0 and COL0, with square r, c painted iff PAINTED[r][c], and
     *  with face k painted iff FACEPAINTED[k] (see isPaintedFace).
     *  Assumes that
     *    * SIDE > 2.
     *    * PAINTED is SIDExSIDE.
     *    * 0 <= ROW0, COL0 < SIDE.
     *    * FACEPAINTED has length 6.
     */
    void initialize(int side, int row0, int col0, boolean[][] painted,
                    boolean[] facePainted) {
        _puzzleside = side;
        _puzzlecolumn = col0;
        _puzzlerow = row0;
        _puzzlepaint = painted;
        _cubepaint = facePainted;
        setChanged();
        notifyObservers();
    }

    /** Initialize puzzle of size SIDExSIDE with the cube initially at
     *  ROW0 and COL0, with square r, c painted iff PAINTED[r][c].
     *  The cube is initially blank.
     *  Assumes that
     *    * SIDE > 2.
     *    * PAINTED is SIDExSIDE.
     *    * 0 <= ROW0, COL0 < SIDE.
     */
    void initialize(int side, int row0, int col0, boolean[][] painted) {
        initialize(side, row0, col0, painted, new boolean[6]);
    }

    /** Initialize puzzle to be a copy of CUBE. */
    void initialize(CubeModel cube) {
        this._puzzleside = cube.side();
        this._puzzlerow = cube.cubeRow();
        this._puzzlecolumn = cube.cubeCol();
        this._moves = cube.moves();
        this._puzzlepaint = new boolean[cube.side()][cube.side()];
        this._cubepaint = new boolean[6];
        for (int i = 0; i < cube.side(); i++) {
            for (int j = 0; j < cube.side(); j++) {
                this._puzzlepaint[i][j] = cube._puzzlepaint[i][j];
            }
        }
        arraycopy(cube._cubepaint, 0, this._cubepaint, 0, 6);

        setChanged();
        notifyObservers();
    }

    /** Move the cube to (ROW, COL), if that position is on the board and
     *  vertically or horizontally adjacent to the current cube position.
     *  Transfers colors as specified by the rules.
     *  Throws IllegalArgumentException if preconditions are not met.
     */
    void move(int row, int col) {
        if (row < 0 || row >= _puzzleside || col < 0 || col >= _puzzleside) {
            throw new IllegalArgumentException();
        }
        if (row != _puzzlerow && col != _puzzlecolumn) {
            throw new IllegalArgumentException();
        }
        if (abs(row - _puzzlerow) > 1 || abs(col - _puzzlecolumn) > 1) {
            throw new IllegalArgumentException();
        }
        _moves += 1;
        if (col - _puzzlecolumn == 1) {
            _puzzlecolumn += 1;
            boolean temp = _cubepaint[5];
            _cubepaint[5] = _cubepaint[2];
            _cubepaint[2] = _cubepaint[4];
            _cubepaint[4] = _cubepaint[3];
            _cubepaint[3] = temp;
            boolean trade = _cubepaint[4];
            _cubepaint[4] = _puzzlepaint[row][col];
            _puzzlepaint[row][col] = trade;
        } else if (col - _puzzlecolumn == -1) {
            _puzzlecolumn -= 1;
            boolean temp = _cubepaint[3];
            _cubepaint[3] = _cubepaint[4];
            _cubepaint[4] = _cubepaint[2];
            _cubepaint[2] = _cubepaint[5];
            _cubepaint[5] = temp;
            boolean trade = _cubepaint[4];
            _cubepaint[4] = _puzzlepaint[row][col];
            _puzzlepaint[row][col] = trade;
        } else if (row - _puzzlerow == 1) {
            _puzzlerow += 1;
            boolean temp = _cubepaint[0];
            _cubepaint[0] = _cubepaint[4];
            _cubepaint[4] = _cubepaint[1];
            _cubepaint[1] = _cubepaint[5];
            _cubepaint[5] = temp;
            boolean trade = _cubepaint[4];
            _cubepaint[4] = _puzzlepaint[row][col];
            _puzzlepaint[row][col] = trade;
        } else if (row - _puzzlerow == -1) {
            _puzzlerow -= 1;
            boolean temp = _cubepaint[1];
            _cubepaint[1] = _cubepaint[4];
            _cubepaint[4] = _cubepaint[0];
            _cubepaint[0] = _cubepaint[5];
            _cubepaint[5] = temp;
            boolean trade = _cubepaint[4];
            _cubepaint[4] = _puzzlepaint[row][col];
            _puzzlepaint[row][col] = trade;
        }
        setChanged();
        notifyObservers();
    }

    /** Return the number of squares on a side. */
    int side() {
        return _puzzleside;
    }

    /** Return true iff square ROW, COL is painted.
     *  Requires 0 <= ROW, COL < board size. */
    boolean isPaintedSquare(int row, int col) {
        return _puzzlepaint[row][col];
    }

    /** Return current row of cube. */
    int cubeRow() {
        return _puzzlerow;
    }

    /** Return current column of cube. */
    int cubeCol() {
        return _puzzlecolumn;
    }

    /** Return the number of moves made on current puzzle. */
    int moves() {
        return _moves;
    }

    /** Return true iff face #FACE, 0 <= FACE < 6, of the cube is painted.
     *  Faces are numbered as follows:
     *    0: Vertical in the direction of row 0 (nearest row to player).
     *    1: Vertical in the direction of last row.
     *    2: Vertical in the direction of column 0 (left column).
     *    3: Vertical in the direction of last column.
     *    4: Bottom face.
     *    5: Top face.
     */
    boolean isPaintedFace(int face) {
        return _cubepaint[face];
    }

    /** Return true iff all faces are painted. */
    boolean allFacesPainted() {
        for (int face = 0; face <= 5; face += 1) {
            if (!_cubepaint[face]) {
                return false;
            }
        }
        return true;
    }

    /**Row and column of the cube, size of the board.*/
    private int _puzzlerow, _puzzlecolumn, _puzzleside;
    /**The matrix to show painted grids. */
    private boolean[][] _puzzlepaint;
    /**The array to record which faces of the cube are painted. */
    private boolean[] _cubepaint;
    /**Number of moves made on current puzzle. Initially 0.*/
    private int _moves = 0;
}
