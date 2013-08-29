/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.preferences;

import org.eventb.core.preferences.autotactics.IInjectLog;

/**
 * This interface is used to translate elements of a complex string preference
 * such as:
 * <ul>
 * <li>list elements</li>
 * <li>map elements</li>
 * <li>...</li>
 * </ul>
 * to their object equivalent and vice-versa.
 * 
 * @param <U>
 *            type that this interface will encapsulate
 * @since 2.1
 */
public interface IPrefElementTranslator<U> {

	/**
	 * Returns the string value of the given element which is significant for
	 * its storage in a preference.
	 * 
	 * @param u
	 *            the element to be stored in a preference
	 * @return a string corresponding to the element given as parameter
	 */
	String extract(U u);

	/**
	 * Returns the value created from the given string parameter.
	 * 
	 * @param s
	 *            the information to create the element
	 * @param log
	 *            a log of injection problems
	 * @return the element created from the given information
	 * @since 3.0
	 */
	U inject(String s, IInjectLog log);

}