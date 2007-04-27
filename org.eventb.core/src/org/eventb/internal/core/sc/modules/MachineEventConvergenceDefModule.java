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
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventConvergenceDefModule extends SCFilterModule {

	public static final IModuleType<MachineEventConvergenceDefModule> MODULE_TYPE = 
		SCCore.getModuleType(EventBPlugin.PLUGIN_ID + ".machineEventConvergenceDefModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ISCFilterModule#accept(org.rodinp.core.IRodinElement, org.eventb.core.sc.state.ISCStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean accept(
			IRodinElement element, 
			ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IEvent event = (IEvent) element;
		
		boolean ok = event.hasConvergence();
		
		if (!ok) {
			createProblemMarker(
					event, 
					EventBAttributes.CONVERGENCE_ATTRIBUTE, 
					GraphProblem.ConvergenceUndefError);
		}
		
		return ok;
	}

}
