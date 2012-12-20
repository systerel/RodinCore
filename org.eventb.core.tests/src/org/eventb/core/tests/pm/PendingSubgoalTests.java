/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.tests.pm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.seqprover.IConfidence.REVIEWED_MAX;
import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.tests.pom.POUtil;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Unit tests for the pending subgoal management in {@link IUserSupport}
 * 
 * @author Laurent Voisin
 */
public class PendingSubgoalTests extends TestPM {
	
	private static FormulaFactory ff = FormulaFactory.getDefault();
	
	private static Type INT = ff.makeIntegerType();
	
	private static Expression id_x = ff.makeFreeIdentifier("x", null, INT);
	
	private static Expression L0 = ff.makeIntegerLiteral(BigInteger.ZERO, null);
	private static Expression L1 = ff.makeIntegerLiteral(BigInteger.ONE, null);
	private static Expression L2 = ff.makeIntegerLiteral(BigInteger.valueOf(2), null);
	
	private static Predicate G = ff.makeRelationalPredicate(
			Predicate.EQUAL, id_x, L0, null);
	private static Predicate P1 = ff.makeRelationalPredicate(
			Predicate.EQUAL, id_x, L1, null);
	private static Predicate nP1 = ff.makeUnaryPredicate(
			Predicate.NOT, P1, null);
	private static Predicate P2 = ff.makeRelationalPredicate(
			Predicate.EQUAL, id_x, L2, null);
	private static Predicate nP2 = ff.makeUnaryPredicate(
			Predicate.NOT, P2, null);

	private static Predicate btrue = ff.makeLiteralPredicate(BTRUE, null);
	
	private static Predicate[] mList(Predicate... preds) {
		return preds;
	}
	
	/**
	 * Checks that the given sets are equal.
	 */
	private boolean checkEquals(Predicate[] ehyps, HashSet<Predicate> ahyps) {
		if (ahyps.size() != ehyps.length) {
			return false;
		}
		for (Predicate ehyp: ehyps) {
			if (! ahyps.contains(ehyp)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks that the given sequent contains the selected hypotheses and goal.
	 */
	private void assertSequent(Predicate[] ehyps, Predicate goal,
			IProverSequent sequent) {
		assertEquals(goal, sequent.goal());
		HashSet<Predicate> ahyps = new HashSet<Predicate>();
		for (Predicate ahyp : sequent.selectedHypIterable()) {
			ahyps.add(ahyp);
		}
		if (!checkEquals(ehyps, ahyps)) {
			fail("Unexpected selected hyps:"
					+ "\n  expected: " + Arrays.asList(ehyps)
					+ "\n  got: " + ahyps);
		}
	}
	
	// Handles to the proof files
	IPORoot poRoot;
	IPSRoot psRoot;
	IPRRoot prRoot;

	// UserSupport for the proof files
	IUserSupport userSupport;
	
	private IPORoot createPOFile() throws RodinDBException {
		IRodinFile file = rodinProject.getRodinFile("x.bpo");
		file.create(true, null);
		poRoot = (IPORoot) file.getRoot();
		IPOPredicateSet hyp0 = POUtil.addPredicateSet(poRoot, "hyp0", null,
				mTypeEnvironment("x=ℤ"));
		POUtil.addSequent(poRoot, "PO1", G.toString(), hyp0,
				mTypeEnvironment());
		file.save(null, true);
		return poRoot;
	}
	
	@Before
	public void createProofFiles() throws Exception {
		disablePostTactic();

		poRoot = createPOFile();
		prRoot = poRoot.getPRRoot();
		psRoot = poRoot.getPSRoot();

		enableTestAutoProver();
		runBuilder();

		userSupport = newUserSupport(psRoot);
	}

	private void setCurrentPO(String poName) throws RodinDBException {
		userSupport.setCurrentPO(psRoot.getStatus(poName), null);
		IProofState ps = userSupport.getCurrentPO();
		assertNotNull("PO not found", ps);
	}
	
	private void gotoNextSibling() throws RodinDBException {
		final IProofState ps = userSupport.getCurrentPO();
		final IProofTreeNode node = ps.getCurrentNode();
		final IProofTreeNode parent = node.getParent();
		final IProofTreeNode[] siblings = parent.getChildNodes();
		final int index = Arrays.asList(siblings).indexOf(node) + 1;
		if (index < siblings.length) {
			ps.setCurrentNode(siblings[index]);
		} else {
			ps.setCurrentNode(siblings[0]);
		}
	}

	/**
	 * Checks that the current node of the current proof tree is open and
	 * contains the given selected hypotheses and goal.
	 */
	private void assertOpen(Predicate[] hyps, Predicate goal) {
		final IProofTreeNode node = userSupport.getCurrentPO().getCurrentNode();
		assertNotNull("No current node in loaded PO", node);
		assertTrue("Current node should be open", node.isOpen());
		assertSequent(hyps, goal, node.getSequent());
	}
	
	/**
	 * Ensures that the current node when loading an unproved PO is open.
	 */
	@Test
	public void testFirstSubgoal() throws CoreException {
		setCurrentPO("PO1");
		assertOpen(mList(), G);
	}

	/**
	 * Ensures that the current node after applying a tactic is the first
	 * created child.
	 */
	@Test
	public void testFirstChild() throws CoreException {
		setCurrentPO("PO1");
		assertOpen(mList(), G);
		
		ITactic tac = Tactics.doCase(P1.toString());
		userSupport.applyTactic(tac, false, null);
		assertOpen(mList(), btrue);
	}

	/**
	 * Ensures that the current node after closing the first child is the second
	 * child.
	 */
	@Test
	public void testSecondChild() throws CoreException {
		setCurrentPO("PO1");
		assertOpen(mList(), G);
		
		final ITactic tac = Tactics.doCase(P1.toString());
		userSupport.applyTactic(tac, false, null);
		assertOpen(mList(), btrue);
		
		final ITactic tac2 = Tactics.review(REVIEWED_MAX);
		userSupport.applyTactic(tac2, false, null);
		assertOpen(mList(P1), G);
	}

	/**
	 * Ensures that the current node after closing the second child is the third
	 * child.
	 */
	@Test
	public void testSecondThenThirdChild() throws CoreException {
		setCurrentPO("PO1");
		assertOpen(mList(), G);
		
		final ITactic tac = Tactics.doCase(P1.toString());
		userSupport.applyTactic(tac, false, null);
		assertOpen(mList(), btrue);
		
		gotoNextSibling();
		assertOpen(mList(P1), G);

		final ITactic tac2 = Tactics.review(REVIEWED_MAX);
		userSupport.applyTactic(tac2, false, null);
		assertOpen(mList(nP1), G);
	}

	/**
	 * Ensures that the current node after closing the third child is the first
	 * child.
	 */
	@Test
	public void testThirdThenFirstChild() throws CoreException {
		setCurrentPO("PO1");
		assertOpen(mList(), G);
		
		final ITactic tac = Tactics.doCase(P1.toString());
		userSupport.applyTactic(tac, false, null);
		assertOpen(mList(), btrue);
		
		gotoNextSibling();
		assertOpen(mList(P1), G);

		gotoNextSibling();
		assertOpen(mList(nP1), G);

		final ITactic tac2 = Tactics.review(REVIEWED_MAX);
		userSupport.applyTactic(tac2, false, null);
		assertOpen(mList(), btrue);
	}

	/**
	 * Ensures that the current node after closing a branch is after the root
	 * of that branch, when present.
	 */
	@Test
	public void testBranchCloseNext() throws CoreException {
		setCurrentPO("PO1");
		assertOpen(mList(), G);
		
		final ITactic tac = Tactics.doCase(P1.toString());
		userSupport.applyTactic(tac, false, null);
		assertOpen(mList(), btrue);
		
		gotoNextSibling();
		assertOpen(mList(P1), G);
		gotoNextSibling();
		assertOpen(mList(nP1), G);

		final ITactic tac2 = Tactics.doCase(P2.toString());
		userSupport.applyTactic(tac2, false, null);
		gotoNextSibling();
		assertOpen(mList(nP1, P2), G);
		
		final ITactic tac3 = Tactics.review(REVIEWED_MAX);
		userSupport.applyTactic(tac3, false, null);
		assertOpen(mList(nP1, nP2), G);
	}

	/**
	 * Ensures that the current node after closing the last node of a tree is
	 * the first pending subgoal of the tree.
	 */
	@Test
	public void testBranchCloseFirst() throws CoreException {
		setCurrentPO("PO1");
		assertOpen(mList(), G);

		final ITactic tac = Tactics.doCase(P1.toString());
		userSupport.applyTactic(tac, false, null);
		assertOpen(mList(), btrue);

		// Clean up the WD lemma
		final ITactic review = Tactics.review(REVIEWED_MAX);
		userSupport.applyTactic(review, false, null);
		assertOpen(mList(P1), G);

		gotoNextSibling();
		assertOpen(mList(nP1), G);

		final ITactic tac2 = Tactics.doCase(P2.toString());
		userSupport.applyTactic(tac2, false, null);
		assertOpen(mList(nP1), btrue);
		userSupport.applyTactic(review, false, null);
		assertOpen(mList(nP1, P2), G);
		userSupport.applyTactic(review, false, null);
		assertOpen(mList(nP1, nP2), G);
		userSupport.applyTactic(review, false, null);
		assertOpen(mList(P1), G);
	}

}
