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


/**
 * Common protocol for accessing tactic parameters.
 * 
 * TODO mediator 
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * 
 * @author Nicolas Beauger
 * @since 2.3
 */
public interface IParameterSetting extends IParameterValuation {

	/**
	 * Set the parameter with the given label to the given value.
	 * 
	 * @param label
	 *            a parameter label
	 * @param value
	 *            the new value
	 * @throws IllegalArgumentException
	 *             if the label does not exist or is not a boolean
	 */
	void setBoolean(String label, Boolean value);

	/**
	 * Set the parameter with the given label to the given value.
	 * 
	 * @param label
	 *            a parameter label
	 * @param value
	 *            the new value
	 * @throws IllegalArgumentException
	 *             if the label does not exist or is not an integer
	 */
	void setInt(String label, Integer value);

	/**
	 * Set the parameter with the given label to the given value.
	 * 
	 * @param label
	 *            a parameter label
	 * @param value
	 *            the new value
	 * @throws IllegalArgumentException
	 *             if the label does not exist or is not a long
	 */
	void setLong(String label, Long value);

	/**
	 * Set the parameter with the given label to the given value.
	 * 
	 * @param label
	 *            a parameter label
	 * @param value
	 *            the new value
	 * @throws IllegalArgumentException
	 *             if the label does not exist or is not a string
	 */
	void setString(String label, String value);

	/**
	 * Set the parameter with the given label to the given value. The given
	 * value must be of the type that corresponds to the given label.
	 * 
	 * @param label
	 *            a parameter label
	 * @param value
	 *            the new value
	 */
	void set(String label, Object value);
}
