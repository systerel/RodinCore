/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.prover;

import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;

/**
 * This is the common interface to provide tactics to the proving user
 * interface.
 * 
 * @author Nicolas Beauger
 * @since 1.1
 */
public interface ITacticProvider {

	/**
	 * Returns a list of tactic applications according to the given arguments.
	 * <p>
	 * The returned list of tactic applications may be empty if the tactic is
	 * not applicable, but NEVER <code>null</code>. When the tactic is
	 * applicable, the size of the returned list may be:
	 * <ul>
	 * <li>one or more for a local tactic (with target goal or hypothesis or
	 * any)</li>
	 * <li>exactly one for a global tactic (with target global)</li>
	 * </ul>
	 * Tactic providers that fail to respect these rules will be ignored.
	 * </p>
	 * 
	 * @param node
	 *            the current proof tree node
	 * @param hyp
	 *            the hypothesis or <code>null</code> if the goal is in
	 *            consideration
	 * @param globalInput
	 *            the input for the tactic (taken from the input text in the
	 *            Proof Control View) in case of global tactic
	 * @return a (possibly empty) list of tactic applications; never
	 *         <code>null</code>
	 * @since 2.0
	 */
	List<ITacticApplication> getPossibleApplications(IProofTreeNode node,
			Predicate hyp, String globalInput);
}
