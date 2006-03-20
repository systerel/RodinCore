package org.eventb.core.prover.externalReasoners;

import java.io.IOException;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IExtReasonerInput;
import org.eventb.core.prover.IExtReasonerOutput;
import org.eventb.core.prover.SuccessfullExtReasonerOutput;
import org.eventb.core.prover.UnSuccessfulExtReasonerOutput;
import org.eventb.core.prover.externalReasoners.classicB.ClassicB;
import org.eventb.core.prover.proofs.TrustedProof;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.ISequent;
import org.eventb.core.prover.sequent.SimpleSequent;


/**
 * Implementation of a call to the Predicate Prover provided by B4free.
 * 
 * @author Laurent Voisin
 */
public class ExternalPP extends LegacyProvers {
	
	public String name(){
		return "PP (ext)";
	}

	private boolean runPP(
			ITypeEnvironment typeEnvironment,
			Set<Hypothesis> hypotheses,
			Predicate goal,
			long timeOutDelay,
			IProgressMonitor monitor) {
		
		final int length = hypotheses.size();
		final Predicate[] hyps = new Predicate[length];
		int index = 0;
		for (Hypothesis hypothesis: hypotheses){
			hyps[index ++] = hypothesis.getPredicate();
		}
		StringBuffer sequent = 
			ClassicB.translateSequent(typeEnvironment, hyps, goal);
		try {
			return ClassicB.proveWithPP(sequent, timeOutDelay, monitor);
		} catch (IOException e) {
			return false;
		}
	}
	
	public IExtReasonerOutput apply(IProverSequent sequent,
			IExtReasonerInput input) {
		
		final Input myInput = (Input) input;
		final long timeOutDelay = myInput.timeOutDelay;
		if (timeOutDelay < 0) {
			return new UnSuccessfulExtReasonerOutput(
					this,
					input,
					"Invalid time out delay"
			);
		}
		final IProgressMonitor monitor = myInput.monitor;
		final ITypeEnvironment typeEnvironment = sequent.typeEnvironment();
		final Set<Hypothesis> hypotheses;
		if (myInput.restricted) {
			hypotheses = sequent.selectedHypotheses();
		} else {
			hypotheses = sequent.visibleHypotheses();
		}
		final Predicate goal = sequent.goal();
		
		final boolean success =
			runPP(typeEnvironment, hypotheses, goal, timeOutDelay, monitor);
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
				"PP failed"
		);
	}
	
	public IExtReasonerInput defaultInput(){
		return new Input();
	}
	
	public static class Input extends LegacyProvers.Input {
		
		// True if only selected hypotheses are passed to PP
		final boolean restricted;
		
		public Input() {
			this(DEFAULT_DELAY, null);
		}

		public Input(long timeOutDelay) {
			this(timeOutDelay, null);
		}
		
		public Input(IProgressMonitor monitor) {
			this(DEFAULT_DELAY, monitor);
		}

		public Input(long timeOutDelay, IProgressMonitor monitor) {
			super(timeOutDelay, monitor);
			this.restricted = false;
		}

		public Input(boolean restricted, long timeOutDelay) {
			super(timeOutDelay, null);
			this.restricted = restricted;
		}

		public Input(boolean restricted, IProgressMonitor monitor) {
			super(DEFAULT_DELAY, monitor);
			this.restricted = restricted;
		}

		public Input(boolean restricted, long timeOutDelay, IProgressMonitor monitor) {
			super(timeOutDelay, monitor);
			this.restricted = restricted;
		}
	}

}
