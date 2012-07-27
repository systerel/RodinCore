/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tactics.tests;

import static org.eventb.core.seqprover.tactics.tests.TreeShape.funImgGoal;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.mbg;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.totalDom;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Inclusion;
import org.junit.Test;

/**
 * Unit tests for the auto-tactic FunAppInDomGoalAutoTac
 * 
 * @author Emmanuel Billaud
 */
public class FunAppInDomGoalAutoTacTests extends AbstractTacticTests {

	public FunAppInDomGoalAutoTacTests() {
		super(new AutoTactics.FunAppInDomGoalAutoTac(),
				"org.eventb.core.seqprover.funAppInDomGoalTac");
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
		addToTypeEnvironment("f=ℙ(ℤ×ℤ), g=ℙ(ℤ×ℤ), A=ℙ(ℤ), C=ℙ(ℤ), B=ℙ(ℤ), D=ℙ(ℤ), x=ℤ");
		final Predicate inclu = parsePredicate("B⊆C");
		final Predicate member = parsePredicate("f(x)∈B");
		final Predicate[] input = new Predicate[] { inclu, member };
		final Predicate goal = parsePredicate("f(x)∈dom(g)");
		final Expression substitute = parseExpression("C");
		for (FunAndRel far : FunAndRel.values()) {
			final String fPredStr = "f∈A" + far.getImage() + "B";
			final Predicate fPred = parsePredicate(fPredStr);
			for (TFunAndTRel tfatr : TFunAndTRel.values()) {
				final String gPredStr = "g∈C" + tfatr.getImage() + "D";
				final String sequent = " ;H; ;S; " + inclu + ";;" + fPredStr
						+ ";;" + gPredStr + "|-" + goal;
				assertSuccess(
						sequent, //
						funImgGoal(fPred, "0",
								totalDom(null, "1", substitute, mbg(input))));
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
		addToTypeEnvironment("f=ℙ(ℤ×ℤ), g=ℙ(ℤ×ℤ), A=ℙ(ℤ), C=ℙ(ℤ), B=ℙ(ℤ), D=ℙ(ℤ), x=ℤ");
		for (FunAndRel far : FunAndRel.values()) {
			final String fPredStr = "f∈A" + far.getImage() + "B";
			for (PFunAndPRel tfatr : PFunAndPRel.values()) {
				final String gPredStr = "g∈C" + tfatr.getImage() + "D";
				assertFailure(" ;H; ;S; B⊆C ;; " + fPredStr + " ;; "
						+ gPredStr + " |- f(x)∈dom(g)");

			}
		}
	}

	/**
	 * Asserts that the auto-tactic fails when :
	 * <ul>
	 * <li>the goal is not an inclusion</li>
	 * <li>right member of the goal is not a domain</li>
	 * <li>left member of the goal is not a function application</li>
	 * </ul>
	 */
	@Test
	public void failsOnGoals() {
		addToTypeEnvironment("f=ℙ(ℤ×ℤ), g=ℙ(ℤ×ℤ), A=ℙ(ℤ), C=ℙ(ℤ), B=ℙ(ℤ), D=ℙ(ℤ), x=ℤ");
		final String[] errorGoals = new String[] { //
		"f(x)∉dom(g)", "f(x)∈B", "x∈dom(g)" };
		for (String erGoal : errorGoals) {
			final String sequent = " ;H; ;S; B⊆C ;; f∈A↔B ;; g∈C↣D |-" + erGoal;
			assertFailure(sequent);
		}
	}
	
	/**
	 * Asserts that the auto-tactic fails when :
	 * <ul>
	 * <li>there is not the needed inclusion as hypothesis</li>
	 * <li>definition of one the function f is not an inclusion or there is
	 * twice a definition of g</li>
	 * <li>definition of one the function g is not an inclusion or there is
	 * twice a definition of f</li>
	 * </ul>
	 */
	@Test
	public void failsOnHypothesis() {
		addToTypeEnvironment("f=ℙ(ℤ×ℤ), g=ℙ(ℤ×ℤ), A=ℙ(ℤ), C=ℙ(ℤ), B=ℙ(ℤ), D=ℙ(ℤ), x=ℤ");

		final String[] errorInclus = new String[] { "B⊆A", "A⊆C" };
		for (String erInclu : errorInclus) {
			final String sequent = " ;H; ;S; " + erInclu
					+ " ;; f∈A↔B ;; g∈C↣D |- f(x)∈dom(g)";
			assertFailure(sequent);
		}
		
		final String[] errorFPreds = new String[] { "f∉A↔B", "g∈A↔B" };
		for (String erFPred : errorFPreds) {
			final String sequent = " ;H; ;S; B⊆C ;; " + erFPred
					+ " ;; g∈C↣D |- f(x)∈dom(g)";
			assertFailure(sequent);
		}
		
		final String[] errorGPreds = new String[] { "g∉C↣D", "f∈C↣D" };
		for (String erGPred : errorGPreds) {
			final String sequent = " ;H; ;S; B⊆C ;; f∈A↔B ;; " + erGPred
					+ "|- f(x)∈dom(g)";
			assertFailure(sequent);
		}

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

}