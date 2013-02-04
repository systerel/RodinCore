/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
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

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInputReasoner;

/**
 * Generates a 'cut' rule, introducing the given predicate as a hypothesis.
 * 
 * <p>
 * This rule is also sometimes refered to as the 'ah' (add hypothesis) rule. The well definedness subgoal 
 * for the introduced hypothesis is also generated.
 * <p>
 * 
 * @author Farhad Mehta
 *
 */
public class Cut extends SinglePredInputReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".cut";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	@ProverRule("CUT")
	public IReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProofMonitor pm){
		
		// Organize Input
		final SinglePredInput input = (SinglePredInput) reasonerInput;
		
		if (input.hasError())
			return ProverFactory.reasonerFailure(this,reasonerInput,input.getError());

		final Predicate lemma = input.getPredicate();
		
		// This check may be redone for replay since the type environment
		// may have shrunk, making the previous predicate with dangling free vars.

		// This check now done when constructing the sequent.. 
		// so the reasoner is successful, but the rule fails.
		
		//		if (! Lib.typeCheckClosed(lemma,seq.typeEnvironment()))
		//			return new ReasonerOutputFail(this,input,
		//					"Type check failed for predicate: "+lemma);
		
		// We can now assume that lemma has been properly parsed and typed.
		
		final Predicate lemmaWD = DLib.WD(lemma);
		
		final Set<Predicate> lemmaWDs = Lib.breakPossibleConjunct(lemmaWD);
		DLib.removeTrue(lemma.getFactory(), lemmaWDs);
		// final ISelectionHypAction deselectWDpreds = ProverFactory.makeDeselectHypAction(lemmaWDs);
		
		
		// Generate the anticidents
		final IAntecedent[] anticidents = new IAntecedent[3];
		
		// Well definedness condition
		anticidents[0] = ProverFactory.makeAntecedent(lemmaWD);
		
		// The lemma to be proven
		// Replaced, adding WD lemma into hyps
		// anticidents[1] = ProverFactory.makeAntecedent(lemma);
		anticidents[1] = ProverFactory.makeAntecedent(
				lemma, 
				lemmaWDs, 
				null);
		
		
		// Proving the original goal with the help of the lemma
		// Replaced, adding WD lemma into hyps
		//		anticidents[2] = ProverFactory.makeAntecedent(
		//				null,
		//				Collections.singleton(lemma),
		//				null);
		final Set<Predicate> lemmaWDsPlusLemma = new LinkedHashSet<Predicate>();
		lemmaWDsPlusLemma.addAll(lemmaWDs);
		lemmaWDsPlusLemma.add(lemma);
		anticidents[2] = ProverFactory.makeAntecedent(
				null,
				lemmaWDsPlusLemma,
				null);

		
		// Generate the proof rule
		final IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				null,
				"ah ("+lemma.toString()+")",
				anticidents);
				
		return reasonerOutput;
	}

}
