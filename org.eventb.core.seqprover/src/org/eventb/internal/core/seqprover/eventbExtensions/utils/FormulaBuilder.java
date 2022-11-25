/*******************************************************************************
 * Copyright (c) 2022 Université de Lorraine and others.
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
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.LOR;
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
	 * Create the builder.
	 *
	 * @param ff formula factory to use to build formulas
	 */
	public FormulaBuilder(FormulaFactory ff) {
		this.ff = ff;
	}

	// Types

	/**
	 * Build the integer type.
	 *
	 * @return the integer type
	 */
	public IntegerType intType() {
		return ff.makeIntegerType();
	}

	/**
	 * Build a type corresponding to the set of all relations between two given
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
	 * Build a bound identifier with the given name and type.
	 *
	 * @param name name of the identifier
	 * @param type type of the identifier
	 * @return the bound identifier with the given name and type
	 */
	public BoundIdentDecl boundIdentDecl(String name, Type type) {
		return ff.makeBoundIdentDecl(name, null, type);
	}

	/**
	 * Build a bound identifier with the given index and type.
	 *
	 * @param index index of the identifier
	 * @param type  type of the identifier
	 * @return the bound identifier with the given index and type
	 */
	public BoundIdentifier boundIdent(int index, Type type) {
		return ff.makeBoundIdentifier(index, null, type);
	}

	/**
	 * Build an existential quantification.
	 *
	 * @param decls bound identifier declarations
	 * @param pred  quantified predicate
	 * @return existential quantification with the given identifiers and predicate
	 */
	public Predicate exists(BoundIdentDecl[] decls, Predicate pred) {
		return ff.makeQuantifiedPredicate(EXISTS, decls, pred, null);
	}

	/**
	 * Build an existential quantification with a single bound identifier.
	 *
	 * @param decl bound identifier declaration
	 * @param pred quantified predicate
	 * @return existential quantification with the given identifier and predicate
	 */
	public Predicate exists(BoundIdentDecl decl, Predicate pred) {
		return ff.makeQuantifiedPredicate(EXISTS, new BoundIdentDecl[] { decl }, pred, null);
	}

	/**
	 * Build a universal quantification.
	 *
	 * @param decls bound identifier declarations
	 * @param pred  quantified predicate
	 * @return universal quantification with the given identifiers and predicate
	 */
	public Predicate forall(BoundIdentDecl[] decls, Predicate pred) {
		return ff.makeQuantifiedPredicate(FORALL, decls, pred, null);
	}

	/**
	 * Build a universal quantification with a single bound identifier.
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
	 * Build a comprehension set.
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
	 * Build a comprehension set with a single bound identifier.
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
	 * Build an integer literal.
	 *
	 * @param i value of the literal
	 * @return integer literal with the given value
	 */
	public IntegerLiteral intLit(BigInteger i) {
		return ff.makeIntegerLiteral(i, null);
	}

	/**
	 * Build a bijection.
	 *
	 * @param left  left-hand side of the expression
	 * @param right right-hand side of the expression
	 * @return the bijection left ⤖ right
	 */
	public Expression bij(Expression left, Expression right) {
		return ff.makeBinaryExpression(TBIJ, left, right, null);
	}

	/**
	 * Build an interval set.
	 *
	 * @param left  left-hand side of the expression
	 * @param right right-hand side of the expression
	 * @return the set left..right
	 */
	public Expression upto(Expression left, Expression right) {
		return ff.makeBinaryExpression(UPTO, left, right, null);
	}

	// Predicates

	/**
	 * Build a conjunction of predicates.
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
	 * Build a set membership predicate.
	 *
	 * @param element element to test
	 * @param set set on which membership is tested
	 * @return the predicate element ∈ set
	 */
	public Predicate in(Expression element, Expression set) {
		return ff.makeRelationalPredicate(IN, element, set, null);
	}

	/**
	 * Build a logical implication.
	 *
	 * @param left left-hand side of the implication
	 * @param right right-hand side of the implication
	 * @return the predicate left ⇒ right
	 */
	public Predicate imp(Predicate left, Predicate right) {
		return ff.makeBinaryPredicate(LIMP, left, right, null);
	}

	/**
	 * Build a disjunction of predicates.
	 *
	 * @param preds predicates to conjunct
	 * @return disjunction of the given predicates
	 */
	public Predicate or(Predicate... preds) {
		return ff.makeAssociativePredicate(LOR, preds, null);
	}

}
