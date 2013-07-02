/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - added cleanup of attempted proofs
 *     Systerel - improve workspace cleanup
 *******************************************************************************/
package org.eventb.core.tests;

import static org.eventb.core.EventBPlugin.getAutoPostTacticManager;
import static org.eventb.core.EventBPlugin.getProofManager;
import static org.eventb.core.EventBPlugin.getUserSupportManager;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_AUTOTACTIC_CHOICE;
import static org.eventb.core.preferences.autotactics.TacticPreferenceConstants.P_POSTTACTIC_CHOICE;
import static org.eventb.core.seqprover.SequentProver.getAutoTacticRegistry;
import static org.eventb.core.tests.ResourceUtils.importProjectFiles;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
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
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.eventb.core.seqprover.eventbExtensions.TacticCombinators.LoopOnAllPending;
import org.eventb.internal.core.pom.POMTacticPreference;
import org.eventb.internal.core.preferences.TacticsProfilesCache;
import org.junit.After;
import org.junit.Before;
import org.rodinp.core.IInternalElement;
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
public abstract class BuilderTest {

	public static final String PLUGIN_ID = "org.eventb.core.tests";

	protected static final FormulaFactory factory = FormulaFactory.getDefault();

	protected IRodinProject rodinProject;
	protected IEventBProject eventBProject;

	protected IWorkspace workspace = ResourcesPlugin.getWorkspace();

	////////////////////////////////////////////////////////////////
	//
	//  Creating and importing Rodin projects
	//
	////////////////////////////////////////////////////////////////

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
	
	protected void importProject(String prjName) throws Exception {
		importProjectFiles(rodinProject.getProject(), prjName);
	}

	////////////////////////////////////////////////////////////////
	//
	//  Creating and saving event-B files
	//
	////////////////////////////////////////////////////////////////

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

	////////////////////////////////////////////////////////////////
	//
	//  Building projects
	//
	////////////////////////////////////////////////////////////////

	protected void runBuilder() throws CoreException {
		runBuilder(rodinProject);
	}

	protected void runBuilder(IRodinProject rp) throws CoreException {
		final IProject project = rp.getProject();
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		final IMarker[] buildPbs= project.findMarkers(
				RodinMarkerUtil.BUILDPATH_PROBLEM_MARKER,
				true,
				IResource.DEPTH_INFINITE
		);
		if (buildPbs.length != 0) {
			for (final IMarker marker: buildPbs) {
				System.out.println("Build problem for " + marker.getResource());
				System.out.println("  " + marker.getAttribute(IMarker.MESSAGE));
			}
			fail("Build produced build problems, see console");
		}
	}

	////////////////////////////////////////////////////////////////
	//
	//  Auto-prover
	//
	////////////////////////////////////////////////////////////////

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
		enableAutoProver(autoTacticIds);
	}

	protected static void enableAutoProver(String... ids) {
		enableAutoProver(getCombinedTacticDescriptors(ids));
	}

	protected static void enableAutoProver(ITacticDescriptor descriptor) {
		enableAutoTactic(getAutoTacticPreference(), descriptor);
	}
	
	protected static void disableAutoProver() {
		getAutoTacticPreference().setEnabled(false);
	}

	protected static IAutoTacticPreference getAutoTacticPreference() {
		return getAutoPostTacticManager().getAutoTacticPreference();
	}

	private static ITacticDescriptor getCombinedTacticDescriptors(String... ids) {
		assertTrue("There must be at least one tactic", ids.length > 0);
		final IAutoTacticRegistry reg = getAutoTacticRegistry();
		final List<ITacticDescriptor> descs = new ArrayList<ITacticDescriptor>();
		for (final String id : ids) {
			descs.add(reg.getTacticDescriptor(id));
		}
		return loopOnAllPending(reg).combine(descs, PLUGIN_ID + ".autoTactic");
	}

	private static ICombinatorDescriptor loopOnAllPending(
			IAutoTacticRegistry reg) {
		return reg.getCombinatorDescriptor(LoopOnAllPending.COMBINATOR_ID);
	}

	private static void enableAutoTactic(IAutoTacticPreference pref,
			ITacticDescriptor tactic) {
		final IEclipsePreferences node = InstanceScope.INSTANCE
				.getNode(EventBPlugin.PLUGIN_ID);
		final TacticsProfilesCache tactics = new TacticsProfilesCache(node);
		final String name = "builderTestTactic";
		final IPrefMapEntry<ITacticDescriptor> entry = tactics.getEntry(name);
		if (entry == null) {
			tactics.add(name, tactic);
		} else {
			entry.setValue(tactic);
		}
		tactics.store();
		final String choice;
		if (pref instanceof POMTacticPreference) {
			choice = P_AUTOTACTIC_CHOICE;
		} else {
			choice = P_POSTTACTIC_CHOICE;
		}
		node.put(choice, name);

		pref.setSelectedDescriptor(tactic);
		pref.setEnabled(true);
	}

	////////////////////////////////////////////////////////////////
	//
	//  Post-tactic
	//
	////////////////////////////////////////////////////////////////

	private static final String[] postTacticIds = new String[] {
		"org.eventb.core.seqprover.trueGoalTac",
		"org.eventb.core.seqprover.falseHypTac",
		"org.eventb.core.seqprover.goalInHypTac",
		"org.eventb.core.seqprover.autoRewriteTac",
		"org.eventb.core.seqprover.typeRewriteTac",
	};
	
	protected static void enablePostTactic() {
		enablePostTactic(postTacticIds);
	}

	protected static void enablePostTactic(String... tacticIds) {
		enablePostTactic(getCombinedTacticDescriptors(tacticIds));
	}

	protected static void enablePostTactic(ITacticDescriptor descriptor) {
		enableAutoTactic(getPostTacticPreference(), descriptor);
	}

	protected static void disablePostTactic() {
		getPostTacticPreference().setEnabled(false);
	}

	protected static IAutoTacticPreference getPostTacticPreference() {
		return getAutoPostTacticManager().getPostTacticPreference();
	}

	////////////////////////////////////////////////////////////////
	//
	//  Indexer
	//
	////////////////////////////////////////////////////////////////
	
	protected void enableIndexing() {
		DebugHelpers.enableIndexing();
	}

	////////////////////////////////////////////////////////////////
	//
	//  User support and proof attempts
	//
	////////////////////////////////////////////////////////////////

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
	public void createFreshProjectEverythingDisabled() throws Exception {
		// ensure autobuilding is turned off
		IWorkspaceDescription wsDescription = workspace.getDescription();
		if (wsDescription.isAutoBuilding()) {
			wsDescription.setAutoBuilding(false);
			workspace.setDescription(wsDescription);
		}
		
		rodinProject = createRodinProject("P");
		eventBProject = (IEventBProject) rodinProject.getAdapter(IEventBProject.class);
		
		disableAutoProver();
		disablePostTactic();
		
		DebugHelpers.disableIndexing();
	}

	public static void setReadOnly(IResource resource, boolean readOnly)
			throws CoreException {
		final ResourceAttributes attrs = resource.getResourceAttributes();
		if (attrs != null && attrs.isReadOnly() != readOnly) {
			attrs.setReadOnly(readOnly);
			resource.setResourceAttributes(attrs);
		}
	}

	@After
	public void cleanupWorkspaceAndProofAttempts() throws Exception {
		cleanupWorkspace();
		deleteAllProofAttempts();
	}

	/**
	 * Deletes all resources, markers, etc from the workspace. We need to first
	 * remove all read-only attributes on resources to ensure that they get
	 * properly deleted.
	 */
	private void cleanupWorkspace() throws CoreException {
		final IWorkspaceRoot root = workspace.getRoot();
		root.accept(CLEANUP);
		root.delete(true, null);
	}

	private static final IResourceVisitor CLEANUP = new IResourceVisitor() {

		@Override
		public boolean visit(final IResource resource) throws CoreException {
			setReadOnly(resource, false);
			return true;
		}

	};

}
