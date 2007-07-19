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

import org.eventb.core.seqprover.ITactic;

/**
 * @author htson
 *         <p>
 *         This defines the general interface for a tactic container.
 *         <ul>
 *         <li>This contains a sequence of tactics that are composed
 *         sequentially into a single tactic.
 *         <li>There is a flag for enable/disable the tactic.
 *         </ul>
 */
public interface ITacticContainer {

	/**
	 * Enable/Disable the tactic container.
	 * <p>
	 * 
	 * @param enable
	 *            <code>true</code> to enable the container <code>false</code>
	 *            to disable the container
	 */
	public void setEnable(boolean enable);

	/**
	 * Check if the tactic container is currently enable or not.
	 * <p>
	 * 
	 * @return <code>true</code> if enable, <code>false</code> otherwise.
	 */
	public boolean isEnable();

	/**
	 * Return the tactic that is the sequential composition of the contained
	 * tactics.
	 * <p>
	 * 
	 * @return a tactic {@link ITactic}.
	 */
	public ITactic getTactic();

	/**
	 * Set the contained tactics to be an array of tactics ({@link ITactic}).
	 * <p>
	 * 
	 * @param tacticIDs
	 *            an array of tactics ({@link ITactic}).
	 */
	public void setTactics(String... tacticIDs);

	public ITacticContainerRegistry getTacticContainerRegistry();
}