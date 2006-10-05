/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.ISCAction;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.IAcceptorModule;
import org.eventb.core.sc.IEventLabelSymbolTable;
import org.eventb.core.sc.ILabelSymbolTable;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.IModuleManager;
import org.eventb.core.sc.IStateRepository;
import org.eventb.core.sc.symbolTable.IActionSymbolInfo;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;
import org.eventb.internal.core.sc.Messages;
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

	private IAcceptorModule[] modules;

	public MachineEventActionModule() {
		IModuleManager manager = ModuleManager.getModuleManager();
		modules = manager.getAcceptorModules(MACHINE_EVENT_ACTION_ACCEPTOR);
	}

	private static String ACTION_NAME_PREFIX = "ACT";

	public void process(
			IRodinElement element, 
			IInternalParent target,
			IStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {

		IEvent event = (IEvent) element;
		
		IAction[] actions = event.getActions();
		
		if (actions.length == 0)
			return;
		
		Assignment[] assignments = new Assignment[actions.length];
	
		checkAndType(
				actions, 
				target,
				assignments,
				modules,
				StaticChecker.getParentName(event),
				repository,
				monitor);

		checkLHS(actions, assignments, monitor);
		saveActions(target, actions, assignments, null);

	}
	
	private void checkLHS(
			IAction[] actions, 
			Assignment[] assignments, 
			IProgressMonitor monitor) throws CoreException {
		HashMap<String, Integer> conflicts = new HashMap<String, Integer>(43);
		boolean[] error = new boolean[actions.length];
		for (int i=0; i< actions.length; i++) {
			if (assignments[i] == null)
				continue;
			for (FreeIdentifier identifier : assignments[i].getAssignedIdentifiers()) {
				String name = identifier.getName();
				Integer conflict = conflicts.get(name);
				if (conflict == null)
					conflicts.put(name, i);
				else if (conflict == -1) {
					error[i] = true;
				} else {
					error[i] = true;
					error[conflict] = true;
					conflicts.put(name, -1);
				}
			}
		}
		for (int i=0; i<actions.length; i++) {
			if (assignments[i] == null)
				continue;
			IActionSymbolInfo actionSymbolInfo = 
				(IActionSymbolInfo) labelSymbolTable.getSymbolInfo(actions[i].getLabel(monitor));
			if (error[i]) {
				assignments[i] = null;
				issueMarker(
						IMarkerDisplay.SEVERITY_ERROR, 
						actions[i], 
						Messages.scuser_ActionDisjointLHSError);
				actionSymbolInfo.setError();
			}
			actionSymbolInfo.setImmutable();
		}
	}

	private void saveActions(
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
			ISCAction scAction = 
				(ISCAction) parent.createInternalElement(
						ISCAction.ELEMENT_TYPE, 
						ACTION_NAME_PREFIX + index++, 
						null, 
						monitor);
			scAction.setLabel(actions[i].getLabel(monitor), monitor);
			scAction.setAssignment(assignments[i]);
			scAction.setSource(actions[i], monitor);
		}
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

}
