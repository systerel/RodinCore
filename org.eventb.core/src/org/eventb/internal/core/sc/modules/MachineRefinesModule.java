/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISCRefinesMachine;
import org.eventb.core.ISCVariable;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.state.IContextTable;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.core.sc.symbolTable.IVariableSymbolInfo;
import org.eventb.core.sc.util.GraphProblem;
import org.eventb.internal.core.sc.AbstractEventInfo;
import org.eventb.internal.core.sc.AbstractEventTable;
import org.eventb.internal.core.sc.AbstractMachineInfo;
import org.eventb.internal.core.sc.Messages;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineRefinesModule extends IdentifierCreatorModule {
	
	private static int ABSEVT_SYMTAB_SIZE = 1013;
	
	ISCMachineFile scMachineFile;
	IRefinesMachine refinesMachine;
	AbstractEventTable abstractEventTable;
	ITypeEnvironment typeEnvironment;

	public void process(
			IRodinElement element, 
			IInternalParent target,
			ISCStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {
		
		// now we can finish if there is no abstraction
		
		if (scMachineFile == null)
			return;
		
		monitor.subTask(Messages.bind(Messages.progress_MachineRefines));
		
		saveRefinesMachine((ISCMachineFile) target, null);
		
		IIdentifierSymbolTable abstractIdentifierSymbolTable =
			(IIdentifierSymbolTable) repository.getState(IIdentifierSymbolTable.STATE_TYPE);
		
		IContextTable contextTable =
			(IContextTable) repository.getState(IContextTable.STATE_TYPE);
		
		fetchSCMachine(
				abstractIdentifierSymbolTable, 
				contextTable,
				repository.getFormulaFactory(), 
				null);
		
		monitor.worked(1);
		
	}
	
	private static final String REFINES_NAME = "REF";

	private void saveRefinesMachine(ISCMachineFile target, IProgressMonitor monitor) throws RodinDBException {
		ISCRefinesMachine scRefinesMachine = target.getSCRefinesClause(REFINES_NAME);
		scRefinesMachine.create(null, monitor);
		scRefinesMachine.setAbstractSCMachine(scMachineFile, null);
	}
	
	/**
	 * Fetches all elements of the abstract machine and fills the symbol tables.
	 * All these elements are considered to be successfully checked and the corresponding
	 * symbol made immutable!
	 * 
	 * @param identifierSymbolTable the identifier symbol table
	 * @param contextTable the abstract contexts
	 * @param factory the formula factory
	 * @param monitor a progress monitor
	 * @throws CoreException if there was a problem
	 */
	protected void fetchSCMachine(
			IIdentifierSymbolTable identifierSymbolTable, 
			IContextTable contextTable,
			FormulaFactory factory, 
			IProgressMonitor monitor) throws CoreException {
		
		fetchSCContexts(
				identifierSymbolTable, 
				contextTable,
				factory, 
				monitor);
		
		fetchSCVariables(identifierSymbolTable, factory, monitor);
		
		fetchSCEvents(factory, monitor);
		
	}
	
	protected void fetchSCEvents(
			FormulaFactory factory, 
			IProgressMonitor monitor) throws CoreException {
		
		ISCEvent[] events = scMachineFile.getSCEvents();
		
		for (ISCEvent event : events) {
			
			fetchSCEvent(event, factory, monitor);
			
		}
		
		abstractEventTable.makeImmutable();
		
	}

	protected void fetchSCContexts(
			IIdentifierSymbolTable identifierSymbolTable, 
			IContextTable contextTable,
			FormulaFactory factory, 
			IProgressMonitor monitor) throws CoreException {
		
		ISCInternalContext[] contexts = scMachineFile.getSCSeenContexts();
		
		for (ISCInternalContext context : contexts) {
			
			final String component = context.getElementName();
			
			contextTable.addContext(component, context);
			
			ISCCarrierSet[] sets = context.getSCCarrierSets();
			
			for (ISCCarrierSet set : sets) {
				IIdentifierSymbolInfo symbolInfo =
					fetchSymbol(
							set, 
							refinesMachine, 
							identifierSymbolTable, 
							factory, 
							abstractCarrierSetCreator);
				symbolInfo.makeImmutable();
			}
			
			ISCConstant[] constants = context.getSCConstants();
			
			for (ISCConstant constant : constants) {
				IIdentifierSymbolInfo symbolInfo =
					fetchSymbol(
							constant, 
							refinesMachine, 
							identifierSymbolTable, 
							factory, 
							abstractConstantCreator);
				symbolInfo.makeImmutable();
			}
						
		}
		
	}
	
	protected void fetchSCVariables(
			IIdentifierSymbolTable identifierSymbolTable, 
			FormulaFactory factory, 
			IProgressMonitor monitor) throws CoreException {
		
		ISCVariable[] variables = scMachineFile.getSCVariables();
		
		if (variables.length == 0)
			return;
		
		for (ISCVariable variable : variables) {
			IVariableSymbolInfo symbolInfo = (IVariableSymbolInfo)
				fetchSymbol(
						variable, 
						refinesMachine, 
						identifierSymbolTable, 
						factory, 
						abstractVariableCreator);
			if (variable.isForbidden())
				symbolInfo.setForbidden();
			symbolInfo.makeImmutable();
		}
		
	}

	protected IIdentifierSymbolInfo fetchSymbol(
			ISCIdentifierElement identifier, 
			IInternalElement pointerElement, 
			IIdentifierSymbolTable identifierSymbolTable,
			FormulaFactory factory,
			IIdentifierSymbolInfoCreator creator) throws CoreException {
		
		String name = identifier.getIdentifierString();
		
		Type type = identifier.getType(factory);
		
		IIdentifierSymbolInfo symbolInfo = 
			creator.createIdentifierSymbolInfo(name, identifier, pointerElement);
		
		symbolInfo.setType(type);
		
		symbolInfo.makeVisible();
		
		identifierSymbolTable.putSymbolInfo(symbolInfo);
		
		typeEnvironment.addName(name, type);
		
		return symbolInfo;
		
	}

	protected void fetchSCEvent(
			ISCEvent event, 
			FormulaFactory factory,
			IProgressMonitor monitor) throws CoreException {
		
		String label = event.getLabel();
		
		AbstractEventInfo abstractEventInfo;
		
		ITypeEnvironment eventTypeEnvironment = factory.makeTypeEnvironment();
		eventTypeEnvironment.addAll(typeEnvironment);
		abstractEventInfo =
			new AbstractEventInfo(
					event,
					label, 
					fetchEventVariables(event, eventTypeEnvironment, factory),
					fetchEventGuards(event, eventTypeEnvironment, factory),
					fetchEventActions(event, eventTypeEnvironment, factory));
		
		abstractEventTable.putAbstractEventInfo(abstractEventInfo);
	}
	
	private FreeIdentifier[] fetchEventVariables(
			ISCEvent event, 
			ITypeEnvironment eventTypeEnvironment,
			FormulaFactory factory) throws CoreException {
		ISCVariable[] variables = event.getSCVariables();
		FreeIdentifier[] identifiers = new FreeIdentifier[variables.length];
		
		for (int i=0; i<variables.length; i++) {
			identifiers[i] = variables[i].getIdentifier(factory);
			eventTypeEnvironment.add(identifiers[i]);
		}
		
		return identifiers;
	}
	
	private Predicate[] fetchEventGuards(
			ISCEvent event, 
			ITypeEnvironment eventTypeEnvironment,
			FormulaFactory factory) throws CoreException {
		ISCGuard[] guards = event.getSCGuards();
		Predicate[] predicates = new Predicate[guards.length];
		
		for (int i=0; i<guards.length; i++) {
			predicates[i] = guards[i].getPredicate(factory, eventTypeEnvironment);
		}
		return predicates;
	}
	
	private Assignment[] fetchEventActions(
			ISCEvent event, 
			ITypeEnvironment eventTypeEnvironment,
			FormulaFactory factory) throws CoreException {
		ISCAction[] actions = event.getSCActions();
		Assignment[] assignments = new Assignment[actions.length];
		
		for (int i=0; i<actions.length; i++) {
			assignments[i] = actions[i].getAssignment(factory, eventTypeEnvironment);
		}
		return assignments;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {

		typeEnvironment = repository.getTypeEnvironment();
		
		IMachineFile machineFile = (IMachineFile) element;
		
		IRefinesMachine[] refinesMachines = machineFile.getRefinesClauses();
		
		if (refinesMachines.length > 1) {
			for (int k=1; k<refinesMachines.length; k++) {
				createProblemMarker(
						refinesMachines[k], 
						EventBAttributes.TARGET_ATTRIBUTE, 
						GraphProblem.TooManyAbstractMachinesError);
			}
		}
		
		refinesMachine = refinesMachines.length == 0 ? null : refinesMachines[0];
		
		scMachineFile = 
			(refinesMachine == null) ? null : refinesMachine.getAbstractSCMachine();
		
		if (scMachineFile != null && !scMachineFile.exists()) {
			createProblemMarker(
					refinesMachine, 
					EventBAttributes.TARGET_ATTRIBUTE, 
					GraphProblem.AbstractMachineNotFoundError);
			
			scMachineFile = null;
		}
		
		repository.setState(new AbstractMachineInfo(scMachineFile));
		
		abstractEventTable = 
			new AbstractEventTable(ABSEVT_SYMTAB_SIZE);
		
		repository.setState(abstractEventTable);
		
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		refinesMachine = null;
		scMachineFile = null;
		abstractEventTable = null;
	}

}

