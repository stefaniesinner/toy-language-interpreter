package de.sesinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.sesinner.TokenType.*;

/**
 * Reads the raw source code and translates it into a list of {@link Token} objects.
 * <p>
 * The Lexer acts as the first phase of the interpreter. It reads the source code character by character, ignores
 * whitespaces, and groups characters into meaningful words. For every valid word, it assigns a {@link TokenType} and
 * creates a new {@link Token}.
 */
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

    /**
     * Initializes the Lexer with the raw source code provided by the user.
     *
     * @param source The complete code to interpret.
     */
    Lexer(String source) {
        this.source = source;
    }

    /**
     * Starts the scanning process.
     * It loops through the entire source code, extracting one word at a time, until it reaches the end.
     *
     * @return A list of all identified tokens in the correct order.
     */
    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current; // marks the beginning of the next word
            char c = scanNextChar();
            scanToken(c);
        }

        tokens.add(new Token(EOF, "", Optional.empty(), line));
        return tokens;
    }

    /**
     * Evaluates a single character to determine what kind of token it belongs to.
     *
     * @param c The character to read and categorize.
     */
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

    /**
     * Extracts a sequence of digits from the source code and converts it into a real Java Integer.
     * This is necessary so the interpreter can later perform actual math with it, rather than treating the number just
     * as text.
     */
    private void scanNumber() {
        while (Character.isDigit(peekNextChar())) {
            scanNextChar();
        }

        String text = source.substring(start, current);
        addToken(NUMBER, Integer.parseInt(text));
    }

    /**
     * Extracts a sequence of letters or numbers to form a word (e.g., a variable name).
     * If the word is a reserved language keyword, it gets a specific keyword token.
     * Otherwise, it is treated as a custom user-defined name (an identifier).
     */
    private void scanIdentifier() {
        while (Character.isLetterOrDigit(peekNextChar()) || peekNextChar() == '_') {
            scanNextChar();
        }

        String text = source.substring(start, current);
        TokenType type = KEYWORDS.getOrDefault(text, IDENTIFIER);
        addToken(type);
    }

    /**
     * Checks if we have reached the end of the source code string.
     *
     * @return {@code true} if there are no more characters to read, otherwise {@code false}.
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * To compare the upcoming character with the one we expect.
     * If they match, the pointer moves forward and consumes the character.
     * Useful for distinguishing two-character symbols.
     *
     * @param expected The character we hope to see.
     * @return {@code true} if it matched with the expected character, otherwise {@code false}.
     */
    private boolean match(char expected) {
        if (!isAtEnd() && source.charAt(current) == expected) {
            current++;
            return true;
        }
        return false;
    }

    /**
     * Inspects the upcoming character without moving the pointer forward.
     * Useful when we are currently reading a word and want to check if the next character belongs to it, without
     * accidentally consuming a character that belongs to the next token.
     *
     * @return The upcoming character, or a null character if we have reached the end of the source code.
     */
    private char peekNextChar() {
        if (isAtEnd()) {
            return EOF_CHAR;
        }
        return source.charAt(current);
    }

    /**
     * Reads the character at the current position and moves the internal pointer one step forward to progress through
     * the source code character by character.
     *
     * @return The character that was just read.
     */
    private char scanNextChar() {
        return source.charAt(current++);
    }

    /**
     * Creates a new token that has no specific literal value, e.g., a plus sign '+' or a keyword like 'true'.
     * The literal field of the resulting Token will be set to {@code Optional.empty()}.
     *
     * @param type The category of the token.
     */
    private void addToken(TokenType type) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, Optional.empty(), line));
    }

    /**
     * Creates a new token with a specific literal value and adds it to our internal list of tokens.
     * The provided literal is wrapped in an {@code Optional}.
     *
     * @param type The category of the token.
     * @param literal The actual parsed Java value (e.g., an Integer).
     */
    @SuppressWarnings("SameParameterValue")
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, Optional.of(literal), line));
    }
}