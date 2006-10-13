/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IPredicateElement;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IResult;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class PredicateModule extends LabeledFormulaModule {
	
	@Override
	protected String getFormulaAttributeId() {
		return EventBAttributes.PREDICATE_ATTRIBUTE;
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.LabeledFormulaModule#parseFormula(int, org.rodinp.core.IInternalElement[], org.eventb.core.ast.Formula[], java.util.Collection, org.eventb.core.ast.FormulaFactory)
	 */
	@Override
	protected Formula parseFormula(
			IInternalElement formulaElement, 
			Collection<FreeIdentifier> freeIdentifierContext, 
			FormulaFactory factory) throws CoreException {
		
		IPredicateElement predicateElement = (IPredicateElement) formulaElement;

		String predicateString = predicateElement.getPredicateString();
		
		// parse the predicate
		
		IParseResult parseResult = factory.parsePredicate(predicateString);
		
		if (!parseResult.isSuccess()) {
			issueASTProblemMarkers(predicateElement, getFormulaAttributeId(), parseResult);
			
			return null;
		}
		Predicate predicate = parseResult.getParsedPredicate();
		
		// check legibility of the predicate
		// (this will only produce a warning on failure)
		
		IResult legibilityResult = predicate.isLegible(freeIdentifierContext);
		
		if (!legibilityResult.isSuccess()) {
			issueASTProblemMarkers(predicateElement, getFormulaAttributeId(), legibilityResult);
		}
		
		return predicate;
	}
	
	protected void copySCPredicates(
			ISCPredicateElement[] predicateElements,
			IInternalParent target, 
			IProgressMonitor monitor) throws CoreException {
		for (ISCPredicateElement predicate : predicateElements)
			predicate.copy(target, null, null, false, monitor);
	}
	

}
