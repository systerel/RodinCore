/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.prover.globaltactics;

import org.eclipse.swt.widgets.ToolItem;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.ui.prover.IGlobalTactic;

/**
 * @author htson
 *         <p>
 *         This class implement the Tool Items in the Proof Control Page (Global
 *         Tactics).
 */
public class GlobalTacticToolItem {

	ToolItem item;

	IGlobalTactic tactic;
	
	boolean interrupt;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param item
	 *            The Tool Item in the Proof Control Page
	 * @param tactic
	 *            The actual Global Tactic
	 */
	public GlobalTacticToolItem(ToolItem item, IGlobalTactic tactic,
			boolean interrupt) {
		this.item = item;
		this.tactic = tactic;
		this.interrupt = interrupt;
	}

	/**
	 * Get the actual Tool Item
	 * <p>
	 * 
	 * @return The Tool Item associated with this.
	 */
	public ToolItem getToolItem() {
		return item;
	}

	/**
	 * Update the status of the tool item according to the current proof tree
	 * node and the optional string input.
	 * <p>
	 * 
	 * @param node
	 *            the current proof tree node
	 * @param input
	 *            the (optional) string input
	 */
	public void updateStatus(IProofTreeNode node, String input) {
		item.setEnabled(tactic.isApplicable(node, input));
	}

	/**
	 * Get the actual global tactic.
	 * <p>
	 * 
	 * @return the global tactic associated with this.
	 */
	public IGlobalTactic getTactic() {
		return tactic;
	}

	public boolean isInterruptable() {
		return interrupt;
	}
	
}
