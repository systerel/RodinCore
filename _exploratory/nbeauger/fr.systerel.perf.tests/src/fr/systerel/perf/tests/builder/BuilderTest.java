/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - added cleanup of attempted proofs
 *******************************************************************************/
package fr.systerel.perf.tests.builder;

import static fr.systerel.perf.tests.PerfUtils.deleteAllProjects;
import static fr.systerel.perf.tests.PerfUtils.disableAllAuto;
import static fr.systerel.perf.tests.PerfUtils.getWorkspace;
import static fr.systerel.perf.tests.PerfUtils.getWorkspaceRoot;
import static org.eventb.core.EventBPlugin.getProofManager;
import static org.eventb.core.EventBPlugin.getUserSupportManager;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.URIUtil;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IUserSupport;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinMarkerUtil;

/**
 * Abstract class for builder performance tests.
 * 
 * @author Nicolas Beauger
 */
@RunWith(Parameterized.class)
public abstract class BuilderTest {
	
	@Rule
	public static final TestName testName = new TestName();

	@Parameters
	public static List<File[]> getProjects() throws Exception {
		final String projectPath = System.getProperty("projectPath");
		assertNotNull(projectPath);
		final URI uri = URIUtil.fromString(projectPath);
		final URI absURI = URIUtil.makeAbsolute(uri, URIUtil.fromString("file://"));
		final File project = URIUtil.toFile(absURI);
		assertTrue(project.isDirectory());
		final File[] testProjects = project.listFiles();
		final List<File[]> res = new ArrayList<File[]>(testProjects.length);
		for (File file : testProjects) {
			res.add(new File[] { file });
		}		
		return res;
	}

	protected final IRodinProject project;
	
	public BuilderTest(File projectDir) throws Exception {
		this.project = importProject(projectDir);
	}
	
	public static final String PLUGIN_ID = "fr.systerel.performances.tests";

	protected static final FormulaFactory factory = FormulaFactory.getDefault();
	
	protected void runBuilder(IRodinProject rodinProject) throws CoreException {
		runBuilder(rodinProject.getProject());
	}
	
	protected void runBuilder(IProject project) throws CoreException {
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

	/**
	 * Deletes all user supports and proof attempts that where created and not
	 * cleaned up.
	 */
	protected static void deleteAllProofAttempts() {
		for (final IUserSupport us : getUserSupportManager().getUserSupports()) {
			us.dispose();
		}
		for (final IProofAttempt pa : getProofManager().getProofAttempts()) {
			pa.dispose();
		}
	}

	@Before
	public void setUp() throws Exception {
		
		disableAllAuto();
	}

	@AfterClass
	public static void clearWorkspace() throws Exception {
		deleteAllProjects();
		
		deleteAllProofAttempts();
		
	}

	private static IRodinProject importProject(File projectDir) throws Exception {
		disableAllAuto();
		clearWorkspace();
		final String projectName = projectDir.getName();
		IProject project = getWorkspaceRoot().getProject(projectName);
		IProjectDescription desc = getWorkspace().newProjectDescription(projectName); 
		desc.setNatureIds(new String[] {RodinCore.NATURE_ID});
		project.create(desc, null);
		project.open(null);
		IProjectNature nature = project.getNature(RodinCore.NATURE_ID);
		nature.configure();
		importFiles(project, projectDir, true);

		final IRodinDB rodinDB = RodinCore.getRodinDB();
		return rodinDB.getRodinProject(projectName);
	}
	
	protected static void importFiles(IProject project, File root, boolean isRoot)
			throws Exception {
		for (File file : root.listFiles()) {
			if (file.isFile()) {
				InputStream is = new FileInputStream(file);
				final String name = (isRoot) ? file.getName() : root.getName()
						+ "/" + file.getName();
				IFile target = project.getFile(name);
				if (target.exists()) { // true for ".project" if present
					target.delete(true, null);
				}
				target.create(is, false, null);
			} else if (file.isDirectory() && !file.getName().equals(".svn")) {
				IFolder folder = project.getFolder(file.getName());
				folder.create(true, false, null);
				importFiles(project, file, false);
			}
		}
	}
	
}
