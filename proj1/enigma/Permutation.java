package enigma;

import static enigma.EnigmaException.*;

import java.util.ArrayList;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Yunjie Zhang
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters not
     *  included in any cycle map to themselves. Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
        _pairs = new ArrayList<String>();
        String[] stringStore = cycles.split("\\W");

        for (int i = 0; i < stringStore.length; i += 1) {
            String temp = stringStore[i];
            if (temp.length() > 0) {
                _pairs.add(stringStore[i]);
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    void addCycle(String cycle) {
        _cycles += "(";
        _cycles += cycle;
        _cycles += ")";
        _pairs.add(cycle);
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char index = _alphabet.toChar(wrap(p));
        char value = permute(index);
        return wrap(_alphabet.toInt(value));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char index = _alphabet.toChar(c);
        char value = invert(index);
        return _alphabet.toInt(value);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int index;
        for (int i = 0; i < _pairs.size(); i += 1) {
            String temp = _pairs.get(i);
            for (int j = 0; j < temp.length(); j += 1) {
                if (temp.charAt(j) == p) {
                    return temp.charAt((j + 1) % temp.length());
                }
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        for (int i = 0; i < _pairs.size(); i += 1) {
            String temp = _pairs.get(i);
            for (int j = 0; j < temp.length(); j += 1) {
                if (temp.charAt(j) == c) {
                    if (j == 0) {
                        return temp.charAt(temp.length() - 1);
                    } else {
                        return temp.charAt(j - 1);
                    }
                }
            }
        }
        return c;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < _pairs.size(); i++) {
            String help = _pairs.get(i);
            if (help.length() == 1) {
                return true;
            }
        }
        return false;
    }
    /** Return cycles. */
    ArrayList<String> getPairs() {
        return _pairs;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
    /** Cycles of permutation.*/
    private String _cycles;
    /** Pairs paring up each pair in the cycle. */
    private ArrayList<String> _pairs;


}

