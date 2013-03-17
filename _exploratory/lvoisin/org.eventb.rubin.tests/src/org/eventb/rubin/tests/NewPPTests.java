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
import static org.eventb.rubin.problems.Chapter11.Example_11_D8;
import static org.eventb.rubin.problems.Chapter11.Example_11_E17;
import static org.eventb.rubin.problems.Chapter11.Example_11_E7;
import static org.eventb.rubin.problems.Others.Animals;
import static org.eventb.rubin.problems.Others.AnimalsPlus;

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
 * Validation tests of newPP based on several examples from Rubin's book and
 * others.
 * 
 * @author Laurent Voisin
 */
public class NewPPTests extends AbstractPPTests {

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
		testChapter(Chapter11.values(),//
				Example_11_D8,//
				Example_11_E7,//
				Example_11_E17 //
		);
	}

	@Test
	public void testOthers() throws Exception {
		testChapter(Others.values(),//
				Animals,//
				AnimalsPlus //
		);
	}

	private void testChapter(IProblem problems[], IProblem... ignore) {
		final Set<IProblem> skip = new HashSet<IProblem>(asList(ignore));
		final IProblemFilter filter = new IProblemFilter() {
			@Override
			public IProblem filter(IProblem problem) {
				if (skip.contains(problem)) {
					return null;
				}
				return problem;
			}
		};
		testProblems(filter, 400, problems);
	}

}
