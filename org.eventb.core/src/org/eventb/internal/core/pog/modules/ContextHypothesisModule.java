/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ISCTheorem;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.internal.core.pog.ContextAxiomTable;
import org.eventb.internal.core.pog.ContextHypothesisManager;
import org.eventb.internal.core.pog.ContextTheoremTable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;


/**
 * @author Stefan Hallerstede
 *
 */
public class ContextHypothesisModule extends GlobalHypothesisModule {

	ContextHypothesisManager hypothesisManager;
	ContextAxiomTable axiomTable;
	ContextTheoremTable theoremTable;
	IPOFile target;
	
	@Override
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		
		ISCContextFile scContextFile = (ISCContextFile) element;
		
		target = repository.getTarget();
		
		IPOPredicateSet rootSet = target.getPredicateSet(ContextHypothesisManager.ABS_HYP_NAME);
		rootSet.create(null, monitor);
		
		ISCInternalContext[] contexts = scContextFile.getAbstractSCContexts();
		
		copyContexts(rootSet, contexts, monitor);
				
		fetchCarriersSetsAndConstants(scContextFile, rootSet, monitor);
		
		ISCAxiom[] axioms = scContextFile.getSCAxioms();
		ISCTheorem[] theorems = scContextFile.getSCTheorems();
		
		List<ISCPredicateElement> predicates = new LinkedList<ISCPredicateElement>();
		fetchPredicates(predicates, axioms);
		fetchPredicates(predicates, theorems);
		
		axiomTable = new ContextAxiomTable(axioms, typeEnvironment, factory);
		theoremTable = new ContextTheoremTable(theorems, typeEnvironment, factory);
		
		repository.setState(axiomTable);
		repository.setState(theoremTable);
		
		axiomTable.makeImmutable();
		theoremTable.makeImmutable();
		
		ISCPredicateElement[] predicateElements = new ISCPredicateElement[predicates.size()];		
		predicates.toArray(predicateElements);
		
		hypothesisManager = 
			new ContextHypothesisManager(scContextFile, target, predicateElements);
		
		repository.setState(hypothesisManager);
		
	}
	
	private void fetchPredicates(
			List<ISCPredicateElement> predicates,
			ISCPredicateElement[] predicateElements) throws RodinDBException {
		
		for(ISCPredicateElement element : predicateElements) {
			predicates.add(element);
		}
	}

	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		
		hypothesisManager.createHypotheses(monitor);
		target = null;
		hypothesisManager = null;
		axiomTable = null;
		theoremTable = null;
		
		super.endModule(element, repository, monitor);
	}

}
