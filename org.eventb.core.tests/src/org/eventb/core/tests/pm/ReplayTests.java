/*******************************************************************************
 * Copyright (c) 2010, 2023 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.pm;

import static org.eventb.core.EventBPlugin.rebuildProof;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.tests.BuilderTest;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

public class ReplayTests extends BuilderTest {

	/**
	 * Ensures that old WD POs can be replayed for a small example component.
	 */
	@Test
	public void testSmall() throws Exception {
		importProject("Small");
		runBuilder();
		assertUndischarged("c", "axm4/WD", "axm4/THM", "axm2/WD");
		replay("c");
		assertUndischarged("c", "axm4/THM");
	}

	/**
	 * Regression test for bug {@link #bug723()}. Ensure that the post-tactic is
	 * run after replay.
	 * <p>
	 * Context d contains two proofs:
	 * <li>a "replayable" partial proof, which gets completed by the post
	 * tactic;</li>
	 * <li>a "notReplayable" complete proof mentioning an unknown reasoner, that
	 * gets reused with {@link IConfidence#UNCERTAIN_MAX} confidence, hence
	 * remains undischarged.</li>
	 * </p>
	 */
	@Test
	public void bug723() throws Exception {
		importProject("Small");
		runBuilder();
		assertUndischarged("d", "replayable/THM", "notReplayable/THM");
		enablePostTactic("org.eventb.core.seqprover.trueGoalTac");
		replay("d");
		assertUndischarged("d", "notReplayable/THM");
	}

	/**
	 * Regression test for bug #805.
	 *
	 * Replaying the provided proof was causing an IllegalArgumentException.
	 */
	@Test
	public void bug805() throws Exception {
		importProject("Bug805");
		runBuilder();
		replay("c");
	}

	private void assertUndischarged(String compName, String... pos)
			throws CoreException {
		final Set<String> actual = getUndischarged(compName);
		final Set<String> expected = new HashSet<String>(Arrays.asList(pos));
		assertEquals("Undischarged POs do not match", expected, actual);
	}

	private Set<String> getUndischarged(String compName) throws CoreException {
		final Set<String> result = new HashSet<String>();
		for (final IPSStatus s : getStatuses(compName)) {
			if (s.getConfidence() <= IConfidence.REVIEWED_MAX || s.isBroken()) {
				result.add(s.getElementName());
			}
		}
		return result;
	}

	private IPSStatus[] getStatuses(String compName) throws RodinDBException {
		return eventBProject.getPSRoot(compName).getStatuses();
	}

	private void replay(String compName) throws CoreException {
		final IPRRoot prRoot = eventBProject.getPRRoot(compName);
		final Set<String> undischarged = getUndischarged(compName);
		for (final String poName : undischarged) {
			final IPRProof proof = prRoot.getProof(poName);
			rebuildProof(proof, true, null);
		}
	}

}
