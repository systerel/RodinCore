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
package org.eventb.core.seqprover.tactics.tests;

import static org.eventb.core.seqprover.tactics.tests.TacticTestUtils.assertFailure;
import static org.eventb.core.seqprover.tactics.tests.TacticTestUtils.assertSuccess;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.funImgGoal;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.funImgInclusion;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.totalDom;
import static org.eventb.core.seqprover.tests.TestLib.genExpr;
import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.eventb.core.seqprover.tests.TestLib.genTypeEnv;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.junit.Test;

/**
 * Unit tests for the auto-tactic FunAppInDomGoalAutoTac
 * 
 * @author Emmanuel Billaud
 */
public class FunAppInDomGoalAutoTacTests {

	private static final ITactic tac = new AutoTactics.FunAppInDomGoalAutoTac();
	private static final String TAC_ID = "org.eventb.core.seqprover.funAppInDomGoalTac";

	/**
	 * Assert that auto tactic is registered.
	 */
	@Test
	public void assertRegistered() {
		final String[] tacticIds = { TAC_ID };
		final ITactic[] tactics = { tac };
		TacticTestUtils.assertTacticsRegistered(tacticIds, tactics);
	}

	/**
	 * Asserts that the auto-tactic succeeds on the following sequent :
	 * <ul>
	 * <li>H, f∈A *op1* B, g∈C *op2* D, B *incl* C ⊦ f(x)∈dom(g)</li>
	 * </ul>
	 * Where :
	 * <ul>
	 * <li><code>op1</code> is a relation or a function {@link FunAndRel}</li>
	 * <li><code>op2</code> is a total relation or a total function
	 * {@link TFunAndTRel}</li>
	 * <li><code>incl</code> is an inclusion {@link Inclusion}</li>
	 * </ul>
	 */
	@Test
	public void apply() {
		final ITypeEnvironment typeEnvStr = genTypeEnv("f=ℙ(ℤ×ℤ), g=ℙ(ℤ×ℤ), A=ℙ(ℤ), C=ℙ(ℤ), B=ℙ(ℤ), D=ℙ(ℤ), x=ℤ");
		final Expression substitue = genExpr(typeEnvStr, "C");
		final Predicate goal = genPred(typeEnvStr, "f(x)∈dom(g)");
		for (Inclusion incl : Inclusion.values()) {
			final Predicate inclu = genPred(typeEnvStr, "B" + incl.getImage()
					+ "C");
			for (FunAndRel far : FunAndRel.values()) {
				final Predicate f_TopS = genPred(typeEnvStr,
						"f∈A" + far.getImage() + "B");
				for (TFunAndTRel tfatr : TFunAndTRel.values()) {
					final Predicate g_TopS = genPred(typeEnvStr,
							"g∈C" + tfatr.getImage() + "D");
					assertSucceeded(goal, inclu, f_TopS, g_TopS, substitue,
							typeEnvStr);

				}
			}
		}
	}

	/**
	 * Asserts that the auto-tactic fails on the following sequent :
	 * <ul>
	 * <li>H, f∈A *op1* B, g∈C *op2* D, B *incl* C ⊦ f(x)∈dom(g)</li>
	 * </ul>
	 * Where :
	 * <ul>
	 * <li><code>op1</code> is a relation or a function {@link FunAndRel}</li>
	 * <li><code>op2</code> is a partial relation or not a total function
	 * {@link PFunAndPRel}</li>
	 * <li><code>inlc</code> is an inclusion {@link Inclusion}</li>
	 * </ul>
	 */
	@Test
	public void failsTypeRelation() {
		final ITypeEnvironment typeEnvStr = genTypeEnv("f=ℙ(ℤ×ℤ), g=ℙ(ℤ×ℤ), A=ℙ(ℤ), C=ℙ(ℤ), B=ℙ(ℤ), D=ℙ(ℤ), x=ℤ");
		final Expression substitue = genExpr(typeEnvStr, "C");
		final Predicate goal = genPred(typeEnvStr, "f(x)∈dom(g)");
		for (Inclusion incl : Inclusion.values()) {
			final Predicate inclu = genPred(typeEnvStr, "B" + incl.getImage()
					+ "C");
			for (FunAndRel far : FunAndRel.values()) {
				final Predicate f_TopS = genPred(typeEnvStr,
						"f∈A" + far.getImage() + "B");
				for (PFunAndPRel tfatr : PFunAndPRel.values()) {
					final Predicate g_TopS = genPred(typeEnvStr,
							"g∈C" + tfatr.getImage() + "D");
					assertFailed(goal, inclu, f_TopS, g_TopS, substitue,
							typeEnvStr);

				}
			}
		}
	}

	/**
	 * Asserts that the auto-tactic fails when :
	 * <ul>
	 * <li>the goal is not an inclusion</li>
	 * <li>right member of the goal is not a domain</li>
	 * <li>left member of the goal is not a function application</li>
	 * <li>there is not the needed inclusion as hypothesis 1/3</li>
	 * <li>there is not the needed inclusion as hypothesis 2/3</li>
	 * <li>there is not the needed inclusion as hypothesis 3/3</li>
	 * <li>definition of one the function is not an inclusion 1/2</li>
	 * <li>there is twice a definition of a function 1/2</li>
	 * <li>definition of one the function is not an inclusion 2/2</li>
	 * <li>there is twice a definition of a function 2/2</li>
	 * </ul>
	 */
	@Test
	public void failsOnHypothesis() {
		final ITypeEnvironment typeEnvStr = genTypeEnv("f=ℙ(ℤ×ℤ), g=ℙ(ℤ×ℤ), A=ℙ(ℤ), C=ℙ(ℤ), B=ℙ(ℤ), D=ℙ(ℤ), x=ℤ");
		final Expression substitue = genExpr(typeEnvStr, "C");
		final Predicate OK_goal = genPred(typeEnvStr, "f(x)∈dom(g)");
		final Predicate OK_inclu = genPred(typeEnvStr, "B⊆C");
		final Predicate OK_f_TopS = genPred(typeEnvStr, "f∈A↔B");
		final Predicate OK_g_TopS = genPred(typeEnvStr, "g∈C↣D");

		final Predicate ER1_goal = genPred(typeEnvStr, "f(x)∉dom(g)");
		assertFailed(ER1_goal, OK_inclu, OK_f_TopS, OK_g_TopS, substitue,
				typeEnvStr);
		final Predicate ER2_goal = genPred(typeEnvStr, "f(x)∈B");
		assertFailed(ER2_goal, OK_inclu, OK_f_TopS, OK_g_TopS, substitue,
				typeEnvStr);
		final Predicate ER3_goal = genPred(typeEnvStr, "x∈dom(g)");
		assertFailed(ER3_goal, OK_inclu, OK_f_TopS, OK_g_TopS, substitue,
				typeEnvStr);

		final Predicate ER1_inclu = genPred(typeEnvStr, "B=C");
		assertFailed(OK_goal, ER1_inclu, OK_f_TopS, OK_g_TopS, substitue,
				typeEnvStr);
		final Predicate ER2_inclu = genPred(typeEnvStr, "B⊆A");
		assertFailed(OK_goal, ER2_inclu, OK_f_TopS, OK_g_TopS, substitue,
				typeEnvStr);
		final Predicate ER3_inclu = genPred(typeEnvStr, "A⊆C");
		assertFailed(OK_goal, ER3_inclu, OK_f_TopS, OK_g_TopS, substitue,
				typeEnvStr);

		final Predicate ER1_f_TopS = genPred(typeEnvStr, "f∉A↔B");
		assertFailed(OK_goal, OK_inclu, ER1_f_TopS, OK_g_TopS, substitue,
				typeEnvStr);
		final Predicate ER2_f_TopS = genPred(typeEnvStr, "g∈A↔B");
		assertFailed(OK_goal, OK_inclu, ER2_f_TopS, OK_g_TopS, substitue,
				typeEnvStr);
		final Predicate ER1_g_TopS = genPred(typeEnvStr, "g∉C↣D");
		assertFailed(OK_goal, OK_inclu, OK_f_TopS, ER1_g_TopS, substitue,
				typeEnvStr);
		final Predicate ER2_g_TopS = genPred(typeEnvStr, "f∈C↣D");
		assertFailed(OK_goal, OK_inclu, OK_f_TopS, ER2_g_TopS, substitue,
				typeEnvStr);

	}

	/**
	 * Asserts that the application of the FunAppInDomGoalAutoTac on a node made
	 * up a goal and severals given hypothesis returns <code>null</code> and
	 * that the resulting tree shape is equal to <code>myTreeShape<code>
	 * 
	 * @param goal
	 *            the goal of the sequent
	 * @param inclu
	 *            the inclusion hypothesis
	 * @param f_hyp
	 *            the definition of the function f
	 * @param g_hyp
	 *            the definition of the function g
	 * @param substitute
	 *            the expression to substitute the domain
	 * @param typeEnv
	 *            the type environment of the sequent
	 */
	private void assertSucceeded(final Predicate goal, final Predicate inclu,
			final Predicate f_hyp, final Predicate g_hyp,
			final Expression substitute, final ITypeEnvironment typeEnv) {
		final Set<Predicate> hypotheses = new HashSet<Predicate>();
		hypotheses.add(inclu);
		hypotheses.add(f_hyp);
		hypotheses.add(g_hyp);
		final IProverSequent sequent = ProverFactory.makeSequent(typeEnv,
				hypotheses, goal);
		final IProofTree pt = ProverFactory.makeProofTree(sequent, null);
		assertSuccess(pt.getRoot(), myTreeShape(f_hyp, substitute), tac);
	}

	/**
	 * Asserts that the application of the FunAppInDomGoalAutoTac on a node made
	 * up a goal and severals given hypothesis does not return <code>null</code>
	 * 
	 * @param goal
	 *            the goal of the sequent
	 * @param inclu
	 *            the inclusion hypothesis
	 * @param f_hyp
	 *            the definition of the function f
	 * @param g_hyp
	 *            the definition of the function g
	 * @param substitute
	 *            the expression to substitute the domain
	 * @param typeEnv
	 *            the type environment of the sequent
	 */
	private void assertFailed(final Predicate goal, final Predicate inclu,
			final Predicate f_hyp, final Predicate g_hyp,
			final Expression substitute, final ITypeEnvironment typeEnv) {
		final Set<Predicate> hypotheses = new HashSet<Predicate>();
		hypotheses.add(inclu);
		hypotheses.add(f_hyp);
		hypotheses.add(g_hyp);
		final IProverSequent sequent = ProverFactory.makeSequent(typeEnv,
				hypotheses, goal);
		final IProofTree pt = ProverFactory.makeProofTree(sequent, null);
		assertFailure(pt.getRoot(), tac);
	}

	/**
	 * Returns a tree shape with the following tree structure :
	 * <ul>
	 * <li>funImgGoal : apply on the root of the goal with the hypothesis
	 * <code>hyp</code></li>
	 * <li>totalDom : apply on the goal, on the position "1", with the
	 * expression substitution <code>substitute</code></li>
	 * <li>funImgInclusionGoal</li>
	 * </ul>
	 * 
	 * @param hyp
	 *            the hypothesis used for the funImgGoal tree shape
	 * @param substitute
	 *            the expression used for the totalDom tree shape
	 * @return a tree shape with the following tree structure :
	 *         <ul>
	 *         <li>funImgGoal : apply on the root of the goal with the
	 *         hypothesis <code>hyp</code></li>
	 *         <li>totalDom : apply on the goal, on the position "1", with the
	 *         expression substitution <code>substitute</code></li>
	 *         <li>funImgInclusionGoal</li>
	 *         </ul>
	 */
	private TreeShape myTreeShape(Predicate hyp, Expression substitute) {
		return funImgGoal(hyp, "0",
				totalDom(null, "1", substitute, funImgInclusion()));
	}

	/**
	 * Enumeration of all the relation and function :
	 * <ul>
	 * <li>REL : "↔" (Relation)</li>
	 * <li>TREL : "" (Total Relation)</li>
	 * <li>SREL : "" (Surjective Relation)</li>
	 * <li>STREL : "" (Total Surjective Relation)</li>
	 * <li>PFUN : "⇸" (Partial Function)</li>
	 * <li>TFUN : "→" (Total Function)</li>
	 * <li>PINJ : "⤔" (Partial Injection)</li>
	 * <li>TINJ : "↣" (Total Injection)</li>
	 * <li>PSUR : "⤀" (Partial Surjection)</li>
	 * <li>TSUR : "↠" (Total Surjection)</li>
	 * <li>TBIJ : "⤖" (Bijection)</li>
	 * </ul>
	 * 
	 */
	private enum FunAndRel {
		REL("↔"), TREL(""), SREL(""), STREL(""), PFUN("⇸"), TFUN("→"), PINJ(
				"⤔"), TINJ("↣"), PSUR("⤀"), TSUR("↠"), TBIJ("⤖");
		private String image;

		FunAndRel(String image) {
			this.image = image;
		}

		String getImage() {
			return this.image;
		}
	}

	/**
	 * Enumeration of all the total function and total relation:
	 * <ul>
	 * <li>TREL : "" (Total Relation)</li>
	 * <li>STREL : "" (Total Surjective Relation)</li>
	 * <li>TFUN : "→" (Total Function)</li>
	 * <li>TINJ : "↣" (Total Injection)</li>
	 * <li>TSUR : "↠" (Total Surjection)</li>
	 * <li>TBIJ : "⤖" (Bijection)</li>
	 * </ul>
	 * 
	 */
	private enum TFunAndTRel {
		TREL(""), STREL(""), TFUN("→"), TINJ("↣"), TSUR("↠"), TBIJ("⤖");
		private String image;

		TFunAndTRel(String image) {
			this.image = image;
		}

		String getImage() {
			return this.image;
		}
	}

	/**
	 * Enumeration of all the partial function and not total relation :
	 * <ul>
	 * <li>REL : "↔" (Relation)</li>
	 * <li>SREL : "" (Surjective Relation)</li>
	 * <li>PFUN : "⇸" (Partial Function)</li>
	 * <li>PINJ : "⤔" (Partial Injection)</li>
	 * <li>PSUR : "⤀" (Partial Surjection)</li>
	 * </ul>
	 * 
	 */
	private enum PFunAndPRel {
		REL("↔"), SREL(""), PFUN("⇸"), PINJ("⤔"), PSUR("⤀");
		private String image;

		PFunAndPRel(String image) {
			this.image = image;
		}

		String getImage() {
			return this.image;
		}
	}

	/**
	 * Enumeration of two inclusions :
	 * <ul>
	 * <li>subseteq : "⊆"</li>
	 * <li>subset : "⊂"</li>
	 * </ul>
	 * 
	 */
	private enum Inclusion {
		subseteq("⊆"), subset("⊂");
		private String image;

		Inclusion(String image) {
			this.image = image;
		}

		String getImage() {
			return this.image;
		}
	}

}