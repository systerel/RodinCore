/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover;

import java.util.List;

/**
 * Common protocol for tactic combinator descriptors.
 * <p>
 * Tactic combinators are contributed through the 'tacticCombinators' extension
 * point.
 * </p>
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @author Nicolas Beauger
 * @since 2.3
 */
public interface ICombinatorDescriptor {

	/**
	 * Returns the descriptor of this combinator, viewed as a tactic.
	 * <p>
	 * Returned descriptor can NOT be instantiated. Any attempt to call
	 * {@link ITacticDescriptor#getTacticInstance()} on returned object will
	 * throw an {@link UnsupportedOperationException}. Call
	 * {@link #combine(List, String)} instead.
	 * </p>
	 * <p>
	 * All other operations are supported (id, name, description).
	 * </p>
	 * 
	 * @return a tactic descriptor
	 * @since 3.0
	 */
	ITacticDescriptor getTacticDescriptor();

	/**
	 * Returns a descriptor of the tactic combining given tactics.
	 * 
	 * @param tactics
	 *            a list of tactic descriptors
	 * @param id
	 *            the id of the resulting tactic
	 * @return the combined tactic
	 * @throws IllegalArgumentException
	 *             if the size of the given list is not valid regarding
	 *             specified arity
	 * @throws IllegalStateException
	 *             in case there is a problem instantiating the tactic (the
	 *             class given in the extension point cannot be loaded or does
	 *             not implement the right interface)
	 */
	ICombinedTacticDescriptor combine(List<ITacticDescriptor> tactics, String id)
			throws IllegalArgumentException;

	/**
	 * Returns the minimum arity of this combinator. This is the minimum
	 * required size of the list given to {@link #getTacticInstance(List)}.
	 * 
	 * @return an integer greater than or equal to 0
	 */
	int getMinArity();

	/**
	 * Returns whether the arity of this combinator is bound.
	 * <p>
	 * If <code>true</code>, the size of the list given to
	 * {@link #getTacticInstance(List)} must be equal to {@link #getMinArity()}.
	 * </p>
	 * <p>
	 * If <code>false</code>, the size of the list given to
	 * {@link #getTacticInstance(List)} must be greater than or equal to
	 * {@link #getMinArity()}.
	 * </p>
	 * 
	 * @return a boolean
	 */
	boolean isArityBound();
}