package enigma;

import java.util.ArrayList;
import java.util.Collection;

/** Class that represents a complete enigma machine.
 *  @author YunjieZhang
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _numPawls = pawls;
        _usedRotors = new ArrayList<Rotor>();
        _allRotors = new ArrayList<Rotor>();
        _allRotors.addAll(allRotors);
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _numPawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        for (int i = 0; i < rotors.length; i += 1) {
            String name = rotors[i].toUpperCase();
            for (Rotor rotor:_allRotors) {
                if (rotor.name().toUpperCase().equals(name)) {
                    _usedRotors.add(rotor);
                }
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of four
     *  upper-case letters. The first letter refers to the leftmost
     *  rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        int numUse = _usedRotors.size();
        for (int i = 1, j = 0; i < numRotors() && j < setting.length();
             i += 1, j += 1) {
            _usedRotors.get(i).set(setting.charAt(j));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        int temp = c;
        int size = _usedRotors.size();
        temp = _plugboard.permute(temp);
        for (int i = 1; i < size - 1; i += 1) {
            boolean selfNotch =  _usedRotors.get(i).atNotch();
            if (_usedRotors.get(i + 1).atNotch() || selfNotch) {
                if (i != 1) {
                    _usedRotors.get(i).setCheck(true);
                }
            }
        }
        if (size > 3) {
            if (_usedRotors.get(2).atNotch()
                    && !_usedRotors.get(3).atNotch()) {
                _usedRotors.get(2).setCheck(false);
            }
        }
        for (int i = 1; i < size - 1; i += 1) {
            if (_usedRotors.get(i).getCheck()) {
                _usedRotors.get(i).advance();
                _usedRotors.get(i).setCheck(false);
            }
        }

        _usedRotors.get(size - 1).advance();

        int rotorIndex = size - 1;

        while (rotorIndex >= 0) {
            temp = _usedRotors.get(rotorIndex).convertForward(temp);
            rotorIndex -= 1;
        }
        for (int i = 1; i < size; i += 1) {
            temp = _usedRotors.get(i).convertBackward(temp);
        }
        temp = _plugboard.invert(temp);
        return temp;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *
     *  the rotors accordingly. */
    String convert(String msg) {
        int temp;
        msg = msg.toUpperCase();
        String result = "";
        for (int i = 0; i < msg.length(); i += 1) {
            char elem = msg.charAt(i);
            temp = convert(_alphabet.toInt(elem));
            result += _alphabet.toChar(temp);
        }
        return result;
    }

    /** Return the rotors that are needed. */
    ArrayList<Rotor> usedRotors() {
        return _usedRotors;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Return rotors needed. */
    public ArrayList<Rotor> getUsedRotors() {
        return _usedRotors;
    }
    /** Numnber of rotors. */
    private int _numRotors;
    /** Number of pawls. */
    private int _numPawls;
    /** Collection of rotors. */
    private ArrayList<Rotor> _allRotors;
    /** Collection of rotors that are being used. */
    private ArrayList<Rotor> _usedRotors;
    /** Plugboard. */
    private Permutation _plugboard;
}
