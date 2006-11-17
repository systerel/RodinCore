package org.eventb.core.tests.pom;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.proofBuilder.IProofSkeleton;
import org.eventb.core.seqprover.proofBuilder.ProofBuilder;
import org.eventb.core.tests.BuilderTest;
import org.eventb.internal.core.pom.AutoPOM;
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
		IPSFile psFile = poFile.getPSFile();
		IPRFile prFile = poFile.getPRFile();
		
		AutoProver.enable();
		runBuilder();
		
		// Checks that status in PS file corresponds POs in PO file.
		checkPOsConsistent(poFile, psFile);

		
		// Checks that status in PS file corresponds Proofs in PR file.
		checkProofsConsistent(prFile, psFile);
		
		// Checks that all POs are discharged except the last one.
		IPSStatus[] prs = (IPSStatus[]) psFile.getStatuses();
		for (int i = 0; i < prs.length - 1; i++) {
			IPSStatus prSequent = prs[i];
			assertDischarged(prSequent);
		}
		assertNotDischarged(prs[prs.length-1]);
		
		// Try an interactive proof on the last one
		IProverSequent seq = POLoader.readPO(prs[prs.length-1].getPOSequent());
		IProofTree proofTree = ProverFactory.makeProofTree(seq, null);
		
		Tactics.lasoo().apply(proofTree.getRoot(), null);
		Tactics.lemma("∀x· x∈ℤ ⇒ x=x").apply(proofTree.getRoot().getFirstOpenDescendant(), null);
		Tactics.norm().apply(proofTree.getRoot(), null);
		// System.out.println(proofTree.getRoot());
		prs[prs.length-1].getProof().setProofTree(proofTree, null);
		AutoPOM.updateStatus(prs[prs.length-1],null);
		
		IProofSkeleton skel = prs[prs.length-1].getProof().getSkeleton(FormulaFactory.getDefault(), null);
		IProofTree loadedProofTree = ProverFactory.makeProofTree(seq, null);
		ProofBuilder.rebuild(loadedProofTree.getRoot(),skel);
		
		// System.out.println(loadedProofTree.getRoot());
		assertTrue(ProverLib.deepEquals(proofTree,loadedProofTree));
		loadedProofTree.getRoot().pruneChildren();
		assertFalse(ProverLib.deepEquals(proofTree,loadedProofTree));
		assertTrue(ProverLib.deepEquals(
				ProverFactory.makeProofTree(seq, null),
				loadedProofTree)
		);
	}
	

	private void checkProofsConsistent(IPRFile prFile, IPSFile psFile) throws RodinDBException {
		IPSStatus[] statuses = psFile.getStatuses();
		for (IPSStatus status : statuses) {
			if (status.getProofConfidence(null) > IConfidence.UNATTEMPTED)
			{
				IPRProof prProofTree = status.getProof();
				String name = status.getElementName();
				assertNotNull("Proof absent for "+name , prProofTree);
				assertEquals("Proof confidence different for "+name, prProofTree.getConfidence(null), status.getProofConfidence(null));
			}
		}
		
	}

	private void checkPOsConsistent(
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
				if (prChild instanceof IPRProof) {
					++ prIdx;
				} else {
					checkPOsConsistent(poChild, prChild);
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

	private void assertDischarged(IPSStatus status) throws RodinDBException {
		// IPRProofTree proofTree = status.getProofTree();
		assertTrue("PO " + status.getElementName() + " should be closed",
				IConfidence.PENDING <
				status.getProofConfidence(null));
		assertTrue("PR " + status.getElementName() + " should be valid",
				status.getProofValidAttribute(null));
		assertTrue("PR " + status.getElementName() + " should be attempted by the auto prover",
				status.hasAutoProofAttribute(null));
		assertTrue("PR " + status.getElementName() + " should be auto proven",
				status.getAutoProofAttribute(null));
		
	}
	
	private void assertNotDischarged(IPSStatus status) throws RodinDBException {
		// IPRProofTree proofTree = status.getProofTree();
		assertTrue("PO " + status.getElementName() + " should not be closed",
				IConfidence.PENDING >=
				status.getProofConfidence(null));
		assertTrue("PR " + status.getElementName() + " should be valid",
				status.getProofValidAttribute(null));
		assertTrue("PR " + status.getElementName() + " should be attempted by the auto prover",
				status.hasAutoProofAttribute(null));
//		assertFalse("PR " + status.getName() + " should not be auto proven",
//				status.isAutoProven());
	}
	
	public static String[] mp(String... strings) {
		return strings;
	}
	
	public static String[] mh(String... strings) {
		return strings;
	}

}
