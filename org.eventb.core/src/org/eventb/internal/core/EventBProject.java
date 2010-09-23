/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - now gets the formula factory from extension provider registry
 *******************************************************************************/
package org.eventb.internal.core;

import org.eclipse.core.runtime.PlatformObject;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBProject;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ast.FormulaFactory;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * Implementation of event-B projects.
 * 
 * @author Laurent Voisin
 */
public class EventBProject extends PlatformObject implements IEventBProject {
	
	private final IRodinProject rodinProject;
	
	public EventBProject(IRodinProject rodinProject) {
		if (rodinProject == null) {
			throw new NullPointerException();
		}
		this.rodinProject = rodinProject;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof EventBProject) {
			return rodinProject.equals(((EventBProject) obj).rodinProject);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return rodinProject.hashCode();
	}

	@Override
	public String toString() {
		return rodinProject.toString();
	}

	@Override
	public IRodinProject getRodinProject() {
		return rodinProject;
	}
	
	@Override
	public IRodinFile getContextFile(String bareName) {
		String name = EventBPlugin.getContextFileName(bareName);
		return getRodinProject().getRodinFile(name);
	}

	@Override
	public IContextRoot getContextRoot(String componentName) {
		return (IContextRoot) getContextFile(componentName).getRoot();
	}

	@Override
	public IRodinFile getMachineFile(String bareName) {
		String name = EventBPlugin.getMachineFileName(bareName);
		return getRodinProject().getRodinFile(name);
	}

	@Override
	public IMachineRoot getMachineRoot(String componentName) {
		return (IMachineRoot) getMachineFile(componentName).getRoot();
	}

	@Override
	public IRodinFile getPRFile(String bareName) {
		String name = EventBPlugin.getPRFileName(bareName);
		return getRodinProject().getRodinFile(name);
	}

	@Override
	public IPRRoot getPRRoot(String componentName) {
		return (IPRRoot) getPRFile(componentName).getRoot();
	}

	@Override
	public IRodinFile getSCContextFile(String bareName) {
		String name = EventBPlugin.getSCContextFileName(bareName);
		return getRodinProject().getRodinFile(name);
	}

	@Override
	public ISCContextRoot getSCContextRoot(String componentName) {
		return (ISCContextRoot) getSCContextFile(componentName).getRoot();
	}

	@Override
	public IRodinFile getSCMachineFile(String bareName) {
		String name = EventBPlugin.getSCMachineFileName(bareName);
		return getRodinProject().getRodinFile(name);
	}

	@Override
	public ISCMachineRoot getSCMachineRoot(String componentName) {
		return (ISCMachineRoot) getSCMachineFile(componentName).getRoot();
	}

	@Override
	public IRodinFile getPOFile(String bareName) {
		String name = EventBPlugin.getPOFileName(bareName);
		return getRodinProject().getRodinFile(name);
	}

	@Override
	public IPORoot getPORoot(String componentName) {
		return (IPORoot) getPOFile(componentName).getRoot();
	}

	@Override
	public IRodinFile getPSFile(String bareName) {
		String name = EventBPlugin.getPSFileName(bareName);
		return getRodinProject().getRodinFile(name);
	}

	@Override
	public IPSRoot getPSRoot(String componentName) {
		return (IPSRoot) getPSFile(componentName).getRoot();
	}

}
