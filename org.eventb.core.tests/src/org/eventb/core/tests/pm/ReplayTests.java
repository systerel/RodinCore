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
package org.eventb.core.tests.pm;

import static org.eventb.core.EventBPlugin.rebuildProof;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.tests.BuilderTest;
import org.rodinp.core.RodinDBException;

public class ReplayTests extends BuilderTest {

	/**
	 * Ensures that old WD POs can be replayed for a small example component.
	 */
	public void testSmall() throws Exception {
		importProject("Small");
		runBuilder();
		assertUndischarged("c", "axm4/WD", "axm4/THM", "axm2/WD");
		replay("c");
		assertUndischarged("c", "axm4/THM");
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
			rebuildProof(proof, false, null);
		}
	}

}
