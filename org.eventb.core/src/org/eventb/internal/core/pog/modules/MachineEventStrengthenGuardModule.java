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
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.POGPredicate;
import org.eventb.core.pog.POGSource;
import org.eventb.core.pog.state.IAbstractEventGuardTable;
import org.eventb.core.pog.state.IConcreteEventGuardTable;
import org.eventb.core.pog.state.IStatePOG;
import org.eventb.core.sc.state.IEventRefinesInfo;
import org.eventb.core.state.IStateRepository;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventStrengthenGuardModule extends MachineEventRefinementModule {
	
	protected IConcreteEventGuardTable concreteEventGuardTable;
	
	protected IEventRefinesInfo eventRefinesInfo;

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IProcessorModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(
			IRodinElement element, 
			IPOFile target,
			IStateRepository<IStatePOG> repository, 
			IProgressMonitor monitor)
			throws CoreException {
		
		if (abstractEventGuardList.getAbstractEvents().size() <= 1) {
		
			ISCEvent abstractEvent = abstractEventGuardList.getFirstAbstractEvent();
			IAbstractEventGuardTable abstractEventGuardTable = 
				abstractEventGuardList.getFirstAbstractEventGuardTable();
			
			if (abstractEvent == null)
				return;
		
			createSplitProofObligation(
					target, 
					abstractEvent,
					abstractEventGuardTable,
					monitor);
		} else {
			
			createMergeProofObligation(target, monitor);
			
		}
	}

	private void createMergeProofObligation(
			IPOFile target, 
			IProgressMonitor monitor) throws RodinDBException {
		
		List<IAbstractEventGuardTable> absGuardTables = 
			abstractEventGuardList.getAbstractEventGuardTables();
		
		List<Predicate> disjPredList = 
			new ArrayList<Predicate>(absGuardTables.size());
		
		for (IAbstractEventGuardTable absGuardTable : absGuardTables) {
			
			List<Predicate> absGuards = absGuardTable.getPredicates();
			
			if (absGuards.size() == 0)
				return;
			
			List<Predicate> conjPredList = new ArrayList<Predicate>(absGuards.size());
			
			for (int i=0; i<absGuards.size(); i++) {
				Predicate absGuard = absGuards.get(i);
				boolean absGuardIsNew = 
					absGuardTable.getIndexOfCorrespondingConcrete(i) == -1;
				
				if (!goalIsTrivial(absGuard) && absGuardIsNew)
					conjPredList.add(absGuard);
			}
			
			if (conjPredList.size() > 0)
				disjPredList.add(
						conjPredList.size() == 1 ? conjPredList.get(0) :
						factory.makeAssociativePredicate(
								Formula.LAND, 
								conjPredList, null));
			else // no proof obligation: one branch is true!
				return;
		}
		
		// disjPredList must have at least two elements
		// if the size was reduced the preceding loop waould have returned from this method
		
		Predicate disjPredicate = 
			factory.makeAssociativePredicate(
					Formula.LOR, 
					disjPredList, null);
		
		ArrayList<POGPredicate> hyp = makeActionHypothesis();
		hyp.addAll(makeWitnessHypothesis());

		disjPredicate = disjPredicate.applyAssignments(witnessTable.getEventDetAssignments(), factory);
		LinkedList<BecomesEqualTo> substitution = new LinkedList<BecomesEqualTo>();
		if (concreteEventActionTable.getXiUnprime() != null)
			substitution.add(concreteEventActionTable.getXiUnprime());
		substitution.addAll(concreteEventActionTable.getPrimedDetAssignments());
		disjPredicate = disjPredicate.applyAssignments(substitution, factory);
		
		List<ISCEvent> absEvents = abstractEventGuardList.getAbstractEvents();
		
		List<POGSource> sourceList = new ArrayList<POGSource>(absEvents.size() + 1);
		for (ISCEvent absEvent : absEvents)
			sourceList.add(new POGSource(IPOSource.ABSTRACT_ROLE, absEvent));
		sourceList.add(new POGSource(IPOSource.CONCRETE_ROLE, concreteEvent));
		
		POGSource[] sources = new POGSource[sourceList.size()];
		sourceList.toArray(sources);
	
		String sequentName = concreteEventLabel + "/MRG";
		createPO(
				target, 
				sequentName, 
				"Guard strengthening (merge)",
				fullHypothesis,
				hyp,
				new POGPredicate(concreteEvent, disjPredicate),
				sources,
				hints(getLocalHypothesisSelectionHint(target, sequentName)),
				monitor);
	}

	private void createSplitProofObligation(
			IPOFile target, 
			ISCEvent abstractEvent, 
			IAbstractEventGuardTable abstractEventGuardTable,
			IProgressMonitor monitor) throws RodinDBException {
		
		List<ISCPredicateElement> guards = abstractEventGuardTable.getElements();
		List<Predicate> absGuards = abstractEventGuardTable.getPredicates();
		
		ArrayList<POGPredicate> hyp = makeActionHypothesis();
		hyp.addAll(makeWitnessHypothesis());
		for (int i=0; i<guards.size(); i++) {
			String guardLabel = ((ISCGuard) guards.get(i)).getLabel();
			Predicate absGuard = absGuards.get(i);
			
			if (goalIsTrivial(absGuard) 
					|| abstractEventGuardTable.getIndexOfCorrespondingConcrete(i) != -1)
				continue;
			
			absGuard = absGuard.applyAssignments(witnessTable.getEventDetAssignments(), factory);
			LinkedList<BecomesEqualTo> substitution = new LinkedList<BecomesEqualTo>();
			if (concreteEventActionTable.getXiUnprime() != null)
				substitution.add(concreteEventActionTable.getXiUnprime());
			substitution.addAll(concreteEventActionTable.getPrimedDetAssignments());
			absGuard = absGuard.applyAssignments(substitution, factory);
			
			String sequentName = concreteEventLabel + "/" + guardLabel + "/REF";
			createPO(
					target, 
					sequentName, 
					"Guard strengthening (split)",
					fullHypothesis,
					hyp,
					new POGPredicate(guards.get(i), absGuard),
					sources(
							new POGSource(IPOSource.ABSTRACT_ROLE, abstractEvent),
							new POGSource(IPOSource.ABSTRACT_ROLE, (ITraceableElement) guards.get(i)),
							new POGSource(IPOSource.CONCRETE_ROLE, concreteEvent)),
					hints(getLocalHypothesisSelectionHint(target, sequentName)),
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
		super.endModule(element, target, repository, monitor);
	}

}
