/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core;

import org.eclipse.core.runtime.PlatformObject;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextFile;
import org.eventb.core.IEventBProject;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;
import org.eventb.core.IPRFile;
import org.eventb.core.IPSFile;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCMachineFile;
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

	public IRodinProject getRodinProject() {
		return rodinProject;
	}
	
	public IContextFile getContextFile(String bareName) {
		String name = EventBPlugin.getContextFileName(bareName);
		return (IContextFile) getRodinProject().getRodinFile(name);
	}

	public IMachineFile getMachineFile(String bareName) {
		String name = EventBPlugin.getMachineFileName(bareName);
		return (IMachineFile) getRodinProject().getRodinFile(name);
	}

	public IPRFile getPRFile(String bareName) {
		String name = EventBPlugin.getPRFileName(bareName);
		return (IPRFile) getRodinProject().getRodinFile(name);
	}

	public ISCContextFile getSCContextFile(String bareName) {
		String name = EventBPlugin.getSCContextFileName(bareName);
		return (ISCContextFile) getRodinProject().getRodinFile(name);
	}

	public ISCMachineFile getSCMachineFile(String bareName) {
		String name = EventBPlugin.getSCMachineFileName(bareName);
		return (ISCMachineFile) getRodinProject().getRodinFile(name);
	}

	public IPOFile getPOFile(String bareName) {
		String name = EventBPlugin.getPOFileName(bareName);
		return (IPOFile) getRodinProject().getRodinFile(name);
	}

	public IPSFile getPSFile(String bareName) {
		String name = EventBPlugin.getPSFileName(bareName);
		return (IPSFile) getRodinProject().getRodinFile(name);
	}

}
