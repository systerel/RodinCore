/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast;

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
 * Default implementation of a type-checking rewriter that does not perform any
 * rewrite. In this class, we do not use the <code>checkReplacement()</code>
 * methods because the result bears the same type by construction. However,
 * these methods must be used by sub-classes that actually perform a rewrite
 * when it cannot be proven that the rewrite is always type-checked.
 * 
 * <p>
 * This implementation guarantees that a leaf node is rebuilt with the rewriter
 * factory if the factory of the node is different as required by
 * {@link ITypeCheckingRewriter}. Consequently, when extending this class,
 * always call the super method, rather than returning the formula unchanged.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class DefaultTypeCheckingRewriter implements ITypeCheckingRewriter {

	public static BoundIdentDecl checkReplacement(BoundIdentDecl src,
			BoundIdentDecl dst) {
		if (src != dst) {
			final Type type = src.getType();
			if (type != null && !type.equals(dst.getType()))
				throw new IllegalArgumentException(
						"Incompatible types in rewrite");
		}
		return dst;
	}

	public static Expression checkReplacement(Expression src, Expression dst) {
		if (src != dst) {
			final Type type = src.getType();
			if (type != null && !type.equals(dst.getType()))
				throw new IllegalArgumentException(
						"Incompatible types in rewrite");
		}
		return dst;
	}

	public static Predicate checkReplacement(Predicate src, Predicate dst) {
		if (src != dst) {
			if (src.isTypeChecked() && !dst.isTypeChecked())
				throw new IllegalArgumentException(
						"Incompatible types in rewrite");
		}
		return dst;
	}

	protected final FormulaFactory ff;
	protected final TypeRewriter typeRewriter;	
	private int bindingDepth;

	public DefaultTypeCheckingRewriter(FormulaFactory ff) {
		this.ff = ff;
		this.typeRewriter = new TypeRewriter(ff);
	}

	@Override
	public boolean autoFlatteningMode() {
		return false;
	}

	@Override
	public FormulaFactory getFactory() {
		return ff;
	}
	
	@Override
	public final void enteringQuantifier(int nbOfDeclarations) {
		bindingDepth += nbOfDeclarations;
	}

	/**
	 * Returns the number of bound identifier declarations between the current
	 * position and the root of the formula which is rewritten.
	 * 
	 * @return the number of bound identifier declarations from the root
	 */
	protected final int getBindingDepth() {
		return bindingDepth;
	}

	@Override
	public final void leavingQuantifier(int nbOfDeclarations) {
		bindingDepth -= nbOfDeclarations;
	}

	@Override
	public BoundIdentDecl rewrite(BoundIdentDecl src) {
		if (ff == src.getFactory()) {
			return src;
		}
		return ff.makeBoundIdentDecl(src.getName(), src.getSourceLocation(),
				typeRewriter.rewrite(src.getType()));
	}

	@Override
	public Expression rewrite(AssociativeExpression src,
			AssociativeExpression expr) {
		return expr;
	}

	@Override
	public Predicate rewrite(AssociativePredicate src, AssociativePredicate pred) {
		return pred;
	}

	@Override
	public Expression rewrite(AtomicExpression src) {
		if (ff == src.getFactory()) {
			return src;
		}
		return ff.makeAtomicExpression(src.getTag(), src.getSourceLocation(),
				typeRewriter.rewrite(src.getType()));
	}

	@Override
	public Expression rewrite(BinaryExpression src, BinaryExpression expr) {
		return expr;
	}

	@Override
	public Predicate rewrite(BinaryPredicate src, BinaryPredicate pred) {
		return pred;
	}

	@Override
	public Expression rewrite(BoolExpression src, BoolExpression expr) {
		return expr;
	}

	@Override
	public Expression rewrite(BoundIdentifier src) {
		if (ff == src.getFactory()) {
			return src;
		}
		return ff.makeBoundIdentifier(src.getBoundIndex(),
				src.getSourceLocation(), typeRewriter.rewrite(src.getType()));
	}

	@Override
	public Expression rewrite(ExtendedExpression src, boolean changed,
			Expression[] newChildExprs, Predicate[] newChildPreds) {
		if (!changed && ff == src.getFactory()) {
			return src;
		}
		return ff.makeExtendedExpression(src.getExtension(), newChildExprs,
				newChildPreds, src.getSourceLocation(),
				typeRewriter.rewrite(src.getType()));
	}

	@Override
	public Predicate rewrite(ExtendedPredicate src, boolean changed,
			Expression[] newChildExprs, Predicate[] newChildPreds) {
		if (!changed && ff == src.getFactory()) {
			return src;
		}
		return ff.makeExtendedPredicate(src.getExtension(), newChildExprs,
				newChildPreds, src.getSourceLocation());
	}

	@Override
	public Expression rewrite(FreeIdentifier src) {
		if (ff == src.getFactory()) {
			return src;
		}
		return ff.makeFreeIdentifier(src.getName(), src.getSourceLocation(),
				typeRewriter.rewrite(src.getType()));
	}

	@Override
	public Expression rewrite(IntegerLiteral src) {
		if (ff == src.getFactory()) {
			return src;
		}
		return ff.makeIntegerLiteral(src.getValue(), src.getSourceLocation());
	}

	@Override
	public Predicate rewrite(LiteralPredicate src) {
		if (ff == src.getFactory()) {
			return src;
		}
		return ff.makeLiteralPredicate(src.getTag(), src.getSourceLocation());
	}

	@Override
	public Predicate rewrite(MultiplePredicate src, MultiplePredicate pred) {
		return pred;
	}

	@Override
	public Predicate rewrite(PredicateVariable src) {
		if (ff == src.getFactory()) {
			return src;
		}
		return ff.makePredicateVariable(src.getName(), src.getSourceLocation());
	}

	@Override
	public Expression rewrite(QuantifiedExpression src,
			QuantifiedExpression expr) {
		return expr;
	}

	@Override
	public Predicate rewrite(QuantifiedPredicate src, QuantifiedPredicate pred) {
		return pred;
	}

	@Override
	public Predicate rewrite(RelationalPredicate src, RelationalPredicate pred) {
		return pred;
	}

	@Override
	public Expression rewrite(SetExtension src, SetExtension expr) {
		if (ff == expr.getFactory()) {
			return expr;
		}
		if (expr.getMembers().length == 0) {
			return ff.makeEmptySetExtension(
					typeRewriter.rewrite(expr.getType()),
					expr.getSourceLocation());
		}
		return ff.makeSetExtension(expr.getMembers(), expr.getSourceLocation());
	}

	@Override
	public Expression rewriteToEmptySet(SetExtension src) {
		final Type type = typeRewriter.rewrite(src.getType());
		return ff.makeEmptySet(type, src.getSourceLocation());
	}

	@Override
	public Predicate rewrite(SimplePredicate src, SimplePredicate pred) {
		return pred;
	}

	@Override
	public Expression rewrite(UnaryExpression src, boolean changed,
			Expression newChild) {
		if (!changed) {
			assert ff == src.getFactory();
			return src;
		}
		return ff.makeUnaryExpression(src.getTag(), newChild,
				src.getSourceLocation());
	}

	@Override
	public Expression rewrite(UnaryExpression src, IntegerLiteral expr) {
		return expr;
	}

	@Override
	public Predicate rewrite(UnaryPredicate src, UnaryPredicate pred) {
		return pred;
	}

}
