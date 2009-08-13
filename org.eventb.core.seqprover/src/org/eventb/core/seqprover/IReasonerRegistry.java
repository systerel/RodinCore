/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - moved part of behaviour to IReasonerDesc
 *******************************************************************************/
package org.eventb.core.seqprover;

/**
 * Common protocol for accessing the Reasoner Registry.
 * <p>
 * The Reasoner Registry manages Reasoner ids that are known to the Sequent
 * Prover. A reasoner id gets registered if any of the following holds
 * (exhaustive list):
 * <ul>
 * <li>It has been registered through the <code>reasoners</code> extension
 * point.</li>
 * <li>Its name has been requested by way of {@link #getReasonerName()}.</li>
 * <li>Its instance has been requested by way of {@link #getReasonerInstance()}.
 * </li>
 * <li>Its descriptor has been requested by way of {@link #getReasonerDesc()}.</li>
 * </ul>
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Farhad Mehta
 */
public interface IReasonerRegistry {

	/**
	 * Checks if a reasoner extension with the given reasoner id is present in
	 * the reasoner registry.
	 * <p>
	 * This is fully equivalent to
	 * <code>Arrays.asList(getReasonerIDs()).contains(reasonerID)</code>,
	 * although implemented in a more efficient way.
	 * </p>
	 * 
	 * @param reasonerID
	 *            the reasoner id to check for
	 * @return <code>true</code> iff the given reasoner id is known to the
	 *         reasoner registry
	 */
	boolean isRegistered(String reasonerID);
	
	/**
	 * Returns the ids of all reasoner extensions that have been registered.
	 * 
	 * @return an array of all registered reasoner ids
	 */
	String[] getRegisteredIDs();

	/**
	 * Returns an instance of the reasoner extension with the given id.
	 * <p>
	 * In case no reasoner extension with the given id has been registered yet,
	 * or if there is a problem instantiating the reasoner class, a dummy
	 * reasoner instance is returned. Subsequently, the reasoner is considered
	 * as registered (with that dummy instance).
	 * </p>
	 * 
	 * @see #isDummyReasoner(IReasoner)
	 * 
	 * @param reasonerID
	 *            the id of the reasoner
	 * @return an instance of the reasoner (might be a dummy one in case of
	 *         error)
	 * @deprecated use {@link #getReasonerDesc(String)}.getInstance()
	 */
	@Deprecated
	IReasoner getReasonerInstance(String reasonerID);

	/**
	 * Returns the name of the reasoner extension with the given id.
	 * <p>
	 * In case no reasoner extension with the given id has been registered, a
	 * placeholder string is returned, stating the problem. Subsequently, the
	 * reasoner is considered as registered (with a dummy instance).
	 * </p>
	 * 
	 * @param reasonerID
	 *            the id of the reasoner
	 * @return the name of the reasoner with the given id
	 * @deprecated use {@link #getReasonerDesc(String)}.getName()
	 */
	@Deprecated
	String getReasonerName(String reasonerID);

	/**
	 * Checks whether a given reasoner is a dummy reasoner.
	 * <p>
	 * A dummy reasoner is used as a facade to a reasoner that is not currently
	 * installed so that the rule provided by it may be used in the proof tree
	 * and serialized. A dummy reasoner cannot be replayed. An attempt to call
	 * the <code>apply()</code> method on it will always return an
	 * <code>IReasonerFailure</code>.
	 * </p>
	 * 
	 * @param reasoner
	 *            the reasoner to check
	 * @return <code>true</code> iff the given reasoner is a dummy reasoner
	 */
	boolean isDummyReasoner(IReasoner reasoner);

	/**
	 * Returns the descriptor of the reasoner with the given id.
	 * <p>
	 * In case no reasoner extension with the given id has been registered, a
	 * placeholder descriptor is returned. Subsequently, the reasoner is
	 * considered as registered (with a dummy instance).
	 * </p>
	 * <p>
	 * Alternatively, the given parameter can be an encoding of both the id and
	 * the version of a reasoner, as returned by
	 * {@link IReasonerDesc#getVersionedId()}. In this case, the returned
	 * descriptor will bear the encoded version (fetched through
	 * {@link IReasonerDesc#getVersion()}) instead of the registered version.
	 * </p>
	 * 
	 * @param id
	 *            the id of a reasoner; alternatively, the encoding of an id
	 *            with a version
	 * @return a descriptor of the reasoner with the given id
	 */
	IReasonerDesc getReasonerDesc(String id);

}
