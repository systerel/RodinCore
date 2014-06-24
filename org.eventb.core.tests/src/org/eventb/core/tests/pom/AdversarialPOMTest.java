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

import static org.eventb.core.tests.extension.PrimeFormulaExtensionProvider.EXT_FACTORY;
import static org.eventb.core.tests.pom.POUtil.addPredicateSet;
import static org.eventb.core.tests.pom.POUtil.addSequent;
import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.tests.extension.PrimeFormulaExtensionProvider;
import org.junit.Before;
import org.junit.Test;

/**
 * Ensures that POM behaves correctly, even in the presence of :
 * <ul>
 * <li>adversarial plug-ins
 * <li>adversarial formula extension providers
 * </ul>
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
		PrimeFormulaExtensionProvider.reset();
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

	/**
	 * Ensures that POM correctly traps formula factory storage problems caused
	 * by an adversarial formula extension provider.
	 */
	@Test
	public final void testSaveFFProblem() throws CoreException {
		PrimeFormulaExtensionProvider.erroneousSaveFormulaFactory = true;
		runBuilder();
		assertNotDischarged(psStatus);
	}

	/**
	 * Ensures that proofTree is re-generated when proof file language parse
	 * problems occurred (the broken factory and the factory of the component are
	 * compatible).
	 */
	@Test
	public final void testLoadFFProblem() throws CoreException {
		PrimeFormulaExtensionProvider.erroneousLoadFormulaFactory = true;
		runBuilder();
		final IPRProof proof = poRoot.getPRRoot().getProof(
				psStatus.getElementName());
		final IProofTree proofTree = proof.getProofTree(null);
		assertNotNull(proofTree);
		assertDischarged(psStatus);
	}

	/**
	 * Ensures that a CoreException is thrown when trying to load a proof tree
	 * that requires an extended formula factory, if there is a factory loading
	 * problem and the proof component gives a wrong factory (the default
	 * factory here).
	 */
	@Test
	public void testLoadExtendedFFProblem() throws Exception {
		PrimeFormulaExtensionProvider.add(poRoot);
		PrimeFormulaExtensionProvider.add(poRoot.getPSRoot());
		PrimeFormulaExtensionProvider.add(poRoot.getPRRoot());
		final ITypeEnvironment te = EXT_FACTORY.makeTypeEnvironment();
		addSequent(poRoot, "PO2", "⊤", null, te, "prime({2})");
		saveRodinFileOf(poRoot);
		runBuilder();

		final IPRProof proof = poRoot.getPRRoot().getProof("PO2");
		PrimeFormulaExtensionProvider.clear();
		PrimeFormulaExtensionProvider.erroneousLoadFormulaFactory = true;

		try {
			proof.getProofTree(null);
			fail("Expected a CoreException");
		} catch (CoreException e) {
			// as expected
		}
	}
}
