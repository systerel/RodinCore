package org.eventb.core.seqprover.eventbExtentionTests.mbGoal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.MembershipGoalImpl;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Rule;
import org.junit.Test;

/**
 * Acceptance tests for the common implementation of the Membership Goal
 * reasoner and tactic.
 * 
 * @author Laurent Voisin
 */
public class MembershipGoalImplTest extends AbstractMbGoalTests {

	private static class TestItem extends AbstractMbGoalTests.TestItem {

		private final MembershipGoalImpl impl;

		TestItem(String goalImage, String typenvImage, String... hypImages) {
			super(typenvImage, hypImages);
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

	@Test
	public void useless() {
		final TestItem it = new TestItem("x ∈ A", "x=ℤ, y=ℤ", "y ∈ A");
		it.assertNotFound();
	}

	@Test
	public void splitHyp() {
		final TestItem it = new TestItem("x ∈ A", "x=ℤ, y=ℤ", "x↦y ∈ A×B");
		it.assertFound(rf.domPrj(it.hyp("x↦y ∈ A×B")));
	}

	@Test
	public void mapletAsRelation() {
		final String hyp = "{x↦y} ∈ A ⇸ B";
		final TestItem it = new TestItem("x ∈ A", "x=ℤ, y=ℤ", hyp);
		it.assertFound(rf.domPrj(it.setExtMember("x↦y",
				rf.relToCprod(it.hyp(hyp)))));
	}

}
