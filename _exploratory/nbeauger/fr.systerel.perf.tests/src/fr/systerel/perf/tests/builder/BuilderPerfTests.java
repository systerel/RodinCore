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

import static fr.systerel.perf.tests.RodinDBUtils.createRodinProject;
import static fr.systerel.perf.tests.RodinDBUtils.getProject;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPRRoot;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
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

	private static final List<IRodinProject> LOCAL_PROJECTS = new ArrayList<IRodinProject>();
	
	// tests build (SC+POG+POM , no Prover)
	// 1: only models (no proof file)
	// 2: with empty proof files (no attempt)
	// 3: with many proofs made

	// test Prove only

	private static String makeOnlyModelsPrjName() {
		return projectName + "_onlyModels";
	}

	private static String makeEmptyProofsPrjName() {
		return projectName + "_emptyProofs";
	}

	private static String makeWithProofsPrjName() {
		return projectName + "_withProofs";
	}

	@BeforeClass
	public static void makeProjects() throws Exception {

		final IRodinDB rodinDB = RodinCore.getRodinDB();
		final IRodinProject project = rodinDB.getRodinProject(projectName);
		Assert.assertTrue(project.exists());

		final IRodinProject onlyModelsPrj = createRodinProject(makeOnlyModelsPrjName());
		LOCAL_PROJECTS.add(onlyModelsPrj);
		final IRodinProject emptyProofsPrj = createRodinProject(makeEmptyProofsPrjName());
		LOCAL_PROJECTS.add(emptyProofsPrj);
		
		final IRodinProject withProofsPrj = createRodinProject(makeWithProofsPrjName());
		LOCAL_PROJECTS.add(withProofsPrj);			

		final IRodinProject[] prjs = LOCAL_PROJECTS.toArray(new IRodinProject[LOCAL_PROJECTS.size()]);
		
		final IRodinFile[] ctxs = getRodinFiles(project, IContextRoot.ELEMENT_TYPE);
		final IRodinFile[] mchs = getRodinFiles(project, IMachineRoot.ELEMENT_TYPE);
		final IRodinFile[] prfs = getRodinFiles(project, IPRRoot.ELEMENT_TYPE);
		
		for (IRodinProject prj : prjs) {
			rodinDB.copy(ctxs, array(prj), null, null, false, null);
			rodinDB.copy(mchs, array(prj), null, null, false, null);
		}
		
		rodinDB.copy(prfs, array(emptyProofsPrj), null, null, false, null);
		rodinDB.copy(prfs, array(withProofsPrj), null, null, false, null);
		
		for (IRodinFile file : emptyProofsPrj.getRodinFiles()) {
			final String fileName = file.getElementName();
			if (fileName.endsWith(".bpr")) {
				file.getRoot().clear(false, null);
				continue;
			}
		}
	}

	private static IRodinFile[] getRodinFiles(IRodinProject project, IInternalElementType<?> rootType)
			throws RodinDBException {
		final IInternalElement[] roots = project.getRootElementsOfType(rootType);
		final IRodinFile[] files = new IRodinFile[roots.length];
		for (int i=0;i<roots.length;i++) {
			files[i]= roots[i].getRodinFile();
		}
		return files;
	}

	@AfterClass
	public static void clearLocalProjects() throws Exception {
		for(IRodinProject project:LOCAL_PROJECTS) {
			project.getProject().delete(true, null);
		}
	}

	@Test
	public void testOnlyModelsFile() throws Exception {
		final IProject project = getProject(makeOnlyModelsPrjName());
//		final IRodinProject rodinProject = getRodinProject(makeOnlyModelsPrjName());
//		final IRodinFile[] rodinFiles = rodinProject.getRodinFiles();
		final Chrono chrono = new Chrono(testName);
		chrono.startMeasure();
		runBuilder(project);
		chrono.endMeasure();
	}

	@Test
	public void testEmptyProofs() throws Exception {
		final IProject project = getProject(makeEmptyProofsPrjName());
//		final IRodinProject rodinProject = getRodinProject(makeEmptyProofsPrjName());
//		final IRodinFile[] rodinFiles = rodinProject.getRodinFiles();
//		final IPRRoot[] proofs = rodinProject.getRootElementsOfType(IPRRoot.ELEMENT_TYPE);
//		for (IPRRoot prf : proofs) {
//			Assert.assertFalse(prf.hasChildren());
//		}
		final Chrono chrono = new Chrono(testName);
		chrono.startMeasure();
		runBuilder(project);
		chrono.endMeasure();
	}

	@Test
	public void testWithProofs() throws Exception {
		final IProject project = getProject(makeWithProofsPrjName());
//		final IRodinProject rodinProject = getRodinProject(makeWithProofsPrjName());
//		final IRodinFile[] rodinFiles = rodinProject.getRodinFiles();
//		final IPRRoot[] proofs = rodinProject.getRootElementsOfType(IPRRoot.ELEMENT_TYPE);
//		for (IPRRoot prf : proofs) {
//			if(prf.hasChildren()) {
//				System.out.println("found for "+prf.getElementName());
//			}
//		}
		final Chrono chrono = new Chrono(testName);
		chrono.startMeasure();
		runBuilder(project);
		chrono.endMeasure();
	}
}
