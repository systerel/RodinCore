/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
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
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
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
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;

/**
 * Basic implementation for Cardinality of range of numbers "card(a..b)"
 */
@SuppressWarnings("unused")
public class CardUpTo extends AbstractManualInference {

	%include {FormulaV2.tom}
	
	public String getReasonerID() {
		return SequentProver.PLUGIN_ID + ".cardUpTo";
	}
	
	@Override
	protected boolean isExpressionApplicable(Expression expression) {
	    %match (Expression expression) {
			
			/**
	    	 * Set Theory: card(a‥b)
	    	 */
			Card(UpTo(_,_)) -> {
				return true;
			}

	    }
	    return false;
	}

	@Override
	protected String getDisplayName() {
		return "card. up to";
	}

	@ProverRule("SIMP_LIT_CARD_UPTO")
	@Override
	protected IAntecedent[] getAntecedents(IProverSequent seq, Predicate pred,
			IPosition position) {
		Predicate predicate = pred;
		if (predicate == null)
			predicate = seq.goal();
		else if (!seq.containsHypothesis(predicate)) {
			return null;
		}

		// position must be an applicable position
		List<IPosition> positions = getPositions(predicate, true);
		if (!positions.contains(position))
			return null;
		
		Formula<?> subFormula = predicate.getSubFormula(position);
			
		Expression expression = (Expression) subFormula;

		Expression a = null;
		Expression b = null;
	    %match (Expression expression) {

			/**
	    	 * Set Theory: card(a‥b)
	    	 */
			Card(UpTo(aa,bb)) -> {
				a = `aa;
				b = `bb;
			}

	    }
		if (a == null)
			return null;
		
		// There will be 2 antecedents
		IAntecedent[] antecedents = new IAntecedent[2];

		// H, a <= b |- Q((b - a) + 1)
		Predicate le = ff.makeRelationalPredicate(Predicate.LE, a, b, null);
		Expression [] newChildren = new Expression[2]; 
		newChildren[0] = ff.makeBinaryExpression(Predicate.MINUS, b, a, null);
		newChildren[1] = ff.makeIntegerLiteral(new BigInteger(
			"1"), null);
		Expression replaced0 = ff.makeAssociativeExpression(Predicate.PLUS,
				newChildren, null);
		Predicate inferredHyp0 = predicate.rewriteSubFormula(position,
				replaced0, ff); 
		
		antecedents[0] = makeAntecedent(pred, inferredHyp0, le);
		
		
		// H, b < a |- Q(0)
		Predicate lt = ff.makeRelationalPredicate(Predicate.LT, b, a, null);
		Expression number0 = ff.makeIntegerLiteral(new BigInteger(
			"0"), null);
		Predicate inferredHyp1 = predicate.rewriteSubFormula(position,
				number0, ff); 
		
		antecedents[1] = makeAntecedent(pred, inferredHyp1, lt);
		return antecedents; 
	}
	
}
