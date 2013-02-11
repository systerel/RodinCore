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
 *     Systerel - fix bug #3469348: Bug in INITIALISATION FIS PO
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
import org.eventb.core.ISCWitness;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.IPOGHint;
import org.eventb.core.pog.IPOGNature;
import org.eventb.core.pog.IPOGPredicate;
import org.eventb.core.pog.IPOGSource;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IAbstractEventActionTable;
import org.eventb.core.pog.state.IAbstractEventGuardList;
import org.eventb.core.pog.state.IEventWitnessTable;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class FwdMachineEventActionModule extends MachineEventActionUtilityModule {

	public static final IModuleType<FwdMachineEventActionModule> MODULE_TYPE = 
		POGCore.getModuleType(EventBPlugin.PLUGIN_ID + ".fwdMachineEventActionModule"); //$NON-NLS-1$
	
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	protected IAbstractEventGuardList abstractEventGuardList;
	protected IAbstractEventActionTable abstractEventActionTable;
	protected IEventWitnessTable witnessTable;

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.state.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void process(
			IRodinElement element, 
			IPOGStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {
		
		List<ISCAction> actions = concreteEventActionTable.getActions();
		
		if (actions.size() == 0)
			return;
		
		IPORoot target = repository.getTarget();
		
		IPOGHint[] hints = new IPOGHint[] {
				makeIntervalSelectionHint(
						eventHypothesisManager.getRootHypothesis(), 
						eventHypothesisManager.getFullHypothesis())
		};
		
		List<Assignment> assignments = concreteEventActionTable.getAssignments();
		
		for (int k=0; k<actions.size(); k++) {
			ISCAction action = actions.get(k);
			Assignment assignment = assignments.get(k);
			
			IPOGSource[] sources = new IPOGSource[] {
					makeSource(IPOSource.DEFAULT_ROLE, action.getSource())
			};
			
			Predicate baPredicate = assignment.getBAPredicate();
			List<IPOGPredicate> hyp = makeAbstractActionHypothesis(baPredicate);

			if (abstractHasNotSameAction(k)) {
				Predicate wdPredicate = assignment.getWDPredicate(factory);
				createProofObligation(target, hyp,
						wdPredicate, action, sources, hints, 
						"WD", IPOGNature.ACTION_WELL_DEFINEDNESS, monitor);
			}

			if (hyp.isEmpty() || abstractHasNotSameAction(k)) {
				Predicate fisPredicate = assignment.getFISPredicate();
				createProofObligation(target, hyp,
						fisPredicate, action, sources, hints, 
						"FIS", IPOGNature.ACTION_FEASIBILITY, monitor);
			}
		}
	}
	
	private boolean abstractHasNotSameAction(int k) {
		return abstractEventActionTable.getIndexOfCorrespondingAbstract(k) == -1;
	}

	private List<IPOGPredicate> makeAbstractActionHypothesis(Predicate baPredicate) throws RodinDBException {
		
		List<ISCAction> actions = abstractEventActionTable.getNondetActions();
		
		ArrayList<IPOGPredicate> hyp = new ArrayList<IPOGPredicate>(
				witnessTable.getNondetWitnesses().size() +
				actions.size());
		
		if (eventVariableWitnessPredicatesUnprimed(hyp)) {
			List<Predicate> predicates = abstractEventActionTable.getNondetPredicates();
			List<BecomesEqualTo> substitution = new LinkedList<BecomesEqualTo>();
			substitution.addAll(witnessTable.getEventDetAssignments());
			
			for (int i=0; i<actions.size(); i++) {
				Predicate predicate = predicates.get(i);
				predicate = predicate.applyAssignments(substitution);
				hyp.add(makePredicate(predicate, actions.get(i).getSource()));
			}
			
			return hyp;
		} else
			return emptyPredicates;
	}

	private boolean eventVariableWitnessPredicatesUnprimed(List<IPOGPredicate> hyp) throws RodinDBException {
		
		List<FreeIdentifier> witnessIdents = witnessTable.getVariables();
		List<Predicate> witnessPreds = witnessTable.getPredicates();
		List<ISCWitness> witnesses = witnessTable.getWitnesses();
		
		for (int i=0; i<witnessIdents.size(); i++) {
			Predicate predicate = witnessPreds.get(i);
			if ( ! witnessIdents.get(i).isPrimed()) {
				FreeIdentifier[] freeIdents = predicate.getFreeIdentifiers();
				for (FreeIdentifier freeIdent : freeIdents) {
					if (freeIdent.isPrimed())
						return false;
				}
				if (!witnessTable.isDeterministic(i))
					hyp.add(makePredicate(predicate, witnesses.get(i).getSource()));
			}
		}
		return true;
	}

	private void createProofObligation(
			IPORoot target, 
			List<IPOGPredicate> hyp,
			Predicate predicate, 
			ISCAction action, 
			IPOGSource[] sources, 
			IPOGHint[] hints, 
			String suffix,
			IPOGNature nature,
			IProgressMonitor monitor) throws CoreException {
		String sequentName = concreteEventLabel + "/" + action.getLabel() + "/" + suffix;
		if (isTrivial(predicate)) {
			if (DEBUG_TRIVIAL)
				debugTraceTrivial(sequentName);
			return;
		}
		createPO(
				target, 
				sequentName, 
				nature, 
				fullHypothesis, 
				hyp, 
				makePredicate(predicate, action.getSource()), 
				sources, 
				hints, 
				accurate,
				monitor);
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
