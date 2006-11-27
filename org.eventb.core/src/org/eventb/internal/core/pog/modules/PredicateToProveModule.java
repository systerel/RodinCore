/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ITraceableElement;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.POGHint;
import org.eventb.core.pog.POGIntervalSelectionHint;
import org.eventb.core.pog.POGPredicate;
import org.eventb.core.pog.POGSource;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class PredicateToProveModule extends PredicateModule {

	@Override
	protected void createProofObligation(
			IPOFile target, 
			String elementLabel, 
			ISCPredicateElement predicateElement, 
			Predicate predicate, 
			IProgressMonitor monitor) throws RodinDBException {
		if(!goalIsTrivial(predicate)) {
			IPOPredicateSet hypothesis = hypothesisManager.getHypothesis(target, predicateElement);
			createPO(
					target, 
					getProofObligationName(elementLabel), 
					getProofObligationDescription(),
					hypothesis,
					emptyPredicates,
					new POGPredicate(predicateElement, predicate),
					sources(new POGSource(
							getProofObligationSourceRole(), 
							(ITraceableElement) predicateElement)),
					new POGHint[] {
						new POGIntervalSelectionHint(
								hypothesisManager.getRootHypothesis(target), 
								hypothesis)
					},
					monitor);
		}
		
	}

	protected abstract String getProofObligationSourceRole();

	protected abstract String getProofObligationDescription();

	protected abstract String getProofObligationName(String elementLabel);

}
