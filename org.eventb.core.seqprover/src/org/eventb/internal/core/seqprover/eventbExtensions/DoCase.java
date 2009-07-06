package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.Collections;

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
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInputReasoner;


/**
 * Generates the proof rule for case distinction on the given predicate
 * 
 * <p>
 * The well definedness subgoal for the given predicate is generated.
 * </p>
 * 
 * @author Farhad Mehta
 *
 */
public class DoCase extends SinglePredInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".doCase";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	@ProverRule("DISTINCT_CASE")
	public IReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProofMonitor pm){
		
		// Organize Input
		SinglePredInput input = (SinglePredInput) reasonerInput;
		
		if (input.hasError())
			return ProverFactory.reasonerFailure(this,reasonerInput,input.getError());

		Predicate trueCase = input.getPredicate();
		// This check may be redone for replay since the type environment
		// may have shrunk, making the previous predicate with dangling free vars.
		
		// This check now done when constructing the sequent.. 
		// so the reasoner is successful, but the rule fails.
		
		//		if (! Lib.typeCheckClosed(trueCase,seq.typeEnvironment()))
		//			return new ReasonerOutputFail(this,input,
		//					"Type check failed for predicate: "+trueCase);
		
		
		// We can now assume that the true case has been properly parsed and typed.
		
		// Generate the well definedness condition for the true case
		Predicate trueCaseWD = Lib.WD(trueCase);
		
		// Generate the anticidents
		IAntecedent[] anticidents = new IAntecedent[3];
		
		// Well definedness condition
		anticidents[0] = ProverFactory.makeAntecedent(trueCaseWD);
		
		// The goal with the true case
		anticidents[1] = ProverFactory.makeAntecedent(
				null,
				Collections.singleton(trueCase),
				null);
		
		// The goal with the false case
		anticidents[2] = ProverFactory.makeAntecedent(
				null,
				Collections.singleton(Lib.makeNeg(trueCase)),
				null);
		
		// Generate the successful reasoner output
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				null,
				"dc ("+trueCase.toString()+")",
				anticidents);

		return reasonerOutput;
	}

}
