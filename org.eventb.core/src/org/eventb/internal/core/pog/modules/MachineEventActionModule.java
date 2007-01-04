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
import org.eventb.core.IPOSource;
import org.eventb.core.ISCAction;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.state.IAbstractEventActionTable;
import org.eventb.core.pog.state.IStateRepository;
import org.eventb.core.pog.util.POGHint;
import org.eventb.core.pog.util.POGIntervalSelectionHint;
import org.eventb.core.pog.util.POGSource;
import org.eventb.core.pog.util.POGTraceablePredicate;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventActionModule extends MachineEventActionUtilityModule {

	protected IAbstractEventActionTable abstractEventActionTable;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.state.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(
			IRodinElement element, 
			IStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {
		
		ISCAction[] actions = concreteEventActionTable.getActions();
		
		if (actions.length == 0)
			return;
		
		IPOFile target = repository.getTarget();
		
		POGHint[] hints = hints(
				new POGIntervalSelectionHint(
						eventHypothesisManager.getRootHypothesis(), 
						eventHypothesisManager.getFullHypothesis()));
		
		Assignment[] assignments = concreteEventActionTable.getAssignments();
		
		for (int k=0; k<actions.length; k++) {
			ISCAction action = actions[k];
			Assignment assignment = assignments[k];
			
			POGSource[] sources = sources(new POGSource(IPOSource.DEFAULT_ROLE, action));
			
			if (abstractEventActionTable.getIndexOfCorrespondingAbstract(k) == -1) {
				
				Predicate wdPredicate = assignment.getWDPredicate(factory);
				createProofObligation(target, 
						wdPredicate, action, sources, hints, 
						"WD", "Well-definedness of action", monitor);
				
				Predicate fisPredicate = assignment.getFISPredicate(factory);
				createProofObligation(target, 
						fisPredicate, action, sources, hints, 
						"FIS", "Feasibility of action", monitor);
				
			}
		}
	}

	private void createProofObligation(
			IPOFile target, 
			Predicate predicate, 
			ISCAction action, 
			POGSource[] sources, 
			POGHint[] hints, 
			String suffix,
			String desc,
			IProgressMonitor monitor) throws RodinDBException {
		if (!goalIsTrivial(predicate)) {
			createPO(
					target, 
					concreteEventLabel + "/" + action.getLabel() + "/" + suffix, 
					desc, 
					fullHypothesis, 
					emptyPredicates, 
					new POGTraceablePredicate(predicate, action), 
					sources, 
					hints, 
					monitor);
		}
	}
	
	@Override
	public void initModule(
			IRodinElement element, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		abstractEventActionTable = 
			(IAbstractEventActionTable) repository.getState(IAbstractEventActionTable.STATE_TYPE);
	}

	@Override
	public void endModule(
			IRodinElement element, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		abstractEventActionTable = null;
		super.endModule(element, repository, monitor);
	}

}
