package de.sesinner;

import java.util.List;

/**
 * Represents a statement in our language.
 * <p>
 * A statement performs an action but does not produce a value. When the {@link Parser} reads the source code, it
 * connects {@link Expr} and {@link Stmt} nodes together to build a tree structure called the <i>Abstract Syntax
 * Tree (AST)</i>. Statements are the outer nodes of that tree that control the flow of the program. They contain
 * {@link Expr} nodes for the parts that compute values.
 */
sealed interface Stmt {

    /**
     * A sequence of statements grouped together, enclosed in curly braces.
     * Used as the body of a {@link Stmt.Function} definition.
     *
     * @param statements The list of statements inside the block.
     */
    record Block(List<Stmt> statements) implements Stmt {}

    /**
     * Wraps a single {@link Expr}, such as {@link Expr.Assign}, so it can be executed as a standalone statement.
     *
     * @param expression The expression to evaluate.
     */
    record Expression(Expr expression) implements Stmt {}

    /**
     * A function declaration that introduces a new named, reusable block of code.
     *
     * @param name The token holding the function's name.
     * @param params The list of parameter name tokens the function accepts.
     * @param body The list of statements to execute when the function is called.
     */
    record Function(Token name, List<Token> params, List<Stmt> body) implements Stmt {}

    /**
     * A conditional statement that runs one of two branches depending on a condition.
     *
     * @param condition The expression evaluated to decide which branch to take.
     * @param thenBranch The statement to execute when the condition is true.
     * @param elseBranch The statement to execute when the condition is false.
     */
    record If(Expr condition, Stmt thenBranch, Stmt elseBranch) implements Stmt {}

    /**
     * Exits the current function and passes a value back to the {@link Expr.Call}.
     *
     * @param keyword The {@code return} token.
     * @param value The expression whose result is sent back to the caller.
     */
    record Return(Token keyword, Expr value) implements Stmt {}

    /**
     * Executes its body as long as the condition remains true.
     *
     * @param condition The expression checked before each iteration.
     * @param body The statement to execute repeatedly.
     */
    record While(Expr condition, Stmt body) implements Stmt {}
}
