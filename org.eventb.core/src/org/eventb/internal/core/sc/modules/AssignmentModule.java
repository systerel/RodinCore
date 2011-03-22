/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - ensure that all AST problems are reported
 *     Systerel - mathematical language V2
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import static org.eventb.core.ast.LanguageVersion.V2;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IAssignmentElement;
import org.eventb.core.ISCAssignmentElement;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IResult;
import org.eventb.core.sc.GraphProblem;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;

/**
 * @author Stefan Hallerstede
 * 
 */
public abstract class AssignmentModule<I extends IInternalElement> extends
		LabeledFormulaModule<Assignment, I> {

	@Override
	protected IAttributeType.String getFormulaAttributeType() {
		return EventBAttributes.ASSIGNMENT_ATTRIBUTE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.internal.core.sc.modules.LabeledFormulaModule#parseFormula
	 * (int, org.rodinp.core.IInternalElement[], org.eventb.core.ast.Formula[],
	 * java.util.Collection, org.eventb.core.ast.FormulaFactory)
	 */
	@Override
	protected Assignment parseFormula(I formulaElement,
			Collection<FreeIdentifier> freeIdentifierContext,
			FormulaFactory factory) throws CoreException {

		IAssignmentElement assignmentElement = (IAssignmentElement) formulaElement;
		IAttributeType.String attributeType = getFormulaAttributeType();
		
		if (!assignmentElement.hasAssignmentString()) {
			createProblemMarker(assignmentElement, attributeType,
					GraphProblem.AssignmentUndefError);
			return null;
		}

		String assignmentString = assignmentElement.getAssignmentString();

		// parse the assignment

		IParseResult parseResult = factory.parseAssignment(assignmentString, V2, assignmentElement);

		if (issueASTProblemMarkers(assignmentElement, attributeType,
				parseResult)) {
			return null;
		}

		Assignment assignment = parseResult.getParsedAssignment();

		// check legibility of the predicate
		// (this will only produce a warning on failure)

		IResult legibilityResult = assignment.isLegible(freeIdentifierContext);
		if (issueASTProblemMarkers(assignmentElement, attributeType,
				legibilityResult)) {
			return null;
		}

		return assignment;
	}

	@Override
	protected Assignment[] allocateFormulas(int size) {
		return new Assignment[size];
	}

	protected final void createSCAssignments(IInternalElement target,
			IProgressMonitor monitor) throws CoreException {
		for (int i = 0; i < formulaElements.length; i++) {
			if (formulas[i] == null)
				continue;
			ISCAssignmentElement scAssnElem = (ISCAssignmentElement) symbolInfos[i]
					.createSCElement(target, null, monitor);
			scAssnElem.setAssignment(formulas[i], null);
		}
	}

}
