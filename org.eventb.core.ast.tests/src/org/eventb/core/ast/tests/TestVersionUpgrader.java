/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.Formula.DOMRES;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.KID_GEN;
import static org.eventb.core.ast.Formula.KPRJ1_GEN;
import static org.eventb.core.ast.Formula.KPRJ2_GEN;
import static org.eventb.core.ast.Formula.NATURAL;
import static org.eventb.core.ast.Formula.PFUN;
import static org.eventb.core.ast.Formula.PINJ;
import static org.eventb.core.ast.Formula.PSUR;
import static org.eventb.core.ast.Formula.REL;
import static org.eventb.core.ast.Formula.SREL;
import static org.eventb.core.ast.Formula.STREL;
import static org.eventb.core.ast.Formula.TBIJ;
import static org.eventb.core.ast.Formula.TFUN;
import static org.eventb.core.ast.Formula.TINJ;
import static org.eventb.core.ast.Formula.TREL;
import static org.eventb.core.ast.Formula.TSUR;
import static org.eventb.core.ast.FormulaFactory.isEventBWhiteSpace;
import static org.eventb.core.ast.LanguageVersion.V2;
import static org.eventb.core.ast.ProblemKind.NotUpgradableError;
import static org.eventb.core.ast.ProblemKind.UnexpectedLPARInDeclList;
import static org.eventb.core.ast.ProblemSeverities.Error;
import static org.eventb.core.ast.tests.FastFactory.mAtomicExpression;
import static org.eventb.core.ast.tests.FastFactory.mBecomesEqualTo;
import static org.eventb.core.ast.tests.FastFactory.mBecomesMemberOf;
import static org.eventb.core.ast.tests.FastFactory.mBecomesSuchThat;
import static org.eventb.core.ast.tests.FastFactory.mBinaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentDecl;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedPredicate;
import static org.eventb.core.ast.tests.FastFactory.mRelationalPredicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IUpgradeResult;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SourceLocation;

/**
 * Acceptance tests for upgrading from mathematical language V1 to V2.
 * 
 * @author Nicolas Beauger
 */
public class TestVersionUpgrader extends AbstractTests {

	private static class UpgradeResultChecker<T extends Formula<T>> {

		private static final List<ASTProblem> NO_PROBLEM = Collections
				.emptyList();

		private final List<ASTProblem> problems;
		private final T upgradedFormula;
		private final boolean upgradeNeeded;

		public UpgradeResultChecker(T upgradedFormula, boolean upgradeNeeded) {
			super();
			this.problems = NO_PROBLEM;
			this.upgradedFormula = upgradedFormula;
			this.upgradeNeeded = upgradeNeeded;
		}

		public UpgradeResultChecker(ASTProblem... problems) {
			super();
			this.problems = Arrays.asList(problems);
			this.upgradedFormula = null;
			this.upgradeNeeded = true;
		}

		public void verify(IUpgradeResult<T> actual) {
			assertEquals("Unexpected problems", problems, actual.getProblems());
			assertEquals("Unexpected upgradeNeeded", upgradeNeeded, actual
					.upgradeNeeded());
			final T actualFormula = actual.getUpgradedFormula();
			assertEquals("Unexpected upgraded formula", upgradedFormula,
					actualFormula);
		}

	}

	private static abstract class TestItem<T extends Formula<T>> {
		protected final String input;
		protected final LanguageVersion targetVersion;
		private final UpgradeResultChecker<T> checker;

		public TestItem(String beforeUpgrade, LanguageVersion targetVersion,
				UpgradeResultChecker<T> checker) {
			this.input = beforeUpgrade;
			this.targetVersion = targetVersion;
			this.checker = checker;
		}

		public void verifyUpgrade() {
			checker.verify(upgrade());
		}

		protected abstract IUpgradeResult<T> upgrade();
	}

	private static class TestItemAssign extends TestItem<Assignment> {
		public TestItemAssign(String beforeUpgrade,
				LanguageVersion targetVersion,
				UpgradeResultChecker<Assignment> checker) {
			super(beforeUpgrade, targetVersion, checker);
		}

		@Override
		protected IUpgradeResult<Assignment> upgrade() {
			return ff.upgradeAssignment(input, targetVersion);
		}
	}

	private static class TestItemExpr extends TestItem<Expression> {
		public TestItemExpr(String beforeUpgrade,
				LanguageVersion targetVersion,
				UpgradeResultChecker<Expression> checker) {
			super(beforeUpgrade, targetVersion, checker);
		}

		@Override
		protected IUpgradeResult<Expression> upgrade() {
			return ff.upgradeExpression(input, targetVersion);
		}
	}

	private static class TestItemPred extends TestItem<Predicate> {
		public TestItemPred(String beforeUpgrade,
				LanguageVersion targetVersion,
				UpgradeResultChecker<Predicate> checker) {
			super(beforeUpgrade, targetVersion, checker);
		}

		@Override
		protected IUpgradeResult<Predicate> upgrade() {
			return ff.upgradePredicate(input, targetVersion);
		}
	}

	private static enum SetOpImageTags {
		eREL("\u2194", REL), eTREL("\ue100", TREL), eSREL("\ue101", SREL), eSTREL(
				"\ue102", STREL), ePFUN("\u21f8", PFUN), eTFUN("\u2192", TFUN), ePINJ(
				"\u2914", PINJ), eTINJ("\u21a3", TINJ), ePSUR("\u2900", PSUR), eTSUR(
				"\u21a0", TSUR), eTBIJ("\u2916", TBIJ), ;

		private final String image;
		private final int tag;

		SetOpImageTags(String image, int tag) {
			this.image = image;
			this.tag = tag;
		}

		public String getImage() {
			return image;
		}

		public int getTag() {
			return tag;
		}

	}

	private static class ParenExpMaker {
		private final TestVersionUpgrader.SetOpImageTags setOp;
		private final String x;
		private final String y;
		private final String z;

		public ParenExpMaker(TestVersionUpgrader.SetOpImageTags setOp) {
			this(setOp, "x", "y", "z");
		}

		public ParenExpMaker(TestVersionUpgrader.SetOpImageTags setOp,
				String x, String y, String z) {
			this.setOp = setOp;
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public String makeNoParen() {
			final StringBuilder sb = new StringBuilder();
			sb.append(x);
			sb.append(setOp.getImage());
			sb.append(y);
			sb.append(setOp.getImage());
			sb.append(z);

			return sb.toString();
		}
	}

	private static <T extends Formula<T>> UpgradeResultChecker<T> makeResultGen(
			T upgradedFormula, boolean upgradeNeeded) {
		return new UpgradeResultChecker<T>(upgradedFormula, upgradeNeeded);
	}

	private static UpgradeResultChecker<Expression> makeResult(
			Expression upgradedFormula, boolean upgradeNeeded) {
		return makeResultGen(upgradedFormula, upgradeNeeded);
	}

	private static UpgradeResultChecker<Predicate> makeResult(
			Predicate upgradedFormula, boolean upgradeNeeded) {
		return makeResultGen(upgradedFormula, upgradeNeeded);
	}

	private static UpgradeResultChecker<Assignment> makeResult(
			Assignment upgradedFormula, boolean upgradeNeeded) {
		return makeResultGen(upgradedFormula, upgradeNeeded);
	}

	private static TestItem<?>[] makeParenSetOp() {
		final FreeIdentifier id_x = mFreeIdentifier("x");
		final FreeIdentifier id_y = mFreeIdentifier("y");
		final FreeIdentifier id_z = mFreeIdentifier("z");
		final List<TestItem<?>> result = new ArrayList<TestItem<?>>();
		for (SetOpImageTags setOp : SetOpImageTags.values()) {
			final ParenExpMaker parenExpMaker = new ParenExpMaker(setOp);
			final TestItem<?> ti = new TestItemExpr(
					parenExpMaker.makeNoParen(), // V1: no parentheses
					V2, makeResult(
							mBinaryExpression(setOp.getTag(),
									mBinaryExpression(setOp.getTag(), id_x,
											id_y), id_z), true));// V2:
			// parenthesised
			// left
			result.add(ti);
		}
		return result.toArray(new TestItemExpr[result.size()]);
	}

	private static void verifyUpgrade(TestItem<?>[] testItems) {
		for (TestItem<?> testItem : testItems) {
			testItem.verifyUpgrade();
		}
	}

	// verify correct upgrade of KPRJ1, KPRJ2, KID,
	public void testGenericExpressionsV1V2() throws Exception {
		final FreeIdentifier id_S = mFreeIdentifier("S");
		final Expression id_gen = mAtomicExpression(KID_GEN);
		final Expression prj1_gen = mAtomicExpression(KPRJ1_GEN);
		final Expression prj2_gen = mAtomicExpression(KPRJ2_GEN);
		final TestItem<?>[] exprsGen = new TestItem<?>[] {
				new TestItemExpr("id(S)", V2, makeResult(mBinaryExpression(
						DOMRES, id_S, id_gen), true)),
				new TestItemExpr("prj1(S)", V2, makeResult(mBinaryExpression(
						DOMRES, id_S, prj1_gen), true)),
				new TestItemExpr("prj2(S)", V2, makeResult(mBinaryExpression(
						DOMRES, id_S, prj2_gen), true)), };
		verifyUpgrade(exprsGen);
	}

	// REL TREL SREL STREL PFUN TFUN PINJ TINJ PSUR TSUR TBIJ
	public void testMissingParenthesesV1V2() throws Exception {
		verifyUpgrade(makeParenSetOp());
	}

	public void testNotTypedGenericFromNotTypedChild() throws Exception {
		final TestItem<?> ti = new TestItemPred("v ∈ id(S)", V2, makeResult(
				mRelationalPredicate(IN, mFreeIdentifier("v"),
						mBinaryExpression(DOMRES, mFreeIdentifier("S"),
								mAtomicExpression(KID_GEN))), true));
		ti.verifyUpgrade();
	}

	public void testTypedGenericFromTypeExpr() throws Exception {
		final IntegerType integerType = ff.makeIntegerType();
		final TestItem<?> ti = new TestItemPred("v ∈ id(ℤ)", V2, makeResult(
				mRelationalPredicate(IN, mFreeIdentifier("v"), ff
						.makeAtomicExpression(KID_GEN, null, REL(integerType,
								integerType))), true));
		ti.verifyUpgrade();
	}

	public void testTypedGenericFromTypedChild() throws Exception {
		final IntegerType integerType = ff.makeIntegerType();
		final TestItem<?> ti = new TestItemPred("v ∈ id(ℕ)", V2, makeResult(
				mRelationalPredicate(IN, mFreeIdentifier("v"),
						mBinaryExpression(DOMRES, mAtomicExpression(NATURAL),
								ff.makeAtomicExpression(KID_GEN, null, REL(
										integerType, integerType)))), true));
		ti.verifyUpgrade();
	}

	public void testBecomesEqualTo() throws Exception {
		final TestItem<?> ti = new TestItemAssign("v ≔ id(S)", V2, makeResult(
				mBecomesEqualTo(mFreeIdentifier("v"), mBinaryExpression(DOMRES,
						mFreeIdentifier("S"), mAtomicExpression(KID_GEN))),
				true));
		ti.verifyUpgrade();
	}

	public void testBecomesEqualToSeveral() throws Exception {
		final TestItem<?> ti = new TestItemAssign("v,w ≔ 0,id(S)", V2,
				makeResult(mBecomesEqualTo(new FreeIdentifier[] {
						mFreeIdentifier("v"), mFreeIdentifier("w") },
						new Expression[] {
								FastFactory.mIntegerLiteral(0),
								mBinaryExpression(DOMRES, mFreeIdentifier("S"),
										mAtomicExpression(KID_GEN)) }), true));
		ti.verifyUpgrade();
	}

	public void testBecomesMemberOf() throws Exception {
		final TestItem<?> ti = new TestItemAssign("v :∈ prj1(S)", V2,
				makeResult(mBecomesMemberOf(mFreeIdentifier("v"),
						mBinaryExpression(DOMRES, mFreeIdentifier("S"),
								mAtomicExpression(KPRJ1_GEN))), true));
		ti.verifyUpgrade();
	}

	public void testBecomesSuchThat() throws Exception {
		final TestItem<?> ti = new TestItemAssign("v :∣ v' ∈ prj2(S)", V2,
				makeResult(mBecomesSuchThat(
						new FreeIdentifier[] { mFreeIdentifier("v") },
						new BoundIdentDecl[] { mBoundIdentDecl("v'") },
						mRelationalPredicate(IN, mBoundIdentifier(0),
								mBinaryExpression(DOMRES, mFreeIdentifier("S"),
										mAtomicExpression(KPRJ2_GEN)))), true));
		ti.verifyUpgrade();
	}

	public void testAssignmentNoChange() throws Exception {
		final TestItem<?>[] testItems = new TestItem<?>[] {
				new TestItemAssign("v :∣ 0 =    0", V2, makeResult(
						(Assignment) null, false)),
				new TestItemAssign("v,w  ≔ 0, 0", V2, makeResult(
						(Assignment) null, false)), };
		verifyUpgrade(testItems);
	}

	/* verify that special space characters do not interfere with the upgrade */
	public void testSpecialCharacters() throws Exception {
		final char spec = new Character('\u2029');

		assertTrue(isEventBWhiteSpace(spec));

		final String form = " ( " + spec + " S \u2194 S " + spec
				+ " ) \u2194 U";
		final TestItem<?> ti = new TestItemExpr(form, V2, makeResult(
				(Expression) null, false));
		ti.verifyUpgrade();
	}

	public void testNoNeedToUpgrade() throws Exception {
		final String pred1 = "∀X· ⊤";
		final String pred2 = "∀X· ((((S ↔ S)  T)  (S  (S ⇸ T))) → S) ⤔ (((S ↣ T) ⤀(S ↠ T))⤖ S)⊆ X";
		final TestItem<?>[] items = new TestItem<?>[] {
				new TestItemPred(pred1, V2, makeResult((Predicate) null, false)),
				new TestItemPred(pred2, V2, makeResult((Predicate) null, false)) };
		verifyUpgrade(items);
	}

	public void testFormulaWithError() throws Exception {
		final String pred = "∀(x)·⊤";
		final UpgradeResultChecker<Predicate> checker = new UpgradeResultChecker<Predicate>(
				new ASTProblem(new SourceLocation(1, 1),
						UnexpectedLPARInDeclList, Error));
		final TestItem<?> ti = new TestItemPred(pred, V2, checker);
		ti.verifyUpgrade();
	}

	// Verify that a formula with an identifier 'partition' in V1
	// is not upgradable to V2
	public void testPartitionAsIdentifier() throws Exception {
		final String pred1 = " partition(x) = y";
		final UpgradeResultChecker<Predicate> checker = new UpgradeResultChecker<Predicate>(
				new ASTProblem(new SourceLocation(1, 9), NotUpgradableError,
						Error));
		final TestItem<?> item = new TestItemPred(pred1, V2, checker);
		item.verifyUpgrade();
	}

	// Verify that a bound identifier "partition" gets upgraded
	public void testPartitionRenamed1() throws Exception {
		final String pred = "∀partition· partition ∈ S";
		final UpgradeResultChecker<Predicate> checker = makeResult(
				mQuantifiedPredicate(FORALL,
						new BoundIdentDecl[] { mBoundIdentDecl("partition1") },
						mRelationalPredicate(IN, mBoundIdentifier(0),
								mFreeIdentifier("S"))), true);
		final TestItem<?> item = new TestItemPred(pred, V2, checker);
		item.verifyUpgrade();
	}

	public void testPartitionRenamed2() throws Exception {
		final String pred = "∀partition1·∃ partition · partition = partition1";
		final UpgradeResultChecker<Predicate> checker = makeResult(
				mQuantifiedPredicate(
						FORALL,
						new BoundIdentDecl[] { mBoundIdentDecl("partition1") },
						mQuantifiedPredicate(
								EXISTS,
								new BoundIdentDecl[] { mBoundIdentDecl("partition2") },
								mRelationalPredicate(EQUAL,
										mBoundIdentifier(0),
										mBoundIdentifier(1)))), true);
		final TestItem<?> item = new TestItemPred(pred, V2, checker);
		item.verifyUpgrade();
	}

	public void testPartitionQuantifiedAndId() throws Exception {
		final String assign = "∀partition·x∈id(S)";

		final FreeIdentifier id_S = mFreeIdentifier("S");
		final Expression id_gen = mAtomicExpression(KID_GEN);
		final UpgradeResultChecker<Predicate> checker = makeResult(
				mQuantifiedPredicate(FORALL,
						new BoundIdentDecl[] { mBoundIdentDecl("partition1") },
						mRelationalPredicate(IN, mFreeIdentifier("x"),
								mBinaryExpression(DOMRES, id_S, id_gen))), true);
		final TestItem<?> ti = new TestItemPred(assign, V2, checker);
		ti.verifyUpgrade();

	}

	public void testPartitionLeftHandSide() throws Exception {
		final String assign = "partition :∈ T";
		final UpgradeResultChecker<Assignment> checker = new UpgradeResultChecker<Assignment>(
				new ASTProblem(new SourceLocation(0, 8), NotUpgradableError,
						Error));
		final TestItem<?> ti = new TestItemAssign(assign, V2, checker);
		ti.verifyUpgrade();
	}

	public void testParenUpgradeNeeded() throws Exception {
		final String pred = "x ∈ A → B → C";

		final UpgradeResultChecker<Predicate> checker = makeResult(
				mRelationalPredicate(IN, mFreeIdentifier("x"),
						mBinaryExpression(TFUN, mBinaryExpression(TFUN,
								mFreeIdentifier("A"), mFreeIdentifier("B")),
								mFreeIdentifier("C"))), true);
		final TestItem<?> ti = new TestItemPred(pred, V2, checker);
		ti.verifyUpgrade();
	}

	public void testParenNoUpgrade() throws Exception {
		final String assign = "x   :∈ (A → B) → C";

		final UpgradeResultChecker<Assignment> checker = makeResult(
				(Assignment) null, false);
		final TestItem<?> ti = new TestItemAssign(assign, V2, checker);
		ti.verifyUpgrade();
	}

}
