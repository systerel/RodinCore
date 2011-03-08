/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover;

/**
 * Common protocol for accessing the Auto Tactic Registry.
 * <p>
 * The Auto Tactic Registry manages tactic ids that are known to the Sequent
 * Prover. A tactic id is registered only if it has been registered through 
 * the <code>autoTactics</code> extension point.
 * </p>
 * 
 * <p>
 * This registry is not used by the sequent prover itself, but is provided as a service
 * to clients that want to register and retrieve auto tactics.
 * </p>
 * <p>
 * An auto tactic is a tactic that does not depend on user inputs.
 * All auto tactics that are registered using this registry are stateless. They are constructed using the
 * default constructor. 
 * </p>
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see SequentProver#getAutoTacticRegistry()
 * 
 * @author Farhad Mehta
 * @since 1.0
 */
public interface IAutoTacticRegistry {

	/**
	 * Checks if a tactic extension with the given tactic id is present in
	 * the auto tactic registry.
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
	 * Returns the tactic descriptor of the tactic extension with the given id.
	 * <p>
	 * Clients should first use {@link #isRegistered(String)} to check if a tactic extension with the
	 * given id is registered.
	 * In case no tactic extension with the given id has been registered an {@link IllegalArgumentException}
	 * is thrown.
	 * </p>
	 * 
	 * @param tacticID
	 *            the id of the tactic
	 * @return the tactic descriptor of the tactic with the given id
	 * 
	 * @throws IllegalArgumentException
	 * 			in case no tactic extension with the given id has been registered
	 */
	ITacticDescriptor getTacticDescriptor(String tacticID) throws IllegalArgumentException;

	
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
	 * 
	 * @deprecated use {@link #getTacticDescriptor(String))} and the methods in {@link ITacticDescriptor} instead.
	 */
	@Deprecated
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
	 * 
	 * @deprecated use {@link #getTacticDescriptor(String))} and the methods in {@link ITacticDescriptor} instead.
	 */
	@Deprecated
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
	 * @deprecated use {@link #getTacticDescriptor(String))} and the methods in {@link ITacticDescriptor} instead.
	 */ 
	@Deprecated
	String getTacticDescription(String tacticID) throws IllegalArgumentException;	
	
	
	
	/**
	 * An {@link ITacticDescriptor} provides a wrapper around the information contained in 
	 * a tactic extension.
	 * 
	 * <p>
	 * Each tactic extension corresponds to a tactic descriptor.
	 * The tactic descriptor for a tactic extension can be obtained using the 
	 * {@link IAutoTacticRegistry#getTacticDescriptor(String)} method. 
	 * </p>
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public interface ITacticDescriptor{
		
		/**
		 * Returns the id of the tactic extension.
		 * 
		 * @return the id of the tactic extension
		 */
		String getTacticID();
		
		/**
		 * Returns the name of the tactic extension.
		 * 
		 * @return the name of the tactic extension
		 */
		String getTacticName();
		
		/**
		 * Returns the description of the tactic extension.
		 * 
		 * <p>
		 * In case no description is provided for a registered tactic, the empty string is returned.
		 * </p>
		 * 
		 * @return the description of the tactic with the given id, or the empty string if no description is provided.
		 * 
		 */
		String getTacticDescription();

		/**
		 * Returns the singeleton instance of the tactic corresponding to the tactic extension.
		 * <p>
		 * In case there is a problem instantiating the tactic class, an {@link IllegalArgumentException}
		 * is thrown.
		 * </p>
		 * 
		 * @return an instance of the tactic
		 * 
		 * @throws IllegalArgumentException
		 * 			in case there is a problem instantiating the tactic
		 */
		ITactic getTacticInstance() throws IllegalArgumentException;
		
	}
	
	

}
