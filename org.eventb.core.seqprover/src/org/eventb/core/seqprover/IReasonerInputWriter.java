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

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;

/**
 * Common protocol for serializing a reasoner input object. Serialization is
 * done by registering strings, predicates and expressions with an instance of
 * this interface. Each one is associated to a key chosen by the client.
 * <p>
 * This interface is intended to be implemented by clients.
 * </p>
 * 
 * @author Farhad Mehta
 * @since 1.0
 */
public interface IReasonerInputWriter {

	/**
	 * Serializes the given predicates with the given key.
	 * 
	 * @param key
	 *            key to use
	 * @param predicates
	 *            predicates to serialize
	 * @throws SerializeException
	 */
	void putPredicates(String key, Predicate... predicates)
			throws SerializeException;

	/**
	 * Serializes the given expressions with the given key.
	 * 
	 * @param key
	 *            key to use
	 * @param expressions
	 *            predicates to serialize
	 * @throws SerializeException
	 */
	void putExpressions(String key, Expression... expressions)
			throws SerializeException;

	/**
	 * Serializes the given string with the given key.
	 * 
	 * @param key
	 *            key to use
	 * @param string
	 *            predicates to serialize
	 * @throws SerializeException
	 */
	void putString(String key, String string) throws SerializeException;

}
