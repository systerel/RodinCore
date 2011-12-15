/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.proofSimplifierTests;

import static org.eventb.core.seqprover.tactics.tests.TreeShape.conjF;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.conjI;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.disjE;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.falseHyp;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.hyp;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.impE;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.impI;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.mbg;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.rm;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.rn;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.trueGoal;

import java.util.Arrays;
import java.util.List;

import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.tactics.tests.TreeShape;
import org.junit.Assert;
import org.junit.runners.Parameterized.Parameters;

/**
 * Launch simplifier on proof trees that admit no simplification, check that it
 * produces the same proof tree, without any reordering.
 * 
 * @author Nicolas Beauger
 * 
 */
public class NoSimplificationTests extends AbstractSimplificationTests {
	
	private static Object[] test(String sequent, TreeShape shape) {
		return new Object[] { sequent, shape };
	}

	public NoSimplificationTests(String sequent, TreeShape shape) {
		super(sequent, shape, shape);
	}
	
	@Override
	protected void additionalChecks(IProofTree original, IProofTree simplified) {
		Assert.assertTrue(ProverLib.deepEquals(original, simplified));
	}
	
	@Parameters
	public static List<Object[]> getTestCases() throws Exception {
		return Arrays.<Object[]>asList(
				/////////////////
				// 1 node test //
				/////////////////
				/**
				 * Proof tree:
				 * 0
				 * Dependencies:
				 * {}
				 */
				test("|- ⊤",
						trueGoal()),
				
				//////////////////
				// 2 nodes test //
				//////////////////
				/**
				 * Proof tree:
				 * 0
				 * 1
				 * Dependencies:
				 * {0->1}
				 */
				test("¬¬x=0|- x=0",
						rn(p("¬¬x=0"), "",
								hyp())),
				
				///////////////////
				// 3 nodes tests //
				///////////////////
				/**
				 * Proof tree:
				 * 0
				 * 1
				 * 2
				 * Dependencies:
				 * {0->1, 0->2, 1->2}
				 */
				test("|- x=0 ⇒ ¬¬x=0",
						impI(
								rn("",
										hyp()))),
				
				/**
				 * Proof tree:
				 * 0
				 * 1
				 * 2
				 * Dependencies:
				 * {0->2, 1->2}
				 */
				test("¬¬x=0 |- x∈{0}",
						rm("",
								rn(p("¬¬x=0"),"",
										hyp()))),
				
				/**
				 * Proof tree:
				 * 0
				 * 1
				 * 2
				 * Dependencies:
				 * {0->1, 1->2}
				 */
				test("|- x=0 ⇒ (x=0 ⇒ ⊤)",
						impI(
								impI(
										trueGoal()))),
				
				/**
				 * Proof tree:
				 *  0
				 * 1 2
				 * Dependencies:
				 * {0->1, 0->2}
				 */
				test("x=0 ;; y=1 |- x=0 ∧ y=1", 
						conjI(
								hyp(),
								hyp())),
				
				///////////////////
				// 4 nodes tests //
				///////////////////
				/**
				 * Proof tree:
				 *  0
				 *  1
				 * 2 3
				 * Dependencies:
				 * {0->1, 1->2, 1->3}
				 */
				test("¬¬x=0 ⇒ y=0 ;; x=0 |- y=0",
						rn(p("¬¬x=0 ⇒ y=0"), "0",
								impE(p("x=0 ⇒ y=0"),
										hyp(),
										hyp()))),
				/**
				 * Proof tree:
				 *  0
				 *  1
				 * 2 3
				 * Dependencies:
				 * {0->2, 0->3, 1->2, 1->3}
				 */
				test("x=0 ∧ y=1 |- x=0 ∧ y=1",
						conjF(p("x=0 ∧ y=1"),
								conjI(
										hyp(),
										hyp()))),
				/**
				 * Proof tree:
				 *  0
				 * 1 2
				 *    3
				 * Dependencies:
				 * {0->1, 0->2, 2->3}
				 */
				test("|- ⊤∧¬¬⊤", 
						conjI(
								trueGoal(), 
								rn("", 
										trueGoal()))),
				
				///////////////////
				// 5 nodes tests //
				///////////////////
				/**
				 * Proof tree:
				 *   0
				 * 1   2
				 *    3 4
				 * Dependencies:
				 * {0->1, 0->2, 2->3, 2->4}
				 */
				test("x=0 ∨ (x=0 ∨ x=0) |- x=0",
						disjE(p("x=0 ∨ (x=0 ∨ x=0)"),
								hyp(),
								disjE(p("x=0 ∨ x=0"),
										hyp(),
										hyp()))),
				
				/**
				 * Proof tree:
				 *   0
				 * 1   2
				 *    3 4
				 * Dependencies:
				 * {0->1, 0->3, 2->3, 2->4}
				 * 2 could be applied first, but 
				 * the user chosen order must be preserved
				 */
				test("⊥ ∨ x∈{0,1} ;; ⊥ ∨ {0,1}⊆C |- x∈C",
						disjE(p("⊥ ∨ x∈{0,1}"),
								falseHyp(),
								disjE(p("⊥ ∨ {0,1}⊆C"),
										falseHyp(),
										mbg(p("x∈{0,1}","{0,1}⊆C")))))
										
		);
	}


}
