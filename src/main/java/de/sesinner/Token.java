package de.sesinner;

import java.util.Optional;

/**
 * Represents a single, meaningful "word" in our language, extracted from the provided raw source code.
 *
 * @param type The category of the token.
 * @param lexeme The exact text as it appeared in the source code.
 * @param literal The parsed Java value or an empty string if not applicable.
 * @param line The line number where the token was found in the source code.
 */
record Token(TokenType type, String lexeme, Optional<Object> literal, int line) {}
