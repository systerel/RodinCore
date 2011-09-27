package org.eventb.core.seqprover.eventbExtentionTests.mbGoal;

import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.Predicate;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.MembershipGoalImpl;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Rationale;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Rule;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Acceptance tests for the common implementation of the Membership Goal
 * reasoner and tactic.
 * 
 * @author Laurent Voisin
 */
public class MembershipGoalImplTest extends AbstractMbGoalTests {

	private static class TestItem extends AbstractMbGoalTests.TestItem {

		private final Predicate goal;
		private final MembershipGoalImpl impl;

		TestItem(String goalImage, String typenvImage, String... hypImages) {
			super(typenvImage, hypImages);
			this.goal = genPred(typenv, goalImage);
			this.impl = new MembershipGoalImpl(goal, hyps, ff, pm);
		}

		public void assertFound(Rule<?> expected) {
			// Ensure expected is consistent
			assertEquals(goal, expected.getConsequent());

			final Rationale rat = impl.search();
			assertNotNull("Should have found a proof", rat);
			final Rule<?> actual = rat.makeRule();
			assertTrue(impl.verify(actual));
			assertEquals(expected, actual);
		}

		public void assertNotFound() {
			final Rationale actual = impl.search();
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
		it.assertFound(rf.domPrjS(it.hyp("x↦y ∈ A×B")));
	}

	@Test
	public void mapletAsRelation() {
		final String hyp = "{x↦y} ∈ A ⇸ B";
		final TestItem it = new TestItem("x ∈ A", "x=ℤ, y=ℤ", hyp);
		it.assertFound(rf.domPrjS(it.setExtMember("x↦y",
				rf.relToCprod(it.hyp(hyp)))));
	}

	/**
	 * Ensures that a set membership can be extracted from the relational
	 * property of an overriding.
	 */
	@Test
	public void mapletOverride() {
		final TestItem it = new TestItem("x ∈ A", "x=ℤ, y=ℤ",
				"f  {x↦y} ∈ A ⤔ B");
		it.assertFound(rf.domPrjS(it.setExtMember("x↦y",
				rf.lastOvr(rf.relToCprod(it.hyp("f  {x↦y} ∈ A ⤔ B"))))));
	}

	/**
	 * Ensures that membership to a relational set is not lost when deriving
	 * fancy hypotheses.
	 */
	@Test
	public void inRelationSet() {
		final String hyp = "{x↦y} ∈ A ⇸ B";
		final TestItem it = new TestItem("{x↦y} ∈ S", "x=ℤ, y=ℤ", hyp,
				"A⇸B ⊆ S");
		it.assertFound(rf.compose(it.hyp(hyp), it.hyp("A⇸B ⊆ S")));
	}

	/**
	 * Ensures that useful inclusions can be derived from several form of
	 * hypothesis.
	 */
	@Test
	public void derivedInclusion() {
		final TestItem it = new TestItem("x ∈ D", "x=ℤ×ℤ",//
				"x ∈ A", "A ∈ B ⤖ C", "B×C = D");
		it.assertFound(rf.compose(
				rf.compose(it.hyp("x ∈ A"), rf.relToCprod(it.hyp("A ∈ B ⤖ C"))),
				rf.eqToSubset(true, it.hyp("B×C = D"))));
	}

	/**
	 * Ensures that a cartesian product can be used in intermediary inclusion.
	 */
	@Test
	public void cprodOnPath() {
		final TestItem it = new TestItem("x ∈ dom(f)", "x=ℤ, y=ℤ",//
				"x↦y ∈ A×B", "A×B ⊆ f");
		it.assertFound(rf.compose(rf.domPrj(it.hyp("x↦y ∈ A×B")),
				rf.domPrj(it.hyp("A×B ⊆ f"))));
	}

	/**
	 * Ensures that a function domain is correctly inferred.
	 */
	@Test
	@Ignore("Not yet implemented")
	public void funDomain() {
		final TestItem it = new TestItem("x ∈ A", "x=ℤ, y=ℤ",//
				"x↦y ∈ f", "f ∈ A ⤖ B");
		it.assertFound(rf.compose(rf.domPrj(it.hyp("x↦y ∈ f")),
				rf.domPrj(rf.relToCprod(it.hyp("f ∈ A ⤖ B")))));
	}

}
