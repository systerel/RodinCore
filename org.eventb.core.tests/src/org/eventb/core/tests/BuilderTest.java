/*******************************************************************************
 * Copyright (c) 2006-2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.tests;

import junit.framework.TestCase;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.internal.core.pom.AutoProver;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;

/**
 * Abstract class for builder tests.
 * 
 * @author Laurent Voisin
 */
public abstract class BuilderTest extends TestCase {
	
	protected static FormulaFactory factory = FormulaFactory.getDefault();

	protected IRodinProject rodinProject;

	protected IWorkspace workspace = ResourcesPlugin.getWorkspace();

	public BuilderTest() {
		super();
	}

	public BuilderTest(String name) {
		super(name);
	}
	
	protected IContextFile createContext(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getContextFileName(bareName);
		IContextFile result = (IContextFile) rodinProject.getRodinFile(fileName);
		result.create(true, null);
		return result;
	}

	protected IMachineFile createMachine(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getMachineFileName(bareName);
		IMachineFile result = (IMachineFile) rodinProject.getRodinFile(fileName);
		result.create(true, null);
		return result;
	}

	protected IPOFile createPOFile(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getPOFileName(bareName);
		IPOFile result = (IPOFile) rodinProject.getRodinFile(fileName);
		result.create(true, null);
		return result;
	}

	protected ISCContextFile createSCContext(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getSCContextFileName(bareName);
		ISCContextFile result = (ISCContextFile) rodinProject.getRodinFile(fileName);
		result.create(true, null);
		return result;
	}

	protected ISCMachineFile createSCMachine(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getSCMachineFileName(bareName);
		ISCMachineFile result = (ISCMachineFile) rodinProject.getRodinFile(fileName);
		result.create(true, null);
		return result;
	}
	
	protected void runBuilder() throws CoreException {
		final IProject project = rodinProject.getProject();
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		IMarker[] buildPbs= project.findMarkers(
				RodinMarkerUtil.BUILDPATH_PROBLEM_MARKER,
				true,
				IResource.DEPTH_INFINITE
		);
		if (buildPbs.length != 0) {
			for (IMarker marker: buildPbs) {
				System.out.println("Build problem for " + marker.getResource());
				System.out.println("  " + marker.getAttribute(IMarker.MESSAGE));
			}
			fail("Build produced build problems, see console");
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		// ensure autobuilding is turned off
		IWorkspaceDescription wsDescription = workspace.getDescription();
		if (wsDescription.isAutoBuilding()) {
			wsDescription.setAutoBuilding(false);
			workspace.setDescription(wsDescription);
		}
		
		// Create a new project
		IProject project = workspace.getRoot().getProject("P");
		project.create(null);
		project.open(null);
		IProjectDescription pDescription = project.getDescription();
		pDescription.setNatureIds(new String[] {RodinCore.NATURE_ID});
		project.setDescription(pDescription, null);
		rodinProject = RodinCore.valueOf(project);
		
		// Turn off automatic provers
		AutoProver.disable();
	}
	
	protected void tearDown() throws Exception {
		rodinProject.getProject().delete(true, true, null);
		super.tearDown();
	}

}
