/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.extension;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEventBProject;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

/**
 * Unit tests for the formula extension provider registry.
 */
public class FormulaExtensionProviderRegistryTest {

	protected IWorkspace workspace = ResourcesPlugin.getWorkspace();

	private IRodinProject rodinProject;
	private IEventBProject eventBProject;

	@Before
	public void setUp() throws CoreException {
		rodinProject = createRodinProject("P");
		eventBProject = (IEventBProject) rodinProject
				.getAdapter(IEventBProject.class);
	}

	@After
	public void cleanUp() throws CoreException {
		eventBProject = null;
		rodinProject.getResource().delete(true, null);
	}

	protected IRodinProject createRodinProject(String projectName)
			throws CoreException {
		final IProject project = workspace.getRoot().getProject(projectName);
		project.create(null);
		project.open(null);
		final IProjectDescription pDescription = project.getDescription();
		pDescription.setNatureIds(new String[] { RodinCore.NATURE_ID });
		project.setDescription(pDescription, null);
		final IRodinProject result = RodinCore.valueOf(project);
		return result;
	}

	/**
	 * Tests that a 'Prime' extension is added to the existing default
	 * extensions of the factory through the formula extension provider
	 * mechanism. This test aims to show that the mechanism of extension
	 * providers works. The extension 'Prime' is static so it is possible to
	 * compare instances.
	 */
	@Test
	public void testFormulaFactoriesEquals() {
		final FormulaFactory factory1 = EventBPlugin
				.getFormulaExtensionProviderRegistry().getFormulaFactory(
						eventBProject);
		final Set<IFormulaExtension> extensions = factory1.getExtensions();
		final HashSet<IFormulaExtension> extensions2 = new HashSet<IFormulaExtension>();
		extensions2.add(Prime.getPrime());
		extensions2.addAll(FormulaFactory.getDefault().getDefaultExtensions());
		assertSameExtensions(extensions, extensions2);
	}

	private void assertSameExtensions(Set<IFormulaExtension> s1,
			Set<IFormulaExtension> s2) {
		assertTrue("Wrong amount of extensions", s1.size() == s2.size());
		assertTrue("Different sets of extensions", s1.containsAll(s2));
	}

}
