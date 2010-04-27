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
package org.eventb.internal.core.ast.extension;

import org.eventb.core.ast.extension.CycleError;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.internal.core.parser.OperatorRegistry;

/**
 * @author Nicolas Beauger
 *
 */
public class PriorityMediator implements IPriorityMediator {

	private final OperatorRegistry registry;
	
	public PriorityMediator(OperatorRegistry registry) {
		this.registry = registry;
	}

	public void addPriority(String leftOpId, String rightOpId) throws CycleError {
		registry.addPriority(leftOpId, rightOpId);
	}

	public void addGroupPriority(String leftGroupId, String rightGroupId)
			throws CycleError {
		registry.addGroupPriority(leftGroupId, rightGroupId);
	}

}
