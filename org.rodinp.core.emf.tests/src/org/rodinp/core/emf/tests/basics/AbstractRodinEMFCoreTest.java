/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.emf.tests.basics;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.After;
import org.junit.Before;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.api.itf.ILFile;
import org.rodinp.core.emf.lightcore.RodinResource;
import org.rodinp.core.tests.basis.NamedElement;

/**
 * Abstract class for Rodin EMF tests
 */
public abstract class AbstractRodinEMFCoreTest {

	protected static final String pNAME = "RodinEMFProject";
	protected static final String fNAME = "myRes.ert";
	protected static final IWorkspace workspace = ResourcesPlugin
			.getWorkspace();

	protected IRodinProject rodinProject;
	protected IRodinFile rodinFile;

	@Before
	public void setUp() throws CoreException {
		rodinProject = createRodinProject(pNAME);
		rodinProject.save(null, true);
		rodinFile = createAndSaveRodinFile(rodinProject, fNAME);
	}

	@After
	public void tearDown() throws Exception {
		rodinProject.getProject().delete(true, true, null);
		rodinProject.getRodinDB().close();
	}

	protected ILFile getRodinResource() {
		return getRodinResource(rodinProject, fNAME);
	}
	
	protected static ILFile getRodinResource(IRodinProject project, String filename) {
		final ResourceSet resourceSet = new ResourceSetImpl();
		final String projectName = project.getElementName();
		final URI uri = URI.createPlatformResourceURI(
				projectName + "/" + filename, true);
		return (RodinResource) resourceSet.getResource(uri, true);
	}

	protected static NamedElement getNamedElement(IInternalElement parent,
			String name) throws RodinDBException {
		final NamedElement ne = (NamedElement) parent.getInternalElement(
				NamedElement.ELEMENT_TYPE, name);
		ne.create(null, null);
		return ne;
	}

	protected static IRodinProject createRodinProject(String projectName)
			throws CoreException {
		final IProject project = workspace.getRoot().getProject(projectName);
		if (project.exists()) {
			return RodinCore.valueOf(project);
		}
		project.create(null);
		project.open(null);
		final IProjectDescription pDescription = project.getDescription();
		pDescription.setNatureIds(new String[] { RodinCore.NATURE_ID });
		project.setDescription(pDescription, null);
		final IRodinProject result = RodinCore.valueOf(project);
		return result;
	}
	
	
	protected static IRodinFile createAndSaveRodinFile(IRodinProject project,
			String fileName) throws RodinDBException{
		final IRodinFile file = project.getRodinFile(fileName);
		file.create(true, null);
		file.save(null, true);
		return file;
	}

}
