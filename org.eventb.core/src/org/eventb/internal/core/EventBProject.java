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
	
	private final FormulaFactory formulaFactory;
	
	public EventBProject(IRodinProject rodinProject) {
		if (rodinProject == null) {
			throw new NullPointerException();
		}
		this.rodinProject = rodinProject;
		this.formulaFactory = FormulaFactory.getDefault();
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

	public IContextRoot getContextRoot(String componentName) {
		return (IContextRoot) getContextFile(componentName).getRoot();
	}

	public IRodinFile getMachineFile(String bareName) {
		String name = EventBPlugin.getMachineFileName(bareName);
		return getRodinProject().getRodinFile(name);
	}

	public IMachineRoot getMachineRoot(String componentName) {
		return (IMachineRoot) getMachineFile(componentName).getRoot();
	}

	public IRodinFile getPRFile(String bareName) {
		String name = EventBPlugin.getPRFileName(bareName);
		return getRodinProject().getRodinFile(name);
	}

	public IPRRoot getPRRoot(String componentName) {
		return (IPRRoot) getPRFile(componentName).getRoot();
	}

	public IRodinFile getSCContextFile(String bareName) {
		String name = EventBPlugin.getSCContextFileName(bareName);
		return getRodinProject().getRodinFile(name);
	}

	public ISCContextRoot getSCContextRoot(String componentName) {
		return (ISCContextRoot) getSCContextFile(componentName).getRoot();
	}

	public IRodinFile getSCMachineFile(String bareName) {
		String name = EventBPlugin.getSCMachineFileName(bareName);
		return getRodinProject().getRodinFile(name);
	}

	public ISCMachineRoot getSCMachineRoot(String componentName) {
		return (ISCMachineRoot) getSCMachineFile(componentName).getRoot();
	}

	public IRodinFile getPOFile(String bareName) {
		String name = EventBPlugin.getPOFileName(bareName);
		return getRodinProject().getRodinFile(name);
	}

	public IPORoot getPORoot(String componentName) {
		return (IPORoot) getPOFile(componentName).getRoot();
	}

	public IRodinFile getPSFile(String bareName) {
		String name = EventBPlugin.getPSFileName(bareName);
		return getRodinProject().getRodinFile(name);
	}

	public IPSRoot getPSRoot(String componentName) {
		return (IPSRoot) getPSFile(componentName).getRoot();
	}

	public FormulaFactory getFormulaFactory() {
		return formulaFactory;
	}

}
