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
import org.eventb.core.ILabeledElement;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCWitness;
import org.eventb.core.IWitness;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.IFilterModule;
import org.eventb.core.sc.IModuleManager;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IEventLabelSymbolTable;
import org.eventb.core.sc.state.IEventRefinesInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.IStateSC;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;
import org.eventb.core.sc.symbolTable.IVariableSymbolInfo;
import org.eventb.core.state.IStateRepository;
import org.eventb.internal.core.sc.ModuleManager;
import org.eventb.internal.core.sc.symbolTable.EventLabelSymbolTable;
import org.eventb.internal.core.sc.symbolTable.WitnessSymbolInfo;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventWitnessModule extends PredicateModule {

	public static final String MACHINE_EVENT_WITNESS_FILTER = 
		EventBPlugin.PLUGIN_ID + ".machineEventWitnessFilter";

	private IFilterModule[] filterModules;

	public MachineEventWitnessModule() {
		IModuleManager manager = ModuleManager.getModuleManager();
		filterModules = manager.getFilterModules(MACHINE_EVENT_WITNESS_FILTER);
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
		
		// the name space of witness label is distinct from the name space of
		// other labels. Witness labels are variable names.
		
		ILabelSymbolTable savedLabelSymbolTable = labelSymbolTable;
		
		labelSymbolTable = new EventLabelSymbolTable(witnesses.length * 4 / 3 + 1);
		
		repository.setState(labelSymbolTable);
		
		checkAndType(
				witnesses, 
				target,
				predicates,
				filterModules,
				event.getElementName(),
				repository,
				monitor);
		
		HashSet<String> witnessNames = new HashSet<String>(WITNESS_HASH_TABLE_SIZE);
		
		getWitnessNames(target, witnessNames, repository, monitor);
		
		checkAndSaveWitnesses((ISCEvent) target, witnesses, predicates, witnessNames, event, monitor);
		
		repository.setState(savedLabelSymbolTable);

	}
	
	private void checkAndSaveWitnesses(
			ISCEvent target, 
			IWitness[] witnesses, 
			Predicate[] predicates,
			HashSet<String> witnessNames,
			IEvent event,
			IProgressMonitor monitor) throws RodinDBException {
		
		if (target == null)
			return;
		
		int index = 0;
		
		for (int i=0; i<witnesses.length; i++) {
			if (predicates[i] == null)
				continue;
			String label = witnesses[i].getLabel();
			if (witnessNames.contains(label)) {
				witnessNames.remove(label);
				createSCWitness(
						target, 
						WITNESS_NAME_PREFIX + index++,
						witnesses[i].getLabel(), 
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
					target, 
					WITNESS_NAME_PREFIX + index++, 
					name, 
					event, 
					btrue, 
					monitor);
		}
	}
	
	void createSCWitness(
			ISCEvent target, 
			String name,
			String label,
			IRodinElement source,
			Predicate predicate, 
			IProgressMonitor monitor) throws RodinDBException {
		ISCWitness scWitness = target.getSCWitness(name);
		scWitness.create(null, monitor);
		scWitness.setLabel(label, monitor);
		scWitness.setPredicate(predicate, null);
		scWitness.setSource(source, monitor);
	}

	private void getWitnessNames(
			IInternalParent target, 
			HashSet<String> witnessNames,
			IStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		
		if (target == null)
			return;
		
		IEventRefinesInfo eventRefinesInfo = (IEventRefinesInfo)
			repository.getState(IEventRefinesInfo.STATE_TYPE);
		
		if (eventRefinesInfo.isEmpty())
			return;
		
		IAbstractEventInfo abstractEventInfo = 
			eventRefinesInfo.getAbstractEventInfos().get(0);
		
		getLocalWitnessNames(abstractEventInfo, witnessNames, monitor);
		
		getGlobalWitnessNames(abstractEventInfo, witnessNames, monitor);
		
	}

	private void getGlobalWitnessNames(
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

	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(
			String symbol, ILabeledElement element, String component) throws CoreException {
		return new WitnessSymbolInfo(symbol, element, EventBAttributes.LABEL_ATTRIBUTE, component);
	}

}
