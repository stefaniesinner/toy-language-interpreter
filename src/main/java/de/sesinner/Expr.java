package de.sesinner;

import java.util.List;

/**
 * Represents an expression in our language.
 * <p>
 * An expression is any piece of code that can be evaluated to produce a value. When the {@link Parser} reads the source
 * code, it connects {@link Expr} and {@link Stmt} nodes together to build a tree structure called the <i>Abstract
 * Syntax Tree (AST)</i>.
 * <p>
 * Expressions are the parts of that tree that compute values. They are embedded inside {@link Stmt} nodes which perform
 * the actions.
 */
sealed interface Expr {

    /**
     * A variable assignment.
     *
     * @param name The token holding the variable's name.
     * @param value The expression being evaluated and stored.
     */
    record Assign(Token name, Expr value) implements Expr {}

    /**
     * An operation with two operands.
     *
     * @param left The left operand.
     * @param operator The operator token.
     * @param right The right operand.
     */
    record Binary(Expr left, Token operator, Expr right) implements Expr {}

    /**
     * A function call, such as {@code add(1, 2)}.
     *
     * @param callee The expression being called.
     * @param paren The closing parenthesis token to pinpoint errors.
     * @param arguments The list of values passed into the function.
     */
    record Call(Expr callee, Token paren, List<Expr> arguments) implements Expr {}

    /**
     * An expression wrapped in parentheses to enforce the order of operations.
     *
     * @param expression The expression inside the parentheses.
     */
    record Grouping(Expr expression) implements Expr {}

    /**
     * A raw, fixed value written directly in the code, such as {@code 42} or {@code "hello"}.
     *
     * @param value The underlying Java value (e.g., Integer, String, Boolean).
     */
    record Literal(Object value) implements Expr {}

    /**
     * A logical operation, such as {@code a AND b} or {@code x OR y}.
     * The right side is only evaluated if the left side does not already determine the result.
     *
     * @param left The left-hand condition.
     * @param operator The logical operator token.
     * @param right The right-hand condition.
     */
    record Logical(Expr left, Token operator, Expr right) implements Expr {}

    /**
     * An operation applied in front of a single value, such as {@code -5} or {@code !true}.
     *
     * @param operator The prefix operator.
     * @param right The expression the operator modifies.
     */
    record Unary(Token operator, Expr right) implements Expr {}

    /**
     * Reading the value of a variable by its name.
     *
     * @param name The token holding the variable's name.
     */
    record Variable(Token name) implements Expr {}
}
