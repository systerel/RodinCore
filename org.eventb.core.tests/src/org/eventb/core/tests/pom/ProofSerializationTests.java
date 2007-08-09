package org.eventb.core.tests.pom;

import java.util.Collections;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRProof;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
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
		IProofSkeleton skel = proof.getSkeleton(ff, null);
		assertTrue(ProverLib.deepEquals(proofTree.getRoot(), skel));
		
		assertEquals(hasDeps, proof.getProofDependencies(ff, null).hasDeps());
	}

	private IWorkspace workspace = ResourcesPlugin.getWorkspace();
	private IRodinProject rodinProject;
	private IPRFile prFile;
	
	private Predicate getFirstUnivHyp(IProverSequent seq) {
		for (Predicate pred: seq.selectedHypIterable()) {
			if (pred.getTag() == Formula.FORALL) {
				return pred;
			}
		}
		return null;
	}

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
		prFile = (IPRFile) rodinProject.getRodinFile("x.bpr");
		prFile.create(true, null);
		
		assertTrue(prFile.exists());
		assertEquals(0, prFile.getProofs().length);
	}

	protected void tearDown() throws Exception {
		rodinProject.getProject().delete(true, true, null);
		super.tearDown();
	}

	public final void test() throws RodinDBException{
		IPRProof proof1 = prFile.getProof("proof1");
		proof1.create(null, null);

		assertEquals(1, prFile.getProofs().length);
		assertTrue(proof1.exists());
		assertEquals(proof1, prFile.getProof("proof1"));
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
		Set<Predicate> noHyps = Collections.emptySet();
		Tactics.mngHyp(ProverFactory.makeHideHypAction(noHyps)).apply(proofTree.getRoot(), null);
		checkProofTreeSerialization(proof1, proofTree, false);
		
		
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
		final IPRProof proof = prFile.getProof("P2");
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

}
