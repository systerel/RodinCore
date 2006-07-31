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

/**
 * @author htson
 *         <p>
 *         This class represent the proof tactic dropdown.
 */
public class GlobalTacticToolbar {

	String ID;
	
	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param ID
	 *            The string ID
	 */
	public GlobalTacticToolbar(String ID) {
		this.ID = ID;
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

}
