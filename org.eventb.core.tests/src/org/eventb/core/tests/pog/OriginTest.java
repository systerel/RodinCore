/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.pog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.junit.Before;
import org.junit.Test;

/**
 * @author A. Gilles
 * 
 */
public class OriginTest extends EventBPOTest {

	private IContextRoot ctx;

	@Before
	public void createInitialContext() throws Exception {
		ctx = createContext("ctx_origin");
	}

	protected static void disablePostTactic() {
		final IAutoTacticPreference p = EventBPlugin.getAutoPostTacticManager()
				.getPostTacticPreference();
		p.setEnabled(false);
	}

	@Test
	public void testOrigin() throws Exception {
		final IAxiom hypExpected = createTheorem("thm1", "1=1", false);
		final IAxiom goalExpected = createTheorem("axm1", "1=1 ∨ 2=2", true);

		runBuilder();

		final IPSRoot[] statusFiles = rodinProject
				.getRootElementsOfType(IPSRoot.ELEMENT_TYPE);
		assertEquals(1, statusFiles.length);

		final IPSRoot statusRoot = statusFiles[0];

		final IPSStatus[] statuses = statusRoot.getStatuses();
		assertEquals(1, statuses.length);
		final IPSStatus st = statuses[0];

		final IUserSupportManager usm = EventBPlugin.getUserSupportManager();
		final IUserSupport us = usm.newUserSupport();
		us.setInput((IPSRoot) st.getRoot());
		us.setCurrentPO(st, null);
		final IProofState po = us.getCurrentPO();
		assertNotNull(po);
		final IProofTreeNode node = po.getCurrentNode().getProofTree()
				.getRoot();
		assertNotNull(node);

		final Object goalOrigin = node.getSequent().goal().getSourceLocation()
				.getOrigin();
		final Iterator<Predicate> hypIterator = node.getSequent().hypIterable()
				.iterator();

		assertEquals("The PO origin should be the Theorem", goalExpected,
				goalOrigin);
		assertTrue("The PO should have hyp", hypIterator.hasNext());
		assertEquals("The PO hyp should be the Axiom", hypExpected, hypIterator
				.next().getSourceLocation().getOrigin());
		assertFalse("The PO should have only one hyp", hypIterator.hasNext());
		us.dispose();
	}

	private IAxiom createTheorem(String thmLabel, String thmString,
			boolean derived) throws Exception {
		final IAxiom axiom = addAxiom(ctx, thmLabel, thmString, derived);
		saveRodinFileOf(ctx);
		return axiom;
	}

}
