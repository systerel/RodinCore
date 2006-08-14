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

import java.util.ArrayList;

/**
 * @author htson
 *         <p>
 *         This class represent the proof tactic dropdown.
 */
public class GlobalTacticDropdownUI {

	String ID;
	
	String toolbar;
	
	ArrayList<GlobalTacticUI> children;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param ID
	 *            The string ID
	 */
	public GlobalTacticDropdownUI(String ID, String toolbar) {
		this.ID = ID;
		this.toolbar = toolbar;
		children = new ArrayList<GlobalTacticUI>();
	}

	/**
	 * Return the string ID.
	 * <p>
	 * 
	 * @return the ID of the dropdown
	 */
	public String getID() {
		return ID;
	}

	public String getToolbar() {
		return toolbar;
	}
	
	public void addChildren(GlobalTacticUI tactic) {
		children.add(tactic);
	}
	
	public ArrayList<GlobalTacticUI> getChildren() {
		return children;
	}
	
}
