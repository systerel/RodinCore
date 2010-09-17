/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
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
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;

/**
 * Basic implementation for Function Converse inference rule f~(f(E))
 */
@SuppressWarnings("unused")
public class FunInterImg extends AbstractManualInference
		implements IVersionedReasoner {

	private static final int REASONER_VERSION = 1;

	%include {FormulaV2.tom}
	
	public String getReasonerID() {
		return SequentProver.PLUGIN_ID + ".funInterImg";
	}

	public int getVersion() {
		return REASONER_VERSION;
	}
	
	@Override
	protected boolean isExpressionApplicable(Expression expression) {
	    %match (Expression expression) {
			
			/**
	    	 * Set Theory: f[S ∩ ... ∩ T]
	    	 */
			RelImage(_, BInter(_)) -> {
				return true;
			}

	    }
	    return false;
	}

	@Override
	protected String getDisplayName() {
		return "fun. inter. img.";
	}

	@Override
	@ProverRule( { "DIS_BINTER_L", "DIS_BINTER_R" })
	protected IAntecedent[] getAntecedents(IProverSequent seq, Predicate pred,
			IPosition position) {
		Predicate predicate = pred;
		if (predicate == null)
			predicate = seq.goal();
		else if (!seq.containsHypothesis(predicate)) {
			return null;
		}

		if (!predicate.isWDStrict(position)) {
			return null; // invalid position: reasoner failure
		}

		Formula<?> subFormula = predicate.getSubFormula(position);

		// "subFormula" should have the form f[S ∩ ... ∩ T]
		if (!isApplicable(subFormula))
			return null;
			
		Expression expression = (Expression) subFormula;

		Expression f = null;
		Expression [] children = null;
	    %match (Expression expression) {

			/**
	    	 * Set Theory: f[S ∩ ... ∩ T]
	    	 */
			RelImage(ff, BInter(cChildren)) -> {
				f = `ff;
				children = `cChildren;
			}

	    }
		if (f == null)
			return null;
		final FormulaFactory ff = seq.getFormulaFactory();
		// There will be 2 antecidents
		IAntecedent[] antecidents = new IAntecedent[2];

		// f~ : A +-> B (from type of f)
		antecidents[0] = makeFunctionalAntecident(f, true, Expression.PFUN, ff);
		
		// f[S] /\ ... /\ f[T]
		Expression [] newChildren = new Expression[children.length];
		for (int i = 0; i < children.length; ++i) {
			newChildren[i] = ff.makeBinaryExpression(Expression.RELIMAGE, f, children[i], null);
		}
		Expression exp = ff.makeAssociativeExpression(Expression.BINTER, newChildren, null);
		
		Predicate inferredPred = predicate.rewriteSubFormula(position,
				exp, ff);

		antecidents[1] = makeAntecedent(pred, inferredPred);
		return antecidents;
	}
	
}
