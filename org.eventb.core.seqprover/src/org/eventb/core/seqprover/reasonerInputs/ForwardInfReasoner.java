/*******************************************************************************
 * Copyright (c) 2007, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added Input.getPred()
 *******************************************************************************/
package org.eventb.core.seqprover.reasonerInputs;

import static org.eventb.core.seqprover.ProverFactory.makeSelectHypAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IHypAction.IRewriteHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ITranslatableReasonerInput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;
import org.eventb.internal.core.seqprover.ReasonerFailure;

/**
 * Common implementation for reasoners that generate a forward inference with exactly one input hypothesis and
 * mark it as the sole required hypothesis for first forward inference of the sole antecedent of the generated rule.
 * 
 * <p>
 * Such reasoners generate rules that are goal independent, 
 * with exactly one antecedent whose first hyp action
 * is a forward hypothesis. 
 * </p>
 * 
 * <p>
 * Such reasoners are designed to be used in an interactive setting. The required hyp of the forward inference is
 * hidden, and the inferred hypotheses are selected. Subclasses may add more hyp actions by overriding the appropriate
 * method.
 * </p>
 * 
 * @author Farhad Mehta
 * @since 1.0
 */
public abstract class ForwardInfReasoner implements IReasoner {
	
	public static final class Input implements IReasonerInput, ITranslatableReasonerInput {

		private Predicate pred;

		public Input(Predicate pred) {
			this.pred = pred;
		}

		@Override
		public void applyHints(ReplayHints hints) {
			pred = hints.applyHints(pred);
		}

		@Override
		public String getError() {
			return null;
		}

		@Override
		public boolean hasError() {
			return false;
		}

		/**
		 * @since 2.4
		 */
		public Predicate getPred() {
			return pred;
		}

		/**
		 * @since 3.0
		 */
		@Override
		public IReasonerInput translate(FormulaFactory factory) {
			return new Input(pred.translate(factory));
		}

		/**
		 * @since 3.0
		 */
		@Override
		public ITypeEnvironment getTypeEnvironment(FormulaFactory factory) {
			final ITypeEnvironmentBuilder typeEnv = factory
					.makeTypeEnvironment();
			typeEnv.addAll(pred.getFreeIdentifiers());
			return typeEnv;
		}
	}
	
	@Override
	public final void serializeInput(IReasonerInput rInput,
			IReasonerInputWriter writer) throws SerializeException {
		
		// Nothing to do
	}

	@Override
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
		if (! (hypActions.get(0) instanceof IRewriteHypAction)) {
			throw new SerializeException(new IllegalStateException(
			"Expected the first hyp action to be a rewrite action."));
		}

		final IRewriteHypAction rwAction = (IRewriteHypAction) hypActions
				.get(0);

		final Collection<Predicate> requiredHyps = rwAction.getHyps();
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

	@Override
	public final IReasonerOutput apply(IProverSequent seq, IReasonerInput rInput,
			IProofMonitor pm) {
		
		final Input input = (Input) rInput;
		final Predicate pred = input.pred;
		if (pred == null) {
			return new ReasonerFailure(this, input, "Null hypothesis");
		}

		final String display = getDisplay(pred);
		final IRewriteHypAction rewriteAction;
		try {
			rewriteAction = getRewriteAction(seq, pred);
		} catch (IllegalArgumentException e) {
			return new ReasonerFailure(this, input, e.getMessage());
		}
		
		final List<IHypAction> hypActions = new ArrayList<IHypAction>();
		hypActions.add(rewriteAction);
		hypActions.add(makeSelectHypAction(rewriteAction.getInferredHyps()));		
		hypActions.addAll(getAdditionalHypActions(seq, pred));
		
		return ProverFactory.makeProofRule(this, input, display, hypActions);
	}
	
	/**
	 * Return the rewrite action to put in the generated rule, or throw an
	 * <code>IllegalArgumentException</code> in case of reasoner failure. In the
	 * latter case, the message associated to the exception will be returned in
	 * the reasoner failure.
	 * 
	 * @param sequent
	 *            the current sequent
	 * @param pred
	 *            the required predicate for the rewrite action. There is no
	 *            guarantee that this predicate is indeed a hypothesis of the
	 *            given sequent
	 * 
	 * @return the rewrite action of the generated rule
	 * @throws IllegalArgumentException
	 *             if no rewrite action could be generated with the given
	 *             predicate.
	 * @since 3.0
	 */
	protected abstract IRewriteHypAction getRewriteAction (IProverSequent sequent,
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
	 * This method only gets called if {@link #getRewriteAction(IProverSequent, Predicate)} did not
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
