package de.sesinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.sesinner.TokenType.*;

class Lexer {

    private static final char EOF_CHAR = '\0'; // end of the source string
    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();

    static {
        KEYWORDS.put("do", DO);
        KEYWORDS.put("else", ELSE);
        KEYWORDS.put("false", FALSE);
        KEYWORDS.put("fun", FUN);
        KEYWORDS.put("if", IF);
        KEYWORDS.put("return", RETURN);
        KEYWORDS.put("then", THEN);
        KEYWORDS.put("true", TRUE);
        KEYWORDS.put("while", WHILE);
    }

    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    // Indices to track the boundaries of the current word we are reading
    private int start = 0;
    private int current = 0;
    private int line = 1;

    Lexer(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current; // marks the beginning of the next word
            char c = scanNextChar();
            scanToken(c);
        }

        tokens.add(new Token(EOF, "", "", line));
        return tokens;
    }

    private void scanToken(char c) {
        switch (c) {
            // single-character tokens
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case '/': addToken(SLASH); break;
            case '*': addToken(STAR); break;

            // one or two character tokens
            case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;
            case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '!':
                if (match('=')) {
                    addToken(NOT_EQUAL);
                } else {
                    throw new RuntimeException("Unexpected character: '!' at line " + line);
                }
                break;

            // whitespaces and new lines (EOL)
            case ' ':
            case '\r':
            case '\t':
                // ignore whitespace
                break;

            case '\n':
                addToken(EOL);
                line++; break;

            default:
                if (Character.isDigit(c)) {
                    scanNumber();
                } else if (Character.isLetter(c) || c == '_') {
                    scanIdentifier();
                } else {
                    throw new RuntimeException("Unexpected character: '" + c + "' at line " + line);
                }
                break;
        }
    }

    private void scanNumber() {
        while (Character.isDigit(peekNextChar())) {
            scanNextChar();
        }

        String text = source.substring(start, current);
        addToken(NUMBER, Integer.parseInt(text));
    }

    private void scanIdentifier() {
        while (Character.isLetterOrDigit(peekNextChar()) || peekNextChar() == '_') {
            scanNextChar();
        }

        String text = source.substring(start, current);
        TokenType type = KEYWORDS.getOrDefault(text, IDENTIFIER);
        addToken(type);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private boolean match(char expected) {
        if (!isAtEnd() && source.charAt(current) == expected) {
            current++;
            return true;
        }
        return false;
    }

    private char peekNextChar() {
        if (isAtEnd()) {
            return EOF_CHAR;
        }
        return source.charAt(current);
    }

    private char scanNextChar() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, "");
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
