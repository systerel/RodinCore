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

import java.util.List;

import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;

/**
 * Common protocol for combined tactic descriptors.
 * <p>
 * Instances of this interface are the result of
 * {@link ICombinatorDescriptor#combine(List, String)}.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 2.3
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @see ICombinatorDescriptor
 */
public interface ICombinedTacticDescriptor extends ITacticDescriptor {

	/**
	 * Returns the id of the combinator used to make the described tactic.
	 * 
	 * @return a combinator id
	 */
	String getCombinatorId();

	/**
	 * Returns the list of combined tactics.
	 * 
	 * @return a list of tactic descriptors
	 */
	List<ITacticDescriptor> getCombinedTactics();
}