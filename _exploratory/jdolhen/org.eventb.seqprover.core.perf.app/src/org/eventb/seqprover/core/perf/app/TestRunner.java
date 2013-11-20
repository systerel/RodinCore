/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.seqprover.core.perf.app;

import static org.eventb.seqprover.core.perf.app.tactics.Tactics.bareRodinTactic;
import static org.eventb.seqprover.core.perf.app.tactics.Tactics.mbGoalTactic;

import java.util.ArrayList;
import java.util.List;

import org.rodinp.core.IRodinProject;

/**
 * Performance test for the tactics (Sequent prover) based on full Event-B
 * projects.
 * 
 * @author Josselin Dolhen
 */
public class TestRunner {

	private final IRodinProject[] projects;

	private final List<TacticTest> tests = new ArrayList<TacticTest>();

	public TestRunner(IRodinProject[] projects) {
		this.projects = projects;
		setTestCases();
	}

	// Sets the list of tactics to run
	private void setTestCases() {
		tests.add(new TacticTest("Rodin", bareRodinTactic()));
		tests.add(new TacticTest("MbGoal", mbGoalTactic()));
	}

	/**
	 * Launch the tests on all Rodin projects contained in the projects
	 * directory.
	 */
	public void testProjects() throws Exception {
		for (final IRodinProject project : projects) {
			testProject(project);
		}
	}

	// Launch all tactic tests on the given project.
	private void testProject(IRodinProject project) throws Exception {
		System.out.println(project.getElementName());
		for (final TacticTest test : tests) {
			test.run(project);
		}
	}

}
