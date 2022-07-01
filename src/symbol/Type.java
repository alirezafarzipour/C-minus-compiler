package symbol;

import lexer.Keyword;
import lexer.Tag;

public class Type extends Keyword {

    public Type(String lexeme, int tag) {
        super(lexeme, tag);
    }

    public static Type INT = new Type("int", Tag.INT);
    public static Type VOID = new Type("void", Tag.VOID);

}
