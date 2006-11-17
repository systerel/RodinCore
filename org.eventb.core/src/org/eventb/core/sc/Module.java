/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.state.IStateSC;
import org.eventb.core.state.IStateRepository;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * 
 * Default implementation for modules.
 * 
 * @see org.eventb.core.sc.IModule
 * 
 * @author Stefan Hallerstede
 *
 */
public abstract class Module implements IModule, IMarkerDisplay {
	
	
	public void createProblemMarker(
			IRodinElement element, 
			IRodinProblem problem, 
			Object... args)
		throws RodinDBException {
		element.createProblemMarker(problem, args);
	}
	
	public void createProblemMarker(IInternalElement element,
			IAttributeType attributeType, IRodinProblem problem,
			Object... args) throws RodinDBException {

		element.createProblemMarker(attributeType, problem, args);
	}

	public void createProblemMarker(IInternalElement element,
			IAttributeType.String attributeType, int charStart, int charEnd,
			IRodinProblem problem, Object... args) throws RodinDBException {

		element.createProblemMarker(attributeType, charStart, charEnd+1, problem,
				args);
	}
	
	protected void initFilterModules(
			IFilterModule[] modules,
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IFilterModule module : modules) {
			module.initModule(repository, monitor);
		}
	}
	
	protected void initProcessorModules(
			IRodinElement element,
			IProcessorModule[] modules,
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IProcessorModule module : modules) {
			module.initModule(element, repository, monitor);
		}
	}
	
	protected boolean filterModules(
			IFilterModule[] modules, 
			IRodinElement element, 
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IFilterModule module : modules) {
			IFilterModule acceptorModule = module;
			if (acceptorModule.accept(element, repository, monitor))
				continue;
			return false;
		}
		return true;
	}
	
	protected void processModules(
			IProcessorModule[] modules, 
			IRodinElement element, 
			IInternalParent target,
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IProcessorModule module : modules) {
			module.process(element, target, repository, monitor);
		}
	}
	
	protected void endFilterModules(
			IFilterModule[] modules, 
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IFilterModule module : modules) {
			module.endModule(repository, monitor);
		}
	}

	protected void endProcessorModules(
			IRodinElement element,
			IProcessorModule[] modules, 
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IProcessorModule module : modules) {
			module.endModule(element, repository, monitor);
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
