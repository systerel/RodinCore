/*******************************************************************************
 * Copyright (c) 2011, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pptrans.translator;

import static org.eventb.core.ast.Formula.MAPSTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment.IIterator;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;
import org.eventb.core.seqprover.transformer.ISequentTransformer;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;

/**
 * New implementation of identifier decomposition implemented as a sequent
 * transformer.
 * <p>
 * The substitution for free identifiers of Cartesian product type (i.e.,
 * identifiers that hide a maplet) is pre-computed in the constructor. The bound
 * identifier decomposition is performed on the fly for each predicate.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class IdentifierDecomposer implements ISequentTransformer {

	private final FormulaFactory ff;

	// Type environment of the sequent to transform
	private final ITypeEnvironmentBuilder typenv;

	// Substitution of variables hiding a pair into scalar variables.
	private final Map<FreeIdentifier, Expression> substitution;

	public IdentifierDecomposer(ISimpleSequent sequent) {
		this.ff = sequent.getFormulaFactory();
		this.typenv = sequent.getTypeEnvironment().makeBuilder();
		this.substitution = new HashMap<FreeIdentifier, Expression>();
		computeSubstitution();
	}

	/*
	 * The substitution must be computed in two steps, as the type environment
	 * gets modified when computing the substitutes.
	 */
	private void computeSubstitution() {
		final List<FreeIdentifier> toDecompose = getMapletIdentifiers();
		for (final FreeIdentifier ident : toDecompose) {
			addSubstitution(ident);
		}
	}

	private List<FreeIdentifier> getMapletIdentifiers() {
		final List<FreeIdentifier> result = new ArrayList<FreeIdentifier>();
		final IIterator iter = typenv.getIterator();
		while (iter.hasNext()) {
			iter.advance();
			if (iter.getType() instanceof ProductType) {
				result.add(iter.asFreeIdentifier());
			}
		}
		return result;
	}

	private void addSubstitution(final FreeIdentifier ident) {
		final String prefix = ident.getName() + "_1";
		final Expression maplet = getSubstitute(prefix, ident.getType());
		substitution.put(ident, maplet);
	}

	private Expression getSubstitute(String prefix, Type type) {
		if (type instanceof ProductType) {
			final ProductType pType = (ProductType) type;
			final Expression left = getSubstitute(prefix, pType.getLeft());
			final Expression right = getSubstitute(prefix, pType.getRight());
			return ff.makeBinaryExpression(MAPSTO, left, right, null);
		}
		final BoundIdentDecl[] bids = new BoundIdentDecl[] { ff
				.makeBoundIdentDecl(prefix, null, type) };
		return typenv.makeFreshIdentifiers(bids)[0];
	}

	@Override
	public Predicate transform(ITrackedPredicate tpred) {
		Predicate pred = tpred.getPredicate();
		pred = pred.substituteFreeIdents(substitution);
		pred = pred.rewrite(new BoundIdentifierDecomposer());
		return pred;
	}

}
