/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover;

import java.util.Collection;

/**
 * Common protocol for parameter valuations.
 * 
 * @author Nicolas Beauger
 * @since 2.3
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IParameterValuation {

	/**
	 * Returns descriptors for parameters of this valuation.
	 * 
	 * @return a collection of parameter descriptors
	 */
	Collection<IParameterDesc> getParameterDescs();

	/**
	 * Returns the boolean value of the parameter with the given label.
	 * 
	 * @param label
	 *            a parameter label
	 * @return a boolean
	 * @throws IllegalArgumentException
	 *             if the given label is unknown or does not refer to a boolean
	 *             parameter
	 */
	boolean getBoolean(String label);

	/**
	 * Returns the integer value of the parameter with the given label.
	 * 
	 * @param label
	 *            a parameter label
	 * @return an integer
	 * @throws IllegalArgumentException
	 *             if the given label is unknown or does not refer to an integer
	 *             parameter
	 */
	int getInt(String label);

	/**
	 * Returns the long value of the parameter with the given label.
	 * 
	 * @param label
	 *            a parameter label
	 * @return a long
	 * @throws IllegalArgumentException
	 *             if the given label is unknown or does not refer to a long
	 *             parameter
	 */
	long getLong(String label);

	/**
	 * Returns the string value of the parameter with the given label.
	 * 
	 * @param label
	 *            a parameter label
	 * @return a string
	 * @throws IllegalArgumentException
	 *             if the given label is unknown or does not refer to a string
	 *             parameter
	 */
	String getString(String label);

	/**
	 * Returns the untyped value of the parameter with the given label.
	 * <p>
	 * Returned value has the java type corresponding to one of the possible
	 * parameter values.
	 * </p>
	 * 
	 * @param label
	 *            a parameter label
	 * @return a value
	 * @throws IllegalArgumentException
	 *             if the given label is unknown
	 */
	Object get(String label);
}
