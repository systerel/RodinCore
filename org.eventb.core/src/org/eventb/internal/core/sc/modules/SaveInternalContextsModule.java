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
import org.eventb.core.ISCContext;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.sc.IContextPointerArray;
import org.eventb.core.sc.IStateRepository;
import org.eventb.core.sc.ProcessorModule;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
@Deprecated
public class SaveInternalContextsModule extends ProcessorModule {

	public void process(
			IRodinElement element, 
			IInternalParent target,
			IStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {
		
		IContextPointerArray contextPointerArray =
			(IContextPointerArray) repository.getState(IContextPointerArray.STATE_TYPE);
		
		List<ISCContext> validContexts = contextPointerArray.getValidContexts();
				
		createInternalContexts(target, validContexts, repository, monitor);
	}
	
	protected void createInternalContexts(
			IInternalParent target, 
			List<ISCContext> scContexts,
			IStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		
		for (ISCContext context : scContexts) {
			
			if (context instanceof ISCInternalContext) {
				
				ISCInternalContext internalContext = (ISCInternalContext) context;
				
				internalContext.copy(target, null, null, false, monitor);
			} else {
				
				ISCContextFile contextFile = (ISCContextFile) context;
			
				ISCInternalContext internalContext = 
					(ISCInternalContext) target.createInternalElement(
							ISCInternalContext.ELEMENT_TYPE, 
							context.getElementName(), null, monitor);
				
				copyElements(contextFile.getChildren(), internalContext, monitor);
				
			}
			
		}
		
		repository.setChanged();
		
	}
	
	private boolean copyElements(
			IRodinElement[] elements, 
			IInternalElement target, 
			IProgressMonitor monitor) throws RodinDBException {
		
		for (IRodinElement element : elements) {
			IInternalElement internalElement = (IInternalElement) element;
			internalElement.copy(target, null, null, false, monitor);
		}
		
		return true;
	}

}
