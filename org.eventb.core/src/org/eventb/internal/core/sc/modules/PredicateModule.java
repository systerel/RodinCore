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
import org.eventb.core.IPredicateElement;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IResult;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.GraphProblem;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;

/**
 * @author Stefan Hallerstede
 * 
 */
public abstract class PredicateModule<I extends IPredicateElement> extends
		LabeledFormulaModule<Predicate, I> {

	@Override
	protected Predicate[] allocateFormulas(int size) {
		return new Predicate[size];
	}

	@Override
	protected IAttributeType.String getFormulaAttributeType() {
		return EventBAttributes.PREDICATE_ATTRIBUTE;
	}

	@Override
	protected Predicate parseFormula(I formulaElement,
			Collection<FreeIdentifier> freeIdentifierContext,
			FormulaFactory factory) throws CoreException {

		IAttributeType.String attributeType = getFormulaAttributeType();

		if (!formulaElement.hasPredicateString()) {
			createProblemMarker(formulaElement,
					attributeType,
					GraphProblem.PredicateUndefError);
			return null;
		}
		String predicateString = formulaElement.getPredicateString();

		// parse the predicate

		IParseResult parseResult = factory.parsePredicate(predicateString, V2,
				formulaElement);

		if (issueASTProblemMarkers(formulaElement, attributeType,
				parseResult)) {
			return null;
		}
		Predicate predicate = parseResult.getParsedPredicate();

		// check legibility of the predicate
		// (this will only produce a warning on failure)

		IResult legibilityResult = predicate.isLegible(freeIdentifierContext);

		if (issueASTProblemMarkers(formulaElement, attributeType,
				legibilityResult)) {
			return null;
		}

		return predicate;
	}

	protected final void copySCPredicates(
			ISCPredicateElement[] predicateElements, IInternalElement target,
			IProgressMonitor monitor) throws CoreException {
		for (ISCPredicateElement predicate : predicateElements)
			predicate.copy(target, null, null, false, monitor);
	}

	protected final void createSCPredicates(IInternalElement target,
			IProgressMonitor monitor) throws CoreException {
		for (int i = 0; i < formulaElements.length; i++) {
			if (formulas[i] == null)
				continue;
			ISCPredicateElement scPredElem = (ISCPredicateElement) symbolInfos[i]
					.createSCElement(target, null, monitor);
			scPredElem.setPredicate(formulas[i], null);
		}
	}

}
