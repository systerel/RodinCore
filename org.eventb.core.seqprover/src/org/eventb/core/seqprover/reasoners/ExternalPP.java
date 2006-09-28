package org.eventb.core.seqprover.reasoners;

import java.io.IOException;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.reasoners.classicB.ClassicB;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputSerializer;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ReplayHints;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAnticident;
import org.eventb.core.seqprover.IReasonerInputSerializer.SerializeException;


/**
 * Implementation of a call to the Predicate Prover provided by B4free.
 * 
 * @author Laurent Voisin
 * @author Farhad Mehta
 */
public class ExternalPP implements IReasoner {
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".externalPP";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerInput deserializeInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		return new Input(
				Boolean.parseBoolean(reasonerInputSerializer.getString("restricted")),
				Long.parseLong(reasonerInputSerializer.getString("timeOutDelay"))
				);
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
	
	public IReasonerOutput apply(IProverSequent sequent,
			IReasonerInput reasonerInput, IProgressMonitor progressMonitor) {
		
		Input input = (Input) reasonerInput;
		
		if (input.hasError())
			ProverFactory.reasonerFailure(this,input,input.getError());
		
		final long timeOutDelay = input.timeOutDelay;
	
		final ITypeEnvironment typeEnvironment = sequent.typeEnvironment();
		final Set<Hypothesis> hypotheses;
		if (input.restricted) {
			hypotheses = sequent.selectedHypotheses();
		} else {
			hypotheses = sequent.visibleHypotheses();
		}
		final Predicate goal = sequent.goal();
	
		final boolean success =
			runPP(typeEnvironment, hypotheses, goal, timeOutDelay, progressMonitor);
		if (success) {
			
			IProofRule reasonerOutput = ProverFactory.makeProofRule(
					this,reasonerInput,
					sequent.goal(),
					hypotheses,
					null,
					"pp",
					new IAnticident[0]);
//			
//			ProofRule reasonerOutput = new ProofRule(this,reasonerInput);
//			reasonerOutput.goal = sequent.goal();
//			reasonerOutput.neededHypotheses.addAll(hypotheses);
//			reasonerOutput.anticidents = new Anticident[0];
//			reasonerOutput.display = "pp";
			
			return reasonerOutput;
		}
		return ProverFactory.reasonerFailure(
				this,
				reasonerInput,
				"PP failed"
		);
	}
	
	public static class Input implements IReasonerInput {
		
		// True if only selected hypotheses are passed to PP
		final boolean restricted;
		final long timeOutDelay;
		final String error;
		
		private static final long DEFAULT_DELAY = 30 * 1000;
		
		public Input(boolean restricted, long timeOutDelay) {
			if (timeOutDelay < 0) {
				this.restricted = false;
				this.timeOutDelay = -1;
				this.error = "Invalid time out delay";
				return;
			}
			this.restricted = restricted;
			this.timeOutDelay = timeOutDelay;
			this.error = null;
		}
		
		public Input(boolean restricted) {
			this(restricted,DEFAULT_DELAY);
		}

		public boolean hasError() {
			return error != null;
		}

		public String getError() {
			return error;
		}

		public void serialize(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
			reasonerInputSerializer.putString("timeOutDelay",Long.toString(timeOutDelay));		
			reasonerInputSerializer.putString("restricted",Boolean.toString(restricted));
		}

		public void applyHints(ReplayHints hints) {
		}
	}
}
