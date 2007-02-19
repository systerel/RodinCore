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
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class BaseModule extends SCProcessorModule {

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor) throws CoreException {
		endProcessorModules(element, repository, monitor);
	}

	@Override
	public void initModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor) throws CoreException {
		initProcessorModules(element, repository, monitor);
	}

	public void process(IRodinElement element, IInternalParent target, ISCStateRepository repository, IProgressMonitor monitor) throws CoreException {
		processModules(element, target, repository, monitor);
	}

}
