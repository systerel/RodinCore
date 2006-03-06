package org.eventb.core.testpom;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRSequent;
import org.eventb.core.IPROOF;
import org.eventb.core.testscpog.BuilderTest;
import org.eventb.internal.core.pom.AutoProver;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class AutoPOMTest extends BuilderTest {

	private IPOFile createPOFile() throws RodinDBException {
		IPOFile poFile = (IPOFile) rodinProject.createRodinFile("x.bpo", true, null);
		POUtil.addTypes(poFile, POUtil.mp("x"), POUtil.mp("ℤ"));
		POUtil.addPredicateSet(poFile, "hyp0", POUtil.mp("1=1","2=2","x∈ℕ"), null);
		POUtil.addSequent(poFile, "PO1", 
				"hyp0", 
				POUtil.mp(), POUtil.mp(), 
				POUtil.mh(
						POUtil.mp(), 
						POUtil.mp()), 
				POUtil.mp("1=1 ∧2=2 ∧x ∈ℕ"));
		POUtil.addSequent(poFile, "PO2", 
				"hyp0", 
				POUtil.mp("y"), POUtil.mp("ℤ"), 
				POUtil.mh(
						POUtil.mp("y∈ℕ"), 
						POUtil.mp()), 
				POUtil.mp("1=1 ∧2=2 ∧x ∈ℕ∧y ∈ℕ"));
		POUtil.addSequent(poFile, "PO3", 
				"hyp0", 
				POUtil.mp(), POUtil.mp(), 
				POUtil.mh(
						POUtil.mp("3=3"), 
						POUtil.mp()), 
				POUtil.mp("∃x·x=3"));
		POUtil.addSequent(poFile, "PO4", 
				"hyp0", 
				POUtil.mp(), POUtil.mp(), 
				POUtil.mh(
						POUtil.mp("3=3"), 
						POUtil.mp()), 
				POUtil.mp("1=1 ∧2=2 ∧x ∈ℕ∧(∃x·(x=3))"));
		POUtil.addSequent(poFile, "PO5", 
				"hyp0", 
				POUtil.mp("y"), POUtil.mp("ℤ"), 
				POUtil.mh(
						POUtil.mp("y∈ℕ"), 
						POUtil.mp()), 
				POUtil.mp("x ≔y","1=1 ∧2=2 ∧x ∈ℕ∧y ∈ℕ"));
		POUtil.addSequent(poFile, "PO6", 
				"hyp0", 
				POUtil.mp("y","x'"), POUtil.mp("ℤ","ℤ"), 
				POUtil.mh(
						POUtil.mp("y∈ℕ"), 
						POUtil.mp()), 
				POUtil.mp("x' ≔y","1=1 ∧2=2 ∧x ∈ℕ∧x' ∈ℕ"));
		POUtil.addSequent(poFile, "PO7", 
				"hyp0", 
				POUtil.mp("y"), POUtil.mp("ℤ"), 
				POUtil.mh(
						POUtil.mp(),
						POUtil.mp()), 
				POUtil.mp("y∈ℕ"));
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
		IPRSequent[] prs = prFile.getSequents();
		for (int i = 0; i < prs.length - 1; i++) {
			IPRSequent prSequent = prs[i];
			assertDischarged(prSequent);
		}
		assertNotDischarged(prs[prs.length-1]);
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
				if (prChild instanceof IPROOF) {
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
		IPROOF status = prSequent.getProof();
		assertEquals("PO " + prSequent.getName() + " should be discharged",
				IPROOF.Status.DISCHARGED,
				status.getStatus());
	}

	private void assertNotDischarged(IPRSequent prSequent) throws RodinDBException {
		IPROOF status = prSequent.getProof();
		assertEquals("PO " + prSequent.getName() + " should not be discharged",
				IPROOF.Status.PENDING,
				status.getStatus());
	}

}
