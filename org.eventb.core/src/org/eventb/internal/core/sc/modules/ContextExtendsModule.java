/*******************************************************************************
 * Copyright (c) 2006-2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextFile;
import org.eventb.core.IExtendsContext;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCExtendsContext;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IContextPointerArray;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.util.GraphProblem;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.ContextPointerArray;
import org.eventb.internal.core.sc.Messages;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextExtendsModule extends ContextPointerModule {

	public static final IModuleType<ContextExtendsModule> MODULE_TYPE = 
		SCCore.getModuleType(EventBPlugin.PLUGIN_ID + ".contextExtendsModule"); //$NON-NLS-1$

	private static final String EXTENDS_NAME_PREFIX = "EXTENDS";
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

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
		
		createExtendsClauses((ISCContextFile) target);
		
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

	private void createExtendsClauses(ISCContextFile scCtxFile)
			throws RodinDBException {

		int count = 0;
		final int size = contextPointerArray.size();
		for (int i = 0; i < size; ++i) {
			if (!contextPointerArray.hasError(i)) {
				final ISCExtendsContext scExtends = scCtxFile
						.getSCExtendsClause(EXTENDS_NAME_PREFIX + count++);
				scExtends.create(null, null);

				final ISCContextFile scSeenContext = contextPointerArray
						.getSCContextFile(i);
				scExtends.setAbstractSCContext(scSeenContext, null);

				final IInternalElement source = contextPointerArray
						.getContextPointer(i);
				scExtends.setSource(source, null);
			}
		}
	}

}
