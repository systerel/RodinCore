/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored enableAutoProver
 *     Systerel - added post-tactic manipulation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.tests.pm;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.eventb.core.EventBPlugin.getAutoPostTacticManager;
import static org.eventb.core.EventBPlugin.getUserSupportManager;
import static org.eventb.core.seqprover.SequentProver.getAutoTacticRegistry;
import static org.eventb.core.tests.BuilderTest.PLUGIN_ID;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.eventb.core.seqprover.eventbExtensions.TacticCombinators.LoopOnAllPending;
import org.junit.After;
import org.junit.Before;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;

/**
 * Abstract class for builder tests.
 * 
 * FIXME 3.0 Merge this class with BuilderTest
 * 
 * @author Laurent Voisin
 */
public abstract class BasicTest {
	
	protected static FormulaFactory factory = FormulaFactory.getDefault();

	protected IWorkspace workspace = ResourcesPlugin.getWorkspace();
	
	public BasicTest() {
		super();
	}

	protected IRodinProject rodinProject;

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
		checkPSFiles();
	}

	private void checkPSFiles() throws RodinDBException {
		IRodinFile[] files = rodinProject.getRodinFiles();
		for (IRodinFile file: files) {
			IInternalElement root = file.getRoot();
			if (root instanceof IPSRoot) {
				checkPSFile((IPSRoot) root);
			}
		}
	}

	private void checkPSFile(IPSRoot root) throws RodinDBException {
		for (IPSStatus psStatus: root.getStatuses()) {
			final IPOSequent poSequent = psStatus.getPOSequent();
			assertEquals("PS file not in sync with PO file",
					poSequent.getPOStamp(), psStatus.getPOStamp());
		}
	}
	
	private static final String[] autoTacticIds = new String[] {
		"org.eventb.core.seqprover.trueGoalTac",
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
	
	protected static void enableTestAutoProver() {
		enableAutoProver(autoTacticIds);
	}

	protected static void enableAutoProver(String... ids) {
		final IAutoTacticPreference pref = getAutoTacticPreference();
		enablePreference(pref, getCombinedTacticDescriptors(ids));
	}

	protected static IAutoTacticPreference getAutoTacticPreference() {
		return getAutoPostTacticManager().getAutoTacticPreference();
	}

	protected static void enablePostTactic(String... ids) {
		final IAutoTacticPreference pref = getPostTacticPreference();
		enablePreference(pref, getCombinedTacticDescriptors(ids));
	}

	protected static void disablePostTactic() {
		final IAutoTacticPreference pref = getPostTacticPreference();
		pref.setEnabled(false);
	}

	private static IAutoTacticPreference getPostTacticPreference() {
		return getAutoPostTacticManager().getPostTacticPreference();
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

	private static void enablePreference(IAutoTacticPreference pref,
			ITacticDescriptor descriptor) {
		pref.setSelectedDescriptor(descriptor);
		pref.setEnabled(true);
	}

	protected static void disableAutoProver() {
		getAutoTacticPreference().setEnabled(false);
	}
	
	protected static IUserSupport newUserSupport(IPSRoot psRoot) {
		final IUserSupportManager usm = getUserSupportManager();
		final IUserSupport us = usm.newUserSupport();
		us.setInput(psRoot.getRodinFile());
		return us;
	}

	protected static void saveRodinFileOf(IInternalElement elem)
			throws RodinDBException {
		elem.getRodinFile().save(null, false);
	}

	@Before
	public void setUpBT() throws Exception {
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
		
		disableAutoProver();
	}
	
	@After
	public void tearDownBT() throws Exception {
		rodinProject.getProject().delete(true, true, null);
	}


}
