package org.eventb.core.testpom;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRProofTree;
import org.eventb.core.IPRSequent;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.proofBuilder.IProofSkeleton;
import org.eventb.core.seqprover.proofBuilder.ProofBuilder;
import org.eventb.core.seqprover.tactics.Tactics;
import org.eventb.core.testscpog.BuilderTest;
import org.eventb.internal.core.pom.AutoProver;
import org.eventb.internal.core.pom.POLoader;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class AutoPOMTest extends BuilderTest {

	private IPOFile createPOFile() throws RodinDBException {
		IPOFile poFile = (IPOFile) rodinProject.createRodinFile("x.bpo", true, null);
		POUtil.addTypes(poFile, mp("x"), mp("ℤ"));
		POUtil.addPredicateSet(poFile, "hyp0", mp("1=1","2=2","x∈ℕ"), null);
		POUtil.addSequent(poFile, "PO1", 
				"hyp0", 
				mp(), mp(), 
				mh(), 
				"1=1 ∧2=2 ∧x ∈ℕ");
		POUtil.addSequent(poFile, "PO2", 
				"hyp0", 
				mp("y"), mp("ℤ"), 
				mh("y∈ℕ"), 
				"1=1 ∧2=2 ∧x ∈ℕ∧y ∈ℕ");
		POUtil.addSequent(poFile, "PO3", 
				"hyp0", 
				mp(), mp(), 
				mh("3=3"), 
				"∃x·x=3");
		POUtil.addSequent(poFile, "PO4", 
				"hyp0", 
				mp(), mp(), 
				mh("3=3"), 
				"1=1 ∧2=2 ∧x ∈ℕ∧(∃x·(x=3))");
		POUtil.addSequent(poFile, "PO5", 
				"hyp0", 
				mp("y"), mp("ℤ"), 
				mh("y∈ℕ"), 
				"1=1 ∧2=2 ∧y ∈ℕ∧y ∈ℕ");
		POUtil.addSequent(poFile, "PO6", 
				"hyp0", 
				mp("y","x'"), mp("ℤ","ℤ"), 
				mh("y∈ℕ"), 
				"1=1 ∧2=2 ∧x ∈ℕ∧y ∈ℕ");
		POUtil.addSequent(poFile, "PO7", 
				"hyp0", 
				mp("y"), mp("ℤ"), 
				mh("x=x"), 
				"y∈ℕ");
		poFile.save(null, true);
		return poFile;
	}

	/*
	 * Test method for 'org.eventb.internal.core.pom.AutoPOM.AutoPOM(IPOFile, IInterrupt, IProgressMonitor)'
	 */
	public final void testAutoPOM() throws CoreException {
		
		IPOFile poFile = createPOFile();
		IPRFile prFile = poFile.getPRFile();

		AutoProver.enable();
		runBuilder();
		
		// Checks that POs in PR files are the same as POs in PO file.
		checkSameContents(poFile, prFile);
		
		// Checks that all POs are discharged except the last one.
		IPRSequent[] prs = (IPRSequent[]) prFile.getSequents();
		for (int i = 0; i < prs.length - 1; i++) {
			IPRSequent prSequent = prs[i];
			assertDischarged(prSequent);
		}
		assertNotDischarged(prs[prs.length-1]);
		
		// Try an interactive proof on the last one
		IProverSequent seq = POLoader.readPO(prs[prs.length-1].getPOSequent());
		IProofTree proofTree = ProverFactory.makeProofTree(seq);
		
		Tactics.lasoo().apply(proofTree.getRoot(), null);
		Tactics.lemma("∀x· x∈ℤ ⇒ x=x").apply(proofTree.getRoot().getFirstOpenDescendant(), null);
		Tactics.norm().apply(proofTree.getRoot(), null);
		// System.out.println(proofTree.getRoot());
		prs[prs.length-1].getProofTree().setProofTree(proofTree);
		prs[prs.length-1].updateStatus();
		
		IProofSkeleton skel = prs[prs.length-1].getProofTree().getRoot().getSkeleton(null);
		IProofTree loadedProofTree = ProverFactory.makeProofTree(seq);
		ProofBuilder.rebuild(loadedProofTree.getRoot(),skel);
		
		// System.out.println(loadedProofTree.getRoot());
		assertTrue(ProverLib.deepEquals(proofTree,loadedProofTree));
		loadedProofTree.getRoot().pruneChildren();
		assertFalse(ProverLib.deepEquals(proofTree,loadedProofTree));
		assertTrue(ProverLib.deepEquals(ProverFactory.makeProofTree(seq),loadedProofTree));
	}
	

	private void checkSameContents(
			IInternalParent poElement,
			IInternalParent prElement) throws RodinDBException {
		
		if (poElement instanceof IPOFile) {
			// No comparison to do
		} else if (poElement instanceof IPOSequent) {
			assertEquals("PO names differ",
					poElement.getElementName(), prElement.getElementName());
		} else {
			assertEquals("element names differ",
					poElement.getElementName(), prElement.getElementName());
			assertEquals("element names differ",
					poElement.getElementType(), prElement.getElementType());
		}
		
		final IRodinElement[] poChildren = poElement.getChildren();
		final IRodinElement[] prChildren = prElement.getChildren();
		if (poElement instanceof IPOSequent) {
			int poIdx = 0;
			int prIdx = 0;
			while (poIdx < poChildren.length && prIdx < prChildren.length) {
				final IInternalParent poChild = (IInternalParent) poChildren[poIdx];
				final IInternalParent prChild = (IInternalParent) prChildren[prIdx];
				if (prChild instanceof IPRProofTree) {
					++ prIdx;
				} else {
					checkSameContents(poChild, prChild);
					++ poIdx;
					++ prIdx;
				}
			}
			assertEquals("Not all PO elements were copied",
					poChildren.length, poIdx);
			assertEquals("Too many PR elements",
					prChildren.length, prIdx);
		}
	}

	private void assertDischarged(IPRSequent prSequent) throws RodinDBException {
		IPRProofTree proofTree = prSequent.getProofTree();
		assertTrue("PO " + prSequent.getName() + " should be closed",
				IConfidence.PENDING !=
				proofTree.getConfidence());
	}

	private void assertNotDischarged(IPRSequent prSequent) throws RodinDBException {
		IPRProofTree proofTree = prSequent.getProofTree();
		assertEquals("PO " + prSequent.getName() + " should not be discharged",
				IConfidence.PENDING,
				proofTree.getConfidence());
	}
	
	public static String[] mp(String... strings) {
		return strings;
	}
	
	public static String[] mh(String... strings) {
		return strings;
	}

}
