/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ISCWitness;
import org.eventb.core.ITraceableElement;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.IAbstractEventActionTable;
import org.eventb.core.pog.IAbstractEventGuardTable;
import org.eventb.core.pog.IConcreteEventActionTable;
import org.eventb.core.pog.IConcreteEventGuardTable;
import org.eventb.core.pog.IEventHypothesisManager;
import org.eventb.core.pog.IIdentifierTable;
import org.eventb.core.pog.IMachineHypothesisManager;
import org.eventb.core.pog.IMachineInvariantTable;
import org.eventb.core.pog.IMachineVariableTable;
import org.eventb.core.pog.IWitnessTable;
import org.eventb.core.pog.Module;
import org.eventb.core.pog.POGHint;
import org.eventb.core.pog.POGPredicate;
import org.eventb.core.pog.POGSource;
import org.eventb.core.sc.IStateRepository;
import org.eventb.core.sc.ITypingState;
import org.eventb.internal.core.pog.IdentifierTable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventActionModule extends Module {

	private Predicate btrue;
	private List<POGPredicate> emptyPredicates;
	private POGHint[] emptyHints;

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IProcessorModule#process(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(IRodinElement element, IPOFile target,
			IStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		
		ISCEvent concreteEvent = (ISCEvent) element;
		String concreteEventLabel = concreteEvent.getLabel(monitor);
		
		boolean isInit = concreteEventLabel.equals("INITIALISATION");
		
		String fullHypothesisName = 
			isInit ? 
					machineHypothesisManager.getContextHypothesisName() : 
					eventHypothesisManager.getFullHypothesisName();
					
		ISCEvent abstractEvent = eventHypothesisManager.getFirstAbstractEvent();
		
		processConcreteActions(
				target, 
				concreteEventLabel, 
				fullHypothesisName, 
				concreteEventActionTable.getActions(), 
				concreteEventActionTable.getAssignments(), 
				monitor);
				
		processInvariants(
				target, 
				abstractEvent,
				concreteEvent, 
				concreteEventLabel, 
				isInit, 
				fullHypothesisName, 
				monitor);
		
		createREF_X_EQL(
				target, 
				abstractEvent,
				concreteEvent, 
				concreteEventLabel, 
				isInit, 
				fullHypothesisName, 
				monitor);
		
		createREF_X_SIM(
				target, 
				abstractEvent,
				concreteEvent, 
				concreteEventLabel, 
				fullHypothesisName, 
				monitor);

		createREF_GRD_REF(
				target, 
				abstractEvent,
				concreteEvent, 
				concreteEventLabel, 
				fullHypothesisName, 
				monitor);
	}

	private void createREF_GRD_REF(
			IPOFile target, 
			ISCEvent abstractEvent, 
			ISCEvent concreteEvent, 
			String concreteEventLabel, 
			String fullHypothesisName, 
			IProgressMonitor monitor) throws RodinDBException {
		List<ISCPredicateElement> guards = abstractEventGuardTable.getElements();
		List<Predicate> grdPredicates = abstractEventGuardTable.getPredicates();
		ArrayList<POGPredicate> hyp = makeActionHypothesis();
		hyp.addAll(makeWitnessHypothesis());
		for (int i=0; i<guards.size(); i++) {
			String guardLabel = ((ISCGuard) guards.get(i)).getLabel(monitor);
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
					eventIdentifierTable,
					fullHypothesisName,
					hyp,
					new POGPredicate(guards.get(i), predicate),
					sources(
							new POGSource("abstract event", abstractEvent),
							new POGSource("abstract guard", (ITraceableElement) guards.get(i)),
							new POGSource("concrete event", concreteEvent)),
					emptyHints,
					monitor);
	
		}
	}

	private void createREF_X_SIM(
			IPOFile target, 
			ISCEvent abstractEvent, 
			ISCEvent concreteEvent, 
			String concreteEventLabel, 
			String fullHypothesisName, 
			IProgressMonitor monitor) throws RodinDBException {

		// this applies to refined events
		if (abstractEvent == null)
			return;

		ArrayList<POGPredicate> hyp = makeActionHypothesis();
		hyp.addAll(makeWitnessHypothesis());
		ArrayList<Assignment> simAssignments = abstractEventActionTable.getSimAssignments();
		ArrayList<ISCAction> simActions = abstractEventActionTable.getSimActions();
		for (int i=0; i<simActions.size(); i++) {
			String actionLabel = simActions.get(i).getLabel(monitor);
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
					eventIdentifierTable,
					fullHypothesisName,
					hyp,
					new POGPredicate(simActions.get(i), predicate),
					sources(
							new POGSource("abstract event", abstractEvent),
							new POGSource("abstract action", simActions.get(i)),
							new POGSource("concrete event", concreteEvent)),
					emptyHints,
					monitor);

		}
	}
	
	protected IIdentifierTable addMachineWitnessIdentifiers(IIdentifierTable identifierTable) {
		IIdentifierTable witIdentifierTable = new IdentifierTable(identifierTable);
		for (FreeIdentifier identifier : witnessTable.getWitnessedVariables())
			if (identifier.isPrimed())
				witIdentifierTable.addIdentifier(identifier);
		return witIdentifierTable;
	}

	private void processInvariants(
			IPOFile target, 
			ISCEvent abstractEvent, 
			ISCEvent concreteEvent, 
			String concreteEventLabel, 
			boolean isInit, 
			String fullHypothesisName, 
			IProgressMonitor monitor) throws RodinDBException {
		List<ISCPredicateElement> invariants = invariantTable.getElements();
		List<Predicate> invPredicates = invariantTable.getPredicates();
		
		for (int i=0; i<invariants.size(); i++) {
			
			String invariantLabel = ((ISCInvariant) invariants.get(i)).getLabel(monitor);
			
			FreeIdentifier[] freeIdentifiers = invPredicates.get(i).getFreeIdentifiers();
			HashSet<FreeIdentifier> freeIdents = 
				new HashSet<FreeIdentifier>(freeIdentifiers.length * 4 / 3 + 1);
			boolean commonIdents = false; // common identifiers?
			for(FreeIdentifier identifier : freeIdentifiers) {
				freeIdents.add(identifier);
				if(!commonIdents && concreteEventActionTable.getAssignedVariables().contains(identifier))
					commonIdents = true;
			}
				
			if (commonIdents || isInit) {
				
				ArrayList<POGPredicate> hyp = makeActionHypothesis(freeIdents);
				
				createMDL_X_INV(
						target, 
						abstractEvent,
						concreteEvent, 
						concreteEventLabel, 
						isInit, 
						(ISCInvariant) invariants.get(i), 
						invPredicates.get(i), 
						invariantLabel, 
						fullHypothesisName, 
						eventIdentifierTable,
						hyp,
						freeIdents, 
						monitor);
				
				createREF_X_INV(
						target, 
						abstractEvent,
						concreteEvent, 
						concreteEventLabel, 
						isInit, 
						(ISCInvariant) invariants.get(i), 
						invariantLabel, 
						invPredicates.get(i), 
						eventIdentifierTable,
						fullHypothesisName, 
						hyp, 
						freeIdents,
						monitor);
		
			}
			
		}
	}

	private void createREF_X_EQL(
			IPOFile target, 
			ISCEvent abstractEvent, 
			ISCEvent concreteEvent, 
			String concreteEventLabel, 
			boolean isInit, 
			String fullHypothesisName, 
			IProgressMonitor monitor) throws RodinDBException {
		
		if (abstractEvent == null)
			return;
		
		ArrayList<POGPredicate> hyp = 
			new ArrayList<POGPredicate>(1);
		
		ArrayList<Assignment> nondetAssignments = concreteEventActionTable.getNondetAssignments();
		ArrayList<ISCAction> nondetActions = concreteEventActionTable.getNondetActions();
		ArrayList<BecomesEqualTo> detAssignments = concreteEventActionTable.getDetAssignments();
		ArrayList<BecomesEqualTo> primedDetAssignments = concreteEventActionTable.getPrimedDetAssignments();
		ArrayList<ISCAction> detActions = concreteEventActionTable.getDetActions();
		
		Set<FreeIdentifier> abstractAssignedVariables = 
			abstractEventActionTable.getAssignedVariables();
		
		for (FreeIdentifier variable : machineVariableTable.getPreservedVariables()) {
			
			if(abstractAssignedVariables.contains(variable))
				continue;
			
			hyp.clear();
			
			Predicate predicate =
				factory.makeRelationalPredicate(Formula.EQUAL, 
						variable.withPrime(factory), 
						variable, null);
			
			IRodinElement source = null;
			
			int pos = findIndex(variable, nondetAssignments);
			
			if (pos >= 0) {
				hyp.add(
						new POGPredicate(nondetActions.get(pos),
								nondetAssignments.get(pos).getBAPredicate(factory)));
				source = nondetActions.get(pos);
			} else {
				pos = findIndex(variable, detAssignments);
				
				if (pos >= 0) {
					predicate = predicate.applyAssignment(primedDetAssignments.get(pos), factory);
					source = detActions.get(pos);
				} else
					continue;
			}
			
			createPO(
					target, 
					concreteEventLabel + "/" + variable.getName() + "/EQL", 
					"Equality " + (isInit ? " establishment" : " preservation"),
					eventIdentifierTable,
					fullHypothesisName,
					hyp,
					new POGPredicate(source, predicate),
					sources(
							new POGSource("abstract event", abstractEvent),
							new POGSource("concrete event", concreteEvent)),
					emptyHints,
					monitor);

		}
	}

	private int findIndex(FreeIdentifier variable, ArrayList assignments) {
		int pos = -1;
		
		for (int i=0; i<assignments.size(); i++) {
			Assignment assignment = (Assignment) assignments.get(i);
			for (FreeIdentifier ident : assignment.getAssignedIdentifiers()) {
				if (variable.equals(ident)) {
					pos = i;
					break;
				}
			}
		}
		return pos;
	}

	private void createREF_X_INV(
			IPOFile target, 
			ISCEvent abstractEvent, 
			ISCEvent concreteEvent, 
			String concreteEventLabel, 
			boolean isInit, 
			ISCInvariant invariant, 
			String invariantLabel, 
			Predicate invPredicate, 
			IIdentifierTable identifierTable,
			String fullHypothesisName, 
			ArrayList<POGPredicate> hyp, 
			Set<FreeIdentifier> freeIdents,
			IProgressMonitor monitor) throws RodinDBException {
		
		// this applies to refined events
		if (abstractEvent == null)
			return;
		
		LinkedList<POGPredicate> bighyp = new LinkedList<POGPredicate>();
		bighyp.addAll(hyp);
		
		ArrayList<Assignment> nondet = concreteEventActionTable.getNondetAssignments();
		ArrayList<ISCAction> nondetActions = concreteEventActionTable.getNondetActions();
		for (int k=0; k<nondet.size(); k++) {
			bighyp.add(
					new POGPredicate(
							nondetActions.get(k), 
							nondet.get(k).getBAPredicate(factory)));
		}
		ArrayList<Predicate> nondetWitPrds = witnessTable.getNondetPredicates();
		ArrayList<FreeIdentifier> witnessedVars = witnessTable.getNondetAssignedVariables();
		ArrayList<ISCWitness> nondetWits = witnessTable.getNondetWitnesses();
		for (int i=0; i<nondetWits.size(); i++) {
			FreeIdentifier ident = witnessedVars.get(i).isPrimed() ?
					witnessedVars.get(i).withoutPrime(factory) :
					witnessedVars.get(i);
			if (freeIdents.contains(ident))
				bighyp.add(
						new POGPredicate(
								nondetWits.get(i),
								nondetWitPrds.get(i)));
		}
		
		LinkedList<BecomesEqualTo> substitution = new LinkedList<BecomesEqualTo>();
		if (concreteEventActionTable.getDeltaPrime() != null)
			substitution.add(concreteEventActionTable.getDeltaPrime());
		substitution.addAll(abstractEventActionTable.getDisappearingWitnesses());
		Predicate predicate = invPredicate.applyAssignments(substitution, factory);
		substitution.clear();		
		substitution.addAll(witnessTable.getEventDetAssignments());
		substitution.addAll(witnessTable.getMachineDetAssignments());
		if (witnessTable.getPrimeSubstitution() != null)
			substitution.add(witnessTable.getPrimeSubstitution());
		predicate = predicate.applyAssignments(substitution, factory);
		substitution.clear();
		if (concreteEventActionTable.getXiUnprime() != null)
			substitution.add(concreteEventActionTable.getXiUnprime());
		substitution.addAll(concreteEventActionTable.getPrimedDetAssignments());
		predicate = predicate.applyAssignments(substitution, factory);
		
		createPO(
				target, 
				concreteEventLabel + "/" + invariantLabel + "/INV", 
				"Invariant " + (isInit ? " establishment" : " preservation"),
				identifierTable,
				fullHypothesisName,
				bighyp,
				new POGPredicate(invariant, predicate),
				sources(
						new POGSource("abstract event", abstractEvent),
						new POGSource("concrete event", concreteEvent), 
						new POGSource("invariant", invariant)),
				emptyHints,
				monitor);
	}
	
	private ArrayList<POGPredicate> makeActionHypothesis() {
		// create local hypothesis for nondeterministic assignments
		
		ArrayList<Assignment> nondetAssignments = concreteEventActionTable.getNondetAssignments();
		ArrayList<ISCAction> nondetActions = concreteEventActionTable.getNondetActions();
		
		ArrayList<POGPredicate> hyp = 
			new ArrayList<POGPredicate>(nondetAssignments.size());
		
		for (int i=0; i<nondetAssignments.size(); i++) {
			hyp.add(
					new POGPredicate(nondetActions.get(i),
							nondetAssignments.get(i).getBAPredicate(factory)));
		}
		return hyp;		
	}
	
	private ArrayList<POGPredicate> makeWitnessHypothesis() {
		// create local hypothesis for nondeterministic assignments
		ArrayList<ISCWitness> nondetWitnesses = witnessTable.getNondetWitnesses();
//		ArrayList<FreeIdentifier> nondetAssignedVariables = witnessTable.getNondetAssignedVariables();
		ArrayList<Predicate> nondetPredicates = witnessTable.getNondetPredicates();
		
		ArrayList<POGPredicate> hyp = 
			new ArrayList<POGPredicate>(nondetWitnesses.size());

		for (int i=0; i<nondetWitnesses.size(); i++) {
			hyp.add(
					new POGPredicate(nondetWitnesses.get(i),
							nondetPredicates.get(i)));
		}
		
		return hyp;
	}

	private ArrayList<POGPredicate> makeActionHypothesis(HashSet<FreeIdentifier> freeIdents) {
		// create local hypothesis for nondeterministic assignments
		
		ArrayList<Assignment> nondetAssignments = concreteEventActionTable.getNondetAssignments();
		ArrayList<ISCAction> nondetActions = concreteEventActionTable.getNondetActions();
		
		ArrayList<POGPredicate> hyp = 
			new ArrayList<POGPredicate>(nondetAssignments.size());
		
		for (int i=0; i<nondetAssignments.size(); i++) {
			for (FreeIdentifier ident : nondetAssignments.get(i).getAssignedIdentifiers()) {
				if (freeIdents.contains(ident)) {
					hyp.add(
							new POGPredicate(nondetActions.get(i),
									nondetAssignments.get(i).getBAPredicate(factory)));
					break;
				}
			}
		
		}
		return hyp;
	}

	private void createMDL_X_INV(
			IPOFile target, 
			ISCEvent abstractEvent, 
			ISCEvent concreteEvent, 
			String concreteEventLabel, 
			boolean isInit, 
			ISCInvariant invariant, 
			Predicate invPredicate, 
			String invariantLabel, 
			String fullHypothesisName, 
			IIdentifierTable identifierTable,
			ArrayList<POGPredicate> hyp,
			HashSet<FreeIdentifier> freeIdents, 
			IProgressMonitor monitor) throws RodinDBException {
		
		// this applies to events of initial models and new events 
		if (abstractEvent != null)
			return;
		
		LinkedList<BecomesEqualTo> substitution = new LinkedList<BecomesEqualTo>();
		
		if (concreteEventActionTable.getDeltaPrime() != null)
			substitution.add(concreteEventActionTable.getDeltaPrime());
		Predicate predicate = invPredicate.applyAssignments(substitution, factory);
		substitution.clear();
		substitution.addAll(concreteEventActionTable.getPrimedDetAssignments());	
		predicate = predicate.applyAssignments(substitution, factory);
		
		createPO(
				target, 
				concreteEventLabel + "/" + invariantLabel + "/INV", 
				"Invariant " + (isInit ? " establishment" : " preservation"),
				identifierTable,
				fullHypothesisName,
				hyp,
				new POGPredicate(invariant, predicate),
				sources(
						new POGSource("event", concreteEvent), 
						new POGSource("invariant", invariant)),
				emptyHints,
				monitor);
	}

	private void processConcreteActions(
			IPOFile target, 
			String eventLabel, 
			String fullHypothesisName, 
			ISCAction[] actions, 
			Assignment[] assignments, 
			IProgressMonitor monitor) throws RodinDBException, CoreException {
		for (int i=0; i<actions.length; i++) {
			
			String actionLabel = actions[i].getLabel(monitor);
			
			createWDPO(
					target, 
					eventLabel, 
					actions[i], 
					actionLabel, 
					assignments[i], 
					fullHypothesisName, 
					monitor);
			
			createFISPO(
					target, 
					eventLabel, 
					actions[i], 
					actionLabel, 
					assignments[i], 
					fullHypothesisName, 
					monitor);
			
		}
		
	}
	
	protected void addAssignedIdentifiers(Assignment assignment, IIdentifierTable identifierTable) {
		FreeIdentifier[] identifiers = assignment.getAssignedIdentifiers();
		for (FreeIdentifier identifier : identifiers)
			identifierTable.addIdentifier(identifier.withPrime(factory));
	}
	
	private void createWDPO(
			IPOFile target, 
			String eventLabel, 
			ISCAction action, 
			String actionLabel, 
			Assignment assignment,
			String fullHypothesisName,
			IProgressMonitor monitor) throws CoreException {
		
		Predicate wdPredicate = assignment.getWDPredicate(factory);
		if(!wdPredicate.equals(btrue)) {
			createPO(
					target, 
					eventLabel + "/" + actionLabel + "/WD", 
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
			String eventLabel, 
			ISCAction action, 
			String actionLabel, 
			Assignment assignment,
			String fullHypothesisName,
			IProgressMonitor monitor) throws CoreException {
		Predicate fisPredicate = assignment.getFISPredicate(factory);
		if(!fisPredicate.equals(btrue)) {
			createPO(
					target, 
					eventLabel + "/" + actionLabel + "/FIS", 
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
		
	IMachineHypothesisManager machineHypothesisManager;
	IEventHypothesisManager eventHypothesisManager;
	ITypeEnvironment eventTypeEnvironment;
	IConcreteEventGuardTable concreteEventGuardTable;
	IAbstractEventGuardTable abstractEventGuardTable;
	IMachineInvariantTable invariantTable;
	IIdentifierTable eventIdentifierTable;
	IMachineVariableTable machineVariableTable;
	IAbstractEventActionTable abstractEventActionTable;
	IConcreteEventActionTable concreteEventActionTable;
	IWitnessTable witnessTable;
	FormulaFactory factory;

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(IRodinElement element, IPOFile target, IStateRepository repository, IProgressMonitor monitor) throws CoreException {
		super.initModule(element, target, repository, monitor);
		machineHypothesisManager =
			(IMachineHypothesisManager) repository.getState(IMachineHypothesisManager.STATE_TYPE);
		eventHypothesisManager = 
			(IEventHypothesisManager) repository.getState(IEventHypothesisManager.STATE_TYPE);
		eventTypeEnvironment =
			((ITypingState) repository.getState(ITypingState.STATE_TYPE)).getTypeEnvironment();
		machineVariableTable =
			(IMachineVariableTable) repository.getState(IMachineVariableTable.STATE_TYPE);
		concreteEventGuardTable = 
			(IConcreteEventGuardTable) repository.getState(IConcreteEventGuardTable.STATE_TYPE);
		abstractEventGuardTable =
			(IAbstractEventGuardTable) repository.getState(IAbstractEventGuardTable.STATE_TYPE);
		invariantTable =
			(IMachineInvariantTable) repository.getState(IMachineInvariantTable.STATE_TYPE);
		eventIdentifierTable =
			(IIdentifierTable) repository.getState(IIdentifierTable.STATE_TYPE);
		witnessTable =
			(IWitnessTable) repository.getState(IWitnessTable.STATE_TYPE);
		factory = repository.getFormulaFactory();
		abstractEventActionTable = 
			(IAbstractEventActionTable) repository.getState(IAbstractEventActionTable.STATE_TYPE);
		concreteEventActionTable =
			(IConcreteEventActionTable) repository.getState(IConcreteEventActionTable.STATE_TYPE);
		btrue = factory.makeLiteralPredicate(Formula.BTRUE, null);
		emptyPredicates = new ArrayList<POGPredicate>(0);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(IRodinElement element, IPOFile target, IStateRepository repository, IProgressMonitor monitor) throws CoreException {
		eventHypothesisManager = null;
		eventTypeEnvironment = null;
		concreteEventGuardTable = null;
		invariantTable = null;
		eventIdentifierTable = null;
		abstractEventActionTable = null;
		concreteEventActionTable = null;
		witnessTable = null;
		factory = null;
		btrue = null;
		super.endModule(element, target, repository, monitor);
	}

}
