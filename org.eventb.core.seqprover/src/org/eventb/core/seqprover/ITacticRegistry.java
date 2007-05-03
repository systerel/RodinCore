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
 * Prover. A tactic id gets registered only if it has been registered through 
 * the <code>tactics</code> extension point.
 * </p>
 * 
 * <p>
 * This registry is not used by the sequent prover iteself, but is provided as a service
 * to clients that want to register and retreive stateless tactics.
 * </p>
 * <p>
 * The tactics that are registered using this registry are stateless. They are constructed using the
 * default constructor. 
 * </p>
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
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
	 * <p>
	 * The order of the returned array is of no significance and may change across
	 * multiple calls of the method.
	 * </p>
	 * 
	 * @return an array of all registered tactic ids
	 */
	String[] getRegisteredIDs();

	/**
	 * Returns an instance of the tactic extension with the given id.
	 * <p>
	 * Clients should first use {@link #isRegistered(String)} to check if a tactic extension with the
	 * given id is registered.
	 * In case no tactic extension with the given id has been registered yet,
	 * or if there is a problem instantiating the tactic class, an {@link IllegalArgumentException}
	 * is thrown.
	 * </p>
	 *  
	 * @param tacticID
	 *            the id of the tactic
	 * @return an instance of the tactic
	 * 
	 * @throws IllegalArgumentException
	 * 			in case no tactic extension with the given id has been registered
	 */
	ITactic getTacticInstance(String tacticID) throws IllegalArgumentException;

	/**
	 * Returns the name of the tactic extension with the given id.
	 * <p>
	 * Clients should first use {@link #isRegistered(String)} to check if a tactic extension with the
	 * given id is registered.
	 * In case no tactic extension with the given id has been registered an {@link IllegalArgumentException}
	 * is thrown.
	 * </p>
	 * 
	 * 
	 * @param tacticID
	 *            the id of the tactic
	 * @return the name of the tactic with the given id
	 * 
	 * @throws IllegalArgumentException
	 * 			in case no tactic extension with the given id has been registered
	 */
	String getTacticName(String tacticID) throws IllegalArgumentException;
	
	/**
	 * Returns the description of the tactic extension with the given id.
	 * <p>
	 * Clients should first use {@link #isRegistered(String)} to check if a tactic extension with the
	 * given id is registered.
	 * In case no tactic extension with the given id has been registered an {@link IllegalArgumentException}
	 * is thrown.
	 * In case no description is provided for a registered tactic, the empty string is returned.
	 * </p>
	 * 
	 * 
	 * @param tacticID
	 *            the id of the tactic
	 * @return the description of the tactic with the given id, or the empty string if no description is provided.
	 * 
	 * @throws IllegalArgumentException
	 * 			in case no tactic extension with the given id has been registered
	 */
	String getTacticDescription(String tacticID) throws IllegalArgumentException;
	
	

}
