/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - deselect WD predicate and used hypothesis
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;

/**
 * Reasoner that instantiates a universally quantified implication and performs a modus ponens on it in one step.
 * 
 * <p>
 * This reasoner reuses the input from {@link AllD.Input}.
 * </p>
 * 
 * @author Farhad Mehta
 *
 */
public class AllmpD extends AllD {
	
	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".allmpD";
		
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput reasonerInput, IProofMonitor pm){
	
		// Organize Input
		Input input = (Input) reasonerInput;

		if (input.hasError())
			return ProverFactory.reasonerFailure(this,reasonerInput,input.getError());
		
		Predicate univHyp = input.pred;
		
		if (! seq.containsHypothesis(univHyp))
			return ProverFactory.reasonerFailure(this,input,
					"Nonexistent hypothesis:"+univHyp);
		if (! Lib.isUnivQuant(univHyp))
			return ProverFactory.reasonerFailure(this,input,
					"Hypothesis is not universally quantified:"+univHyp);
		if (! Lib.isImp(Lib.getBoundPredicate(univHyp)))
			return ProverFactory.reasonerFailure(this,input,
					"Universally quantified hypothesis: " + univHyp + " is not a bound implication");
		
		BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(univHyp);
		
		
		// compute instantiations from the input: 
		// it can be that the number of bound variables have increased 
	    // or decreased, or their types have changed.
		// Not sure if reasoner should actually modify its input to reflect this.
		Expression[] instantiations = input.computeInstantiations(boundIdentDecls);
		
		if (instantiations == null)
			return ProverFactory.reasonerFailure(
					this,
					reasonerInput,
					"Type error when trying to instantiate bound identifiers");
		
		assert instantiations.length == boundIdentDecls.length;
		
		for (int i = 0; i < instantiations.length; i++) {
			if (instantiations[i] == null) 
				return ProverFactory.reasonerFailure(this,input,
						"Missing instantiation for " + boundIdentDecls[i]);
		}
		
		// Generate the well definedness predicate for the instantiations
		final Predicate WDpred = Lib.WD(instantiations);
		final Set<Predicate> WDpreds = Lib.breakPossibleConjunct(WDpred);
		Lib.removeTrue(WDpreds);
		
		// Generate the instantiated predicate
		Predicate instantiatedImp = Lib.instantiateBoundIdents(univHyp,instantiations);
		assert instantiatedImp != null;
		assert Lib.isImp(instantiatedImp);
		Predicate impLeft = Lib.impLeft(instantiatedImp);
		Predicate impRight = Lib.impRight(instantiatedImp);
		
		// Generate the successful reasoner output
		
		// Generate the anticidents
		IAntecedent[] anticidents = new IAntecedent[3];

		// Well definedness condition
		anticidents[0] = ProverFactory.makeAntecedent(Lib.makeConj(WDpreds));

		// The instantiated to impLeft goal
		{
			final Set<Predicate> addedHyps = new LinkedHashSet<Predicate>();
			addedHyps.addAll(WDpreds);

			anticidents[1] = ProverFactory.makeAntecedent(
					impLeft,
					addedHyps,
					ProverFactory.makeDeselectHypAction(WDpreds)
			);
		}

		// The instantiated continuation
		{
			final Set<Predicate> addedHyps = new LinkedHashSet<Predicate>();
			addedHyps.addAll(WDpreds);
			addedHyps.addAll(Lib.breakPossibleConjunct(impRight));

			final Set<Predicate> toDeselect = new HashSet<Predicate>();
			toDeselect.add(univHyp);
			toDeselect.addAll(WDpreds);

			anticidents[2] = ProverFactory.makeAntecedent(
					null,
					addedHyps,
					ProverFactory.makeDeselectHypAction(toDeselect)
			);
		}
		
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				null,
				univHyp,
				"∀ hyp mp (inst "+super.displayInstantiations(instantiations)+")",
				anticidents
				);
		
		return reasonerOutput;
	}

}
