/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import static java.util.Collections.emptyList;

import java.util.List;

import org.eventb.core.ast.IFormulaInspector;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

/**
 * Abstract class for the new tactic providers handling both hypotheses
 * applications and goal applications.
 * 
 * @see ITacticProvider
 * @author "Thomas Muller"
 */
public abstract class AbstractHypGoalTacticProvider implements ITacticProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.ui.prover.ITacticProvider2#getPossibleApplications(org.eventb
	 * .core.seqprover.IProofTreeNode, org.eventb.core.ast.Predicate,
	 * java.lang.String)
	 */
	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		if (node == null) {
			return emptyList();
		}
		final Predicate goal = node.getSequent().goal();
		final Predicate predicate = (hyp != null) ? hyp : goal;
		return getApplicationsOnPredicate(node, hyp, globalInput, predicate);
	}

	/**
	 * Method that clients should implement in order to give the applications of
	 * a tactic they build for given <code>predicate</code> which is the
	 * hypothesis <code>hyp</code>, or the goal (if the hypothesis
	 * <code>hyp</code> is <code>null</code>). If the positions for applications
	 * are non trivial (e.g. different from <code>IPosition.Root</code>),
	 * clients should return the applications built by an
	 * {@link IFormulaInspector}. See {@link DefaultApplicationInspector}.
	 * 
	 * @param node
	 *            the current proof tree node
	 * @param hyp
	 *            the hypothesis or <code>null</code> if the goal is taken into
	 *            account
	 * 
	 * @param globalInput
	 *            the input for the tactic (taken from the input text in the
	 *            Proof Control View) in case of global tactic
	 * @param predicate
	 *            the predicate to consider : the hypothesis if is not
	 *            <code>null</code> or the goal
	 * @return the list of possible tactic applications
	 */
	protected abstract List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate);

}
