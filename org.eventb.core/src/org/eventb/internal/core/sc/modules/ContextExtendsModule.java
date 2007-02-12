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
import org.eventb.core.ISCInternalContext;
import org.eventb.core.sc.state.IContextPointerArray;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.util.GraphProblem;
import org.eventb.internal.core.sc.ContextPointerArray;
import org.eventb.internal.core.sc.Messages;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextExtendsModule extends ContextPointerModule {

	protected ContextPointerArray contextPointerArray;
	
	@Override
	public void initModule(
			IRodinElement element, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		
		IContextFile contextFile = (IContextFile) element;
		
		IExtendsContext[] extendsContexts = contextFile.getExtendsClauses();
		
		ISCContextFile[] contextFiles = new ISCContextFile[extendsContexts.length];
		
		for(int i=0; i<extendsContexts.length; i++) {
			contextFiles[i] = extendsContexts[i].getAbstractSCContext();
		}
		
		contextPointerArray = 
			new ContextPointerArray(
					IContextPointerArray.PointerType.EXTENDS_POINTER, 
					extendsContexts, 
					contextFiles);
		repository.setState(contextPointerArray);
	}

	@Override
	public void endModule(
			IRodinElement element, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.endModule(element, repository, monitor);
		contextPointerArray = null;
	}
	
	public void process(
			IRodinElement element, 
			IInternalParent target,
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		// we need to do everything up to this point
		// produce a define repository state
		
		if (contextPointerArray.size() == 0)
			return; // nothing to do
		
		monitor.subTask(Messages.bind(Messages.progress_ContextExtends));
		
		fetchSCContexts(
				contextPointerArray,
				monitor);
		
		createInternalContexts(
				target, 
				contextPointerArray.getValidContexts(), 
				repository, 
				null);
		
	}

	@Override
	protected IRodinProblem getTargetContextNotFoundProblem() {
		return GraphProblem.AbstractContextNotFoundError;
	}

	@Override
	protected ISCInternalContext getSCInternalContext(IInternalParent target, String elementName) {
		return ((ISCContextFile) target).getSCInternalContext(elementName);
	}

}
