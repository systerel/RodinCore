/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.internal.core.seqprover.eventbExtensions.OnePointFilterUtils.*;

import java.util.List;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.internal.core.seqprover.eventbExtensions.OnePointFilterUtils.Replacement;
import org.eventb.internal.core.seqprover.eventbExtensions.OnePointFilterUtils.ToProcessStruct;

public class OnePointUtils {

	public static Replacement findReplacement(List<Predicate> conjuncts,
			BoundIdentDecl[] boundIdentDecls, Predicate replacementPredicate) {
		for (int i = 0; i < conjuncts.size(); i++) {
			final Predicate pred = conjuncts.get(i);
			final Replacement replacement = getReplacement(pred,
					boundIdentDecls, replacementPredicate, i);
			if (replacement != null)
				return replacement;
		}
		return null;
	}

	public static void removeConjunct(List<Predicate> conjuncts,
			int replacementID) {
		conjuncts.remove(replacementID);
	}

	public static Predicate processReplacement(int tag, ToProcessStruct struct,
			Replacement replacement, FormulaFactory ff) {
		final Predicate innerPred;
		struct.conjuncts.remove(replacement.getReplacementIndex());
		if (struct.impRight == null) { // simple conjunctive form
			innerPred = makeConjunction(struct.conjuncts, ff);
		} else { // imply conjunctive form
			innerPred = makeInnerPred(struct.conjuncts, struct.impRight, ff);
		}
		if (innerPred == null) {
			return Lib.True;
		} else {
			return makePredicateWithReplacement(tag, replacement,
					struct.identDecls, innerPred, ff);
		}
	}

	private static Predicate makePredicateWithReplacement(int tag,
			Replacement replacement, BoundIdentDecl[] identDecls,
			Predicate innerPred, FormulaFactory ff) {
		final QuantifiedPredicate qPred = ff.makeQuantifiedPredicate(tag,
				identDecls, innerPred, null);
		return applyTo(replacement.getReplacement(), replacement
				.getBoundIdent(), qPred, ff);
	}

	private static Predicate makeConjunction(List<Predicate> conjuncts,
			FormulaFactory ff) {
		// final Predicate left;
		switch (conjuncts.size()) {
		case 0:
			return null;
		case 1:
			return conjuncts.iterator().next();
		default:
			return ff.makeAssociativePredicate(Predicate.LAND, conjuncts, null);
		}
	}

	private static Predicate makeInnerPred(List<Predicate> conjuncts,
			Predicate impRight, FormulaFactory ff) {
		Predicate left = null;
		left = makeConjunction(conjuncts, ff);
		final Predicate innerPred;
		if (left == null) {
			innerPred = impRight;
		} else {
			innerPred = ff.makeBinaryPredicate(Predicate.LIMP, left, impRight,
					null);
		}
		return innerPred;
	}

	public static Predicate applyTo(Expression replacement,
			BoundIdentifier boundIdent, QuantifiedPredicate predicate,
			FormulaFactory ff) {
		OnePointInstantiator ops = new OnePointInstantiator(predicate,
				boundIdent, replacement, ff);
		return ops.instantiate();
	}

	public static boolean checkReplacement(Predicate replPred,
			BoundIdentifier boundIdent, Expression expression,
			Predicate basePred, Predicate replacementPredicate) {
		if (!(basePred instanceof QuantifiedPredicate)) {
			return false;
		}
		BoundIdentDecl[] identDecls = ((QuantifiedPredicate) basePred)
				.getBoundIdentDecls();
		return (Replacement.isValid(boundIdent, expression, identDecls) && (replPred == null || replPred
				.equals(replacementPredicate)));
	}
}
