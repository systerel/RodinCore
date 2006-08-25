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
import org.eventb.core.IContextFile;
import org.eventb.core.IExtendsContext;
import org.eventb.core.ISCContextFile;
import org.eventb.core.sc.IContextPointerArray;
import org.eventb.core.sc.IStateRepository;
import org.eventb.internal.core.sc.ContextPointerArray;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextExtendsModule extends ContextPointerModule {

	public void process(
			IRodinElement element, 
			IInternalParent target,
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		IContextFile contextFile = (IContextFile) element;
		
		IExtendsContext[] extendsContexts = contextFile.getExtendsClauses();
		
		ISCContextFile[] contextFiles = new ISCContextFile[extendsContexts.length];
		
		for(int i=0; i<extendsContexts.length; i++) {
			contextFiles[i] = extendsContexts[i].getAbstractSCContext();
		}
		
		IContextPointerArray contextPointerArray = 
			new ContextPointerArray(
					IContextPointerArray.EXTENDS_POINTER, 
					extendsContexts, 
					contextFiles);
		repository.setState(contextPointerArray);
		
		// we need to do everything up to this point
		// produce a define repository state
		
		if (extendsContexts.length == 0)
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
