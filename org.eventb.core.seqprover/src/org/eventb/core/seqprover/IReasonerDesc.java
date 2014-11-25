/*******************************************************************************
 * Copyright (c) 2009, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover;

/**
 * Common protocol for accessing reasoners and their properties.
 * <p>
 * Instances of this interface can be obtained through the reasoner registry (
 * {@link IReasonerRegistry#getReasonerDesc(String)}).
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author "Nicolas Beauger"
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IReasonerDesc {

	/**
	 * Constant invalid version, meaning that the reasoner is not versioned.
	 */
	public static final int NO_VERSION = -1;

	/**
	 * Returns the id of the described reasoner.
	 * 
	 * @return the id of the described reasoner
	 */
	String getId();

	/**
	 * Returns the name of the reasoner, as written in the extension point.
	 * 
	 * @return the name of the reasoner
	 */
	String getName();

	/**
	 * Returns an instance of the reasoner.
	 * <p>
	 * In case the reasoner has not been registered, or if there is a problem
	 * instantiating the reasoner class, a dummy reasoner instance is returned.
	 * </p>
	 * <p>
	 * A dummy reasoner cannot be replayed. An attempt to call the
	 * <code>apply()</code> method on it will always return an
	 * <code>IReasonerFailure</code>. One can know whether the returned instance
	 * is a dummy one by calling {@link IReasonerRegistry#isDummyReasoner(IReasoner)}.
	 * </p>
	 * 
	 * @return an instance of the reasoner (might be a dummy one in case of
	 *         error)
	 */
	IReasoner getInstance();

	/**
	 * Returns the actual version of the described reasoner.
	 * <p>
	 * The actual version can be different from the registered version (as
	 * returned by the reasoner instance) when the IReasonerDesc was obtained
	 * through proof deserialization, and the reasoner version has changed since
	 * the proof was serialized.
	 * </p>
	 * 
	 * @return a natural integer that is the actual version of the described
	 *         reasoner, or NO_VERSION if the reasoner is not registered or not
	 *         versioned
	 * @see #hasVersionConflict()
	 */
	int getVersion();

	/**
	 * Returns the registered version of the described reasoner.
	 * <p>
	 * When the reasoner instance implements IVersionedReasoner, it is
	 * equivalent to calling <code>getInstance().getVersion()</code>. If the
	 * reasoner is not versioned or not registered, the method returns
	 * NO_VERSION.
	 * </p>
	 * 
	 * @return the registered version of the reasoner, or NO_VERSION
	 */
	int getRegisteredVersion();

	/**
	 * Returns an encoding of both the id and the version of the described
	 * reasoner.
	 * <p>
	 * The encoded version is the actual one, as returned by
	 * {@link #getVersion()}. If the reasoner is not versioned, then the bare id
	 * is returned.
	 * </p>
	 * <p>
	 * The result is intended to be used for reasoner persistence purposes.
	 * </p>
	 * 
	 * @return the String encoding of the id with the version
	 */
	String getVersionedId();

	/**
	 * Returns whether the described reasoner has an actual version different
	 * from the registered version.
	 * 
	 * @return <code>true</code> iff the actual version is different from the
	 *         registered version.
	 */
	boolean hasVersionConflict();

	/**
	 * Returns whether the described reasoner is context dependent.
	 * <p>
	 * A reasoner is context dependent if its output depends on its external
	 * environment: language extensions, additional proof rules, or whatever may
	 * make it produce a different output at different times when applied with
	 * the same input.
	 * </p>
	 * <p>
	 * Context dependent reasoners are never reused directly based on proof
	 * dependencies alone. Instead, their output is checked in every proof where
	 * they appear, and for all applications inside each proof.
	 * </p>
	 * 
	 * @return <code>true</code> iff the reasoner is context dependent
	 * @since 3.0
	 */
	boolean isContextDependent();

	/**
	 * Returns whether the described reasoner is trusted.
	 * <p>
	 * A reasoner is trusted if all of the following hold:
	 * <li>it is registered ( {@link IReasonerRegistry#isRegistered(String)})</li>
	 * <li>it is NOT a dummy reasoner (
	 * {@link IReasonerRegistry#isDummyReasoner(IReasoner)})</li>
	 * <li>it has NO version conflict ({@link #hasVersionConflict()}</li>
	 * </p>
	 * 
	 * @return <code>true</code> if the reasoner is trusted, <code>false</code>
	 *         otherwise
	 * @since 3.1
	 */
	boolean isTrusted();

}
