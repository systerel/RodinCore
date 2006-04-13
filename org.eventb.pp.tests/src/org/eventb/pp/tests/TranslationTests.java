package org.eventb.pp.tests;

import static org.eventb.pp.tests.FastFactory.mAssociativePredicate;

import java.math.BigInteger;

import junit.framework.TestCase;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
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
		IParseResult parseResult = ff.parsePredicate(string);
		assertTrue("Parse error for: " + string + " Problems: " + parseResult.getProblems(), parseResult.isSuccess());
		Predicate pred = parseResult.getParsedPredicate();
		ITypeCheckResult tcResult = pred.typeCheck(te);
		assertTrue(string + " is not typed. Problems: " + tcResult.getProblems(), tcResult.isSuccess());
		return pred;
	}
	
	public Predicate parse(String string) {
		return parse(string, ff.makeTypeEnvironment());
	}
	
	public static ITypeEnvironment typeEnv = ff.makeTypeEnvironment();
	
	ITypeEnvironment actTypeEnv = ff.makeTypeEnvironment();
	

	
	public void doTest(String input, String expected, boolean transformExpected) {
		Predicate pinput = parse(input, actTypeEnv);
		Predicate pexpected = parse(expected, actTypeEnv);
		if(transformExpected) pexpected = Translator.reduceToPredCalc(pexpected, ff);
		doTest(pinput, pexpected);
	}
	
	/**
	 * Main test routine for predicates.
	 */
	public void testPredicateTranslation () {

		doTest( "⊤ ∧ ⊤",
				"⊤ ∧ ⊤", false);
	}
	
	public void testIdentifierDecomposition1() {
		doTest( "∀x·10↦20 = x", 
				"∀x,x0·10=x0∧20=x", false);		
	}

	public void testIdentifierDecomposition2() {
		doTest( "10↦20 = s",
				"∀x,x0·s=x0 ↦ x⇒10=x0∧20=x", false);
	}
	
	public void testIdentifierDecomposition3() {
		actTypeEnv = FastFactory.mTypeEnvironment(new String[]{"S"}, new Type[]{REL(BOOL, CPROD(INT, INT))});
		
		doTest( "E ∈ S", 
				"∀x,x0,x1·E=x1↦(x0↦x) ⇒ x1↦(x0↦x)∈S", false);		
	}
	
	
	public void testIdentifierDecomposition4() {
		actTypeEnv = FastFactory.mTypeEnvironment(new String[]{"s"}, new Type[]{CPROD(INT, INT)});
		
		doTest( "10 ↦ 20 = s",
				"∀x,x0·s=x0 ↦ x⇒10=x0∧20=x", false);
	}
	
	public void testIdentifierDecomposition5() {
		actTypeEnv = FastFactory.mTypeEnvironment(new String[]{"s"}, new Type[]{CPROD(INT, INT)});
		
		doTest( "t = s",
				"∀x,x0,x1,x2·t=x2↦x1∧s=x0↦x⇒x2=x0∧x1=x", false);
	}
	
	public void testIdentifierDecomposition6() {
		actTypeEnv = FastFactory.mTypeEnvironment(new String[]{"s"}, new Type[]{CPROD(INT, INT)});
		
		doTest( "t = s",
				"∀x,x0,x1,x2·t=x2↦x1∧s=x0↦x⇒x2=x0∧x1=x", false);
	}

	public void testIdentifierDecomposition7() {
		actTypeEnv = FastFactory.mTypeEnvironment(new String[]{"t"}, new Type[]{CPROD(INT, INT)});
		doTest( "t=t",
				"∀x,x0·t=x0↦x⇒(x0=x0∧x=x)", false);
	}
	
	public void testIdentifierDecomposition8() {
		actTypeEnv = FastFactory.mTypeEnvironment(
				new String[]{"S"}, new Type[]{REL(INT, CPROD(BOOL, CPROD(INT, BOOL)))});
		doTest( "∀a·∃b·∀c·∃d·E∈S∧ a=1 ∧ b=1 ∧ c=1 ∧ d=1",
				"∀x,x0,x1,x2·E=x2 ↦ (x1 ↦ (x0 ↦ x))⇒(∀a·∃b·∀c·∃d·x2 ↦ (x1 ↦ (x0 ↦ x))∈S∧a=1∧b=1∧c=1∧d=1)", false);
		
	}
	
	public void testSubsetEqRule() {
		actTypeEnv = FastFactory.mTypeEnvironment(
				new String[]{"s", "t"}, new Type[]{INT_SET, INT_SET});
		doTest( "s⊆t",
				"∀x·x∈s⇒x∈t", false);
	}
	
	public void testSubsetEqRule2() {
		actTypeEnv = FastFactory.mTypeEnvironment(
				new String[]{"s", "t"}, new Type[]{REL(BOOL, CPROD(INT, BOOL)), REL(BOOL, CPROD(INT, BOOL))});
		doTest( "s⊆t",
				"∀x,x0,x1·x1↦(x0↦x)∈s⇒x1↦(x0↦x)∈t", false);
	}
	
	public void testNotSubsetEqRule() {
		actTypeEnv = FastFactory.mTypeEnvironment(
				new String[]{"s", "t"}, new Type[]{INT_SET, INT_SET});
		doTest( "s⊈t",
				"¬(∀x·x∈s⇒x∈t)", false);
	}
	
	public void testNotSubsetEqRule2() {
		actTypeEnv = FastFactory.mTypeEnvironment(
				new String[]{"s", "t"}, new Type[]{REL(BOOL, CPROD(INT, BOOL)), REL(BOOL, CPROD(INT, BOOL))});
		doTest( "s⊈t",
				"¬(∀x,x0,x1·x1↦(x0↦x)∈s⇒x1↦(x0↦x)∈t)", false);
	}
	
	public void testSubsetRule() {
		actTypeEnv = FastFactory.mTypeEnvironment(
				new String[]{"s", "t"}, new Type[]{INT_SET, INT_SET});
		doTest( "s⊂t",
				"(∀x·x∈s⇒x∈t)∧¬(∀x·x∈t⇒x∈s)", false);
	}

	public void testSubsetRule2() {
		actTypeEnv = FastFactory.mTypeEnvironment(
				new String[]{"s", "t"}, new Type[]{REL(BOOL, CPROD(INT, BOOL)), REL(BOOL, CPROD(INT, BOOL))});
		doTest( "s⊂t", "(∀x,x0,x1·x1↦(x0↦x)∈s⇒x1↦(x0↦x)∈t)∧¬(∀x,x0,x1·x1↦(x0↦x)∈t⇒x1↦(x0↦x)∈s)", false);
	}

	public void testNotSubsetRule() {
		actTypeEnv = FastFactory.mTypeEnvironment(
				new String[]{"s", "t"}, new Type[]{INT_SET, INT_SET});
		doTest( "s⊄t",
				"¬(∀x·x∈s⇒x∈t)∨(∀x·x∈t⇒x∈s)", false);
	}

	public void testNotSubsetRule2() {
		actTypeEnv = FastFactory.mTypeEnvironment(
				new String[]{"s", "t"}, new Type[]{REL(BOOL, CPROD(INT, BOOL)), REL(BOOL, CPROD(INT, BOOL))});
		doTest( "s⊄t",
				"¬((∀x,x0,x1·x1↦(x0↦x)∈s⇒x1↦(x0↦x)∈t))∨(∀x,x0,x1·x1↦(x0↦x)∈t⇒x1↦(x0↦x)∈s)", false);
	}
	
	public void testFiniteRule() {
		actTypeEnv = FastFactory.mTypeEnvironment(new String[]{"S"}, new Type[]{INT_SET});
		doTest( "finite(S)",
				"∀a·∃b,f·f∈(S↣a‥b)", true);
	}
	
	public void testFiniteRule2() {
		actTypeEnv = FastFactory.mTypeEnvironment(new String[]{"S", "T"}, new Type[]{INT_SET, INT_SET});
		doTest( "finite(ℙ(S∪T))",
				"∀a·∃b,f·f∈(ℙ(S∪T)↣a‥b)", true);
	}
		
	public void testPowerSetInRule1() {
		actTypeEnv = FastFactory.mTypeEnvironment(new String[]{"T", "E"}, new Type[]{INT_SET, INT_SET});
		doTest( "E∈ℙ(T)",
				"∀x·x∈E⇒x∈T", true);
	}

	public void testPowerSetInRule2() {
		actTypeEnv = FastFactory.mTypeEnvironment(
				new String[]{"T", "E"}, new Type[]{POW(CPROD(BOOL, INT)), POW(CPROD(BOOL, INT))});
		
		doTest( "E∈ℙ(T)", 
				"∀x·x∈E⇒x∈T", true);
	}
	
	public void testPowerSetInRule3() {
		actTypeEnv = FastFactory.mTypeEnvironment(new String[]{"T", "S", "E"}, new Type[]{INT_SET, INT_SET, POW(INT_SET)});
		doTest( "E∈ℙ(ℙ(S∪T))",
				"∀x·x∈E⇒x∈ℙ(S∪T)", true);
	}

	public void testNaturalInRule() {
		doTest( "E∈ℕ",
				"E≥0", false);
	}
	
	public void testNaturalInRule2() {
		doTest( "(3∗4+5)∈ℕ",
				"3∗4+5≥0", false);
	}

	public void testNatural1InRule() {
		doTest( "E∈ℕ1",
				"E>0", false);
	}
	
	public void testIntegerInRule() {
		doTest( "E∈ℤ",
				"⊤", false);
	}
	
	public void testCSetInRule() {
		actTypeEnv = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT});
		doTest( "E∈{x·⊤ ∣ x}",
				"∃x·⊤∧E=x", false);
	}
	
	public void testCSetInRule2() {
		actTypeEnv = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT});
		doTest( "E∈{x·x>3∣ x∗2}",
				"∃x·x>3∧E=x∗2", false);
	}

	public void testCSetInRule3() {
		actTypeEnv = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT_SET});
		doTest( "E∈{x·x>3∣ {2}}",
				"∃x·x>3∧E={2}", true);
	}

	public void testQInterInRule() {
		actTypeEnv = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT});
		doTest( "E∈(⋂x·⊤ ∣ {x})",
				"∀x·⊤⇒E=x", false);
	}

	public void testQUnionInRule() {
		actTypeEnv = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT});
		doTest( "E∈(⋃x·⊤ ∣ {x})",
				"∃x·⊤∧E=x", false);
	}
	
	public void testUnionRule() {
		actTypeEnv = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT});
		doTest( "E∈union(S)",
				"∃x·x∈S∧E∈x", false);
	}

	public void testInterRule() {
		actTypeEnv = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT});
		doTest( "E∈inter(S)",
				"∀x·x∈S⇒E∈x", false);
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
		actTypeEnv = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, INT), INT_SET, INT_SET});
		doTest( "E∈S↔T",
				"dom(E)⊆S∧ran(E)⊆T", true);
	}
	
	public void testRelRule2() {		
		actTypeEnv = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, 
				new Type[]{REL(CPROD(BOOL, INT), INT_SET), REL(BOOL, INT), POW(INT_SET)});

		doTest( "E∈S↔T",
				"dom(E)⊆S∧ran(E)⊆T", true);
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
	
	public void testCardImgRule() {
		Type intRel = REL(INT, INT);
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"f", "g", "s", "t"}, new Type[]{intRel, intRel, intRel, intRel});
		Predicate input = parse("f(card({w·f(card({w·f(3)>1∣2∗w}))>1∣2∗w}))>1", te);
		Predicate expected = parse("f(card({w·f(card({w·f(3)>1∣2∗w}))>1∣2∗w}))>1", te);
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
		actTypeEnv = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, INT), INT_SET, INT_SET});
		
		doTest( "E∈S↣T",
				"E∈S→T∧(∀C,B,A·(B↦A∈E∧C↦A∈E)⇒B=C)", true);
	}
	
	public void testTotInjRule2() {
		actTypeEnv = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT_SET, INT), INT_SET, INT_SET});
		
		doTest( "E∈ℙ(S∪T)↣T",
				"E∈ℙ(S∪T)→T∧(∀C,B,A·(B↦A∈E∧C↦A∈E)⇒B=C)", true);
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
		actTypeEnv = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, INT), INT_SET, INT_SET});
		
		doTest( "E∈S⇸T",
				"E∈S↔T∧(∀C,B,A·(A↦B∈E∧A↦C∈E)⇒B=C)", true);
	}
	
	public void testPartFunRule2() {
		actTypeEnv = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, INT), INT_SET, INT_SET});
		
		doTest( "E∈S∪T⇸T",
				"E∈S∪T↔T∧(∀C,B,A·(A↦B∈E∧A↦C∈E)⇒B=C)", true);
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
		Predicate input = parse("E↦F∈(r∼)", te);
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
		Predicate input = parse("E∈{10∗20,20}");
		Predicate expected = parse("E=10∗20 ∨ E=20");
		expected = Translator.reduceToPredCalc(expected, ff);
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
		Predicate input = parse("E∈{FALSE,bool(x>y)}");
		Predicate expected = parse("E=FALSE ∨ E=bool(x>y)");
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testBool2() {
		Predicate input = parse("E∈{bool(TRUE=bool(⊥))}");
		Predicate expected = parse("E=bool(TRUE=bool(⊥))");
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);	
	}
	
	public void testBool3() {
		Predicate input = parse("e ∈ {bool(⊥)↦bool(1>2)}");
		Predicate expected = parse("e ∈ {bool(⊥)↦bool(1>2)}");
		expected = Translator.reduceToPredCalc(expected, ff);
		doTest(input, expected);
	}
	
	public void testMinRule() {
		doTest( "n = min(S)",
				"n ∈ S ∧ (∀x·x∈S ⇒ n≤x)", true);
	}
	
	public void testMaxRule() {
		doTest( "n = max(S)",
				"n ∈ S ∧ (∀x·x ∈ S ⇒ x ≤ n)", true);
	}
	
	public void testReorganization1() {
		doTest( "E={card({1, 1})}",
				"E={card({1, 1})}", true);
	}
	
	public void testReorganization2() {
		doTest( "{1} = {1}",
				"{1} = {1}", true);
	}
	
	public void testReorganization3() {
		doTest( "{1} ∈ {{1}, {2}}",
				"{1} = {1} ∨ {1} = {2}", true);
	}
	
	public void testReorganization4() {
		doTest( "1↦(2↦3) ∈ E",
				"1↦(2↦3) ∈ E", true);
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
