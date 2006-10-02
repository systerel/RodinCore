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
import org.eventb.core.IEvent;
import org.eventb.core.IEventConvergence;
import org.eventb.core.ISCEvent;
import org.eventb.core.sc.IAbstractEventInfo;
import org.eventb.core.sc.IEventRefinesInfo;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.IStateRepository;
import org.eventb.core.sc.IVariantInfo;
import org.eventb.core.sc.ProcessorModule;
import org.eventb.internal.core.sc.Messages;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventConvergenceModule extends ProcessorModule {
	
	IVariantInfo variantInfo;
	IEventRefinesInfo eventRefinesInfo;

	@Override
	public void initModule(IRodinElement element, IStateRepository repository, IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		variantInfo = (IVariantInfo) repository.getState(IVariantInfo.STATE_TYPE);
		eventRefinesInfo = (IEventRefinesInfo) repository.getState(IEventRefinesInfo.STATE_TYPE);
	}

	public void process(
			IRodinElement element, 
			IInternalParent target, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		IEvent event = (IEvent) element;
		
		int convergence = event.getConvergence(null);
		
		List<IAbstractEventInfo> abstractEventInfos = eventRefinesInfo.getAbstractEventInfos();
		if (abstractEventInfos.size() != 0) { // not a new event
			convergence = 
				checkAbstractConvergence(element, convergence, abstractEventInfos, null);
		}
		convergence = checkVariantConvergence(element, convergence);
		saveConvergence((ISCEvent) target, convergence, null);
	}
	
	void saveConvergence(
			ISCEvent target, 
			int convergence, 
			IProgressMonitor monitor) throws RodinDBException {
		if (target == null)
			return;
		target.setConvergence(convergence, monitor);
	}
	
	int checkAbstractConvergence(
			IRodinElement element, 
			int convergence,
			List<IAbstractEventInfo> abstractEventInfos, 
			IProgressMonitor monitor) throws CoreException {
		int abstractConvergence = -1;
		int i = 0;
		boolean ok = true;
		for (IAbstractEventInfo abstractEventInfo : abstractEventInfos) {
			if (i++ == 0)
				abstractConvergence = abstractEventInfo.getEvent().getConvergence(monitor);
			else if (abstractEventInfo.getEvent().getConvergence(monitor) != abstractConvergence)
				ok = false;
		}
		if (!ok) {
			issueMarker(
					IMarkerDisplay.SEVERITY_WARNING, 
					element, 
					Messages.scuser_InconsistentAbstractConvergence);
			return IEventConvergence.ORDINARY;
		} else {
			ok = false;
			ok |= abstractConvergence == IEventConvergence.ORDINARY && convergence == IEventConvergence.ORDINARY;
			ok |= abstractConvergence == IEventConvergence.ANTICIPATED && convergence == IEventConvergence.ANTICIPATED;
			ok |= abstractConvergence == IEventConvergence.ANTICIPATED && convergence == IEventConvergence.CONVERGENT;
			ok |= abstractConvergence == IEventConvergence.CONVERGENT && convergence == IEventConvergence.ORDINARY;
			if (!ok) {
				switch (abstractConvergence) {
				case IEventConvergence.ORDINARY:
				case IEventConvergence.CONVERGENT:
					issueMarker(
							IMarkerDisplay.SEVERITY_WARNING, 
							element, 
							Messages.scuser_OrdinaryConvergentFaultyConvergence);
					break;
				default:
					issueMarker(
							IMarkerDisplay.SEVERITY_WARNING, 
							element, 
							Messages.scuser_AnticipatedFaultyConvergence);
				}
				return IEventConvergence.ORDINARY;
			}
		}
		return convergence;
	}

	int checkVariantConvergence(
			IRodinElement element, 
			int convergence) throws CoreException {
		
		if (variantInfo.getExpression() == null)
			if (convergence == IEventConvergence.ANTICIPATED) {
				issueMarker(IMarkerDisplay.SEVERITY_WARNING, element, Messages.scuser_AnticipatedEventNoVariant);
				return IEventConvergence.ORDINARY;
			} else if (convergence == IEventConvergence.CONVERGENT) {
				issueMarker(IMarkerDisplay.SEVERITY_WARNING, element, Messages.scuser_ConvergentEventNoVariant);
				return IEventConvergence.ORDINARY;
			}
		return convergence;
	}
	
	@Override
	public void endModule(IRodinElement element, IStateRepository repository, IProgressMonitor monitor) throws CoreException {
		variantInfo = null;
		eventRefinesInfo = null;
		super.endModule(element, repository, monitor);
	}

}
