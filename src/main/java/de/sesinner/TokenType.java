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
    EQUAL, EQUAL_EQUAL, NOT_EQUAL, // = == !=
    GREATER, GREATER_EQUAL, // > >=
    LESS, LESS_EQUAL, // < <=

    // literals
    IDENTIFIER, NUMBER,

    // keywords
    DO, ELSE, FALSE, FUN, IF, RETURN, THEN, TRUE, WHILE,

    EOL, // separates statements at line breaks
    EOF  // marks the end of the source code
}
