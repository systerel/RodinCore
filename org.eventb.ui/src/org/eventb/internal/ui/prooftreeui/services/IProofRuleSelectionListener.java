/*******************************************************************************
 * Copyright (c) 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prooftreeui.services;

import org.eventb.core.seqprover.IProofRule;

/**
 * Common protocol for classes that react when the selected proof rule changes.
 * <p>
 * Such classes shall register with the {@link ProofRuleSelectionService}.
 * </p>
 * 
 * @author beauger
 * @see ProofRuleSelectionService
 */
public interface IProofRuleSelectionListener {

	/**
	 * Notification that the current proof rule has changed.
	 * 
	 * @param newRule
	 *            the new proof rule, or <code>null</code> if none is selected
	 */
	void ruleChanged(IProofRule newRule);

}
