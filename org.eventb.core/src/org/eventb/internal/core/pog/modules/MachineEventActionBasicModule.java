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
import org.eventb.core.ISCAction;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.POGPredicate;
import org.eventb.core.pog.POGSource;
import org.eventb.core.pog.state.IStatePOG;
import org.eventb.core.state.IStateRepository;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventActionBasicModule extends MachineEventActionModule {

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(IRodinElement element, IPOFile target,
			IStateRepository<IStatePOG> repository, IProgressMonitor monitor)
			throws CoreException {

		processConcreteActions(
				target, 
				concreteEventActionTable.getActions(), 
				concreteEventActionTable.getAssignments(), 
				monitor);
				
	}
	
	private void processConcreteActions(
			IPOFile target, 
			ISCAction[] actions, 
			Assignment[] assignments, 
			IProgressMonitor monitor) throws RodinDBException, CoreException {
		for (int i=0; i<actions.length; i++) {
			
			String actionLabel = actions[i].getLabel(monitor);
			
			createWDPO(
					target, 
					actions[i], 
					actionLabel, 
					assignments[i], 
					monitor);
			
			createFISPO(
					target, 
					actions[i], 
					actionLabel, 
					assignments[i], 
					monitor);
			
		}
		
	}
	
	private void createWDPO(
			IPOFile target, 
			ISCAction action, 
			String actionLabel, 
			Assignment assignment,
			IProgressMonitor monitor) throws CoreException {
		
		Predicate wdPredicate = assignment.getWDPredicate(factory);
		if(!wdPredicate.equals(btrue)) {
			createPO(
					target, 
					concreteEventLabel + "/" + actionLabel + "/WD", 
					"Well-definedness of Action",
					eventIdentifierTable,
					fullHypothesisName,
					emptyPredicates,
					new POGPredicate(action, wdPredicate),
					sources(new POGSource("assignment", action)),
					emptyHints,
					monitor);
		}
	}
	
	private void createFISPO(
			IPOFile target, 
			ISCAction action, 
			String actionLabel, 
			Assignment assignment,
			IProgressMonitor monitor) throws CoreException {
		Predicate fisPredicate = assignment.getFISPredicate(factory);
		if(!fisPredicate.equals(btrue)) {
			createPO(
					target, 
					concreteEventLabel + "/" + actionLabel + "/FIS", 
					"Feasibility of Action",
					eventIdentifierTable,
					fullHypothesisName,
					emptyPredicates,
					new POGPredicate(action, fisPredicate),
					sources(new POGSource("assignment", action)),
					emptyHints,
					monitor);
		}
	}

}
