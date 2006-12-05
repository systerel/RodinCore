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
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ITraceableElement;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.POGPredicate;
import org.eventb.core.pog.POGSource;
import org.eventb.core.pog.state.IAbstractEventGuardTable;
import org.eventb.core.pog.state.IConcreteEventGuardTable;
import org.eventb.core.pog.state.IStatePOG;
import org.eventb.core.state.IStateRepository;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventStrengthenGuardModule extends MachineEventRefinementModule {
	
	protected IConcreteEventGuardTable concreteEventGuardTable;
	protected IAbstractEventGuardTable abstractEventGuardTable;

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
		
		createREF_GRD_REF(
				target, 
				abstractEvent,
				monitor);
	}

	private void createREF_GRD_REF(
			IPOFile target, 
			ISCEvent abstractEvent, 
			IProgressMonitor monitor) throws RodinDBException {
		List<ISCPredicateElement> guards = abstractEventGuardTable.getElements();
		List<Predicate> grdPredicates = abstractEventGuardTable.getPredicates();
		ArrayList<POGPredicate> hyp = makeActionHypothesis();
		hyp.addAll(makeWitnessHypothesis());
		for (int i=0; i<guards.size(); i++) {
			String guardLabel = ((ISCGuard) guards.get(i)).getLabel();
			Predicate predicate = grdPredicates.get(i);
			predicate = predicate.applyAssignments(witnessTable.getEventDetAssignments(), factory);
			LinkedList<BecomesEqualTo> substitution = new LinkedList<BecomesEqualTo>();
			if (concreteEventActionTable.getXiUnprime() != null)
				substitution.add(concreteEventActionTable.getXiUnprime());
			substitution.addAll(concreteEventActionTable.getPrimedDetAssignments());
			predicate = predicate.applyAssignments(substitution, factory);
			
			createPO(
					target, 
					concreteEventLabel + "/" + guardLabel + "/REF", 
					"Action simulation",
					fullHypothesis,
					hyp,
					new POGPredicate(guards.get(i), predicate),
					sources(
							new POGSource(IPOSource.ABSTRACT_ROLE, abstractEvent),
							new POGSource(IPOSource.ABSTRACT_ROLE, (ITraceableElement) guards.get(i)),
							new POGSource(IPOSource.CONCRETE_ROLE, concreteEvent)),
					emptyHints,
					monitor);
	
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			IPOFile target, 
			IStateRepository<IStatePOG> repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, target, repository, monitor);
		concreteEventGuardTable = 
			(IConcreteEventGuardTable) repository.getState(IConcreteEventGuardTable.STATE_TYPE);
		abstractEventGuardTable =
			(IAbstractEventGuardTable) repository.getState(IAbstractEventGuardTable.STATE_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			IPOFile target, 
			IStateRepository<IStatePOG> repository, 
			IProgressMonitor monitor) throws CoreException {
		concreteEventGuardTable = null;
		abstractEventGuardTable = null;
		super.endModule(element, target, repository, monitor);
	}

}
