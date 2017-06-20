package ataxx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static ataxx.PieceColor.RED;
import static ataxx.PieceColor.BLUE;
import static ataxx.PieceColor.EMPTY;
import static java.lang.Math.min;
import static java.lang.Math.max;

/** A Player that computes its own moves.
 *  @author YunjieZhang
 */
class AI extends Player {

    /** Maximum minimax search depth before going to static evaluation. */
    private static final int MAX_DEPTH = 4;
    /** A position magnitude indicating a win (for red if positive, blue
     *  if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI for GAME that will play MYCOLOR. */
    AI(Game game, PieceColor myColor) {
        super(game, myColor);
        _type = "AI";
    }

    @Override
    Move myMove() {
        if (!board().canMove(myColor())) {
            return Move.pass();
        }
        Move move = findMove();
        return move;
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        if (myColor() == RED) {
            findMove(b, MAX_DEPTH, true, 1, -INFTY, INFTY);
        } else {
            findMove(b, MAX_DEPTH, true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /** Used to communicate best moves found by findMove, when asked for. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value >= BETA if SENSE==1,
     *  and minimal value or value <= ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels before using a static estimate. */
    private int findMove(Board board, int depth, boolean saveMove, int sense,
                         int alpha, int beta) {
        HashMap<Move, Integer> moveResults = new HashMap<Move, Integer>();
        int v;
        if (depth == 0) {
            ArrayList<Move> possibles = allPossibleMoves(board);
            return bottomCheck(possibles, board, moveResults);
        }
        if (board.gameOver()) {
            if (depth % 2 == 0) {
                int score = Integer.MIN_VALUE + 1;
                score = max(staticScore(board), score);
                return score;
            } else {
                int score = Integer.MAX_VALUE - 1;
                score = min(staticScore(board), score);
                return score;
            }
        }
        if (sense == 1) {
            v = Integer.MIN_VALUE + 1;
            ArrayList<Move> possibles = allPossibleMoves(board);
            for (Move move:possibles) {
                moveResults.put(move, 0);
                board.makeMove(move);
                int bScore = findMove(board, depth - 1, false, -1, alpha, beta);
                moveResults.replace(move, bScore);
                v = max(v, bScore);
                alpha = max(alpha, v);
                board.undo();
                if (beta <= alpha) {
                    break;
                }
            }
        } else {
            v = Integer.MAX_VALUE - 1;
            ArrayList<Move> possibles = allPossibleMoves(board);
            for (Move move:possibles) {
                moveResults.put(move, 0);
                board.makeMove(move);
                int bScore = findMove(board, depth - 1, false, 1, alpha, beta);
                moveResults.replace(move, bScore);
                v = min(v, bScore);
                beta = min(beta, v);
                board.undo();
                if (beta <= alpha) {
                    break;
                }
            }
        }
        ArrayList<Move> eqMoves = new ArrayList<Move>();
        if (saveMove) {
            for (Map.Entry<Move, Integer> elem:moveResults.entrySet()) {
                if (elem.getValue() == v) {
                    eqMoves.add(elem.getKey());
                }
            }
            _lastFoundMove = eqMoves.get(game().nextRandom(eqMoves.size()));
        }
        return v;
    }

    /** Return the best score of the bottom choice.
     * @param possibles all possible moves.
     * @param board the board
     * @param  moveResults hashmap of moves with corresponding results. */
    int bottomCheck(ArrayList<Move> possibles, Board board,
                    HashMap<Move, Integer> moveResults) {
        int score = Integer.MIN_VALUE + 1;
        for (Move move:possibles) {
            board.makeMove(move);
            score = max(score, staticScore(board));
            moveResults.put(move, staticScore(board));
            board.undo();
        }
        for (Map.Entry<Move, Integer> entry:moveResults.entrySet()) {
            if (entry.getValue() == score) {
                return score;
            }
        }
        return score;
    }

    /** Return the arrayList of all possible moves of a color.
     * @param  board is the board that we play on. */
    ArrayList<Move> allPossibleMoves(Board board) {
        ArrayList<Move> possibleRed = new ArrayList<Move>();
        ArrayList<Move> possibleBlue = new ArrayList<Move>();
        PieceColor color = board.whoseMove();
        for (char c = 'a'; c <= 'g'; c += 1) {
            for (char r = '1'; r <= '7'; r += 1) {
                PieceColor cur = board.get(c, r);
                Move move;
                if (cur.equals(color)) {
                    for (int col = -2; col <= 2; col += 1) {
                        for (int row = -2; row <= 2; row += 1) {
                            if (col == 0 && row == 0) {
                                continue;
                            }
                            char col1 = (char) (c + col);
                            char row1 = (char) (r + row);
                            if (board.get(col1, row1).equals(EMPTY)) {
                                move = Move.move(c, r, col1, row1);
                                if (board.legalMove(move)) {
                                    if (color.equals(RED)) {
                                        possibleRed.add(move);
                                    }
                                    if (color.equals(BLUE)) {
                                        possibleBlue.add(move);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (color.equals(RED)) {
            return possibleRed;
        } else {
            return possibleBlue;
        }
    }

    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        int redNum = board.redPieces();
        int blueNum = board.bluePieces();
        if (board.gameOver()) {
            if (redNum > blueNum) {
                return MAX_SCORE;
            } else {
                return MIN_SCORE;
            }
        }
        int score = redNum - blueNum;
        if (board.getLastMove().isPass()) {
            score -= 10;
        }
        return score;
    }

    /** Return the type of the player. */
    @Override
    String getType() {
        return _type;
    }
    /** The variable to show whether the player is an AI or a man. */
    private String _type;
    /** The maximum score to get from a game. */
    static final int MAX_SCORE = 49;
    /** The minimum score to get from a game. */
    static final int MIN_SCORE = 49;
}
