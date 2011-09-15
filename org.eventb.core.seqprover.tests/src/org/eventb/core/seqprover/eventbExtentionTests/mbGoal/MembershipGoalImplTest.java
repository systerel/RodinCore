package org.eventb.core.seqprover.eventbExtentionTests.mbGoal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.MembershipGoalImpl;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.MembershipGoalRules;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Rule;
import org.junit.Test;

public class MembershipGoalImplTest {

	private static final FormulaFactory ff = FormulaFactory.getDefault();

	private static final MembershipGoalRules rf = new MembershipGoalRules(ff);

	private static class TestItem {
		private final ITypeEnvironment typenv;
		private final MembershipGoalImpl impl;

		TestItem(String goalImage, String typenvImage, String... hypImages) {
			this.typenv = TestLib.genTypeEnv(typenvImage);
			final Set<Predicate> hyps = new LinkedHashSet<Predicate>();
			for (String hypImage : hypImages) {
				hyps.add(TestLib.genPred(typenv, hypImage));
			}
			final Predicate goal = TestLib.genPred(typenv, goalImage);
			this.impl = new MembershipGoalImpl(goal, hyps, ff);
		}

		public void assertFound(Rule<?> expected) {
			final Rule<?> actual = impl.search();
			assertNotNull(actual);
			assertTrue(impl.verify(actual));
			assertEquals(expected, actual);
		}

		public void assertNotFound() {
			final Rule<?> actual = impl.search();
			assertNull(actual);
		}

		public Rule<?> hyp(String hypImage) {
			final Predicate hyp = TestLib.genPred(typenv, hypImage);
			return rf.hypothesis(hyp);
		}

	}

	@Test
	public void minimal() {
		final TestItem it = new TestItem("x ∈ A", "x=ℤ", "x ∈ A");
		it.assertFound(it.hyp("x ∈ A"));
	}

	@Test
	public void oneHop() {
		final TestItem it = new TestItem("x ∈ B", "x=ℤ", "x ∈ A", "A ⊆ B");
		it.assertFound(rf.compose(it.hyp("x ∈ A"), it.hyp("A ⊆ B")));
	}

	@Test
	public void twoHops() {
		final TestItem it = new TestItem("x ∈ C", "x=ℤ", "x ∈ A", "A ⊆ B",
				"B ⊂ C");
		it.assertFound(rf.compose(rf.compose(it.hyp("x ∈ A"), it.hyp("A ⊆ B")),
				it.hyp("B ⊂ C")));
	}

	@Test
	public void backtrack() {
		final TestItem it = new TestItem("x ∈ B", "x=ℤ, C=ℙ(ℤ)", "x ∈ A",
				"C ⊆ B", "A ⊆ B");
		it.assertFound(rf.compose(it.hyp("x ∈ A"), it.hyp("A ⊆ B")));
	}

	@Test
	public void mightLoop() {
		final TestItem it = new TestItem("x ∈ B", "x=ℤ, B=ℙ(ℤ)", "x ∈ A",
				"B ⊆ B", "A ⊆ B");
		it.assertFound(rf.compose(it.hyp("x ∈ A"), it.hyp("A ⊆ B")));
	}

	@Test
	public void mightLoopLong() {
		final TestItem it = new TestItem("x ∈ B", "x=ℤ, B=ℙ(ℤ)", "x ∈ A",
				"C ⊆ B", "B ⊆ C", "A ⊆ B");
		it.assertFound(rf.compose(it.hyp("x ∈ A"), it.hyp("A ⊆ B")));
	}

	@Test
	public void notFound() {
		final TestItem it = new TestItem("x ∈ A", "x=ℤ");
		it.assertNotFound();
	}

	@Test
	public void notFoundBacktrack() {
		final TestItem it = new TestItem("x ∈ A", "x=ℤ, A=ℙ(ℤ)", "B ⊆ A",
				"C ⊆ B", "D ⊆ A");
		it.assertNotFound();
	}

}
