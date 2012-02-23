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
package org.eventb.internal.pptrans.translator;

import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.MAPSTO;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;

/**
 * Implement the decomposition of an expression that contains maplet hiding into
 * a new expression without maplet hiding. The decomposition is done by
 * introducing a quantifier.
 * <p>
 * The protocol for using this class is decomposed in two phases: a recording
 * phase followed by a processing phase. The phase change is triggered by
 * calling <code>startPhase2</code>.
 * </p>
 * <p>
 * During the recording phase, one need only call method
 * <code>decompose()</code>. Method <code>push()</code> can also be called but
 * does nothing.
 * </p>
 * <p>
 * During the processing phase, method <code>decompose()</code> must be called
 * with the exact same arguments as during the recording phase. In addition,
 * method <code>push()</code> shall be called for all expressions that occur in
 * the predicate passed to <code>bind()</code> and must not change. Finally,
 * method <code>bind()</code> must be called last to get the resulting
 * quantified predicate.
 * </p>
 * <p>
 * Hence, the expected protocol for this class is
 * <ul>
 * <li><code>decompose()</code> and <code>push</code> freely mixed,
 * <li><code>needsBinding()</code>,
 * <li><code>startPhase2()</code>,
 * <li><code>decompose()</code> and <code>push</code> freely mixed,
 * <li><code>bind()</code>.
 * </ul>
 * </p>
 * 
 * @author Laurent Voisin
 */
public class MapletDecomposer extends Decomp2PhaseQuant {

	private final List<Predicate> bindings = new ArrayList<Predicate>();

	public MapletDecomposer(FormulaFactory ff) {
		super(ff);
	}

	/**
	 * Decompose the given expression.
	 * 
	 * @param expr
	 *            some expression of Cartesian product type
	 * @return a new expression that does not contain any maplet hiding
	 */
	public Expression decompose(Expression expr) {
		switch (expr.getTag()) {
		case MAPSTO:
			return decomposeMaplet((BinaryExpression) expr);
		default:
			return decomposeNonMaplet(expr);
		}
	}

	private Expression decomposeMaplet(BinaryExpression expr) {
		final Expression left = expr.getLeft();
		final Expression right = expr.getRight();
		final Expression newLeft = decompose(left);
		final Expression newRight = decompose(right);
		if (recording || newLeft == left && newRight == right) {
			return expr;
		}
		final SourceLocation loc = expr.getSourceLocation();
		return ff.makeBinaryExpression(MAPSTO, newLeft, newRight, loc);
	}

	private Expression decomposeNonMaplet(Expression expr) {
		final Expression newExpr = push(expr);
		final Type type = expr.getType();
		if (!(type instanceof ProductType)) {
			return newExpr;
		}
		final Expression boundExpr = addQuantifier(type, null);
		if (!recording) {
			final Predicate binding = ff.makeRelationalPredicate(EQUAL,
					boundExpr, newExpr, null);
			bindings.add(binding);
		}
		return boundExpr;
	}

	/**
	 * Tells whether any expression has been decomposed so far.
	 * 
	 * @return <code>true</code> if and only if some expression has been
	 *         decomposed
	 */
	public boolean needsDecomposition() {
		return offset() != 0;
	}

	/**
	 * Binds the given predicate after adding equality predicates for the bound
	 * identifiers created by previous calls to {@link #decompose(Expression)}.
	 * 
	 * @param pred
	 *            predicate to bind
	 * @return a quantified predicate or the exact same predicate if no bound
	 *         identifier was created.
	 */
	public Predicate bind(Predicate pred) {
		assert !recording;
		if (offset() == 0) {
			return pred;
		}
		final Predicate boundPredicate = addBindings(pred);
		return makeQuantifiedPredicate(FORALL, boundPredicate, null);
	}

	private Predicate addBindings(Predicate pred) {
		final Predicate lhs;
		switch (bindings.size()) {
		case 0:
			throw new IllegalStateException("shall have some bindings");
		case 1:
			lhs = bindings.get(0);
			break;
		default:
			lhs = ff.makeAssociativePredicate(LAND, bindings, null);
		}
		return ff.makeBinaryPredicate(LIMP, lhs, pred, null);
	}

}
