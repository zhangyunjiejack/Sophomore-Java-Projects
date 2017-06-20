package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author YunjieZhang
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }
        _config = getInput(args[0]);
        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        try {
            Machine newMachine = readConfig();
            String first = _input.nextLine();
            if (first.charAt(0) != '*') {
                throw new NoSuchElementException();
            }
            setUp(newMachine, first);
            for (int i = 0; i < newMachine.numRotors()
                    - newMachine.numPawls(); i++) {
                Rotor temp = newMachine.usedRotors().get(i);
                if (temp.rotates()) {
                    throw new NoSuchElementException();
                }
            }
            while (_input.hasNextLine()) {
                String currentLine = _input.nextLine();
                if (currentLine.isEmpty()) {
                    _output.println();
                } else {
                    if (currentLine.charAt(0) == '*') {
                        setUp(newMachine, currentLine);
                    } else {
                        String msg = "";
                        Scanner currentScan = new Scanner(currentLine);
                        while (currentScan.hasNext()) {
                            String help = currentScan.next();
                            String converted = newMachine.convert(help);
                            msg += converted;
                        }
                        printMessageLine(msg);
                    }
                }
            }
        } catch (NoSuchElementException e) {
            throw error("Not begin with *");
        }

    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _allInfo = new ArrayList<String>();
            _alphabet = new Alphabet(_config.next());
            _allRotors = new ArrayList<Rotor>();
            if (!_config.hasNext()) {
                throw new NoSuchElementException();
            }
            int numRotors = _config.nextInt();
            int numPawls = _config.nextInt();
            while (_config.hasNext()) {
                _allInfo.add(_config.next());
            }
            while (_index < _allInfo.size()) {
                _allRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, numPawls, _allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _allInfo.get(_index).toUpperCase();
            String mNotches = _allInfo.get(_index + 1).toUpperCase();
            _index += 2;
            Permutation newPerm = new Permutation("", _alphabet);
            while (_index < _allInfo.size()) {
                String cycleElem = _allInfo.get(_index);
                int cycleLen = cycleElem.length();
                if (cycleElem.charAt(0) == '(') {
                    if (cycleElem.charAt(cycleLen - 1) != ')') {
                        throw new NoSuchElementException();
                    }
                    String[] temp = cycleElem.split("\\W");
                    int len = temp.length;
                    for (int i = 0; i < len; i += 1) {
                        if (temp[i].length() > 0) {
                            String part = temp[i];
                            newPerm.addCycle(part);
                        }
                    }
                    _index += 1;
                } else {
                    break;
                }
            }
            char type = mNotches.charAt(0);
            if (type == 'M') {
                String notches = mNotches.substring(1, mNotches.length());
                return new MovingRotor(name, newPerm, notches);
            }
            if (type == 'N') {
                return new FixedRotor(name, newPerm);
            }
            if (type == 'R') {
                return new Reflector(name, newPerm);
            } else {
                throw new NoSuchElementException();
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        try {
            M.getUsedRotors().clear();
            Scanner newLine = new Scanner(settings);
            String temp = newLine.next();
            if (!temp.equals("*")) {
                throw new IOException(temp + "is not *.");
            }
            int num = M.numRotors();
            String[] names = new String[num];
            Permutation plugboard = new Permutation("", _alphabet);
            for (int i = 0; i < num; i += 1) {
                names[i] = newLine.next().toUpperCase();
            }

            String sets = newLine.next();
            while (newLine.hasNext()) {
                String plugCycle = newLine.next();
                int len = plugCycle.length();
                plugboard.addCycle(plugCycle.substring(1, len - 1));
            }
            M.insertRotors(names);
            M.setRotors(sets);
            M.setPlugboard(plugboard);
        } catch (IOException e) {
            throw error("not good." + e.getMessage());
        }
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        int theRest = msg.length() % 5;
        for (int i = 0; i < msg.length() / 5; i += 1) {
            _output.print(msg.substring(5 * i, 5 * (i + 1)));
            _output.print(" ");
        }
        _output.print(msg.substring(msg.length() - theRest, msg.length()));
        _output.println();

    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** A collection of all rotors. */
    private ArrayList<Rotor> _allRotors;

    /** Store all information. */
    private ArrayList<String> _allInfo;

    /** Index of information pieces in _allInfo. */
    private int _index = 0;

}
