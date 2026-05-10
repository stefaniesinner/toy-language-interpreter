package de.sesinner;

import org.junit.jupiter.api.Test;

import java.util.List;

import static de.sesinner.TokenType.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the implementation of the {@link Lexer} class.
 */
class LexerTest {

    /**
     * Tests that an empty source code results in exactly one {@code EOF} token.
     */
    @Test
    void testScanTokens_emptySource_returnsEofToken() {
        Lexer lexer = new Lexer("");
        List<Token> tokens = lexer.scanTokens();

        assertEquals(1, tokens.size());
        assertEquals(EOF, tokens.get(0).type());
    }

    @Test
    void testScanTokens_singleCharacterTokens_returnsCorrectSequence() {
        Lexer lexer = new Lexer("({+/)*}-");
        List<Token> tokens = lexer.scanTokens();

        final TokenType[] expected = {
                LEFT_PAREN, LEFT_BRACE, PLUS, SLASH,
                RIGHT_PAREN, STAR, RIGHT_BRACE, MINUS,
                EOF
        };

        assertEquals(expected.length, tokens.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], tokens.get(i).type());
        }
    }

    @Test
    void testScanTokens_comma_isParsedAsCommaToken() {
        Lexer lexer = new Lexer("a, b");
        List<Token> tokens = lexer.scanTokens();

        final TokenType[] expected = {
                IDENTIFIER, COMMA, IDENTIFIER,
                EOF
        };

        assertEquals(expected.length, tokens.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], tokens.get(i).type());
        }
    }

    @Test
    void testScanTokens_multiCharacterTokens_returnsCorrectSequence() {
        Lexer lexer = new Lexer("= == > >= < <= !=");
        List<Token> tokens = lexer.scanTokens();

        final TokenType[] expected = {
                EQUAL, EQUAL_EQUAL,
                GREATER, GREATER_EQUAL,
                LESS, LESS_EQUAL,
                NOT_EQUAL,
                EOF
        };

        assertEquals(expected.length, tokens.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], tokens.get(i).type());
        }
    }

    @Test
    void testScanTokens_keywords_returnsKeywordToken() {
        Lexer lexer = new Lexer("do else false fun if return then true while");
        List<Token> tokens = lexer.scanTokens();

        final TokenType[] expected = {
                DO, ELSE, FALSE, FUN, IF, RETURN, THEN, TRUE, WHILE,
                EOF
        };

        assertEquals(expected.length, tokens.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], tokens.get(i).type());
        }
    }

    @Test
    void testScanTokens_identifiers_returnsIdentifierToken() {
        Lexer lexer = new Lexer("myVar _test123");
        List<Token> tokens = lexer.scanTokens();

        final TokenType[] expected = {
                IDENTIFIER, IDENTIFIER,
                EOF
        };

        assertEquals(expected.length, tokens.size());
        assertEquals("myVar", tokens.get(0).lexeme());
        assertEquals("_test123", tokens.get(1).lexeme());
    }

    @Test
    void testScanTokens_number_returnsNumberToken() {
        Lexer lexer = new Lexer("42");
        List<Token> tokens = lexer.scanTokens();

        assertEquals(2, tokens.size()); // NUMBER and EOF

        Token numberToken = tokens.get(0);
        assertEquals(NUMBER, numberToken.type());
        assertEquals("42", numberToken.lexeme());

        assertTrue(numberToken.literal().isPresent());
        assertEquals(42, numberToken.literal().get());
    }

    /**
     * Tests that spaces, tabs, and carriage returns are completely ignored by the lexer.
     */
    @Test
    void testScanTokens_whitespaces_areIgnored() {
        Lexer lexer = new Lexer(" \t \r");
        List<Token> tokens = lexer.scanTokens();

        // only EOF should remain
        assertEquals(1, tokens.size());
        assertEquals(EOF, tokens.get(0).type());
    }

    /**
     * Tests that {@code \n} characters generate {@code EOL} tokens and correctly increment the line counter.
     */
    @Test
    void testScanTokens_newLines_returnsEolTokensAndIncrementsLine() {
        Lexer lexer = new Lexer("x\n\ny");
        List<Token> tokens = lexer.scanTokens();

        assertEquals(5, tokens.size()); // IDENTIFIER, EOL, EOL, IDENTIFIER, EOF

        assertEquals(1, tokens.get(0).line()); // 'x' is on line 1
        assertEquals(EOL, tokens.get(1).type()); // first \n
        assertEquals(EOL, tokens.get(2).type()); // second \n
        assertEquals(3, tokens.get(3).line()); // 'y' is on line 3
    }

    @Test
    void testScanTokens_invalidCharacter_throwsRuntimeException() {
        Lexer lexer = new Lexer("x = 2%");

        try {
            lexer.scanTokens();
            fail("Lexer should throw an exception for unexpected character '%'");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Unexpected character: '%'"));
        }
    }

    @Test
    void testScanTokens_loneExclamationMark_throwsRuntimeException() {
        Lexer lexer = new Lexer("!");

        try {
            lexer.scanTokens();
            fail("Lexer should throw an exception for a lone exclamation mark");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Unexpected character: '!'"));
        }
    }

    /**
     * Tests that a number exceeding the maximum capacity of a 32-bit Java {@code Integer} is safely caught
     * and throws a controlled {@code RuntimeException}, rather than crashing the lexer with an
     * internal {@code NumberFormatException}.
     */
    @Test
    void testScanTokens_numberTooLarge_throwsRuntimeException() {
        Lexer lexer = new Lexer("9999999999999999999999");

        try {
            lexer.scanTokens();
            fail("Lexer should throw an exception for numbers that exceed the Integer limit");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Number too large"));
        }
    }
}