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
import org.eventb.core.IEvent;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.ISCEvent;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.ProcessorModule;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IEventRefinesInfo;
import org.eventb.core.sc.state.IStateSC;
import org.eventb.core.sc.state.IVariantInfo;
import org.eventb.core.state.IStateRepository;
import org.rodinp.core.IInternalElement;
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
	public void initModule(
			IRodinElement element, 
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		variantInfo = (IVariantInfo) repository.getState(IVariantInfo.STATE_TYPE);
		eventRefinesInfo = (IEventRefinesInfo) repository.getState(IEventRefinesInfo.STATE_TYPE);
	}

	public void process(
			IRodinElement element, 
			IInternalParent target, 
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		
		IEvent event = (IEvent) element;
		
		int convergence = event.getConvergence(null);
		
		List<IAbstractEventInfo> abstractEventInfos = eventRefinesInfo.getAbstractEventInfos();
		if (abstractEventInfos.size() != 0) { // not a new event
			convergence = 
				checkAbstractConvergence(event, convergence, abstractEventInfos, null);
		}
		convergence = checkVariantConvergence(event, convergence);
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
			IInternalElement element, 
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
			createProblemMarker(
					element, 
					EventBAttributes.CONVERGENCE_ATTRIBUTE, 
					GraphProblem.InconsistentAbstractConvergenceWarning);
			return IConvergenceElement.ORDINARY;
		} else {
			ok = false;
			ok |= abstractConvergence == IConvergenceElement.ORDINARY && convergence == IConvergenceElement.ORDINARY;
			ok |= abstractConvergence == IConvergenceElement.ANTICIPATED && convergence == IConvergenceElement.ANTICIPATED;
			ok |= abstractConvergence == IConvergenceElement.ANTICIPATED && convergence == IConvergenceElement.CONVERGENT;
			ok |= abstractConvergence == IConvergenceElement.CONVERGENT && convergence == IConvergenceElement.ORDINARY;
			if (!ok) {
				switch (abstractConvergence) {
				case IConvergenceElement.ORDINARY:
					createProblemMarker(
							element, 
							EventBAttributes.CONVERGENCE_ATTRIBUTE, 
							GraphProblem.OrdinaryFaultyConvergenceWarning);
					break;
				case IConvergenceElement.CONVERGENT:
					createProblemMarker(
							element, 
							EventBAttributes.CONVERGENCE_ATTRIBUTE, 
							GraphProblem.ConvergentFaultyConvergenceWarning);
					break;
				default:
					createProblemMarker(
							element, 
							EventBAttributes.CONVERGENCE_ATTRIBUTE, 
							GraphProblem.AnticipatedFaultyConvergence);
				}
				return IConvergenceElement.ORDINARY;
			}
		}
		return convergence;
	}

	int checkVariantConvergence(
			IInternalElement element, 
			int convergence) throws CoreException {
		
		if (variantInfo.getExpression() == null)
			if (convergence == IConvergenceElement.ANTICIPATED) {
				createProblemMarker(
						element, 
						EventBAttributes.CONVERGENCE_ATTRIBUTE, 
						GraphProblem.AnticipatedEventNoVariant);
				return IConvergenceElement.ORDINARY;
			} else if (convergence == IConvergenceElement.CONVERGENT) {
				createProblemMarker(
						element, 
						EventBAttributes.CONVERGENCE_ATTRIBUTE, 
						GraphProblem.ConvergentEventNoVariant);
				return IConvergenceElement.ORDINARY;
			}
		return convergence;
	}
	
	@Override
	public void endModule(
			IRodinElement element, 
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		variantInfo = null;
		eventRefinesInfo = null;
		super.endModule(element, repository, monitor);
	}

}
