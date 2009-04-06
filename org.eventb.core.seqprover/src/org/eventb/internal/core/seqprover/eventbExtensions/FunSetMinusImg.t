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
import org.eventb.core.ast.MultiplePredicate;
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
 * Basic implementation for Function Converse inference rule f~(f(E))
 */
@SuppressWarnings("unused")
public class FunSetMinusImg extends AbstractManualInference {

	%include {Formula.tom}
	
	public String getReasonerID() {
		return SequentProver.PLUGIN_ID + ".funSetMinusImg";
	}
	
	@Override
	protected boolean isExpressionApplicable(Expression expression) {
	    %match (Expression expression) {
			
			/**
	    	 * Set Theory: f[S ∖ T]
	    	 */
			RelImage(_, SetMinus(_, _)) -> {
				return true;
			}

	    }
	    return false;
	}

	@Override
	protected String getDisplayName() {
		return "fun. set minus img.";
	}

	@Override
	protected IAntecedent[] getAntecedents(IProverSequent seq, Predicate pred,
			IPosition position) {
		Predicate predicate = pred;
		if (predicate == null)
			predicate = seq.goal();
		else if (!seq.containsHypothesis(predicate)) {
			return null;
		}

		Formula<?> subFormula = predicate.getSubFormula(position);

		// "subFormula" should have the form f[S ∖ T]
		if (!isApplicable(subFormula))
			return null;
			
		Expression expression = (Expression) subFormula;

		Expression f = null;
		Expression S = null;
		Expression T = null;
	    %match (Expression expression) {

			/**
	    	 * Set Theory: f[S ∖ T]
	    	 */
			RelImage(ff, SetMinus(SS, TT)) -> {
				f = `ff;
				S = `SS;
				T = `TT;
			}

	    }
		if (f == null)
			return null;
		
		// There will be 2 antecidents
		IAntecedent[] antecidents = new IAntecedent[2];

		// f : A +-> B (from type of f)
		antecidents[0] = makeFunctionalAntecident(f, true, Expression.PFUN);
		
		// f[S] ∖ f[T]
		Expression exp1 = ff.makeBinaryExpression(Expression.RELIMAGE, f, S, null);
		Expression exp2 = ff.makeBinaryExpression(Expression.RELIMAGE, f, T, null);
		Expression exp = ff.makeBinaryExpression(Expression.SETMINUS, exp1, exp2, null);
		
		Predicate inferredPred = predicate.rewriteSubFormula(position,
				exp, ff);

		antecidents[1] = makeAntecedent(pred, inferredPred);
		return antecidents;
	}
	
}
