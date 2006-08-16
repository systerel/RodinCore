package org.eventb.core.seqprover.reasoners;

import java.io.IOException;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.reasoners.classicB.ClassicB;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputSerializer;
import org.eventb.core.seqprover.ReasonerOutput;
import org.eventb.core.seqprover.ReasonerOutputFail;
import org.eventb.core.seqprover.ReasonerOutputSucc;
import org.eventb.core.seqprover.ReplayHints;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IReasonerInputSerializer.SerializeException;
import org.eventb.core.seqprover.ReasonerOutputSucc.Anticident;
import org.eventb.core.seqprover.sequent.Hypothesis;
import org.eventb.core.seqprover.sequent.IProverSequent;


/**
 * Implementation of a call to the Mono-Lemma Prover provided by B4free.
 * 
 * @author Laurent Voisin
 * @author Farhad Mehta
 */
public class ExternalML implements IReasoner {
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".externalML";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerInput deserializeInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		return new Input(
				reasonerInputSerializer.getString("forces"),
				Long.parseLong(reasonerInputSerializer.getString("timeOutDelay"))
				);
	}

	
	private boolean runML(
			ITypeEnvironment typeEnvironment,
			Set<Hypothesis> hypotheses,
			Predicate goal,
			String forces,
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
			return ClassicB.proveWithML(sequent, forces, timeOutDelay, monitor);
		} catch (IOException e) {
			return false;
		}
	}
	
	public ReasonerOutput apply(IProverSequent sequent,
			IReasonerInput reasonerInput, IProgressMonitor progressMonitor) {
		
		Input input = (Input) reasonerInput;
		
		if (input.hasError())
			return new ReasonerOutputFail(this,input,input.getError());
		
		final long timeOutDelay = input.timeOutDelay;
		final String forces = input.forces;
		
		final ITypeEnvironment typeEnvironment = sequent.typeEnvironment();
		final Set<Hypothesis> hypotheses = sequent.visibleHypotheses();
		final Predicate goal = sequent.goal();
		
		final boolean success =
			runML(typeEnvironment, hypotheses, goal, forces, timeOutDelay, progressMonitor);
		if (success) {
			
			ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,reasonerInput);
			reasonerOutput.goal = sequent.goal();
			reasonerOutput.neededHypotheses.addAll(hypotheses);
			reasonerOutput.anticidents = new Anticident[0];
			reasonerOutput.display = "ml";
			
			return reasonerOutput;

		}
		return new ReasonerOutputFail(
				this,
				reasonerInput,
				"ML failed"
		);
	}
	
	public static class Input implements IReasonerInput {
		
		public static int FORCE_0 = 0x1;
		public static int FORCE_1 = 0x2;
		public static int FORCE_2 = 0x4;
		public static int FORCE_3 = 0x8;
		
		public static int DEFAULT_FORCES = FORCE_0 | FORCE_1;

		// Forces to use in the mono-lemma prover
		final String forces;
		final long timeOutDelay;
		final String error;
		
		private static final long DEFAULT_DELAY = 30 * 1000;
		
		private Input(String forces, long timeOutDelay) {
			if (timeOutDelay < 0) {
				this.forces = null;
				this.timeOutDelay = -1;
				this.error = "Invalid time out delay";
				return;
			}
			if (forces.length() == 0){
				this.forces = null;
				this.timeOutDelay = -1;
				this.error = "Invalid forces";
				return;
			}
			
			this.timeOutDelay = timeOutDelay;
			this.forces = forces;
			this.error = null;
		}
		
		public Input(int forces, long timeOutDelay) {
			this(forcesToString(forces),timeOutDelay);
		}
		
		public Input(int forces) {
			this(forces,DEFAULT_DELAY);
		}

		public static String forcesToString(int forces) {
			StringBuilder builder = new StringBuilder();
			addForce(builder, FORCE_0, '0', forces);
			addForce(builder, FORCE_1, '1', forces);
			addForce(builder, FORCE_2, '2', forces);
			addForce(builder, FORCE_3, '3', forces);
			return builder.toString();
		}

		private static void addForce(StringBuilder builder, int force, char image, int forces) {
			if ((forces & force) != 0) {
				if (builder.length() != 0) builder.append(';');
				builder.append(image);
			}			
		}
		
		public boolean hasError() {
			return error != null;
		}

		public String getError() {
			return error;
		}
		
		public void serialize(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
			reasonerInputSerializer.putString("timeOutDelay",Long.toString(timeOutDelay));		
			reasonerInputSerializer.putString("forces",forces);
		}

		public void applyHints(ReplayHints hints) {
		}
		
	}

}
