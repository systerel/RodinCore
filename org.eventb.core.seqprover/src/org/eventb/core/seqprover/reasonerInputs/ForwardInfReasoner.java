package org.eventb.core.seqprover.reasonerInputs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;
import org.eventb.internal.core.seqprover.ReasonerFailure;

/**
 * Common implementation for reasoners that generate a forward inference with exactly one input hypothesis and
 * mark it as the sole required hypothesis for first forward inference of the sole antecedent of the generated rule.
 * 
 * <p>
 * Such reasoners generate rules that are goal independant, 
 * with exactly one antecedent whose first hyp action
 * is a forward hypothesis. 
 * </p>
 * 
 * <p>
 * Such reasoners are designed to be used in an ineractive setting. The required hyp of the forward inference is
 * hidden, and the inferred hypoptheses are selected. Subclasses may add more hyp actions by overriding the appropriate
 * method.
 * </p>
 * 
 * @author Farhad Mehta
 * @since 1.0
 */
public abstract class ForwardInfReasoner implements IReasoner {
	
	public static final class Input implements IReasonerInput {

		Predicate pred;

		public Input(Predicate pred) {
			this.pred = pred;
		}

		public void applyHints(ReplayHints hints) {
			pred = hints.applyHints(pred);
		}

		public String getError() {
			return null;
		}

		public boolean hasError() {
			return false;
		}

	}
	
	public final void serializeInput(IReasonerInput rInput,
			IReasonerInputWriter writer) throws SerializeException {
		
		// Nothing to do
	}

	public final Input deserializeInput(IReasonerInputReader reader)
	throws SerializeException {

		IAntecedent[] antecedents = reader.getAntecedents();
		if (antecedents.length != 1) {
			throw new SerializeException(new IllegalStateException(
			"Expected exactly one antecedent."));
		}

		List<IHypAction> hypActions = antecedents[0].getHypActions();
		if (hypActions.size() < 1) {
			throw new SerializeException(new IllegalStateException(
			"Expected at least one hyp action."));
		}
		if (! (hypActions.get(0) instanceof IForwardInfHypAction)) {
			throw new SerializeException(new IllegalStateException(
			"Expected the first hyp action to be a forward inference."));
		}

		IForwardInfHypAction fwdHypAction = (IForwardInfHypAction) hypActions.get(0);

		Collection<Predicate> requiredHyps = fwdHypAction.getHyps();
		final int length = requiredHyps.size();

		if (length != 1) {
			throw new SerializeException(new IllegalStateException(
			"Expected exactly one required hypothesis."));
		}
		for (Predicate hyp: requiredHyps) {
			return new Input(hyp);
		}
		assert false;
		return null;
	}

	public final IReasonerOutput apply(IProverSequent seq, IReasonerInput rInput,
			IProofMonitor pm) {
		
		final Input input = (Input) rInput;
		final Predicate pred = input.pred;

		final String display = getDisplay(pred);
		final IForwardInfHypAction forwardInf;
		try {
			forwardInf = getForwardInf(seq, pred);
		} catch (IllegalArgumentException e) {
			return new ReasonerFailure(this, input, e.getMessage());
		}
		
		final List<IHypAction> hypActions = new ArrayList<IHypAction>();
		hypActions.add(forwardInf);
		hypActions.add(ProverFactory.makeHideHypAction(forwardInf.getHyps()));
		hypActions.add(ProverFactory.makeSelectHypAction(forwardInf.getInferredHyps()));		
		hypActions.addAll(getAdditionalHypActions(seq, pred));
		
		return ProverFactory.makeProofRule(this, input, display, hypActions);
	}
	
	/**
	 * Return the forward infernece to put in the generated rule, or throw an
	 * <code>IllegalArgumentException</code> in case of reasoner failure. In
	 * the latter case, the message associated to the exception will be returned
	 * in the reasoner failure.
	 * 
	 * @param sequent
	 *            the goal of the current sequent
	 * @param pred
	 *            the required predicate for the forward inference.
	 *            
	 * @return the forward inference of the generated rule
	 * @throws IllegalArgumentException
	 *             if no forward inference could be genreated with the given predicate.
	 */
	protected abstract IForwardInfHypAction getForwardInf (IProverSequent sequent,
			Predicate pred) throws IllegalArgumentException;
	
	
	/**
	 * Return the display string to associate to the generated rule
	 * 
	 * @param pred
	 *            the predicate of the hypothesis.
	 * @return the display string for the generated rule
	 */
	protected abstract String getDisplay(Predicate pred);
	
	/**
	 * Return the additional hyp Actions to put in the generated rule after the generated
	 * hyp action.
	 * 
	 * <p>
	 * This method only gets called if {@link #getForwardInf(IProverSequent, Predicate)} did not
	 * throw an {@link IllegalArgumentException}. Subclasses should override this method only if they
	 * want to provide additional hyp actions.
	 * <p>
	 *  
	 * @param sequent
	 *            the goal of the current sequent
	 * @param pred
	 *            the required predicate for the forward inference.
	 *            
	 * @return the additional hyp actions for the generated rule.
	 */
	protected List<IHypAction> getAdditionalHypActions (IProverSequent sequent,
			Predicate pred){
		return Collections.emptyList();
	}

}
