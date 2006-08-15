package org.eventb.core.prover.reasoners;

import java.io.IOException;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IReasonerInputSerializer;
import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.ReasonerOutput;
import org.eventb.core.prover.ReasonerOutputFail;
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.ReplayHints;
import org.eventb.core.prover.SerializableReasonerInput;
import org.eventb.core.prover.IReasonerInputSerializer.SerializeException;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
import org.eventb.core.prover.reasoners.classicB.ClassicB;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;


/**
 * Implementation of a call to the Predicate Prover provided by B4free.
 * 
 * @author Laurent Voisin
 */
public class ExternalPP extends LegacyProvers {
	
	public String getReasonerID() {
		return "PP(ext)";
	}
	
	public ReasonerInput deserializeInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
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
			ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,reasonerInput);
			reasonerOutput.goal = sequent.goal();
			reasonerOutput.neededHypotheses.addAll(hypotheses);
			reasonerOutput.anticidents = new Anticident[0];
			reasonerOutput.display = "pp";
			
			return reasonerOutput;
		}
		return new ReasonerOutputFail(
				this,
				reasonerInput,
				"PP failed"
		);
	}
	
	public ReasonerInput defaultInput(){
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
		
		public Input(SerializableReasonerInput serializableReasonerInput) {
			super(serializableReasonerInput);
			this.restricted = Boolean.parseBoolean(serializableReasonerInput.getString("restricted"));
		}

		public SerializableReasonerInput genSerializable() {
			SerializableReasonerInput serializableReasonerInput =
				super.genSerializable();
			serializableReasonerInput.putString("restricted",String.valueOf(this.restricted));
			return serializableReasonerInput;
		}

		public boolean hasError() {
			// TODO Auto-generated method stub
			return false;
		}

		public String getError() {
			// TODO Auto-generated method stub
			return null;
		}

		public void serialize(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
			reasonerInputSerializer.putString("timeOutDelay",Long.toString(timeOutDelay));		
			reasonerInputSerializer.putString("restricted",Boolean.toString(restricted));
		}

		public void applyHints(ReplayHints hints) {
			// TODO Auto-generated method stub
			
		}
	}
}
