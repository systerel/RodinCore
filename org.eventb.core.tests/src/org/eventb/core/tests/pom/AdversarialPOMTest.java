/*******************************************************************************
 * Copyright (c) 2011, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.pom;

import static org.eventb.core.tests.pom.POUtil.addPredicateSet;
import static org.eventb.core.tests.pom.POUtil.addSequent;
import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.seqprover.IProofSkeleton;
import org.junit.Before;
import org.junit.Test;

/**
 * Ensures that POM behaves correctly, even in the presence of adversarial
 * plug-ins.
 * 
 * @author Laurent Voisin
 */
public class AdversarialPOMTest extends AutoPOMTest {

	private IPSStatus psStatus;

	@Before
	public void setUp() throws Exception {
		createPOFile();
		enableAutoProver("org.eventb.core.tests.adversarialTac");
		AdversarialReasoner.reset();
	}

	private void createPOFile() throws CoreException {
		poRoot = createPOFile("x");
		final ITypeEnvironment te = mTypeEnvironment();
		final IPOPredicateSet h0 = addPredicateSet(poRoot, "h0", null, te);
		addSequent(poRoot, "PO1", "⊤", h0, te);
		psStatus = poRoot.getPSRoot().getStatus("PO1");
		saveRodinFileOf(poRoot);
	}

	/**
	 * Ensures that POM works correctly when no problems is caused by an
	 * adversarial plug-in.
	 */
	@Test
	public final void testNormal() throws CoreException {
		runBuilder();
		assertDischarged(psStatus);
	}

	/**
	 * Ensures that POM correctly traps serialization problems caused by an
	 * adversarial plug-in.
	 */
	@Test
	public final void testSerializationProblem() throws CoreException {
		AdversarialReasoner.erroneousSerializeInput = true;
		runBuilder();
		assertNotDischarged(psStatus);
	}

	/**
	 * Ensures that POM correctly traps reasoner application problems caused by
	 * an adversarial plug-in.
	 */
	@Test
	public final void testApplicationProblem() throws CoreException {
		AdversarialReasoner.erroneousApply = true;
		runBuilder();
		assertNotDischarged(psStatus);
	}

	/**
	 * Ensures that POM correctly traps deserialization problems caused by an
	 * adversarial plug-in.
	 */
	@Test
	public final void testDeserializationProblem() throws CoreException {
		AdversarialReasoner.erroneousDeserializeInput = true;
		runBuilder();
		assertDischarged(psStatus);
		assertEmptyProof();
	}

	/**
	 * Ensures that POM correctly traps reparation of input reasoner problems
	 * caused by an adversarial plug-in.
	 */
	@Test
	public final void testRepairInputProblem() throws CoreException {
		AdversarialReasoner.erroneousRepairInput = true;
		runBuilder();
		assertDischarged(psStatus);
		assertEmptyProof();
	}

	/*
	 * Checks that there is apparently no serialized proof. This might be caused
	 * by the fact the proof cannot be deserialized.
	 */
	private void assertEmptyProof() throws CoreException {
		final String poName = psStatus.getElementName();
		final IPRProof proof = poRoot.getPRRoot().getProof(poName);
		final FormulaFactory fac = proof.getFormulaFactory(null);
		final IProofSkeleton skel = proof.getSkeleton(fac, null);
		assertNull(skel.getRule());
		assertEquals(0, skel.getChildNodes().length);
	}

}
