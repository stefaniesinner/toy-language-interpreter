package de.sesinner;

import java.util.ArrayList;
import java.util.List;

import static de.sesinner.TokenType.EOF;

/**
 * Reads the flat list of {@link Token} objects produced by the {@link Lexer} and builds the
 * <i>Abstract Syntax Tree (AST)</i>.
 * <p>
 * The parser acts as the second phase of the interpreter. It processes tokens one by one and produces a list of
 * {@link Stmt} nodes, which the {@link Interpreter} then executes.
 *
 * <p>
 * To build the tree, the parser uses a technique called <i>recursive descent</i>: each grammar rule is handled by its
 * own method, and methods call each other to handle nested structures. The deeper a method sits in the call chain, the
 * higher the binding precedence of the constructs it handles.
 */
class Parser {

    private final List<Token> tokens;
    private int current = 0;

    /**
     * Initializes the parser with the token list produced by the {@link Lexer}.
     *
     * @param tokens The complete list of tokens to parse.
     */
    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Parses the full token list and returns a list of top-level statements.
     * Top-level statements are separated by {@link TokenType#EOL} tokens.
     *
     * @return The list of parsed statements forming the program.
     */
    List<Stmt> parseProgram() {
        List<Stmt> stmts = new ArrayList<>();
        while (!hasNoMoreTokens()) {
            stmts.add(parseStmt());
        }
        return stmts;
    }

    private Stmt parseStmt() {
        return null;
    }

    /**
     * Checks if the end of the token list has been reached.
     * The {@link TokenType#EOF} token is always the last token produced by the {@link Lexer}.
     *
     * @return {@code true} if there are no more tokens to parse, otherwise {@code false}.
     */
    private boolean hasNoMoreTokens() {
        return peekToken().type() == EOF;
    }

    /**
     * Returns the current token without consuming it.
     *
     * @return The current token.
     */
    private Token peekToken() {
        return tokens.get(current);
    }
}
