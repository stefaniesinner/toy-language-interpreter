package de.sesinner;

import java.util.List;

/**
 * Reads the flat list of {@link Token} objects produced by the {@link Lexer} and builds the <i>Abstract Syntax
 * Tree (AST)</i>.
 * <p>
 * The parser acts as the second phase of the interpreter. It processes tokens one by one and produces a list of
 * {@link Stmt} nodes, which the {@link Interpreter} then executes.
 * <p>
 * To build the tree, the parser uses a technique called <i>recursive descent</i>: each grammar rule is handled by its
 * own method, and methods call each other to handle nested structures.
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
}
