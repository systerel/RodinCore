/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.indexer;

import org.eventb.core.EventBAttributes;
import org.eventb.core.IAssignmentElement;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.IParseResult;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IIndexingToolkit;

/**
 * @author Nicolas Beauger
 * 
 */
public class AssignmentIndexer extends ElementIndexer {
	
	final IAssignmentElement element;

	public AssignmentIndexer(IAssignmentElement element,
			SymbolTable eventSpecificTable) {

		super(eventSpecificTable);
		this.element = element;
	}

	public void process(IIndexingToolkit index) throws RodinDBException {
		if (!isValid(element, EventBAttributes.ASSIGNMENT_ATTRIBUTE)) {
			return;
		}
		final String assignmentString = element.getAssignmentString();
		IParseResult result = ff.parseAssignment(assignmentString);
		if (!result.isSuccess()) {
			return;
		}
		final Assignment assign = result.getParsedAssignment();
		visitAndIndex(element, EventBAttributes.ASSIGNMENT_ATTRIBUTE, assign, index);
	}

}
