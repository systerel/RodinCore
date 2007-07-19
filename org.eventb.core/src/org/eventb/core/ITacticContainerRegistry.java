/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 *******************************************************************************/

package org.eventb.core;

/**
 * @author htson
 *         <p>
 *         This is a default interface for tactic registry that can be use for
 *         post-tactics, auto-tactics. (@see ITacticRegistry)
 */
public interface ITacticContainerRegistry {

	/**
	 * Checked if a tactic is declared for using with the tactic container.
	 * <p>
	 * 
	 * @param tacticID
	 *            a tactic ID.
	 * @return <code>true</code> if the tactic can be used with the tactic
	 *         container. Return <code>false</code> otherwise.
	 */
	public abstract boolean isDeclared(String tacticID);

	/**
	 * Return the set of tactic that are declared for using with the tactic
	 * container.
	 * <p>
	 * 
	 * @return array of tactic IDs.
	 */
	public abstract String[] getTacticIDs();

	/**
	 * Return the set of tactic that are declared for using as default with the
	 * tactic container.
	 * <p>
	 * 
	 * @return array of tactic IDs.
	 */
	public abstract String[] getDefaultTacticIDs();

}