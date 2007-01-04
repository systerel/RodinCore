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
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSource;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ITraceableElement;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.state.IHypothesisManager;
import org.eventb.core.pog.state.IPredicateTable;
import org.eventb.core.pog.state.IState;
import org.eventb.core.pog.state.IStateRepository;
import org.eventb.core.pog.util.POGHint;
import org.eventb.core.pog.util.POGIntervalSelectionHint;
import org.eventb.core.pog.util.POGSource;
import org.eventb.core.pog.util.POGTraceablePredicate;
import org.eventb.core.tool.state.IToolStateRepository;
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
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		predicateTable = 
			getPredicateTable(repository);
		hypothesisManager = 
			getHypothesisManager(repository);
	}

	protected abstract IHypothesisManager getHypothesisManager(IToolStateRepository<IState> repository) 
	throws CoreException;

	protected abstract IPredicateTable getPredicateTable(IToolStateRepository<IState> repository) 
	throws CoreException;
	
	@Override
	public void endModule(IRodinElement element, IStateRepository repository, IProgressMonitor monitor) throws CoreException {
		predicateTable = null;
		hypothesisManager = null;
		super.endModule(element, repository, monitor);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.state.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(IRodinElement element, IStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {
		
		ISCPredicateElement[] elements = predicateTable.getElements();
		
		if(elements.length == 0)
			return;
		
		IPOFile target = repository.getTarget();
		
		Predicate[] predicates = predicateTable.getPredicates();
		
		for (int i=0; i<elements.length; i++) {
			ISCPredicateElement predicateElement = elements[i];
			String elementLabel = ((ILabeledElement) predicateElement).getLabel();
			
			Predicate predicate = predicates[i];
			
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
			IPOPredicateSet hypothesis = hypothesisManager.makeHypothesis(predicateElement);
			createPO(
					target, 
					getWDProofObligationName(elementLabel), 
					getWDProofObligationDescription(),
					hypothesis,
					emptyPredicates,
					new POGTraceablePredicate(wdPredicate, predicateElement),
					sources(new POGSource(IPOSource.DEFAULT_ROLE, (ITraceableElement) predicateElement)),
					new POGHint[] {
						new POGIntervalSelectionHint(
								hypothesisManager.getRootHypothesis(), 
								hypothesis)
					},
					monitor);
		}
	}

	protected abstract String getWDProofObligationDescription();

	protected abstract String getWDProofObligationName(String elementLabel);

}
