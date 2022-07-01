package lexer;

public class Keyword extends Token {

    public final String lexeme;

    public Keyword(String lexeme, int tag) {
        super(tag);
        this.lexeme = lexeme;
    }

    public static final Keyword EQUAL = new Keyword("==", Tag.EQ),
            N_EQUAL = new Keyword("!=", Tag.NE),
            L_EQUAL = new Keyword("<=", Tag.LE),
            G_EQUAL = new Keyword(">=", Tag.GE),
            IF = new Keyword("if", Tag.IF),
            ELSE = new Keyword("else", Tag.ELSE),
            WHILE = new Keyword("while", Tag.WHILE),
            VOID = new Keyword("void", Tag.VOID),
            RETURN = new Keyword("return", Tag.RETURN),
            INT = new Keyword("int", Tag.INT);

}
