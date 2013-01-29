/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IFormulaInspector;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

/**
 * Checks the consistency of the type-checked status of formulas. Call method
 * {@link #check(Formula)} to check some formula.
 * <p>
 * The check is implemented by traversing the AST of the formula, verifying on
 * each type-checked node that its children are also type-checked. We also check
 * that the types born by nodes are well-formed.
 * </p>
 * <p>
 * The traversal itself is implemented with a formula inspector.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class TypeCheckedChecker implements IFormulaInspector<Boolean> {

	/**
	 * Checks consistency of the type-checked status in the tree of the given
	 * formula. This is the entry point of this class.
	 * 
	 * @param formula
	 *            the formula to check
	 */
	public static void check(Formula<?> formula) {
		final TypeCheckedChecker checker = new TypeCheckedChecker();
		if (formula instanceof Assignment) {
			checkAssignment((Assignment) formula, checker);
		} else {
			checker.checkFormula(formula);
		}
	}

	/*
	 * Formula inspector does not support assignment, we start the traversal by
	 * hand.
	 */
	private static void checkAssignment(Assignment assign,
			TypeCheckedChecker checker) {
		if (assign.isTypeChecked()) {
			assertChildrenTypeChecked(assign);
		}
		final int childCount = assign.getChildCount();
		for (int i = 0; i < childCount; i++) {
			checker.checkFormula(assign.getChild(i));
		}
	}

	private void checkFormula(Formula<?> formula) {
		formula.inspect(this);
	}

	private static void assertConsistent(BoundIdentDecl decl) {
		final Type type = decl.getType();
		if (decl.isTypeChecked()) {
			assertNotNull(type);
			assertTrue(type.isSolved());
		} else {
			assertNull(type);
		}
	}

	private static void assertConsistent(Expression expr) {
		final Type type = expr.getType();
		if (expr.isTypeChecked()) {
			assertNotNull(type);
			assertTrue(type.isSolved());
			assertChildrenTypeChecked(expr);
		} else {
			assertNull(type);
		}
	}

	private static void assertConsistent(Predicate pred) {
		if (pred.isTypeChecked()) {
			assertChildrenTypeChecked(pred);
		}
	}

	private static void assertChildrenTypeChecked(Formula<?> formula) {
		final int childCount = formula.getChildCount();
		for (int i = 0; i < childCount; i++) {
			assertTrue(formula.getChild(i).isTypeChecked());
		}
	}

	@Override
	public void inspect(AssociativeExpression expression,
			IAccumulator<Boolean> accumulator) {
		assertConsistent(expression);
	}

	@Override
	public void inspect(AssociativePredicate predicate,
			IAccumulator<Boolean> accumulator) {
		assertConsistent(predicate);
	}

	@Override
	public void inspect(AtomicExpression expression,
			IAccumulator<Boolean> accumulator) {
		assertConsistent(expression);
	}

	@Override
	public void inspect(BinaryExpression expression,
			IAccumulator<Boolean> accumulator) {
		assertConsistent(expression);
	}

	@Override
	public void inspect(BinaryPredicate predicate,
			IAccumulator<Boolean> accumulator) {
		assertConsistent(predicate);
	}

	@Override
	public void inspect(BoolExpression expression,
			IAccumulator<Boolean> accumulator) {
		assertConsistent(expression);
	}

	@Override
	public void inspect(BoundIdentDecl decl, IAccumulator<Boolean> accumulator) {
		assertConsistent(decl);
	}

	@Override
	public void inspect(BoundIdentifier identifier,
			IAccumulator<Boolean> accumulator) {
		assertConsistent(identifier);
	}

	@Override
	public void inspect(ExtendedExpression expression,
			IAccumulator<Boolean> accumulator) {
		assertConsistent(expression);
	}

	@Override
	public void inspect(ExtendedPredicate predicate,
			IAccumulator<Boolean> accumulator) {
		assertConsistent(predicate);
	}

	@Override
	public void inspect(FreeIdentifier identifier,
			IAccumulator<Boolean> accumulator) {
		assertConsistent(identifier);
	}

	@Override
	public void inspect(IntegerLiteral literal,
			IAccumulator<Boolean> accumulator) {
		assertConsistent(literal);
	}

	@Override
	public void inspect(LiteralPredicate predicate,
			IAccumulator<Boolean> accumulator) {
		assertConsistent(predicate);
	}

	@Override
	public void inspect(MultiplePredicate predicate,
			IAccumulator<Boolean> accumulator) {
		assertConsistent(predicate);
	}

	@Override
	public void inspect(PredicateVariable predicate,
			IAccumulator<Boolean> accumulator) {
		assertConsistent(predicate);
	}

	@Override
	public void inspect(QuantifiedExpression expression,
			IAccumulator<Boolean> accumulator) {
		assertConsistent(expression);
	}

	@Override
	public void inspect(QuantifiedPredicate predicate,
			IAccumulator<Boolean> accumulator) {
		assertConsistent(predicate);
	}

	@Override
	public void inspect(RelationalPredicate predicate,
			IAccumulator<Boolean> accumulator) {
		assertConsistent(predicate);
	}

	@Override
	public void inspect(SetExtension expression,
			IAccumulator<Boolean> accumulator) {
		assertConsistent(expression);
	}

	@Override
	public void inspect(SimplePredicate predicate,
			IAccumulator<Boolean> accumulator) {
		assertConsistent(predicate);
	}

	@Override
	public void inspect(UnaryExpression expression,
			IAccumulator<Boolean> accumulator) {
		assertConsistent(expression);
	}

	@Override
	public void inspect(UnaryPredicate predicate,
			IAccumulator<Boolean> accumulator) {
		assertConsistent(predicate);
	}
}
