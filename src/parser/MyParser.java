package parser;


import symbol.Id;

import java.io.IOException;

import lexer.Keyword;
import lexer.Lexer;
import lexer.Tag;
import lexer.Token;
import symbol.SymbolTable;
import symbol.Type;

public class MyParser {

    private final Lexer lexer;
    private Token lookahead;

    public MyParser(Lexer lexer) throws IOException {
        this.lexer = lexer;
        move();
    }

    private void move() throws IOException {
        lookahead = lexer.scan();
    }

    // done
    private void match(int t) throws IOException {
        Token tok = lookahead;
        if (lookahead.tag == t) {
            move();
        } else {
            try {
                error("Syntax Error: '" + ((Keyword) tok).lexeme + "'");
            } catch (Exception e) {
                error("Syntax Error");
            }
        }
    }

    private void error(String s) {
        throw new Error("line " + Lexer.line + ": " + s);
    }

    public void start() throws IOException {
        program();
    }

    private void program() throws IOException {
        declaration_list();
    }

    private SymbolTable top = new SymbolTable(); // top symbol table

    // done
    private void declaration_list() throws IOException { //declaration_list → declaration declaration_list
        if (declaration())
            declaration_list();
//        declaration_list_prime();
    }

    // done
    private boolean declaration() throws IOException {
        if (lookahead.tag != Tag.END_FILE) {
            Type type = type_specifier();
            ID(type);
            switch (lookahead.tag) {
                case ';':
                    move();
                    return true;
                case '[':
                    NUM();
                    match(';');
                    return true;
                case '(':
                    move();
                    params();
                    match(')');
                    compound_stmt();
                    return true;
                default:
                    error("declaration error");
            }
        }
        return false;
    }

    // done
    private Type type_specifier() throws IOException {
        Token tok = lookahead;
        switch (lookahead.tag) {
            case Tag.INT:
                match(Tag.INT);
                return Type.INT;
            case Tag.VOID:
                match(Tag.VOID);
                return Type.VOID;
            default:
                try {
                    error("type " + ((Keyword) tok).lexeme + " not define");
                } catch (Exception e) {
                    error("type not define");
                }
        }
        return null;
    }

    // done
    private void var_declaration() throws IOException {
        Type type = type_specifier();
        ID(type);
        if (lookahead.tag == '[')
            NUM();
        match(';');
    }

    // done
    private void params() throws IOException { // params → param_list | void
        if (lookahead.tag == Tag.VOID) {
            match(Tag.VOID);
            return;
        }
        param_list();
    }

    // done
    private void param_list() throws IOException { // param_list → param param_list_prime
        param();
        param_list_prime();
    }

    // done
    private void param_list_prime() throws IOException { // param_list_prime →  , param param_list_prime
        if (lookahead.tag != ',')
            return;
        match(',');
        param();
        param_list_prime();
    }

    // done
    private void param() throws IOException { // param_list → param param_list_prime
        Type type = type_specifier();
        ID(type);
        if (lookahead.tag == '[') {
            match('[');
            match(']');
        }
    }

    // done
    private void compound_stmt() throws IOException { // compound_stmt → { local_declaration statement_list }
        match('{');
        local_declaration();
        statement_list();
        match('}');
    }

    // done
    private void local_declaration() throws IOException { // local_declaration → local_declaration var_declaration | empty
        while (lookahead.tag == Tag.INT || lookahead.tag == Tag.VOID) {
            var_declaration();
//            local_declaration();
        }
        // empty
    }

    // done
    private void statement_list() throws IOException { // statement_list → statement_list statement | empty
//        boolean flag = true;  // for checking when we have empty
        do {
            statement();
            match(';');
//            statement_list();
        } while (lookahead.tag != '}');
        // empty

    }

    // done
    private void statement() throws IOException {
        switch (lookahead.tag) {
            case ';': // expression-stmt → ;
                move();
                return;
            case Tag.ID: // expression-stmt → expression ;       FIRST(expression) = ID
                expression();
//                match(';');
                return;
            case '{': // statement → compound-stmt
                compound_stmt();
                return;
            case Tag.IF: // selection-stmt → if ( expression ) statement
                match(Tag.IF);
                match('(');
                expression();
                match(')');
                statement();
                if (lookahead.tag == Tag.ELSE) {
                    match(Tag.ELSE); // statement → if ( expression ) statement else statement
                    statement();
                }
                return;
            case Tag.WHILE: // iteration-stmt → while ( expression ) statement
                match(Tag.WHILE);
                match('(');
                expression();
                match(')');
                statement();
                return;
            case Tag.RETURN: // return-stmt → return ;
                match(Tag.RETURN);
                if (lookahead.tag == ';') {
//                    match(';');
                    return;
                }
                expression(); // return-stmt → return expression ;
//                match(';');
                return;
            default:
                error("statement error");
        }
    }


    private void expression() throws IOException { // expression → var = expression
        if (lookahead.tag == Tag.ID) {
            var();
            if (lookahead.tag == '='){
                match('=');
                expression();
            }
            else if (addop()){
                move();
                term();
            }
//            else
//                return;  // for when we're on  simple_expression → additive_expression → term → factor → var
        }
        simple_expression();
    }

    private void simple_expression() throws IOException { // simple_expression → additive-expression relop additive-expression | additive-expression
        additive_expression();
        if (relop()){
            move();
            additive_expression();
        }
    }

    private boolean relop() throws IOException {
        switch (lookahead.tag) {
            case '<':
            case Tag.LE:
            case Tag.GE:
            case '>':
            case Tag.NE:
            case Tag.EQ:
                return true;
            default:
                return false;
        }
    }

    private void additive_expression() throws IOException { // additive_expression → term additive_expression_prime
        term();
        additive_expression_prime();
    }

    private void additive_expression_prime() throws IOException { // additive_expression → addop term additive_expression | empty
        if (lookahead.tag == '+' || lookahead.tag == '-') {
            move();
            term();
            additive_expression();
        }
        // empty
    }

    private boolean addop() throws IOException {
        if (lookahead.tag == '+' || lookahead.tag == '-')
            return true;
        else
            return false;
//            error("arithmetic operation " + ((Keyword) lookahead).lexeme + " undeclared");
    }

    private void mulop() throws IOException {
        if (lookahead.tag == '*')
            match('*');
        else if (lookahead.tag == '/')
            match('/');
        else
            error("arithmetic operation " + ((Keyword) lookahead).lexeme + " undeclared");
    }

    private void term() throws IOException { // term → factor term_prime
        factor();
        term_prime();
    }

    private void term_prime() throws IOException { // term_prime → mulop factor term_prime | empty
        if (lookahead.tag == '*' || lookahead.tag == '/') {
            mulop();
            factor();
            term_prime();
        }
        //empty
    }

    private void factor() throws IOException {
        switch (lookahead.tag) {
            case '(': // factor → ( expression )
                move();
                expression();
                match(')');
            case Tag.ID:
                ID();
                if (lookahead.tag == '(') { // factor → call
                    match('(');
                    args();
                    match(')');
                } else if (lookahead.tag == '[') { // factor → var
                    match('[');
                    expression();
                    match(']');
                }
                return;
            case Tag.NUM: // factor → NUM
                match(Tag.NUM);
                return;
//            case Tag.IF: // selection-stmt → if ( expression ) statement
//                match(Tag.IF);
        }
    }

    private void args() throws IOException { // args → arg_list | empty
        if (lookahead.tag == Tag.ID || lookahead.tag == ',') // FIRST(args) = ID and ,
            arg_list();
        // empty
    }

    private void arg_list() throws IOException { // arg_list → expression arg_list_prime
        expression();
        arg_list_prime();
    }

    private void arg_list_prime() throws IOException { // arg_list_prime → , expression arg_list_prime
        if (lookahead.tag == ',') {
            match(',');
            expression();
            arg_list_prime();
        }
        //empty
    }

    private void var() throws IOException { // var → ID | ID [expression]
        if (lookahead.tag == Tag.ID) {
            ID();
            if (lookahead.tag == '[') {
                match('[');
                expression();
                match(']');
            }
//            else if (lookahead.tag != '=') {
//
//            }

        } else
            error("Syntax error");
    }

    private void ID(Type type) throws IOException {
        Token tok = lookahead;
        match(Tag.ID);
        if (top.getLocal(((Keyword) tok).lexeme) == null) {
            Id id = new Id((Keyword) tok, type);
            top.push(((Keyword) tok).lexeme, id);
        } else {
            error("duplicate declaration " + ((Keyword) tok).lexeme);
        }
    }

    private void ID() throws IOException {
        Token tok = lookahead;
        match(Tag.ID);
        if (top.getLocal(((Keyword) tok).lexeme) == null) {
            try {
                error("variable " + ((Keyword) lookahead).lexeme + " undeclared");
            } catch (Exception e) {
                error("variable undeclared");
            }
        }
    }

    private void NUM() throws IOException {
        match('[');
        match(Tag.NUM);
        match(']');
    }

}
