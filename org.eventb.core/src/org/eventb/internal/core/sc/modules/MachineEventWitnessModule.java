/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.HashSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.ISCWitness;
import org.eventb.core.IWitness;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.IAcceptorModule;
import org.eventb.core.sc.IModuleManager;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IEventLabelSymbolTable;
import org.eventb.core.sc.state.IEventRefinesInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.IStateSC;
import org.eventb.core.sc.symbolTable.IVariableSymbolInfo;
import org.eventb.core.state.IStateRepository;
import org.eventb.internal.core.sc.ModuleManager;
import org.eventb.internal.core.sc.symbolTable.EventLabelSymbolTable;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventWitnessModule extends PredicateModule {

	public static final String MACHINE_EVENT_WITNESS_ACCEPTOR = 
		EventBPlugin.PLUGIN_ID + ".machineEventWitnessAcceptor";

	private IAcceptorModule[] modules;

	public MachineEventWitnessModule() {
		IModuleManager manager = ModuleManager.getModuleManager();
		modules = manager.getAcceptorModules(MACHINE_EVENT_WITNESS_ACCEPTOR);
	}
	
	Predicate btrue;
	FormulaFactory factory;
	
	private static String WITNESS_NAME_PREFIX = "WIT";
	
	private static int WITNESS_HASH_TABLE_SIZE = 31;

	public void process(
			IRodinElement element, 
			IInternalParent target,
			IStateRepository<IStateSC> repository,
			IProgressMonitor monitor)
			throws CoreException {

		IEvent event = (IEvent) element;
		
		IWitness[] witnesses = event.getWitnesses();
		
		Predicate[] predicates = new Predicate[witnesses.length];
		
//		if (witnesses.length == 0)
//			return;
		
		// the name space of witness label is distinct from the name space of
		// other labels. Witness labels are variable names.
		
		ILabelSymbolTable savedLabelSymbolTable = labelSymbolTable;
		
		labelSymbolTable = new EventLabelSymbolTable(witnesses.length * 4 / 3 + 1);
		
		repository.setState(labelSymbolTable);
		
		checkAndType(
				witnesses, 
				target,
				predicates,
				modules,
				event.getElementName(),
				repository,
				monitor);
		
		HashSet<String> witnessNames = new HashSet<String>(WITNESS_HASH_TABLE_SIZE);
		
		getWitnessNames(target, witnessNames, repository, monitor);
		
		checkAndSaveWitnesses(target, witnesses, predicates, witnessNames, event, monitor);
		
		repository.setState(savedLabelSymbolTable);

	}
	
	private void checkAndSaveWitnesses(
			IInternalParent parent, 
			IWitness[] witnesses, 
			Predicate[] predicates,
			HashSet<String> witnessNames,
			IEvent event,
			IProgressMonitor monitor) throws RodinDBException {
		
		if (parent == null)
			return;
		
		int index = 0;
		
		for (int i=0; i<witnesses.length; i++) {
			if (predicates[i] == null)
				continue;
			String label = witnesses[i].getLabel(monitor);
			if (witnessNames.contains(label)) {
				witnessNames.remove(label);
				createSCWitness(
						parent, 
						WITNESS_NAME_PREFIX + index++,
						witnesses[i].getLabel(monitor), 
						witnesses[i],
						predicates[i], 
						monitor);
			} else {
				createProblemMarker(
						witnesses[i], 
						EventBAttributes.LABEL_ATTRIBUTE, 
						GraphProblem.WitnessLabelNeedLessError);
			}
		}
		
		for (String name : witnessNames) {
			createProblemMarker(
					event, 
					GraphProblem.WitnessLabelMissingWarning,
					name);
			createSCWitness(
					parent, 
					WITNESS_NAME_PREFIX + index++, 
					name, 
					event, 
					btrue, 
					monitor);
		}
	}
	
	void createSCWitness(
			IInternalParent parent, 
			String name,
			String label,
			IRodinElement source,
			Predicate predicate, 
			IProgressMonitor monitor) throws RodinDBException {
		ISCWitness scWitness = 
			(ISCWitness) parent.createInternalElement(
					ISCWitness.ELEMENT_TYPE, 
					name, 
					null, 
					monitor);
		scWitness.setLabel(label, monitor);
		scWitness.setPredicate(predicate);
		scWitness.setSource(source, monitor);
	}

	private void getWitnessNames(
			IInternalParent parent, 
			HashSet<String> witnessNames,
			IStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		
		if (parent == null)
			return;
		
		IEventRefinesInfo refinedEventTable = (IEventRefinesInfo)
			repository.getState(IEventRefinesInfo.STATE_TYPE);
		
		if (refinedEventTable.isEmpty())
			return;
		
		IAbstractEventInfo abstractEventInfo = 
			refinedEventTable.getAbstractEventInfos().get(0);
		
		getLocalWitnessNames(parent, abstractEventInfo, witnessNames, monitor);
		
		getGlobalWitnessNames(parent, abstractEventInfo, witnessNames, monitor);
		
	}

	private void getGlobalWitnessNames(
			IInternalParent parent, 
			IAbstractEventInfo abstractEventInfo, 
			HashSet<String> witnessNames,
			IProgressMonitor monitor) throws RodinDBException {
		Assignment[] assignments = abstractEventInfo.getActions();
		
		for (Assignment assignment : assignments) {
			
			if (assignment instanceof BecomesEqualTo)
				continue;
			
			FreeIdentifier[] identifiers = assignment.getAssignedIdentifiers();
			
			for (FreeIdentifier identifier : identifiers) {
				
				// there must be a variable symbol of this name in the symbol
				// table. We must check if it is disappearing or not.
				IVariableSymbolInfo symbolInfo = (IVariableSymbolInfo)
					identifierSymbolTable.getSymbolInfo(identifier.getName());
				
				if (symbolInfo.isConcrete())
					continue;
				
				FreeIdentifier primedIdentifier = identifier.withPrime(factory);
				
				witnessNames.add(primedIdentifier.getName());
			}
			
		}
	}

	private void getLocalWitnessNames(
			IInternalParent parent, 
			IAbstractEventInfo abstractEventInfo, 
			HashSet<String> witnessNames,
			IProgressMonitor monitor) throws RodinDBException {
		FreeIdentifier[] identifiers = abstractEventInfo.getIdentifiers();
		
		for (FreeIdentifier identifier : identifiers) {
			// if a symbol with the same name is found it can only be
			// a local variable of the concrete event.
			if (identifierSymbolTable.getSymbolInfo(identifier.getName()) != null)
				continue;
			
			witnessNames.add(identifier.getName());
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.Module#initModule(org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		factory = repository.getFormulaFactory();
		btrue = factory.makeLiteralPredicate(Formula.BTRUE, null);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.Module#endModule(org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		super.endModule(element, repository, monitor);
		btrue = null;
		factory = null;
	}

	@Override
	protected void makeProgress(IProgressMonitor monitor) {
		// no progress inside event
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.LabeledElementModule#getLabelSymbolTableFromRepository(org.eventb.core.sc.IStateRepository)
	 */
	@Override
	protected ILabelSymbolTable getLabelSymbolTableFromRepository(
			IStateRepository repository) throws CoreException {
		return (ILabelSymbolTable) repository.getState(IEventLabelSymbolTable.STATE_TYPE);
	}

}
