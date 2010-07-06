package org.eventb.internal.core.seqprover.eventbExtensions;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

/**
 * A reasoner that generates proof rules that discharge a goal of the form 
 * 
 * 		E : T1 -/-> T2
 * 
 *  where E is an expression, and T1 and T2 are type expressions. Such goals are often generated as WD proof
 *  obligations.
 * 
 * <p>
 * In order to discharge such a goal, the reasoner searches for the presence of a hypothesis of the form
 * 
 * 		E : *any_expr* *functional_arrow* *any_expr*0
 * 
 *  and reports it as a needed hypothesis.
 *  </p>
 *  
 * 
 * @author Farhad Mehta
 *
 */
public class IsFunGoal extends EmptyInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".isFunGoal";
	
	public String getReasonerID() {
		return REASONER_ID;
	}

	@ProverRule("FUN_GOAL")
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProofMonitor pm){
	
		// Check that goal is of the correct form.
		
		if (! Lib.isInclusion(seq.goal()))
			return ProverFactory.reasonerFailure(this,input,"Goal is not an inclusion");
		
		Expression element = Lib.getElement(seq.goal());
		Expression set = Lib.getSet(seq.goal());
		
		if (! Lib.isPFun(set))
			return ProverFactory.reasonerFailure(this,input,"Goal is not a functional inclusion");
		
		if (! Lib.getLeft(set).isATypeExpression())
			return ProverFactory.reasonerFailure(this,input,"Left hand side of functional inclusion in goal is not a type expression.");

		if (! Lib.getRight(set).isATypeExpression())
			return ProverFactory.reasonerFailure(this,input,"Right hand side of functional inclusion in goal is not a type expression.");

		
		// Search for hyp of the correct form
		// TODO : maybe look first in selected hypotheses.
		
		Predicate funHyp = null;
		for (Predicate hyp : seq.visibleHypIterable()) {
			if (Lib.isInclusion(hyp) && 
					Lib.isFun(Lib.getSet(hyp)) && 
					element.equals(Lib.getElement(hyp)) ){
				funHyp = hyp;
				break;
			}
		}
				
		if (funHyp == null)
			return ProverFactory.reasonerFailure(this,input,"No appropriate hypothesis found");
		
		// construct the proof rule
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this, input,
				seq.goal(), funHyp, 
				"functional goal",
				new IAntecedent[0]);
		
		return reasonerOutput;
	}

}
