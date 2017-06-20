package enigma;

import java.util.ArrayList;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author YunjieZhang
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = new ArrayList<Character>();
        for (int i = 0; i < notches.length(); i += 1) {
            _notches.add(notches.charAt(i));
        }
    }

    @Override
    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return true;
    }

    /** Overriden atNotch method. */
    @Override
    boolean atNotch() {
        for (int i = 0; i < _notches.size(); i += 1) {
            char current = _notches.get(i);
            int temp = this.permutation().alphabet().toInt(current);
            if (this.setting() == temp) {
                return true;
            }
        }
        return false;
    }

    @Override
    void advance() {
        set(setting() + 1);
    }

    /** The notches on the rotor. */
    private ArrayList<Character> _notches;

}
