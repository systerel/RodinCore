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
import org.eventb.internal.core.parser.AbstractGrammar;

/**
 * @author Nicolas Beauger
 *
 */
public class PriorityMediator implements IPriorityMediator {

	private final AbstractGrammar grammar;
	
	public PriorityMediator(AbstractGrammar grammar) {
		this.grammar = grammar;
	}

	public void addPriority(String lowOpId, String highOpId) throws CycleError {
		grammar.addPriority(lowOpId, highOpId);
	}

	public void addGroupPriority(String lowGroupId, String highGroupId)
			throws CycleError {
		grammar.addGroupPriority(lowGroupId, highGroupId);
	}

}
