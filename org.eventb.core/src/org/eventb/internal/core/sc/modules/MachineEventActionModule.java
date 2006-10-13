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
import org.eventb.core.ISCAction;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.IAcceptorModule;
import org.eventb.core.sc.IEventLabelSymbolTable;
import org.eventb.core.sc.ILabelSymbolTable;
import org.eventb.core.sc.IModuleManager;
import org.eventb.core.sc.IStateRepository;
import org.eventb.core.sc.symbolTable.IActionSymbolInfo;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;
import org.eventb.core.sc.symbolTable.ISymbolInfo;
import org.eventb.core.sc.symbolTable.IVariableSymbolInfo;
import org.eventb.internal.core.sc.ModuleManager;
import org.eventb.internal.core.sc.StaticChecker;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventActionModule extends AssignmentModule {

	public static final String MACHINE_EVENT_ACTION_ACCEPTOR = 
		EventBPlugin.PLUGIN_ID + ".machineEventActionAcceptor";

	private final IAcceptorModule[] modules;

	public MachineEventActionModule() {
		IModuleManager manager = ModuleManager.getModuleManager();
		modules = manager.getAcceptorModules(MACHINE_EVENT_ACTION_ACCEPTOR);
	}

	private static String ACTION_NAME_PREFIX = "ACT";
	private static String ACTION_REPAIR_PREFIX = "GEN";
	private static String ACTION_REPAIR_LABEL = "GEN";
	
	private boolean isInitialisation;
	private FormulaFactory factory;
	
	public void process(
			IRodinElement element, 
			IInternalParent target,
			IStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {

		IEvent event = (IEvent) element;
		
		IAction[] actions = event.getActions();
		
		Assignment[] assignments = new Assignment[actions.length];
	
		if (actions.length > 0)
			checkAndType(
					actions, 
					target,
					assignments,
					modules,
					StaticChecker.getParentName(event),
					repository,
					monitor);

		HashMap<String, Integer> assignedByAction = checkLHS(actions, assignments, monitor);
		commitActions(target, actions, assignments, null);
		if (isInitialisation)
			repairInitialisation(target, event, assignedByAction, monitor);

	}
	
	private HashMap<String, Integer> checkLHS(
			IAction[] actions, 
			Assignment[] assignments, 
			IProgressMonitor monitor) throws CoreException {
		HashMap<String, Integer> assignedByAction = new HashMap<String, Integer>(43);
		boolean[] error = getAssignedByActionMap(actions, assignments, assignedByAction);
		issueLHSProblemMarkers(actions, assignments, error, monitor);
		return assignedByAction;
	}

	private void issueLHSProblemMarkers(IAction[] actions, Assignment[] assignments, boolean[] error, IProgressMonitor monitor) throws RodinDBException, CoreException {
		for (int i=0; i<actions.length; i++) {
			if (assignments[i] == null)
				continue;
			IActionSymbolInfo actionSymbolInfo = 
				(IActionSymbolInfo) labelSymbolTable.getSymbolInfo(actions[i].getLabel(monitor));
			if (error[i]) {
				assignments[i] = null;
				createProblemMarker(
						actions[i], 
						getFormulaAttributeId(), 
						GraphProblem.ActionDisjointLHSError);
				actionSymbolInfo.setError();
			}
			actionSymbolInfo.setImmutable();
		}
	}

	private boolean[] getAssignedByActionMap(IAction[] actions, Assignment[] assignments, HashMap<String, Integer> assignedByAction) {
		boolean[] error = new boolean[actions.length];
		for (int i=0; i< actions.length; i++) {
			if (assignments[i] == null)
				continue;
			for (FreeIdentifier identifier : assignments[i].getAssignedIdentifiers()) {
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
			IInternalParent parent, 
			IAction[] actions, 
			Assignment[] assignments,
			IProgressMonitor monitor) throws RodinDBException {
		
		if (parent == null)
			return;
		
		int index = 0;
		
		for (int i=0; i<actions.length; i++) {
			if (assignments[i] == null)
				continue;
			saveAction(
					parent, 
					ACTION_NAME_PREFIX + index++, 
					actions[i].getLabel(monitor), 
					assignments[i], 
					actions[i], 
					monitor);
		}
	}
	
	private void saveAction(
			IInternalParent parent, 
			String dbName,
			String label, 
			Assignment assignment,
			IRodinElement source,
			IProgressMonitor monitor) throws RodinDBException {
		ISCAction scAction = 
			(ISCAction) parent.createInternalElement(
					ISCAction.ELEMENT_TYPE, 
					dbName, 
					null, 
					monitor);
		scAction.setLabel(label, monitor);
		scAction.setAssignment(assignment);
		scAction.setSource(source, monitor);
	}
	
	private void repairInitialisation(
			IInternalParent parent, 
			IEvent event, 
			HashMap<String, Integer> assignedByAction,
			IProgressMonitor monitor) throws RodinDBException {
		List<FreeIdentifier> patchLHS = new LinkedList<FreeIdentifier>();
		List<BoundIdentDecl> patchBound = new LinkedList<BoundIdentDecl>();
		for (ISymbolInfo symbolInfo : identifierSymbolTable.getParentTable()) {
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
		
		if (parent == null)
			return;
		
		if (patchLHS.size() > 0) {
			Predicate btrue = factory.makeLiteralPredicate(Formula.BTRUE, null);
			Assignment assignment = factory.makeBecomesSuchThat(patchLHS, patchBound, btrue, null);
			String label = createFreshLabel();
			saveAction(parent, ACTION_REPAIR_PREFIX, label, assignment, event, monitor);
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
			IStateRepository repository) throws CoreException {
		return (ILabelSymbolTable) repository.getState(IEventLabelSymbolTable.STATE_TYPE);
	}

	@Override
	protected void setImmutable(ILabelSymbolInfo symbolInfo) {
		// do nothing
	}

	@Override
	public void initModule(
			IRodinElement element, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		isInitialisation = ((IEvent) element).getLabel(monitor).equals(IEvent.INITIALISATION);
		factory = repository.getFormulaFactory();
	}

	@Override
	public void endModule(
			IRodinElement element, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		factory = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	protected String getFormulaAttributeId() {
		return EventBAttributes.ASSIGNMENT_ATTRIBUTE;
	}

}
