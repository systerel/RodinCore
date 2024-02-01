/*******************************************************************************
 * Copyright (c) 2024 UPEC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     UPEC - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.ast.Formula.BOUND_IDENT;
import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.MAPSTO;
import static org.eventb.core.ast.QuantifiedExpression.Form.Implicit;
import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeProofRule;
import static org.eventb.core.seqprover.ProverFactory.reasonerFailure;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;
import org.eventb.internal.core.seqprover.eventbExtensions.utils.FormulaBuilder;

/**
 * Implementation of an inference reasoner on finiteness of set comprehension.
 *
 * The reasoner matches a goal like <code>finite({ x · P(X) ∣ F(x) })</code> and
 * replaces it with <code>finite({ x ∣ P(X) })</code>.
 *
 * @author Guillaume Verdier
 */
public class FiniteCompset extends EmptyInputReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".finiteCompset";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	private static QuantifiedExpression getCompsetIfApplicable(Predicate predicate) {
		if (predicate.getTag() != KFINITE) {
			return null;
		}
		Expression finExpr = ((SimplePredicate) predicate).getExpression();
		if (finExpr.getTag() != CSET) {
			return null;
		}
		var cset = (QuantifiedExpression) finExpr;
		if (expressionIsAllBoundIdents(cset.getExpression(), cset.getBoundIdentDecls().length)) {
			return null;
		}
		return cset;
	}

	/*
	 * There is no point in applying the rewrite to a set which expression is just a
	 * maplet of its bound identifiers. It is not much more useful to apply it if
	 * the result is just a reordering of the maplet (e.g., if the expression was
	 * something like b↦a↦c and is rewritten into a↦b↦c). These cases are detected
	 * here and the reasoner is not applicable to them. If a bound identifier is
	 * absent or appears several times, then the rewriter is enabled.
	 */
	private static boolean expressionIsAllBoundIdents(Expression e, int nBoundIdents) {
		var boundIdentsFound = new boolean[nBoundIdents];
		if (!traverse(e, boundIdentsFound)) {
			return false;
		}
		for (boolean found : boundIdentsFound) {
			if (!found) {
				return false;
			}
		}
		return true;
	}

	// Find bound identifiers in nested maplets; rejects everything else, as well as
	// bound identifiers that are outside the scope and those already seen
	private static boolean traverse(Expression e, boolean[] boundIdentsFound) {
		if (e.getTag() == BOUND_IDENT) {
			int index = ((BoundIdentifier) e).getBoundIndex();
			if (index >= boundIdentsFound.length) {
				// Reference to an identifier outside of the set
				return false;
			}
			if (boundIdentsFound[index]) {
				// Same identifier seen twice
				return false;
			}
			boundIdentsFound[index] = true;
			return true;
		} else if (e.getTag() == MAPSTO) {
			var binExpr = (BinaryExpression) e;
			return traverse(binExpr.getLeft(), boundIdentsFound) && traverse(binExpr.getRight(), boundIdentsFound);
		} else {
			return false;
		}
	}

	/**
	 * Test whether the reasoner is applicable at the given predicate's root.
	 *
	 * @param predicate predicate to check
	 * @return whether the reasoner is applicable
	 */
	public static boolean isApplicable(Predicate predicate) {
		return getCompsetIfApplicable(predicate) != null;
	}

	@ProverRule("FIN_COMPSET_R")
	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProofMonitor pm) {
		Predicate goal = seq.goal();
		var cset = getCompsetIfApplicable(goal);
		if (cset == null) {
			return reasonerFailure(this, input, "Inference 'finite of set comprehension' is not applicable");
		}
		BoundIdentDecl[] decls = cset.getBoundIdentDecls();
		FormulaBuilder fb = new FormulaBuilder(seq.getFormulaFactory());
		Expression newExpr = fb.boundIdent(0, decls[0].getType());
		for (int i = 1; i < decls.length; i++) {
			newExpr = fb.mapsTo(fb.boundIdent(i, decls[i].getType()), newExpr);
		}
		Predicate newGoal = fb.finite(fb.cset(decls, cset.getPredicate(), newExpr, Implicit));
		return makeProofRule(this, input, goal, "finite of set comprehension", makeAntecedent(newGoal));
	}

}
