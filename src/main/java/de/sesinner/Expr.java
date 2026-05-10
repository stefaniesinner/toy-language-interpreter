package de.sesinner;

import java.util.List;

/**
 * Represents an expression in our language.
 * <p>
 * An expression is any piece of code that can be evaluated to produce a value. When the {@link Parser} reads the source
 * code, it connects these expressions together to build a tree structure, called the <i>Abstract Syntax Tree (AST)</i>.
 *
 * <p>
 * <b>Example:</b>
 * <br>
 * The source code {@code x = 1 + 2} is translated into the following structures:
 * <pre>
 *     Conceptual View:                 Actual AST Representation:
 *
 *            x                             Assign (name: "x")
 *            |                                     |
 *            +                           Binary (operator: "+")
 *          /   \                           /                \
 *         1     2               Literal (value: 1)      Literal (value: 2)
 * </pre>
 *
 * <p>
 * Because this interface is {@code sealed}, only the specific record types defined inside this file are permitted.
 * This ensures type safety when the interpreter evaluates the tree.
 */
sealed interface Expr {

    /**
     * Represents a variable assignment.
     * <p>
     * <b>Example:</b> {@code x = 5}
     *
     * @param name The {@code IDENTIFIER} token of the variable being assigned to.
     * @param value The expression whose result is stored in the variable.
     */
    record Assign(Token name, Expr value) implements Expr {}

    /**
     * Represents an operation with two operands.
     * <p>
     * <b>Examples:</b> {@code a + b}, {@code 5 == 3}
     *
     * @param left The left operand.
     * @param operator The operator token.
     * @param right The right operand.
     */
    record Binary(Expr left, Token operator, Expr right) implements Expr {}

    /**
     * Represents a function call.
     * <p>
     * <b>Example:</b> {@code add(1, 2)}
     *
     * @param callee The expression that produces the function to call (usually a variable name).
     * @param paren The closing parenthesis token. Stored so that error messages can point to the call.
     * @param arguments The values passed to the function.
     */
    record Call(Expr callee, Token paren, List<Expr> arguments) implements Expr {}

    /**
     * Represents an expression wrapped in parentheses.
     * Parentheses are used to control the order in which parts of an expression are evaluated.
     * <p>
     * <b>Example:</b> {@code (x + 2)}
     *
     * @param expression The expression inside the parentheses.
     */
    record Grouping(Expr expression) implements Expr {}

    /**
     * Represents a fixed value written directly in the source code.
     * <p>
     * <b>Examples:</b> {@code 42}, {@code true}
     *
     * @param value The value as a plain Java object (e.g. {@code Integer}, {@code Boolean}).
     */
    record Literal(Object value) implements Expr {}

    /**
     * Represents a logical operation using {@code AND} or {@code OR}.
     * Unlike {@link Binary}, the right side is only evaluated if it is still needed (for {@code AND} only when the left
     * side is true, for {@code OR} only when the left side is false).
     * <p>
     * <b>Example:</b> {@code a AND b}
     *
     * @param left The left-hand condition.
     * @param operator The logical operator token.
     * @param right The right-hand condition.
     */
    record Logical(Expr left, Token operator, Expr right) implements Expr {}

    /**
     * Represents an operation that is written in front of a single value.
     * <p>
     * <b>Examples:</b> {@code -5}, {@code !true}
     *
     * @param operator The operator token.
     * @param right The value the operator is applied to.
     */
    record Unary(Token operator, Expr right) implements Expr {}

    /**
     * Represents reading the value of a variable by its name.
     * <p>
     * <b>Example:</b> {@code myVar}
     *
     * @param name The {@code IDENTIFIER} token for the variable.
     */
    record Variable(Token name) implements Expr {}
}
