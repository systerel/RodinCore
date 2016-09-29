/*******************************************************************************
 * Copyright (c) 2014, 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.extension;

import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.FUNIMAGE;
import static org.eventb.core.ast.Formula.MAPSTO;
import static org.eventb.core.ast.Formula.TRUE;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;

/**
 * Common implementation of a translator for an extension instance. Instances
 * are uniquely identified by their signature.
 * 
 * @author Thomas Muller
 */
public abstract class ExtensionTranslator {

	protected final FreeIdentifier function;
	protected final FormulaFactory factory;

	public ExtensionTranslator(FreeIdentifier function) {
		this.function = function;
		this.factory = function.getFactory();
	}

	protected Expression makeFunApp(Expression[] newChildExprs,
			Predicate[] newChildPreds) {
		Expression param = null;
		for (final Expression expr : newChildExprs) {
			param = join(param, expr);
		}
		for (final Predicate pred : newChildPreds) {
			param = join(param, makeExprOfPred(pred));
		}
		if (param == null) {
			// Atomic extension
			return function;
		}
		return factory.makeBinaryExpression(FUNIMAGE, function, param, null);

	}

	private Expression makeExprOfPred(Predicate pred) {
		if (pred.getTag() == Formula.EQUAL) {
			final RelationalPredicate relPred = (RelationalPredicate) pred;
			if (relPred.getRight().getTag() == Formula.TRUE) {
				return relPred.getLeft();
			}
		}
		return factory.makeBoolExpression(pred, null);
	}

	/*
	 * Joins the given expressions with a maplet, unless the first is null.
	 */
	private Expression join(Expression left, Expression right) {
		if (left == null) {
			return right;
		}
		return factory.makeBinaryExpression(MAPSTO, left, right, null);
	}

	public static class PredicateExtTranslator extends
			ExtensionTranslator {

		private final Expression btrue;

		public PredicateExtTranslator(FreeIdentifier function) {
			super(function);
			this.btrue = factory.makeAtomicExpression(TRUE, null);
		}

		public Predicate translate(Expression[] newChildExprs,
				Predicate[] newChildPreds) {
			return makePredOfExpr(makeFunApp(newChildExprs, newChildPreds));
		}

		private Predicate makePredOfExpr(Expression expr) {
			return factory.makeRelationalPredicate(EQUAL, expr, btrue, null);
		}

	}

	public static class ExpressionExtTranslator extends
			ExtensionTranslator {

		public ExpressionExtTranslator(FreeIdentifier function) {
			super(function);
		}

		public Expression translate(Expression[] newChildExprs,
				Predicate[] newChildPreds) {
			return makeFunApp(newChildExprs, newChildPreds);
		}

	}

	/**
	 * Translator for a parametric type. The translation class has allocated a
	 * given type that corresponds to the parametric type instance and we just
	 * return this given type (there are no type polymorphism in plain Event-B).
	 */
	public static class TypeExtTranslator extends ExtensionTranslator {

		private final Type type;

		public TypeExtTranslator(FreeIdentifier typeName) {
			super(typeName);
			this.type = typeName.getType().getBaseType();
		}

		public Type translate() {
			return type;
		}

	}

}
