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
 * The lexer acts as the first phase of the {@link Interpreter}. It reads the source code character by character,
 * ignores whitespaces, and groups characters into meaningful words. For every valid word, it assigns a
 * {@link TokenType} and creates a new {@link Token}.
 */
class Lexer {

    private static final char EOF_CHAR = '\0'; // end of the source string
    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();

    static {
        KEYWORDS.put("and", AND);
        KEYWORDS.put("do", DO);
        KEYWORDS.put("else", ELSE);
        KEYWORDS.put("false", FALSE);
        KEYWORDS.put("fun", FUN);
        KEYWORDS.put("if", IF);
        KEYWORDS.put("nil", NIL);
        KEYWORDS.put("or", OR);
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
     * It loops through the entire source code, extracting one token at a time, until it reaches the end.
     *
     * @return A list of all identified tokens in the correct order.
     */
    List<Token> scanTokens() {
        while (!isEndOfSource()) {
            start = current; // marks the beginning of the next word
            char c = advanceChar();
            scanToken(c);
        }
        // marks the end of the source code
        tokens.add(new Token(EOF, "", Optional.empty(), line));
        return tokens;
    }

    /**
     * Dispatches on a single character to produce the corresponding token.
     *
     * @param c The character to read and categorize.
     */
    private void scanToken(char c) {
        switch (c) {
            case '(' -> addToken(LEFT_PAREN);
            case ')' -> addToken(RIGHT_PAREN);
            case '{' -> addToken(LEFT_BRACE);
            case '}' -> addToken(RIGHT_BRACE);
            case ',' -> addToken(COMMA);
            case '-' -> addToken(MINUS);
            case '+' -> addToken(PLUS);
            case '/' -> addToken(SLASH);
            case '*' -> addToken(STAR);

            case '!' -> addToken(match('=') ? BANG_EQUAL : BANG);
            case '=' -> addToken(match('=') ? EQUAL_EQUAL : EQUAL);
            case '>' -> addToken(match('=') ? GREATER_EQUAL : GREATER);
            case '<' -> addToken(match('=') ? LESS_EQUAL : LESS);

            case ' ', '\r', '\t' -> {} // ignore whitespaces and line breaks (EOL)

            case '\n' -> { addToken(EOL); line++; }

            default -> {
                if (Character.isDigit(c)) {
                    scanNumber();
                } else if (Character.isLetter(c) || c == '_') {
                    scanIdentifier();
                } else {
                    throw new RuntimeException("Unexpected character: '" + c + "' at line " + line);
                }
            }
        }
    }

    /**
     * Extracts a sequence of digits and emits a {@link TokenType#NUMBER} token with its parsed {@code int} value.
     */
    private void scanNumber() {
        while (Character.isDigit(peekChar())) {
            advanceChar();
        }

        String text = source.substring(start, current);
        try {
            int value = Integer.parseInt(text);
            addToken(NUMBER, value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Number too large at line " + line + ": " + text);
        }
    }

    /**
     * Extracts a sequence of letters or numbers to form a word (e.g., a variable name).
     * If the word is a reserved language keyword, it is mapped to a specific keyword token.
     * Otherwise, it is treated as a custom user-defined name (an {@code IDENTIFIER}).
     */
    private void scanIdentifier() {
        while (Character.isLetterOrDigit(peekChar()) || peekChar() == '_') {
            advanceChar();
        }

        String text = source.substring(start, current);
        TokenType type = KEYWORDS.getOrDefault(text, IDENTIFIER);
        addToken(type);
    }

    /**
     * Checks if we have reached the end of the source code.
     *
     * @return {@code true} if there are no more characters to read, otherwise {@code false}.
     */
    private boolean isEndOfSource() {
        return current >= source.length();
    }

    /**
     * Consumes the current character if it equals {@code expected}, otherwise does nothing.
     * Used to distinguish two-character tokens such as {@code ==} from {@code =}.
     *
     * @param expected The character to compare against.
     * @return {@code true} if the character was consumed, {@code false} otherwise.
     */
    private boolean match(char expected) {
        if (!isEndOfSource() && source.charAt(current) == expected) {
            current++;
            return true;
        }
        return false;
    }

    /**
     * Returns the current character without consuming it.
     *
     * @return The current character, or {@code \0} if the end of the source has been reached.
     */
    private char peekChar() {
        if (isEndOfSource()) {
            return EOF_CHAR;
        }
        return source.charAt(current);
    }

    /**
     * Consumes the current character and advances to the next one.
     *
     * @return The character that was just consumed.
     */
    private char advanceChar() {
        return source.charAt(current++);
    }

    /**
     * Creates a new token that has no specific literal value, e.g., a plus sign {@code +}.
     * The literal field of the resulting {@link Token} will be set to {@code Optional.empty()}.
     *
     * @param type The category of the token.
     */
    private void addToken(TokenType type) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, Optional.empty(), line));
    }

    /**
     * Creates a new token with a specific literal value and adds it to our internal list of tokens.
     * The provided literal is wrapped in an {@link Optional}.
     *
     * @param type The category of the token.
     * @param literal The actual parsed Java value (e.g., an {@code Integer}).
     */
    @SuppressWarnings("SameParameterValue")
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, Optional.of(literal), line));
    }
}