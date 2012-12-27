/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.pm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

import java.util.Collection;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinedTacticDescriptor;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.eventb.core.tests.pom.POUtil;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Integration tests for the <code>funOvrGoalTac</code> tactic.
 * 
 * @author Laurent Voisin
 */
public class FunOvrGoalTacTests extends BasicTest {
	
	private static final String PO1 = "PO1";

	private static final String TAC_ID = "org.eventb.core.seqprover.funOvrGoalTac";
	
	private static final String[] tactics = new String[] {
		"org.eventb.core.seqprover.goalInHypTac",
		"org.eventb.core.seqprover.shrinkImpHypTac",
		TAC_ID,
	};

	private static void assertIn(Collection<ITacticDescriptor> set) {
		for (ITacticDescriptor desc: set) {
			if (TAC_ID.equals(desc.getTacticID())) {
				return;
			}
		}
		fail("Tactic " + TAC_ID + " not found.");
	}

	private static void assertIn(ITacticDescriptor desc) {
		assertTrue("Tactic " + TAC_ID + " not found.", contains(desc));
	}

	private static boolean contains(ITacticDescriptor desc) {
		if (TAC_ID.equals(desc.getTacticID())) {
			return true;
		}
		if (desc instanceof ICombinedTacticDescriptor) {
			final ICombinedTacticDescriptor comb = (ICombinedTacticDescriptor) desc;
			for (ITacticDescriptor child : comb.getCombinedTactics()) {
				if (contains(child)) {
					return true;
				}
			}
		}
		return false;
	}

	private static void assertDischarged(IPSStatus status)
			throws RodinDBException {
		assertTrue(status.getConfidence() > IConfidence.REVIEWED_MAX);
		assertFalse(status.isBroken());
	}
	
	private static void assertNotDischarged(IPSStatus status)
			throws RodinDBException {
		assertTrue(status.getConfidence() <= IConfidence.PENDING
				|| status.isBroken());
	}

	private IPORoot poRoot;
	private IPSRoot psRoot;
	
	@Before
	public void createProofFiles() throws Exception {
		createPOFile();
		psRoot = poRoot.getPSRoot();
	}
	
	private void createPOFile() throws RodinDBException {
		IRodinFile poFile = rodinProject.getRodinFile("po.bpo");
		poFile.create(true, null);
		poRoot = (IPORoot) poFile.getRoot();
		final ITypeEnvironment typenv = mTypeEnvironment("f=ℤ↔ℤ; g=ℤ↔ℤ; x=ℤ");
		final IPOPredicateSet h0 = POUtil.addPredicateSet(poRoot, "h0", null,
				typenv, //
				"f ∈ ℤ → ℤ", //
				"g ∈ ℤ → ℤ", //
				"x ∈ ℤ", //
				"  x ∈ dom(g) ⇒ g(x) ∈ ℕ", //
				"¬ x ∈ dom(g) ⇒ f(x) ∈ ℕ");
		final ITypeEnvironment empty = mTypeEnvironment();
		POUtil.addSequent(poRoot, PO1, "(fg)(x) ∈ ℕ", h0, empty);
		saveRodinFileOf(poRoot);
	}

	/**
	 * Ensures that the tactic is registered as an auto tactic and part of the
	 * default.
	 */
	@Test
	public void testRegisteredAsAuto() throws Exception {
		final IAutoTacticPreference pref = getAutoTacticPreference();
		assertIn(pref.getDeclaredDescriptors());
		assertIn(pref.getDefaultDescriptor());
	}

	/**
	 * Ensures that the tactic is registered as a post tactic and part of the
	 * default.
	 */
	@Test
	public void testRegisteredAsPost() throws Exception {
		final IAutoTacticPreference pref = EventBPlugin
				.getAutoPostTacticManager().getPostTacticPreference();
		assertIn(pref.getDeclaredDescriptors());
		assertIn(pref.getDefaultDescriptor());
	}

	/**
	 * Ensures that the tactic can discharge an appropriate PO within the
	 * auto-prover.
	 */
	@Test
	public void testAutoDischarge() throws Exception {
		enableAutoProver(tactics);
		runBuilder();
		assertDischarged(psRoot.getStatus(PO1));
	}

	/**
	 * Ensures that the tactic can discharge an appropriate PO within the
	 * post tactic.
	 */
	@Test
	public void testPostDischarge() throws Exception {
		final IPSStatus status = psRoot.getStatus(PO1);

		runBuilder();
		assertNotDischarged(status);

		enablePostTactic(tactics);
		final IUserSupport us = newUserSupport(psRoot);
		us.setCurrentPO(status, null);
		assertTrue(us.getCurrentPO().isClosed());
	}

}
