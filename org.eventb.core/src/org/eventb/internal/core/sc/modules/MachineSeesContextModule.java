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
import org.eventb.core.IMachineFile;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISeesContext;
import org.eventb.core.sc.IContextPointerArray;
import org.eventb.core.sc.IStateRepository;
import org.eventb.internal.core.sc.ContextPointerArray;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineSeesContextModule extends ContextPointerModule {

	public void process(
			IRodinElement element, 
			IInternalParent target,
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {

		IMachineFile machineFile = (IMachineFile) element;
		
		ISeesContext[] seesContexts = machineFile.getSeesClauses();
		
		ISCContextFile[] contextFiles = new ISCContextFile[seesContexts.length];
		
		for(int i=0; i<seesContexts.length; i++) {
			contextFiles[i] = seesContexts[i].getSeenSCContext();
		}
		
		IContextPointerArray contextPointerArray = 
			new ContextPointerArray(
					IContextPointerArray.SEES_POINTER, 
					seesContexts, 
					contextFiles);
		repository.setState(contextPointerArray);
		
		// we need to do everything up to this point
		// produce a define repository state
		
		if (seesContexts.length == 0)
			return; // nothing to do
		
		fetchSCContexts(
				contextPointerArray,
				monitor);
		
		createInternalContexts(
				target, 
				contextPointerArray.getValidContexts(), 
				repository, 
				monitor);
		
	}
		
}
