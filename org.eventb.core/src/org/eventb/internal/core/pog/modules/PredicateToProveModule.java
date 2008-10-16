/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSource;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ITraceableElement;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.IPOGHint;
import org.eventb.core.pog.IPOGSource;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class PredicateToProveModule<PE extends ISCPredicateElement> 
extends PredicateModule<PE> {

	@Override
	protected void createProofObligation(
			IPORoot target, 
			String elementLabel, 
			PE predicateElement, 
			Predicate predicate, 
			IProgressMonitor monitor) throws CoreException {
		if(!goalIsTrivial(predicate)) {
			IPOPredicateSet hypothesis = hypothesisManager.makeHypothesis(predicateElement);
			IRodinElement predicateSource = ((ITraceableElement) predicateElement).getSource();
			createPO(
					target, 
					getProofObligationName(elementLabel), 
					getProofObligationDescription(),
					hypothesis,
					emptyPredicates,
					makePredicate(predicate, predicateSource),
					new IPOGSource[] {
						makeSource(IPOSource.DEFAULT_ROLE, predicateSource)
					},
					new IPOGHint[] {
						makeIntervalSelectionHint(
								hypothesisManager.getRootHypothesis(), 
								hypothesis)
					},
					isAccurate(),
					monitor);
		} else {
			if (DEBUG_TRIVIAL)
				debugTraceTrivial(getProofObligationName(elementLabel));
		}
		
	}

	protected abstract String getProofObligationDescription();

	protected abstract String getProofObligationName(String elementLabel);

}
