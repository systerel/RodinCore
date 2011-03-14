/*******************************************************************************
 * Copyright (c) 2009, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.proofSimplifier;

import static org.eventb.core.seqprover.eventbExtensions.Tactics.hyp;
import static org.eventb.core.tests.ResourceUtils.CTX_BARE_NAME;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.pm.IProofManager;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.tests.BuilderTest;
import org.eventb.core.tests.ResourceUtils;
import org.eventb.core.tests.pom.POUtil;
import org.eventb.core.tests.pom.TestLib;
import org.rodinp.core.RodinDBException;

/**
 * @author Nicolas Beauger
 * 
 */
public class ProofSimplifierTests extends BuilderTest {

	private static final String AXM1_THM = "axm1/THM";
	
	private IPSRoot createPSFile() throws Exception {
		final String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
				+ "<org.eventb.core.psFile>"
				+ "   <org.eventb.core.psStatus name=\"axm1/THM\" org.eventb.core.confidence=\"1000\" org.eventb.core.poStamp=\"1\" org.eventb.core.psManual=\"false\"/>"
				+ "</org.eventb.core.psFile>";

		return ResourceUtils
				.createPSFile(rodinProject, CTX_BARE_NAME, contents);
	}

	private void createPOFile1() throws RodinDBException {
		final IPORoot poRoot = createPOFile(CTX_BARE_NAME);
		
		final ITypeEnvironment typeEnv = factory.makeTypeEnvironment();
		typeEnv.addName("c", factory.makeIntegerType());
		final IPOPredicateSet hyp0 = POUtil.addPredicateSet(poRoot, "hyp0", null,
				typeEnv,
				"c≠1", "c≠2", "c=0"
		);
		POUtil.addSequent(poRoot, AXM1_THM, 
				"c=0",
				hyp0, 
				typeEnv
		);
		saveRodinFileOf(poRoot);
	}

	// FIXME This test fails as the proof simplifier has been disconnected (in r9882)
	// FIXME uses autoRewrites which is subject to version changes
	public void testSimplifyPRProof() throws Exception {
		final String contents =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<org.eventb.core.prFile version=\"1\">" +
			"	<org.eventb.core.prProof name=\"axm1/THM\" org.eventb.core.confidence=\"1000\" org.eventb.core.prFresh=\"\" org.eventb.core.prGoal=\"p0\" org.eventb.core.prHyps=\"p0\" org.eventb.core.psManual=\"true\">" +
			"		<org.eventb.core.prRule name=\"org.eventb.core.seqprover.autoRewrites:4\" org.eventb.core.confidence=\"1000\" org.eventb.core.prDisplay=\"simplification rewrites\" org.eventb.core.prHyps=\"\">" +
			"			<org.eventb.core.prAnte name=\"0\">" +
			"				<org.eventb.core.prHypAction name=\"FORWARD_INF0\" org.eventb.core.prHyps=\"p1\" org.eventb.core.prInfHyps=\"p2\"/>" +
			"				<org.eventb.core.prHypAction name=\"HIDE1\" org.eventb.core.prHyps=\"p1\"/>" +
			"				<org.eventb.core.prHypAction name=\"FORWARD_INF2\" org.eventb.core.prHyps=\"p3\" org.eventb.core.prInfHyps=\"p4\"/>" +
			"				<org.eventb.core.prHypAction name=\"HIDE3\" org.eventb.core.prHyps=\"p3\"/>" +
			"				<org.eventb.core.prRule name=\"org.eventb.core.seqprover.hyp\" org.eventb.core.confidence=\"1000\" org.eventb.core.prDisplay=\"hyp\" org.eventb.core.prGoal=\"p0\" org.eventb.core.prHyps=\"p0\"/>" +
			"			</org.eventb.core.prAnte>" +
			"		</org.eventb.core.prRule>" +
			"		<org.eventb.core.prIdent name=\"c\" org.eventb.core.type=\"ℤ\"/>" +
			"		<org.eventb.core.prPred name=\"p3\" org.eventb.core.predicate=\"c≠2\"/>" +
			"		<org.eventb.core.prPred name=\"p0\" org.eventb.core.predicate=\"c=0\"/>" +
			"		<org.eventb.core.prPred name=\"p4\" org.eventb.core.predicate=\"¬c=2\"/>" +
			"		<org.eventb.core.prPred name=\"p1\" org.eventb.core.predicate=\"c≠1\"/>" +
			"		<org.eventb.core.prPred name=\"p2\" org.eventb.core.predicate=\"¬c=1\"/>" +
			"	</org.eventb.core.prProof>" +
			"</org.eventb.core.prFile>";
		
		
		createPOFile1();
		createPSFile();
		final IPRRoot prRoot = ResourceUtils.createPRFile(rodinProject,
				CTX_BARE_NAME, contents);
		
		final IPRProof proof = prRoot.getProof(AXM1_THM);
		final boolean success = EventBPlugin.simplifyProof(proof, factory, null);
		assertTrue("should have succeeded", success);
		final IProofTree simplified = proof.getProofTree(null);
		assertTrue(simplified.getRoot().getChildNodes().length == 0);
	}

	private void createPOFile2() throws RodinDBException {
		final IPORoot poRoot = createPOFile(CTX_BARE_NAME);
		
		final ITypeEnvironment typeEnv = factory.makeTypeEnvironment();
		typeEnv.addName("c", factory.makeIntegerType());
		
		final IPOPredicateSet hyp0 = POUtil.addPredicateSet(poRoot, "hyp0", null,
				typeEnv,
				"c ∈ ℕ∖{0}", "c ∈ ℤ∖{0}"
		);
		POUtil.addSequent(poRoot, AXM1_THM, 
				"c ∈ ℕ",
				hyp0, 
				typeEnv
		);
		saveRodinFileOf(poRoot);
	}
	
	public void testBug2800402() throws Exception {
		final String contents = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		"<org.eventb.core.prFile version=\"1\"/>";

		createPOFile2();
		createPSFile();
		final IPRRoot prRoot = ResourceUtils.createPRFile(rodinProject,
				CTX_BARE_NAME, contents);
		
		final IProofManager pm = EventBPlugin.getProofManager();
		final IProofComponent pc = pm.getProofComponent(prRoot);
		
		final IProofAttempt pa = pc.createProofAttempt(AXM1_THM, "TEST", null);
		final IProofTreeNode root = pa.getProofTree().getRoot();
		IProofTreeNode node = root;
		
		Tactics.removeMembership(TestLib.genPred("c ∈ ℤ∖{0}"), IPosition.ROOT)
				.apply(node, null);
		node = node.getFirstOpenDescendant();
		Tactics.removeMembership(TestLib.genPred("c ∈ ℕ∖{0}"), IPosition.ROOT)
				.apply(node, null);
		node = node.getFirstOpenDescendant();
		hyp().apply(node, null);

		pa.commit(true, true, null);
		pa.dispose();
		
		// the following throws IllegalStateException when the bug is present
		pc.getProofSkeleton(AXM1_THM, factory, null);
	}
}
