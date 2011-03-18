/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - added used reasoners to proof dependencies
 *******************************************************************************/
package org.eventb.core.tests.pom;

import static org.eventb.core.EventBAttributes.HYPS_ATTRIBUTE;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.impI;
import static org.rodinp.core.IRodinDBStatusConstants.ATTRIBUTE_DOES_NOT_EXIST;

import java.util.Collections;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRProofRule;
import org.eventb.core.IPRRoot;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
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
import org.eventb.core.tests.ResourceUtils;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Class containing unit tests to test the proper serialization and deserialization of proof trees.
 * 
 * @author Farhad Mehta
 *
 */
public class ProofSerializationTests extends TestCase {
	
	private static FormulaFactory ff = FormulaFactory.getDefault();

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
		IProofSkeleton skel = proof.getSkeleton(ff, null);
		assertTrue(ProverLib.deepEquals(proofTree.getRoot(), skel));
		
		assertEquals(hasDeps, proof.getProofDependencies(ff, null).hasDeps());
	}

	private IWorkspace workspace = ResourcesPlugin.getWorkspace();
	private IRodinProject rodinProject;
	private IPRRoot prRoot;
	
	private Predicate getFirstUnivHyp(IProverSequent seq) {
		for (Predicate pred: seq.selectedHypIterable()) {
			if (pred.getTag() == Formula.FORALL) {
				return pred;
			}
		}
		return null;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		// ensure auto-building is turned off
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
		
		// Create a new proof file
		IRodinFile prFile = rodinProject.getRodinFile("x.bpr");
		prFile.create(true, null);
		prRoot = (IPRRoot) prFile.getRoot();
		assertTrue(prRoot.exists());
		assertEquals(0, prRoot.getProofs().length);
	}

	@Override
	protected void tearDown() throws Exception {
		rodinProject.getProject().delete(true, true, null);
		super.tearDown();
	}

	public final void test() throws RodinDBException{
		IPRProof proof1 = prRoot.getProof("proof1");
		proof1.create(null, null);

		assertEquals(1, prRoot.getProofs().length);
		assertTrue(proof1.exists());
		assertEquals(proof1, prRoot.getProof("proof1"));
		assertEquals(IConfidence.UNATTEMPTED, proof1.getConfidence());
		assertFalse(proof1.getProofDependencies(ff, null).hasDeps());
		
		// Test 1
		
		IProverSequent sequent = TestLib.genSeq("|- ⊤ ⇒ ⊤");
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
		
		sequent = TestLib.genSeq("⊤ |- ⊤ ∧ ⊤");
		proofTree = ProverFactory.makeProofTree(sequent, null);
		(new AutoTactics.ClarifyGoalTac()).apply(proofTree.getRoot(), null);
		// The next check is to see if the prover is behaving itself.
		assertTrue(proofTree.isClosed());
		checkProofTreeSerialization(proof1, proofTree, true);
		
		// Test 3
		
		sequent = TestLib.genSeq("⊤ |- 0 ∈ ℕ ∧ 0 ∈ ℤ");
		proofTree = ProverFactory.makeProofTree(sequent, null);
		checkProofTreeSerialization(proof1, proofTree, false);
		
		(new AutoTactics.ClarifyGoalTac()).apply(proofTree.getRoot(), null);
		checkProofTreeSerialization(proof1, proofTree, true);
		
		// Test 4 ; a proof tree with no goal dependencies, open
		sequent = TestLib.genSeq("⊥ |- ⊥");
		proofTree = ProverFactory.makeProofTree(sequent, null);
		checkProofTreeSerialization(proof1, proofTree, false);
		
		// an identity rule that doesn't do anything.
		// since a reasoner is used, it does have dependencies
		Set<Predicate> noHyps = Collections.emptySet();
		Tactics.mngHyp(ProverFactory.makeHideHypAction(noHyps)).apply(proofTree.getRoot(), null);
		checkProofTreeSerialization(proof1, proofTree, true);
		
		
		// Test 4 ; a proof tree with no goal dependencies, closed
		sequent = TestLib.genSeq("⊥ |- ⊥");
		proofTree = ProverFactory.makeProofTree(sequent, null);
		checkProofTreeSerialization(proof1, proofTree, false);
		(new AutoTactics.FalseHypTac()).apply(proofTree.getRoot(), null);
		checkProofTreeSerialization(proof1, proofTree, true);
	}
	
	/**
	 * Ensures that a proof tree containing a partial instantiation can be
	 * serialized and deserialized.
	 */
	public final void testPartialInstantiation() throws RodinDBException {
		final IPRProof proof = prRoot.getProof("P2");
		proof.create(null, null);

		final IProverSequent seq = TestLib.genSeq(
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

	public void testReasonerVersionCurrent() throws Exception {
		IPRProof proof1 = prRoot.getProof("proof1");
		proof1.create(null, null);

		IProverSequent sequent = TestLib.genSeq("|- ⊤ ⇒ ⊤");
		IProofTree proofTree = ProverFactory.makeProofTree(sequent, null);
		
		checkProofTreeSerialization(proof1, proofTree, false);
		
		BasicTactics.reasonerTac(new ReasonerV2(), new EmptyInput())
				.apply(proofTree.getRoot(), null);

		checkProofTreeSerialization(proof1, proofTree, true);

	}

	public void testReasonerVersionOld() throws Exception {
		IPRProof proof1 = prRoot.getProof("proof1");
		proof1.create(null, null);

		IProverSequent sequent = TestLib.genSeq("|- ⊤ ⇒ ⊤");
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

	public void testErroneousProof() throws Exception {
		final IPRProof proof = prRoot.getProof("proof1");
		proof.create(null, null);

		final IProverSequent sequent = TestLib.genSeq("|- ⊤ ⇒ ⊤");
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
			proof.getSkeleton(ff, null);
			fail("Should have raised an exception");
		} catch (RodinDBException e) {
			final IRodinDBStatus status = e.getRodinDBStatus();
			assertEquals(ATTRIBUTE_DOES_NOT_EXIST, status.getCode());
		}
	}

	// before 2.2, versioned reasoner ids were stored in rule element names
	// from 2.2 on, rule element name bears a reference to a IPRReasoner
	// located in proof root
	// ensure that old storage is still readable
	public void testReasonerStorageCompatibility() throws Exception {
		final String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
				+ "<org.eventb.core.prFile version=\"1\">"
				+ "<org.eventb.core.prProof name=\"oldProof\" org.eventb.core.confidence=\"1000\" org.eventb.core.prFresh=\"\" org.eventb.core.prGoal=\"p0\" org.eventb.core.prHyps=\"\">"
				+ "<org.eventb.core.prRule name=\"org.eventb.core.seqprover.impI\" org.eventb.core.confidence=\"1000\" org.eventb.core.prDisplay=\"⇒ goal\" org.eventb.core.prGoal=\"p0\" org.eventb.core.prHyps=\"\">"
				+ "<org.eventb.core.prAnte name=\"0\" org.eventb.core.prGoal=\"p1\" org.eventb.core.prHyps=\"p1\">"
				+ "<org.eventb.core.prRule name=\"org.eventb.core.seqprover.trueGoal\" org.eventb.core.confidence=\"1000\" org.eventb.core.prDisplay=\"⊤ goal\" org.eventb.core.prGoal=\"p1\" org.eventb.core.prHyps=\"\"/>"
				+ "</org.eventb.core.prAnte>"
				+ "</org.eventb.core.prRule>"
				+ "<org.eventb.core.prPred name=\"p1\" org.eventb.core.predicate=\"⊤\"/>"
				+ "<org.eventb.core.prPred name=\"p0\" org.eventb.core.predicate=\"⊤⇒⊤\"/>"
				+ "</org.eventb.core.prProof>"
				+ "</org.eventb.core.prFile>";
		final IPRRoot prFile = ResourceUtils.createPRFile(rodinProject, "oldProofFile", contents);
		final IPRProof proof = prFile.getProof("oldProof");

		final IProverSequent sequent = TestLib.genSeq("|- ⊤ ⇒ ⊤");
		final IProofTree proofTree = ProverFactory.makeProofTree(sequent, null);
		final IProofTreeNode root = proofTree.getRoot();
		impI().apply(root, null);
		new TrueGoalTac().apply(root.getFirstOpenDescendant(), null);
		assertTrue(proofTree.isClosed());

		checkDeserialization(proof, proofTree, true);
	}
}
