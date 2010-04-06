/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 * 	   Systerel - added check on primed identifiers
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.ILabeledElement;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCParameter;
import org.eventb.core.ISCWitness;
import org.eventb.core.IWitness;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IAccuracyInfo;
import org.eventb.core.sc.state.IConcreteEventInfo;
import org.eventb.core.sc.state.IEventLabelSymbolTable;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.state.SymbolFactory;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.symbolTable.EventLabelSymbolTable;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 * 
 */
public class MachineEventWitnessModule extends PredicateModule<IWitness> {

	public static final IModuleType<MachineEventWitnessModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID
					+ ".machineEventWitnessModule"); //$NON-NLS-1$

	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	private Predicate btrue;
	private FormulaFactory factory;
	private IConcreteEventInfo concreteEventInfo;

	private static String WITNESS_NAME_PREFIX = "WIT";

	private static int WITNESS_HASH_TABLE_SIZE = 31;

	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		// the name space of witness label is distinct from the name space of
		// other labels. Witness labels are variable names.

		ILabelSymbolTable savedLabelSymbolTable = labelSymbolTable;

		labelSymbolTable = new EventLabelSymbolTable(
				formulaElements.length * 4 / 3 + 1);

		repository.setState(labelSymbolTable);

		if (formulaElements.length > 0)
			checkAndType(element.getElementName(), repository, monitor);

		// the hash set provides a fast way to treat duplicates
		HashSet<String> witnessNames = new HashSet<String>(
				WITNESS_HASH_TABLE_SIZE);

		getWitnessNames(witnessNames, repository);

		checkAndSaveWitnesses((ISCEvent) target, witnessNames, element, monitor);

		if (witnessNames.size() != 0)
			concreteEventInfo.setNotAccurate();

		repository.setState(savedLabelSymbolTable);

	}

	// TODO adapt to new symbol table architecture: use createSCPredicates(target, namePrefix, index, monitor)
	private void checkAndSaveWitnesses(ISCEvent target,
			Set<String> witnessNames, IRodinElement event,
			IProgressMonitor monitor) throws RodinDBException {
		Set<String> permissible = new HashSet<String>(witnessNames);

		int index = 0;

		for (int i = 0; i < formulaElements.length; i++) {
			if (formulas[i] != null) {
				String label = formulaElements[i].getLabel();
				boolean labelIsNeeded = witnessNames.contains(label);
				if (labelIsNeeded) {
					witnessNames.remove(label);
					createSCWitness(target, WITNESS_NAME_PREFIX + index++,
							formulaElements[i].getLabel(), formulaElements[i],
							formulas[i], monitor);
				} else {
					if (permissible.contains(label)) {
						createProblemMarker(formulaElements[i],
								EventBAttributes.LABEL_ATTRIBUTE,
								GraphProblem.WitnessLabelNeedLessError, label);
					} else {
						createProblemMarker(formulaElements[i],
								EventBAttributes.LABEL_ATTRIBUTE,
								GraphProblem.WitnessLabelNotPermissible, label);
					}
				}
			}
		}

		for (String name : witnessNames) {
			createProblemMarker(event, GraphProblem.WitnessLabelMissingWarning,
					name);
			createSCWitness(target, WITNESS_NAME_PREFIX + index++, name, event,
					btrue, monitor);
		}
	}

	void createSCWitness(ISCEvent target, String name, String label,
			IRodinElement source, Predicate predicate, IProgressMonitor monitor)
			throws RodinDBException {

		if (target == null)
			return;

		// TODO save witnesses by means of symbol infos
		ISCWitness scWitness = target.getSCWitness(name);
		scWitness.create(null, monitor);
		scWitness.setLabel(label, monitor);
		scWitness.setPredicate(predicate, null);
		scWitness.setSource(source, monitor);
	}

	private void getWitnessNames(Set<String> witnessNames,
			ISCStateRepository repository) throws CoreException {

		IConcreteEventInfo eventRefinesInfo = (IConcreteEventInfo) repository
				.getState(IConcreteEventInfo.STATE_TYPE);

		if (eventRefinesInfo.eventIsNew())
			return;

		getLocalWitnessNames(eventRefinesInfo, witnessNames);

		// all actions must be identical; so we choose one arbitrarily
		IAbstractEventInfo abstractEventInfo = eventRefinesInfo
				.getAbstractEventInfos().get(0);

		getGlobalWitnessNames(abstractEventInfo, witnessNames);

	}

	private void getGlobalWitnessNames(IAbstractEventInfo abstractEventInfo,
			Set<String> witnessNames) throws CoreException {
		List<Assignment> assignments = abstractEventInfo.getActions();

		for (Assignment assignment : assignments) {

			if (assignment instanceof BecomesEqualTo)
				continue;

			FreeIdentifier[] identifiers = assignment.getAssignedIdentifiers();

			for (FreeIdentifier identifier : identifiers) {

				// there must be a variable symbol of this name in the symbol
				// table. We must check if it is disappearing or not.
				IIdentifierSymbolInfo symbolInfo = identifierSymbolTable
						.getSymbolInfo(identifier.getName());

				if (symbolInfo
						.getAttributeValue(EventBAttributes.CONCRETE_ATTRIBUTE))
					continue;

				FreeIdentifier primedIdentifier = identifier.withPrime(factory);

				witnessNames.add(primedIdentifier.getName());
			}

		}
	}

	private void getLocalWitnessNames(IConcreteEventInfo eventRefinesInfo,
			Set<String> witnessNames) throws CoreException {

		for (IAbstractEventInfo abstractEventInfo : eventRefinesInfo
				.getAbstractEventInfos()) {

			List<FreeIdentifier> identifiers = abstractEventInfo
					.getParameters();

			for (FreeIdentifier identifier : identifiers) {
				// if a symbol with the same name is found it can only be
				// a local variable of the concrete event.
				IIdentifierSymbolInfo symbolInfo = identifierSymbolTable
						.getSymbolInfo(identifier.getName());
				if (symbolInfo != null) {
					if (symbolInfo.getSymbolType() == ISCParameter.ELEMENT_TYPE
							&& !symbolInfo.hasError()) {
						continue;
					}
				}
				witnessNames.add(identifier.getName());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.sc.Module#initModule(org.eventb.core.sc.IStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		factory = FormulaFactory.getDefault();
		btrue = factory.makeLiteralPredicate(Formula.BTRUE, null);
		concreteEventInfo = (IConcreteEventInfo) repository
				.getState(IConcreteEventInfo.STATE_TYPE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.sc.Module#endModule(org.eventb.core.sc.IStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.endModule(element, repository, monitor);
		btrue = null;
		factory = null;
		concreteEventInfo = null;
	}

	@Override
	protected void makeProgress(IProgressMonitor monitor) {
		// no progress inside event
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eventb.internal.core.sc.modules.LabeledElementModule#
	 * getLabelSymbolTableFromRepository(org.eventb.core.sc.IStateRepository)
	 */
	@Override
	protected ILabelSymbolTable getLabelSymbolTableFromRepository(
			ISCStateRepository repository) throws CoreException {
		return (ILabelSymbolTable) repository
				.getState(IEventLabelSymbolTable.STATE_TYPE);
	}

	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(String symbol,
			ILabeledElement element, String component) throws CoreException {
		// generate appropriate error messages, if the label is not an
		// identifier
		// (we do not need to do anything else because the witness will be
		// invalid anyway!)
		IdentifierModule.parseIdentifier(symbol, element,
				EventBAttributes.LABEL_ATTRIBUTE, factory, this, true);
		return SymbolFactory.getInstance().makeLocalWitness(symbol, true, element,
				component);
	}

	@Override
	protected IWitness[] getFormulaElements(IRodinElement element)
			throws CoreException {
		IEvent event = (IEvent) element;
		return event.getWitnesses();
	}

	@Override
	protected IAccuracyInfo getAccuracyInfo(ISCStateRepository repository)
			throws CoreException {
		return (IAccuracyInfo) repository
				.getState(IConcreteEventInfo.STATE_TYPE);
	}

}
