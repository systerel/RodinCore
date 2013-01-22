/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast;

import static org.eventb.internal.core.ast.DefaultTypeCheckingRewriter.checkReplacement;

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
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IFormulaRewriter2;
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
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

/**
 * Perform a simple rewriting of formula where neither the language nor the
 * types change. This class encapsulates instances of
 * <code>IFormulaRewriter</code> provided by clients and ensures that the client
 * returns well-typed formulas.
 * 
 * @author Thomas Muller
 */
public class SameTypeRewriter implements ITypeCheckingRewriter {

	private final IFormulaRewriter rewriter;

	public SameTypeRewriter(IFormulaRewriter rewriter) {
		this.rewriter = rewriter;
	}

	@Override
	public boolean autoFlatteningMode() {
		return rewriter.autoFlatteningMode();
	}

	@Override
	public void enteringQuantifier(int nbOfDeclarations) {
		rewriter.enteringQuantifier(nbOfDeclarations);
	}

	@Override
	public FormulaFactory getFactory() {
		return rewriter.getFactory();
	}

	@Override
	public void leavingQuantifier(int nbOfDeclarations) {
		rewriter.leavingQuantifier(nbOfDeclarations);
	}

	@Override
	public Expression rewrite(AssociativeExpression src,
			AssociativeExpression expr) {
		return checkReplacement(src, rewriter.rewrite(expr));
	}

	@Override
	public Predicate rewrite(AssociativePredicate src, AssociativePredicate pred) {
		return checkReplacement(src, rewriter.rewrite(pred));
	}

	@Override
	public Expression rewrite(AtomicExpression src) {
		return checkReplacement(src, rewriter.rewrite(src));
	}

	@Override
	public Expression rewrite(BinaryExpression src, BinaryExpression expr) {
		return checkReplacement(src, rewriter.rewrite(expr));
	}

	@Override
	public Predicate rewrite(BinaryPredicate src, BinaryPredicate pred) {
		return checkReplacement(src, rewriter.rewrite(pred));
	}

	@Override
	public Expression rewrite(BoolExpression src, BoolExpression expr) {
		return checkReplacement(src, rewriter.rewrite(expr));
	}

	@Override
	public BoundIdentDecl rewrite(BoundIdentDecl decl) {
		// Bound identifier declarations are not rewritten
		return decl;
	}

	@Override
	public Expression rewrite(BoundIdentifier src) {
		return checkReplacement(src, rewriter.rewrite(src));
	}

	@Override
	public Expression rewrite(ExtendedExpression src, boolean changed,
			Expression[] newChildExprs, Predicate[] newChildPreds) {
		final ExtendedExpression expr;
		if (changed) {
			expr = getFactory().makeExtendedExpression(src.getExtension(),
					newChildExprs, newChildPreds, src.getSourceLocation(),
					src.getType());
		} else {
			expr = src;
		}
		return checkReplacement(src, rewriter.rewrite(expr));
	}

	@Override
	public Predicate rewrite(ExtendedPredicate src, boolean changed,
			Expression[] newChildExprs, Predicate[] newChildPreds) {
		final ExtendedPredicate pred;
		if (changed) {
			pred = getFactory().makeExtendedPredicate(src.getExtension(),
					newChildExprs, newChildPreds, src.getSourceLocation());
		} else {
			pred = src;
		}
		return checkReplacement(src, rewriter.rewrite(pred));
	}

	@Override
	public Expression rewrite(FreeIdentifier src) {
		return checkReplacement(src, rewriter.rewrite(src));
	}

	@Override
	public Expression rewrite(IntegerLiteral src) {
		return checkReplacement(src, rewriter.rewrite(src));

	}

	@Override
	public Predicate rewrite(LiteralPredicate src) {
		return checkReplacement(src, rewriter.rewrite(src));
	}

	@Override
	public Predicate rewrite(MultiplePredicate src, MultiplePredicate pred) {
		return checkReplacement(src, rewriter.rewrite(pred));
	}

	@Override
	public Predicate rewrite(PredicateVariable src) {
		if (!(rewriter instanceof IFormulaRewriter2)) {
			throw new IllegalArgumentException(
					"The given rewriter shall support predicate variables");
		}
		final IFormulaRewriter2 rewriter2 = (IFormulaRewriter2) rewriter;
		return checkReplacement(src, rewriter2.rewrite(src));
	}

	@Override
	public Expression rewrite(QuantifiedExpression src,
			QuantifiedExpression expr) {
		return checkReplacement(src, rewriter.rewrite(expr));
	}

	@Override
	public Predicate rewrite(QuantifiedPredicate src, QuantifiedPredicate pred) {
		return checkReplacement(src, rewriter.rewrite(pred));
	}

	@Override
	public Predicate rewrite(RelationalPredicate src, RelationalPredicate pred) {
		return checkReplacement(src, rewriter.rewrite(pred));
	}

	@Override
	public Expression rewrite(SetExtension src, SetExtension expr) {
		return checkReplacement(src, rewriter.rewrite(expr));
	}

	@Override
	public Expression rewrite(SetExtension src, AtomicExpression expr) {
		return checkReplacement(src, rewriter.rewrite(expr));
	}

	@Override
	public Predicate rewrite(SimplePredicate src, SimplePredicate pred) {
		return checkReplacement(src, rewriter.rewrite(pred));
	}

	@Override
	public Expression rewrite(UnaryExpression src, boolean changed,
			Expression newChild) {
		final UnaryExpression expr;
		if (changed) {
			expr = getFactory().makeUnaryExpression(src.getTag(), newChild,
					src.getSourceLocation());
		} else {
			expr = src;
		}
		return checkReplacement(src, rewriter.rewrite(expr));
	}

	@Override
	public Expression rewrite(UnaryExpression src, IntegerLiteral expr) {
		return checkReplacement(src, rewriter.rewrite(expr));
	}

	@Override
	public Predicate rewrite(UnaryPredicate src, UnaryPredicate pred) {
		return checkReplacement(src, rewriter.rewrite(pred));
	}

}
