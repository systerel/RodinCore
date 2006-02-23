package org.eventb.core.prover.externalReasoners;

import java.io.IOException;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IExtReasonerInput;
import org.eventb.core.prover.IExtReasonerOutput;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.SuccessfullExtReasonerOutput;
import org.eventb.core.prover.UnSuccessfulExtReasonerOutput;
import org.eventb.core.prover.externalReasoners.classicB.ClassicB;
import org.eventb.core.prover.proofs.TrustedProof;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.ISequent;
import org.eventb.core.prover.sequent.SimpleSequent;


public class LegacyProvers implements IExternalReasoner {
	
	public String name(){
		return "legacy provers";
	}

	private boolean runLegacyProvers(
			ITypeEnvironment typeEnvironment,
			Set<Hypothesis> hypotheses,
			Predicate goal,
			long timeOutDelay) {
		
		final int length = hypotheses.size();
		final Predicate[] hyps = new Predicate[length];
		int index = 0;
		for (Hypothesis hypothesis: hypotheses){
			hyps[index ++] = hypothesis.getPredicate();
		}
		StringBuffer sequent = 
			ClassicB.translateSequent(typeEnvironment, hyps, goal);
		try {
			if (ClassicB.proveWithML(sequent, timeOutDelay))
				return true;
			else
				return ClassicB.proveWithPP(sequent, timeOutDelay);
		} catch (IOException e) {
			return false;
		}
	}
	
	public IExtReasonerOutput apply(IProverSequent sequent,
			IExtReasonerInput input) {
		
		final ITypeEnvironment typeEnvironment = sequent.typeEnvironment();
		final Set<Hypothesis> hypotheses = sequent.visibleHypotheses();
		final Predicate goal = sequent.goal();
		final Input myInput = (Input) input;
		final long timeOutDelay = myInput.timeOutDelay;
		if (timeOutDelay < 0) {
			return new UnSuccessfulExtReasonerOutput(
					this,
					input,
					"Invalid time out delay"
			);
		}
		
		final boolean success =
			runLegacyProvers(typeEnvironment, hypotheses, goal, timeOutDelay);
		if (success) {
			ISequent outputSequent = 
				new SimpleSequent(typeEnvironment, hypotheses, goal);
			return new SuccessfullExtReasonerOutput(
					this,
					input,
					new TrustedProof(outputSequent)
			);
		}
		return new UnSuccessfulExtReasonerOutput(
				this,
				input,
				"Legacy provers failed"
		);
	}
	
	public IExtReasonerInput defaultInput(){
		return new Input();
	}
	
	public static class Input implements IExtReasonerInput{
		
		public final long timeOutDelay;
		
		public Input() {
			// Defaults to 30 seconds 
			this.timeOutDelay = 30 * 1000;
		}

		public Input(long timeOutDelay) {
			this.timeOutDelay  = timeOutDelay;
		}
	}

}
