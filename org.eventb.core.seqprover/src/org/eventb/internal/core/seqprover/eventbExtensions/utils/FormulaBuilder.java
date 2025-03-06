/*******************************************************************************
 * Copyright (c) 2022, 2025 Université de Lorraine and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Université de Lorraine - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.utils;

import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.DIV;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.EXPN;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.FUNIMAGE;
import static org.eventb.core.ast.Formula.GE;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.KPRJ1_GEN;
import static org.eventb.core.ast.Formula.KPRJ2_GEN;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LE;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.LOR;
import static org.eventb.core.ast.Formula.MAPSTO;
import static org.eventb.core.ast.Formula.MINUS;
import static org.eventb.core.ast.Formula.MUL;
import static org.eventb.core.ast.Formula.NOT;
import static org.eventb.core.ast.Formula.PLUS;
import static org.eventb.core.ast.Formula.TBIJ;
import static org.eventb.core.ast.Formula.UPTO;

import java.math.BigInteger;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression.Form;
import org.eventb.core.ast.Type;

/**
 * Builder class to create formulas in a simpler way than through
 * {@link FormulaFactory}.
 *
 * This class is not exported. Methods can be added as needed.
 *
 * @author Guillaume Verdier
 */
public class FormulaBuilder {

	private final FormulaFactory ff;

	/**
	 * Creates the builder.
	 *
	 * @param ff formula factory to use to build formulas
	 */
	public FormulaBuilder(FormulaFactory ff) {
		this.ff = ff;
	}

	// Types

	/**
	 * Builds the integer type.
	 *
	 * @return the integer type
	 */
	public IntegerType intType() {
		return ff.makeIntegerType();
	}

	/**
	 * Builds a type corresponding to the set of all relations between two given
	 * types.
	 *
	 * The result is ℙ(left×right).
	 *
	 * @param left  the left-hand side of the type
	 * @param right the right-hand side of the type
	 * @return the relational type between the two given types
	 */
	public PowerSetType relType(Type left, Type right) {
		return ff.makeRelationalType(left, right);
	}

	// Quantifiers

	/**
	 * Builds a bound identifier with the given name and type.
	 *
	 * @param name name of the identifier
	 * @param type type of the identifier
	 * @return the bound identifier with the given name and type
	 */
	public BoundIdentDecl boundIdentDecl(String name, Type type) {
		return ff.makeBoundIdentDecl(name, null, type);
	}

	/**
	 * Builds a bound identifier with the given index and type.
	 *
	 * @param index index of the identifier
	 * @param type  type of the identifier
	 * @return the bound identifier with the given index and type
	 */
	public BoundIdentifier boundIdent(int index, Type type) {
		return ff.makeBoundIdentifier(index, null, type);
	}

	/**
	 * Builds an existential quantification.
	 *
	 * @param decls bound identifier declarations
	 * @param pred  quantified predicate
	 * @return existential quantification with the given identifiers and predicate
	 */
	public Predicate exists(BoundIdentDecl[] decls, Predicate pred) {
		return ff.makeQuantifiedPredicate(EXISTS, decls, pred, null);
	}

	/**
	 * Builds an existential quantification with a single bound identifier.
	 *
	 * @param decl bound identifier declaration
	 * @param pred quantified predicate
	 * @return existential quantification with the given identifier and predicate
	 */
	public Predicate exists(BoundIdentDecl decl, Predicate pred) {
		return ff.makeQuantifiedPredicate(EXISTS, new BoundIdentDecl[] { decl }, pred, null);
	}

	/**
	 * Builds a universal quantification.
	 *
	 * @param decls bound identifier declarations
	 * @param pred  quantified predicate
	 * @return universal quantification with the given identifiers and predicate
	 */
	public Predicate forall(BoundIdentDecl[] decls, Predicate pred) {
		return ff.makeQuantifiedPredicate(FORALL, decls, pred, null);
	}

	/**
	 * Builds a universal quantification with a single bound identifier.
	 *
	 * @param decl bound identifier declaration
	 * @param pred quantified predicate
	 * @return universal quantification with the given identifier and predicate
	 */
	public Predicate forall(BoundIdentDecl decl, Predicate pred) {
		return ff.makeQuantifiedPredicate(FORALL, new BoundIdentDecl[] { decl }, pred, null);
	}

	// Expressions

	/**
	 * Builds a comprehension set.
	 *
	 * Depending on {@code form}, the set can be: { expr ∣ pred }, { decls · pred ∣
	 * expr } or {@code λ decls · pred ∣ expr}.
	 *
	 * @param decls bound identifier declarations
	 * @param pred  child predicate
	 * @param expr  child expression
	 * @param form  form the set
	 * @return comprehension set with the given identifiers, predicate, expression
	 *         and form
	 */
	public Expression cset(BoundIdentDecl[] decls, Predicate pred, Expression expr, Form form) {
		return ff.makeQuantifiedExpression(CSET, decls, pred, expr, null, form);
	}

	/**
	 * Builds a comprehension set with a single bound identifier.
	 *
	 * Depending on {@code form}, the set can be: { expr ∣ pred }, { decl · pred ∣
	 * expr } or {@code λ decl · pred ∣ expr}.
	 *
	 * @param decl bound identifier declaration
	 * @param pred child predicate
	 * @param expr child expression
	 * @param form form the set
	 * @return comprehension set with the given identifier, predicate, expression
	 *         and form
	 */
	public Expression cset(BoundIdentDecl decl, Predicate pred, Expression expr, Form form) {
		return ff.makeQuantifiedExpression(CSET, new BoundIdentDecl[] { decl }, pred, expr, null, form);
	}

	/**
	 * Builds an integer literal.
	 *
	 * @param i value of the literal
	 * @return integer literal with the given value
	 */
	public IntegerLiteral intLit(BigInteger i) {
		return ff.makeIntegerLiteral(i, null);
	}

	/**
	 * Builds a bijection.
	 *
	 * @param left  left-hand side of the expression
	 * @param right right-hand side of the expression
	 * @return the bijection left ⤖ right
	 */
	public Expression bij(Expression left, Expression right) {
		return ff.makeBinaryExpression(TBIJ, left, right, null);
	}

	/**
	 * Builds a function application.
	 *
	 * @param fun the function
	 * @param arg the argument
	 * @return the expression fun(arg)
	 */
	public Expression funimg(Expression fun, Expression arg) {
		return ff.makeBinaryExpression(FUNIMAGE, fun, arg, null);
	}

	/**
	 * Builds an interval set.
	 *
	 * @param left  left-hand side of the expression
	 * @param right right-hand side of the expression
	 * @return the set left..right
	 */
	public Expression upto(Expression left, Expression right) {
		return ff.makeBinaryExpression(UPTO, left, right, null);
	}

	/**
	 * Builds a maps to.
	 *
	 * @param left  left-hand side of the expression
	 * @param right right-hand side of the expression
	 * @return the expression left ↦ right
	 */
	public Expression mapsTo(Expression left, Expression right) {
		return ff.makeBinaryExpression(MAPSTO, left, right, null);
	}

	/**
	 * Builds a projection.
	 *
	 * @param prodType product type of the projection's input
	 * @return prj1
	 */
	public Expression prj1(ProductType prodType) {
		return ff.makeAtomicExpression(KPRJ1_GEN, null, relType(prodType, prodType.getLeft()));
	}

	/**
	 * Builds a projection.
	 *
	 * @param prodType product type of the projection's input
	 * @return prj2
	 */
	public Expression prj2(ProductType prodType) {
		return ff.makeAtomicExpression(KPRJ2_GEN, null, relType(prodType, prodType.getRight()));
	}

	/**
	 * Builds an addition.
	 *
	 * @param children children expressions
	 * @return the expression children[0] + children[1] + ...
	 */
	public Expression plus(Expression... children) {
		return ff.makeAssociativeExpression(PLUS, children, null);
	}

	/**
	 * Builds a multiplication.
	 *
	 * @param children children expressions
	 * @return the expression children[0] ∗ children[1] ∗ ...
	 */
	public Expression mul(Expression... children) {
		return ff.makeAssociativeExpression(MUL, children, null);
	}

	/**
	 * Builds a subtraction.
	 *
	 * @param left  left-hand side of the expression
	 * @param right right-hand side of the expression
	 * @return the expression left − right
	 */
	public Expression minus(Expression left, Expression right) {
		return ff.makeBinaryExpression(MINUS, left, right, null);
	}

	/**
	 * Builds a division.
	 *
	 * @param left  left-hand side of the expression
	 * @param right right-hand side of the expression
	 * @return the expression left ÷ right
	 */
	public Expression div(Expression left, Expression right) {
		return ff.makeBinaryExpression(DIV, left, right, null);
	}

	/**
	 * Builds an exponentiation.
	 *
	 * @param left  left-hand side of the expression
	 * @param right right-hand side of the expression
	 * @return the expression left ^ right
	 */
	public Expression expn(Expression left, Expression right) {
		return ff.makeBinaryExpression(EXPN, left, right, null);
	}

	// Predicates

	/**
	 * Builds a conjunction of predicates.
	 *
	 * @param preds predicates to conjunct
	 * @return conjunction of the given predicates
	 */
	public Predicate and(Predicate... preds) {
		return ff.makeAssociativePredicate(LAND, preds, null);
	}

	/**
	 * Builds a predicate for the finiteness of a set.
	 *
	 * @param expr set parameter
	 * @return the predicate finite(expr)
	 */
	public Predicate finite(Expression expr) {
		return ff.makeSimplePredicate(KFINITE, expr, null);
	}

	/**
	 * Builds a set membership predicate.
	 *
	 * @param element element to test
	 * @param set     set on which membership is tested
	 * @return the predicate element ∈ set
	 */
	public Predicate in(Expression element, Expression set) {
		return ff.makeRelationalPredicate(IN, element, set, null);
	}

	/**
	 * Builds a logical implication.
	 *
	 * @param left  left-hand side of the implication
	 * @param right right-hand side of the implication
	 * @return the predicate left ⇒ right
	 */
	public Predicate imp(Predicate left, Predicate right) {
		return ff.makeBinaryPredicate(LIMP, left, right, null);
	}

	/**
	 * Builds an equal relation.
	 *
	 * @param left  left-hand side of the relation
	 * @param right right-hand side of the relation
	 * @return the predicate left = right
	 */
	public Predicate equal(Expression left, Expression right) {
		return ff.makeRelationalPredicate(EQUAL, left, right, null);
	}

	/**
	 * Builds a not-equal relation.
	 *
	 * The not equal is returned normalised as the negation of an equality.
	 *
	 * @param left  left-hand side of the relation
	 * @param right right-hand side of the relation
	 * @return the predicate ¬ left = right
	 */
	public Predicate notequal(Expression left, Expression right) {
		return not(equal(left, right));
	}

	/**
	 * Builds a greater-or-equal relation.
	 *
	 * @param left  left-hand side of the relation
	 * @param right right-hand side of the relation
	 * @return the predicate left ≥ right
	 */
	public Predicate ge(Expression left, Expression right) {
		return ff.makeRelationalPredicate(GE, left, right, null);
	}

	/**
	 * Builds a less-or-equal relation.
	 *
	 * @param left  left-hand side of the relation
	 * @param right right-hand side of the relation
	 * @return the predicate left ≤ right
	 */
	public Predicate le(Expression left, Expression right) {
		return ff.makeRelationalPredicate(LE, left, right, null);
	}

	/**
	 * Builds a disjunction of predicates.
	 *
	 * @param preds predicates to conjunct
	 * @return disjunction of the given predicates
	 */
	public Predicate or(Predicate... preds) {
		return ff.makeAssociativePredicate(LOR, preds, null);
	}

	/**
	 * Builds the negation of a predicate.
	 *
	 * @param pred predicate to negate
	 * @return negation of the given predicate
	 */
	public Predicate not(Predicate pred) {
		return ff.makeUnaryPredicate(NOT, pred, null);
	}

}
