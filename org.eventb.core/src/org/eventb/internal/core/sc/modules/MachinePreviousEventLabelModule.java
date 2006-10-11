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
import org.eventb.core.IEvent;
import org.eventb.core.ILabeledElement;
import org.eventb.core.sc.AcceptorModule;
import org.eventb.core.sc.IAbstractEventInfo;
import org.eventb.core.sc.IAbstractEventTable;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.IStateRepository;
import org.eventb.internal.core.sc.Messages;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachinePreviousEventLabelModule extends AcceptorModule {
	
	private IAbstractEventTable abstractEventTable;

	@Override
	public void initModule(IStateRepository repository, IProgressMonitor monitor) throws CoreException {
		abstractEventTable = (IAbstractEventTable)
			repository.getState(IAbstractEventTable.STATE_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IAcceptorModule#accept(org.rodinp.core.IRodinElement, org.rodinp.core.IInternalParent, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean accept(
			IRodinElement element,
			IStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		String label = ((ILabeledElement) element).getLabel(monitor);
		IAbstractEventInfo abstractEventInfo = 
			abstractEventTable.getAbstractEventInfo(label);
		if (abstractEventInfo != null) {
			if (element instanceof IEvent) {
				if (abstractEventInfo.isForbidden()) {
					issueMarker(
							IMarkerDisplay.SEVERITY_WARNING, 
							element, 
							Messages.scuser_ObsoleteEventLabelProblem, 
							label);
				}
			} else {
				issueMarker(
						IMarkerDisplay.SEVERITY_WARNING, 
						element, 
						Messages.scuser_WasAbstractEventLabelProblem, 
						label);
			}
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.Module#endModule(org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(IStateRepository repository, IProgressMonitor monitor) throws CoreException {
		abstractEventTable = null;
	}

}
