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
package org.eventb.internal.ui.prooftreeui;

import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofTreeNode;

/**
 * Service that maintains a current selection of proof rule.
 * <p>
 * Classes that want to be notified shall implement
 * {@link IProofRuleSelectionListener} and call
 * {@link #addListener(IProofRuleSelectionListener)}.
 * </p>
 * 
 * @author beauger
 * @see IProofRuleSelectionListener
 */
public class ProofRuleSelectionService extends AbstractSelectionService<IProofRule, IProofRuleSelectionListener> implements IProofTreeSelectionListener {

	private ProofRuleSelectionService() {
		// singleton
	}

	private static final ProofRuleSelectionService INSTANCE = new ProofRuleSelectionService();

	public static ProofRuleSelectionService getInstance() {
		return INSTANCE;
	}

	@Override
	protected void startListening() {
		ProofTreeSelectionService.getInstance().addListener(INSTANCE);
	}

	@Override
	protected void stopListening() {
		ProofTreeSelectionService.getInstance().removeListener(INSTANCE);
	}

	@Override
	protected void notifyChange(IProofRuleSelectionListener listener, IProofRule newRule) {
		listener.ruleChanged(newRule);		
	}

	@Override
	public void nodeChanged(IProofTreeNode newNode) {
		ruleChanged(newNode.getRule());
	}

	/**
	 * Notify that the currently selected rule has changed.
	 * <p>
	 * This method is intended to be called by classes that are aware of a proof
	 * rule change, aside from a proof node selection change.
	 * </p>
	 * 
	 * @param newRule
	 */
	public void ruleChanged(IProofRule newRule) {
		currentChanged(newRule);
	}

}
