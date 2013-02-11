/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - added PO nature
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSource;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCEvent;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.IPOGHint;
import org.eventb.core.pog.IPOGNature;
import org.eventb.core.pog.IPOGPredicate;
import org.eventb.core.pog.IPOGSource;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class FwdMachineEventActionBodySimModule extends
		MachineEventRefinementModule {
	
	public static final IModuleType<FwdMachineEventActionBodySimModule> MODULE_TYPE = 
		POGCore.getModuleType(EventBPlugin.PLUGIN_ID + ".fwdMachineEventActionBodySimModule"); //$NON-NLS-1$
	
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IProcessorModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void process(
			IRodinElement element, 
			IPOGStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {
		
		ISCEvent abstractEvent = abstractEventGuardList.getFirstAbstractEvent();
		
		// this POG module applies to refined events
		if (abstractEvent == null)
			return;
		
		createBodySimProofObligations(
				repository.getTarget(), 
				abstractEvent,
				monitor);

	}

	private void createBodySimProofObligations(
			IPORoot target, 
			ISCEvent abstractEvent, 
			IProgressMonitor monitor) throws CoreException {

		List<Assignment> simAssignments = 
			abstractEventActionTable.getSimAssignments();
		List<ISCAction> simActions = abstractEventActionTable.getSimActions();
		
		for (int i=0; i<simActions.size(); i++) {
			ISCAction action = simActions.get(i);
			String actionLabel = action.getLabel();
			Assignment simAssignment = simAssignments.get(i);
			
			if (abstractEventActionTable.getIndexOfCorrespondingConcrete(i) != -1)
				continue;
			
			Predicate simPredicate = simAssignment.getBAPredicate();
			
			String sequentName = concreteEventLabel + "/" + actionLabel + "/SIM";
			
			if (isTrivial(simPredicate)) {
				if (DEBUG_TRIVIAL)
					debugTraceTrivial(sequentName);
				continue;
			}
			
			LinkedList<BecomesEqualTo> substitution = new LinkedList<BecomesEqualTo>();
			substitution.addAll(witnessTable.getMachinePrimedDetAssignments());
			substitution.addAll(witnessTable.getEventDetAssignments());
			simPredicate = simPredicate.applyAssignments(substitution);
			substitution.clear();
			if (concreteEventActionTable.getXiUnprime() != null)
				substitution.add(concreteEventActionTable.getXiUnprime());
			substitution.addAll(concreteEventActionTable.getPrimedDetAssignments());
			simPredicate = simPredicate.applyAssignments(substitution);
			
			ArrayList<IPOGPredicate> hyp = makeActionAndWitnessHypothesis(simPredicate);
			
			createPO(
					target, 
					sequentName, 
					IPOGNature.ACTION_SIMULATION,
					fullHypothesis,
					hyp,
					makePredicate(simPredicate, action.getSource()),
					new IPOGSource[] {
						makeSource(IPOSource.ABSTRACT_ROLE, abstractEvent.getSource()),
						makeSource(IPOSource.ABSTRACT_ROLE, action.getSource()),
						makeSource(IPOSource.CONCRETE_ROLE, concreteEvent.getSource())
					},
					new IPOGHint[] {
						getLocalHypothesisSelectionHint(target, sequentName)
					},
					accurate,
					monitor);

		}
	}	

}
