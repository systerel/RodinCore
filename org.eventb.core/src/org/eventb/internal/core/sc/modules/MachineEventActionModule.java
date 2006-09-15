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
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.ISCAction;
import org.eventb.core.ast.Assignment;
import org.eventb.core.sc.IAcceptorModule;
import org.eventb.core.sc.IModuleManager;
import org.eventb.core.sc.IStateRepository;
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
		
		saveActions(target, actions, assignments, null);

	}
	
	private void saveActions(
			IInternalParent parent, 
			IAction[] actions, 
			Assignment[] assignments,
			IProgressMonitor monitor) throws RodinDBException {
		
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

}
