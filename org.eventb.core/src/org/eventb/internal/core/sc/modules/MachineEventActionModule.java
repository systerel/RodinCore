/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eventb.core.ISCAction;
import org.eventb.core.ISCEvent;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IAccuracyInfo;
import org.eventb.core.sc.state.IEventAccuracyInfo;
import org.eventb.core.sc.state.IEventLabelSymbolTable;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.symbolTable.IActionSymbolInfo;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;
import org.eventb.core.sc.symbolTable.ISymbolInfo;
import org.eventb.core.sc.symbolTable.IVariableSymbolInfo;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.symbolTable.ActionSymbolInfo;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventActionModule extends AssignmentModule<IAction> {

	public static final IModuleType<MachineEventActionModule> MODULE_TYPE = 
		SCCore.getModuleType(EventBPlugin.PLUGIN_ID + ".machineEventActionModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	private static String ACTION_NAME_PREFIX = "ACT";
	private static String ACTION_REPAIR_PREFIX = "GEN";
	private static String ACTION_REPAIR_LABEL = "GEN";
	
	private boolean isInitialisation;
	private FormulaFactory factory;
	
	public void process(
			IRodinElement element, 
			IInternalParent target,
			ISCStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {

		if (formulaElements.length > 0)
			checkAndType(
					element.getParent().getElementName(),
					repository,
					monitor);

		ISCEvent targetEvent = (ISCEvent) target;
		
		HashMap<String, Integer> assignedByAction = checkLHS(monitor);
		commitActions(targetEvent, null);
		if (isInitialisation)
			repairInitialisation(targetEvent, element, assignedByAction, monitor);

	}
	
	private HashMap<String, Integer> checkLHS(IProgressMonitor monitor) throws CoreException {
		HashMap<String, Integer> assignedByAction = new HashMap<String, Integer>(43);
		boolean[] error = getAssignedByActionMap(assignedByAction);
		issueLHSProblemMarkers(error, monitor);
		return assignedByAction;
	}

	private void issueLHSProblemMarkers(boolean[] error, IProgressMonitor monitor) throws RodinDBException, CoreException {
		for (int i=0; i<formulaElements.length; i++) {
			if (formulas[i] == null)
				continue;
			IActionSymbolInfo actionSymbolInfo = 
				(IActionSymbolInfo) labelSymbolTable.getSymbolInfo(formulaElements[i].getLabel());
			if (error[i]) {
				formulas[i] = null;
				createProblemMarker(
						formulaElements[i], 
						getFormulaAttributeType(), 
						GraphProblem.ActionDisjointLHSError);
				actionSymbolInfo.setError();
			}
			actionSymbolInfo.makeImmutable();
		}
	}

	private boolean[] getAssignedByActionMap(HashMap<String, Integer> assignedByAction) {
		boolean[] error = new boolean[formulaElements.length];
		for (int i=0; i< formulaElements.length; i++) {
			if (formulas[i] == null)
				continue;
			for (FreeIdentifier identifier : formulas[i].getAssignedIdentifiers()) {
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

	private void commitActions(
			ISCEvent target, 
			IProgressMonitor monitor) throws RodinDBException {
		
		if (target == null)
			return;
		
		int index = 0;
		
		for (int i=0; i<formulaElements.length; i++) {
			if (formulas[i] == null)
				continue;
			saveAction(
					target, 
					ACTION_NAME_PREFIX + index++, 
					formulaElements[i].getLabel(), 
					formulas[i], 
					formulaElements[i], 
					monitor);
		}
	}
	
	private void saveAction(
			ISCEvent target, 
			String dbName,
			String label, 
			Assignment assignment,
			IRodinElement source,
			IProgressMonitor monitor) throws RodinDBException {
		ISCAction scAction = target.getSCAction(dbName);
		scAction.create(null, monitor);
		scAction.setLabel(label, monitor);
		scAction.setAssignment(assignment, null);
		scAction.setSource(source, monitor);
	}
	
	private void repairInitialisation(
			ISCEvent target, 
			IRodinElement event, 
			HashMap<String, Integer> assignedByAction,
			IProgressMonitor monitor) throws RodinDBException {
		List<FreeIdentifier> patchLHS = new LinkedList<FreeIdentifier>();
		List<BoundIdentDecl> patchBound = new LinkedList<BoundIdentDecl>();
		for (ISymbolInfo symbolInfo : identifierSymbolTable.getParentTable().getSymbolInfosFromTop()) {
			if (symbolInfo instanceof IVariableSymbolInfo && !symbolInfo.hasError()) {
				IVariableSymbolInfo variableSymbolInfo = (IVariableSymbolInfo) symbolInfo;
				if (variableSymbolInfo.isConcrete()) {
					String name = variableSymbolInfo.getSymbol();
					Integer a = assignedByAction.get(name);
					if (a == null || a == -1) {
						createProblemMarker(event, GraphProblem.InitialisationIncompleteWarning, name);
						FreeIdentifier identifier = 
							factory.makeFreeIdentifier(name, null, variableSymbolInfo.getType());
						patchLHS.add(identifier);
						patchBound.add(identifier.asPrimedDecl(factory));
					}
				}
			}
		}
		
		if (target == null)
			return;
		
		if (patchLHS.size() > 0) {
			accuracyInfo.setNotAccurate();
			Predicate btrue = factory.makeLiteralPredicate(Formula.BTRUE, null);
			Assignment assignment = factory.makeBecomesSuchThat(patchLHS, patchBound, btrue, null);
			String label = createFreshLabel();
			saveAction(target, ACTION_REPAIR_PREFIX, label, assignment, event, monitor);
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
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.LabeledElementModule#getLabelSymbolTableFromRepository(org.eventb.core.sc.IStateRepository)
	 */
	@Override
	protected ILabelSymbolTable getLabelSymbolTableFromRepository(
			ISCStateRepository repository) throws CoreException {
		return (ILabelSymbolTable) repository.getState(IEventLabelSymbolTable.STATE_TYPE);
	}

	@Override
	protected void setImmutable(ILabelSymbolInfo symbolInfo) {
		// do nothing
	}

	@Override
	public void initModule(
			IRodinElement element, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		isInitialisation = ((IEvent) element).getLabel().equals(IEvent.INITIALISATION);
		factory = FormulaFactory.getDefault();
	}

	@Override
	public void endModule(
			IRodinElement element, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		factory = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	protected IAttributeType.String getFormulaAttributeType() {
		return EventBAttributes.ASSIGNMENT_ATTRIBUTE;
	}

	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(String symbol, ILabeledElement element, String component) throws CoreException {
		return new ActionSymbolInfo(symbol, element, EventBAttributes.LABEL_ATTRIBUTE, component);
	}

	@Override
	protected IAction[] getFormulaElements(IRodinElement element) throws CoreException {
		IEvent event = (IEvent) element;
		return event.getActions();
	}

	@Override
	protected IAccuracyInfo getAccuracyInfo(ISCStateRepository repository) throws CoreException {
		return (IEventAccuracyInfo) repository.getState(IEventAccuracyInfo.STATE_TYPE);
	}

}
