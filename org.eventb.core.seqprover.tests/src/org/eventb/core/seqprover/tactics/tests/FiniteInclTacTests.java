/*******************************************************************************
 * Copyright (c) 2014, 2025 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tactics.tests;

import static org.eventb.core.seqprover.tactics.tests.TreeShape.conjI;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.empty;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.finiteInterShape;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.finiteSetMinusShape;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.finiteSetShape;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.finiteUnionShape;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.hyp;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.hypOr;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.trueGoal;
import static org.eventb.core.seqprover.tests.TestLib.genExpr;
import static org.eventb.core.seqprover.tests.TestLib.genFullSeq;
import static org.eventb.core.seqprover.tests.TestLib.mTypeEnvironment;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.junit.Test;

/**
 * Unit tests for the tactic FiniteInclusion
 * 
 * @author Josselin Dolhen
 */
public class FiniteInclTacTests extends AbstractTacticTests {

	public FiniteInclTacTests() {
		super(new AutoTactics.FiniteInclusionAutoTac(),
				"org.eventb.core.seqprover.finiteInclusionTac");
	}

	private static final String prefix = " ;H; ;S; A∈ℙ(ℤ) ;; B∈ℙ(ℤ) ;; ";

	/**
	 * Ensures that the tactic succeeds when applicable once.
	 */
	@Test
	public void simpleApplications() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("B=ℙ(ℤ)");
		final TreeShape expectedShape = finiteSetShape(genExpr(typeEnv, "B"),
				trueGoal(), hyp(), hyp());
		// works with Subseteq
		assertSuccess(prefix + "A⊆B ;; finite(B) |- finite(A)", //
				expectedShape);
		// works with Subset
		assertSuccess(prefix + "A⊂B ;; finite(B) |- finite(A)", //
				expectedShape);
		// works with Equality (1/2)
		assertSuccess(prefix + "A=B ;; finite(B) |- finite(A)", //
				expectedShape);
		// works with Equality (2/2)
		assertSuccess(prefix + "B=A ;; finite(B) |- finite(A)", //
				expectedShape);
		// works with set minus
		assertSuccess(prefix + "finite(A) |- finite(A ∖ B)", //
				finiteSetMinusShape(hyp()));
		// works with set intersection: left-hand-side (1/3)
		assertSuccess(prefix + "finite(B) |- finite(B ∩ A)", //
				finiteInterShape(hypOr()));
		// works with set intersection: right-hand-side (2/3)
		assertSuccess(prefix + "finite(B) |- finite(A ∩ B)", //
				finiteInterShape(hypOr()));
		// works with set intersection: associativity with many sets (3/3)
		assertSuccess(prefix + "C∈ℙ(ℤ) ;; finite(C) |- finite(A ∩ B ∩ C ∩ D ∩ E)", //
				finiteInterShape(hypOr()));
		// works with set union: two sets (1/2)
		assertSuccess(prefix + "finite(A) ;; finite(B) |- finite(A ∪ B)", //
				finiteUnionShape(conjI(hyp(), hyp())));
		// works with set union: associativity with many sets (2/2)
		assertSuccess(prefix + "finite(A) ;; finite(B) ;; finite(C) ;; finite(D) |- finite(A ∪ B ∪ C ∪ D)", //
				finiteUnionShape(conjI(hyp(), hyp(), hyp(), hyp())));
		// no free identifier
		assertSuccess(prefix + "C∈ℙ(ℤ) ;; A∩C⊆B ;; finite(B) |- finite(A∩C)",
				expectedShape);
		// Unselected hypotheses
		assertSuccess("A=ℙ(ℤ); B=ℙ(ℤ)", "A⊆B ;; finite(B)", "finite(A)",
				expectedShape);

		// No trivial WD
		final String typenv2Str = "B=ℙ(ℙ(ℤ))";
		final ITypeEnvironmentBuilder typenv2 = mTypeEnvironment(typenv2Str);
		final Expression interB = genExpr(typenv2, "inter(B)");
		assertSuccess(typenv2Str, "A⊆inter(B) ;; finite(inter(B))",
				"finite(A)", finiteSetShape(interB, empty, hyp(), hyp()));
	}

	/**
	 * Ensures that the tactic fails when it is not applicable.
	 */
	@Test
	public void notApplicable() {
		// Not a finite goal
		assertFailure(" ;H; ;S; A∈ℙ(ℤ) ;; B∈ℙ(ℤ) ;; B⊆A ;; finite(B) |- ⊤ ");
		// Nothing to do
		assertFailure(" ;H; ;S; A∈ℙ(ℤ) ;; B∈ℙ(ℤ) ;; B⊆A ;; finite(B) |- finite(A) ");
		// Set minus without right hypothesis
		assertFailure(" ;H; ;S; A∈ℙ(ℤ) ;; B∈ℙ(ℤ) ;; B⊆A ;; finite(B) |- finite(A ∖ B) ");
		// Set intersection without right hypothesis
		assertFailure(" ;H; ;S; A∈ℙ(ℤ) ;; B∈ℙ(ℤ) ;; B⊆A |- finite(A ∩ B) ");
		// Set union without right hypothesis
		assertFailure(" ;H; ;S; A∈ℙ(ℤ) ;; B∈ℙ(ℤ) ;; B⊆A ;; finite(B) |- finite(A ∪ B) ");
	}

	private void assertSuccess(String typeEnvImage, String defaultHypsImage,
			String goalImage, TreeShape expected) {
		final IProverSequent seq = genFullSeq(typeEnvImage, "",
				defaultHypsImage, "", goalImage);
		TacticTestUtils.assertSuccess(seq, expected, tactic);
	}

}
