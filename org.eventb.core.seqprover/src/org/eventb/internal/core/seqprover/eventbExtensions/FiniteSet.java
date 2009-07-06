package org.eventb.internal.core.seqprover.eventbExtensions;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SimplePredicate;
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
import org.eventb.core.seqprover.reasonerInputs.SingleExprInput;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInputReasoner;

public class FiniteSet extends SingleExprInputReasoner {
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".finiteSet";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	@ProverRule("FIN_SUBSETEQ_R")
	public IReasonerOutput apply(IProverSequent seq,IReasonerInput input, IProofMonitor pm){
		
		Predicate goal = seq.goal();
		if (! Lib.isFinite(goal))
			return ProverFactory.reasonerFailure(this,input,"Goal is not a finiteness");
		
		Expression S = ((SimplePredicate) goal).getExpression();
		
		if (!(input instanceof SingleExprInput))
			return ProverFactory.reasonerFailure(this, input,
					"Expected a single expression input");

		if (((SingleExprInput) input).hasError()) {
			return ProverFactory.reasonerFailure(this, input,
					((SingleExprInput) input).getError());
		}
		Expression T = ((SingleExprInput) input).getExpression();

		if (!(S.getType().equals(T.getType()))) {
			return ProverFactory.reasonerFailure(this, input,
					"Incorrect input type");
		}
		
		// There will be two antecident
		IAntecedent[] antecidents = new IAntecedent[2];
		
		FormulaFactory ff = FormulaFactory
						.getDefault();
		// finite(T) is the goal
		antecidents[0] = ProverFactory.makeAntecedent(
				ff.makeSimplePredicate(Predicate.KFINITE, T, null));
		
		// S <: T is the goal
		antecidents[1] = ProverFactory.makeAntecedent(
				ff.makeRelationalPredicate(Predicate.SUBSETEQ, S, T, null));
		
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				goal,
				"finite set",
				antecidents);
		
		return reasonerOutput;
	}

}
