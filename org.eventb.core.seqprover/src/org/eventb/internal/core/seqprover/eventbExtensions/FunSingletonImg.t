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
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;

/**
 * Basic implementation for "function apply to singleton set image" f[{E}]
 */
@SuppressWarnings("unused")
public class FunSingletonImg extends AbstractManualInference {

	%include {FormulaV2.tom}
	
	public String getReasonerID() {
		return SequentProver.PLUGIN_ID + ".funSingletonImg";
	}
	
	@Override
	protected boolean isExpressionApplicable(Expression expression) {
	    %match (Expression expression) {
			
			/**
	    	 * Set Theory: f[{E}]
	    	 */
			RelImage(_, SetExtension(children)) -> {
				return `children.length == 1;
			}

	    }
	    return false;
	}

	@Override
	protected String getDisplayName() {
		return "fun. singleton img.";
	}

	@ProverRule( { "SIM_REL_IMAGE_L", "SIM_REL_IMAGE_R" })
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

		// "subFormula" should have the form f[{E}]
		if (!isApplicable(subFormula))
			return null;
			
		Expression expression = (Expression) subFormula;

		Expression f = null;
		Expression E = null;
	    %match (Expression expression) {

			/**
	    	 * Set Theory: f[{E}]
	    	 */
			RelImage(ff, SetExtension(children)) -> {
				if (`children.length == 1) {
					f = `ff;
					E = `children[0];
				}
			}

	    }
		if (f == null)
			return null;
		final FormulaFactory ff = seq.getFormulaFactory();
		// There will be 2 antecidents
		IAntecedent[] antecidents = new IAntecedent[2];

		// {f(E)}
		Expression exp = ff.makeBinaryExpression(Expression.FUNIMAGE, f, E, null);
		Expression setExt = ff.makeSetExtension(exp, null);
		
		Predicate inferredPred = predicate.rewriteSubFormula(position,
				setExt, ff);

		// Well-definedness
		antecidents[0] = makeWD(inferredPred);

		antecidents[1] = makeAntecedent(pred, inferredPred);
		return antecidents;
	}
	
}
