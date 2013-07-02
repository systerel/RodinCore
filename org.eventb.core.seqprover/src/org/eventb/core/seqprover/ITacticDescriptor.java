/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - extracted interface
 *******************************************************************************/
package org.eventb.core.seqprover;

/**
 * An {@link ITacticDescriptor} provides a wrapper around the information
 * contained in a tactic extension.
 * 
 * <p>
 * Each tactic extension corresponds to a tactic descriptor. The tactic
 * descriptor for a tactic extension can be obtained using the
 * {@link IAutoTacticRegistry#getTacticDescriptor(String)} method.
 * </p>
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @author Farhad Mehta
 * @since 3.0
 * 
 */
public interface ITacticDescriptor {

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
	 * In case no description is provided for a registered tactic, the empty
	 * string is returned.
	 * </p>
	 * 
	 * @return the description of the tactic extension, or the empty string if
	 *         no description is provided.
	 * 
	 */
	String getTacticDescription();

	/**
	 * Returns whether this descriptor can be instantiated through a call to
	 * {@link #getTacticInstance()}.
	 * 
	 * @return <code>true</code> if instantiable, <code>false</code> otherwise.
	 */
	boolean isInstantiable();

	/**
	 * Returns the singleton instance of the tactic corresponding to the tactic
	 * extension.
	 * <p>
	 * Before requesting an instance, callers must ensure that this descriptor
	 * is instantiable (see {@link #isInstantiable()}). Otherwise, an
	 * {@link UnsupportedOperationException} is thrown. In case there is a
	 * problem instantiating the tactic class, an {@link IllegalStateException}
	 * is thrown.
	 * </p>
	 * 
	 * @return an instance of the tactic
	 * 
	 * @throws UnsupportedOperationException
	 *             if this descriptor is not instantiable
	 * @throws IllegalStateException
	 *             in case there is a problem instantiating the tactic
	 */
	ITactic getTacticInstance();

}