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
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSource;
import org.eventb.core.ISCAction;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.POGHint;
import org.eventb.core.pog.POGIntervalSelectionHint;
import org.eventb.core.pog.POGPredicate;
import org.eventb.core.pog.POGSource;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
@Deprecated
public class MachineEventActionBasicModule extends MachineEventActionUtilityModule {

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(IRodinElement element, IPOGStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {

		processConcreteActions(
				repository.getTarget(), 
				concreteEventActionTable.getActions(), 
				concreteEventActionTable.getAssignments(), 
				monitor);
				
	}
	
	private void processConcreteActions(
			IPOFile target, 
			List<ISCAction> actions, 
			List<Assignment> assignments, 
			IProgressMonitor monitor) throws RodinDBException, CoreException {
		
		POGHint[] hints = hints(
				new POGIntervalSelectionHint(
						eventHypothesisManager.getRootHypothesis(target), 
						eventHypothesisManager.getFullHypothesis(target)));
		
		for (int i=0; i<actions.size(); i++) {
			
			ISCAction action = actions.get(i);
			String actionLabel = action.getLabel();
			Assignment assignment = assignments.get(i);

			createWDPO(
					target, 
					action, 
					actionLabel, 
					assignment, 
					hints,
					monitor);
			
			createFISPO(
					target, 
					action, 
					actionLabel, 
					assignment, 
					hints,
					monitor);
			
		}
		
	}
	
	private void createWDPO(
			IPOFile target, 
			ISCAction action, 
			String actionLabel, 
			Assignment assignment,
			POGHint[] hints,
			IProgressMonitor monitor) throws CoreException {
		
		Predicate wdPredicate = assignment.getWDPredicate(factory);
		if( !goalIsTrivial(wdPredicate)) {
			createPO(
					target, 
					concreteEventLabel + "/" + actionLabel + "/WD", 
					"Well-definedness of Action",
					fullHypothesis,
					emptyPredicates,
					new POGPredicate(action, wdPredicate),
					sources(new POGSource(IPOSource.DEFAULT_ROLE, action)),
					hints,
					monitor);
		}
	}
	
	private void createFISPO(
			IPOFile target, 
			ISCAction action, 
			String actionLabel, 
			Assignment assignment,
			POGHint[] hints,
			IProgressMonitor monitor) throws CoreException {
		Predicate fisPredicate = assignment.getFISPredicate(factory);
		if(!goalIsTrivial(fisPredicate)) {
			createPO(
					target, 
					concreteEventLabel + "/" + actionLabel + "/FIS", 
					"Feasibility of Action",
					fullHypothesis,
					emptyPredicates,
					new POGPredicate(action, fisPredicate),
					sources(new POGSource(IPOSource.DEFAULT_ROLE, action)),
					hints,
					monitor);
		}
	}

}
