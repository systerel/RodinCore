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
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.IPOGHint;
import org.eventb.core.pog.IPOGNature;
import org.eventb.core.pog.IPOGPredicate;
import org.eventb.core.pog.IPOGSource;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IAbstractEventGuardList;
import org.eventb.core.pog.state.IAbstractEventGuardTable;
import org.eventb.core.pog.state.IConcreteEventGuardTable;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class FwdMachineEventStrengthenGuardModule extends MachineEventRefinementModule {
	
	public static final IModuleType<FwdMachineEventStrengthenGuardModule> MODULE_TYPE = 
		POGCore.getModuleType(EventBPlugin.PLUGIN_ID + ".fwdMachineEventStrengthenGuardModule"); //$NON-NLS-1$
	
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	protected IConcreteEventGuardTable concreteEventGuardTable;

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IProcessorModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void process(
			IRodinElement element, 
			IPOGStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {
		
		IPORoot target = repository.getTarget();
		
		if (abstractEventGuardList.getRefinementType() != IAbstractEventGuardList.MERGE) {
		
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
			IPORoot target, 
			IProgressMonitor monitor) throws CoreException {
		
		List<IAbstractEventGuardTable> absGuardTables = 
			abstractEventGuardList.getAbstractEventGuardTables();
		
		String sequentName = concreteEventLabel + "/MRG";
		
		List<Predicate> disjPredList = 
			new ArrayList<Predicate>(absGuardTables.size());
		
		for (IAbstractEventGuardTable absGuardTable : absGuardTables) {
			
			List<ISCGuard> absGuardsElems = absGuardTable.getElements();
			List<Predicate> absGuards = absGuardTable.getPredicates();
			
			if (absGuards.size() == 0) {
				if (DEBUG_TRIVIAL)
					debugTraceTrivial(sequentName);
				return;
			}
			
			List<Predicate> conjPredList = new ArrayList<Predicate>(absGuards.size());
			
			for (int i=0; i<absGuards.size(); i++) {
				final ISCGuard elem = absGuardsElems.get(i);
				if (elem.isTheorem()) {
					continue;
				}
				Predicate absGuard = absGuards.get(i);
				boolean absGuardIsNew = 
					absGuardTable.getIndexOfCorrespondingConcrete(i) == -1;
				
				if (!isTrivial(absGuard) && absGuardIsNew)
					conjPredList.add(absGuard);
			}
			
			if (conjPredList.size() > 0)
				disjPredList.add(
						conjPredList.size() == 1 ? conjPredList.get(0) :
						factory.makeAssociativePredicate(
								Formula.LAND, 
								conjPredList, null));
			else { // no proof obligation: one branch is true!
				if (DEBUG_TRIVIAL)
					debugTraceTrivial(sequentName);
				return;
			}
		}
		
		// disjPredList must have at least two elements
		// if the size was reduced the preceding loop would have returned from this method
		
		Predicate disjPredicate = 
			factory.makeAssociativePredicate(
					Formula.LOR, 
					disjPredList, null);
		
		disjPredicate = disjPredicate.applyAssignments(witnessTable.getEventDetAssignments());
		LinkedList<BecomesEqualTo> substitution = new LinkedList<BecomesEqualTo>();
		if (concreteEventActionTable.getXiUnprime() != null)
			substitution.add(concreteEventActionTable.getXiUnprime());
		substitution.addAll(concreteEventActionTable.getPrimedDetAssignments());
		disjPredicate = disjPredicate.applyAssignments(substitution);
		
		List<ISCEvent> absEvents = abstractEventGuardList.getAbstractEvents();
		
		List<IPOGSource> sourceList = new ArrayList<IPOGSource>(absEvents.size() + 1);
		for (ISCEvent absEvent : absEvents)
			sourceList.add(makeSource(IPOSource.ABSTRACT_ROLE, absEvent.getSource()));
		sourceList.add(makeSource(IPOSource.CONCRETE_ROLE, concreteEvent.getSource()));
		
		ArrayList<IPOGPredicate> hyp = makeActionAndWitnessHypothesis(disjPredicate);
		
		IPOGSource[] sources = new IPOGSource[sourceList.size()];
		sourceList.toArray(sources);
	
		createPO(
				target, 
				sequentName, 
				IPOGNature.GUARD_STRENGTHENING_MERGE,
				fullHypothesis,
				hyp,
				makePredicate(disjPredicate, concreteEvent.getSource()),
				sources,
				new IPOGHint[] {
					getLocalHypothesisSelectionHint(target, sequentName)
				},
				accurate,
				monitor);
	}

	private void createSplitProofObligation(
			IPORoot target, 
			ISCEvent abstractEvent, 
			IAbstractEventGuardTable abstractEventGuardTable,
			IProgressMonitor monitor) throws CoreException {
		
		List<ISCGuard> absGuardElements = abstractEventGuardTable.getElements();
		List<Predicate> absGuardPredicates = abstractEventGuardTable.getPredicates();
		
		for (int i=0; i<absGuardElements.size(); i++) {
			ISCGuard absGuardElement = absGuardElements.get(i);
			if (absGuardElement.isTheorem()) {
				continue;
			}
			String guardLabel = absGuardElement.getLabel();
			Predicate absGuard = absGuardPredicates.get(i);
			String sequentName = concreteEventLabel + "/" + guardLabel + "/GRD";
			
			if (isTrivial(absGuard) 
					|| abstractEventGuardTable.getIndexOfCorrespondingConcrete(i) != -1) {
				if (DEBUG_TRIVIAL)
					debugTraceTrivial(sequentName);
				continue;
			}
			
			absGuard = absGuard.applyAssignments(witnessTable.getEventDetAssignments());
			LinkedList<BecomesEqualTo> substitution = new LinkedList<BecomesEqualTo>();
			if (concreteEventActionTable.getXiUnprime() != null)
				substitution.add(concreteEventActionTable.getXiUnprime());
			substitution.addAll(concreteEventActionTable.getPrimedDetAssignments());
			absGuard = absGuard.applyAssignments(substitution);
		
			ArrayList<IPOGPredicate> hyp = makeActionAndWitnessHypothesis(absGuard);
			
			createPO(
					target, 
					sequentName, 
					IPOGNature.GUARD_STRENGTHENING_SPLIT,
					fullHypothesis,
					hyp,
					makePredicate(absGuard, absGuardElement.getSource()),
					new IPOGSource[] {
							makeSource(IPOSource.ABSTRACT_ROLE, abstractEvent.getSource()),
							makeSource(IPOSource.ABSTRACT_ROLE, absGuardElement.getSource()),
							makeSource(IPOSource.CONCRETE_ROLE, concreteEvent.getSource())
					},
					new IPOGHint[] {
						getLocalHypothesisSelectionHint(target, sequentName)
					},
					accurate,
					monitor);
	
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
		concreteEventGuardTable = 
			(IConcreteEventGuardTable) repository.getState(IConcreteEventGuardTable.STATE_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		concreteEventGuardTable = null;
		super.endModule(element, repository, monitor);
	}

}
