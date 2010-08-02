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

/**
 * @author Nicolas Beauger
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 */
public interface IPriorityMediator {

	/**
	 * Adds a priority between operators of given ids.
	 * <p>
	 * lowOpId operator gets a lower priority than highOpId operator.
	 * </p>
	 * <p>
	 * Stating that operator op1 has lower priority than operator op2 means
	 * that:
	 * <li><b>a op1 b op2 c</b> is parsed <b>a op1 (b op2 c)</b></li>
	 * <li><b>a op2 b op1 c</b> is parsed <b>(a op2 b) op1 c</b></li>
	 * <li>parentheses are required for parsing <b>(a op1 b) op2 c</b> and <b>a
	 * op2 (b op1 c)</b></li>
	 * </p>
	 * 
	 * @param lowOpId
	 *            an operator id
	 * @param highOpId
	 *            an operator id
	 * @throws CycleError
	 *             if adding the given priority introduces a cycle
	 */
	void addPriority(String lowOpId, String highOpId) throws CycleError;

	/**
	 * Adds a priority between groups of given ids.
	 * <p>
	 * lowGroupId group gets a lower priority than highGroupId group.
	 * </p>
	 * <p>
	 * Stating that group gr1 has lower priority than group gr2 means that every
	 * operator in gr1 has lower priority than any operator in gr2.
	 * </p>
	 * 
	 * @param lowGroupId
	 *            a group id
	 * @param highGroupId
	 *            a group id
	 * @throws CycleError
	 *             if adding the given priority introduces a cycle
	 */
	void addGroupPriority(String lowGroupId, String highGroupId)
			throws CycleError;
}
