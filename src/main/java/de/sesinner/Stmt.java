package de.sesinner;

import java.util.List;

/**
 * Represents a statement in our language.
 * <p>
 * A statement performs an action and controls the flow of the program or defines something. The {@link Parser} builds
 * a list of statements from the source code, and the {@link Interpreter} executes them one by one.
 *
 * <p>
 * Because this interface is {@code sealed}, only the specific record types defined inside this file are permitted.
 * This makes sure the {@link Interpreter} can safely handle every kind of statement.
 */
sealed interface Stmt {

    record Block(List<Stmt> statements) implements Stmt {}

    record Expression(Expr expression) implements Stmt {}

    record Function(Token name, List<Token> params, List<Stmt> body) implements Stmt {}

    record If(Expr condition, Stmt thenBranch, Stmt elseBranch) implements Stmt {}

    record Return(Token keyword, Expr value) implements Stmt {}

    record While(Expr condition, Stmt body) implements Stmt {}
}
