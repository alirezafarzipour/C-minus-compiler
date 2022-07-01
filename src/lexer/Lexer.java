package lexer;

import java.io.IOException;
import java.util.Hashtable;

public class Lexer {

    private Hashtable<String, Keyword> words = new Hashtable<>();

    private char peek = ' ';

    public static int line = 1;


    private void reserve(Keyword kword) {
        words.put(kword.lexeme, kword);
    }


    private void reserveKeywords() {

        reserve(Keyword.WHILE);
        reserve(Keyword.IF);
        reserve(Keyword.ELSE);
        reserve(Keyword.INT);
        reserve(Keyword.RETURN);
        reserve(Keyword.VOID);

    }

    // call reserveKeywords() for all Keywords we need
    public Lexer() {
        reserveKeywords();
    }


    // read one character from input program
    private void read() throws IOException {
        peek = (char) System.in.read();
    }


    // read double character like == or >=
    private boolean read(char c) throws IOException {
        read();
        if (peek != c) {
            return false;
        }
        peek = ' ';
        return true;
    }


    // scan input program and character that is in peek
    public Token scan() throws IOException {

        // for skip empty space and comment
        for (; ; read()) {
            if (peek == ' ' || peek == '\t') {
                //continue;
            } else if (peek == '\n') {
                line = line + 1;
            } else if (peek == '/') {
                if (read('*')) //see  /*  and search for */
                    while (true)
                        if(read('*'))
                            if(read('/'))
                                break;
            } else {
                break;
            }
        }

        // check all operator that we have
        switch (peek) {
            case '>':
                if (read('=')) {
                    return Keyword.G_EQUAL;
                } else {
                    return new Token('>');
                }
            case '<':
                if (read('=')) {
                    return Keyword.L_EQUAL;
                } else {
                    return new Token('<');
                }
            case '=':
                if (read('=')) {
                    return Keyword.EQUAL;
                } else {
                    return new Token('=');
                }
            case '!':
                if (read('=')) {
                    return Keyword.N_EQUAL;
                } else {
                    return new Token('!');
                }
        }

        // if input is Digit
        if (Character.isDigit(peek)) {
            int v = 0;
            do {
                v = 10 * v + Character.digit(peek, 10);
                read();
            } while (Character.isDigit(peek));

            return new Num(v);
        }

        // if input is Letter
        if (Character.isLetter((int) peek)) {
            StringBuilder b = new StringBuilder();
            do {
                b.append(peek);
                read();
            } while (Character.isLetterOrDigit((int) peek));
            String s = b.toString();

            // check for " is reserved or not "
            Keyword w = (Keyword) words.get(s);
            if (w != null) {
                return w;
            }
            w = new Keyword(s, Tag.ID);
            words.put(s, w);
            return new Keyword(s, Tag.ID);
        }

        Token t = new Token(peek);
        peek = ' ';
        return t;
    }

}
