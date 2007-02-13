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
import org.eventb.core.pog.state.IAbstractEventActionTable;
import org.eventb.core.pog.state.IAbstractEventGuardList;
import org.eventb.core.pog.state.IEventWitnessTable;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.pog.util.POGHint;
import org.eventb.core.pog.util.POGIntervalSelectionHint;
import org.eventb.core.pog.util.POGPredicate;
import org.eventb.core.pog.util.POGSource;
import org.eventb.core.pog.util.POGTraceablePredicate;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class MachineEventActionModule extends MachineEventActionUtilityModule {

	protected IAbstractEventGuardList abstractEventGuardList;
	protected IAbstractEventActionTable abstractEventActionTable;
	protected IEventWitnessTable witnessTable;

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.state.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(
			IRodinElement element, 
			IPOGStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {
		
		if (!isApplicable())
			return;
		
		List<ISCAction> actions = concreteEventActionTable.getActions();
		
		if (actions.size() == 0)
			return;
		
		IPOFile target = repository.getTarget();
		
		POGHint[] hints = hints(
				new POGIntervalSelectionHint(
						eventHypothesisManager.getRootHypothesis(), 
						eventHypothesisManager.getFullHypothesis()));
		
		List<Assignment> assignments = concreteEventActionTable.getAssignments();
		
		for (int k=0; k<actions.size(); k++) {
			ISCAction action = actions.get(k);
			Assignment assignment = assignments.get(k);
			
			POGSource[] sources = sources(new POGSource(IPOSource.DEFAULT_ROLE, action));
			
			if (abstractHasNotSameAction(k)) {
				
				Predicate baPredicate = assignment.getBAPredicate(factory);
				List<POGPredicate> hyp = makeAbstractActionHypothesis(baPredicate);
				
				Predicate wdPredicate = assignment.getWDPredicate(factory);
				createProofObligation(target, hyp,
						wdPredicate, action, sources, hints, 
						"WD", "Well-definedness of action", monitor);
				
				Predicate fisPredicate = assignment.getFISPredicate(factory);
				createProofObligation(target, hyp,
						fisPredicate, action, sources, hints, 
						"FIS", "Feasibility of action", monitor);
				
			}
		}
	}
	
	protected abstract boolean isApplicable();

	protected abstract boolean abstractHasNotSameAction(int k);

	protected abstract List<POGPredicate> makeAbstractActionHypothesis(Predicate baPredicate);

	private void createProofObligation(
			IPOFile target, 
			List<POGPredicate> hyp,
			Predicate predicate, 
			ISCAction action, 
			POGSource[] sources, 
			POGHint[] hints, 
			String suffix,
			String desc,
			IProgressMonitor monitor) throws RodinDBException {
		String sequentName = concreteEventLabel + "/" + action.getLabel() + "/" + suffix;
		if (!goalIsTrivial(predicate)) {
			createPO(
					target, 
					sequentName, 
					desc, 
					fullHypothesis, 
					hyp, 
					new POGTraceablePredicate(predicate, action), 
					sources, 
					hints, 
					monitor);
		} else {
			if (DEBUG_TRIVIAL)
				debugTraceTrivial(sequentName);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		abstractEventGuardList =
			(IAbstractEventGuardList) repository.getState(IAbstractEventGuardList.STATE_TYPE);
		abstractEventActionTable = 
			(IAbstractEventActionTable) repository.getState(IAbstractEventActionTable.STATE_TYPE);
		witnessTable =
			(IEventWitnessTable) repository.getState(IEventWitnessTable.STATE_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		abstractEventGuardList = null;
		abstractEventActionTable = null;
		witnessTable = null;
		super.endModule(element, repository, monitor);
	}

}
