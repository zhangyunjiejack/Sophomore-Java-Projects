package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a reflector in the enigma.
 *  @author YunjieZhang
 */
class Reflector extends FixedRotor {

    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is PERM. */
    Reflector(String name, Permutation perm) {
        super(name, perm);
    }

    /** Return true iff I reflect. */
    @Override
    boolean reflecting() {
        return true;
    }

    /** To set the rotor to the needed position. */
    @Override
    void set(char cposn) {
        if (cposn != 'A') {
            throw error("reflector has only one position");
        }
    }


    @Override
    void set(int posn) {
        if (posn != 0) {
            throw error("reflector has only one position");
        }
    }

}
