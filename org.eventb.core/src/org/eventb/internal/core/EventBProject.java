/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.core;

import org.eclipse.core.runtime.PlatformObject;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEventBProject;
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

	public IRodinProject getRodinProject() {
		return rodinProject;
	}
	
	public IRodinFile getContextFile(String bareName) {
		String name = EventBPlugin.getContextFileName(bareName);
		return getRodinProject().getRodinFile(name);
	}

	public IRodinFile getMachineFile(String bareName) {
		String name = EventBPlugin.getMachineFileName(bareName);
		return getRodinProject().getRodinFile(name);
	}

	public IRodinFile getPRFile(String bareName) {
		String name = EventBPlugin.getPRFileName(bareName);
		return getRodinProject().getRodinFile(name);
	}

	public IRodinFile getSCContextFile(String bareName) {
		String name = EventBPlugin.getSCContextFileName(bareName);
		return getRodinProject().getRodinFile(name);
	}

	public IRodinFile getSCMachineFile(String bareName) {
		String name = EventBPlugin.getSCMachineFileName(bareName);
		return getRodinProject().getRodinFile(name);
	}

	public IRodinFile getPOFile(String bareName) {
		String name = EventBPlugin.getPOFileName(bareName);
		return getRodinProject().getRodinFile(name);
	}

	public IRodinFile getPSFile(String bareName) {
		String name = EventBPlugin.getPSFileName(bareName);
		return getRodinProject().getRodinFile(name);
	}

}
