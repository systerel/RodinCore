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
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IEvent;
import org.eventb.core.ISCEvent;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.ICurrentEvent;
import org.eventb.core.sc.state.IEventRefinesInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.state.IVariantInfo;
import org.eventb.core.sc.util.GraphProblem;
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

	IVariantInfo variantInfo;
	IEventRefinesInfo eventRefinesInfo;
	ICurrentEvent currentEvent;

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

	public void process(
			IRodinElement element, 
			IInternalParent target, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		IEvent event = (IEvent) element;
		
		IConvergenceElement.Convergence convergence = event.getConvergence();
		
		if (currentEvent.isInitialisation()) {
			if (convergence != IConvergenceElement.Convergence.ORDINARY) {
				convergence = IConvergenceElement.Convergence.ORDINARY;
				createProblemMarker(
						event, 
						EventBAttributes.CONVERGENCE_ATTRIBUTE, 
						GraphProblem.InconsistentAbstractConvergenceWarning);
			}
		} else {
		
			List<IAbstractEventInfo> abstractEventInfos = eventRefinesInfo.getAbstractEventInfos();
			if (abstractEventInfos.size() != 0) { // not a new event
				convergence = 
					checkAbstractConvergence(event, convergence, abstractEventInfos, null);
			}
			convergence = checkVariantConvergence(event, convergence);
		}
		saveConvergence((ISCEvent) target, convergence, null);
	}
	
	void saveConvergence(
			ISCEvent target, 
			IConvergenceElement.Convergence convergence, 
			IProgressMonitor monitor) throws RodinDBException {
		if (target == null)
			return;
		target.setConvergence(convergence, monitor);
	}
	
	IConvergenceElement.Convergence checkAbstractConvergence(
			IInternalElement element, 
			IConvergenceElement.Convergence convergence,
			List<IAbstractEventInfo> abstractEventInfos, 
			IProgressMonitor monitor) throws CoreException {
		IConvergenceElement.Convergence abstractConvergence = 
			IConvergenceElement.Convergence.ORDINARY;
		int i = 0;
		boolean ok = true;
		for (IAbstractEventInfo abstractEventInfo : abstractEventInfos) {
			if (i++ == 0)
				abstractConvergence = abstractEventInfo.getEvent().getConvergence();
			else if (abstractEventInfo.getEvent().getConvergence() != abstractConvergence)
				ok = false;
		}
		if (!ok) {
			createProblemMarker(
					element, 
					EventBAttributes.CONVERGENCE_ATTRIBUTE, 
					GraphProblem.InconsistentAbstractConvergenceWarning);
			return IConvergenceElement.Convergence.ORDINARY;
		} else {
			ok = false;
			ok |= abstractConvergence == IConvergenceElement.Convergence.ORDINARY 
			&& convergence == IConvergenceElement.Convergence.ORDINARY;
			ok |= abstractConvergence == IConvergenceElement.Convergence.ANTICIPATED 
			&& convergence == IConvergenceElement.Convergence.ANTICIPATED;
			ok |= abstractConvergence == IConvergenceElement.Convergence.ANTICIPATED 
			&& convergence == IConvergenceElement.Convergence.CONVERGENT;
			ok |= abstractConvergence == IConvergenceElement.Convergence.CONVERGENT 
			&& convergence == IConvergenceElement.Convergence.CONVERGENT;
			if (!ok) {
				if (abstractConvergence == IConvergenceElement.Convergence.ORDINARY) {
					createProblemMarker(
							element, 
							EventBAttributes.CONVERGENCE_ATTRIBUTE, 
							GraphProblem.OrdinaryFaultyConvergenceWarning);
					return IConvergenceElement.Convergence.ORDINARY;
				} else if (abstractConvergence == IConvergenceElement.Convergence.CONVERGENT) {
					createProblemMarker(
							element, 
							EventBAttributes.CONVERGENCE_ATTRIBUTE, 
							GraphProblem.ConvergentFaultyConvergenceWarning);
					return IConvergenceElement.Convergence.CONVERGENT;
				} else {
					createProblemMarker(
							element, 
							EventBAttributes.CONVERGENCE_ATTRIBUTE, 
							GraphProblem.AnticipatedFaultyConvergenceWarning);
				return IConvergenceElement.Convergence.ANTICIPATED;
				}
			}
		}
		return convergence;
	}

	IConvergenceElement.Convergence checkVariantConvergence(
			IInternalElement element, 
			IConvergenceElement.Convergence convergence) throws CoreException {
		
		if (variantInfo.getExpression() == null)
			if (convergence == IConvergenceElement.Convergence.ANTICIPATED) {
				createProblemMarker(
						element, 
						EventBAttributes.CONVERGENCE_ATTRIBUTE, 
						GraphProblem.AnticipatedEventNoVariantWarning);
				return IConvergenceElement.Convergence.ORDINARY;
			} else if (convergence == IConvergenceElement.Convergence.CONVERGENT) {
				createProblemMarker(
						element, 
						EventBAttributes.CONVERGENCE_ATTRIBUTE, 
						GraphProblem.ConvergentEventNoVariantWarning);
				return IConvergenceElement.Convergence.ORDINARY;
			}
		return convergence;
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
