package org.eventb.internal.pp.ui;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.xprover.XProverCall;
import org.eventb.core.seqprover.xprover.XProverInput;
import org.eventb.pp.PPProof;
import org.eventb.pp.PPResult.Result;

public class PPProverCall extends XProverCall {

	private PPProof prover; 
	private XProverInput input;
	
	public PPProverCall(XProverInput input, Iterable<Predicate> hypothesis, Predicate goal, IProofMonitor pm) {
		super(hypothesis,goal,pm);
		
		this.prover = new PPProof(hypothesis,goal);
		this.input = input;
	}
	
	@Override
	public void cleanup() {
//		prover = null;
	}

	@Override
	public String displayMessage() {
		return "newPP";
	}

	@Override
	public boolean isValid() {
		return prover.getResult().getResult()==Result.valid;
	}

	private static final long DEFAULT_PERIOD = 317;

	@Override
	public void run() {
		final Thread thread = Thread.currentThread();
		
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (isCancelled()) {
					thread.interrupt();
					cancel();
				}
			}
		}, DEFAULT_PERIOD, DEFAULT_PERIOD);
		
		prover.translate();
		prover.prove(-1);

	}
	
	@Override
	public boolean isGoalNeeded() {
		return prover.getResult().getTracer().isGoalNeeded();
	}

	@Override
	public Set<Predicate> neededHypotheses() {
		return new HashSet<Predicate>(prover.getResult().getTracer().getOriginalPredicates());
	}

}
