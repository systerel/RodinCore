package org.eventb.core.seqprover.eventbExtentionTests.mbGoal;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Membership;
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
			final List<Predicate> hyps = new ArrayList<Predicate>();
			for (String hypImage : hypImages) {
				hyps.add(TestLib.genPred(typenv, hypImage));
			}
			final Predicate goal = TestLib.genPred(typenv, goalImage);
			this.impl = new MembershipGoalImpl(goal, hyps, ff);
		}

		public void assertFound(String goalImage, Rule<?> expected) {
			final Predicate goal = TestLib.genPred(typenv, goalImage);
			final Membership ms = new Membership(goal);
			final Rule<?> actual = impl.search(ms);
			assertEquals(expected, actual);
		}

		public Rule<?> hyp(String hypImage) {
			final Predicate hyp = TestLib.genPred(typenv, hypImage);
			return rf.hypothesis(hyp);
		}

	}

	@Test
	public void minimal() {
		final TestItem it = new TestItem("x ∈ A", "x=ℤ", "x ∈ A");
		it.assertFound("x ∈ A", it.hyp("x ∈ A"));
	}

	@Test
	public void oneHop() {
		final TestItem it = new TestItem("x ∈ B", "x=ℤ", "x ∈ A", "A ⊆ B");
		it.assertFound("x ∈ B", rf.compose(it.hyp("x ∈ A"), it.hyp("A ⊆ B")));
	}

	@Test
	public void twoHops() {
		final TestItem it = new TestItem("x ∈ C", "x=ℤ", "x ∈ A", "A ⊆ B",
				"B ⊂ C");
		it.assertFound(
				"x ∈ C",
				rf.compose(rf.compose(it.hyp("x ∈ A"), it.hyp("A ⊆ B")),
						it.hyp("B ⊂ C")));
	}

	@Test
	public void backtrack() {
		final TestItem it = new TestItem("x ∈ B", "x=ℤ, C=ℙ(ℤ)", "x ∈ A",
				"C ⊆ B", "A ⊆ B");
		it.assertFound("x ∈ B", rf.compose(it.hyp("x ∈ A"), it.hyp("A ⊆ B")));
	}

	@Test
	public void mightLoop() {
		final TestItem it = new TestItem("x ∈ B", "x=ℤ, B=ℙ(ℤ)", "x ∈ A",
				"B ⊆ B", "A ⊆ B");
		it.assertFound("x ∈ B", rf.compose(it.hyp("x ∈ A"), it.hyp("A ⊆ B")));
	}

	@Test
	public void mightLoopLong() {
		final TestItem it = new TestItem("x ∈ B", "x=ℤ, B=ℙ(ℤ)", "x ∈ A",
				"C ⊆ B", "B ⊆ C", "A ⊆ B");
		it.assertFound("x ∈ B", rf.compose(it.hyp("x ∈ A"), it.hyp("A ⊆ B")));
	}

}
