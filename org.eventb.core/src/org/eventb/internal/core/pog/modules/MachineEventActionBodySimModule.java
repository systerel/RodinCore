/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.ArrayList;
import java.util.LinkedList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSource;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCEvent;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
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
public class MachineEventActionBodySimModule extends
		MachineEventRefinementModule {
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IProcessorModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(
			IRodinElement element, 
			IPOFile target,
			IStateRepository<IStatePOG> repository, 
			IProgressMonitor monitor)
			throws CoreException {
		
		ISCEvent abstractEvent = eventHypothesisManager.getFirstAbstractEvent();
		
		// this POG module applies to refined events
		if (abstractEvent == null)
			return;
		
		createBodySimProofObligations(
				target, 
				abstractEvent,
				monitor);

	}

	private void createBodySimProofObligations(
			IPOFile target, 
			ISCEvent abstractEvent, 
			IProgressMonitor monitor) throws RodinDBException {

		ArrayList<POGPredicate> hyp = makeActionHypothesis();
		hyp.addAll(makeWitnessHypothesis());
		ArrayList<Assignment> simAssignments = abstractEventActionTable.getSimAssignments();
		ArrayList<ISCAction> simActions = abstractEventActionTable.getSimActions();
		for (int i=0; i<simActions.size(); i++) {
			String actionLabel = simActions.get(i).getLabel();
			Predicate predicate = simAssignments.get(i).getBAPredicate(factory);
			LinkedList<BecomesEqualTo> substitution = new LinkedList<BecomesEqualTo>();
			substitution.addAll(witnessTable.getMachinePrimedDetAssignments());
			if (witnessTable.getPrimeSubstitution() != null)
				substitution.add(witnessTable.getPrimeSubstitution());
			substitution.addAll(witnessTable.getEventDetAssignments());
			predicate = predicate.applyAssignments(substitution, factory);
			substitution.clear();
			if (concreteEventActionTable.getXiUnprime() != null)
				substitution.add(concreteEventActionTable.getXiUnprime());
			substitution.addAll(concreteEventActionTable.getPrimedDetAssignments());
			predicate = predicate.applyAssignments(substitution, factory);
			
			createPO(
					target, 
					concreteEventLabel + "/" + actionLabel + "/SIM", 
					"Action simulation",
					fullHypothesis,
					hyp,
					new POGPredicate(simActions.get(i), predicate),
					sources(
							new POGSource(IPOSource.ABSTRACT_ROLE, abstractEvent),
							new POGSource(IPOSource.ABSTRACT_ROLE, simActions.get(i)),
							new POGSource(IPOSource.CONCRETE_ROLE, concreteEvent)),
					emptyHints,
					monitor);

		}
	}	
		

}
