/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.ISCGuard;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.IAbstractEventInfo;
import org.eventb.core.sc.IAcceptorModule;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.IModuleManager;
import org.eventb.core.sc.IRefinedEventTable;
import org.eventb.core.sc.IStateRepository;
import org.eventb.internal.core.sc.Messages;
import org.eventb.internal.core.sc.ModuleManager;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventGuardModule extends PredicateWithTypingModule {

	public static final String MACHINE_EVENT_GUARD_ACCEPTOR = 
		EventBPlugin.PLUGIN_ID + ".machineEventGuardAcceptor";

	private IAcceptorModule[] modules;

	public MachineEventGuardModule() {
		IModuleManager manager = ModuleManager.getModuleManager();
		modules = manager.getAcceptorModules(MACHINE_EVENT_GUARD_ACCEPTOR);
	}

	private static String GUARD_NAME_PREFIX = "GRD";

	public void process(
			IRodinElement element, 
			IInternalParent target,
			IStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {

		IEvent event = (IEvent) element;
		
		IGuard[] guards = event.getGuards();
		
		Predicate[] predicates = new Predicate[guards.length];
		
		if (guards.length == 0)
			return;
		
		checkAndType(
				guards, 
				target,
				predicates,
				modules,
				event.getElementName(),
				repository,
				monitor);
		
		saveGuards(target, guards, predicates, monitor);

	}
	
	private void saveGuards(
			IInternalParent parent, 
			IGuard[] guards, 
			Predicate[] predicates,
			IProgressMonitor monitor) throws RodinDBException {
		
		int index = 0;
		
		for (int i=0; i<guards.length; i++) {
			if (predicates[i] == null)
				continue;
			ISCGuard scGuard = 
				(ISCGuard) parent.createInternalElement(
						ISCGuard.ELEMENT_TYPE, 
						GUARD_NAME_PREFIX + index++, 
						null, 
						monitor);
			scGuard.setLabel(guards[i].getLabel(monitor), monitor);
			scGuard.setPredicate(predicates[i]);
			scGuard.setSource(guards[i], monitor);
		}
	}

	protected IRefinedEventTable refinedEventTable;
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.LabeledFormulaModule#typeCheckFormula(int, org.rodinp.core.IInternalElement[], org.eventb.core.ast.Formula[], org.eventb.core.ast.ITypeEnvironment)
	 */
	@Override
	protected ITypeEnvironment typeCheckFormula(
			int index, 
			IInternalElement[] formulaElements, 
			Formula[] formulas, 
			ITypeEnvironment typeEnvironment) throws CoreException {
		
		// local variables must not change their type from an abstract machine
		// to a concrete machine
		
		ITypeEnvironment inferredTypeEnvironment =
			super.typeCheckFormula(index, formulaElements, formulas, typeEnvironment);
		
		if (inferredTypeEnvironment == null)
			return null;
		
		boolean ok = true;
		
		ITypeEnvironment.IIterator iterator = inferredTypeEnvironment.getIterator();
		
		List<IAbstractEventInfo> eventInfos = refinedEventTable.getAbstractEventInfos();
		IAbstractEventInfo eventInfo = eventInfos.size() > 0 ? refinedEventTable.getAbstractEventInfos().get(0) : null;
			
		while (iterator.hasNext()) {
			iterator.advance();
			String name = iterator.getName();
			Type type = iterator.getType();
			
			if (eventInfo != null) {
			
				FreeIdentifier identifier = eventInfo.getIdentifier(name);
			
				if (identifier == null || identifier.getType().equals(type))
					continue;
			
				ok = false;
						
				issueMarker(IMarkerDisplay.SEVERITY_ERROR, formulaElements[index], 
						Messages.scuser_LocalVariableChangedTypeError, 
						name, type.toString(), identifier.getType().toString());
			}
		}
		
		return ok ? inferredTypeEnvironment : null;

	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.Module#initModule(org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		refinedEventTable = (IRefinedEventTable) repository.getState(IRefinedEventTable.STATE_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.Module#endModule(org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.endModule(element, repository, monitor);
		refinedEventTable = null;
	}

}
