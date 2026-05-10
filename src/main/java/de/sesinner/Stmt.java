package de.sesinner;

import java.util.List;

/**
 * Represents a statement in our language.
 * <p>
 * A statement performs an action but does not produce a value. The {@link Parser} builds a list of statements from the
 * source code, and the {@link Interpreter} executes them one by one.
 *
 * <p>
 * <b>Example:</b>
 * <br>
 * The source code {@code if x > 0 then y = 1 else y = 0} is translated into the following structures:
 * <pre>
 * If (condition: x > 0)
 *   then Branch: Expression -> Assign (name: "y", value: 1)
 *   else Branch: Expression -> Assign (name: "y", value: 0)
 * </pre>
 *
 * <p>
 * This interface is {@code sealed}, meaning only the specific types listed below are allowed.
 * This makes evaluating the tree safer and easier for the {@link Interpreter}.
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
