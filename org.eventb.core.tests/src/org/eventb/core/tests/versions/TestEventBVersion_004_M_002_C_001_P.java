/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *     University of Dusseldorf - added theorem attribute
 *******************************************************************************/

package org.eventb.core.tests.versions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eventb.core.EventBAttributes;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRRoot;
import org.junit.Test;
import org.rodinp.core.IRodinFile;

/**
 * Version 4 of machine and version 2 of context database take mathematical
 * language version 2 in formula attributes.
 * 
 * @author Nicolas Beauger
 * 
 */
public class TestEventBVersion_004_M_002_C_001_P extends EventBVersionTest {

	private static final String CTX_NAME = "ctx.buc";
	private static final String MCH_NAME = "mch.bum";
	private static final String PRF_NAME = "prf.bpr";

	private static String makeContextWithAxiom(String axiomString) {
		return
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<org.eventb.core.contextFile" +
			"		version=\"1\"" +
			"		org.eventb.core.configuration=\"org.eventb.core.fwd\">" +
			"	<org.eventb.core.axiom" +
			"		name=\"internal_axm1\"" +
			"		org.eventb.core.label=\"axm1\"" +
			"		org.eventb.core.predicate=\"" + axiomString + "\"/>" +
			"</org.eventb.core.contextFile>";
	}
	
	private static void assertNoChange(String expected, String actual) {
		assertEquals("expected no change", expected, actual);
	}

	private static void assertChanged(String formulaString, String upgraded) {
		assertFalse("expected formula string change for " + formulaString,
				formulaString.equals(upgraded));
	}

	private static void assertTyped(String formulaString) {
		assertTrue("expected a typed formula, but got: " + formulaString, 
				countOccs(formulaString, '⦂') > 0);
	}

	private static int countOccs(String str, char c) {
		int count = 0;
		for (int i=0;i<str.length();i++) {
			if (str.charAt(i)==c) count++;
		}
		return count;
	}

	private String makeUpgrade(String axiomString)
			throws Exception {
		createFile(CTX_NAME, makeContextWithAxiom(axiomString));
		
		final IRodinFile file = rodinProject.getRodinFile(CTX_NAME);

		try {
			final IContextRoot root = (IContextRoot) file.getRoot();
			final IAxiom[] axioms = root.getAxioms();
			final String actual = assertSingleGet(axioms).getPredicateString();
			return actual;
		} finally {
			file.delete(true, null);
		}
	}

	// Verify that a formula with an identifier 'partition' in V1
	// is left unchanged in V2 (even though it does not parse anymore)
	@Test
	public void testPartitionAsIdentifier() throws Exception {
		final String partitionIdent = "partition(x) = y";
		
		final String actual = makeUpgrade(partitionIdent);
		assertNoChange(partitionIdent, actual);
	}

	@Test
	public void testChangeExpected() throws Exception {
		final String pred = "∀X· \n (T  \n \t S \t ⤔ S) ⊆ X";
		
		final String actual = makeUpgrade(pred);
	
		assertChanged(pred, actual);
	}
	
	@Test
	public void testContext() throws Exception {
		final String axiom = "c ∈ id(S)";
		final String theorem = axiom;
		final String ctx =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		"<org.eventb.core.contextFile" +
		"		version=\"1\"" +
		"		org.eventb.core.configuration=\"org.eventb.core.fwd\">" +
		"	<org.eventb.core.axiom" +
		"		name=\"internal_axm1\"" +
		"		org.eventb.core.label=\"axm1\"" +
		"		org.eventb.core.predicate=\"" + axiom + "\"/>" +
		"	<org.eventb.core.theorem" +
		"		name=\"internal_thm1\"" +
		"		org.eventb.core.label=\"thm1\"" +
		"		org.eventb.core.predicate=\"" + theorem + "\"/>" +
		"</org.eventb.core.contextFile>";

		createFile(CTX_NAME, ctx);
		
		final IRodinFile file = rodinProject.getRodinFile(CTX_NAME);
		final IContextRoot root = (IContextRoot) file.getRoot();
		final IAxiom[] axioms = root.getAxioms();
		
		final String axiomUpg;
		final String theoremUpg;
		if (axioms[0].isTheorem()) {
			axiomUpg = axioms[1].getPredicateString();
			theoremUpg = axioms[0].getPredicateString();
		} else {
			axiomUpg = axioms[0].getPredicateString();
			theoremUpg = axioms[1].getPredicateString();
		}
		assertChanged(axiom, axiomUpg);
		assertChanged(theorem, theoremUpg);
	}

	@Test
	public void testMachine() throws Exception {
		final String invariant = "v ∈ id(ℤ)";
		final String variant = "id(v)";
		final String guard = "p ∈ id(ℤ)";
		final String witness = "m ∈ id(ℤ)";
		final String action = "v ≔ (p;m)(id(ℤ))";
		final String mch =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		"<org.eventb.core.machineFile" +
		"	version=\"3\"" +
		"	org.eventb.core.configuration=\"org.eventb.core.fwd\">" +
		"		<org.eventb.core.variable" +
		"			name=\"internal_var1\"" +
		"			org.eventb.core.identifier=\"v\"/>" +
		"		<org.eventb.core.invariant" +
		"			name=\"internal_inv1\"" +
		"			org.eventb.core.label=\"inv1\"" +
		"			org.eventb.core.predicate=\"" + invariant + "\"/>" +
		"		<org.eventb.core.variant" +
		"			name=\"internal_1\"" +
		"			org.eventb.core.expression=\"" + variant + "\"/>" +
		"		<org.eventb.core.event" +
		"			name=\"internal_evt1\"" +
		"			org.eventb.core.convergence=\"0\"" +
		"			org.eventb.core.extended=\"false\"" +
		"			org.eventb.core.label=\"evt1\">" +
		"			<org.eventb.core.parameter" +
		"				name=\"internal_prm1\"" +
		"				org.eventb.core.identifier=\"p\"/>" +
		"			<org.eventb.core.guard" +
		"				name=\"internal_grd1\"" +
		"				org.eventb.core.label=\"grd1\"" +
		"				org.eventb.core.predicate=\"" + guard + "\"/>" +
		"			<org.eventb.core.witness" +
		"				name=\"internal_wit1\"" +
		"				org.eventb.core.label=\"m\"" +
		"				org.eventb.core.predicate=\"" + witness + "\"/>" +
		"			<org.eventb.core.action" +
		"				name=\"internal_act1\"" +
		"				org.eventb.core.label=\"act1\"" +
		"				org.eventb.core.assignment=\"" + action + "\"/>" +
		"		</org.eventb.core.event>" +
		"</org.eventb.core.machineFile>";

		createFile(MCH_NAME, mch);
		
		final IRodinFile file = rodinProject.getRodinFile(MCH_NAME);
		final IMachineRoot root = (IMachineRoot) file.getRoot();
		final String invariantUpg = assertSingleGet(root.getInvariants()).getPredicateString();
		final String variantUpg = assertSingleGet(root.getVariants()).getExpressionString();
		final IEvent event = assertSingleGet(root.getEvents());
		final String guardUpg = assertSingleGet(event.getGuards()).getPredicateString();
		final String witnessUpg = assertSingleGet(event.getWitnesses()).getPredicateString();
		final String actionUpg = assertSingleGet(event.getActions()).getAssignmentString();
		
		assertChanged(invariant, invariantUpg);
		assertChanged(variant, variantUpg);
		assertChanged(guard, guardUpg);
		assertChanged(witness, witnessUpg);
		assertChanged(action, actionUpg);
		
	}
	
	@Test
	public void testProof() throws Exception {
		final String pred = "∃s⦂ℙ(ℤ)·s⊆ℕ∧id(s)=(∅ ⦂ ℙ(ℤ×ℤ))";
		final String expr = "(∅ ⦂ ℙ(ℤ))∩id(ℕ)[ℕ]";
		final String prf =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		"<org.eventb.core.prFile>" +
		"	<org.eventb.core.prProof name=\"thm1/THM\" org.eventb.core.confidence=\"1000\" org.eventb.core.prFresh=\"\" org.eventb.core.prGoal=\"p0\" org.eventb.core.prHyps=\"\" org.eventb.core.prSets=\"S\" org.eventb.core.psManual=\"true\">" +
		"		<org.eventb.core.prPred name=\"p2\" org.eventb.core.predicate=\"" + pred + "\"/>" +
		"		<org.eventb.core.prExpr name=\"e0\" org.eventb.core.expression=\"" + expr + "\"/>" +
		"	</org.eventb.core.prProof>" +
		"</org.eventb.core.prFile>";

		createFile(PRF_NAME, prf);
		
		final IRodinFile file = rodinProject.getRodinFile(PRF_NAME);
		final IPRRoot root = (IPRRoot) file.getRoot();
		final IPRProof prfThm1 = root.getProof("thm1/THM");
		final String predUpg = prfThm1.getPredicate("p2").getAttributeValue(EventBAttributes.PREDICATE_ATTRIBUTE);
		final String exprUpg = prfThm1.getExpression("e0").getAttributeValue(EventBAttributes.EXPRESSION_ATTRIBUTE);
		assertChanged(pred, predUpg);
		assertTyped(predUpg);
		assertChanged(expr, exprUpg);
		assertTyped(exprUpg);
	}
}
