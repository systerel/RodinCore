/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.perf.tests.builder;

import static fr.systerel.perf.tests.PerfUtils.copy;
import static fr.systerel.perf.tests.PerfUtils.createRodinProject;

import java.io.File;

import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPRRoot;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.systerel.perf.tests.Chrono;

/**
 * Runs performance tests on projects. Duration is measured by JUnit and
 * exported to the test report.
 * 
 * @author Nicolas Beauger
 * 
 */
public class BuilderPerfTests extends BuilderTest {

	private final String projectName;

	public BuilderPerfTests(File testProject) throws Exception {
		super(testProject);
		this.projectName = testProject.getName();
	}

	private IRodinProject testProject;

	// tests build (SC+POG+POM , no Prover)
	// 1: only models (no proof file)
	// 2: with empty proof files (no attempt)
	// 3: with many proofs made

	private String makeTestPrjName() {
		return projectName + "_" + testName.getMethodName();
	}

	private String makeChronoName() {
		return testName.getMethodName() + " / " + projectName;
	}

	@Before
	public void init() throws Exception {
		final IRodinDB rodinDB = RodinCore.getRodinDB();
		final IRodinProject project = rodinDB.getRodinProject(projectName);
		Assert.assertTrue(project.exists());

		testProject = createRodinProject(makeTestPrjName());

		final IRodinFile[] ctxs = getRodinFiles(project,
				IContextRoot.ELEMENT_TYPE);
		final IRodinFile[] mchs = getRodinFiles(project,
				IMachineRoot.ELEMENT_TYPE);
		final IRodinFile[] prfs = getRodinFiles(project, IPRRoot.ELEMENT_TYPE);

		copy(ctxs, testProject);
		copy(mchs, testProject);
		copy(prfs, testProject);
	}

	@After
	public void clearLocalProjects() throws Exception {
		testProject.getProject().delete(true, null);
	}

	private static IRodinFile[] getRodinFiles(IRodinProject project,
			IInternalElementType<?> rootType) throws RodinDBException {
		final IInternalElement[] roots = project
				.getRootElementsOfType(rootType);
		final IRodinFile[] files = new IRodinFile[roots.length];
		for (int i = 0; i < roots.length; i++) {
			files[i] = roots[i].getRodinFile();
		}
		return files;
	}

	@Test
	public void buildOnlyModels() throws Exception {
		final IRodinFile[] prfs = getRodinFiles(testProject,
				IPRRoot.ELEMENT_TYPE);
		for (IRodinFile prf : prfs) {
			prf.delete(true, null);
		}

		final Chrono chrono = new Chrono(makeChronoName());
		chrono.startMeasure();
		runBuilder(testProject);
		chrono.endMeasure();
	}

	@Test
	public void buildEmptyProofs() throws Exception {
		final IRodinFile[] prfs = getRodinFiles(testProject,
				IPRRoot.ELEMENT_TYPE);
		for (IRodinFile prf : prfs) {
			prf.getRoot().clear(false, null);
		}

		final Chrono chrono = new Chrono(makeChronoName());
		chrono.startMeasure();
		runBuilder(testProject);
		chrono.endMeasure();
	}

	@Test
	public void buildWithProofs() throws Exception {
		// proof files are kept as is

		final Chrono chrono = new Chrono(makeChronoName());
		chrono.startMeasure();
		runBuilder(testProject);
		chrono.endMeasure();
	}
}
