/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInput;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInputReasoner;

/**
 * This reasoner abstracts a given expression with a fresh free identifier.
 * 
 * It does this by intruducing a new free variable and an equality hypothesis that can be
 * used to later rewrite all occurances of the expression by the free variable.
 * 
 * @author Farhad Mehta
 *
 */
public class AbstrExpr extends SingleExprInputReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".ae";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProofMonitor pm){
		
		// Organize Input
		SingleExprInput input = (SingleExprInput) reasonerInput;
		
		if (input.hasError())
			return ProverFactory.reasonerFailure(this,reasonerInput,input.getError());

		Expression expr = input.getExpression();
				
		// We can now assume that lemma has been properly parsed and typed.
		
		// Generate the well definedness condition for the lemma
		final DLib lib = DLib.mDLib(seq.getFormulaFactory());
		final Predicate exprWD = lib.WD(expr);
		final Set<Predicate> exprWDs = Lib.breakPossibleConjunct(exprWD);
		lib.removeTrue(exprWDs);
		
		// Generate a fresh free identifier
		final FormulaFactory ff = seq.getFormulaFactory();
		final FreeIdentifier freeIdent = ff.makeFreeIdentifier(
				genFreshFreeIdentName(seq.typeEnvironment()),
				null, expr.getType());
		
		// Generate the equality predicate
		final Predicate aeEq = lib.makeEq(freeIdent, expr);
		
		// Generate the anticidents
		final IAntecedent[] anticidents = new IAntecedent[2];
		
		// Well definedness condition
		anticidents[0] = ProverFactory.makeAntecedent(exprWD);
		
		// 
		final Set<Predicate> addedHyps = new LinkedHashSet<Predicate>();
		addedHyps.addAll(exprWDs);
		// aeEq is always the last addedHyp
		addedHyps.add(aeEq);
		anticidents[1] = ProverFactory.makeAntecedent(
				null, addedHyps,
				new FreeIdentifier[] {freeIdent}, null);
		
		// Generate the proof rule
		return ProverFactory.makeProofRule(this, input, null,
				"ae (" + expr.toString() + ")", anticidents);
	}
	

	/**
	 * Generates a name for an identifier that does not occur in the
	 * given type environment.
	 * 
	 * @param typeEnv
	 * 			the given type environment.
	 * @return a fresh identifier name.
	 * 			
	 */
	private String genFreshFreeIdentName(ITypeEnvironment typeEnv){
		String prefix = "ae";
		String identName = prefix;
		int i = 0;
		while (typeEnv.contains(identName)){
			identName = prefix + Integer.toString(i);
			i++;
		}
		return identName;
	}
	
}
