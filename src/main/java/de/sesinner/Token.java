package de.sesinner;

record Token(TokenType type, String lexeme, Object literal, int line) {}
