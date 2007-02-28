/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IEvent;
import org.eventb.core.ISCEvent;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.ICurrentEvent;
import org.eventb.core.sc.state.IEventRefinesInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.state.IVariantInfo;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventConvergenceModule extends SCProcessorModule {
	
	public static final IModuleType<MachineEventConvergenceModule> MODULE_TYPE = 
		SCCore.getModuleType(EventBPlugin.PLUGIN_ID + ".machineEventConvergenceModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	private IVariantInfo variantInfo;
	private IEventRefinesInfo eventRefinesInfo;
	private ICurrentEvent currentEvent;

	@Override
	public void initModule(
			IRodinElement element, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		variantInfo = (IVariantInfo) repository.getState(IVariantInfo.STATE_TYPE);
		eventRefinesInfo = (IEventRefinesInfo) repository.getState(IEventRefinesInfo.STATE_TYPE);
		currentEvent = (ICurrentEvent) repository.getState(ICurrentEvent.STATE_TYPE);
	}
	
	private IConvergenceElement.Convergence concreteConvergence;
	private IConvergenceElement.Convergence abstractConvergence;

	public void process(
			IRodinElement element, 
			IInternalParent target, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		IEvent event = (IEvent) element;
		
		concreteConvergence = currentEvent.getConvergence();
		
		if (currentEvent.isInitialisation()) {
			if (concreteConvergence != IConvergenceElement.Convergence.ORDINARY) {
				concreteConvergence = IConvergenceElement.Convergence.ORDINARY;
				createProblemMarker(
						event, 
						EventBAttributes.CONVERGENCE_ATTRIBUTE, 
						GraphProblem.InitialisationNotOrdinaryWarning);
			}
		} else {
		
			List<IAbstractEventInfo> abstractEventInfos = eventRefinesInfo.getAbstractEventInfos();
			
			if (abstractEventInfos.size() != 0) { // not a new event
				
				checkAbstractConvergence(event, abstractEventInfos, null);
				
			}
			checkVariantConvergence(event);
		}
		
		saveConvergence((ISCEvent) target, null);
	}
	
	void saveConvergence(
			ISCEvent target, 
			IProgressMonitor monitor) throws RodinDBException {
		if (target == null)
			return;
		target.setConvergence(concreteConvergence, monitor);
	}
	
	private void checkAbstractConvergence(
			IInternalElement element, 
			List<IAbstractEventInfo> abstractEventInfos, 
			IProgressMonitor monitor) throws CoreException {
		
		getAbstractConvergence(abstractEventInfos);
		
		if (abstractConvergence == IConvergenceElement.Convergence.ORDINARY
				&& concreteConvergence != IConvergenceElement.Convergence.ORDINARY) {
			createProblemMarker(
					element, 
					EventBAttributes.CONVERGENCE_ATTRIBUTE, 
					GraphProblem.OrdinaryFaultyConvergenceWarning);
			concreteConvergence = IConvergenceElement.Convergence.ORDINARY;
		} 
	}

	private void getAbstractConvergence(List<IAbstractEventInfo> abstractEventInfos) throws RodinDBException {

		List<IConvergenceElement.Convergence> convergences = 
			new ArrayList<IConvergenceElement.Convergence>(abstractEventInfos.size());
		
		for (IAbstractEventInfo abstractEventInfo : abstractEventInfos) {
			
			convergences.add(abstractEventInfo.getConvergence());
			
		}
		
		abstractConvergence = Collections.min(convergences);
	}

	private void checkVariantConvergence(IInternalElement element) throws CoreException {
		
		if (variantInfo.getExpression() == null)
			if (concreteConvergence == IConvergenceElement.Convergence.CONVERGENT
					&& abstractConvergence != IConvergenceElement.Convergence.CONVERGENT) {
				createProblemMarker(
						element, 
						EventBAttributes.CONVERGENCE_ATTRIBUTE, 
						GraphProblem.ConvergentEventNoVariantWarning);
				concreteConvergence = IConvergenceElement.Convergence.ORDINARY;
			}
	}
	
	@Override
	public void endModule(
			IRodinElement element, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		variantInfo = null;
		eventRefinesInfo = null;
		currentEvent = null;
		super.endModule(element, repository, monitor);
	}

}
