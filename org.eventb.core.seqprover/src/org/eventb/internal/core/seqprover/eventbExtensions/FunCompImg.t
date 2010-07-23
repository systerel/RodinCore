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
import java.util.ArrayList;
import java.util.Collection;
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
public class FunCompImg extends AbstractManualInference {

	%include {FormulaV2.tom}
	
	public String getReasonerID() {
		return SequentProver.PLUGIN_ID + ".funCompImg";
	}
	
	@Override
	protected List<IPosition> filter(Predicate predicate, List<IPosition> positions) {
		List<IPosition> results = new ArrayList<IPosition>();
		for (IPosition position : positions) {
			IPosition leftChild = position.getFirstChild();
			IPosition child = leftChild.getFirstChild();
			Formula<?> subFormula = predicate.getSubFormula(child);
			while (subFormula != null) {
				if (!child.isFirstChild()) {
					results.add(child);
				}
				child = child.getNextSibling();
				subFormula = predicate.getSubFormula(child);
			}
		}
		
		return results; 
	}
	
	@Override
	protected boolean isExpressionApplicable(Expression expression) {
	    %match (Expression expression) {
			
			/**
	    	 * Set Theory: (f;...;g)(E)
	    	 */
			FunImage(Fcomp(_), _) -> {
				return true;
			}

	    }
	    return false;
	}

	@Override
	protected String getDisplayName() {
		return "fun. comp. img.";
	}

	@Override
	@ProverRule( { "SIM_FCOMP_L", "SIM_FCOMP_R" })
	protected IAntecedent[] getAntecedents(IProverSequent seq, Predicate pred,
			IPosition position) {
		Predicate predicate = pred;
		if (predicate == null)
			predicate = seq.goal();
		else if (!seq.containsHypothesis(predicate)) {
			return null;
		}
		
		Formula<?> subExp = predicate.getSubFormula(position);
		if (position.isRoot())
			return null;

		IPosition parentPos = position.getParent();
		if (parentPos.isRoot()) {
			return null;
		}
		
		IPosition replacedPos = parentPos.getParent();

		Formula<?> formula = predicate.getSubFormula(replacedPos);
		
		if (formula == null || !(formula instanceof Expression))
			return null;
		
		Expression expression = (Expression) formula;
		
		Collection<Expression> firstHalf = new ArrayList<Expression>();
		Collection<Expression> secondHalf = new ArrayList<Expression>();
		Expression E = null;
		
	    %match (Expression expression) {

			/**
	    	 * Set Theory: (f;...;g;h;...;l)(E) and function compsition contains subExp
	    	 */
			FunImage(Fcomp(children), EE) -> {
				if (subExp == `children[0])
					return null;
				boolean found = false;
				for (Expression child : `children) {
					if (found)
						secondHalf.add(child);
					else if (child == subExp) {
						found = true;
						secondHalf.add(child);
					}
					else
						firstHalf.add(child);
				}
				E = `EE;
			}

	    }
		if (firstHalf.size() == 0 || secondHalf.size() == 0)
			return null;
		final FormulaFactory ff = seq.getFormulaFactory();
		// There will be 2 antecidents
		IAntecedent[] antecidents = new IAntecedent[2];

		// (f;...;g)(E)
		Expression fToGComp = makeCompIfNeccessary(firstHalf, ff);
		Expression funImg = ff.makeBinaryExpression(Expression.FUNIMAGE,
				fToGComp, E, null);
		
		// (h;...;l)((f;...;g)(E))
		Expression hToLComp = makeCompIfNeccessary(secondHalf, ff);
		Expression funImg2 = ff.makeBinaryExpression(Expression.FUNIMAGE,
				hToLComp, funImg, null);

		Predicate inferredPred = predicate.rewriteSubFormula(replacedPos,
				funImg2, ff);

		antecidents[0] = makeAntecedent(pred, inferredPred);

		// Well-definedness
		antecidents[1] = makeWD(seq.getFormulaFactory(), inferredPred);

		return antecidents;
	}
	
}
