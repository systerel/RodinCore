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
 *     Systerel - added used reasoners to proof dependencies
 *     Systerel - externalized xml proof files
 *******************************************************************************/
package org.eventb.core.tests.pom;

import static org.eventb.core.EventBAttributes.HYPS_ATTRIBUTE;
import static org.eventb.core.seqprover.ProverFactory.makeProofTree;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.impI;
import static org.eventb.core.seqprover.tactics.BasicTactics.replayTac;
import static org.eventb.core.tests.ResourceUtils.importProjectFiles;
import static org.eventb.core.tests.extension.PrimeFormulaExtensionProvider.EXT_FACTORY;
import static org.eventb.core.tests.pom.TestLib.genSeq;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.rodinp.core.IRodinDBStatusConstants.ATTRIBUTE_DOES_NOT_EXIST;

import java.util.Collections;
import java.util.Set;

import org.eventb.core.IPRProof;
import org.eventb.core.IPRProofRule;
import org.eventb.core.IPRRoot;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerDesc;
import org.eventb.core.seqprover.IReasonerRegistry;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics.TrueGoalTac;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.core.tests.BuilderTest;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Class containing unit tests to test the proper serialization and deserialization of proof trees.
 * 
 * @author Farhad Mehta
 *
 */
public class ProofSerializationTests extends BuilderTest {
	
	private static void checkProofTreeSerialization(IPRProof proof,
			IProofTree proofTree, boolean hasDeps) throws RodinDBException {

		// Store the proof tree
		proof.setProofTree(proofTree, null);
		assertEquals(proofTree.getConfidence(), proof.getConfidence());

		// Check that the stored tree is the same
		checkDeserialization(proof, proofTree, hasDeps);
	}

	private static void checkDeserialization(IPRProof proof,
			IProofTree proofTree, boolean hasDeps) throws RodinDBException {
		final FormulaFactory fac = proof.getFormulaFactory(null);
		IProofSkeleton skel = proof.getSkeleton(fac, null);
		assertTrue(ProverLib.deepEquals(proofTree.getRoot(), skel));
		
		assertEquals(hasDeps, proof.getProofDependencies(fac, null).hasDeps());
	}

	private static ITactic autoRewriteL2() {
		final IReasonerRegistry registry = SequentProver.getReasonerRegistry();
		final IReasonerDesc desc = registry
				.getReasonerDesc("org.eventb.core.seqprover.autoRewritesL2:1");
		return BasicTactics.reasonerTac(desc.getInstance(), new EmptyInput());
	}

	private void importProofSerializationProofs() throws Exception {
		importProjectFiles(rodinProject.getProject(), "ProofSerialization");
	}

	private IPRRoot prRoot;
	
	private Predicate getFirstUnivHyp(IProverSequent seq) {
		for (Predicate pred: seq.selectedHypIterable()) {
			if (pred.getTag() == Formula.FORALL) {
				return pred;
			}
		}
		return null;
	}

	@Before
	public void createProofFile() throws Exception {
		final IRodinFile prFile = rodinProject.getRodinFile("x.bpr");
		prFile.create(true, null);
		prRoot = (IPRRoot) prFile.getRoot();
		assertTrue(prRoot.exists());
		assertEquals(0, prRoot.getProofs().length);
	}

	@Test
	public final void test() throws RodinDBException{
		IPRProof proof1 = prRoot.getProof("proof1");
		proof1.create(null, null);

		assertEquals(1, prRoot.getProofs().length);
		assertTrue(proof1.exists());
		assertEquals(proof1, prRoot.getProof("proof1"));
		assertEquals(IConfidence.UNATTEMPTED, proof1.getConfidence());
		assertFalse(proof1.getProofDependencies(factory, null).hasDeps());
		
		// Test 1
		
		IProverSequent sequent = TestLib.genSeq(factory, "|- ⊤ ⇒ ⊤");
		IProofTree proofTree = ProverFactory.makeProofTree(sequent, null);
		checkProofTreeSerialization(proof1, proofTree, false);
		
		(new AutoTactics.ImpGoalTac()).apply(proofTree.getRoot(), null);
		checkProofTreeSerialization(proof1, proofTree, true);
		
		(new AutoTactics.TrueGoalTac()).apply(proofTree.getRoot().getFirstOpenDescendant(),null);
		// The next check is to see if the prover is behaving itself.
		assertTrue(proofTree.isClosed());
		assertEquals(IConfidence.DISCHARGED_MAX, proofTree.getConfidence());
		checkProofTreeSerialization(proof1, proofTree, true);
		
		// Test 2
		
		sequent = TestLib.genSeq(factory, "⊤ |- ⊤ ∧ ⊤");
		proofTree = ProverFactory.makeProofTree(sequent, null);
		(new AutoTactics.ClarifyGoalTac()).apply(proofTree.getRoot(), null);
		// The next check is to see if the prover is behaving itself.
		assertTrue(proofTree.isClosed());
		checkProofTreeSerialization(proof1, proofTree, true);
		
		// Test 3
		
		sequent = TestLib.genSeq(factory, "⊤ |- 0 ∈ ℕ ∧ 0 ∈ ℤ");
		proofTree = ProverFactory.makeProofTree(sequent, null);
		checkProofTreeSerialization(proof1, proofTree, false);
		
		(new AutoTactics.ClarifyGoalTac()).apply(proofTree.getRoot(), null);
		checkProofTreeSerialization(proof1, proofTree, true);
		
		// Test 4 ; a proof tree with no goal dependencies, open
		sequent = TestLib.genSeq(factory, "⊥ |- ⊥");
		proofTree = ProverFactory.makeProofTree(sequent, null);
		checkProofTreeSerialization(proof1, proofTree, false);
		
		// an identity rule that doesn't do anything.
		// since a reasoner is used, it does have dependencies
		Set<Predicate> noHyps = Collections.emptySet();
		Tactics.mngHyp(ProverFactory.makeHideHypAction(noHyps)).apply(proofTree.getRoot(), null);
		checkProofTreeSerialization(proof1, proofTree, true);
		
		
		// Test 4 ; a proof tree with no goal dependencies, closed
		sequent = TestLib.genSeq(factory, "⊥ |- ⊥");
		proofTree = ProverFactory.makeProofTree(sequent, null);
		checkProofTreeSerialization(proof1, proofTree, false);
		(new AutoTactics.FalseHypTac()).apply(proofTree.getRoot(), null);
		checkProofTreeSerialization(proof1, proofTree, true);
	}
	
	/**
	 * Ensures that a proof tree containing a partial instantiation can be
	 * serialized and deserialized.
	 */
	@Test
	public final void testPartialInstantiation() throws RodinDBException {
		final IPRProof proof = prRoot.createChild(IPRProof.ELEMENT_TYPE, null,
				null);

		final IProverSequent seq = TestLib.genSeq(factory,
				"   x ∈ ℕ" +
				";; y ∈ ℕ" +
				";; (∀a,b· a ∈ ℕ ∧ b ∈ ℕ ⇒ a+b ∈ ℕ)" +
				"|- x+y ∈ ℕ");
		final IProofTree proofTree = ProverFactory.makeProofTree(seq, null);
		final Predicate univ = getFirstUnivHyp(seq);
		ITactic tactic;

		// Apply AllD with a full instantiation
		tactic = Tactics.allD(univ, "x", "y");
		tactic.apply(proofTree.getRoot(), null);
		checkProofTreeSerialization(proof, proofTree, true);
		proofTree.getRoot().pruneChildren();

		// Apply AllD with a partial instantiation (only "a")
		tactic = Tactics.allD(univ, "x", null);
		tactic.apply(proofTree.getRoot(), null);
		checkProofTreeSerialization(proof, proofTree, true);
		proofTree.getRoot().pruneChildren();

		// Apply AllD with a partial instantiation (only "b")
		tactic = Tactics.allD(univ, null, "y");
		tactic.apply(proofTree.getRoot(), null);
		checkProofTreeSerialization(proof, proofTree, true);
		proofTree.getRoot().pruneChildren();
	}

	@Test
	public void testReasonerVersionCurrent() throws Exception {
		IPRProof proof1 = prRoot.createChild(IPRProof.ELEMENT_TYPE, null, null);

		IProverSequent sequent = TestLib.genSeq(factory, "|- ⊤ ⇒ ⊤");
		IProofTree proofTree = ProverFactory.makeProofTree(sequent, null);
		
		checkProofTreeSerialization(proof1, proofTree, false);
		
		BasicTactics.reasonerTac(new ReasonerV2(), new EmptyInput())
				.apply(proofTree.getRoot(), null);

		checkProofTreeSerialization(proof1, proofTree, true);

	}

	@Test
	public void testReasonerVersionOld() throws Exception {
		IPRProof proof1 = prRoot.createChild(IPRProof.ELEMENT_TYPE, null, null);

		IProverSequent sequent = TestLib.genSeq(factory, "|- ⊤ ⇒ ⊤");
		IProofTree proofTree = ProverFactory.makeProofTree(sequent, null);

		final IReasonerRegistry registry = SequentProver.getReasonerRegistry();
		final IReasonerDesc desc = registry
				.getReasonerDesc(new ReasonerV2().getReasonerID() + ":1");
		final Set<Predicate> noHyps = Collections.emptySet();
		final IProofRule rule = ProverFactory.makeProofRule(desc,
				new EmptyInput(), sequent.goal(), noHyps,
				IConfidence.DISCHARGED_MAX, desc.getName());
		final boolean success = proofTree.getRoot().applyRule(rule);
		assertTrue(success);
		assertTrue(proofTree.isClosed());

		checkProofTreeSerialization(proof1, proofTree, true);
	}

	@Test
	public void testErroneousProof() throws Exception {
		final IPRProof proof = prRoot.createChild(IPRProof.ELEMENT_TYPE, null, null);

		final IProverSequent sequent = TestLib.genSeq(factory, "|- ⊤ ⇒ ⊤");
		final IProofTree proofTree = ProverFactory.makeProofTree(sequent, null);
		final IProofTreeNode root = proofTree.getRoot();
		impI().apply(root, null);
		new TrueGoalTac().apply(root.getFirstOpenDescendant(), null);
		assertTrue(proofTree.isClosed());
		proof.setProofTree(proofTree, null);
		assertEquals(proofTree.getConfidence(), proof.getConfidence());

		// Fiddle with the serialized proof to break it
		final IPRProofRule rule = proof.getProofRules()[0];
		rule.removeAttribute(HYPS_ATTRIBUTE, null);

		try {
			proof.getSkeleton(factory, null);
			fail("Should have raised an exception");
		} catch (RodinDBException e) {
			final IRodinDBStatus status = e.getRodinDBStatus();
			assertEquals(ATTRIBUTE_DOES_NOT_EXIST, status.getCode());
		}
	}

	private IPRRoot getProofRoot(String proofFileName) throws Exception {
		final IPRRoot[] proofRoots = rodinProject.getRootElementsOfType(IPRRoot.ELEMENT_TYPE);
		for (IPRRoot proofRoot : proofRoots) {
			final String elementName = proofRoot.getElementName();
			if (elementName.equals(proofFileName)) {
				return proofRoot;
			}
		}
		fail("could not find proof " + proofFileName);
		return null;
	}

	// before 2.2, versioned reasoner ids were stored in rule element names
	// from 2.2 on, rule element name bears a reference to a IPRReasoner
	// located in proof root
	// ensure that old storage is still readable
	@Test
	public void testReasonerStorageCompatibility() throws Exception {
		importProofSerializationProofs();
		
		final IPRRoot prFile = getProofRoot("reasonerStorage");
		final IPRProof proof = prFile.getProof("oldProof");

		final IProverSequent sequent = TestLib.genSeq(factory, "|- ⊤ ⇒ ⊤");
		final IProofTree proofTree = ProverFactory.makeProofTree(sequent, null);
		final IProofTreeNode root = proofTree.getRoot();
		impI().apply(root, null);
		new TrueGoalTac().apply(root.getFirstOpenDescendant(), null);
		assertTrue(proofTree.isClosed());

		checkDeserialization(proof, proofTree, true);
	}
	
	// check repaired input correctly applies  
	private static void checkReplay(final IProverSequent sequent,
			final IPRProof proof) throws RodinDBException {
		final IProofSkeleton oldSkel = proof.getSkeleton(factory, null);
		final IProofTree replayTree = ProverFactory.makeProofTree(
				sequent, null);
		final IProofTreeNode oldRoot = replayTree.getRoot();
		final Object result = replayTac(oldSkel).apply(oldRoot, null);
		assertEquals(null, result);
		assertTrue(replayTree.isClosed());
	}

	// reasoner doubleImplGoalRewrites used not to be registered
	// as a consequence its input was not serialized
	// verify ability to repair and replay with broken inputs
	@Test
	public void testContrapInHyp_Bug3370087() throws Exception {
		importProofSerializationProofs();

		// input for doubleImplGoalRewrites is missing
		final IPRRoot prFile = getProofRoot("contrapInHyp");
		final IPRProof proof = prFile.getProof("oldContrapHyp");

		final IProverSequent sequent = TestLib.genSeq(factory, "0=0⇒⊥ |- 0≠0");
		Predicate hyp = TestLib.genPred(factory, "0=0⇒⊥");
		final IProofTree proofTree = ProverFactory.makeProofTree(sequent, null);
		final IProofTreeNode root = proofTree.getRoot();

		Tactics.contImpHyp(hyp, IPosition.ROOT).apply(root, null);
		autoRewriteL2().apply(root.getFirstOpenDescendant(), null);
		new AutoTactics.FalseHypTac()
				.apply(root.getFirstOpenDescendant(), null);
		assertTrue(proofTree.isClosed());

		checkDeserialization(proof, proofTree, true);

		checkReplay(proofTree.getSequent(), proof);
	}

	// same as above in a more complex predicate
	@Test
	public void testContrapInHyp2_Bug3370087() throws Exception {
		importProofSerializationProofs();

		// input for doubleImplGoalRewrites is missing
		final IPRRoot prFile = getProofRoot("contrapInHyp2");
		final IPRProof proof = prFile.getProof("cplx");

		final IProverSequent sequent = TestLib.genSeq(factory,
				"s≠(∅ ⦂ ℙ(S))⇒(∀x⦂ℙ(S)·s⊆x⇒(x⊆t⇒t=s)) ;; " +
				"s∈ℙ(S)∧s⊆t ;; " +
				"s≠(∅ ⦂ ℙ(S))" +
				" |- t=s");

		checkReplay(sequent, proof);
	}

	@Test
	public void testAbstrExpr_Bug3370087() throws Exception {
		importProofSerializationProofs();

		// input for ae is missing
		final IPRRoot prFile = getProofRoot("ae");
		final IPRProof proof = prFile.getProof("oldAE");

		final IProverSequent sequent = TestLib.genSeq(factory, "|- 0≥0");
		final IProofTree expected = ProverFactory.makeProofTree(sequent, null);
		final IProofTreeNode expectedRoot = expected.getRoot();

		Tactics.abstrExprThenEq("0").apply(expectedRoot, null);
		new TrueGoalTac().apply(expectedRoot.getFirstOpenDescendant(), null);
		new AutoTactics.FalseHypTac().apply(
				expectedRoot.getFirstOpenDescendant(), null);
		autoRewriteL2().apply(expectedRoot.getFirstOpenDescendant(), null);
		new TrueGoalTac().apply(expectedRoot.getFirstOpenDescendant(), null);
		assertTrue(expected.isClosed());

		checkDeserialization(proof, expected, true);

		checkReplay(expected.getSequent(), proof);
	}
	
	@Test
	public void testAbstrExpr_WD_Bug3370087() throws Exception {
		importProofSerializationProofs();

		// input for ae is missing and input has non trivial WD
		final IPRRoot prFile = getProofRoot("ae_WD");
		final IPRProof proof = prFile.getProof("ae_with_wd");

		final IProverSequent sequent = TestLib.genSeq(factory,
				"s∈ℙ(BOOL) ;; finite(s) |- card(s)≥0");

		checkReplay(sequent, proof);
	}
	
	/**
	 * Ensures that a closed proof tree using a specialized factory is correctly
	 * serialized and deserialized.
	 */
	@Test
	public void specializedFactoryClosedProof() throws Exception {
		final IPRProof proof = prRoot.getProof("proof");
		proof.create(null, null);
		final IProverSequent sequent = genSeq(EXT_FACTORY, "prime({2}) |- ⊤");
		final IProofTree proofTree = makeProofTree(sequent, null);
		new AutoTactics.TrueGoalTac().apply(proofTree.getRoot(), null);
		assertTrue(proofTree.isClosed());
		checkProofTreeSerialization(proof, proofTree, true);
	}

}
