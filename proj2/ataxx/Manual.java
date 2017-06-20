package ataxx;

/** A Player that receives its moves from its Game's getMoveCmnd method.
 *  @author YunjieZhang
 */
class Manual extends Player {

    /** A Player that will play MYCOLOR on GAME, taking its moves from
     *  GAME. */
    Manual(Game game, PieceColor myColor) {
        super(game, myColor);
        _type = "manual";
    }

    /** Return the type of the player. */
    @Override
    String getType() {
        return _type;
    }
    /** Return my choice of move. */
    @Override
    Move myMove() {
        Command temp;
        if (!game().board().canMove(myColor())) {
            return Move.pass();
        }
        temp = game().getMoveCmnd(myColor().toString() + ": ");
        String[] operands = temp.operands();
        char col0 = operands[0].charAt(0);
        char row0 = operands[1].charAt(0);
        char col1 = operands[2].charAt(0);
        char row1 = operands[3].charAt(0);
        return Move.move(col0, row0, col1, row1);
    }
    /** The variable to show whether the player is an AI or a man. */
    private String _type;

}

