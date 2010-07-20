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
	 * Left operator gets a higher priority than right operator.
	 * </p>
	 * 
	 * @param leftOpId
	 *            an operator id
	 * @param rightOpId
	 *            an operator id
	 * @throws CycleError
	 *             if adding the given priority introduces a cycle
	 */
	void addPriority(String leftOpId, String rightOpId) throws CycleError;

	/**
	 * Adds a priority between groups of given ids.
	 * 
	 * @param leftGroupId
	 *            a group id
	 * @param rightGroupId
	 *            a group id
	 * @throws CycleError
	 *             if adding the given priority introduces a cycle
	 */
	void addGroupPriority(String leftGroupId, String rightGroupId)
			throws CycleError;
}
