/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.seqprover;

/**
 * Common protocol for accessing the Tactic Registry.
 * <p>
 * The Tactic Registry manages Tactic ids that are known to the Sequent
 * Prover. A tactic id gets registered if any of the following hold
 * (exhaustive list):
 * <ul>
 * <li>It has been registered through the <code>tactics</code> extension
 * point.</li>
 * <li>Its name has been requested by way of {@link #getTacticName()}.</li>
 * <li>Its instance has been requested by way of {@link #getTacticInstance()}.</li>
 * </ul>
 * </p>
 * <p>
 * This interface is similar to {@link IReasonerRegistry}.
 * </p>
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * TODO : see if a dummy tactic is really needed in this setting.
 * 
 * @author Farhad Mehta
 */
public interface ITacticRegistry {

	/**
	 * Checks if a tactic extension with the given tactic id is present in
	 * the tactic registry.
	 * <p>
	 * This is fully equivalent to
	 * <code>Arrays.asList(getTacticIDs()).contains(tacticID)</code>,
	 * although implemented in a more efficient way.
	 * </p>
	 * 
	 * @param tacticID
	 *            the tactic id to check for
	 * @return <code>true</code> iff the given tactic id is known to the
	 *         tactic registry
	 */
	boolean isRegistered(String tacticID);
	
	/**
	 * Returns the ids of all tactic extensions that have been registered.
	 * 
	 * @return an array of all registered tactic ids
	 */
	String[] getRegisteredIDs();

	/**
	 * Returns an instance of the tactic extension with the given id.
	 * <p>
	 * In case no tactic extension with the given id has been registered yet,
	 * or if there is a problem instantiating the tactic class, a dummy
	 * tactic instance is returned. Subsequently, the tactic is considered
	 * as registered (with that dummy instance).
	 * </p>
	 * 
	 * @see #isDummyTactic(ITactic)
	 * 
	 * @param tacticID
	 *            the id of the tactic
	 * @return an instance of the tactic (might be a dummy one in case of
	 *         error)
	 */
	ITactic getTacticInstance(String tacticID);

	/**
	 * Returns the name of the tactic extension with the given id.
	 * <p>
	 * In case no tactic extension with the given id has been registered, a
	 * placeholder string is returned, stating the problem. Subsequently, the
	 * tactic is considered as registered (with a dummy instance).
	 * </p>
	 * 
	 * @param tacticID
	 *            the id of the tactic
	 * @return the name of the tactic with the given id
	 */
	String getTacticName(String tacticID);

	/**
	 * Checks whether a given tactic is a dummy tactic.
	 * <p>
	 * A dummy tactic is used as a facade to a tactic that is not currently
	 * installed. An attempt to call
	 * the <code>apply()</code> method on it will have no effect on a proof tree.
	 * </p>
	 * 
	 * TODO : see if a dummy tactic is really needed in this setting.
	 * 
	 * @param tactic
	 *            the tactic to check
	 * @return <code>true</code> iff the given tactic is a dummy tactic
	 */
	boolean isDummyTactic(ITactic reasoner);

}
