/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.rubin.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.rubin.PredicateFrontEnd;
import org.eventb.rubin.Sequent;
import org.junit.Test;

/**
 * Unit tests for the PredicateFormula Calculus parser.
 * 
 * @author Laurent Voisin
 */
public class ParserTests extends AbstractPPTests {

	private static Type Universe = ff.makeGivenType("UNIVERSE"); 
	private static Type Boolean = ff.makeBooleanType(); 
	
	private static Expression mId(String name) {
		return mId(name, Universe);
	}

	private static Expression mId(String name, Type type) {
		return ff.makeFreeIdentifier(name, null, type);
	}

	private static Expression mBd(int index) {
		return ff.makeBoundIdentifier(index, null, Universe);
	}

	private static Predicate mProp(String name) {
		return ff.makeRelationalPredicate(Formula.EQUAL,
				mId(name, Boolean),
				ff.makeAtomicExpression(Formula.TRUE, null),
				null);
	}

	private static Predicate mPred(String name, Expression arg) {
		Type type = ff.makePowerSetType(arg.getType());
		return ff.makeRelationalPredicate(Formula.IN, arg, mId(name, type), null);
	}

	private static Expression mPair(Expression left, Expression right) {
		return ff.makeBinaryExpression(Formula.MAPSTO, left, right, null);
	}

	private static Predicate mNot(Predicate pred) {
		return ff.makeUnaryPredicate(Formula.NOT, pred, null);
	}

	private static Predicate mAnd(Predicate... children) {
		return ff.makeAssociativePredicate(Formula.LAND, children, null);
	}

	private static Predicate mOr(Predicate... children) {
		return ff.makeAssociativePredicate(Formula.LOR, children, null);
	}

	private static Predicate mImp(Predicate left, Predicate right) {
		return ff.makeBinaryPredicate(Formula.LIMP, left, right, null);
	}

	private static Predicate mEqv(Predicate left, Predicate right) {
		return ff.makeBinaryPredicate(Formula.LEQV, left, right, null);
	}

	private static Predicate mAll(String varName, Predicate pred) {
		final BoundIdentDecl decl = ff.makeBoundIdentDecl(varName, null, Universe);
		return ff.makeQuantifiedPredicate(Formula.FORALL,
				new BoundIdentDecl[] {decl},
				pred,
				null);
	}

	private static Predicate mSome(String varName, Predicate pred) {
		final BoundIdentDecl decl = ff.makeBoundIdentDecl(varName, null, Universe);
		return ff.makeQuantifiedPredicate(Formula.EXISTS,
				new BoundIdentDecl[] {decl},
				pred,
				null);
	}

	private static Predicate mEqual(Expression left, Expression right) {
		return ff.makeRelationalPredicate(Formula.EQUAL, left, right, null);
	}

	private static Predicate parsePred(String string) {
		String input = "|-" + string;
		Sequent[] sequents = PredicateFrontEnd.parseString(input);
		assertNotNull("Parser shouldn't fail", sequents);
		assertEquals("Wrong number of parsed sequents", 1, sequents.length);
		Sequent sequent = sequents[0];
		assertEquals("Wrong sequent name", "", sequent.getName());
		assertEquals("Wrong sequent hypotheses", 0, sequent.getHypotheses().length);
		final Predicate goal = sequent.getGoal();
		assertTrue("should be type-checked", goal.isTypeChecked());
		return goal;
	}

	public static Sequent mSequent(String name, String... preds) {
		final int nbHyps = preds.length - 1;
		assert nbHyps >= 0;
		Predicate[] hyps = new Predicate[nbHyps];
		for (int i = 0; i < nbHyps; ++i) {
			hyps[i] = parsePred(preds[i]);
		}
		Predicate goal = parsePred(preds[nbHyps]);
		return new Sequent(name, hyps, goal);
	}
	
	/**
	 * Ensures that parsing the given string produces the given predicate. 
	 */
	private static void testPred(String input, Predicate expected) {
		Sequent[] actSequents = PredicateFrontEnd.parseString(input);
		if (expected == null) {
			assertNull("Parser should fail", actSequents);
			return;
		}
		assertTrue("Expected result is not type-checked", expected.isTypeChecked());
		Predicate actual = parsePred(input);
		assertEquals("Wrong sequent goal", expected, actual);
	}
	
	/**
	 * Ensures that parsing the given string fails. 
	 */
	private static void testFail(String input) {
		Sequent[] actSequents = PredicateFrontEnd.parseString(input);
		assertNull("Parser should fail", actSequents);
	}
	
	/**
	 * Ensures that both strings are parsed in the same way, resulting in the
	 * same tree.
	 */
	private static void testSame(String input, String expected) {
		Sequent[] expSequents = PredicateFrontEnd.parseString(expected);
		assertNotNull("Parser shouldn't fail", expSequents);
		testSequents(input, expSequents);
	}
	
	private static void testSequents(String input, Sequent[] expSequents) {
		for (Sequent sequent : expSequents) {
			assertTrue("Expected sequent should be type-checked", 
					sequent.isTypeChecked());
		}
		
		Sequent[] actSequents = PredicateFrontEnd.parseString(input);
		assertNotNull("Parser shouldn't fail", actSequents);
		assertEquals("Wrong number of parsed sequents", 
				expSequents.length, actSequents.length);
		for (int i = 0; i < expSequents.length; i++) {
			assertEquals("Unexpected sequent", expSequents[i], actSequents[i]);
		}
	}
	
	private static void testSequent(String input, Sequent expected) {
		assertTrue("Expected sequent should be type-checked", 
				expected.isTypeChecked());

		Sequent[] actSequents = PredicateFrontEnd.parseString(input);
		assertNotNull("Parser shouldn't fail", actSequents);
		assertEquals("Wrong number of parsed sequents", 1, actSequents.length);
		assertEquals("Wrong sequent", expected, actSequents[0]);
	}

	public void testAtoms() {
		testPred("A", mProp("A"));
		testPred("Px", mPred("P", mId("x")));
		testPred("Qxy", mPred("Q", mPair(mId("x"), mId("y"))));
		testPred("Rxyz", mPred("R", mPair(mPair(mId("x"), mId("y")), mId("z"))));
		testPred("x=y", mEqual(mId("x"), mId("y")));
		testPred("x/=y", mNot(mEqual(mId("x"), mId("y"))));
	}

	public void testSimple() {
		testPred("~A", mNot(mProp("A")));
		testPred("~Px", mNot(mPred("P", mId("x"))));
		testPred("~x=y", mNot(mEqual(mId("x"), mId("y"))));
		testPred("~x/=y", mNot(mNot(mEqual(mId("x"), mId("y")))));

		testPred("A & B", mAnd(mProp("A"), mProp("B")));
		testPred("Px & Qy", mAnd(mPred("P", mId("x")), mPred("Q", mId("y"))));
		testPred("x=y & B", mAnd(mEqual(mId("x"), mId("y")), mProp("B")));
		testPred("x/=y & B", mAnd(mNot(mEqual(mId("x"), mId("y"))), mProp("B")));
		testPred("A & x=y", mAnd(mProp("A"), mEqual(mId("x"), mId("y"))));
		testPred("A & x/=y", mAnd(mProp("A"), mNot(mEqual(mId("x"), mId("y")))));

		testPred("A | B", mOr(mProp("A"), mProp("B")));
		testPred("Px | Qy", mOr(mPred("P", mId("x")), mPred("Q", mId("y"))));
		testPred("x=y | B", mOr(mEqual(mId("x"), mId("y")), mProp("B")));
		testPred("x/=y | B", mOr(mNot(mEqual(mId("x"), mId("y"))), mProp("B")));
		testPred("A | x=y", mOr(mProp("A"), mEqual(mId("x"), mId("y"))));
		testPred("A | x/=y", mOr(mProp("A"), mNot(mEqual(mId("x"), mId("y")))));

		testPred("A -> B", mImp(mProp("A"), mProp("B")));
		testPred("Px -> Qy", mImp(mPred("P", mId("x")), mPred("Q", mId("y"))));
		testPred("x=y -> B", mImp(mEqual(mId("x"), mId("y")), mProp("B")));
		testPred("x/=y -> B", mImp(mNot(mEqual(mId("x"), mId("y"))), mProp("B")));
		testPred("A -> x=y", mImp(mProp("A"), mEqual(mId("x"), mId("y"))));
		testPred("A -> x/=y", mImp(mProp("A"), mNot(mEqual(mId("x"), mId("y")))));

		testPred("A <-> B", mEqv(mProp("A"), mProp("B")));
		testPred("Px <-> Qy", mEqv(mPred("P", mId("x")), mPred("Q", mId("y"))));
		testPred("x=y <-> B", mEqv(mEqual(mId("x"), mId("y")), mProp("B")));
		testPred("x/=y <-> B", mEqv(mNot(mEqual(mId("x"), mId("y"))), mProp("B")));
		testPred("A <-> x=y", mEqv(mProp("A"), mEqual(mId("x"), mId("y"))));
		testPred("A <-> x/=y", mEqv(mProp("A"), mNot(mEqual(mId("x"), mId("y")))));

		testPred("!x Px", mAll("x", mPred("P", mBd(0))));
		testPred("#x Px", mSome("x", mPred("P", mBd(0))));
		testPred("!x x=y", mAll("x", mEqual(mBd(0), mId("y"))));
		testPred("#x x=y", mSome("x", mEqual(mBd(0), mId("y"))));
		testPred("!x x/=y", mAll("x", mNot(mEqual(mBd(0), mId("y")))));
		testPred("#x x/=y", mSome("x", mNot(mEqual(mBd(0), mId("y")))));
	}

	@Test
	public void testAssociativity() {
		testPred("~~A", mNot(mNot(mProp("A"))));
		testPred("A & B & C", mAnd(mProp("A"), mProp("B"), mProp("C")));
		testPred("A | B | C", mOr(mProp("A"), mProp("B"), mProp("C")));
		testFail("A -> B -> C");
		testFail("A <-> B <-> C");
		testPred("!x!y Pxy", 
				mAll("x", mAll("y", mPred("P", mPair(mBd(1), mBd(0))))));
		testPred("#x#y Pxy", 
				mSome("x", mSome("y", mPred("P", mPair(mBd(1), mBd(0))))));
	}

	@Test
	public void testPrecedence() {
		// Relative precedence of quantifiers
		testSame("|- ~!xPx", "|- ~(!x(Px))");
		testSame("|- !x~Px", "|- !x(~Px)");
		testSame("|- !xPx &   Qx", "|- (!x(Px)) &     Qx");
		testSame("|-   Px & !xQx", "|-     Px   & (!x(Qx))");
		testSame("|- !xPx |   Qx", "|- (!x(Px)) |     Qx");
		testSame("|-   Px | !xQx", "|-     Px   | (!x(Qx))");
		testSame("|- !xPx ->   Qx", "|- (!x(Px)) ->     Qx");
		testSame("|-   Px -> !xQx", "|-     Px   -> (!x(Qx))");
		testSame("|- !xPx <->   Qx", "|- (!x(Px)) <->     Qx");
		testSame("|-   Px <-> !xQx", "|-     Px   <-> (!x(Qx))");
		
		testSame("|- ~#xPx", "|- ~(#x(Px))");
		testSame("|- #x~Px", "|- #x(~Px)");
		testSame("|- #xPx &   Qx", "|- (#x(Px)) &     Qx");
		testSame("|-   Px & #xQx", "|-     Px   & (#x(Qx))");
		testSame("|- #xPx |   Qx", "|- (#x(Px)) |     Qx");
		testSame("|-   Px | #xQx", "|-     Px   | (#x(Qx))");
		testSame("|- #xPx ->   Qx", "|- (#x(Px)) ->     Qx");
		testSame("|-   Px -> #xQx", "|-     Px   -> (#x(Qx))");
		testSame("|- #xPx <->   Qx", "|- (#x(Px)) <->     Qx");
		testSame("|-   Px <-> #xQx", "|-     Px   <-> (#x(Qx))");
		
		// Relative precedence of "not"
		testSame("|- ~A &  B", "|- (~A) &   B");
		testSame("|-  A & ~B", "|-   A  & (~B)");
		testSame("|- ~A |  B", "|- (~A) |   B");
		testSame("|-  A | ~B", "|-   A  | (~B)");
		testSame("|- ~A ->  B", "|- (~A) ->   B");
		testSame("|-  A -> ~B", "|-   A  -> (~B)");
		testSame("|- ~A <->  B", "|- (~A) <->   B");
		testSame("|-  A <-> ~B", "|-   A  <-> (~B)");
		
		// Relative precedence of "and" and "or"
		testPred("|- A & B | C", null);
		testPred("|- A | B & C", null);

		testSame("|- A & B -> C", "|- (A & B) -> C");
		testSame("|- A & B <-> C", "|- (A & B) <-> C");
		testSame("|- A -> B & C", "|- A -> (B & C)");
		testSame("|- A <-> B & C", "|- A <-> (B & C)");

		testSame("|- A | B -> C", "|- (A | B) -> C");
		testSame("|- A | B <-> C", "|- (A | B) <-> C");
		testSame("|- A -> B | C", "|- A -> (B | C)");
		testSame("|- A <-> B | C", "|- A <-> (B | C)");

		// Relative precedence of "implies" and "equivalence"
		testPred("|- A -> B <-> C", null);
		testPred("|- A <-> B -> C", null);
	}

	/**
	 * Examples given in the book, page 113.
	 */
	@Test
	public void testBook() {
		testSame("|- !xRx -> #y(Iy & Bxy)", "|- ((!xRx) -> (#y(Iy & Bxy)))");
		testSame("|- !x#yRxy", "|- (!x(#y(Rxy)))");
		testSame("|- !x#y(Sx & Ty -> Rxy)", "|- (!x(#y((Sx & Ty) -> Rxy)))");
		testSame("|- ~!x~(Rx & ~Sx)", "|- (~(!x(~(Rx & (~Sx)))))");
	}
	
	@Test
	public void testHypotheses() {
		testSequent("A |- B", mSequent("", "A", "B"));
		testSequent("A, B |- C", mSequent("", "A", "B", "C"));
		testSequent("!xPx, #yQy, ~A, A & B, A | B, A -> B, A <-> B |- C",
				mSequent("", "!xPx", "#yQy", "~A", "A & B", "A | B",
						"A -> B", "A <-> B", "C"));
	}
	
	@Test
	public void testSequents() {
		String input =
			"\"toto\" : A |- B;\n" +
			"C |- D;\n" +
			"\"titi\" : E |- F";
		Sequent[] expSequents = new Sequent[] {
			mSequent("toto", "A", "B"),	
			mSequent(    "", "C", "D"),	
			mSequent("titi", "E", "F"),	
		};
		
		testSequents(input, expSequents);
	}

	/**
	 * Various examples from the book.
	 */
	@Test
	public void testVarious() {
		String input =
			"\"Rubin, exa 7.1(1), p147\":\n" +
			"!x(Cx -> Wx), ~Wt |- ~Ct;\n" +
			"\"Rubin, exa 7.1(2), p148\":\n" +
			"!x(Gx & Mx -> Lxo), !y(Lyo -> ~Loy) |- !z(Gz & Loz -> ~Mz);\n" +
			"\"Rubin, exa 7.1(3), p149\":\n" +
			"!x(Sx -> !y(Iy -> ~Lxy)), !x(Sx -> !y(Sy -> Lxy)) |-\n" +
			"!x(Ix -> ~Sx)";
		Sequent[] expSequents = new Sequent[] {
				mSequent("Rubin, exa 7.1(1), p147",
						"!x(Cx -> Wx)",
						"~Wt",
						"~Ct"
				),
				mSequent("Rubin, exa 7.1(2), p148",
						"!x(Gx & Mx -> Lxo)",
						"!y(Lyo -> ~Loy)",
						"!z(Gz & Loz -> ~Mz)"
				),	
				mSequent("Rubin, exa 7.1(3), p149",
						"!x(Sx -> !y(Iy -> ~Lxy))",
						"!x(Sx -> !y(Sy -> Lxy))",
						"!x(Ix -> ~Sx)"
				),	
		};
			
		testSequents(input, expSequents);
	}
	
}
