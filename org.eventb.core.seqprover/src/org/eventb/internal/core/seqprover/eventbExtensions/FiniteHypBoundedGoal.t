/*******************************************************************************
 * Copyright (c) 2009, 2022 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.eventb.core.ast.Formula.BTRUE;

import java.math.BigInteger;

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
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

/**
 * Implementation for "min or max goal with finite hyp"
 */
@SuppressWarnings({"unused", "cast"})
public class FiniteHypBoundedGoal extends EmptyInputReasoner {

	%include {FormulaV2.tom}
	
	@Override
	public String getReasonerID() {
		return SequentProver.PLUGIN_ID + ".finiteHypBoundedGoal";
	}
	
	private Expression getBoundSet(Predicate goal) {
	    %match (Predicate goal) {
			
			/**
	    	 * FIN_L_LOWER_BOUND_L
	    	 * Set Theory: ∃n·(∀x·x ∈ S ⇒ n ≤ x)
	    	 */
			Exists(bidList(_), ForAll(bidList(_), Limp(In(BoundIdentifier(0), S),
			        Le(BoundIdentifier(1), BoundIdentifier(0))))) -> {
				if (`S.isWellFormed()) {
                    return `S;
				}
			}

			/**
	    	 * FIN_L_LOWER_BOUND_R
             * Set Theory: ∃n·(∀x·x ∈ S ⇒ x ≥ n)
	    	 */
			Exists(bidList(_), ForAll(bidList(_), Limp(In(BoundIdentifier(0), S),
			        Ge(BoundIdentifier(0), BoundIdentifier(1))))) -> {
				if (`S.isWellFormed()) {
                    return `S;
				}
			}
			
            /**
             * FIN_L_UPPER_BOUND_R
             * Set Theory: ∃n·(∀x·x ∈ S ⇒ x ≤ n)
             */
            Exists(bidList(_), ForAll(bidList(_), Limp(In(BoundIdentifier(0), S),
                    Le(BoundIdentifier(0), BoundIdentifier(1))))) -> {
				if (`S.isWellFormed()) {
                    return `S;
				}
            }

            /**
             * FIN_L_UPPER_BOUND_L
             * Set Theory: ∃n·(∀x·x ∈ S ⇒ n ≥ x)
             */
            Exists(bidList(_), ForAll(bidList(_), Limp(In(BoundIdentifier(0), S),
                    Ge(BoundIdentifier(1), BoundIdentifier(0))))) -> {
				if (`S.isWellFormed()) {
                    return `S;
				}
            }
			
	    }
	    return null;
	}

    private boolean isFiniteBoundSetHyp(Predicate hyp, Expression boundedSet) {
    
        %match (Predicate hyp) {
            /**
             * Set Theory: finite(S)
             */
            Finite(S) -> {
                return boundedSet.equals(`S);
            }
        }
        return false;
    }

    /*
     * This can return:
     * - a predicate finite(set) from an hypothesis
     * - a true predicate if the set is implicitly finite (i.e., set extension)
     * - null if nothing proves that the set is finite
     */
    private Predicate getFiniteBoundSetHyp(IProverSequent seq) {
        final Expression boundedSet = getBoundSet(seq.goal());
        if (boundedSet == null) {
            return null;
        }
        for (Predicate shyp : seq.visibleHypIterable()) {
            if (isFiniteBoundSetHyp(shyp, boundedSet)) {
                return shyp;
            }
        }
        // For backwards compatibility, we first look for the set in the hypotheses.
        // If it is not found, but the bounded set is a set extension, we accept it
        // (finiteness is implicit), while it was previously rejected.
        %match (Expression boundedSet) {
            SetExtension(_) -> {
                return DLib.True(seq.getFormulaFactory());
            }
        }
        return null;
    }

    public boolean isApplicable(IProverSequent seq) {
        final Predicate finiteHyp = getFiniteBoundSetHyp(seq);

        return (finiteHyp != null);
    }
    
	private String getDisplayName() {
		return "Existence of minimum or maximum in goal with finite hypothesis";
	}

	@Override
    @ProverRule( { "FIN_L_LOWER_BOUND_L", "FIN_L_LOWER_BOUND_R",
            "FIN_L_UPPER_BOUND_L", "FIN_L_UPPER_BOUND_R" })
    public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
            IProofMonitor pm) {
	
        final Predicate finiteHyp = getFiniteBoundSetHyp(seq);
        if (finiteHyp == null) {
            return ProverFactory.reasonerFailure(
                    this,input,
                    "Finite hyp is not applicable");
        }
			
        final IProofRule reasonerOutput = ProverFactory.makeProofRule(
                this,
                input,
                seq.goal(),
                finiteHyp.getTag() == BTRUE ? emptySet() : singleton(finiteHyp),
                getDisplayName(),
                new IAntecedent[0]);
        
        return reasonerOutput;
	}
}
