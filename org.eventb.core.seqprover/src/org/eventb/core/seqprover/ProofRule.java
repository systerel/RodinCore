package org.eventb.core.seqprover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule.IAnticident;
import org.eventb.core.seqprover.sequent.HypothesesManagement;
import org.eventb.core.seqprover.sequent.Hypothesis;
import org.eventb.core.seqprover.sequent.IProverSequent;
import org.eventb.core.seqprover.sequent.HypothesesManagement.Action;

public class ProofRule extends ReasonerOutput implements IProofRule{
	
	public static class Anticident implements IAnticident{
		
		public FreeIdentifier[] addedFreeIdentifiers;
		private Set <Predicate> addedHypotheses;
		public List <Action> hypAction;
		public Predicate goal;
		
		@Deprecated
		public Anticident(){
			addedFreeIdentifiers = new FreeIdentifier[0];
			addedHypotheses = new HashSet<Predicate>();
			hypAction = new ArrayList<Action>();
			goal = null;
		}
		
		public Anticident(Predicate goal){
			addedFreeIdentifiers = new FreeIdentifier[0];
			addedHypotheses = new HashSet<Predicate>();
			hypAction = new ArrayList<Action>();
			this.goal = goal;
		}
		
		public Anticident(Predicate goal, Set<Predicate> addedHyps, FreeIdentifier[] addedFreeIdents, List<Action> hypAction) {
			assert goal != null;
			this.goal = goal;
			this.addedHypotheses = addedHyps == null ? new HashSet<Predicate>() : addedHyps;
			this.addedFreeIdentifiers = addedFreeIdents == null ? new FreeIdentifier[0] : addedFreeIdents;
			this.hypAction = hypAction == null ? new ArrayList<Action>() : hypAction;
		}

		public void addConjunctsToAddedHyps(Predicate pred){
			addedHypotheses.addAll(Lib.breakConjuncts(pred));
		}
		
		public void addToAddedHyps(Predicate pred){
			addedHypotheses.add(pred);
		}
		
		public void addToAddedHyps(Collection<Predicate> preds){
			addedHypotheses.addAll(preds);
		}
		
		
		/**
		 * @return Returns the addedFreeIdentifiers.
		 */
		public final FreeIdentifier[] getAddedFreeIdents() {
			return addedFreeIdentifiers;
		}

		/**
		 * @param addedFreeIdentifiers The addedFreeIdentifiers to set.
		 */
		public final void setAddedFreeIdentifiers(FreeIdentifier[] addedFreeIdentifiers) {
			this.addedFreeIdentifiers = addedFreeIdentifiers;
		}

		/**
		 * @return Returns the hypAction.
		 */
		public final List<Action> getHypAction() {
			return hypAction;
		}

		public final void addHypAction(Action hypAction) {
			this.hypAction.add(hypAction);
		}

		/**
		 * @return Returns the addedHypotheses.
		 */
		public final Set<Predicate> getAddedHyps() {
			return Collections.unmodifiableSet(addedHypotheses);
		}

		/**
		 * @return Returns the goal.
		 */
		public final Predicate getGoal() {
			return goal;
		}
		
		
		
		private IProverSequent genSequent(IProverSequent seq){
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
			IProverSequent result = seq.replaceGoal(goal,newTypeEnv);
			if (result == null) return null;
			Set<Hypothesis> hypsToAdd = Hypothesis.Hypotheses(addedHypotheses);
			result = result.addHyps(hypsToAdd,null);
			if (result == null) return null;
			result = result.selectHypotheses(hypsToAdd);
			result = HypothesesManagement.perform(hypAction,result);
			return result;
		}

		public void addFreeIdents(ITypeEnvironment typeEnv) {
			assert goal != null;
			typeEnv.addAll(goal.getFreeIdentifiers());
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
		
	}
	
	public String display;
	public IAnticident[] anticidents;
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
	
	public ProofRule(IReasoner generatedBy, IReasonerInput generatedUsing, Predicate goal, IAnticident[] anticidents){
		super(generatedBy,generatedUsing);
		display = generatedBy.getReasonerID();
		this.anticidents = anticidents;
		neededHypotheses = new HashSet<Hypothesis>();
		this.goal = goal;
		reasonerConfidence = IConfidence.DISCHARGED_MAX;
	}

	public ProofRule(IReasoner generatedBy, IReasonerInput generatedUsing, Predicate goal, Set<Hypothesis> neededHyps, Integer confidence, String display, IAnticident[] anticidents) {
		super(generatedBy,generatedUsing);
		
		assert goal != null;
		assert anticidents != null;
		
		this.goal = goal;
		this.anticidents = anticidents;
		this.neededHypotheses = neededHyps == null ? new HashSet<Hypothesis>() : neededHyps;
		this.reasonerConfidence = confidence == null ? IConfidence.DISCHARGED_MAX : confidence;
		this.display = display == null ? generatedBy.getReasonerID() : display;		
	}

	// TODO : change to protected
	public void addFreeIdents(ITypeEnvironment typeEnv) {
		for(IAnticident anticident : anticidents){
			((Anticident) anticident).addFreeIdents(typeEnv);
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

	// TODO : change to protected
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
			anticidents[i] = ((Anticident) reasonerOutput.anticidents[i]).genSequent(seq);
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

	public Predicate getGoal() {
		return goal;
	}

	public IAnticident[] getAnticidents() {
		return anticidents;
	}

}
