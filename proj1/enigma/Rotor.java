package enigma;


import java.util.ArrayList;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author YunjieZhang
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name.toUpperCase();
        _permutation = perm;
        _set = 0;
        _cyclesPerm = perm.getPairs();
        _checkAdvance = false;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.alphabet().size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _set;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        int change = posn - _set;

        for (int i = 0; i < _cyclesPerm.size(); i += 1) {
            String temp = _cyclesPerm.get(i);
            String newChars = "";
            for (int j = 0; j < temp.length(); j += 1) {
                int origin = _permutation.alphabet().toInt(temp.charAt(j));
                int current = _permutation.wrap(origin - change);
                char currentChar = _permutation.alphabet().toChar(current);
                newChars += currentChar;
            }
            _cyclesPerm.set(i, newChars);
        }
        _set = _permutation.wrap(posn);
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        set(_permutation.alphabet().toInt(cposn));
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        return _permutation.permute(p);
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        return _permutation.invert(e);
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    /** @param x set whether it should advance. */
    void setCheck(boolean x) {
        _checkAdvance = x;
    }

    /** Return whether it should rotate. */
    boolean getCheck() {
        return _checkAdvance;
    }

    /** Convert. */
    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemnted by this rotor in its 0 position. */
    protected Permutation _permutation;

    /** The setting of rotor. */
    protected int _set;

    /** The cycle of the permutation. */
    private ArrayList<String> _cyclesPerm;
    /** To check if it should advance. */
    private boolean _checkAdvance;
}
