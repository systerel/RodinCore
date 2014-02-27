/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pptrans.translator;

import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.MAPSTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedExpression.Form;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;

/**
 * Implements decomposition of bound identifiers of Cartesian product type. The
 * decomposition is implemented as a formula rewriter. For each quantified
 * expression or predicate, we replace it with an equivalent one where all bound
 * identifier declarations have been decomposed.
 * 
 * @author Laurent Voisin
 */
public class BoundIdentifierDecomposer extends DefaultRewriter {

	// Internal state used during rewrite
	private FormulaFactory fac;
	private int count;
	private List<BoundIdentDecl> newDecls;
	private Expression[] replacements;

	public BoundIdentifierDecomposer() {
		super(false); // no flattening
	}

	@Override
	public Predicate rewrite(QuantifiedPredicate predicate) {
		final BoundIdentDecl[] decls = predicate.getBoundIdentDecls();
		if (!needsDecomposition(decls)) {
			return predicate;
		}
		fac = predicate.getFactory();
		final int tag = predicate.getTag();
		computeSubstitution(decls);
		final Predicate instPred = instantiate(predicate);
		return fac.makeQuantifiedPredicate(tag, newDecls, instPred, null);
	}

	@Override
	public Expression rewrite(QuantifiedExpression qexpr) {
		final BoundIdentDecl[] decls = qexpr.getBoundIdentDecls();
		if (!needsDecomposition(decls)) {
			return qexpr;
		}
		fac = qexpr.getFactory();
		final int tag = qexpr.getTag();
		final Form form = qexpr.getForm();
		computeSubstitution(decls);
		final Predicate instPred = instantiate(decls, qexpr.getPredicate());
		final Expression instExpr = instantiate(decls, qexpr.getExpression());
		return fac.makeQuantifiedExpression(tag, newDecls, instPred, instExpr,
				null, form);
	}

	private boolean needsDecomposition(BoundIdentDecl[] decls) {
		for (final BoundIdentDecl decl : decls) {
			if (needsDecomposition(decl.getType())) {
				return true;
			}
		}
		return false;
	}

	private boolean needsDecomposition(final Type type) {
		return type instanceof ProductType;
	}

	/**
	 * We compute the substitutes from right to left, because the bound
	 * identifiers are numbered in this direction.
	 */
	private void computeSubstitution(BoundIdentDecl[] decls) {
		count = 0;
		newDecls = new ArrayList<BoundIdentDecl>();
		replacements = new Expression[decls.length];
		for (int i = decls.length - 1; i >= 0; i--) {
			replacements[i] = decompose(decls[i]);
		}
		Collections.reverse(newDecls);
	}

	private Expression decompose(BoundIdentDecl decl) {
		final Type type = decl.getType();
		if (needsDecomposition(type)) {
			return decompose(decl.getName() + "_1", type);
		}
		return addNewDecl(decl);
	}

	private Expression decompose(String prefix, Type type) {
		if (!needsDecomposition(type)) {
			return addNewDecl(fac.makeBoundIdentDecl(prefix, null, type));
		}
		final ProductType pType = (ProductType) type;
		final Expression right = decompose(prefix, pType.getRight());
		final Expression left = decompose(prefix, pType.getLeft());
		return fac.makeBinaryExpression(MAPSTO, left, right, null);
	}

	private BoundIdentifier addNewDecl(BoundIdentDecl decl) {
		newDecls.add(decl);
		return fac.makeBoundIdentifier(count++, null, decl.getType());
	}

	private Predicate instantiate(QuantifiedPredicate predicate) {
		final QuantifiedPredicate inner;
		inner = (QuantifiedPredicate) predicate.shiftBoundIdentifiers(count);
		return inner.instantiate(replacements, fac);
	}

	private Predicate instantiate(BoundIdentDecl[] decls, Predicate predicate) {
		return instantiate(mExists(decls, predicate));
	}

	private QuantifiedPredicate mExists(BoundIdentDecl[] decls,
			Predicate predicate) {
		return fac.makeQuantifiedPredicate(EXISTS, decls, predicate, null);
	}

	/*
	 * There is no service for instantiating an expression, therefore we
	 * encapsulate it into an arbitrary quantified predicate.
	 */
	private Expression instantiate(BoundIdentDecl[] decls, Expression expr) {
		final Predicate pred = fac.makeSimplePredicate(KFINITE, expr, null);
		final Predicate instPred = instantiate(decls, pred);
		assert instPred.getTag() == KFINITE;
		return ((SimplePredicate) instPred).getExpression();
	}

}
