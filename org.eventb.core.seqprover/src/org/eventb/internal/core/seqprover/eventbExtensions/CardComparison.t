/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import java.math.BigInteger;
import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultFilter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Tactics;

/**
 * Basic implementation for comparison of cardinalities
 */
@SuppressWarnings("unused")
public class CardComparison extends AbstractManualInference {

	%include {Formula.tom}
	
	public String getReasonerID() {
		return SequentProver.PLUGIN_ID + ".cardComparison";
	}
	
	@Override
	protected boolean isPredicateApplicable(Predicate predicate) {
	    %match (Predicate predicate) {
			
			/**
	    	 * Set Theory: card(S) = card(T), where S and T have the same type.
	    	 */
			Equal(Card(S), Card(T)) -> {
				if (`S.getType().equals(`T.getType()))
					return true;
			}

			/**
	    	 * Set Theory: card(S) ≤ card(T), where S and T have the same type.
	    	 */
			Le(Card(S), Card(T)) -> {
				if (`S.getType().equals(`T.getType()))
					return true;
			}

			/**
	    	 * Set Theory: card(S) < card(T), where S and T have the same type.
	    	 */
			Lt(Card(S), Card(T)) -> {
				if (`S.getType().equals(`T.getType()))
					return true;
			}

			/**
	    	 * Set Theory: card(S) ≥ card(T), where S and T have the same type.
	    	 */
			Ge(Card(S), Card(T)) -> {
				if (`S.getType().equals(`T.getType()))
					return true;
			}

			/**
	    	 * Set Theory: card(S) > card(T), where S and T have the same type.
	    	 */
			Gt(Card(S), Card(T)) -> {
				if (`S.getType().equals(`T.getType()))
					return true;
			}

	    }
	    return false;
	}

	@Override
	protected String getDisplayName() {
		return "card. comparison";
	}

	@Override
	protected IAntecedent[] getAntecedents(IProverSequent seq, Predicate pred,
			IPosition position) {
		if (pred != null)
			return null; // Only can apply in goal
		Predicate predicate = seq.goal();
		
		// position must be an applicable position
		List<IPosition> positions = getPositions(predicate, true);
		if (!positions.contains(position))
			return null;
		
		Formula<?> subFormula = predicate.getSubFormula(position);
			
		Predicate subPredicate = (Predicate) subFormula;

		Expression S = null;
		Expression T = null;
		int tag = 0;
	    %match (Predicate subPredicate) {

			/**
	    	 * Set Theory: card(S) = card(T)
	    	 */
			Equal(Card(SS), Card(TT)) -> {
				if (`SS.getType().equals(`TT.getType()))
				  S = `SS;
				  T = `TT;
				  tag = Predicate.EQUAL;
			}

			/**
	    	 * Set Theory: card(S) ≤ card(T)
	    	 */
			Le(Card(SS), Card(TT)) -> {
				if (`SS.getType().equals(`TT.getType()))
				  S = `SS;
				  T = `TT;
				  tag = Predicate.LE;
			}

			/**
	    	 * Set Theory: card(S) < card(T)
	    	 */
			Lt(Card(SS), Card(TT)) -> {
				if (`SS.getType().equals(`TT.getType()))
				  S = `SS;
				  T = `TT;
				  tag = Predicate.LT;
			}

			/**
	    	 * Set Theory: card(S) ≥ card(T)
	    	 */
			Ge(Card(SS), Card(TT)) -> {
				if (`SS.getType().equals(`TT.getType()))
				  S = `SS;
				  T = `TT;
				  tag = Predicate.GE;
			}

			/**
	    	 * Set Theory: card(S) > card(T)
	    	 */
			Gt(Card(SS), Card(TT)) -> {
				if (`SS.getType().equals(`TT.getType()))
				  S = `SS;
				  T = `TT;
				  tag = Predicate.GT;
			}

	    }
		if (S == null)
			return null;
		
		// There will be 1 antecedent
		IAntecedent[] antecedents = new IAntecedent[1];
		
		Predicate inferredHyp = null;
		if (tag == Predicate.EQUAL) {
			// H |- Q(S = T)
			Predicate rPred = ff.makeRelationalPredicate(Predicate.EQUAL, S, T, null);
			inferredHyp = predicate.rewriteSubFormula(position,
					rPred, ff); 
		}
		else if (tag == Predicate.LE) {
			Predicate rPred = ff.makeRelationalPredicate(Predicate.SUBSETEQ, S, T, null);
			inferredHyp = predicate.rewriteSubFormula(position,
					rPred, ff);
		}
		else if (tag == Predicate.LT) {
			Predicate rPred = ff.makeRelationalPredicate(Predicate.SUBSET, S, T, null);
			inferredHyp = predicate.rewriteSubFormula(position,
					rPred, ff);
		}
		else if (tag == Predicate.GE) {
			Predicate rPred = ff.makeRelationalPredicate(Predicate.SUBSETEQ, T, S, null);
			inferredHyp = predicate.rewriteSubFormula(position,
					rPred, ff);
		}
		else if (tag == Predicate.GT) {
			Predicate rPred = ff.makeRelationalPredicate(Predicate.SUBSET, T, S, null);
			inferredHyp = predicate.rewriteSubFormula(position,
					rPred, ff);
		}
		assert (inferredHyp != null);
		antecedents[0] = makeAntecedent(pred, inferredHyp);
		
		return antecedents; 
	}
	
}
