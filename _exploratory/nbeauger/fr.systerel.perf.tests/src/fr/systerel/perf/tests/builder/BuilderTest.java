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
 *     Systerel - added cleanup of attempted proofs
 *******************************************************************************/
package fr.systerel.perf.tests.builder;

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
import java.util.Calendar;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.URIUtil;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinMarkerUtil;
import org.rodinp.internal.core.debug.DebugHelpers;

/**
 * Abstract class for builder tests.
 * 
 * @author Laurent Voisin
 */
public abstract class BuilderTest {
	
	@Rule
	public static final TestName testName = new TestName();

	public static final String projectPath = System.getProperty("projectPath");
	protected static String projectName;

	public static final String PLUGIN_ID = "fr.systerel.performances.tests";

	protected static final FormulaFactory factory = FormulaFactory.getDefault();

	protected static final IWorkspace workspace = ResourcesPlugin.getWorkspace();

	public static <T> T[] array(T... objs) {
		return objs;
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

	private static void enableAutoTactics(IAutoTacticPreference pref,
			String[] tacticIds) {
		final List<ITacticDescriptor> descrs = new ArrayList<ITacticDescriptor>(
				tacticIds.length);
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		for (String id : tacticIds) {
			descrs.add(reg.getTacticDescriptor(id));
		}
		pref.setSelectedDescriptors(descrs);
		pref.setEnabled(true);
	}

	private static final String[] autoTacticIds = new String[] {
		"org.eventb.core.seqprover.trueGoalTac",
		"org.eventb.core.seqprover.falseHypTac",
		"org.eventb.core.seqprover.goalInHypTac",
		"org.eventb.core.seqprover.funGoalTac",
		"org.eventb.core.seqprover.autoRewriteTac",
		"org.eventb.core.seqprover.typeRewriteTac",
		"org.eventb.core.seqprover.findContrHypsTac",
		"org.eventb.core.seqprover.eqHypTac",
		"org.eventb.core.seqprover.shrinkImpHypTac",
		"org.eventb.core.seqprover.clarifyGoalTac",
	};
	
	protected static void enableAutoProver() {
		final IAutoTacticPreference autoPref = EventBPlugin
				.getAutoTacticPreference();
		enableAutoTactics(autoPref, autoTacticIds);
	}

	protected static void disableAutoProver() {
		EventBPlugin.getAutoTacticPreference().setEnabled(false);
	}

	private static final String[] postTacticIds = new String[] {
		"org.eventb.core.seqprover.trueGoalTac",
		"org.eventb.core.seqprover.falseHypTac",
		"org.eventb.core.seqprover.goalInHypTac",
		"org.eventb.core.seqprover.autoRewriteTac",
		"org.eventb.core.seqprover.typeRewriteTac",
	};
	
	protected static void enablePostTactics() {
		final IAutoTacticPreference postPref = EventBPlugin
				.getPostTacticPreference();
		enableAutoTactics(postPref, postTacticIds);
	}

	protected static void disablePostTactics() {
		EventBPlugin.getPostTacticPreference().setEnabled(false);
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

	private static void disableAllAuto() throws CoreException {
		// ensure autobuilding is turned off
		IWorkspaceDescription wsDescription = workspace.getDescription();
		if (wsDescription.isAutoBuilding()) {
			wsDescription.setAutoBuilding(false);
			workspace.setDescription(wsDescription);
		}
		
		disableAutoProver();
		disablePostTactics();
		
		DebugHelpers.disableIndexing();
	}

	protected static IRodinProject createRodinProject(String projectName)
			throws CoreException {
		IProject project = workspace.getRoot().getProject(projectName);
		project.create(null);
		project.open(null);
		IProjectDescription pDescription = project.getDescription();
		pDescription.setNatureIds(new String[] {RodinCore.NATURE_ID});
		project.setDescription(pDescription, null);
		IRodinProject result = RodinCore.valueOf(project);
		return result;
	}
	
	@AfterClass
	public static void clearWorkspace() throws Exception {
		// Delete all Rodin projects
		final IRodinDB rodinDB = RodinCore.getRodinDB();
		for (IRodinProject rp: rodinDB.getRodinProjects()) {
			rp.getProject().delete(true, true, null);
		}
		
		deleteAllProofAttempts();
		
	}

	public static IWorkspaceRoot getWorkspaceRoot() {
		return workspace.getRoot();
	}

	/**
	 * Returns the Rodin Project with the given name in this test
	 * suite's database. This is a convenience method.
	 */
	public static IRodinProject getRodinProject(String name) {
		IProject project = getProject(name);
		return RodinCore.valueOf(project);
	}
	
	protected static IProject getProject(String project) {
		return getWorkspaceRoot().getProject(project);
	}
	
	@BeforeClass
	public static void importProject() throws Exception {
		assertNotNull(projectPath);
		final URI uri = URIUtil.fromString(projectPath);
		projectName = URIUtil.lastSegment(uri);

		disableAllAuto();
		final URI absURI = URIUtil.makeAbsolute(uri, URIUtil.fromString("file://"));
		final File project = URIUtil.toFile(absURI);
		assertTrue(project.isDirectory());
		importProject(project);
	}

	private static void importProject(File projectDir) throws Exception {
		final String projectName = projectDir.getName();
		IProject project = getWorkspaceRoot().getProject(projectName);
		IProjectDescription desc = workspace.newProjectDescription(projectName); 
		desc.setNatureIds(new String[] {RodinCore.NATURE_ID});
		project.create(desc, null);
		project.open(null);
		IProjectNature nature = project.getNature(RodinCore.NATURE_ID);
		nature.configure();
		importFiles(project, projectDir, true);
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

	public static class Chrono {
		
		private final String name;
		private long startTime;
		
		public Chrono() {
			this.name = testName.getMethodName();
		}
		
		public void startMeasure() {
			startTime = Calendar.getInstance().getTimeInMillis();
		}

		public void endMeasure() {
			final long endTime = Calendar.getInstance().getTimeInMillis();
			
			final long duration = endTime - startTime;
			System.out.println(name + " : " + duration);
		}
	}
	
	protected static long startMeasure() {
		return Calendar.getInstance().getTimeInMillis();
	}
	
}
