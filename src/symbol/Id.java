package symbol;

import lexer.Keyword;
import lexer.Token;

public class Id {

    public Token token;
    public Type type;

    public Id(Keyword token, Type type) {
        this.token = token;
        this.type = type;
    }

}
