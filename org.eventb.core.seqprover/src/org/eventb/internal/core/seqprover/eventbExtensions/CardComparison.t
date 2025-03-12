/*******************************************************************************
 * Copyright (c) 2008, 2025 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language V2
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.SUBSET;
import static org.eventb.core.ast.Formula.SUBSETEQ;
import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeProofRule;
import static org.eventb.core.seqprover.ProverFactory.reasonerFailure;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

/**
 * Basic implementation for comparison of cardinalities
 */
@SuppressWarnings({"unused", "cast"})
public class CardComparison extends EmptyInputReasoner {

	%include {FormulaV2.tom}
	
	@Override
	public String getReasonerID() {
		return SequentProver.PLUGIN_ID + ".cardComparison";
	}
	
	private static final List<IPosition> ROOT_POS = Collections
			.singletonList(IPosition.ROOT);

	private static final List<IPosition> NO_POS = Collections.emptyList();

	public static List<IPosition> getRootPositions(Predicate goal) {
		if (isApplicable(goal)) {
			return ROOT_POS;
		}
		return NO_POS;
	}

	public static boolean isApplicable(Predicate predicate) {
	    %match (Predicate predicate) {
			
			/**
	    	 * Set Theory: card(S) = card(T), where S and T have the same type.
	    	 * Set Theory: card(S) ≤ card(T), where S and T have the same type.
	    	 * Set Theory: card(S) < card(T), where S and T have the same type.
	    	 * Set Theory: card(S) ≥ card(T), where S and T have the same type.
	    	 * Set Theory: card(S) > card(T), where S and T have the same type.
	    	 */
			(Equal|Le|Lt|Ge|Gt)(Card(S), Card(T)) -> {
				return haveSameType(`S, `T);
			}

	    }
	    return false;
	}

	private static boolean haveSameType(Expression left, Expression right) {
		return left.getType().equals(right.getType());
	}

	@ProverRule( { "DERIV_EQUAL_CARD", "DERIV_LE_CARD", "DERIV_LT_CARD",
			"DERIV_GE_CARD", "DERIV_GT_CARD" })
	protected IAntecedent[] getAntecedents(IProverSequent seq) {
		final Predicate goal = seq.goal();
		
		// position must be an applicable position
		if (! isApplicable(goal))
			return null;
		
		final FormulaFactory ff = seq.getFormulaFactory();
		
	    %match (Predicate goal) {

			/**
	    	 * Set Theory: card(S) = card(T)
	    	 */
			Equal(Card(S), Card(T)) -> {
				if (haveSameType(`S, `T))
					return makeAntecedents(EQUAL, `S, `T, ff);
			}

			/**
	    	 * Set Theory: card(S) ≤ card(T)
	    	 */
			Le(Card(S), Card(T)) -> {
				if (haveSameType(`S, `T))
					return makeAntecedents(SUBSETEQ, `S, `T, ff);
			}

			/**
	    	 * Set Theory: card(S) < card(T)
	    	 */
			Lt(Card(S), Card(T)) -> {
				if (haveSameType(`S, `T))
					return makeAntecedents(SUBSET, `S, `T, ff);
			}

			/**
	    	 * Set Theory: card(S) ≥ card(T)
	    	 */
			Ge(Card(S), Card(T)) -> {
				if (haveSameType(`S, `T))
					return makeAntecedents(SUBSETEQ, `T, `S, ff);
			}

			/**
	    	 * Set Theory: card(S) > card(T)
	    	 */
			Gt(Card(S), Card(T)) -> {
				if (haveSameType(`S, `T))
					return makeAntecedents(SUBSET, `T, `S, ff);
			}
			
	    }
		return null;
	}

	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProofMonitor pm) {
		IAntecedent[] antecedents = getAntecedents(seq);
		if (antecedents == null) {
			return reasonerFailure(this, input, "Inference " + getReasonerID() + " is not applicable");
		}
		return makeProofRule(this, input, seq.goal(), (Predicate) null, "card. comparison", antecedents);
	}
	
	private IAntecedent[] makeAntecedents(int tag, Expression l, Expression r, FormulaFactory ff) {
		final Predicate newPred = ff.makeRelationalPredicate(tag, l, r, null);
		return new IAntecedent[] { makeAntecedent(newPred) };
	}

}
