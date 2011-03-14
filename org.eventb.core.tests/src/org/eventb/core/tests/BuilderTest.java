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
package org.eventb.core.tests;

import static org.eventb.core.EventBPlugin.getProofManager;
import static org.eventb.core.EventBPlugin.getUserSupportManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBProject;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;
import org.rodinp.internal.core.debug.DebugHelpers;

/**
 * Abstract class for builder tests.
 * 
 * @author Laurent Voisin
 */
public abstract class BuilderTest extends TestCase {

	public static final String PLUGIN_ID = "org.eventb.core.tests";

	protected static FormulaFactory factory = FormulaFactory.getDefault();

	protected IRodinProject rodinProject;
	protected IEventBProject eventBProject;

	protected IWorkspace workspace = ResourcesPlugin.getWorkspace();

	public BuilderTest() {
		super();
	}

	public BuilderTest(String name) {
		super(name);
	}
	
	protected IContextRoot createContext(String bareName) throws RodinDBException {
		final IContextRoot result = eventBProject.getContextRoot(bareName);
		createRodinFileOf(result);
		return result;
	}

	protected IMachineRoot createMachine(String bareName) throws RodinDBException {
		final IMachineRoot result = eventBProject.getMachineRoot(bareName);
		createRodinFileOf(result);
		return result;
	}

	protected IPORoot createPOFile(String bareName) throws RodinDBException {
		final IPORoot result = eventBProject.getPORoot(bareName);
		createRodinFileOf(result);
		return result;
	}

	protected ISCContextRoot createSCContext(String bareName) throws RodinDBException {
		final ISCContextRoot result = eventBProject.getSCContextRoot(bareName);
		createRodinFileOf(result);
		return result;
	}

	protected ISCMachineRoot createSCMachine(String bareName) throws RodinDBException {
		final ISCMachineRoot result = eventBProject.getSCMachineRoot(bareName);
		createRodinFileOf(result);
		return result;
	}
	
	private void createRodinFileOf(IInternalElement result)
			throws RodinDBException {
		result.getRodinFile().create(true, null);
	}
	
	public static void saveRodinFileOf(IInternalElement elem) throws RodinDBException {
		elem.getRodinFile().save(null, false);
	}
	
	protected void runBuilder() throws CoreException {
		runBuilder(rodinProject);
	}

	protected void runBuilder(IRodinProject rp) throws CoreException {
		final IProject project = rp.getProject();
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
				.getAutoPostTacticManager().getAutoTacticPreference();
		enableAutoTactics(autoPref, autoTacticIds);
	}

	protected static void disableAutoProver() {
		EventBPlugin.getAutoPostTacticManager().getAutoTacticPreference()
				.setEnabled(false);
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
				.getAutoPostTacticManager().getPostTacticPreference();
		enableAutoTactics(postPref, postTacticIds);
	}

	protected static void disablePostTactics() {
		EventBPlugin.getAutoPostTacticManager().getPostTacticPreference()
				.setEnabled(false);
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

	/**
	 * Imports files of a template project into an already existing project.
	 * 
	 * @param dest
	 *            destination project. Must already exist and be configured
	 * @param srcName
	 *            name of the source project which lies in the
	 *            <code>projects</code> folder of this plug-in
	 * @throws Exception
	 *             in case of error
	 */
	public static void importProjectFiles(IProject dest, String srcName)
			throws Exception {
		final URL entry = Platform.getBundle(PLUGIN_ID).getEntry("projects");
		final URL projectsURL = FileLocator.toFileURL(entry);
		final File projectsDir = new File(projectsURL.toURI());
		for (final File project : projectsDir.listFiles()) {
			if (project.isDirectory() && project.getName().equals(srcName))
				importFiles(dest, project, true);
		}
	}

	private static void importFiles(IProject project, File root, boolean isRoot)
			throws IOException, CoreException {
		for (final File file : root.listFiles()) {
			final String filename = file.getName();
			if (file.isFile()) {
				final InputStream is = new FileInputStream(file);
				final String name = (isRoot) ? filename : root.getName() + "/"
						+ filename;
				final IFile target = project.getFile(name);
				target.create(is, false, null);
			} else if (file.isDirectory() && !filename.equals(".svn")) {
				final IFolder folder = project.getFolder(filename);
				folder.create(true, false, null);
				importFiles(project, file, false);
			}
		}
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		// ensure autobuilding is turned off
		IWorkspaceDescription wsDescription = workspace.getDescription();
		if (wsDescription.isAutoBuilding()) {
			wsDescription.setAutoBuilding(false);
			workspace.setDescription(wsDescription);
		}
		
		rodinProject = createRodinProject("P");
		eventBProject = (IEventBProject) rodinProject.getAdapter(IEventBProject.class);
		
		disableAutoProver();
		disablePostTactics();
		
		DebugHelpers.disableIndexing();
	}

	protected IRodinProject createRodinProject(String projectName)
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
	
	@Override
	protected void tearDown() throws Exception {
		// Delete all Rodin projects
		final IRodinDB rodinDB = RodinCore.getRodinDB();
		for (IRodinProject rp: rodinDB.getRodinProjects()) {
			rp.getProject().delete(true, true, null);
		}
		
		deleteAllProofAttempts();
		
		super.tearDown();
	}

}
