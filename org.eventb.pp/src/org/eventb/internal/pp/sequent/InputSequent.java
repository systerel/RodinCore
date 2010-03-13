/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.sequent;

import static org.eventb.core.ast.Formula.MAPSTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;
import org.eventb.internal.pp.CancellationChecker;

/**
 * Implementation of a sequent passed as input to a call to the predicate
 * prover. This class takes care of translating the sequent to predicate
 * calculus, after normalization to remove all variables of Cartesian product
 * type (thus hiding a pair) which are not supported by PP core.
 * 
 * @author Laurent Voisin
 */
public class InputSequent {

	// Formula factory to be used for translation
	private final FormulaFactory ff;

	private final CancellationChecker cancellation;

	// predicates of this sequent
	private final InputPredicate[] predicates;

	// Type environment of this sequent
	private final ITypeEnvironment typenv;

	// Variables of a Cartesian product type (thus hiding a pair)
	private final Set<FreeIdentifier> variablesToNormalize;

	// Substitution of variables hiding a pair into scalar variables.
	private final Map<FreeIdentifier, Expression> substitution;

	public InputSequent(Predicate[] hypotheses, Predicate goal,
			FormulaFactory ff, CancellationChecker cancellation) {
		this(Arrays.asList(hypotheses), goal, ff, cancellation);
	}

	public InputSequent(Iterable<Predicate> hypotheses, Predicate goal,
			FormulaFactory ff, CancellationChecker cancellation) {
		final List<InputPredicate> list = new ArrayList<InputPredicate>();
		for (final Predicate hyp : hypotheses) {
			list.add(new InputPredicate(hyp, false));
		}
		list.add(new InputPredicate(goal, true));
		this.predicates = list.toArray(new InputPredicate[list.size()]);
		this.typenv = ff.makeTypeEnvironment();
		this.variablesToNormalize = new LinkedHashSet<FreeIdentifier>();
		this.substitution = new HashMap<FreeIdentifier, Expression>();
		this.ff = ff;
		this.cancellation = cancellation;
	}

	/**
	 * Normalizes this sequent by replacing all variables of a Cartesian product
	 * type by pairs of scalar variables. After normalization all variables of
	 * this sequent are either non-pair scalars or sets.
	 */
	private void normalize() {
		computeTypeEnvironment();
		cancellation.check();
		computeSubstitution();
		cancellation.check();
		applySubstitution();
	}

	private void computeTypeEnvironment() {
		for (final InputPredicate ipred : predicates) {
			processIdents(ipred.freeIdentifiers());
			cancellation.check();
		}
	}

	private void processIdents(FreeIdentifier[] idents) {
		for (final FreeIdentifier ident : idents) {
			if (ident.getType() instanceof ProductType) {
				variablesToNormalize.add(ident);
			} else {
				typenv.add(ident);
			}
		}
	}

	private void computeSubstitution() {
		for (FreeIdentifier ident : variablesToNormalize) {
			final String prefix = ident.getName() + "_1";
			substitution.put(ident, getSubstitute(prefix, ident.getType()));
		}
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
		return ff.makeFreshIdentifiers(bids, typenv)[0];
	}

	private void applySubstitution() {
		for (final InputPredicate ipred : predicates) {
			ipred.normalize(substitution);
			cancellation.check();
		}
	}

	/**
	 * Translates this sequent to predicate calculus.
	 */
	public void translate() {
		normalize();
		cancellation.check();
		for (final InputPredicate ipred : predicates) {
			ipred.translate();
			cancellation.check();
		}
	}

	public ITypeEnvironment typeEnvironment() {
		return typenv;
	}

	@Deprecated
	public List<Predicate> getTranslatedHypotheses() {
		final int len = predicates.length - 1;
		final Predicate[] result = new Predicate[len];
		for (int i = 0; i < len; i++) {
			result[i] = predicates[i].translatedPredicate();
		}
		return Arrays.asList(result);
	}

	@Deprecated
	public Predicate getTranslatedGoal() {
		final int idx = predicates.length - 1;
		return predicates[idx].translatedPredicate();
	}

	public InputPredicate[] getPredicates() {
		return predicates;
	}

}
