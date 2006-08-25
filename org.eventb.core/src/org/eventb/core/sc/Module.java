/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinDBMarker;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

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
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IMarkerDisplay#issueMarkerWithInterval(int, org.rodinp.core.IRodinElement, java.lang.String, int, int, java.lang.Object...)
	 */
	public void issueMarkerWithLocation(int severity, IRodinElement element, String message, int startLocation, int endLocation, Object... objects) {
		// TODO complete when markers are available
		
		issueMarker(severity, element, message, objects);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IMarkerDisplay#issueMarker(int, org.rodinp.core.IRodinElement, java.lang.String, java.lang.Object[])
	 */
	public void issueMarker(int severity, IRodinElement element, String message, Object... objects) {
		addMarker((IRodinFile) element.getOpenable(), element, message, severity);
	}
		
	private String printElement(IRodinElement element) {
		String elementType = element.getElementType();
		String result = elementType.substring(elementType.lastIndexOf('.')+1);
		IRodinElement parent = element.getParent();
		if(parent instanceof IInternalElement)
			result = result + " " + ((IInternalElement) element).getElementName() + " in " + printElement(parent);
		else
			result = result + " " + element.getElementName(); 
		return result;
	}
	
	private void addMarker(IRodinFile rodinFile, IRodinElement element, String message, int severity) {
		try {
			IMarker marker = rodinFile.getResource().createMarker(IRodinDBMarker.RODIN_PROBLEM_MARKER);
			
			// TODO: correctly implement marker location
			marker.setAttribute(IMarker.LOCATION, element.getPath().toString());
			marker.setAttribute(IMarker.MESSAGE, "(" + printElement(element) + ") " + message);
			marker.setAttribute(IMarker.SEVERITY, severity);
		} catch(CoreException e) {
			// can safely ignore
		}
	}
	
	protected void initAcceptorModules(
			IAcceptorModule[] modules,
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IAcceptorModule module : modules) {
			module.initModule(repository, monitor);
		}
	}
	
	protected void initProcessorModules(
			IRodinElement element,
			IProcessorModule[] modules,
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IProcessorModule module : modules) {
			module.initModule(element, repository, monitor);
		}
	}
	
	protected boolean acceptModules(
			IAcceptorModule[] modules, 
			IRodinElement element, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IAcceptorModule module : modules) {
			IAcceptorModule acceptorModule = module;
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
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IProcessorModule module : modules) {
			module.process(element, target, repository, monitor);
		}
	}
	
	protected void endAcceptorModules(
			IAcceptorModule[] modules, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IAcceptorModule module : modules) {
			module.endModule(repository, monitor);
		}
	}

		protected void endProcessorModules(
			IRodinElement element,
			IProcessorModule[] modules, 
			IStateRepository repository, 
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
