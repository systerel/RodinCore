/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import static org.eventb.core.EventBAttributes.TARGET_ATTRIBUTE;
import static org.eventb.core.sc.GraphProblem.ParameterNameImportConflictWarning;

import org.eventb.core.IRefinesMachine;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.state.ISymbolWarning;
import org.rodinp.core.RodinDBException;

/**
 * Produces a warning for a name conflict with a parameter of an abstract event.
 * 
 * @author Laurent Voisin
 */
public class AbstractParameterWarning implements ISymbolWarning {

	private static final GraphProblem PROBLEM = ParameterNameImportConflictWarning;

	private IRefinesMachine refinesClause;
	private Object[] args;

	public AbstractParameterWarning(IRefinesMachine refinesClause,
			String paramName, String eventName) {
		this.refinesClause = refinesClause;
		this.args = new String[] { paramName, eventName };
	}

	@Override
	public void createConflictWarning(IMarkerDisplay markerDisplay)
			throws RodinDBException {
		markerDisplay.createProblemMarker(refinesClause, TARGET_ATTRIBUTE,
				PROBLEM, args);
	}

	@Override
	public String toString() {
		return PROBLEM.getLocalizedMessage(args);
	}

}
