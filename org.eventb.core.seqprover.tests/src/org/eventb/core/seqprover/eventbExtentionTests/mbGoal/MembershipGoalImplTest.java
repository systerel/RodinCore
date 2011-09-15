package org.eventb.core.seqprover.eventbExtentionTests.mbGoal;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Expression;
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

		TestItem(String memberImage, String setImage, String typenvImage,
				String... hypImages) {
			this.typenv = TestLib.genTypeEnv(typenvImage);
			final List<Predicate> hyps = new ArrayList<Predicate>();
			for (String hypImage : hypImages) {
				hyps.add(TestLib.genPred(typenv, hypImage));
			}
			final Expression member = TestLib.genExpr(typenv, memberImage);
			final Expression set = TestLib.genExpr(typenv, setImage);
			this.impl = new MembershipGoalImpl(member, set, hyps, ff);
		}

		public void assertFound(String memberImage, String setImage,
				Rule<?> expected) {
			final Expression member = TestLib.genExpr(typenv, memberImage);
			final Expression set = TestLib.genExpr(typenv, setImage);
			final Rule<?> actual = impl.search(member, set);
			assertEquals(expected, actual);
		}

		public Rule<?> hyp(String hypImage) {
			final Predicate hyp = TestLib.genPred(typenv, hypImage);
			return rf.hypothesis(hyp);
		}

	}

	@Test
	public void minimal() {
		final TestItem it = new TestItem("x", "A", "x=ℤ", "x ∈ A");
		it.assertFound("x", "A", it.hyp("x ∈ A"));
	}

	@Test
	public void oneHop() {
		final TestItem it = new TestItem("x", "B", "x=ℤ", "x ∈ A", "A ⊆ B");
		it.assertFound("x", "B", rf.compose(it.hyp("x ∈ A"), it.hyp("A ⊆ B")));
	}

	@Test
	public void twoHops() {
		final TestItem it = new TestItem("x", "C", "x=ℤ", "x ∈ A", "A ⊆ B",
				"B ⊂ C");
		it.assertFound(
				"x",
				"C",
				rf.compose(rf.compose(it.hyp("x ∈ A"), it.hyp("A ⊆ B")),
						it.hyp("B ⊂ C")));
	}

	@Test
	public void backtrack() {
		final TestItem it = new TestItem("x", "B", "x=ℤ, C=ℙ(ℤ)", "x ∈ A",
				"C ⊆ B", "A ⊆ B");
		it.assertFound("x", "B", rf.compose(it.hyp("x ∈ A"), it.hyp("A ⊆ B")));
	}
}
