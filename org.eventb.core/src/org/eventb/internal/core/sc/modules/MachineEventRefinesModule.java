/*******************************************************************************
 * Copyright (c) 2008 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soton - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.ISCAction;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IAbstractEventTable;
import org.eventb.core.sc.state.IAbstractMachineInfo;
import org.eventb.core.sc.state.IConcreteEventInfo;
import org.eventb.core.sc.state.IConcreteEventTable;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.IMachineLabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 * 
 */
public class MachineEventRefinesModule extends SCFilterModule {

	public static final IModuleType<MachineEventRefinesModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID
					+ ".machineEventRefinesModule"); //$NON-NLS-1$

	private IAbstractMachineInfo abstractMachineInfo;

	private IAbstractEventTable abstractEventTable;

	private IConcreteEventTable concreteEventTable;

	private ILabelSymbolTable labelSymbolTable;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.internal.core.tool.types.ISCFilterModule#accept(org.rodinp
	 * .core.IRodinElement, org.eventb.core.sc.state.ISCStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IEvent event = (IEvent) element;
		String eventLabel = event.getLabel();
		ILabelSymbolInfo symbolInfo = labelSymbolTable
				.getSymbolInfo(eventLabel);
		IRefinesEvent[] refinesEvents = event.getRefinesClauses();
		if (eventLabel.equals(IEvent.INITIALISATION)) {
			return checkInitRefinement(event, symbolInfo, refinesEvents);
		} else {
			return fetchRefinement((IMachineRoot) event.getParent(), event,
					symbolInfo);
		}
	}

	private boolean fetchRefinement(IMachineRoot machineFile, IEvent event,
			ILabelSymbolInfo symbolInfo) throws RodinDBException, CoreException {

		boolean found = fetchRefineData(symbolInfo, event.getRefinesClauses());
		if (!found) {
			IAbstractEventInfo info = abstractEventTable
					.getAbstractEventInfo(symbolInfo.getSymbol());
			if (info != null)
				createProblemMarker(event,
						GraphProblem.InconsistentEventLabelWarning, symbolInfo.getSymbol());
		}

		return true;
	}

	private boolean fetchRefineData(ILabelSymbolInfo symbolInfo,
			IRefinesEvent[] refinesEvents) throws CoreException {

		IConcreteEventInfo concreteInfo = concreteEventTable
				.getConcreteEventInfo(symbolInfo.getSymbol());

		boolean found = false;

		ArrayList<String> abstractLabels = (refinesEvents.length > 1) ? new ArrayList<String>(
				refinesEvents.length)
				: null;

		HashSet<String> typeErrors = (refinesEvents.length > 1) ? new HashSet<String>(
				37)
				: null;
		Hashtable<String, Type> types = (refinesEvents.length > 1) ? new Hashtable<String, Type>(
				37)
				: null;

		boolean firstAction = true;
		boolean actionError = false;
		Hashtable<String, String> actions = (refinesEvents.length > 1) ? new Hashtable<String, String>(
				43)
				: null;

		for (int i = 0; i < refinesEvents.length; i++) {

			if (!refinesEvents[i].hasAbstractEventLabel()) {
				createProblemMarker(refinesEvents[i],
						EventBAttributes.TARGET_ATTRIBUTE,
						GraphProblem.AbstractEventLabelUndefError);
				continue;
			}

			String abstractLabel = refinesEvents[i].getAbstractEventLabel();

			// filter duplicates
			if (abstractLabels != null)
				if (abstractLabels.contains(abstractLabel)) {
					createProblemMarker(refinesEvents[i],
							EventBAttributes.TARGET_ATTRIBUTE,
							GraphProblem.AbstractEventLabelConflictWarning,
							abstractLabel);
					continue;
				} else
					abstractLabels.add(abstractLabel);

			if (abstractLabel.equals(IEvent.INITIALISATION)) {
				createProblemMarker(refinesEvents[i],
						EventBAttributes.TARGET_ATTRIBUTE,
						GraphProblem.InitialisationRefinedError);
				issueRefinementErrorMarker(symbolInfo);
				continue;
			}

			IAbstractEventInfo abstractInfo = getAbstractEventInfoForLabel(
					symbolInfo, abstractLabel, refinesEvents[i],
					EventBAttributes.TARGET_ATTRIBUTE);

			if (abstractInfo == null)
				continue;

			if (symbolInfo.getSymbol().equals(abstractInfo.getEventLabel()))
				found = true;

			checkForParameterTypeErrors(symbolInfo, typeErrors, types,
					abstractInfo);

			if (actions != null && !actionError)
				if (firstAction) {
					for (ISCAction action : abstractInfo.getEvent()
							.getSCActions()) {
						actions.put(action.getLabel(), action
								.getAssignmentString());
					}
					firstAction = false;
				} else {
					actionError = checkAbstractActionAccordance(symbolInfo,
							actions, abstractInfo);
				}

			concreteInfo.getAbstractEventInfos().add(abstractInfo);
			concreteInfo.getRefinesClauses().add(refinesEvents[i]);

			// this is a pretty rough distinction. But it should be sufficient
			// in practice.
			if (refinesEvents.length == 1) {
				abstractInfo.getSplitters().add(concreteInfo);
			} else {
				abstractInfo.getMergers().add(concreteInfo);
			}
		}

		concreteInfo.makeImmutable();

		return found;
	}

	private boolean checkAbstractActionAccordance(ILabelSymbolInfo symbolInfo,
			Hashtable<String, String> actions, IAbstractEventInfo abstractInfo)
			throws CoreException {
		ISCAction[] scActions = abstractInfo.getEvent().getSCActions();
		boolean ok = scActions.length == actions.size();
		if (ok)
			for (ISCAction action : scActions) {
				String assignment = actions.get(action.getLabel());
				String other = action.getAssignmentString();
				if (assignment != null && assignment.equals(other))
					continue;
				if (assignment == null || actions.containsValue(other)) {
					createProblemMarker(symbolInfo.getProblemElement(),
							GraphProblem.EventMergeLabelError);
					symbolInfo.setError();
					return true;
				}
				ok = false;
				break;
			}
		if (!ok) {
			createProblemMarker(symbolInfo.getProblemElement(),
					GraphProblem.EventMergeActionError);
			symbolInfo.setError();
			return true;
		}
		return false;
	}

	private void checkForParameterTypeErrors(ILabelSymbolInfo symbolInfo,
			HashSet<String> typeErrors, Hashtable<String, Type> types,
			IAbstractEventInfo abstractInfo) throws RodinDBException,
			CoreException {
		if (types != null)
			for (FreeIdentifier identifier : abstractInfo.getParameters()) {
				String name = identifier.getName();
				Type newType = identifier.getType();
				Type type = types.put(name, newType);
				if (type == null || type.equals(newType))
					continue;
				if (typeErrors.add(name)) {
					createProblemMarker(symbolInfo.getProblemElement(),
							GraphProblem.EventMergeVariableTypeError, name);
					symbolInfo.setError();
				}
			}
	}

	private boolean checkInitRefinement(IEvent event,
			ILabelSymbolInfo symbolInfo, IRefinesEvent[] refinesEvents)
			throws CoreException {
		if (refinesEvents.length > 0) {
			for (IRefinesEvent refinesEvent : refinesEvents) {
				createProblemMarker(refinesEvent,
						EventBAttributes.TARGET_ATTRIBUTE,
						GraphProblem.InitialisationRefinesEventWarning);
			}
		}

		if (abstractMachineInfo.getAbstractMachine() != null) {
			IAbstractEventInfo abstractInfo = getAbstractEventInfoForLabel(
					symbolInfo, symbolInfo.getSymbol(), event, null);

			if (abstractInfo == null) {
				symbolInfo.setError();
				return false;
			} else {
				IConcreteEventInfo concreteInfo = concreteEventTable
						.getConcreteEventInfo(symbolInfo.getSymbol());
				abstractInfo.getSplitters().add(concreteInfo);
				concreteInfo.getAbstractEventInfos().add(abstractInfo);
				concreteInfo.makeImmutable();
				return true;
			}
		} else {
			return true;
		}
	}

	private void issueRefinementErrorMarker(ILabelSymbolInfo symbolInfo)
			throws CoreException {
		if (!symbolInfo.hasError())
			createProblemMarker(symbolInfo.getProblemElement(),
					GraphProblem.EventRefinementError);
		symbolInfo.setError();
	}

	private IAbstractEventInfo getAbstractEventInfoForLabel(
			ILabelSymbolInfo symbolInfo, String abstractLabel,
			IInternalElement element, IAttributeType attributeType)
			throws CoreException {
		IAbstractEventInfo info = abstractEventTable
				.getAbstractEventInfo(abstractLabel);

		if (info == null) {
			if (attributeType == null)
				createProblemMarker(element,
						GraphProblem.AbstractEventNotFoundError, abstractLabel);
			else
				createProblemMarker(element, attributeType,
						GraphProblem.AbstractEventNotFoundError, abstractLabel);
			info = null;
			issueRefinementErrorMarker(symbolInfo);
		}
		return info;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.core.tool.types.IModule#getModuleType()
	 */
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		abstractEventTable = (IAbstractEventTable) repository
				.getState(IAbstractEventTable.STATE_TYPE);

		concreteEventTable = (IConcreteEventTable) repository
				.getState(IConcreteEventTable.STATE_TYPE);

		abstractMachineInfo = (IAbstractMachineInfo) repository
				.getState(IAbstractMachineInfo.STATE_TYPE);

		labelSymbolTable = (ILabelSymbolTable) repository
				.getState(IMachineLabelSymbolTable.STATE_TYPE);
	}

	@Override
	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		abstractEventTable = null;
		concreteEventTable = null;
		abstractMachineInfo = null;
		super.endModule(repository, monitor);
	}

}
