/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - got factory from repository
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCParameter;
import org.eventb.core.ISCRefinesMachine;
import org.eventb.core.ISCVariable;
import org.eventb.core.IVariable;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IContextTable;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.IMachineAccuracyInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.state.SymbolFactory;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.AbstractEventInfo;
import org.eventb.internal.core.sc.AbstractEventTable;
import org.eventb.internal.core.sc.AbstractMachineInfo;
import org.eventb.internal.core.sc.Messages;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 * 
 */
public class MachineRefinesModule extends IdentifierCreatorModule {

	public static final IModuleType<MachineRefinesModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID + ".machineRefinesModule"); //$NON-NLS-1$

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	private static int ABSEVT_SYMTAB_SIZE = 1013;

	private IMachineRoot machineFile;
	private ISCMachineRoot scAbstractMachineFile;
	private IRefinesMachine refinesMachine;
	private AbstractEventTable abstractEventTable;
	private ITypeEnvironmentBuilder typeEnvironment;
	private IMachineAccuracyInfo accuracyInfo;

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		// now we can finish if there is no abstraction

		if (scAbstractMachineFile == null) {
			abstractEventTable.makeImmutable();
			return;
		}

		monitor.subTask(Messages.bind(Messages.progress_MachineRefines));

		saveRefinesMachine((ISCMachineRoot) target, null);

		if (!scAbstractMachineFile.isAccurate())
			accuracyInfo.setNotAccurate();

		IIdentifierSymbolTable abstractIdentifierSymbolTable = (IIdentifierSymbolTable) repository
				.getState(IIdentifierSymbolTable.STATE_TYPE);

		IContextTable contextTable = (IContextTable) repository
				.getState(IContextTable.STATE_TYPE);

		fetchSCMachine(abstractIdentifierSymbolTable, contextTable,
				repository.getFormulaFactory(), null);

		abstractEventTable.makeImmutable();

		monitor.worked(1);

	}

	private void saveRefinesMachine(ISCMachineRoot target,
			IProgressMonitor monitor) throws RodinDBException {
		ISCRefinesMachine scRefinesMachine = target.createChild(
				ISCRefinesMachine.ELEMENT_TYPE, null, monitor);
		scRefinesMachine.setAbstractSCMachine(scAbstractMachineFile.getRodinFile(), null);
		scRefinesMachine.setSource(refinesMachine, null);
	}

	/**
	 * Fetches all elements of the abstract machine and fills the symbol tables.
	 * All these elements are considered to be successfully checked and the
	 * corresponding symbol made immutable!
	 * 
	 * @param identifierSymbolTable
	 *            the identifier symbol table
	 * @param contextTable
	 *            the abstract contexts
	 * @param factory
	 *            the formula factory
	 * @param monitor
	 *            a progress monitor
	 * @throws CoreException
	 *             if there was a problem
	 */
	protected void fetchSCMachine(IIdentifierSymbolTable identifierSymbolTable,
			IContextTable contextTable, FormulaFactory factory,
			IProgressMonitor monitor) throws CoreException {

		fetchSCContexts(identifierSymbolTable, contextTable, factory, monitor);

		fetchSCVariables(identifierSymbolTable, factory, monitor);

		fetchSCEvents(factory, monitor);

	}

	protected void fetchSCEvents(FormulaFactory factory,
			IProgressMonitor monitor) throws CoreException {

		ISCEvent[] events = scAbstractMachineFile.getSCEvents();

		for (ISCEvent event : events) {

			fetchSCEvent(event, factory, monitor);

		}

	}

	protected void fetchSCContexts(
			IIdentifierSymbolTable identifierSymbolTable,
			IContextTable contextTable, FormulaFactory factory,
			IProgressMonitor monitor) throws CoreException {

		ISCInternalContext[] contexts = scAbstractMachineFile
				.getSCSeenContexts();

		for (ISCInternalContext context : contexts) {

			final String component = context.getElementName();

			contextTable.addContext(component, context);

			ISCCarrierSet[] sets = context.getSCCarrierSets();

			for (ISCCarrierSet set : sets) {
				IIdentifierSymbolInfo symbolInfo = fetchImportedSymbol(set,
						refinesMachine, identifierSymbolTable, factory,
						importedCarrierSetCreator);
				symbolInfo.makeImmutable();
			}

			ISCConstant[] constants = context.getSCConstants();

			for (ISCConstant constant : constants) {
				IIdentifierSymbolInfo symbolInfo = fetchImportedSymbol(
						constant, refinesMachine, identifierSymbolTable,
						factory, importedConstantCreator);
				symbolInfo.makeImmutable();
			}

		}

	}

	protected void fetchSCVariables(
			IIdentifierSymbolTable identifierSymbolTable,
			FormulaFactory factory, IProgressMonitor monitor)
			throws CoreException {

		ISCVariable[] variables = scAbstractMachineFile.getSCVariables();

		if (variables.length == 0)
			return;

		// load concrete variables into table to check for reuse of abstract
		// variables
		// this is done to attach problem markers to concrete variables rather
		// rather than to the refines clause (when possible)
		IVariable[] concreteVariables = machineFile.getVariables();
		Map<String, IVariable> varMap = new HashMap<String, IVariable>(
				concreteVariables.length * 4 / 3 + 1);
		for (IVariable variable : concreteVariables) {
			varMap.put(variable.getIdentifierString(), variable);
		}

		for (ISCVariable variable : variables) {
			String name = variable.getIdentifierString();
			Type type = variable.getType(factory);
			IVariable concreteVariable = varMap.get(name);
			IIdentifierSymbolInfo symbolInfo = null;
			boolean concrete = variable.isConcrete();
			if (concreteVariable == null || !concrete) {
				symbolInfo = importedVariableCreator
						.createIdentifierSymbolInfo(name, variable,
								refinesMachine);
				symbolInfo.setAttributeValue(EventBAttributes.SOURCE_ATTRIBUTE,
						variable.getSource());
				symbolInfo.setAttributeValue(
						EventBAttributes.ABSTRACT_ATTRIBUTE, concrete);
			} else {
				symbolInfo = SymbolFactory.getInstance().makeLocalVariable(
						name, true, concreteVariable,
						machineFile.getComponentName());
				symbolInfo.setAttributeValue(EventBAttributes.SOURCE_ATTRIBUTE,
						concreteVariable);
				symbolInfo.setAttributeValue(
						EventBAttributes.ABSTRACT_ATTRIBUTE, true);

			}
			symbolInfo.setAttributeValue(
					EventBAttributes.CONCRETE_ATTRIBUTE, false);
			insertIdentifierSymbolInfo(symbolInfo, type, identifierSymbolTable);
			symbolInfo.makeImmutable();
		}

	}

	private IIdentifierSymbolInfo fetchImportedSymbol(
			ISCIdentifierElement identifier, IInternalElement pointerElement,
			IIdentifierSymbolTable identifierSymbolTable,
			FormulaFactory factory, IIdentifierSymbolInfoCreator creator)
			throws CoreException {
		IIdentifierSymbolInfo identifierSymbolInfo = creator
				.createIdentifierSymbolInfo(identifier.getIdentifierString(),
						identifier, pointerElement);
		Type type = identifier.getType(factory);
		insertIdentifierSymbolInfo(identifierSymbolInfo, type,
				identifierSymbolTable);
		return identifierSymbolInfo;
	}

	private void insertIdentifierSymbolInfo(IIdentifierSymbolInfo symbolInfo,
			Type type, IIdentifierSymbolTable identifierSymbolTable)
			throws CoreException {

		symbolInfo.setType(type);

		identifierSymbolTable.putSymbolInfo(symbolInfo);

		typeEnvironment.addName(symbolInfo.getSymbol(), type);

	}

	protected void fetchSCEvent(ISCEvent event, FormulaFactory factory,
			IProgressMonitor monitor) throws CoreException {

		String label = event.getLabel();

		AbstractEventInfo abstractEventInfo;

		ITypeEnvironmentBuilder eventTypeEnvironment = typeEnvironment.makeBuilder();
		abstractEventInfo = new AbstractEventInfo(event, label, event
				.getConvergence(), fetchEventParameters(event,
				eventTypeEnvironment, factory), fetchEventGuards(event,
				eventTypeEnvironment, factory), fetchEventActions(event,
				eventTypeEnvironment, factory), refinesMachine);

		abstractEventTable.putAbstractEventInfo(abstractEventInfo);
	}

	private FreeIdentifier[] fetchEventParameters(ISCEvent event,
			ITypeEnvironmentBuilder eventTypeEnvironment, FormulaFactory factory)
			throws CoreException {
		ISCParameter[] parameters = event.getSCParameters();
		FreeIdentifier[] identifiers = new FreeIdentifier[parameters.length];

		for (int i = 0; i < parameters.length; i++) {
			identifiers[i] = parameters[i].getIdentifier(factory);
			eventTypeEnvironment.add(identifiers[i]);
		}

		return identifiers;
	}

	private Predicate[] fetchEventGuards(ISCEvent event,
			ITypeEnvironment eventTypeEnvironment, FormulaFactory factory)
			throws CoreException {
		ISCGuard[] guards = event.getSCGuards();
		Predicate[] predicates = new Predicate[guards.length];

		for (int i = 0; i < guards.length; i++) {
			predicates[i] = guards[i].getPredicate(eventTypeEnvironment);
		}
		return predicates;
	}

	private Assignment[] fetchEventActions(ISCEvent event,
			ITypeEnvironment eventTypeEnvironment, FormulaFactory factory)
			throws CoreException {
		ISCAction[] actions = event.getSCActions();
		Assignment[] assignments = new Assignment[actions.length];

		for (int i = 0; i < actions.length; i++) {
			assignments[i] = actions[i].getAssignment(eventTypeEnvironment);
		}
		return assignments;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.sc.ProcessorModule#initModule(org.rodinp.core.IRodinElement
	 * , org.eventb.core.sc.IStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		typeEnvironment = repository.getTypeEnvironment();

		IRodinFile rf = (IRodinFile) element;
		machineFile = (IMachineRoot) rf.getRoot();

		IRefinesMachine[] refinesMachines = machineFile.getRefinesClauses();

		if (refinesMachines.length > 1) {
			for (int k = 1; k < refinesMachines.length; k++) {
				createProblemMarker(refinesMachines[k],
						EventBAttributes.TARGET_ATTRIBUTE,
						GraphProblem.TooManyAbstractMachinesError);
			}
		}

		refinesMachine = refinesMachines.length == 0 ? null
				: refinesMachines[0];
		scAbstractMachineFile = null;

		if (refinesMachine != null) {
			if (refinesMachine.hasAbstractMachineName()) {
				scAbstractMachineFile = (ISCMachineRoot) refinesMachine
						.getAbstractSCMachine().getRoot();
			} else {
				createProblemMarker(refinesMachine,
						EventBAttributes.TARGET_ATTRIBUTE,
						GraphProblem.AbstractMachineNameUndefError);
			}
		}

		if (scAbstractMachineFile != null) {
			if (!scAbstractMachineFile.exists()) {
				createProblemMarker(refinesMachine,
						EventBAttributes.TARGET_ATTRIBUTE,
						GraphProblem.AbstractMachineNotFoundError,
						scAbstractMachineFile.getComponentName());

				scAbstractMachineFile = null;
			} else if (!scAbstractMachineFile.hasConfiguration()) {
				createProblemMarker(refinesMachine,
						EventBAttributes.TARGET_ATTRIBUTE,
						GraphProblem.AbstractMachineWithoutConfigurationError,
						scAbstractMachineFile.getComponentName());

				scAbstractMachineFile = null;
			}
		}

		repository.setState(new AbstractMachineInfo(scAbstractMachineFile,
				refinesMachine));

		abstractEventTable = new AbstractEventTable(ABSEVT_SYMTAB_SIZE);

		repository.setState(abstractEventTable);

		accuracyInfo = (IMachineAccuracyInfo) repository
				.getState(IMachineAccuracyInfo.STATE_TYPE);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.sc.ProcessorModule#endModule(org.rodinp.core.IRodinElement
	 * , org.eventb.core.sc.IStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		refinesMachine = null;
		scAbstractMachineFile = null;
		abstractEventTable = null;
		accuracyInfo = null;
	}

}
