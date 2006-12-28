/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSource;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ITraceableElement;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.POGHint;
import org.eventb.core.pog.POGIntervalSelectionHint;
import org.eventb.core.pog.POGPredicate;
import org.eventb.core.pog.POGSource;
import org.eventb.core.pog.state.IHypothesisManager;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.pog.state.IPredicateTable;
import org.eventb.core.pog.state.IPOGState;
import org.eventb.core.tool.state.IStateRepository;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class PredicateModule extends UtilityModule {
	
	protected IPredicateTable predicateTable;
	protected IHypothesisManager hypothesisManager;

	@Override
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		predicateTable = 
			getPredicateTable(repository);
		hypothesisManager = 
			getHypothesisManager(repository);
	}

	protected abstract IHypothesisManager getHypothesisManager(IStateRepository<IPOGState> repository) 
	throws CoreException;

	protected abstract IPredicateTable getPredicateTable(IStateRepository<IPOGState> repository) 
	throws CoreException;
	
	@Override
	public void endModule(IRodinElement element, IPOGStateRepository repository, IProgressMonitor monitor) throws CoreException {
		predicateTable = null;
		hypothesisManager = null;
		super.endModule(element, repository, monitor);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.state.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(IRodinElement element, IPOGStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {
		
		List<ISCPredicateElement> elements = predicateTable.getElements();
		
		if(elements.size() == 0)
			return;
		
		IPOFile target = repository.getTarget();
		
		List<Predicate> predicates = predicateTable.getPredicates();
		
		for (int i=0; i<elements.size(); i++) {
			ISCPredicateElement predicateElement = elements.get(i);
			String elementLabel = ((ILabeledElement) predicateElement).getLabel();
			
			Predicate predicate = predicates.get(i);
			
			createWDProofObligation(target, elementLabel, predicateElement, predicate, i, monitor);
			
			createProofObligation(target, elementLabel, predicateElement, predicate, monitor);

		}

	}

	protected void createProofObligation(
			IPOFile target, 
			String elementLabel, 
			ISCPredicateElement predicateElement, 
			Predicate predicate, 
			IProgressMonitor monitor) throws CoreException {
		// create proof obligation (used for theorems)
	}

	protected void createWDProofObligation(
			IPOFile target, 
			String elementLabel, 
			ISCPredicateElement predicateElement, 
			Predicate predicate, 
			int index,
			IProgressMonitor monitor) throws CoreException {
		Predicate wdPredicate = predicate.getWDPredicate(factory);
		if(!goalIsTrivial(wdPredicate)) {
			IPOPredicateSet hypothesis = hypothesisManager.getHypothesis(target, predicateElement);
			createPO(
					target, 
					getWDProofObligationName(elementLabel), 
					getWDProofObligationDescription(),
					hypothesis,
					emptyPredicates,
					new POGPredicate(predicateElement, wdPredicate),
					sources(new POGSource(IPOSource.DEFAULT_ROLE, (ITraceableElement) predicateElement)),
					new POGHint[] {
						new POGIntervalSelectionHint(
								hypothesisManager.getRootHypothesis(target), 
								hypothesis)
					},
					monitor);
		}
	}

	protected abstract String getWDProofObligationDescription();

	protected abstract String getWDProofObligationName(String elementLabel);

}
