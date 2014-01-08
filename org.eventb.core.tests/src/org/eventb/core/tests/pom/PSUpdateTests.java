/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.tests.pom;

import static org.eventb.core.EventBAttributes.HYPS_ATTRIBUTE;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.lemma;
import static org.eventb.core.tests.extension.PrimeFormulaExtensionProvider.EXT_FACTORY;
import static org.eventb.core.tests.pom.TestLib.genPred;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRProofRule;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.pm.IProofManager;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.tests.BuilderTest;
import org.eventb.core.tests.extension.PrimeFormulaExtensionProvider;
import org.junit.Test;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Tests ensuring that PS files are properly updated when the AutoPOM tool is
 * run.
 * 
 * @author Laurent Voisin
 */
public class PSUpdateTests extends BuilderTest {

	private static final String[] NO_STRING = new String[0];

	private static FormulaFactory ff = FormulaFactory.getDefault();
	private static Predicate BTRUE = ff.makeLiteralPredicate(Formula.BTRUE, null);

	private IPORoot poRoot;
	private IPSRoot psRoot;
	
	private IPOSequent getPOSequent(String name) {
		return poRoot.getSequent(name);
	}
	
	private void createPOFile() throws RodinDBException {
		poRoot = createPOFile("x");
		psRoot = poRoot.getPSRoot();
	}
	
	private void assertPOFile(String nameList) throws RodinDBException {
		final String[] names;
		if (nameList.trim().length() == 0) {
			names = NO_STRING;
		} else {
			names = nameList.split(",");
		}
		final IPOSequent[] poSequents = poRoot.getSequents();
		final int length = names.length;
		assertEquals(length, poSequents.length);
		for (int i = 0; i < length; ++ i) {
			final String name = names[i].trim();
			assertEquals(name, poSequents[i].getElementName());
		}
	}

	private void addPO(String name, IPOSequent nextSibling)
			throws RodinDBException {
		addPO(name, nextSibling, BTRUE);
	}

	private void addPO(String name, IPOSequent nextSibling, Predicate goal)
			throws RodinDBException {
		final IPOSequent poSequent = poRoot.getSequent(name);
		poSequent.create(nextSibling, null);
		poSequent.setAccuracy(true, null);
		poSequent.setPOStamp(123, null);
		final IPOPredicate poGoal = poSequent.getGoal("G");
		poGoal.create(null, null);
		poGoal.setPredicate(goal, null);
	}

	private void changePO(String name) throws RodinDBException {
		final IPOSequent poSequent = poRoot.getSequent(name);
		poSequent.setPOStamp(poSequent.getPOStamp() + 1, null);
	}

	private void addProof(String name) throws RodinDBException {
		final IProofManager pm = EventBPlugin.getProofManager();
		final IProofComponent pc = pm.getProofComponent(poRoot);
		final IProofAttempt pa = pc.createProofAttempt(name, "test", null);
		final IProofTreeNode root = pa.getProofTree().getRoot();
		lemma(BTRUE.toString()).apply(root, null);
		pa.commit(true, null);
		pc.save(null, false);
	}

	// Remove an essential field of a proof dependency
	private void damageProofDependency(String name) throws RodinDBException {
		final IPRProof proof = poRoot.getPRRoot().getProof(name);
		proof.removeAttribute(HYPS_ATTRIBUTE, null);
		proof.getRodinFile().save(null, false);
	}

	// Remove an essential field of a rule
	private void damageProof(String name) throws RodinDBException {
		final IPRProof proof = poRoot.getPRRoot().getProof(name);
		final IPRProofRule rule = proof.getProofRules()[0];
		rule.removeAttribute(HYPS_ATTRIBUTE, null);
		proof.getRodinFile().save(null, false);
	}

	// Remove the argument of a cut rule, destroying its input
	private void damageProofInput(String name) throws RodinDBException {
		final IPRProof proof = poRoot.getPRRoot().getProof(name);
		final IPRProofRule rule = proof.getProofRules()[0];
		rule.getPRPredRef("pred").delete(false, null);
		proof.getRodinFile().save(null, false);
	}

	private void deletePO(String name) throws RodinDBException {
		final IPOSequent poSequent = poRoot.getSequent(name);
		poSequent.delete(false, null);
	}

	private void movePO(String name, String nextName) throws RodinDBException {
		final IPOSequent poSequent = poRoot.getSequent(name);
		final IPOSequent nextSibling;
		if (nextName == null) {
			nextSibling = null;
		} else {
			nextSibling = getPOSequent(nextName);
		}
		poSequent.move(poRoot, nextSibling, null, false, null);
	}

	/**
	 * Checks that the PS file contains statuses for all POs, and with the
	 * correct stamp. Also checks that statuses are stored in exactly the same
	 * order as POs.
	 */
	private void checkPSFile() throws RodinDBException {
		assertTrue(poRoot.exists());
		assertTrue(psRoot.exists());
		final IPOSequent[] poSequents = poRoot.getSequents();
		final IPSStatus[] psStatuses = psRoot.getStatuses();
		final int length = poSequents.length;
		assertEquals(length, psStatuses.length);
		for (int i = 0; i < length; i++) {
			final IPOSequent poSequent = poSequents[i];
			final IPSStatus psStatus = psStatuses[i]; 
			assertEquals(poSequent, psStatus.getPOSequent());
			if (poSequent.hasPOStamp()) {
				assertTrue(psStatus.hasPOStamp());
				assertEquals(poSequent.getPOStamp(), psStatus.getPOStamp());
			} else {
				assertFalse(psStatus.hasPOStamp());
			}
		}
	}

	/**
	 * Saves the PO file, checks its contents, then runs the builder and check
	 * the generated / modified PS file.
	 */
	protected void runBuilder(String poNameList) throws CoreException {
		final IRodinFile poFile = poRoot.getRodinFile();
		if (poFile.hasUnsavedChanges()) {
			poFile.save(null, false, false);
		}
		assertPOFile(poNameList);
		super.runBuilder();
		checkPSFile();
	}
	
	/**
	 * Ensures that an empty PO file is properly processed.
	 */
	@Test
	public final void testEmpty() throws CoreException {
		createPOFile();
		runBuilder("");
	}
	
	/**
	 * Ensures that adding one PO to an empty PO file is properly processed.
	 */
	@Test
	public final void testEmptyAddOne() throws CoreException {
		createPOFile();
		runBuilder("");
		
		addPO("1", null);
		runBuilder("1");
	}

	/**
	 * Ensures that adding two POs to an empty PO file is properly processed.
	 */
	@Test
	public final void testEmptyAddTwo() throws CoreException {
		createPOFile();
		runBuilder("");
		
		addPO("1", null);
		addPO("2", null);
		runBuilder("1,2");
	}

	/**
	 * Ensures that a PO file containing one PO is properly processed.
	 */
	@Test
	public final void testOne() throws CoreException {
		createPOFile();
		addPO("1", null);
		runBuilder("1");
	}
	
	/**
	 * Ensures that adding one PO before the only PO of a PO file is properly
	 * processed.
	 */
	@Test
	public final void testOneAddBefore() throws CoreException {
		createPOFile();
		addPO("1", null);
		runBuilder("1");
		
		addPO("2", getPOSequent("1"));
		runBuilder("2,1");
	}
	
	/**
	 * Ensures that adding one PO after the only PO of a PO file is properly
	 * processed.
	 */
	@Test
	public final void testOneAddAfter() throws CoreException {
		createPOFile();
		addPO("1", null);
		runBuilder("1");
		
		addPO("2", null);
		runBuilder("1,2");
	}
	
	/**
	 * Ensures that a PO file containing two POs is properly processed.
	 */
	@Test
	public final void testTwo() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		runBuilder("1,2");
	}
	
	/**
	 * Ensures that adding one PO before the two POs of a PO file is properly
	 * processed.
	 */
	@Test
	public final void testTwoAddBefore() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		runBuilder("1,2");
		
		addPO("3", getPOSequent("1"));
		runBuilder("3,1,2");
	}
	
	/**
	 * Ensures that adding one PO between the two POs of a PO file is properly
	 * processed.
	 */
	@Test
	public final void testTwoAddBetween() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		runBuilder("1,2");
		
		addPO("3", getPOSequent("2"));
		runBuilder("1,3,2");
	}
	
	/**
	 * Ensures that adding one PO after the two POs of a PO file is properly
	 * processed.
	 */
	@Test
	public final void testTwoAddAfter() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		runBuilder("1,2");
		
		addPO("3", null);
		runBuilder("1,2,3");
	}
	
	/**
	 * Ensures that adding three POs at every position in a PO file containing
	 * two POs is properly processed.
	 */
	@Test
	public final void testTwoAddThree() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		runBuilder("1,2");
		
		addPO("3", getPOSequent("1"));
		addPO("4", getPOSequent("2"));
		addPO("5", null);
		runBuilder("3,1,4,2,5");
	}
	
	/**
	 * Ensures that deleting the only PO of a PO file is properly processed.
	 */
	@Test
	public final void testOneDelete() throws CoreException {
		createPOFile();
		addPO("1", null);
		runBuilder("1");
		
		deletePO("1");
		runBuilder("");
	}
	
	/**
	 * Ensures that deleting the first PO of a PO file containing two POs is
	 * properly processed.
	 */
	@Test
	public final void testTwoDeleteFirst() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		runBuilder("1,2");
		
		deletePO("1");
		runBuilder("2");
	}
	
	/**
	 * Ensures that deleting the last PO of a PO file containing two POs is
	 * properly processed.
	 */
	@Test
	public final void testTwoDeleteLast() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		runBuilder("1,2");
		
		deletePO("2");
		runBuilder("1");
	}
	
	/**
	 * Ensures that deleting both POs of a PO file containing two POs is
	 * properly processed.
	 */
	@Test
	public final void testTwoDeleteAll() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		runBuilder("1,2");
		
		deletePO("1");
		deletePO("2");
		runBuilder("");
	}
	
	/**
	 * Ensures that deleting the first PO of a PO file containing three POs is
	 * properly processed.
	 */
	@Test
	public final void testThreeDeleteFirst() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		addPO("3", null);
		runBuilder("1,2,3");
		
		deletePO("1");
		runBuilder("2,3");
	}
	
	/**
	 * Ensures that deleting the second PO of a PO file containing three POs is
	 * properly processed.
	 */
	@Test
	public final void testThreeDeleteSecond() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		addPO("3", null);
		runBuilder("1,2,3");
		
		deletePO("2");
		runBuilder("1,3");
	}
	
	/**
	 * Ensures that deleting the last PO of a PO file containing three POs is
	 * properly processed.
	 */
	@Test
	public final void testThreeDeleteLast() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		addPO("3", null);
		runBuilder("1,2,3");
		
		deletePO("3");
		runBuilder("1,2");
	}
	
	/**
	 * Ensures that deleting all POs of a PO file containing three POs is
	 * properly processed.
	 */
	@Test
	public final void testThreeDeleteAll() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		addPO("3", null);
		runBuilder("1,2,3");
		
		deletePO("1");
		deletePO("2");
		deletePO("3");
		runBuilder("");
	}
	
	/**
	 * Ensures that permuting the only two POs of a PO file is properly
	 * processed.
	 */
	@Test
	public final void testTwoPermute() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		runBuilder("1,2");
		
		movePO("1", null);
		runBuilder("2,1");
	}
	
	/**
	 * Ensures that permuting the only three POs of a PO file is properly
	 * processed.
	 */
	@Test
	public final void testThreePermute_1() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		addPO("3", null);
		runBuilder("1,2,3");
		
		movePO("2", null);
		runBuilder("1,3,2");
	}
	
	/**
	 * Ensures that permuting the only three POs of a PO file is properly
	 * processed.
	 */
	@Test
	public final void testThreePermute_2() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		addPO("3", null);
		runBuilder("1,2,3");
		
		movePO("2", "1");
		runBuilder("2,1,3");
	}
	
	/**
	 * Ensures that permuting the only three POs of a PO file is properly
	 * processed.
	 */
	@Test
	public final void testThreePermute_3() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		addPO("3", null);
		runBuilder("1,2,3");
		
		movePO("1", null);
		runBuilder("2,3,1");
	}
	
	/**
	 * Ensures that permuting the only three POs of a PO file is properly
	 * processed.
	 */
	@Test
	public final void testThreePermute_4() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		addPO("3", null);
		runBuilder("1,2,3");
		
		movePO("3", "1");
		runBuilder("3,1,2");
	}
	
	/**
	 * Ensures that permuting the only three POs of a PO file is properly
	 * processed.
	 */
	@Test
	public final void testThreePermute_5() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		addPO("3", null);
		runBuilder("1,2,3");
		
		movePO("2", null);
		movePO("1", null);
		runBuilder("3,2,1");
	}
	
	/**
	 * Ensures that inverse permuting the only six POs of a PO file is properly
	 * processed.
	 */
	@Test
	public final void testSixPermute_1() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		addPO("3", null);
		addPO("4", null);
		addPO("5", null);
		addPO("6", null);
		runBuilder("1,2,3,4,5,6");
		
		movePO("5", null);
		movePO("4", null);
		movePO("3", null);
		movePO("2", null);
		movePO("1", null);
		runBuilder("6,5,4,3,2,1");
	}
	
	/**
	 * Ensures that permuting the only six POs of a PO file is properly
	 * processed (case with a random permutation with random deletions).
	 */
	@Test
	public final void testSixPermute_2() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		addPO("3", null);
		addPO("4", null);
		addPO("5", null);
		addPO("6", null);
		runBuilder("1,2,3,4,5,6");
		
		movePO("2", "1");
		deletePO("3");
		movePO("4", "1");
		movePO("5", "2");
		movePO("6", "4");
		runBuilder("5,2,6,4,1");
	}
	
	/**
	 * Ensures that permuting the only six POs of a PO file is properly
	 * processed (case with a random permutation with random deletions).
	 */
	@Test
	public final void testSixPermute_3() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		addPO("3", null);
		addPO("4", null);
		addPO("5", null);
		addPO("6", null);
		runBuilder("1,2,3,4,5,6");
		
		movePO("1", "3");
		deletePO("2");
		movePO("4", "3");
		movePO("5", "3");
		deletePO("6");
		runBuilder("1,4,5,3");
	}
	
	/**
	 * Ensures that permuting the only six POs of a PO file is properly
	 * processed (case with a random permutation with random deletions).
	 */
	@Test
	public final void testSixPermute_4() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		addPO("3", null);
		addPO("4", null);
		addPO("5", null);
		addPO("6", null);
		runBuilder("1,2,3,4,5,6");
		
		movePO("2", "1");
		movePO("3", "1");
		movePO("4", "2");
		movePO("5", "3");
		movePO("6", "4");
		runBuilder("6,4,2,5,3,1");
	}

	/**
	 * Ensures that a damaged proof dependency does not prevent POM from
	 * succeeding. The PO with the damaged proof is then marked as broken.
	 */
	@Test
	public final void testErroneousProofDependency() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		addPO("3", null);
		runBuilder("1,2,3");

		addProof("2");
		damageProofDependency("2");
		changePO("2");
		runBuilder("1,2,3");
		assertTrue(psRoot.getStatus("2").isBroken());
	}

	/**
	 * Ensures that a damaged proof does not prevent POM from succeeding.
	 * However, the PO with the damaged proof is not marked as broken if the
	 * proof dependencies match.
	 */
	@Test
	public final void testErroneousProof() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		addPO("3", null);
		runBuilder("1,2,3");

		addProof("2");
		damageProof("2");
		changePO("2");
		runBuilder("1,2,3");
		assertFalse(psRoot.getStatus("2").isBroken());
	}

	/**
	 * Ensures that a proof with a damaged input does not prevent POM from
	 * succeeding. However, the PO with the damaged proof is not marked as
	 * broken if the proof dependencies match.
	 */
	@Test
	public final void testErroneousProofInput() throws CoreException {
		createPOFile();
		addPO("1", null);
		addPO("2", null);
		addPO("3", null);
		runBuilder("1,2,3");

		addProof("2");
		damageProofInput("2");
		changePO("2");
		runBuilder("1,2,3");
		assertFalse(psRoot.getStatus("2").isBroken());
	}
	
	/**
	 * Ensure that a proof is kept valid when updating its proof obligation
	 * without really changing it nor its factory, even when using a specialized
	 * factory.
	 */
	@Test
	public void specializedFactoryNotChanged() throws Exception {
		createPOFile();
		PrimeFormulaExtensionProvider.add(poRoot);
		addPO("1", null, genPred(EXT_FACTORY, "prime({2})"));
		runBuilder("1");

		changePO("1");
		runBuilder("1");
		assertFalse(psRoot.getStatus("1").isBroken());
	}

	/**
	 * Ensure that a non-empty proof becomes invalid when updating a proof
	 * obligation without really changing it, if PO factory changes and that it
	 * gets recovered when resetting the PO factory.
	 */
	@Test
	public void specializedFactoryChangedToDefault() throws Exception {
		createPOFile();
		PrimeFormulaExtensionProvider.add(poRoot);
		addPO("1", null, genPred(EXT_FACTORY, "⊤"));
		runBuilder("1");

		addProof("1");
		changePO("1");
		PrimeFormulaExtensionProvider.clear();
		runBuilder("1");
		assertTrue(psRoot.getStatus("1").isBroken());

		changePO("1");
		PrimeFormulaExtensionProvider.add(poRoot);
		runBuilder("1");
		assertFalse(psRoot.getStatus("1").isBroken());
	}

	/**
	 * Ensure that a non-empty proof becomes invalid when updating a proof
	 * obligation without really changing it, if PO factory changes.
	 */
	@Test
	public void defaultFactoryChangedToSpecialized() throws Exception {
		createPOFile();
		addPO("1", null, genPred(ff, "⊤"));
		runBuilder("1");

		addProof("1");
		changePO("1");
		PrimeFormulaExtensionProvider.add(poRoot);
		runBuilder("1");
		assertTrue(psRoot.getStatus("1").isBroken());

		changePO("1");
		PrimeFormulaExtensionProvider.clear();
		runBuilder("1");
		assertFalse(psRoot.getStatus("1").isBroken());
	}

	/**
	 * Ensures that an empty proof has no formula factory. This is detected by
	 * changing the factory of the proof obligation and checking that the proof
	 * used instantaneously the same factory (without need to rebuild).
	 */
	@Test
	public void emptyProofHasNofactory() throws Exception {
		createPOFile();
		addPO("1", null, genPred(ff, "⊤"));
		runBuilder("1");

		final IPRProof proof = poRoot.getPRRoot().getProof("1");
		assertSame(ff, proof.getFormulaFactory(null));

		PrimeFormulaExtensionProvider.add(poRoot);
		assertSame(EXT_FACTORY, proof.getFormulaFactory(null));
	}

}
