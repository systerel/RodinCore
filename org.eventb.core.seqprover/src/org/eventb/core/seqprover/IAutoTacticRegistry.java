/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added parameterized tactics
 *     Systerel - added tactic combinators
 *******************************************************************************/
package org.eventb.core.seqprover;

/**
 * Common protocol for accessing the Auto Tactic Registry.
 * <p>
 * The Auto Tactic Registry manages tactic ids that are known to the Sequent
 * Prover. A tactic id is registered only if it has been registered through the
 * <code>autoTactics</code> extension point.
 * </p>
 * 
 * <p>
 * This registry is not used by the sequent prover itself, but is provided as a
 * service to clients that want to register and retrieve auto tactics.
 * </p>
 * <p>
 * An auto tactic is a tactic that does not depend on user inputs. All auto
 * tactics that are registered using this registry are stateless. They are
 * constructed using the default constructor.
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
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IAutoTacticRegistry {

	/**
	 * Checks if a tactic extension with the given tactic id is present in the
	 * auto tactic registry.
	 * <p>
	 * This is fully equivalent to
	 * <code>Arrays.asList(getTacticIDs()).contains(tacticID)</code>, although
	 * implemented in a more efficient way.
	 * </p>
	 * 
	 * @param tacticID
	 *            the tactic id to check for
	 * @return <code>true</code> iff the given tactic id is known to the tactic
	 *         registry
	 */
	boolean isRegistered(String tacticID);

	/**
	 * Returns the ids of all tactic extensions that have been registered.
	 * <p>
	 * The order of the returned array is of no significance and may change
	 * across multiple calls of the method.
	 * </p>
	 * 
	 * @return an array of all registered tactic ids
	 */
	String[] getRegisteredIDs();

	/**
	 * Returns the tactic descriptor of the tactic extension with the given id.
	 * <p>
	 * Clients should first use {@link #isRegistered(String)} to check if a
	 * tactic extension with the given id is registered. In case no tactic
	 * extension with the given id has been registered a placeholder is
	 * returned. Place holder tactics always fail.
	 * </p>
	 * 
	 * @param tacticID
	 *            the id of the tactic
	 * @return the tactic descriptor of the tactic with the given id
 	 * @since 3.0
	 */
	ITacticDescriptor getTacticDescriptor(String tacticID)
			throws IllegalArgumentException;

	/**
	 * Returns whether the given id corresponds to a registered tactic
	 * parameterizer.
	 * 
	 * @param id
	 *            a parameterizer id
	 * @return <code>true</code> if registered, <code>false</code> otherwise
	 * @since 3.0
	 */
	boolean isRegisteredParameterizer(String id);

	/**
	 * Returns descriptors for all registered tactic parameterizers.
	 * <p>
	 * The order of the returned array is of no significance and may change
	 * across multiple calls of the method.
	 * </p>
	 * 
	 * @return an array of instantiators
	 * @return
	 * @since 2.3
	 */
	IParameterizerDescriptor[] getParameterizerDescriptors();

	/**
	 * Returns parameterizer descriptor with given id, or a placeholder if
	 * it is not registered.
	 * 
	 * @param id
	 *            a parameterizer id
	 * @return a parameterizer descriptor
	 * @since 2.3
	 */
	IParameterizerDescriptor getParameterizerDescriptor(String id);

	/**
	 * Returns whether the given id corresponds to a registered tactic
	 * combinator.
	 * 
	 * @param id
	 *            a combinator id
	 * @return <code>true</code> if registered, <code>false</code> otherwise
	 * @since 3.0
	 */
	boolean isRegisteredCombinator(String id);

	/**
	 * Returns descriptors for all registered tactic combinators.
	 * <p>
	 * The order of the returned array is of no significance and may change
	 * across multiple calls of the method.
	 * </p>
	 * 
	 * @return an array of combinator descriptors
	 * @since 2.3
	 */
	ICombinatorDescriptor[] getCombinatorDescriptors();

	/**
	 * Returns combinator descriptor with given id, or a placeholder if it
	 * is not registered.
	 * 
	 * @param id
	 *            a combinator id
	 * @return a combinator descriptor
	 * @since 2.3
	 */
	ICombinatorDescriptor getCombinatorDescriptor(String id);

	/**
	 * Returns all available dynamically provided tactics.
	 * 
	 * @return an array of tactic descriptors
	 * @since 3.0
	 */
	ITacticDescriptor[] getDynTactics();

}
