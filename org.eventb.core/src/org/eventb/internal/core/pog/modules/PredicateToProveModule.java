/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSource;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ITraceableElement;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.util.POGHint;
import org.eventb.core.pog.util.POGIntervalSelectionHint;
import org.eventb.core.pog.util.POGSource;
import org.eventb.core.pog.util.POGTraceablePredicate;

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
			IProgressMonitor monitor) throws CoreException {
		if(!goalIsTrivial(predicate)) {
			IPOPredicateSet hypothesis = hypothesisManager.makeHypothesis(predicateElement);
			createPO(
					target, 
					getProofObligationName(elementLabel), 
					getProofObligationDescription(),
					hypothesis,
					emptyPredicates,
					new POGTraceablePredicate(predicate, predicateElement),
					sources(new POGSource(IPOSource.DEFAULT_ROLE, (ITraceableElement) predicateElement)),
					new POGHint[] {
						new POGIntervalSelectionHint(
								hypothesisManager.getRootHypothesis(), 
								hypothesis)
					},
					monitor);
		}
		
	}

	protected abstract String getProofObligationDescription();

	protected abstract String getProofObligationName(String elementLabel);

}
