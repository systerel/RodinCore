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
 *     Systerel - fixed bug #2997671 using labels instead of fixed prefix
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCVariable;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BoundIdentDecl;
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
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 * 
 */
public class MachineEventActionModule extends AssignmentModule<IAction> {

	public static final IModuleType<MachineEventActionModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID + ".machineEventActionModule"); //$NON-NLS-1$

	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	private static String ACTION_NAME_PREFIX = "ACT";
	private static String ACTION_REPAIR_LABEL = "GEN";

	private IConcreteEventInfo concreteEventInfo;
	private boolean isInitialisation;
	private FormulaFactory factory;

	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		if (formulaElements.length > 0)
			checkAndType(element.getParent().getElementName(), repository,
					monitor);

		ISCEvent targetEvent = (ISCEvent) target;

		HashMap<String, Integer> assignedByAction = checkLHS(monitor);

		if (targetEvent != null) {
			createSCAssignments(targetEvent, ACTION_NAME_PREFIX, targetEvent
					.getSCActions().length, monitor);
		}

		if (isInitialisation)
			repairInitialisation(targetEvent, element, assignedByAction,
					monitor);

	}

	private HashMap<String, Integer> checkLHS(IProgressMonitor monitor)
			throws CoreException {
		HashMap<String, Integer> assignedByAction = new HashMap<String, Integer>(
				43);
		boolean[] error = getAssignedByActionMap(assignedByAction);
		issueLHSProblemMarkers(error, monitor);
		return assignedByAction;
	}

	private void issueLHSProblemMarkers(boolean[] error,
			IProgressMonitor monitor) throws RodinDBException, CoreException {
		for (int i = 0; i < formulaElements.length; i++) {
			if (formulas[i] == null)
				continue;
			ILabelSymbolInfo actionSymbolInfo = labelSymbolTable
					.getSymbolInfo(formulaElements[i].getLabel());
			if (error[i]) {
				formulas[i] = null;
				createProblemMarker(formulaElements[i],
						getFormulaAttributeType(),
						GraphProblem.ActionDisjointLHSError);
				actionSymbolInfo.setError();
			}
			actionSymbolInfo.makeImmutable();
		}
		if (error[formulaElements.length]) {
			if (isInitialisation) {
				createProblemMarker(concreteEventInfo.getEvent(),
						EventBAttributes.EXTENDED_ATTRIBUTE,
						GraphProblem.ActionDisjointLHSWarining);
			} else {
				IRefinesEvent refinesEvent = concreteEventInfo
						.getRefinesClauses().get(0);
				createProblemMarker(refinesEvent,
						EventBAttributes.TARGET_ATTRIBUTE,
						GraphProblem.ActionDisjointLHSWarining);
			}
		}
	}

	private boolean[] getAssignedByActionMap(
			HashMap<String, Integer> assignedByAction) throws CoreException {
		int last = formulaElements.length;
		boolean[] error = new boolean[last + 1];

		if (concreteEventInfo.getSymbolInfo().hasAttribute(
				EventBAttributes.EXTENDED_ATTRIBUTE)
				&& concreteEventInfo.getSymbolInfo().getAttributeValue(
						EventBAttributes.EXTENDED_ATTRIBUTE)
				&& concreteEventInfo.getAbstractEventInfos().size() > 0) {
			IAbstractEventInfo info = concreteEventInfo.getAbstractEventInfos()
					.get(0);
			for (Assignment assignment : info.getActions()) {
				for (FreeIdentifier identifier : assignment
						.getAssignedIdentifiers()) {
					assignedByAction.put(identifier.getName(), last);
				}
			}
		}

		for (int i = 0; i < last; i++) {
			if (formulas[i] == null)
				continue;
			for (FreeIdentifier identifier : formulas[i]
					.getAssignedIdentifiers()) {
				String name = identifier.getName();
				Integer conflict = assignedByAction.get(name);
				if (conflict == null)
					assignedByAction.put(name, i);
				else if (conflict == -1) {
					error[i] = true;
				} else {
					error[i] = true;
					error[conflict] = true;
					assignedByAction.put(name, -1);
				}
			}
		}
		return error;
	}

	private void saveAction(ISCEvent target, String dbName, String label,
			Assignment assignment, IRodinElement source,
			IProgressMonitor monitor) throws RodinDBException {
		ISCAction scAction = target.getSCAction(dbName);
		scAction.create(null, monitor);
		scAction.setLabel(label, monitor);
		scAction.setAssignment(assignment, null);
		scAction.setSource(source, monitor);
	}

	private void repairInitialisation(ISCEvent target, IRodinElement event,
			HashMap<String, Integer> assignedByAction, IProgressMonitor monitor)
			throws RodinDBException {
		List<FreeIdentifier> patchLHS = new LinkedList<FreeIdentifier>();
		List<BoundIdentDecl> patchBound = new LinkedList<BoundIdentDecl>();
		for (IIdentifierSymbolInfo symbolInfo : identifierSymbolTable
				.getParentTable().getSymbolInfosFromTop()) {
			if (symbolInfo.getSymbolType() == ISCVariable.ELEMENT_TYPE
					&& !symbolInfo.hasError()) {
				if (symbolInfo
						.getAttributeValue(EventBAttributes.CONCRETE_ATTRIBUTE)) {
					String name = symbolInfo.getSymbol();
					Integer a = assignedByAction.get(name);
					if (a == null || a == -1) {
						createProblemMarker(event,
								GraphProblem.InitialisationIncompleteWarning,
								name);
						FreeIdentifier identifier = factory.makeFreeIdentifier(
								name, null, symbolInfo.getType());
						patchLHS.add(identifier);
						patchBound.add(identifier.asPrimedDecl(factory));
					}
				}
			}
		}

		if (target == null)
			return;

		if (patchLHS.size() > 0) {
			concreteEventInfo.setNotAccurate();
			Predicate btrue = factory.makeLiteralPredicate(Formula.BTRUE, null);
			Assignment assignment = factory.makeBecomesSuchThat(patchLHS,
					patchBound, btrue, null);
			String label = createFreshLabel();
			saveAction(target, label, label, assignment, event,
					monitor);
		}
	}

	private String createFreshLabel() {
		String label = ACTION_REPAIR_LABEL;
		int index = 1;
		while (labelSymbolTable.containsKey(label))
			label += index++;
		return label;
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
	protected void setImmutable(ILabelSymbolInfo symbolInfo) {
		// do nothing
	}

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		concreteEventInfo = (IConcreteEventInfo) repository
				.getState(IConcreteEventInfo.STATE_TYPE);
		isInitialisation = concreteEventInfo.isInitialisation();
		factory = FormulaFactory.getDefault();
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		factory = null;
		concreteEventInfo = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	protected IAttributeType.String getFormulaAttributeType() {
		return EventBAttributes.ASSIGNMENT_ATTRIBUTE;
	}

	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(String symbol,
			ILabeledElement element, String component) throws CoreException {
		return SymbolFactory.getInstance().makeLocalAction(symbol, true, element,
				component);
	}

	@Override
	protected IAction[] getFormulaElements(IRodinElement element)
			throws CoreException {
		IEvent event = (IEvent) element;
		return event.getActions();
	}

	@Override
	protected IAccuracyInfo getAccuracyInfo(ISCStateRepository repository)
			throws CoreException {
		return (IAccuracyInfo) repository
				.getState(IConcreteEventInfo.STATE_TYPE);
	}

}
