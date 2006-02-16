package org.eventb.core.prover.rules;

import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.SuccessfullExtReasonerOutput;
import org.eventb.core.prover.sequent.HypothesesManagement;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.ISequent;

public class PLb implements Rule {

	private SuccessfullExtReasonerOutput pluginOutput;
	
	public String name(){
		return "["+pluginOutput.generatedBy().name()+"]";
	}
	
	public PLb(SuccessfullExtReasonerOutput pluginOutput){
		this.pluginOutput = pluginOutput;
	}
			
//		if (((pluginOutput.getGoal() instanceof BinaryPredicate)) &	 
//				(pluginOutput.getGoal().getTag() == Formula.LIMP))
//			this.pluginOutput = pluginOutput;
//	}
//	
	
	public boolean isApplicable(IProverSequent S) {
//		if (pluginOutput.proof().discharges((Sequent)S)) return true;
		final ISequent proven = pluginOutput.proof().ofSequent();
		if (! isInContextOf(proven,S)) return false;
		if (proven.goal().equals(Lib.False)) return true;
		if (proven.goal().equals(S.goal())) return true;
		if (Lib.isImp(proven.goal()) & Lib.impRight(proven.goal()).equals(S.goal()))
			return true;
//		if ( proven.goal() instanceof BinaryPredicate &
//				proven.goal().getTag() == Formula.LIMP &
//				((BinaryPredicate)proven.goal()).getRight().equals(S.goal()) &
//				S.typeEnvironment().containsAll(proven.typeEnvironment()) &
//				S.hypotheses().containsAll(proven.hypotheses())) return true;
		
		return false;
	}
		
//		final Predicate goal = ((BinaryPredicate)pluginOutput.getGoal()).getRight();
//		if (! goal.equals(S.getGoal())) return false;
//		if (! pluginOutput.getPC().isSubcontextOf(S.getPC())) return false;
//		return true;
//	}

	public IProverSequent[] apply(IProverSequent S) {
		if (! isApplicable(S)) return null;
		
		final ISequent proven = pluginOutput.proof().ofSequent();
		if (! isInContextOf(proven,S)) return null;
		if (proven.goal().equals(Lib.False)) return new IProverSequent[0];
		if (proven.goal().equals(S.goal())) return new IProverSequent[0];
//		assert proven.goal() instanceof BinaryPredicate;
//		assert proven.goal().getTag() == Formula.LIMP;
//		assert ((BinaryPredicate)proven.goal()).getRight().equals(S.goal());
//		assert S.typeEnvironment().containsAll(proven.typeEnvironment());
//		assert S.hypotheses().containsAll(proven.hypotheses());
		if (!(Lib.isImp(proven.goal()) & Lib.impRight(proven.goal()).equals(S.goal())))
			return null;
		final Predicate newGoal = Lib.impLeft(proven.goal());
		IProverSequent anticident = S;
		anticident = anticident.replaceGoal(newGoal,null);
		anticident = HypothesesManagement.perform(pluginOutput.hypActions(),anticident);
		return new IProverSequent[] {anticident};
	}
	
	private boolean isInContextOf(ISequent S1,IProverSequent S2){
		if (! S2.typeEnvironment().containsAll(S1.typeEnvironment())) return false;
		if (! S2.hypotheses().containsAll(S1.hypotheses())) return false;
		return true;
	}
	
}
