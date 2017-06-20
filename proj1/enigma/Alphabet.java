package enigma;

import static enigma.EnigmaException.*;

/* Extra Credit Only */

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author YunjieZhang
 */
class Alphabet {

    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _character = chars.toUpperCase();
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _character.length();
    }

    /** Returns true if C is in this alphabet. */
    boolean contains(char c) {
        String elem = Character.toString(Character.toUpperCase(c));
        return _character.contains(elem);
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        return _character.charAt(index);
    }

    /** Returns the index of character C, which must be in the alphabet. */
    int toInt(char c) {
        return _character.indexOf(Character.toUpperCase(c));
    }


    /** The characters. */
    private String _character;
}
