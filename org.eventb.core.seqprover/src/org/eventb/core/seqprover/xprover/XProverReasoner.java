/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - extracted a super class to make new API
 *******************************************************************************/
package org.eventb.core.seqprover.xprover;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;

/**
 * Abstract base class for running an external prover as a reasoner.
 * <p>
 * This class implements the {@link IReasoner} interface and provides a skeletal
 * implementation for running an external prover.
 * </p>
 * <p>
 * To implement a reasoner, clients should extend this class and provide two
 * methods:
 * <ul>
 * <li><code>getReasonerId</code> returns the id of the reasoner,</li>
 * <li><code>newProverCall</code> returns a new object encapsulating a run of
 * the external prover on a given sequent</li>
 * </ul>
 * </p>
 * <p>
 * Optionally, one can use an extended reasoner input (rather than the default
 * one). When this is the case, one has to also override the
 * <code>serializeInput</code> and <code>deserializeInput</code> methods. An
 * example of such an extension is described in the unit tests of this plugin
 * (see class
 * <code>org.eventb.core.seqprover.xprover.tests.ExtendedInputTests</code>).
 * </p>
 * 
 * @author François Terrier
 * @author Laurent Voisin
 * @since 1.0
 * @deprecated use {@link XProverReasoner2} instead
 */
@Deprecated
public abstract class XProverReasoner extends AbstractXProverReasoner {

	@Override
	final AbstractXProverCall makeCall(IReasonerInput input,
			ISimpleSequent sequent, IProofMonitor pm) {
		final List<Predicate> hypotheses = new ArrayList<Predicate>();
		Predicate goal = null;
		for (ITrackedPredicate tracked : sequent.getPredicates()) {
			final Predicate pred = tracked.getPredicate();
			if (tracked.isHypothesis()) {
				hypotheses.add(pred);
			} else {
				goal = pred;
			}
		}
		return newProverCall(input, hypotheses, goal, pm);
	}

	/**
	 * Returns a new object encapsulating a run of the external prover with the
	 * given input.
	 * 
	 * @param input
	 *            the input to this reasoner (can contain additional parameters
	 *            specific to the external prover)
	 * @param hypotheses
	 *            hypotheses of the sequent to prove
	 * @param goal
	 *            goal of the sequent to prove
	 * @param pm
	 *            proof monitor (might be <code>null</code>)
	 * @return a new run of the external prover
	 */
	public abstract XProverCall newProverCall(IReasonerInput input,
			Iterable<Predicate> hypotheses, Predicate goal, IProofMonitor pm);

}
