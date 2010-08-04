/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.extension;

import java.util.Map;
import java.util.Set;

/**
 * Common protocol for operator groups.
 * <p>
 * Instances of this interface are provided by {@link IGrammar#getGroups()} and
 * related methods. The purpose is to give a view of an operator group. Objects
 * returned by the various methods are not intended to be modified. Anyway,
 * modifications would have no effect on the described operator group.
 * </p>
 * 
 * @since 2.0
 * @author Nicolas Beauger
 * @see IOperator
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IOperatorGroup {

	/**
	 * Returns the id of this operator group.
	 * 
	 * @return a String identifier
	 */
	String getId();

	/**
	 * Returns a set of all operators in this group.
	 * 
	 * @return a set of operators
	 * @see IOperator
	 */
	Set<IOperator> getOperators();

	/**
	 * Returns a map describing the priorities between operators of this group.
	 * <p>
	 * The meaning is that the key operator has a lower priority than all
	 * operators in the value set.
	 * </p>
	 * 
	 * @return a map representation of operator priorities
	 * @see IPriorityMediator#addPriority(String, String)
	 */
	Map<IOperator, Set<IOperator>> getPriorities();

	/**
	 * Returns a map describing the compatibilities between operators of this
	 * group.
	 * <p>
	 * The meaning is that the key operator is compatible all operators in the
	 * value set.
	 * </p>
	 * 
	 * @return a map representation of operator compatibilities
	 * @see ICompatibilityMediator#addCompatibility(String, String)
	 */
	Map<IOperator, Set<IOperator>> getCompatibilities();

	/**
	 * Returns a set of all associative operators in this group.
	 * 
	 * @return a set of operators
	 * @see ICompatibilityMediator#addAssociativity(String)
	 */
	Set<IOperator> getAssociativeOperators();

}