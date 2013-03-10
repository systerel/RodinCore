/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - remove formula files
 *******************************************************************************/
package org.eventb.rubin.tests;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.rubin.Sequent;
import org.eventb.rubin.problems.Chapter01;
import org.eventb.rubin.problems.Chapter02;
import org.eventb.rubin.problems.Chapter03;
import org.eventb.rubin.problems.Chapter07;
import org.eventb.rubin.problems.Chapter08;
import org.eventb.rubin.problems.Chapter09;
import org.eventb.rubin.problems.Chapter10;
import org.eventb.rubin.problems.Chapter11;
import org.eventb.rubin.problems.Others;
import org.junit.Test;

import com.clearsy.atelierb.provers.core.AtbProversCore;

public class OldPPTests extends AbstractPPTests {
	
	private static final long PP_DELAY = 800; 
	
	/**
	 * Runs the new predicate prover on a set of sequents taken from chapter 1
	 * of Jean E. Rubin's book.
	 */
	@Test
	public void testChapter01() throws Exception {
		testChapter(Chapter01.values());
	}

	/**
	 * Runs the new predicate prover on a set of sequents taken from chapter 2
	 * of Jean E. Rubin's book.
	 */
	@Test
	public void testChapter02() throws Exception {
		testChapter(Chapter02.values());
	}

	/**
	 * Runs the new predicate prover on a set of sequents taken from chapter 3
	 * of Jean E. Rubin's book.
	 */
	@Test
	public void testChapter03() throws Exception {
		testChapter(Chapter03.values());
	}

	/**
	 * Runs the new predicate prover on a set of sequents taken from chapter 7
	 * of Jean E. Rubin's book.
	 */
	@Test
	public void testChapter07() throws Exception {
		testChapter(Chapter07.values());
	}

	/**
	 * Runs the new predicate prover on a set of sequents taken from chapter 8
	 * of Jean E. Rubin's book.
	 */
	@Test
	public void testChapter08() throws Exception {
		testChapter(Chapter08.values());
	}

	/**
	 * Runs the new predicate prover on a set of sequents taken from chapter 9
	 * of Jean E. Rubin's book.
	 */
	@Test
	public void testChapter09() throws Exception {
		testChapter(Chapter09.values());
	}

	/**
	 * Runs the new predicate prover on a set of sequents taken from chapter 10
	 * of Jean E. Rubin's book.
	 */
	@Test
	public void testChapter10() throws Exception {
		testChapter(Chapter10.values());
	}

	/**
	 * Runs the new predicate prover on a set of sequents taken from chapter 11
	 * of Jean E. Rubin's book.
	 */
	@Test
	public void testChapter11() throws Exception {
		testChapter(Chapter11.values());
	}

	@Test
	public void testOthers() throws Exception {
		testChapter(Others.values());
	}
	
	private void testChapter(IProblem problems[], IProblem... ignore) {
		final Set<IProblem> skip = new HashSet<IProblem>(asList(ignore));
		for (final IProblem problem : problems) {
			if (!skip.contains(problem)) {
				testProblem(problem);
			}
		}
	}
	
	protected void testProblem(IProblem problem) {
		final Sequent sequent = problem.sequent();
		final String name = sequent.getName();
		
		// Translate to a prover sequent
		final ITypeEnvironment typenv = typeCheck(sequent);
		final IProverSequent ps = ProverFactory.makeSequent(
				typenv,
				new HashSet<Predicate>(Arrays.asList(sequent.getHypotheses())),
				sequent.getGoal()
		);

		// Create the tactic and apply PP
		final ITactic tactic = AtbProversCore.externalPP(false, PP_DELAY);
		final IProofTree tree = ProverFactory.makeProofTree(ps,null);
		tactic.apply(tree.getRoot(), null);

		switch (problem.status()) {
		case VALID:
			assertTrue("PP failed unexpectedly on sequent " + sequent, tree.isClosed());
			break;
		case INVALID:
		case VALIDPPFAILS:
			assertFalse("PP succeeded unexpectedly on sequent" + sequent, tree.isClosed());
			break;
		default:
			fail("Invalid status for problem " + name);
			break;
		}
	}

}
