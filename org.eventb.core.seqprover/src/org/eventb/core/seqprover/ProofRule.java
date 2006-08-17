package org.eventb.core.seqprover;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.sequent.HypothesesManagement;
import org.eventb.core.seqprover.sequent.Hypothesis;
import org.eventb.core.seqprover.sequent.IProverSequent;
import org.eventb.core.seqprover.sequent.HypothesesManagement.Action;

public class ProofRule extends ReasonerOutput implements IProofRule{
	
	public static class Anticident{
		
		public FreeIdentifier[] addedFreeIdentifiers;
		public Set<Predicate> addedHypotheses;
		public List <Action> hypAction;
		public Predicate subGoal;
		
		public Anticident(){
			addedFreeIdentifiers = new FreeIdentifier[0];
			addedHypotheses = new HashSet<Predicate>();
			hypAction = new ArrayList<Action>();
			subGoal = null;
		}
		
		public IProverSequent genSequent(IProverSequent seq){
			ITypeEnvironment newTypeEnv;
			if (addedFreeIdentifiers.length == 0)
				newTypeEnv = seq.typeEnvironment();
			else
			{
				newTypeEnv = seq.typeEnvironment().clone();
				for (FreeIdentifier freeIdent : addedFreeIdentifiers) {
					// check for variable name clash
					if (newTypeEnv.contains(freeIdent.getName()))
					{
						// name clash
						return null;
					}
					newTypeEnv.addName(freeIdent.getName(),freeIdent.getType());
				}
				// Check of variable name clashes
//				if (! Collections.disjoint(
//						seq.typeEnvironment().getNames(),
//						addedFreeIdentifiers.getNames()))
//					// This is the place to add name refactoring code.
//					return null;
//				newTypeEnv = seq.typeEnvironment().clone();
//				newTypeEnv.addAll(addedFreeIdentifiers);
			}
			IProverSequent result = seq.replaceGoal(subGoal,newTypeEnv);
			if (result == null) return null;
			Set<Hypothesis> hypsToAdd = Hypothesis.Hypotheses(addedHypotheses);
			result = result.addHyps(hypsToAdd,null);
			if (result == null) return null;
			result = result.selectHypotheses(hypsToAdd);
			result = HypothesesManagement.perform(hypAction,result);
			return result;
		}

		public void addFreeIdents(ITypeEnvironment typeEnv) {
			assert subGoal != null;
			typeEnv.addAll(subGoal.getFreeIdentifiers());
			for(Predicate hyp: addedHypotheses){
				typeEnv.addAll(
						hyp.getFreeIdentifiers());
			}
			// This is not strictly needed. Just to be safe..
			typeEnv.addAll(addedFreeIdentifiers);
		}
		
//		public Set<FreeIdentifier> getNeededFreeIdents() {
//			Set<FreeIdentifier> neededFreeIdents = new HashSet<FreeIdentifier>();
//			assert subGoal != null;
//			neededFreeIdents.addAll(Arrays.asList(subGoal.getFreeIdentifiers()));
//			for(Predicate hyp: addedHypotheses){
//				neededFreeIdents.addAll(
//						Arrays.asList(hyp.getFreeIdentifiers()));
//			}
//			neededFreeIdents.removeAll(Arrays.asList(addedFreeIdentifiers));
//			return neededFreeIdents;
//		}
		
		public void addConjunctsToAddedHyps(Predicate pred){
			addedHypotheses.addAll(Lib.breakConjuncts(pred));
		}
		
		public void addToAddedHyps(Predicate pred){
			addedHypotheses.add(pred);
		}
	}
	
	public String display;
	public Anticident[] anticidents;
	public Set<Hypothesis> neededHypotheses;
	public Predicate goal;
	public int reasonerConfidence;
	
	public ProofRule(IReasoner generatedBy, IReasonerInput generatedUsing){
		super(generatedBy,generatedUsing);
		display = generatedBy.getReasonerID();
		anticidents = null;
		neededHypotheses = new HashSet<Hypothesis>();
		goal = null;
		reasonerConfidence = IConfidence.DISCHARGED_MAX;
	}

	public void addFreeIdents(ITypeEnvironment typeEnv) {
		for(Anticident anticident : anticidents){
			anticident.addFreeIdents(typeEnv);
		}
		
		typeEnv.addAll(goal.getFreeIdentifiers());
		for(Hypothesis hyp: neededHypotheses){
			typeEnv.addAll(
					hyp.getPredicate().getFreeIdentifiers());
		}
	}

	public String getDisplayName() {
		return display;
	}

	public String getRuleID() {
		return generatedBy.getReasonerID();
	}

	public int getConfidence() {
		return reasonerConfidence;
	}

	public IProverSequent[] apply(IProverSequent seq) {
		ProofRule reasonerOutput = this;
		// Check if all the needed hyps are there
		if (! seq.hypotheses().containsAll(reasonerOutput.neededHypotheses))
			return null;
		// Check if the goal is the same
		if (! reasonerOutput.goal.equals(seq.goal())) return null;
		
		// Generate new anticidents
		// Anticident[] anticidents = reasonerOutput.anticidents;
		IProverSequent[] anticidents 
			= new IProverSequent[reasonerOutput.anticidents.length];
		for (int i = 0; i < anticidents.length; i++) {
			anticidents[i] = reasonerOutput.anticidents[i].genSequent(seq);
			if (anticidents[i] == null)
				// most probably a name clash occured
				// or an invalid type env.
				// add renaming/refactoring code here
				return null;
		}
		
		return anticidents;
	}

	public Set<Hypothesis> getNeededHypotheses() {
		return neededHypotheses;
	}

}
