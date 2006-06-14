package org.eventb.core.prover.reasoners;

import java.io.IOException;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.ReasonerOutput;
import org.eventb.core.prover.ReasonerOutputFail;
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.SerializableReasonerInput;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
import org.eventb.core.prover.reasoners.classicB.ClassicB;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;


/**
 * Implementation of a call to the Mono-Lemma Prover provided by B4free.
 * 
 * @author Laurent Voisin
 */
public class ExternalML extends LegacyProvers {
	

	public String getReasonerID() {
		return "ML (ext)";
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
			ReasonerInput reasonerInput) {
		
		Input myInput;
		if (reasonerInput instanceof SerializableReasonerInput){
			myInput = new Input((SerializableReasonerInput)reasonerInput);
		} 
		else myInput = (Input) reasonerInput;
		
		final long timeOutDelay = myInput.timeOutDelay;
		if (timeOutDelay < 0) {
			return new ReasonerOutputFail(
					this,
					reasonerInput,
					"Invalid time out delay"
			);
		}
		final String forces = myInput.getForces();
		if (forces.length() == 0) {
			return new ReasonerOutputFail(
					this,
					reasonerInput,
					"Invalid forces"
			);
		}
		final IProgressMonitor monitor = myInput.monitor;
		final ITypeEnvironment typeEnvironment = sequent.typeEnvironment();
		final Set<Hypothesis> hypotheses = sequent.visibleHypotheses();
		final Predicate goal = sequent.goal();
		
		final boolean success =
			runML(typeEnvironment, hypotheses, goal, forces, timeOutDelay, monitor);
		if (success) {
			
			ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,reasonerInput);
			reasonerOutput.goal = sequent.goal();
			reasonerOutput.neededHypotheses.addAll(hypotheses);
			reasonerOutput.anticidents = new Anticident[0];
			
			return reasonerOutput;

		}
		return new ReasonerOutputFail(
				this,
				reasonerInput,
				"ML failed"
		);
	}
	
	public ReasonerInput defaultInput(){
		return new Input();
	}
	
	public static class Input extends LegacyProvers.Input {
		
		public static int FORCE_0 = 0x1;
		public static int FORCE_1 = 0x2;
		public static int FORCE_2 = 0x4;
		public static int FORCE_3 = 0x8;
		
		public static int DEFAULT_FORCES = FORCE_0 | FORCE_1;

		// Forces to use in the mono-lemma prover
		final int forces;
		
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
			this.forces = DEFAULT_FORCES;
		}

		public Input(int forces, long timeOutDelay) {
			super(timeOutDelay, null);
			this.forces = forces;
		}

		public Input(int forces, IProgressMonitor monitor) {
			super(DEFAULT_DELAY, monitor);
			this.forces = forces;
		}

		public Input(int forces, long timeOutDelay, IProgressMonitor monitor) {
			super(timeOutDelay, monitor);
			this.forces = forces;
		}
		
		public String getForces() {
			StringBuilder builder = new StringBuilder();
			addForce(builder, FORCE_0, '0');
			addForce(builder, FORCE_1, '1');
			addForce(builder, FORCE_2, '2');
			addForce(builder, FORCE_3, '3');
			return builder.toString();
		}

		private void addForce(StringBuilder builder, int force, char image) {
			if ((forces & force) != 0) {
				if (builder.length() != 0) builder.append(';');
				builder.append(image);
			}			
		}
		
		public Input(SerializableReasonerInput serializableReasonerInput) {
			super(serializableReasonerInput);
			this.forces = Integer.parseInt(serializableReasonerInput.getString("forces"));
		}

		public SerializableReasonerInput genSerializable() {
			SerializableReasonerInput serializableReasonerInput =
				super.genSerializable();
			serializableReasonerInput.putString("forces",String.valueOf(this.forces));
			return serializableReasonerInput;
		}
		
	}

}
