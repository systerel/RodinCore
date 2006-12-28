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
import java.util.List;

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
import org.eventb.core.pog.state.IPOGStateRepository;
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
			IPOGStateRepository repository, 
			IProgressMonitor monitor)
			throws CoreException {
		
		ISCEvent abstractEvent = abstractEventGuardList.getFirstAbstractEvent();
		
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
		
		List<Assignment> simAssignments = abstractEventActionTable.getSimAssignments();
		List<ISCAction> simActions = abstractEventActionTable.getSimActions();
		
		for (int i=0; i<simActions.size(); i++) {
			String actionLabel = simActions.get(i).getLabel();
			Assignment simAssignment = simAssignments.get(i);
			
			if (abstractEventActionTable.getIndexOfCorrespondingConcrete(i) != -1)
				continue;
			
			Predicate simPredicate = simAssignment.getBAPredicate(factory);
			
			if (goalIsTrivial(simPredicate))
				continue;
			
			LinkedList<BecomesEqualTo> substitution = new LinkedList<BecomesEqualTo>();
			substitution.addAll(witnessTable.getMachinePrimedDetAssignments());
			if (witnessTable.getPrimeSubstitution() != null)
				substitution.add(witnessTable.getPrimeSubstitution());
			substitution.addAll(witnessTable.getEventDetAssignments());
			simPredicate = simPredicate.applyAssignments(substitution, factory);
			substitution.clear();
			if (concreteEventActionTable.getXiUnprime() != null)
				substitution.add(concreteEventActionTable.getXiUnprime());
			substitution.addAll(concreteEventActionTable.getPrimedDetAssignments());
			simPredicate = simPredicate.applyAssignments(substitution, factory);
			
			String sequentName = concreteEventLabel + "/" + actionLabel + "/SIM";
			createPO(
					target, 
					sequentName, 
					"Action simulation",
					fullHypothesis,
					hyp,
					new POGPredicate(simActions.get(i), simPredicate),
					sources(
							new POGSource(IPOSource.ABSTRACT_ROLE, abstractEvent),
							new POGSource(IPOSource.ABSTRACT_ROLE, simActions.get(i)),
							new POGSource(IPOSource.CONCRETE_ROLE, concreteEvent)),
					hints(getLocalHypothesisSelectionHint(target, sequentName)),
					monitor);

		}
	}	

}
