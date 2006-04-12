package org.eventb.pp.tests;

import static org.eventb.pp.tests.FastFactory.mAssociativePredicate;
import static org.eventb.pp.tests.FastFactory.mLiteralPredicate;

import java.math.BigInteger;

import junit.framework.TestCase;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.Type;
import org.eventb.internal.pp.translator.GoalChecker;
import org.eventb.internal.pp.translator.IdentifierDecomposition;
import org.eventb.internal.pp.translator.Translator;

/**
 * Ensures that the translator from set-theory to predicate calculus works
 * correctly.
 * 
 * 
 * @author Matthias Konrad
 */

public class TranslationTests extends TestCase {
	
	public interface TestTranslation {
		Formula translate(Formula input, FormulaFactory ff);
	}
	
	private TestTranslation translator = new TestTranslation() {
		public Formula translate(Formula input, FormulaFactory formulaFactory) {
			return Translator.reduceToPredCalc((Predicate)input, formulaFactory);
		}
	};
	
	private TestTranslation identifierDecomposition = new TestTranslation() {
		public Formula translate(Formula input, FormulaFactory formulaFactory) {
			return IdentifierDecomposition.decomposeIdentifiers((Predicate)input, formulaFactory);
		}
	};
	

	private static FormulaFactory ff = FormulaFactory.getDefault();

	// Types used in these tests
	private static IntegerType INT = ff.makeIntegerType();
	private static BooleanType BOOL = ff.makeBooleanType();
	private static PowerSetType INT_SET = ff.makePowerSetType(INT);

	private static GivenType ty_S = ff.makeGivenType("S");
	private static GivenType ty_T = ff.makeGivenType("T");
	private static GivenType ty_U = ff.makeGivenType("U");
	private static GivenType ty_V = ff.makeGivenType("V");

	private static Type POW(Type base) {
		return ff.makePowerSetType(base);
	}

	private static Type CPROD(Type left, Type right) {
		return ff.makeProductType(left, right);
	}
	
	private static Type REL(Type left, Type right) {
		return ff.makeRelationalType(left, right);
	}
	
	private static BinaryExpression Maplet(Expression left, Expression right) {
		return ff.makeBinaryExpression(Formula.MAPSTO, left, right, null);
	}
	
	private static IntegerLiteral IntLiteral(int value) {
		return ff.makeIntegerLiteral(new BigInteger("" + value), null);
	}
	
	public Predicate parse(String string, ITypeEnvironment te) {
		Predicate pred = ff.parsePredicate(string).getParsedPredicate();
		pred.typeCheck(te);
		assertTrue(string + ": is not typed", pred.isTypeChecked());
		return pred;
	}
	
	public Predicate parse(String string) {
		return parse(string, ff.makeTypeEnvironment());
	}
	
	/**
	 * Main test routine for predicates.
	 */
	public void testPredicateTranslation () {

		Predicate pred;
		
		pred = mAssociativePredicate(Formula.LAND, 
				mLiteralPredicate(Formula.BTRUE),
				mLiteralPredicate(Formula.BTRUE)
		);
		doTest(pred, pred);
	}
	
	public void testIdentifierDecomposition1() {
		Predicate input = parse("∀x·10↦20 = x");
		Predicate expected = parse("∀x,x0·10=x0 ∧ 20=x");
		
		doTest(input, expected);		
	}

	public void testIdentifierDecomposition2() {
		Predicate input = parse("10 ↦ 20 = s");
		Predicate expected = parse("∀x,x0·s=x0 ↦ x⇒10=x0∧20=x");
		doTest(input, expected);		
	}
	
	public void testIdentifierDecomposition3() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"s"}, new Type[]{CPROD(INT, INT)});
		Predicate input = parse("∀x·s=x", te);
		Predicate expected =parse("∀x,x0·s=x0↦x⇒(∀y,y0·x0↦x=y0↦y)", te);
		doTest(input, expected, identifierDecomposition);		
	}
	
	public void testIdentifierDecomposition4() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"s"}, new Type[]{CPROD(INT, INT)});
		Predicate input = parse("t=s", te);
		Predicate expected = parse("∀x,x0,x1,x2·(t=x2↦x1∧s=x0↦x)⇒(x2=x0∧x1=x)", te);
		
		doTest(input, expected);		
	}

	public void testIdentifierDecomposition5() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"t"}, new Type[]{CPROD(INT, INT)});
		Predicate input = parse("t=t", te);
		Predicate expected = parse("∀x,x0·t=x0↦x⇒(x0=x0∧x=x)", te);
		
		doTest(input, expected);		
	}
	
	public void testSubsetEqRule() {
		Predicate input, expected;
		
		Expression s = FastFactory.mFreeIdentifier("s", INT_SET);
		Expression t = FastFactory.mFreeIdentifier("t", INT_SET);
		
		input = FastFactory.mRelationalPredicate(
				Formula.SUBSETEQ,
				s,
				t);
		
		expected = FastFactory.mQuantifiedPredicate(
				Formula.FORALL,
				FastFactory.mList(FastFactory.mBoundIdentDecl("x", null)),
				FastFactory.mBinaryPredicate(
					Formula.LIMP,
					FastFactory.mRelationalPredicate(
							Formula.IN, FastFactory.mBoundIdentifier(0), s),
					FastFactory.mRelationalPredicate(
							Formula.IN, FastFactory.mBoundIdentifier(0), t)));
		
		doTest(input, expected);		
	}
	
	public void testNotSubsetEqRule() {
		Predicate input, expected;
		
		Expression s = FastFactory.mFreeIdentifier("s", INT_SET);
		Expression t = FastFactory.mFreeIdentifier("t", INT_SET);
		
		input = FastFactory.mRelationalPredicate(Formula.NOTSUBSETEQ,	s, t);
		
		expected = FastFactory.mUnaryPredicate(
				Formula.NOT, 
				FastFactory.mQuantifiedPredicate(
					Formula.FORALL,
					FastFactory.mList(FastFactory.mBoundIdentDecl("x")),
					FastFactory.mBinaryPredicate(
						Formula.LIMP,
						FastFactory.mRelationalPredicate(
								Formula.IN, FastFactory.mBoundIdentifier(0), s),
						FastFactory.mRelationalPredicate(
								Formula.IN, FastFactory.mBoundIdentifier(0), t))));					
		
		doTest(input, expected);		
	}
	
	public void testSubsetRule() {
		Predicate input, expected;
		
		Expression s = FastFactory.mFreeIdentifier("s", INT_SET);
		Expression t = FastFactory.mFreeIdentifier("t", INT_SET);
		
		input = FastFactory.mRelationalPredicate(Formula.SUBSET, s, t);

		expected = mAssociativePredicate(
				Formula.LAND,
				FastFactory.mQuantifiedPredicate(
						Formula.FORALL,
						FastFactory.mList(FastFactory.mBoundIdentDecl("x")),
						FastFactory.mBinaryPredicate(
							Formula.LIMP,
							FastFactory.mRelationalPredicate(
									Formula.IN, FastFactory.mBoundIdentifier(0), s),
							FastFactory.mRelationalPredicate(
									Formula.IN, FastFactory.mBoundIdentifier(0), t))),
				FastFactory.mUnaryPredicate(
					Formula.NOT, 
					FastFactory.mQuantifiedPredicate(
						Formula.FORALL,
						FastFactory.mList(FastFactory.mBoundIdentDecl("x")),
						FastFactory.mBinaryPredicate(
							Formula.LIMP,
							FastFactory.mRelationalPredicate(
									Formula.IN, FastFactory.mBoundIdentifier(0), t),
							FastFactory.mRelationalPredicate(
									Formula.IN, FastFactory.mBoundIdentifier(0), s)))));
		
		doTest(input, expected);		
	}

	public void testNotSubsetRule() {
		Predicate input, expected;

		Expression s = FastFactory.mFreeIdentifier("s", INT_SET);
		Expression t = FastFactory.mFreeIdentifier("t", INT_SET);
		
		input = FastFactory.mRelationalPredicate(Formula.NOTSUBSET, s, t);

		expected = mAssociativePredicate(
				Formula.LOR,
				FastFactory.mUnaryPredicate(
						Formula.NOT, 
						FastFactory.mQuantifiedPredicate(
							Formula.FORALL,
							FastFactory.mList(FastFactory.mBoundIdentDecl("x")),
							FastFactory.mBinaryPredicate(
								Formula.LIMP,
								FastFactory.mRelationalPredicate(
										Formula.IN, FastFactory.mBoundIdentifier(0), s),
								FastFactory.mRelationalPredicate(
										Formula.IN, FastFactory.mBoundIdentifier(0), t)))),
				FastFactory.mQuantifiedPredicate(
						Formula.FORALL,
						FastFactory.mList(FastFactory.mBoundIdentDecl("x")),
						FastFactory.mBinaryPredicate(
							Formula.LIMP,
							FastFactory.mRelationalPredicate(
									Formula.IN, FastFactory.mBoundIdentifier(0), t),
							FastFactory.mRelationalPredicate(
									Formula.IN, FastFactory.mBoundIdentifier(0), s))));
		
		doTest(input, expected);		
	}

	public void testFiniteRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"S"}, new Type[]{INT_SET});
		Predicate input = parse("finite(S)", te);
		Predicate expected = parse("∀a·∃b,f·f∈(S↣a‥b)", te);
		
		expected.typeCheck(FastFactory.mTypeEnvironment());
		doTest(input, Translator.reduceToPredCalc(expected, ff));
	}
	/*
	public void testTinjTule() {
		Predicate input, expected;
		
		input = FastFactory.mRelationalPredicate(
				Formula.IN,
				FastFactory.mFreeIdentifier("f"),
				FastFactory.mBinaryExpression(
						Formula.TINJ,
						FastFactory.mFreeIdentifier("S", INT_SET),
						FastFactory.mBinaryExpression(
								Formula.UPTO, 
								FastFactory.mFreeIdentifier("a", INT),
								FastFactory.mFreeIdentifier("b", INT))));
		
		expected = FastFactory.mLiteralPredicate(Formula.BTRUE);
		
		doTest(input, expected);
	}*/
	
	public void testPowerSetInRule1() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"T", "E"}, new Type[]{INT_SET, INT_SET});
		Predicate input = parse("E∈ℙ(T)", te);
		Predicate expected = parse("∀x·x∈E⇒x∈T", te);
		
		expected.typeCheck(FastFactory.mTypeEnvironment());
		doTest(input, Translator.reduceToPredCalc(expected, ff));
	}
	
	public void testPowerSetInRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"T", "E"}, new Type[]{POW(CPROD(BOOL, INT)), POW(CPROD(BOOL, INT))});
		Predicate input = parse("E∈ℙ(T)", te);
		Predicate expected = parse("∀x·x∈E⇒x∈T", te);
		
		expected.typeCheck(FastFactory.mTypeEnvironment());
		doTest(input, Translator.reduceToPredCalc(expected, ff));
	}

	public void testNaturalInRule() {
		Expression E = FastFactory.mFreeIdentifier("E");
		Predicate input = FastFactory.mRelationalPredicate(
				Formula.IN,
				E,
				FastFactory.mAtomicExpression(Formula.NATURAL));
		
		Predicate expected = FastFactory.mRelationalPredicate(Formula.GE, E, IntLiteral(0));
		
		doTest (input, expected);		
	}
	
	public void testNatural1InRule() {
		Expression E = FastFactory.mFreeIdentifier("E");
		Predicate input = FastFactory.mRelationalPredicate(
				Formula.IN,
				E,
				FastFactory.mAtomicExpression(Formula.NATURAL1));
		
		Predicate expected = FastFactory.mRelationalPredicate(
				Formula.GT,
				E,
				IntLiteral(0));
		
		doTest (input, expected);		
	}
	
	public void testIntegerInRule() {
		Expression E = FastFactory.mFreeIdentifier("E");
		Predicate input = FastFactory.mRelationalPredicate(
				Formula.IN,
				E,
				FastFactory.mAtomicExpression(Formula.INTEGER));
		
		Predicate expected = FastFactory.mLiteralPredicate(Formula.BTRUE);
		
		doTest (input, expected);
	}
	
	public void testCSetInRule() {
		Expression E = FastFactory.mFreeIdentifier("E");
		Predicate input = FastFactory.mRelationalPredicate(
				Formula.IN,
				E,
				FastFactory.mQuantifiedExpression(
					Formula.CSET,
					QuantifiedExpression.Form.Explicit,
					FastFactory.mList(FastFactory.mBoundIdentDecl("x", INT)),
					FastFactory.mLiteralPredicate(Formula.BTRUE),
					FastFactory.mBoundIdentifier(0)));
				
		Predicate expected = FastFactory.mQuantifiedPredicate(
				Formula.EXISTS,
				FastFactory.mList(FastFactory.mBoundIdentDecl("x", INT)),
				mAssociativePredicate(
					Formula.LAND,
					FastFactory.mList(
						FastFactory.mLiteralPredicate(Formula.BTRUE),
						FastFactory.mRelationalPredicate(
							Formula.EQUAL,
							E,
							FastFactory.mBoundIdentifier(0)))));
		
		doTest(input, expected);	
	}
	
	public void testQInterInRule() {
		Expression E = FastFactory.mFreeIdentifier("E");
		Predicate input = FastFactory.mRelationalPredicate(
				Formula.IN,
				E,
				FastFactory.mQuantifiedExpression(
					Formula.QINTER,
					QuantifiedExpression.Form.Explicit,
					FastFactory.mList(FastFactory.mBoundIdentDecl("x", INT)),
					FastFactory.mLiteralPredicate(Formula.BTRUE),
					FastFactory.mSetExtension(
							FastFactory.mList(FastFactory.mBoundIdentifier(0)))));

		Predicate expected = FastFactory.mQuantifiedPredicate(
				Formula.FORALL,
				FastFactory.mList(FastFactory.mBoundIdentDecl("x", INT)),
				FastFactory.mBinaryPredicate(
					Formula.LIMP,
					FastFactory.mLiteralPredicate(Formula.BTRUE),
					FastFactory.mRelationalPredicate(
						Formula.EQUAL,
						E,
						FastFactory.mBoundIdentifier(0))));
		
		doTest(input, expected);				
	}

	public void testQUnionInRule() {
		Expression E = FastFactory.mFreeIdentifier("E");
		Predicate input = FastFactory.mRelationalPredicate(
				Formula.IN,
				E,
				FastFactory.mQuantifiedExpression(
					Formula.QUNION,
					QuantifiedExpression.Form.Explicit,
					FastFactory.mList(FastFactory.mBoundIdentDecl("x", INT)),
					FastFactory.mLiteralPredicate(Formula.BTRUE),
					FastFactory.mSetExtension(
							FastFactory.mList(FastFactory.mBoundIdentifier(0)))));

		Predicate expected = FastFactory.mQuantifiedPredicate(
				Formula.EXISTS,
				FastFactory.mList(FastFactory.mBoundIdentDecl("x", INT)),
				mAssociativePredicate(
					Formula.LAND,
					FastFactory.mList(
						FastFactory.mLiteralPredicate(Formula.BTRUE),
						FastFactory.mRelationalPredicate(
							Formula.EQUAL,
							E,
							FastFactory.mBoundIdentifier(0)))));
		
		doTest(input, expected);				
	}
	
	public void testUnionRule() {
		Predicate input, expected;
		
		Expression S = FastFactory.mFreeIdentifier("S", POW(INT_SET));
		Expression E = FastFactory.mFreeIdentifier("E", INT);
		input = FastFactory.mRelationalPredicate(
				Formula.IN,
				E,
				FastFactory.mUnaryExpression(Formula.KUNION, S));
		
		expected = FastFactory.mQuantifiedPredicate(
				Formula.EXISTS,
				FastFactory.mList(FastFactory.mBoundIdentDecl("x", INT_SET)),
				mAssociativePredicate(
						Formula.LAND,
						FastFactory.mRelationalPredicate(
								Formula.IN,
								FastFactory.mBoundIdentifier(0), 
								S),
						FastFactory.mRelationalPredicate(
								Formula.IN,
								E, 
								FastFactory.mBoundIdentifier(0))));
		
		doTest(input, expected);				
	}

	public void testInterRule() {
		Predicate input, expected;
		
		Expression S = FastFactory.mFreeIdentifier("S", POW(INT_SET));
		Expression E = FastFactory.mFreeIdentifier("E", INT);
		input = FastFactory.mRelationalPredicate(
				Formula.IN,
				E,
				FastFactory.mUnaryExpression(Formula.KINTER, S));
		
		expected = FastFactory.mQuantifiedPredicate(
				Formula.FORALL,
				FastFactory.mList(FastFactory.mBoundIdentDecl("x", INT_SET)),
				FastFactory.mBinaryPredicate(
						Formula.LIMP,
						FastFactory.mRelationalPredicate(
								Formula.IN,
								FastFactory.mBoundIdentifier(0),
								S),
						FastFactory.mRelationalPredicate(
								Formula.IN,
								E,
								FastFactory.mBoundIdentifier(0))));
						

		doTest(input, expected);
	}
	
	public void testPow1Rule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"T", "E"}, new Type[]{INT_SET, INT_SET});
		Predicate input = parse("E∈ℙ1(T)", te);
		Predicate expected = parse("E∈ℙ(T) ∧ (∃x·x∈E)", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		
		doTest(input, expected);
	}
	
	public void testEmptySetRule() {
		Predicate input, expected;
		
		Expression E = FastFactory.mFreeIdentifier("E", INT);
		input = FastFactory.mRelationalPredicate(
				Formula.IN,
				E,
				FastFactory.mEmptySet(INT_SET));
		
		expected = FastFactory.mLiteralPredicate(Formula.BFALSE);
		
		doTest(input, expected);
	}
	
	public void testSetExtensionRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT});
		Predicate input = parse("E∈{a,b,c}", te);
		Predicate expected = parse("E=a ∨ E=b ∨ E=c", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		
		doTest(input, expected);
	}
	
	public void testUpToRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E", "a", "b"}, new Type[]{INT, INT, INT});
		Predicate input = parse("E∈a‥b", te);
		Predicate expected = parse("E≥a∧E≤b", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		
		doTest(input, expected);
	}
	
	public void testSetMinusRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{INT, INT_SET, INT_SET});
		Predicate input = parse("E∈S∖T", te);
		Predicate expected = parse("E∈S∧¬(E∈T)", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		
		doTest(input, expected);
	}

	public void testBInterRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{INT, INT_SET, INT_SET});
		Predicate input = parse("E∈S∩T", te);
		Predicate expected = parse("E∈S∧E∈T", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		
		doTest(input, expected);	
	}

	public void testBUnionRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{INT, INT_SET, INT_SET});
		Predicate input = parse("E∈S∩T", te);
		Predicate expected = parse("E∈S∧E∈T", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		
		doTest(input, expected);	
	}
	
	public void testRelRule() {		
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, INT), INT_SET, INT_SET});
		Predicate input = parse("E∈S↔T", te);
		Predicate expected = parse("dom(E)⊆S∧ran(E)⊆T", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testRelImgRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "r", "w"}, new Type[]{INT, REL(INT, INT), INT_SET});
		Predicate input = parse("E∈r[w]", te);
		Predicate expected = parse("∃x·x∈w∧x↦E∈r", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testFunImgRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "f", "w"}, new Type[]{INT, REL(INT, INT_SET), INT});
		Predicate input = parse("E∈f(w)", te);
		Predicate expected = parse("∃x·w↦x∈f∧E∈x", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testFundImgRule2() {
		Type intRel = REL(INT, INT);
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"f", "g", "s", "t"}, new Type[]{intRel, intRel, intRel, intRel});
		Predicate input = parse("g(s(t(10))) > 10", te);
		Predicate expected = parse("∀x·x=g(s(t(10))) ⇒ x>10", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}

	public void testFundImgRule3() {
		Type intRel = REL(INT, INT);
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"f", "g", "s", "t"}, new Type[]{intRel, intRel, intRel, intRel});
		Predicate input = parse("f(23) − g(s(t(10))) > g(29)", te);
		Predicate expected = parse("∀x,x0,x1·x1=f(23)∧x0=g(s(t(10)))∧x=g(29) ⇒ x1−x0>x", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}

	public void testRangeRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"r", "E"}, new Type[]{REL(INT, INT), INT});
		Predicate input = parse("E∈ran(r)", te);
		Predicate expected = parse("∃x·x↦E∈r", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}

	public void testDomainRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"r", "E"}, new Type[]{REL(INT, INT), INT});
		Predicate input = parse("E∈dom(r)", te);
		Predicate expected = parse("∃x·E↦x∈r", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testTotRelRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, INT), INT_SET, INT_SET});
		Predicate input = parse("E∈ST", te);
		Predicate expected = parse("E∈S↔T∧S⊆dom(E)", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testSurRelRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, INT), INT_SET, INT_SET});
		Predicate input = parse("E∈ST", te);
		Predicate expected = parse("E∈S↔T∧T⊆ran(E)", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testSurjTotalRelRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, INT), INT_SET, INT_SET});
		Predicate input = parse("E∈ST", te);
		Predicate expected = parse("E∈ST∧T⊆ran(E)", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testTotBijRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, INT), INT_SET, INT_SET});
		Predicate input = parse("E∈S⤖T", te);
		Predicate expected = parse("E∈S↠T∧(∀x,x0,x1·(x0↦x1∈E∧x↦x1∈E)⇒x0=x)", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testTotSurjRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, INT), INT_SET, INT_SET});
		Predicate input = parse("E∈S↠T", te);
		Predicate expected = parse("E∈S→T∧T⊆ran(E)", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testParSurjRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, INT), INT_SET, INT_SET});
		Predicate input = parse("E∈S⤀T", te);
		Predicate expected = parse("E∈S⇸T∧T⊆ran(E)", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testTotInjRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, INT), INT_SET, INT_SET});
		Predicate input = parse("E∈S↣T", te);
//		Predicate expected = parse("E∈S→T∧(∀C,B,A·(B↦A∈E∧C↦A∈E)⇒B=C)", te);
		Predicate expected = parse("E∈S→T∧(∀x,x0,x1·(x0↦x1∈E∧x↦x1∈E)⇒x0=x)", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testPartInjRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, INT), INT_SET, INT_SET});
		Predicate input = parse("E∈S⤔T", te);
//		Predicate expected = parse("E∈S⇸T∧(∀C,B,A·(B↦A∈E∧C↦A∈E)⇒B=C)", te);
		Predicate expected = parse("E∈S⇸T∧(∀x,x0,x1·(x0↦x1∈E∧x↦x1∈E)⇒x0=x)", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}

	public void testTotFunRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, INT), INT_SET, INT_SET});
		Predicate input = parse("E∈S→T", te);
		Predicate expected = parse("E∈S⇸T∧S⊆dom(E)", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testPartFunRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, INT), INT_SET, INT_SET});
		Predicate input = parse("E∈S⇸T", te);
//		Predicate expected = parse("E∈S↔T∧(∀C,B,A·(A↦B∈E∧A↦C∈E)⇒B=C)", te);
		Predicate expected = parse("E∈S↔T∧(∀x,x0,x1·(x1↦x0∈E∧x1↦x∈E)⇒x0=x)", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testCProdRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "S", "T"}, new Type[]{INT, INT, INT_SET, INT_SET});
		Predicate input = parse("E↦F∈S×T", te);
		Predicate expected = parse("E∈S∧F∈T", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testRelOvrRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "q", "r"}, new Type[]{INT, INT, REL(INT, INT), REL(INT, INT)});
		Predicate input = parse("E↦F∈qr", te);
		Predicate expected = parse("E↦F∈dom(r)⩤q∨E↦F∈r", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}

	public void testRelOvrRule2() {
		Type rt = REL(INT, BOOL);
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "q", "s", "t", "w"}, new Type[]{INT, BOOL, rt, rt, rt, rt});
		Predicate input = parse("E↦F∈qstw", te);
		Predicate expected = 
			parse("E↦F∈(dom(s)∪dom(t)∪dom(w))⩤q ∨E↦F∈(dom(t)∪dom(w))⩤s ∨ E↦F∈dom(w)⩤t ∨ E↦F∈w", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}

	public void testRanSubRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "r", "T"}, new Type[]{INT, INT, REL(INT, INT), INT_SET});
		Predicate input = parse("E↦F∈r⩥T", te);
		Predicate expected = parse("E↦F∈r∧¬(F∈T)", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}

	public void testDomSubRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "r", "S"}, new Type[]{INT, INT, REL(INT, INT), INT_SET});
		Predicate input = parse("E↦F∈S⩤r", te);
		Predicate expected = parse("E↦F∈r∧¬(E∈S)", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}

	public void testRanResRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "r", "T"}, new Type[]{INT, INT, REL(INT, INT), INT_SET});
		Predicate input = parse("E↦F∈r▷T", te);
		Predicate expected = parse("E↦F∈r∧F∈T", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}

	public void testDomResRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "r", "S"}, new Type[]{INT, INT, REL(INT, INT), INT_SET});
		Predicate input = parse("E↦F∈S◁r", te);
		Predicate expected = parse("E↦F∈r∧E∈S", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}

	public void testIdRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "S"}, new Type[]{INT, INT, INT_SET});
		Predicate input = parse("E↦F∈id(S)", te);
		Predicate expected = parse("E∈S∧E=F", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}

	public void testFCompRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "p", "q"}, new Type[]{INT, INT, REL(INT, INT), REL(INT, INT)});
		Predicate input = parse("E↦F∈p;q", te);
		Predicate expected = parse("∃x·E↦x∈p∧x↦F∈q", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testFCompRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "p", "q", "w"}, new Type[]{INT, INT, REL(INT, BOOL), REL(BOOL, BOOL), REL(BOOL, INT)});
		Predicate input = parse("E↦F∈p;q;w", te);
		Predicate expected = parse("∃x,x0·E↦x0∈p∧x0↦x∈q∧x↦F∈w", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}


	public void testBCompRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "p", "q"}, new Type[]{INT, INT, REL(INT, INT), REL(INT, INT)});
		Predicate input = parse("E↦F∈p∘q", te);
		Predicate expected = parse("E↦F∈q;p", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testBCompRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "p", "q", "w"}, new Type[]{INT, INT, REL(BOOL, INT), REL(BOOL, BOOL), REL(INT, BOOL)});
		Predicate input = parse("E↦F∈p∘q∘w", te);
		Predicate expected = parse("E↦F∈w;q;p", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testInvRelRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "r"}, new Type[]{INT, INT, REL(INT, INT)});
		Predicate input = parse("E↦F∈(r^−1)", te);
		Predicate expected = parse("F↦E∈r", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testPrj1Rule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "G", "r"}, new Type[]{INT, INT, INT, REL(INT, INT)});
		Predicate input = parse("(E↦F)↦G ∈ prj1(r)", te);
		Predicate expected = parse("E↦F∈r ∧ G=E", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testPrj2Rule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "G", "r"}, new Type[]{INT, INT, INT, REL(INT, INT)});
		Predicate input = parse("(E↦F)↦G ∈ prj2(r)", te);
		Predicate expected = parse("E↦F∈r ∧ G=F", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testDirectProdRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "G", "p", "q"}, new Type[]{INT, INT, INT, REL(INT, INT), REL(INT, INT)});
		Predicate input = parse("E↦(F↦G) ∈ p⊗q", te);
		Predicate expected = parse("E↦F∈p ∧ E↦G∈q", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testPrallelProdRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "G", "H", "p", "q"}, 
				new Type[]{INT, INT, INT, INT, REL(INT, INT), REL(INT, INT)});
		Predicate input = parse("(E↦F)↦(G↦H) ∈ p∥q", te);
		Predicate expected = parse("E↦G∈p ∧ F↦H∈q", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}	
	
	public void testArithmeticRule() {
		Predicate input, expected;
		
		Expression E = FastFactory.mFreeIdentifier("E", INT);

		input = FastFactory.mRelationalPredicate(
				Formula.IN,
				E,
				FastFactory.mSetExtension(
						FastFactory.mList(
								FastFactory.mAssociativeExpression(
										Formula.MUL, 
										FastFactory.mList(
											FastFactory.mIntegerLiteral(10),
											FastFactory.mIntegerLiteral(20))),
								FastFactory.mIntegerLiteral(20))));
		
		expected = FastFactory.mLiteralPredicate(Formula.BTRUE);
		
		/*
		Predicate p = FastFactory.mQuantifiedPredicate(
				Formula.FORALL,
				FastFactory.mList(FastFactory.mBoundIdentDecl("x", INT)),
				FastFactory.mRelationalPredicate(
						Formula.GE,
						FastFactory.mBoundIdentifier(1, INT),
						FastFactory.mBoundIdentifier(0, INT),
						),
				);
		
		
		System.out.println("P: " + p);
		p = p.shiftBoundIdentifiers(1000, ff);
		System.out.println("P: " + p);
		p = p.shiftBoundIdentifiers(-1001, ff);
		System.out.println("P: " + p);
		*/
		
		doTest(input, expected);		
	}
	
	public void testCardinality1() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"S", "n"}, new Type[]{INT_SET, INT});
		Predicate input = parse("card(S)=n", te);
		Predicate expected = parse("∃f·f∈S⤖1‥n", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}

	public void testCardinality2() {
		Predicate input = parse("card({1}) > 1");
		Predicate expected = parse("∀x·x=card({1})⇒x>1");
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testCardinality3() {
		Predicate input =  parse("card({card({1})}) = card({2})");
		Predicate expected = parse("∀x,x0·x0=card({card({1})})∧x=card({2}) ⇒ x0=x");
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testCardinality4() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"S"}, new Type[]{INT_SET});
		Predicate input =  parse("∀s·card(S)>card({t·t>s∧t<card({t,t})∣t})", te);
		Predicate expected = parse("∀s·∀x,x0·x0=card(S)∧x=card({t·t>s∧t<card({t,t})∣t})⇒x0>x", te);
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}	

	public void testBool() {
		Predicate input, expected;
		
		Expression E = FastFactory.mFreeIdentifier("E", BOOL);

		Expression x = FastFactory.mFreeIdentifier("x", INT);
		Expression y = FastFactory.mFreeIdentifier("y", INT);

		input = FastFactory.mRelationalPredicate(
				Formula.IN,
				E,
				FastFactory.mSetExtension(
						FastFactory.mAtomicExpression(Formula.FALSE),
						FastFactory.mBoolExpression(
								FastFactory.mRelationalPredicate(Formula.GT, x, y))));
		
		expected = FastFactory.mLiteralPredicate(Formula.BTRUE);
		
		doTest(input, expected);
	}
	
	public void testBool2() {
		Predicate input, expected;
		
		Expression E = FastFactory.mFreeIdentifier("E", BOOL);

		input = FastFactory.mRelationalPredicate(
				Formula.IN,
				E,
				FastFactory.mSetExtension(
						FastFactory.mBoolExpression(
								FastFactory.mRelationalPredicate(
										Formula.EQUAL,
										FastFactory.mAtomicExpression(Formula.TRUE),
										FastFactory.mBoolExpression(FastFactory.mLiteralPredicate(Formula.BFALSE))))));
		
		expected = FastFactory.mLiteralPredicate(Formula.BTRUE);
		
		doTest(input, expected);
	}
	
	public void testBool3() {
		Predicate input = parse("e ∈ {bool(⊤)↦bool(1>2)}");
		Predicate expected = FastFactory.mLiteralPredicate(Formula.BTRUE);
		
		doTest(input, expected);
	}
	
	public void testCardExt() {
		Predicate input = parse("e ∈ {card({1})↦card({1})}");
		Predicate expected = FastFactory.mLiteralPredicate(Formula.BTRUE);
		
		doTest(input, expected);
	}
	
	private void doTest(Predicate input, Predicate expected) {
		doTest(input, expected, translator);
	}
	
	private void doTest(Formula input, Formula expected, TestTranslation translation) {
		ITypeCheckResult tcr = null;
		tcr = input.typeCheck(FastFactory.mTypeEnvironment());
		assertTrue("Input is not typed: " + tcr.getProblems(), tcr.isSuccess());
		tcr=expected.typeCheck(FastFactory.mTypeEnvironment());
		assertTrue("Expected result is not typed: " + tcr.getProblems(), tcr.isSuccess());

		Formula actual = translation.translate(input, ff);
		
		tcr=actual.typeCheck(FastFactory.mTypeEnvironment());
		assertTrue("Actual result is not typed: " + tcr.getProblems(), tcr.isSuccess());
		if(actual instanceof Predicate)
			assertTrue("Result not in goal: " + actual, GoalChecker.isInGoal((Predicate)actual, ff));
		assertEquals("Unexpected result of translation", expected, actual);
	}
}
