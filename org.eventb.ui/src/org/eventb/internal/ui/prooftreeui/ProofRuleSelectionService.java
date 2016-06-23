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

import org.eclipse.core.runtime.ListenerList;
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
public class ProofRuleSelectionService implements IProofTreeSelectionListener {

	private ProofRuleSelectionService() {
		// singleton
	}

	private static final ProofRuleSelectionService INSTANCE = new ProofRuleSelectionService();

	public static ProofRuleSelectionService getInstance() {
		return INSTANCE;
	}

	private static void init() {
		ProofTreeSelectionService.getInstance().addListener(INSTANCE);
	}

	private static void uninit() {
		ProofTreeSelectionService.getInstance().removeListener(INSTANCE);
	}

	private final ListenerList listeners = new ListenerList(ListenerList.IDENTITY);
	private IProofRule currentRule;

	@Override
	public void nodeChanged(IProofTreeNode newNode) {
		final IProofRule rule = newNode.getRule();
		if (rule != currentRule) {
			currentRule = rule;
			fireChange();
		}
	}

	/**
	 * Notify that the currently selected rule has changed.
	 * <p>
	 * This method is intended to be called by classes that are aware of a proof
	 * rule change aside from a proof node selection change.
	 * </p>
	 * 
	 * @param newRule
	 */
	public void ruleChanged(IProofRule newRule) {
		if (newRule != currentRule) {
			currentRule = newRule;
			fireChange();
		}
	}

	/**
	 * Returns the currently selected proof rule.
	 * 
	 * @return a proof rule, or <code>null</code> if none is currently selected
	 */
	public IProofRule getCurrentRule() {
		return currentRule;
	}

	/**
	 * Registers the given listener so that it receives notifications when the
	 * current rule changes. This method has no effect if the same listener is
	 * already registered.
	 * 
	 * @param listener
	 *            a proof rule listener
	 */
	public synchronized void addListener(IProofRuleSelectionListener listener) {
		if (listeners.isEmpty()) {
			init();
		}
		listeners.add(listener);
	}

	/**
	 * Removes the given listener. Has no effect if the same listener was not
	 * already registered.
	 * 
	 * @param listener
	 */
	public synchronized void removeListener(IProofRuleSelectionListener listener) {
		listeners.remove(listener);
		if (listeners.isEmpty()) {
			uninit();
		}
	}

	private void fireChange() {
		// following code recommendation from ListenerList
		final Object[] listenerArray = listeners.getListeners();
		for (int i = 0; i < listenerArray.length; i++) {
			((IProofRuleSelectionListener) listenerArray[i]).ruleChanged(currentRule);
		}
	}

}
