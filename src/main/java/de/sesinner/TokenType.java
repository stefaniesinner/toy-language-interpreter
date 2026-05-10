package de.sesinner;

/**
 * Defines every valid operator, symbol, and keyword that our language understands.
 */
enum TokenType {

    // single-character tokens
    LEFT_PAREN, RIGHT_PAREN, // ( )
    LEFT_BRACE, RIGHT_BRACE, // { }
    COMMA, MINUS, PLUS, SLASH, STAR, // , - + / *

    // one or two character tokens
    BANG, BANG_EQUAL, // ! !=
    EQUAL, EQUAL_EQUAL, // = ==
    GREATER, GREATER_EQUAL, // > >=
    LESS, LESS_EQUAL, // < <=

    // literals
    IDENTIFIER, NUMBER,

    // keywords
    AND, DO, ELSE, FALSE, FUN, IF, NIL, OR, RETURN, THEN, TRUE, WHILE,

    EOL, // separates statements at line breaks
    EOF  // marks the end of the source code
}
