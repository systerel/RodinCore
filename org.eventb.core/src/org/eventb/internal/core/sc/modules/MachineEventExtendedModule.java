/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.ISCVariable;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IConcreteEventInfo;
import org.eventb.core.sc.state.IConcreteEventTable;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.IMachineLabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 * 
 */
public class MachineEventExtendedModule extends SCFilterModule {

	public static final IModuleType<MachineEventExtendedModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID
					+ ".machineEventExtendedModule"); //$NON-NLS-1$

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	private ILabelSymbolTable labelSymbolTable;
	private IIdentifierSymbolTable identifierSymbolTable;
	private IConcreteEventTable concreteEventTable;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.sc.IFilterModule#accept(org.rodinp.core.IRodinElement,
	 * org.eventb.core.state.IStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {

		IEvent event = (IEvent) element;
		String eventLabel = event.getLabel();

		ILabelSymbolInfo eventSymbolInfo = labelSymbolTable
				.getSymbolInfo(eventLabel);
		IConcreteEventInfo eventInfo = concreteEventTable
				.getConcreteEventInfo(eventLabel);

		boolean extended;

		if (event.hasExtended())
			extended = event.isExtended();
		else {
			createProblemMarker(event, EventBAttributes.EXTENDED_ATTRIBUTE,
					GraphProblem.ExtendedUndefError);

			return false;
		}

		eventSymbolInfo.setAttributeValue(
				EventBAttributes.EXTENDED_ATTRIBUTE, extended);
		
		if (extended) {

			boolean ok = checkSplit(event, eventLabel, eventInfo);

			if (eventInfo.getAbstractEventInfos().size() > 0) {
				IAbstractEventInfo abstractEventInfo = eventInfo
						.getAbstractEventInfos().get(0);

				ok &= checkFormulas(event, eventSymbolInfo, abstractEventInfo,
						abstractEventInfo.getGuards());
				ok &= checkFormulas(event, eventSymbolInfo, abstractEventInfo,
						abstractEventInfo.getActions());
			}

			return ok;

		} else {
			return true;
		}
	}

	private boolean checkSplit(IEvent event, String eventLabel,
			IConcreteEventInfo eventInfo) throws CoreException {
		List<IRefinesEvent> refinesEvents = eventInfo.getRefinesClauses();
		boolean isInitialisation = event.getLabel().equals(
				IEvent.INITIALISATION);
		switch (refinesEvents.size()) {
		case 0:
			if (isInitialisation)
				return true;
			else {
				createProblemMarker(event, EventBAttributes.EXTENDED_ATTRIBUTE,
						GraphProblem.EventExtendedUnrefinedError, eventLabel);
				return false;
			}
		case 1:
			return true;
		default:
			if (!isInitialisation) {
				createProblemMarker(event, EventBAttributes.EXTENDED_ATTRIBUTE,
						GraphProblem.EventExtendedMergeError, eventLabel);
			}
			return false;
		}
	}

	private <T extends Formula<T>> boolean checkFormulas(IEvent event,
			ILabelSymbolInfo eventSymbolInfo,
			IAbstractEventInfo abstractEventInfo, List<T> formulas)
			throws CoreException {

		boolean ok = true;

		LinkedList<String> seenFaultyIdents = new LinkedList<String>();

		for (T formula : formulas) {
			for (FreeIdentifier identifier : formula.getFreeIdentifiers()) {
				String name = identifier.getName();
				boolean found = abstractEventInfo.getParameter(name) != null;
				if (!found) {
					IIdentifierSymbolInfo symbolInfo = identifierSymbolTable
							.getSymbolInfo(name);
					if (symbolInfo == null || symbolInfo.hasError()) {
						createIdentProblem(event, name, seenFaultyIdents,
								GraphProblem.UndeclaredFreeIdentifierError);
						ok = false;
					} else if (symbolInfo.getSymbolType() == ISCVariable.ELEMENT_TYPE) {
						if (!symbolInfo
								.getAttributeValue(EventBAttributes.CONCRETE_ATTRIBUTE)) {
							createIdentProblem(event, name, seenFaultyIdents,
									GraphProblem.VariableHasDisappearedError);
							ok = false;
						}
					}
				}
			}
		}

		return ok;
	}

	private void createIdentProblem(IEvent event, String name,
			LinkedList<String> seenFaultyIdents, IRodinProblem problem)
			throws RodinDBException {
		if (!seenFaultyIdents.contains(name)) {
			seenFaultyIdents.add(name);
			createProblemMarker(event,
					GraphProblem.UndeclaredFreeIdentifierError, name);
		}
	}

	@Override
	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		labelSymbolTable = (ILabelSymbolTable) repository
				.getState(IMachineLabelSymbolTable.STATE_TYPE);
		identifierSymbolTable = (IIdentifierSymbolTable) repository
				.getState(IIdentifierSymbolTable.STATE_TYPE);
		concreteEventTable = (IConcreteEventTable) repository
				.getState(IConcreteEventTable.STATE_TYPE);
	}

	@Override
	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		labelSymbolTable = null;
		identifierSymbolTable = null;
		concreteEventTable = null;
		super.endModule(repository, monitor);
	}

}
