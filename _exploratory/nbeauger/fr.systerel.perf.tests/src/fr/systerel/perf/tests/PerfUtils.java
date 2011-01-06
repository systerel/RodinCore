/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.perf.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.XMLFormatter;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.internal.core.debug.DebugHelpers;



/**
 * @author Nicolas Beauger
 *
 */
public class PerfUtils {

	public static final Logger logger = Logger
			.getLogger("Rodin Performance Measurement");

	static {
		try {
			final FileHandler fh = new FileHandler("Results.log", true);
			fh.setFormatter(new XMLFormatter());
			// fh.setFormatter(new SimpleFormatter());
			logger.addHandler(fh);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static final IWorkspace WORKSPACE = ResourcesPlugin.getWorkspace();

	public static IWorkspaceRoot getWorkspaceRoot() {
		return PerfUtils.WORKSPACE.getRoot();
	}

	public static IWorkspace getWorkspace() {
		return WORKSPACE;
	}

	public static IProject getProject(String project) {
		return getWorkspaceRoot().getProject(project);
	}

	/**
	 * Returns the Rodin Project with the given name in this test
	 * suite's database. This is a convenience method.
	 */
	public static IRodinProject getRodinProject(String name) {
		IProject project = getProject(name);
		return RodinCore.valueOf(project);
	}

	public static IRodinProject createRodinProject(String projectName)
			throws CoreException {
		IProject project = getWorkspaceRoot().getProject(projectName);
		project.create(null);
		project.open(null);
		IProjectDescription pDescription = project.getDescription();
		pDescription.setNatureIds(new String[] {RodinCore.NATURE_ID});
		project.setDescription(pDescription, null);
		IRodinProject result = RodinCore.valueOf(project);
		return result;
	}

	public static void deleteAllProjects() throws RodinDBException, CoreException {
		// Delete all Rodin projects
		final IRodinDB rodinDB = RodinCore.getRodinDB();
		for (IRodinProject rp: rodinDB.getRodinProjects()) {
			rp.getProject().delete(true, true, null);
		}
	}

	public static void disableAllAuto() throws CoreException {
		// ensure autobuilding is turned off
		IWorkspaceDescription wsDescription = getWorkspace().getDescription();
		if (wsDescription.isAutoBuilding()) {
			wsDescription.setAutoBuilding(false);
			getWorkspace().setDescription(wsDescription);
		}
		
		PerfUtils.disableAutoProver();
		PerfUtils.disablePostTactics();
		
		DebugHelpers.disableIndexing();
	}

	public static void disableAutoProver() {
		EventBPlugin.getAutoTacticPreference().setEnabled(false);
	}

	public static void disablePostTactics() {
		EventBPlugin.getPostTacticPreference().setEnabled(false);
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
	private static final String[] postTacticIds = new String[] {
		"org.eventb.core.seqprover.trueGoalTac",
		"org.eventb.core.seqprover.falseHypTac",
		"org.eventb.core.seqprover.goalInHypTac",
		"org.eventb.core.seqprover.autoRewriteTac",
		"org.eventb.core.seqprover.typeRewriteTac",
	};

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

	public static void enableAutoProver() {
		final IAutoTacticPreference autoPref = EventBPlugin
				.getAutoTacticPreference();
		enableAutoTactics(autoPref, autoTacticIds);
	}

	public static void enablePostTactics() {
		final IAutoTacticPreference postPref = EventBPlugin
				.getPostTacticPreference();
		enableAutoTactics(postPref, postTacticIds);
	}
	
}
