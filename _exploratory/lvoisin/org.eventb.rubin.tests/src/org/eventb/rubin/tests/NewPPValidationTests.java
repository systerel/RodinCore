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
import static org.eventb.rubin.problems.Chapter02.*;
import static org.eventb.rubin.problems.Chapter03.*;
import static org.eventb.rubin.problems.Chapter08.*;
import static org.eventb.rubin.problems.Chapter11.*;
import static org.eventb.rubin.tests.ProblemStatus.INVALID;

import java.util.HashSet;
import java.util.Set;

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

/**
 * Run the new predicate prover on invalid variants of known valid sequents.
 * This demonstrates that the predicate prover is not seriously unsound.
 * 
 * The problems that are not tested are the one that contain a contradiction
 * among their hypotheses, because we cannot easily derive an invalid problem
 * from them.
 * 
 * @author Laurent Voisin
 */
public class NewPPValidationTests extends AbstractPPTests {

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
		testChapter(Chapter02.values(),//
				Exercise_2_H10,//
				Exercise_2_H11//
		);
	}

	/**
	 * Runs the new predicate prover on a set of sequents taken from chapter 3
	 * of Jean E. Rubin's book.
	 */
	@Test
	public void testChapter03() throws Exception {
		testChapter(Chapter03.values(),//
				Example_3_1,//
				Example_3_2a,//
				Example_3_2c,//
				Exercise_3_A1,//
				Exercise_3_A5,//
				Exercise_3_A7,//
				Exercise_3_A8,//
				Exercise_3_A17//
		);
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
		testChapter(Chapter08.values(),//
				Exercise_8_A20_simp//
		);
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
		testChapter(Chapter11.values(),//
				Example_11_2,//
				Example_11_D2,//
				Example_11_D3,//
				Example_11_D5,//
				Example_11_D8,//
				Example_11_D13,//
				Example_11_D14//
		);
	}

	@Test
	public void testOthers() throws Exception {
		testChapter(Others.values());
	}

	private void testChapter(IProblem problems[], IProblem... ignore) {
		final Set<IProblem> skip = new HashSet<IProblem>(asList(ignore));
		for (final IProblem problem : problems) {
			if (problem.status() != INVALID && !skip.contains(problem)) {
				testProblem(5000, problem.invalidVariant());
			}
		}
	}

}
