/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - mathematical language V2
 *******************************************************************************/
package org.eventb.core.tests.pom;

import static org.eventb.core.ast.LanguageVersion.V2;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.pm.IProofManager;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.tests.BuilderTest;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class AutoPOMTest extends BuilderTest {

	/**
	 * Creates a new type environment from the given strings. The given strings
	 * are alternatively an identifier name and its type.
	 * 
	 * @param strings
	 *            an even number of strings
	 * @return a new type environment
	 */
	public ITypeEnvironment mTypeEnvironment(String... strings) {
		// even number of strings
		assert (strings.length & 1) == 0;
		final ITypeEnvironment result = factory.makeTypeEnvironment();
		for (int i = 0; i < strings.length; i += 2) {
			final String name = strings[i];
			final String typeString = strings[i+1];
			final IParseResult pResult = factory.parseType(typeString, V2);
			assertFalse("Parsing type failed for " + typeString,
					pResult.hasProblem());
			final Type type = pResult.getParsedType(); 
			result.addName(name, type);
		}
		return result;
	}

	private IPORoot poRoot;
	
	private void createPOFile() throws RodinDBException {
		poRoot = createPOFile("x");
		final IPOPredicateSet hyp0 = POUtil.addPredicateSet(poRoot, "hyp0", null,
				mTypeEnvironment("x", "ℤ"),
				"1=1", "2=2", "x∈ℕ"
		);
		POUtil.addSequent(poRoot, "PO1", 
				"1=1 ∧2=2 ∧x ∈ℕ",
				hyp0, 
				mTypeEnvironment()
		);
		POUtil.addSequent(poRoot, "PO2", 
				"1=1 ∧2=2 ∧x ∈ℕ∧y ∈ℕ",
				hyp0, 
				mTypeEnvironment("y", "ℤ"), 
				"y∈ℕ" 
		);
		POUtil.addSequent(poRoot, "PO3", 
				"3=3", 
				hyp0, 
				mTypeEnvironment(),
				"3=3"
		);
		POUtil.addSequent(poRoot, "PO4", 
				"1=1 ∧2=2 ∧x ∈ℕ", 
				hyp0, 
				mTypeEnvironment(),
				"3=3"
		);
		POUtil.addSequent(poRoot, "PO5", 
				"1=1 ∧2=2 ∧y ∈ℕ∧y ∈ℕ", 
				hyp0, 
				mTypeEnvironment("y", "ℤ"), 
				"y∈ℕ"
		);
		POUtil.addSequent(poRoot, "PO6", 
				"1=1 ∧2=2 ∧x ∈ℕ∧y ∈ℕ", 
				hyp0, 
				mTypeEnvironment(
						"y", "ℤ",
						"x'", "ℤ"
				), 
				"y∈ℕ"
		);
		POUtil.addSequent(poRoot, "PO7", 
				"y∈ℕ", 
				hyp0, 
				mTypeEnvironment("y", "ℤ"), 
				"x=x"
		);
		saveRodinFileOf(poRoot);
	}

	/*
	 * Test method for 'org.eventb.internal.core.pom.AutoPOM.AutoPOM(IPOFile, IInterrupt, IProgressMonitor)'
	 */
	public final void testAutoPOM() throws CoreException {
		
		createPOFile();
		IPSRoot psRoot = poRoot.getPSRoot();
		IPRRoot prRoot = poRoot.getPRRoot();
		
		enableAutoProver();
		runBuilder();
		
		// Checks that status in PS file corresponds POs in PO file.
		checkPOsConsistent(poRoot, psRoot);		
		// Checks that status in PS file corresponds Proofs in PR file.
		checkProofsConsistent(prRoot, psRoot);
		
		// Checks that all POs are discharged except the last one.
		IPSStatus[] prs = (IPSStatus[]) psRoot.getStatuses();
		for (int i = 0; i < prs.length - 1; i++) {
			IPSStatus prSequent = prs[i];
			assertDischarged(prSequent);
		}
		assertNotDischarged(prs[prs.length-1]);
		
		// Try an interactive proof on the last one via the PSWrapper
		final IProofManager pm = EventBPlugin.getProofManager();
		final IProofComponent pc = pm.getProofComponent(psRoot);
		final String poName = prs[prs.length-1].getElementName();
		final IProofAttempt pa = pc.createProofAttempt(poName, "test", null);
		final IProofTree proofTree = pa.getProofTree();
		Tactics.lemma("∀x· x∈ℤ ⇒ x=x").apply(proofTree.getRoot().getFirstOpenDescendant(), null);
		pa.commit(true, null);
		pa.dispose();
		
		// Checks that proof is marked as manual
		assertManualProof(prs[prs.length-1]);
		// Checks that status in PS file corresponds POs in PO file.
		checkPOsConsistent(poRoot, psRoot);		
		// Checks that status in PS file corresponds Proofs in PR file.
		checkProofsConsistent(prRoot, psRoot);
	}
	

	private void checkProofsConsistent(IPRRoot prRoot, IPSRoot psFile) throws RodinDBException {
		IPSStatus[] statuses = psFile.getStatuses();
		for (IPSStatus status : statuses) {
			IPRProof prProofTree = status.getProof();
			String name = status.getElementName();
			assertTrue("Proof absent for "+name , prProofTree.exists());
			assertEquals("Proof confidence different for " + name, prProofTree.getConfidence(), status.getConfidence());
			assertEquals("hasManualProof attribute different for " + name, prProofTree.getHasManualProof(), status.getHasManualProof());
		}
	}

	// TODO : make simpler and add check for PO stamps
	private void checkPOsConsistent(
			IInternalElement poElement,
			IInternalElement prElement) throws RodinDBException {
		
		if (poElement instanceof IPORoot) {
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
				final IInternalElement poChild = (IInternalElement) poChildren[poIdx];
				final IInternalElement prChild = (IInternalElement) prChildren[prIdx];
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
		assertFalse("PR " + status.getElementName() + " should be valid",
				status.isBroken());
		assertTrue("PO " + status.getElementName() + " should be closed",
				IConfidence.PENDING <
				status.getConfidence());
		assertFalse("PR " + status.getElementName() + " should be auto proven",
				status.getHasManualProof());
	}
	
	private void assertManualProof(IPSStatus status) throws RodinDBException {
		assertFalse("PR " + status.getElementName() + " should be valid",
				status.isBroken());
		assertTrue("PR " + status.getElementName() + " should not be marked as a manual proof",
				status.getHasManualProof());
	}
	
	private void assertNotDischarged(IPSStatus status) throws RodinDBException {
		assertFalse("PR " + status.getElementName() + " should be valid",
				status.isBroken());
		assertTrue("PO " + status.getElementName() + " should not be closed",
				IConfidence.PENDING >=
				status.getConfidence());
		assertFalse("PR " + status.getElementName() + " should be auto proven",
				status.getHasManualProof());
	}
	
	public static String[] mp(String... strings) {
		return strings;
	}
	
	public static String[] mh(String... strings) {
		return strings;
	}

}
