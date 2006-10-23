package org.eventb.core.seqprover;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.HypothesesManagement.Action;
import org.eventb.core.seqprover.IProofRule.IAnticident;
import org.eventb.internal.core.seqprover.ProofRule;
import org.eventb.internal.core.seqprover.ProofTree;
import org.eventb.internal.core.seqprover.ProverSequent;
import org.eventb.internal.core.seqprover.ReasonerFailure;
import org.eventb.internal.core.seqprover.ProofRule.Anticident;

/**
 * Static class with factory methods required to construct various data structures
 * used in the sequent prover
 * 
 * @author Farhad Mehta
 *
 */
public final class ProverFactory {

	private ProverFactory() {
	}
	
	public static IReasonerFailure reasonerFailure(
			IReasoner generatedBy,
			IReasonerInput generatedUsing,
			String reason){
		return new ReasonerFailure(generatedBy,generatedUsing,reason);
	}
	
	public static IProofRule makeProofRule (
			IReasoner generatedBy,
			IReasonerInput generatedUsing,
			Predicate goal,
			Set<Hypothesis> neededHyps,
			Integer confidence,
			String display,
			IAnticident[] anticidents) {
		
		assert goal != null;
		assert anticidents != null;
		
		ProofRule proofRule = new ProofRule(generatedBy,generatedUsing,goal,neededHyps,confidence,display,anticidents);
		
		return proofRule;
	}
	
	public static IProofRule makeProofRule (
			IReasoner generatedBy,
			IReasonerInput generatedUsing,
			Predicate goal,
			Hypothesis neededHyp,
			String display,
			IAnticident[] anticidents) {
		return makeProofRule(generatedBy,generatedUsing,goal,Collections.singleton(neededHyp),null, display,anticidents);
	}
	
	public static IProofRule makeProofRule (
			IReasoner generatedBy,
			IReasonerInput generatedUsing,
			Predicate goal,
			String display,
			IAnticident[] anticidents) {
		return makeProofRule(generatedBy,generatedUsing,goal,null,null,display,anticidents);
	}
	
	public static IAnticident makeAnticident(
			Predicate goal,
			Set<Predicate> addedHyps,
			FreeIdentifier[] addedFreeIdents,
			List<Action> hypAction){
		
		assert goal != null;
		
		IAnticident anticident = new Anticident(goal, addedHyps, addedFreeIdents, hypAction);
		
		return anticident;
		
	}

	public static IAnticident makeAnticident(
			Predicate goal,
			Set<Predicate> addedHyps,
			Action hypAction) {
		
		if (hypAction != null){
			ArrayList<Action> hypActions = new ArrayList<Action>(1);
			hypActions.add(hypAction);
			return makeAnticident(goal,addedHyps,null,hypActions);
		}
		return makeAnticident(goal,addedHyps,null,null);
	}
	
	public static IAnticident makeAnticident(Predicate goal) {
		return makeAnticident(goal,null,null,null);
	}

	public static IProverSequent makeSequent(ITypeEnvironment typeEnvironment,Set<Hypothesis> hyps,Predicate goal){
		return new ProverSequent(typeEnvironment,hyps,goal);
	}

	/**
	 * Creates a new proof tree for the given sequent.
	 * 
	 * @param sequent
	 *            the sequent to prove
	 * @return a new proof tree for the given sequent
	 */
	public static IProofTree makeProofTree(IProverSequent sequent) {
		return new ProofTree(sequent);
	}

	

}
