/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.ui.prover;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IUserSupport;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This is the common interface fo providing proof commands to the
 *         proving user interface.
 * @since 1.0
 */
public interface IProofCommand {

	/**
	 * Indicates if the proof command is applicable at the current state or not.
	 * <p>
	 * 
	 * @param us
	 *            the User Support, see {@link org.eventb.core.pm.IUserSupport}.
	 * @param hyp
	 *            the current hypothesis or <code>null</code> if the this is
	 *            checking for goal.
	 * @param input
	 *            the string input to the proof command. It is the input taken
	 *            from the input area in the Proof Control View, or
	 *            <code>null</code> otherwise.
	 * @return <code>true</code> if the proof command is applicable,
	 *         otherwise, return <code>false</code>.
	 */
	public boolean isApplicable(IUserSupport us, Predicate hyp, String input);

	/**
	 * Apply the proof command.
	 * <p>
	 * 
	 * @param us
	 *            the User Support, see {@link org.eventb.core.pm.IUserSupport}.
	 * @param hyp
	 *            the current hypothesis or <code>null</code> if the this is
	 *            apply to goal.
	 * @param inputs
	 *            the inputs of the tactic which are an array of strings, there
	 *            are several possibilities for different types of tactics
	 *            <ul>
	 *            <li>Global tactic: The input is an array contains a single
	 *            string taken from the input area in the Proof Control View.</li>
	 *            <li>Goal/Hypothesis tactics: This inputs only applied to
	 *            instantiate quantified predicates. This is the list of string
	 *            used to instantiate taken from the Proving User Interface.</li>
	 *            </ul>
	 * @param monitor
	 *            a progress monitor.
	 * 
	 * @throws RodinDBException
	 *             when there is a problem applying the command.
	 */
	public void apply(IUserSupport us, Predicate hyp, String[] inputs,
			IProgressMonitor monitor) throws RodinDBException;

}
