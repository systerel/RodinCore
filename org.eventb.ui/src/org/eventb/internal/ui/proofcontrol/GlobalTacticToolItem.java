/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored to use ITacticProvider2 and ITacticApplication
 *******************************************************************************/
package org.eventb.internal.ui.proofcontrol;

import org.eclipse.swt.widgets.ToolItem;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.prover.TacticUIRegistry;

/**
 * @author htson
 *         <p>
 *         This class implement the Tool Items in the Proof Control Page (Global
 *         Tactics).
 */
public class GlobalTacticToolItem {

	static final TacticUIRegistry registry = TacticUIRegistry.getDefault();

	ToolItem item;

	String tacticID;

	boolean interrupt;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param item
	 *            The Tool Item in the Proof Control Page
	 * @param tacticID
	 *            The actual Global Tactic
	 */
	public GlobalTacticToolItem(ToolItem item, String tacticID,
			boolean interrupt) {
		this.item = item;
		this.tacticID = tacticID;
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
	 * @param input
	 *            the (optional) string input
	 */
	public void updateStatus(IUserSupport us, String input) {
		item.setEnabled(shouldBeEnabled(us, input));
	}

	private boolean shouldBeEnabled(IUserSupport us, String input) {
		final Object application = registry.getGlobalApplication(tacticID, us,
				input);
		return application != null;
	}

	/**
	 * Get the actual global tactic.
	 * <p>
	 * 
	 * @return the global tactic associated with this.
	 */
	public String getTactic() {
		return tacticID;
	}

	public boolean isInterruptable() {
		return interrupt;
	}

}
