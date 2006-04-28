package org.eventb.pp.tests;

import static org.eventb.pp.tests.FastFactory.mList;
import static org.eventb.pp.tests.FastFactory.mTypeEnvironment;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.pp.Translator;


/**
 * Ensures that the translator from set-theory to predicate calculus works
 * correctly.
 * 
 * 
 * @author Matthias Konrad
 */

public class TranslationTests extends AbstractTranslationTests {
	
	private static void doTest(String input, String expected, boolean transformExpected) {
		doTest(input, expected, transformExpected, FastFactory.mTypeEnvironment());
	}

	private static void doTest(String input, String expected, boolean transformExpected, ITypeEnvironment te) {
		Predicate pinput = parse(input, te);
		Predicate pexpected = parse(expected, te);
		if(transformExpected) {
			pexpected = Translator.decomposeIdentifiers(pexpected, ff);
			pexpected = Translator.reduceToPredicateCalulus(pexpected, ff);
		}
		doTest(pinput, pexpected);
	}
	
	private static void doTest(Predicate input, Predicate expected) {
		assertTrue("Input is not typed: " + input, input.isTypeChecked());
		assertTrue("Expected result is not typed: " + expected, 
				expected.isTypeChecked());

		Predicate actual = Translator.decomposeIdentifiers(input, ff);
		actual = Translator.reduceToPredicateCalulus(actual, ff);

		assertTrue("Actual result is not typed: " + actual,
				actual.isTypeChecked());
		assertTrue("Result not in goal: " + actual, Translator.isInGoal(actual));
		assertEquals("Unexpected result of translation", expected, actual);
	}
	
	/**
	 * Main test routine for predicates.
	 */
	
	public void testSubsetEqRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"s", "t"}, new Type[]{INT_SET, INT_SET});
		doTest( "s⊆t",
				"∀x·x∈s⇒x∈t", false, te);
	}
	
	public void testSubsetEqRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"s", "t"}, new Type[]{REL(BOOL, CPROD(INT, BOOL)), REL(BOOL, CPROD(INT, BOOL))});
		doTest( "s⊆t",
				"∀x,x0,x1·x↦(x0↦x1)∈s⇒x↦(x0↦x1)∈t", false, te);
	}

	public void testSubsetEqRule3() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"s", "t"}, new Type[]{INT_SET, INT_SET});
		doTest( "s∪t ⊆ s∪t",
				"∀x·x∈s∪t⇒x∈s∪t", true, te);
	}

	public void testNotSubsetEqRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"s", "t"}, new Type[]{INT_SET, INT_SET});
		doTest( "s⊈t",
				"¬(∀x·x∈s⇒x∈t)", false, te);
	}
	
	public void testNotSubsetEqRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"s", "t"}, new Type[]{REL(BOOL, CPROD(INT, BOOL)), REL(BOOL, CPROD(INT, BOOL))});
		doTest( "s⊈t",
				"¬(∀x,x0,x1·x↦(x0↦x1)∈s⇒x↦(x0↦x1)∈t)", false, te);
	}
	
	public void testNotSubsetEqRule3() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"s", "t"}, new Type[]{INT_SET, INT_SET});
		doTest( "s∪t⊈s∪t",
				"¬(∀x·x∈s∪t⇒x∈s∪t)", true, te);
	}

	public void testSubsetRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"s", "t"}, new Type[]{INT_SET, INT_SET});
		doTest( "s⊂t",
				"(∀x·x∈s⇒x∈t)∧¬(∀x·x∈t⇒x∈s)", false, te);
	}

	public void testSubsetRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"s", "t"}, new Type[]{REL(BOOL, CPROD(INT, BOOL)), REL(BOOL, CPROD(INT, BOOL))});
		doTest( "s⊂t", "(∀x,x0,x1·x↦(x0↦x1)∈s⇒x↦(x0↦x1)∈t)∧¬(∀x,x0,x1·x↦(x0↦x1)∈t⇒x↦(x0↦x1)∈s)", false, te);
	}

	public void testSubsetRule3() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"s", "t"}, new Type[]{INT_SET, INT_SET});
		doTest( "s∪t⊂s∪t",
				"(∀x·x∈s∪t⇒x∈s∪t)∧¬(∀x·x∈s∪t⇒x∈s∪t)", true, te);
	}

	public void testNotSubsetRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"s", "t"}, new Type[]{INT_SET, INT_SET});
		doTest( "s⊄t",
				"¬(∀x·x∈s⇒x∈t)∨(∀x·x∈t⇒x∈s)", false, te);
	}

	public void testNotSubsetRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"s", "t"}, new Type[]{REL(BOOL, CPROD(INT, BOOL)), REL(BOOL, CPROD(INT, BOOL))});
		doTest( "s⊄t",
				"¬((∀x,x0,x1·x↦(x0↦x1)∈s⇒x↦(x0↦x1)∈t))∨(∀x,x0,x1·x↦(x0↦x1)∈t⇒x↦(x0↦x1)∈s)", false, te);
	}
	
	public void testNotSubsetRule3() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"s", "t"}, new Type[]{INT_SET, INT_SET});
		doTest( "s∪t⊄s∪t",
				"¬(∀x·x∈s∪t⇒x∈s∪t)∨(∀x·x∈s∪t⇒x∈s∪t)", true, te);
	}

	public void testFiniteRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"S"}, new Type[]{INT_SET});
		doTest( "finite(S)",
				"∀a·∃b,f·f∈(S↣a‥b)", true, te);
	}
	
	public void testFiniteRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"t"}, new Type[]{CPROD(CPROD(BOOL, INT), BOOL)});
		doTest( "∀y·y=t∨finite({y})",
				"∀y·y=t∨(∀a·∃b,f·f∈({y}↣a‥b))", true, te);
	}
	
	public void testFiniteRule3() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"S", "T"}, new Type[]{INT_SET, INT_SET});
		doTest( "finite(ℙ(S∪T))",
				"∀a·∃b,f·f∈(ℙ(S∪T)↣a‥b)", true, te);
	}
		
	public void testPowerSetInRule1() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"T", "E"}, new Type[]{INT_SET, INT_SET});
		doTest( "E∈ℙ(T)",
				"∀x·x∈E⇒x∈T", true, te);
	}

	public void testPowerSetInRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"T", "E"}, new Type[]{POW(CPROD(BOOL, INT)), POW(CPROD(BOOL, INT))});
		
		doTest( "E∈ℙ(T)", 
				"∀x·x∈E⇒x∈T", true, te);
	}
	
	public void testPowerSetInRule3() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"T", "S", "E"}, new Type[]{INT_SET, INT_SET, POW(INT_SET)});
		doTest( "E∪E∈ℙ(ℙ(S∪T))",
				"∀x·x∈E∪E⇒x∈ℙ(S∪T)", true, te);
	}
	
	public void testPowerSetInRule4() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"T", "E"}, new Type[]{INT_SET, INT_SET});
		doTest( "∀s·E∈ℙ({s})",
				"∀s·(∀x·x∈E⇒x=s)", false, te);
	}

	public void testNaturalInRule() {
		doTest( "E∈ℕ",
				"E≥0", false);
	}
	
	public void testNaturalInRule2() {
		doTest( "card({1,2})∈ℕ",
				"∀c·c=card({1,2})⇒c≥0", true);
	}

	public void testNatural1InRule() {
		doTest( "E∈ℕ1",
				"E>0", false);
	}
	
	public void testNatural1InRule2() {
		doTest( "card({1,2})∈ℕ1",
				"∀c·c=card({1,2})⇒c>0", true);
	}

	public void testIntegerInRule() {
		doTest( "E∈ℤ",
				"⊤", false);
	}
	
	public void testCSetInRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT});
		doTest( "E∈{x·⊤ ∣ x}",
				"∃x·⊤∧E=x", false, te);
	}
	
	public void testCSetInRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT});
		doTest( "E∈{x·x∈{1}∣ x∗2}",
				"∃x·x∈{1}∧E=x∗2", true, te);
	}

	public void testCSetInRule3() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT_SET});
		doTest( "E∈{x·x>3∣ {2}}",
				"∃x·x>3∧E={2}", true, te);
	}
	
	public void testCSetInRule4() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{REL(BOOL, INT)});
		doTest( "∀y·({bool(y>3)↦1∗2}∈{x·x>3∣{bool(y>x)↦x}})",
				"∀y·({bool(y>3)↦1∗2}∈{x·x>3∣{bool(y>x)↦x}})", true, te);
	}
	
	public void testQInterInRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT});
		doTest( "E∈(⋂x·⊤ ∣ {x})",
				"∀x·⊤⇒E=x", false, te);
	}

	public void testQInterInRule1() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{REL(BOOL, INT)});
		doTest( "∀y·(bool(y>3)↦1∗2∈(⋂x·x∈{1}∣{bool(y>x)↦x}))",
				"∀y·(bool(y>3)↦1∗2∈(⋂x·x∈{1}∣{bool(y>x)↦x}))", true, te);
	}

	public void testQUnionInRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT});
		doTest( "E∈(⋃x·⊤ ∣ {x})",
				"∃x·⊤∧E=x", false, te);
	}
	
	public void testQUnionInRule1() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{REL(BOOL, INT)});
		doTest( "∀y·(bool(y>3)↦1∗2∈(⋃x·x∈{1}∣{bool(y>x)↦x}))",
				"∀y·(bool(y>3)↦1∗2∈(⋃x·x∈{1}∣{bool(y>x)↦x}))", true, te);
	}
	
	public void testUnionRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT});
		doTest( "E∈union(S)",
				"∃x·x∈S∧E∈x", false, te);
	}

	public void testUnionRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT});
		doTest( "E ∈ union({{1}})",
				"∃x·x∈{{1}} ∧ E∈x ", true, te);
	}
	
	public void testUnionRule3() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{CPROD(BOOL, CPROD(INT, BOOL))});
		doTest( "∀t·E ∈ union({{t, t}, {t} ∪ {t}})",
				"∀t·(∃x·x∈{{t, t}, {t} ∪ {t}} ∧ E∈x) ", true, te);
	}


	public void testInterRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT});
		doTest( "E∈inter(S)",
				"∀x·x∈S⇒E∈x", false, te);
	}
	
	public void testInterRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT});
		doTest( "E ∈ inter({{1}})",
				"∀x·x∈{{1}}⇒E∈x ", true, te);
	}
	
	public void testInterRule3() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{CPROD(BOOL, CPROD(INT, BOOL))});
		doTest( "∀t·E ∈ inter({{t, t}, {t} ∪ {t}})",
				"∀t·(∀x·x∈{{t, t}, {t} ∪ {t}} ⇒ E∈x) ", true, te);
	}

	public void testPow1Rule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"T", "E"}, new Type[]{INT_SET, INT_SET});
		doTest( "E∈ℙ1(T)",
				"E∈ℙ(T) ∧ (∃x·x∈E)", true, te);
	}
	
	public void testPow1Rule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT_SET});
		doTest( "∀t,s·E∈ℙ1({s}∪t)",
				"(∀t,s·E∈ℙ({s}∪t) ∧(∃x·x∈E))", true, te);
	}
	
	public void testPow1Rule3() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"T", "E"}, new Type[]{POW(INT_SET), POW(INT_SET)});
		doTest( "∀S·E∪S∈ℙ1(T∪S)",
				"∀S·(∀x·x=E∪S⇒x∈ℙ(T∪S) ∧ (∃x0·x0∈x))", true, te);
	}

	public void testEmptySetRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT_SET});
		doTest( "E∈∅",
				"⊥", false, te);
	}
	
	public void testSetExtensionRule() {
		doTest( "E∈{a,b,1}",
				"E=a ∨ E=b ∨ E=1", false);
	}
	
	public void testSetExtensionRule2() {
		doTest( "E∈{1}",
				"E=1", false);
	}
	
	public void testSetExtensionRule3() {
		Predicate input = FastFactory.mRelationalPredicate(Formula.IN,
				FastFactory.mFreeIdentifier("E", INT), 
				FastFactory.mEmptySet(POW(INT)));
		Predicate expected = parse("⊥");
		doTest(input, expected);
	}
	
	public void testSetExtensionRule4() {
		doTest( "∃S·S∪E∈{S∪E, S, E, {1}}",
				"∃S·∀x·(x=S∪E⇒x=S∪E ∨ x=S ∨ x=E ∨ x={1})", true);
	}

	public void testUpToRule() {
		doTest( "E∈a‥b",
				"E≥a∧E≤b", false);
	}

	public void testUpToRule2() {
		doTest( "∀v·v∗card({13,v})∈(card({2,v})‥(v+2))",
				"∀v·∀E·(E=v∗card({13,v}) ⇒ E≥card({2,v}) ∧ E≤(v+2))", true);
	}

	public void testSetMinusRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{INT, INT_SET, INT_SET});
		doTest( "E∈S∖T", 
				"E∈S∧¬(E∈T)", true, te);
	}

	public void testSetMinusRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT});
		doTest( "E∈(S∪X) ∖ (T∪Y)", 
				"E∈S∪X ∧ ¬(E∈T∪Y)", true, te);
	}
	
	public void testSetMinusRule3() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S"}, new Type[]{INT, INT_SET});
		doTest( "∀F·E+F∈S∖{F}", 
				"∀F·∀x·x=E+F⇒x∈S∧¬(x∈{F})", true, te);
	}

	public void testBInterRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT});
		doTest( "E∈S∩T",
				"E∈S∧E∈T", true, te);
	}

	public void testBInterRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT});
		doTest( "E∈(S∪X)∩(T∪Y)",
				"E∈S∪X∧E∈T∪Y", true, te);
	}
	
	public void testBInterRule3() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT});
		doTest( "∀e·e+3 ∈ {e}∩{3}∩{6}",
				"∀e·∀x·x=e+3⇒x∈{e}∧x∈{3}∧x∈{6}", true, te);
	}

	public void testBUnionRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT});
		doTest( "E∈S∪T", 
				"E∈S∨E∈T", true, te);
	}
	
	public void testBUnionRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT});
		doTest( "E∈(S∪X)∪(T∪Y)", 
				"E∈S∪X∨E∈T∪Y", true, te);
	}
	
	public void testBUnionRule3() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[]{"E"}, new Type[]{INT});
		doTest( "∀e·e+3 ∈ {e}∪{3}∪{6}", 
				"∀e·∀x·x=e+3⇒x∈{e}∨x∈{3}∨x∈{6}", true, te);
	}

	public void testRelRule() {		
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, BOOL), INT_SET, POW(BOOL)});
		doTest( "E∈S↔T",
				"dom(E)⊆S∧ran(E)⊆T", true, te);
	}
	
	public void testRelRule2() {		
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, BOOL), INT_SET, POW(BOOL)});
		doTest( "E∈(S∪X)↔(T∪Y)",
				"dom(E)⊆(S∪X)∧ran(E)⊆(T∪Y)", true, te);
	}
	
	public void testRelRule3() {		
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, 
				new Type[]{REL(CPROD(BOOL, INT), INT_SET), REL(BOOL, INT), POW(INT_SET)});

		doTest( "E∈S↔T",
				"dom(E)⊆S∧ran(E)⊆T", true, te);
	}
	
	public void testRelRule4() {		
		doTest( "∀e·{e+3↦e} ∈ {e} ↔ {e, 2}",
				"∀e·∀x·x={e+3↦e}⇒dom(x)⊆{e}∧ran(x)⊆{e,2}", true);
	}

	public void testRelImgRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "r", "w"}, new Type[]{BOOL, REL(INT, BOOL), INT_SET});
		doTest( "E∈r[w]", 
				"∃x·x∈w∧x↦E∈r", false, te);
	}
	
	public void testRelImgRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "r", "w"}, new Type[]{BOOL, REL(INT, BOOL), INT_SET});
		doTest( "∀t,v·E∈(r∪t)[w∪v]", 
				"∀t,v·∃x·x∈(w∪v)∧x↦E∈(r∪t)", true, te);
	}
	
	public void testRelImgRule3() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"r", "w"}, new Type[]{REL(INT, BOOL), INT_SET});
		doTest( "∀E,t,v·E∈(r∪t)[w∪v]", 
				"∀E,t,v·∃x·x∈(w∪v)∧x↦E∈(r∪t)", true, te);
	}

	public void testFunImgRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "f", "w"}, new Type[]{INT, REL(INT, INT_SET), INT});
		doTest( "E∈f(w)",
				"∃x·w↦x∈f∧E∈x", true, te);
	}
	
	public void testFunImgRule2() {
		Type intRel = REL(INT, INT);
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"f", "g", "s", "t"}, new Type[]{intRel, intRel, intRel, intRel});
		doTest( "∀n·g(s(t(n))) > n", 
				"∀n·(∀x·x=g(s(t(n))) ⇒ x>n)", true, te);
	}

	public void testFunImgRule3() {
		Type intRel = REL(INT, INT);
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"f", "g", "s", "t"}, new Type[]{intRel, intRel, intRel, intRel});
		doTest( "f(23) − g(s(t(10))) > g(29)", 
				"∀x,x0,x1·x1=f(23)∧x0=g(s(t(10)))∧x=g(29) ⇒ x1−x0>x", true, te);
	}
	
	public void testCardImgRule() {
		Type intRel = REL(INT, INT);
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"f", "g", "s", "t"}, new Type[]{intRel, intRel, intRel, intRel});
		doTest("f(card({w·f(card({w·f(3)>1∣2∗w}))>1∣2∗w}))>1", 
				"f(card({w·f(card({w·f(3)>1∣2∗w}))>1∣2∗w}))>1", true, te);
	}

	public void testRangeRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"r", "E"}, new Type[]{REL(INT, BOOL), BOOL});
		doTest( "E∈ran(r)", 
				"∃x·x↦E∈r", true, te);
	}
	
	public void testRangeRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"r"}, new Type[]{REL(BOOL, INT)});
		doTest( "∀t,E·E∈ran(r∪t)", 
				"∀t,E·∃x·x↦E∈r∪t", true, te);
	}


	public void testDomainRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"r", "E"}, new Type[]{REL(INT, BOOL), INT});
		doTest( "E∈dom(r)", 
				"∃x·E↦x∈r", true, te);
	}
	
	public void testDomainRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"r"}, new Type[]{REL(BOOL, INT)});
		
		doTest( "∀t,E·E∈dom(r∪t)", 
				"∀t,E·∃x·E↦x∈r∪t", true, te);
	}

	public void testTotRelRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, BOOL), INT_SET, POW(BOOL)});
		
		doTest( "E∈ST", 
				"E∈S↔T∧S⊆dom(E)", true, te);
	}

	public void testTotRelRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, BOOL), INT_SET, POW(BOOL)});
		
		doTest( "E∈(S)(T∪Y)", 
				"E∈(S)↔(T∪Y)∧(S)⊆dom(E)", true, te);
	}
	
	public void testTotRelRule3() {
		doTest( "∀e·{e+3↦(e↦2)} ∈ {e}  {e↦2}", 
				"∀e·(∀S,E·(E={e+3↦(e↦2)}∧S={e} ⇒ E∈S↔{e↦2} ∧ S ⊆ dom(E)))", true);
	}

	public void testSurRelRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, BOOL), INT_SET, POW(BOOL)});
		
		doTest( "E∈ST", 
				"E∈S↔T∧T⊆ran(E)", true, te);
	}
	
	public void testSurRelRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, BOOL), INT_SET, POW(BOOL)});
		
		doTest( "E∈(S∪X)(T)", 
				"E∈(S∪X)↔(T)∧(T)⊆ran(E)", true, te);
	}

	public void testSurRelRule3() {
		doTest( "∀e·{e+3↦(e↦2)} ∈ {e}  {e↦2}", 
				"∀e·(∀T,E·(E={e+3↦(e↦2)}∧T={e↦2} ⇒ E∈{e}↔T ∧ T ⊆ ran(E)))", true);
	}

	public void testSurjTotalRelRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, BOOL), INT_SET, POW(BOOL)});
		
		doTest( "E∈(S∪X)(T)", 
				"E∈(S∪X)(T)∧(T)⊆ran(E)", true, te);
	}

	public void testSurjTotalRelRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, BOOL), INT_SET, POW(BOOL)});
		
		doTest( "E∈(S)(T)", 
				"E∈(S)(T)∧(T)⊆ran(E)", true, te);
	}
	
	public void testSurjTotalRelRule3() {
		doTest( "∀e·{e+3↦e} ∈ {e}  {e, 2}",
				"∀e·∀T,E·E={e+3↦e}∧T={e, 2} ⇒ E∈{e}T ∧ T ⊆ ran(E)", true);
	}

	public void testTotBijRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, BOOL), INT_SET, POW(BOOL)});
		
		doTest( "E∈S⤖T", 
				"E∈S↠T∧(∀x,x0,x1·(x0↦x1∈E∧x↦x1∈E)⇒x0=x)", true, te);
	}
	
	public void testTotBijRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"S", "T"}, new Type[]{INT_SET, POW(BOOL)});
		
		doTest( "∀E,X,Y·E∈S∪X⤖T∪Y",
				"∀E,X,Y·E∈S∪X↠T∪Y∧(∀x,x0,x1·(x0↦x1∈E∧x↦x1∈E)⇒x0=x)", true, te);
	}

	public void testTotBijRule3() {
		doTest( "∀e·{e+3↦e} ∈ {e} ⤖ {e, 2}",
				"∀e·(∀E·(E={e+3↦e} ⇒ E∈{e}↠{e, 2} ∧ (∀C,B,A·(B↦A∈E∧C↦A∈E)⇒B=C)))", true);
	}

	public void testTotSurjRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, BOOL), INT_SET, POW(BOOL)});
		
		doTest( "E∈S↠T",
				"E∈S→T∧T⊆ran(E)", true, te);
	}
	
	public void testTotSurjRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, BOOL), INT_SET, POW(BOOL)});
		
		doTest( "E∈S∪X↠T",
				"E∈S∪X→T∧T⊆ran(E)", true, te);
	}

	public void testTotSurjRule3() {
		doTest( "∀e·{e+3↦e} ∈ {e} ↠ {e, 2}",
				"∀e·(∀T,E·(E={e+3↦e}∧T={e, 2} ⇒ E∈{e}→T ∧ T ⊆ ran(E)))", true);
	}

	public void testParSurjRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, BOOL), INT_SET, POW(BOOL)});
		
		doTest( "E∈S⤀T",
				"E∈S⇸T∧T⊆ran(E)", true, te);
	}
	
	public void testParSurjRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, BOOL), INT_SET, POW(BOOL)});
		
		doTest( "E∈S∪X⤀T",
				"E∈S∪X⇸T∧T⊆ran(E)", true, te);
	}

	public void testParSurjRule3() {
		doTest( "∀e·{e+3↦e} ∈ {e} ⤀{e, 2}",
				"∀e·(∀T,E·(E={e+3↦e}∧T={e, 2} ⇒ E∈{e}⇸T ∧ T ⊆ ran(E)))", true);
	}

	public void testTotInjRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, BOOL), INT_SET, POW(BOOL)});
		
		doTest( "E∈S↣T",
				"E∈S→T∧(∀C,B,A·(B↦A∈E∧C↦A∈E)⇒B=C)", true, te);
	}
	
	public void testTotInjRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT_SET, INT), INT_SET, INT_SET});
		
		doTest( "E∈ℙ(S∪T)↣T",
				"E∈ℙ(S∪T)→T∧(∀C,B,A·(B↦A∈E∧C↦A∈E)⇒B=C)", true, te);
	}

	public void testTotInjRule3() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, BOOL), INT_SET, POW(BOOL)});
		
		doTest( "∀E,X,Y·E∈S∪X↣T∪Y",
				"∀E,X,Y·(E∈S∪X→T∪Y∧(∀C,B,A·(B↦A∈E∧C↦A∈E)⇒B=C))", true, te);
	}

	public void testTotInjRule4() {
		doTest( "∀e·{e+3↦e} ∈ {e} ↣ {e, 2}",
				"∀e·∀E·E={e+3↦e} ⇒ E∈{e}→{e,2} ∧ (∀C,B,A·(B↦A∈E∧C↦A∈E)⇒B=C)", true);
	}

	public void testPartInjRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, BOOL), INT_SET, POW(BOOL)});

		doTest( "E∈S⤔T", 
				"E∈S⇸T∧(∀C,B,A·(B↦A∈E∧C↦A∈E)⇒B=C)", true, te);
	}

	public void testPartInjRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"S", "T"}, new Type[]{INT_SET, POW(BOOL)});

		doTest( "∀E,X,Y·E∈S∪X⤔T∪Y", 
				"∀E,X,Y·E∈S∪X⇸T∪Y∧(∀C,B,A·(B↦A∈E∧C↦A∈E)⇒B=C)", true, te);
	}

	public void testPartInjRule3() {
		doTest( "∀e·{e+3↦e} ∈ {e} ⤔ {e, 2}",
				"∀e·∀E·E={e+3↦e} ⇒ E∈{e}⇸{e,2} ∧ (∀C,B,A·(B↦A∈E∧C↦A∈E)⇒B=C)", true);
	}


	public void testTotFunRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, BOOL), INT_SET, POW(BOOL)});
		
		doTest( "E∈S→T", 
				"E∈S⇸T∧S⊆dom(E)", true, te);
	}
	
	public void testTotFunRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"S", "T"}, new Type[]{INT_SET, POW(BOOL)});
		
		doTest( "∀E,Y·E∈S→T∪Y", 
				"∀E,Y·E∈S⇸T∪Y∧S⊆dom(E)", true, te);
	}

	public void testTotFunRule3() {
		doTest( "∀e·{e+3↦e} ∈ {e} → {e, 2}",
				"∀e·(∀S,E·(E={e+3↦e}∧S={e}⇒ E∈S⇸{e,2} ∧ S⊆dom(E)))", true);
	}


	public void testPartFunRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "S", "T"}, new Type[]{REL(INT, BOOL), INT_SET, POW(BOOL)});
		
		doTest( "E∈S⇸T",
				"E∈S↔T∧(∀C,B,A·(A↦B∈E∧A↦C∈E)⇒B=C)", true, te);
	}
	
	public void testPartFunRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"S", "T"}, new Type[]{INT_SET, POW(BOOL)});
		
		doTest( "∀E,X,Y·E∈S∪X⇸T∪Y",
				"∀E,X,Y·E∈S∪X↔T∪Y∧(∀C,B,A·(A↦B∈E∧A↦C∈E)⇒B=C)", true, te);
	}	

	public void testPartFunRule3() {
		doTest( "∀e·{e+3↦e} ∈ {e} ⇸ {e, 2}",
				"∀e·∀E·E={e+3↦e} ⇒ E∈{e}↔{e,2} ∧ (∀C,B,A·(A↦B∈E∧A↦C∈E)⇒B=C)", true);
	}

	public void testCProdRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "S", "T"}, new Type[]{INT, BOOL, INT_SET, POW(BOOL)});

		doTest( "E↦F∈S×T", 
				"E∈S∧F∈T", true, te);
	}
	
	public void testCProdRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "S", "T"}, new Type[]{INT, BOOL, INT_SET, POW(BOOL)});

		doTest( "E↦F∈(S∪X)×(T∪Y)", 
				"E∈(S∪X)∧F∈(T∪Y)", true, te);
	}
	
	public void testRelOvrRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "q", "r"}, new Type[]{INT, BOOL, REL(INT, BOOL), REL(INT, BOOL)});

		doTest( "E↦F∈qr", 
				"E↦F∈dom(r)⩤q∨E↦F∈r", true, te);
	}

	public void testRelOvrRule2() {
		Type rt = REL(INT, BOOL);
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "q", "s", "t", "w"}, new Type[]{INT, BOOL, rt, rt, rt, rt});
		
		doTest( "E↦F∈qstw", 
				"E↦F∈(dom(s)∪dom(t)∪dom(w))⩤q ∨E↦F∈(dom(t)∪dom(w))⩤s ∨ E↦F∈dom(w)⩤t ∨ E↦F∈w", true, te);
 	}
	
	public void testRelOvrRule3() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "q", "r"}, new Type[]{INT, BOOL, REL(INT, BOOL), REL(INT, BOOL)});

		doTest( "E↦F∈(q∪q2)(r∪r2)", 
				"∀R·R=r∪r2⇒E↦F∈dom(R)⩤(q∪q2)∨E↦F∈(R)", true, te);
	}
	
	public void testRelOvrRule4() {
		doTest( "∀e,f,q2,r2·E+e↦F+f∈(q∪q2)(r∪r2)", 
				"∀e,f,q2,r2·(∀rr,EF·EF=E+e↦F+f∧rr=r∪r2 ⇒ EF∈dom(rr)⩤(q∪q2)∨EF∈(rr))", true);
	}

	/* Not allowed by type checker
	public void testRelOvrRule4() {
		Predicate input = FastFactory.mRelationalPredicate(
				FastFactory.mFreeIdentifier("E", CPROD(INT, BOOL)),
				FastFactory.mAssociativeExpression(
						Formula.OVR, 
						FastFactory.mFreeIdentifier("r", REL(INT, BOOL))));
		Predicate expected = FastFactory.mRelationalPredicate(
				FastFactory.mFreeIdentifier("E", CPROD(INT, BOOL)),
				FastFactory.mFreeIdentifier("r", REL(INT, BOOL)));
		
		doTest(input, Translator.reduceToPredicateCalulus(expected, ff));
	}
	*/

	public void testRanSubRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "r", "T"}, new Type[]{INT, BOOL, REL(INT, BOOL), POW(BOOL)});
		
		doTest ("E↦F∈r⩥T", 
				"E↦F∈r∧¬(F∈T)", true, te);
	}

	public void testRanSubRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "r", "T"}, new Type[]{INT, BOOL, REL(INT, BOOL), POW(BOOL)});
		
		doTest ("E↦F∈(r∪r2)⩥(T∪S)", 
				"E↦F∈(r∪r2)∧¬(F∈T∪S)", true, te);
	}
	
	public void testRanSubRule3() {
		doTest ( "∀e1,f1,R1,S1·(e1+1↦f1+1∈(S1∪{1})⩤(R1∪{1↦1}))", 
				 "∀e1,f1,R1,S1·(∀E·E=e1+1⇒E↦f1+1∈R1∪{1↦1}∧¬(E∈S1∪{1}))", true);
	}

	public void testDomSubRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "r", "S"}, new Type[]{INT, BOOL, REL(INT, BOOL), INT_SET});
		
		doTest( "E↦F∈S⩤r", 
				"E↦F∈r∧¬(E∈S)", true, te);
	}
	
	public void testDomSubRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "r", "S"}, new Type[]{INT, BOOL, REL(INT, BOOL), INT_SET});
		
		doTest( "E↦F∈(T∪S)⩤(r∪r2)", 
				"E↦F∈(r∪r2)∧¬(E∈(T∪S))", true, te);
	}
	
	public void testDomSubRule3() {
		doTest ( "∀e1,f1,R1,T1·(e1+1↦f1+1∈(R1∪{1↦1})⩥(T1∪{1}))", 
				 "∀e1,f1,R1,T1·(∀F·F=f1+1⇒e1+1↦F∈R1∪{1↦1}∧¬(F∈T1∪{1}))", true);
	}

	public void testRanResRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "r", "T"}, new Type[]{INT, BOOL, REL(INT, BOOL), POW(BOOL)});
		
		doTest( "E↦F∈r▷T", 
				"E↦F∈r∧F∈T", true, te);
	}

	public void testRanResRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "r", "T"}, new Type[]{INT, BOOL, REL(INT, BOOL), POW(BOOL)});
		
		doTest( "E↦F∈(r∪r2)▷(T∪S)", 
				"E↦F∈(r∪r2)∧F∈(T∪S)", true, te);
	}

	public void testRanResRule3() {
		doTest ( "∀e1,f1,R1,T1·(e1+1↦f1+1∈(R1∪{1↦1})▷(T1∪{1}))", 
				 "∀e1,f1,R1,T1·(∀F·F=f1+1⇒e1+1↦F∈R1∪{1↦1}∧(F∈T1∪{1}))", true);
	}

	public void testDomResRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "r", "S"}, new Type[]{INT, INT, REL(INT, INT), INT_SET});
		doTest( "E↦F∈S◁r", 
				"E↦F∈r∧E∈S", true, te);
	}
	
	public void testDomResRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "r", "S"}, new Type[]{INT, INT, REL(INT, INT), INT_SET});
		doTest( "E↦F∈(S∪S2)◁(r∪r2)", 
				"E↦F∈(r∪r2)∧E∈(S∪S2)", true, te);
	}

	public void testDomResRule3() {
		doTest ( "∀e1,f1,R1,S1·(e1+1↦f1+1∈(S1∪{1})◁(R1∪{1↦1}))", 
				 "∀e1,f1,R1,S1·(∀E·E=e1+1⇒E↦f1+1∈R1∪{1↦1}∧ (E∈S1∪{1}))", true);
	}
	
	public void testIdRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "S"}, new Type[]{INT, INT, INT_SET});
		
		doTest( "E↦F∈id(S)",
				"E∈S∧E=F", true, te);
	}

	public void testIdRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "S"}, new Type[]{INT, INT, INT_SET});
		
		doTest( "E↦F∈id(S∪T)",
				"E∈(S∪T)∧E=F", true, te);
	}

	public void testIdRule3() {
		doTest( "∀S,e,f·(e+1↦f+1 ∈ id(S∪{1}))",
				"∀S,e,f·(∀E·E=e+1 ⇒ E∈S∪{1} ∧ E = f+1)", true);
	}

	public void testFCompRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "p", "q"}, new Type[]{INT, INT_SET, REL(INT, BOOL), REL(BOOL, INT_SET)});
		
		doTest( "E↦F∈p;q", 
				"∃x·E↦x∈p∧x↦F∈q", true, te);
	}
	
	public void testFCompRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "p", "q", "w"}, new Type[]{INT, POW(BOOL), REL(INT, BOOL), REL(BOOL, INT_SET), REL(INT_SET, POW(BOOL))});

		doTest( "E↦F∈p;q;w",
				"∃x,x0·E↦x0∈p∧x0↦x∈q∧x↦F∈w", true, te);
	}
	
	public void testFCompRule3() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"p", "q", "w"}, new Type[]{REL(INT, BOOL), REL(BOOL, INT_SET), REL(INT_SET, POW(BOOL))});

		doTest( "∀E,F,p2,q2,w2·E↦F∈(p∪p2);(q∪q2);(w∪w2)",
				"∀E,F,p2,q2,w2·(∃x,x0·E↦x0∈(p∪p2)∧x0↦x∈(q∪q2)∧x↦F∈(w∪w2))", true, te);
	}

	public void testBCompRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "p", "q"}, new Type[]{INT, INT_SET, REL(BOOL, INT_SET), REL(INT, BOOL)});

		doTest( "E↦F∈p∘q", 
				"E↦F∈q;p", true, te);
	}
	
	public void testBCompRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "p", "q", "w"}, new Type[]{INT, POW(BOOL), REL(INT_SET, POW(BOOL)), REL(BOOL, INT_SET), REL(INT, BOOL)});
		
		doTest( "E↦F∈(p∪p2)∘(q∪q2)∘(w∪w2)", 
				"E↦F∈(w∪w2);(q∪q2);(p∪p2)", true, te);
	}
	
	public void testInvRelRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "r"}, new Type[]{INT, BOOL, REL(BOOL, INT)});

		doTest( "E↦F∈(r∼)", 
				"F↦E∈r", true, te);
	}
	
	public void testInvRelRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "r"}, new Type[]{INT, BOOL, REL(BOOL, INT)});

		doTest( "E↦F∈((r∪r2)∼)", 
				"F↦E∈(r∪r2)", true, te);
	}
	
	public void testPrj1Rule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "G", "r"}, new Type[]{INT, BOOL, INT, REL(INT, BOOL)});
		
		doTest( "(E↦F)↦G ∈ prj1(r)", 
				"E↦F∈r ∧ G=E", true, te);
	}

	public void testPrj1Rule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "G", "r"}, new Type[]{INT, BOOL, INT, REL(INT, BOOL)});
		
		doTest( "(E↦F)↦G ∈ prj1((r∪r2))", 
				"E↦F∈(r∪r2) ∧ G=E", true, te);
	}

	public void testPrj1Rule3() {
		doTest( "∀r,e,f,g·(e+1↦f+1)↦g+1 ∈ prj1(r∪{1↦1})", 
				"∀r,e,f,g·(∀E·E=e+1 ⇒ E↦f+1∈r∪{1↦1} ∧ g+1=E)", true);
	}

	public void testPrj2Rule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "G", "r"}, new Type[]{INT, BOOL, BOOL, REL(INT, BOOL)});

		doTest( "(E↦F)↦G ∈ prj2(r)", 
				"E↦F∈r ∧ G=F", true, te);
	}
	
	public void testPrj2Rule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "G", "r"}, new Type[]{INT, BOOL, BOOL, REL(INT, BOOL)});

		doTest( "(E↦F)↦G ∈ prj2(r∪r2)", 
				"E↦F∈(r∪r2) ∧ G=F", true, te);
	}
	
	public void testPrj2Rule3() {
		doTest( "∀r,e,f,g·(e+1↦f+1)↦g+1 ∈ prj2(r∪{1↦1})", 
				"∀r,e,f,g·(∀F·F=f+1 ⇒ e+1↦F∈r∪{1↦1} ∧ g+1=F)", true);
	}

	public void testDirectProdRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "G", "p", "q"}, new Type[]{INT, BOOL, INT_SET, REL(INT, BOOL), REL(INT, INT_SET)});
		
		doTest( "E↦(F↦G) ∈ p⊗q",
				"E↦F∈p ∧ E↦G∈q", true, te);
	}
	
	public void testDirectProdRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "G", "p", "q"}, new Type[]{INT, BOOL, INT_SET, REL(INT, BOOL), REL(INT, INT_SET)});
		
		doTest( "E↦(F↦G) ∈ (p∪p2)⊗(q∪q2)",
				"E↦F∈(p∪p2) ∧ E↦G∈(q∪q2)", true, te);
	}
	
	public void testDirectProdRule3() {
		doTest( "∀p,q,e,f,g·e+1↦(f+1↦g+1) ∈ (p∪{1↦1})⊗(q∪{1↦1})",
				"∀p,q,e,f,g·(∀E·E=e+1 ⇒ E↦f+1∈p∪{1↦1} ∧ E↦g+1∈q∪{1↦1})", true);
	}
	
	public void testPrallelProdRule() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "G", "H", "p", "q"}, 
				new Type[]{INT, BOOL, INT_SET, POW(BOOL), REL(INT, INT_SET), REL(BOOL, POW(BOOL))});
		
		doTest( "(E↦F)↦(G↦H) ∈ p∥q",
				"E↦G∈p ∧ F↦H∈q", true, te);
	}	

	public void testPrallelProdRule2() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"E", "F", "G", "H", "p", "q"}, 
				new Type[]{INT, BOOL, INT_SET, POW(BOOL), REL(INT, INT_SET), REL(BOOL, POW(BOOL))});
		
		doTest( "(E↦F)↦(G↦H) ∈ (p∪p2)∥(q∪q2)",
				"E↦G∈(p∪p2) ∧ F↦H∈(q∪q2)", true, te);
	}	
	
	public void testArithmeticRule() {
		doTest( "E∈{10∗20,20}",
				"E=10∗20 ∨ E=20", true);
	}
	
	public void testCardinality1() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"S", "n"}, new Type[]{INT_SET, INT});
		doTest( "card(S)=n",
				"∃f·f∈S⤖1‥n", true, te);
	}

	public void testCardinality2() {
		doTest( "card({1}) > 1",
				"∀x·x=card({1})⇒x>1", true);
	}
	
	public void testCardinality3() {
		doTest( "card({card({1})}) = card({2})",
				"∀x,x0·x0=card({card({1})})∧x=card({2}) ⇒ x0=x", true);
	}
	
	public void testCardinality4() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(
				new String[]{"S"}, new Type[]{INT_SET});
		doTest( "∀s·card(S)>card({t·t>s∧t<card({t,t})∣t})",
				"∀s·∀x,x0·x0=card(S)∧x=card({t·t>s∧t<card({t,t})∣t})⇒x0>x", true, te);
	}	

	public void testBool() {
		doTest( "E∈{FALSE,bool(x>y)}",
				"E=FALSE ∨ E=bool(x>y)", true);
	}
	
	public void testBool2() {
		doTest( "E∈{bool(TRUE=bool(⊥))}",
				"E=bool(TRUE=bool(⊥))", true);
	}
	
	public void testBool3() {
		doTest( "e ∈ {bool(⊥)↦bool(1>2)}",
				"e ∈ {bool(⊥)↦bool(1>2)}", true);
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
	
//	public void testReorganization3() {
//		doTest( "{1} ∈ {{1}, {2}}",
//				"{1} = {1} ∨ {1} = {2}", true);
//	}
	
	public void testReorganization4() {
		doTest( "1↦(2↦3) ∈ E",
				"1↦(2↦3) ∈ E", true);
	}	
	
	public void testCardExt() {
		doTest ("e ∈ {card({1})↦card({1})}",
				"e ∈ {card({1})↦card({1})}", true);
	}
	
	public void testFunImgHard() {
		doTest( "∃f·f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(1)+1))+1))+1))+1))+1))+1))+1))+1))+1))+1))+1))+1)=1",
				"∃f·f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(f(1)+1))+1))+1))+1))+1))+1))+1))+1))+1))+1))+1))+1)=1", true);
	}
	
	public void testRelImgHard() {
		doTest( "∃r,n·r[r[r[r[r[r[r[r[r[r[r[r[{1+n}]∪{n}]∪{n}]∪{n}]∪{n}]∪{n}]∪{n}]∪{n}]∪{n}]∪{n}]∪{n}]∪{n}]∪{n}={1}",
				"∃r,n·r[r[r[r[r[r[r[r[r[r[r[r[{1+n}]∪{n}]∪{n}]∪{n}]∪{n}]∪{n}]∪{n}]∪{n}]∪{n}]∪{n}]∪{n}]∪{n}]∪{n}={1}", true);
	}
	/*
	public void testMinHard() {
		doTest( "E ∈ {a·⊤∣a+min({b·⊤∣b+min({c·⊤∣c+min({d·⊤∣d+min( {e·⊤∣e+min({f·⊤∣f+min({g·⊤∣g+min({h·⊤∣h+min({a·⊤∣a+min({b·⊤∣b+min({c·⊤∣c+min({d·⊤∣d+min( {e·⊤∣e+min({f·⊤∣f+min({g·⊤∣g+min({h·⊤∣h+min({1+a+b+c+d+e+f+g+h})})})})})})})})})})})})})})})})}",
				"E ∈ {a·⊤∣a+min({b·⊤∣b+min({c·⊤∣c+min({d·⊤∣d+min( {e·⊤∣e+min({f·⊤∣f+min({g·⊤∣g+min({h·⊤∣h+min({a·⊤∣a+min({b·⊤∣b+min({c·⊤∣c+min({d·⊤∣d+min( {e·⊤∣e+min({f·⊤∣f+min({g·⊤∣g+min({h·⊤∣h+min({1+a+b+c+d+e+f+g+h})})})})})})})})})})})})})})})})}", true);
	}

	public void testMaxHard() {
		doTest( "E ∈ {a·⊤∣a+max({b·⊤∣b+max({c·⊤∣c+max({d·⊤∣d+max( {e·⊤∣e+max({f·⊤∣f+max({g·⊤∣g+max({h·⊤∣h+max({a·⊤∣a+max({b·⊤∣b+max({c·⊤∣c+max({d·⊤∣d+max( {e·⊤∣e+max({f·⊤∣f+max({g·⊤∣g+max({h·⊤∣h+max({1+a+b+c+d+e+f+g+h})})})})})})})})})})})})})})})})}",
				"E ∈ {a·⊤∣a+max({b·⊤∣b+max({c·⊤∣c+max({d·⊤∣d+max( {e·⊤∣e+max({f·⊤∣f+max({g·⊤∣g+max({h·⊤∣h+max({a·⊤∣a+max({b·⊤∣b+max({c·⊤∣c+max({d·⊤∣d+max( {e·⊤∣e+max({f·⊤∣f+max({g·⊤∣g+max({h·⊤∣h+max({1+a+b+c+d+e+f+g+h})})})})})})})})})})})})})})})})}", true);
	}

	public void testCardHard() {
		doTest( "E ∈ {a·⊤∣a+card({b·⊤∣b+card({c·⊤∣c+card({d·⊤∣d+card( {e·⊤∣e+card({f·⊤∣f+card({g·⊤∣g+card({h·⊤∣h+card({1+a+b+c+d+e+f+g+h})})})})})})})})}",
				"E ∈ {a·⊤∣a+card({b·⊤∣b+card({c·⊤∣c+card({d·⊤∣d+card( {e·⊤∣e+card({f·⊤∣f+card({g·⊤∣g+card({h·⊤∣h+card({1+a+b+c+d+e+f+g+h})})})})})})})})}", true);
//		doTest( "E ∈ {a·⊤∣a+card({b·⊤∣b+card({c·⊤∣c+card({d·⊤∣d+card( {e·⊤∣e+card({f·⊤∣f+card({g·⊤∣g+card({h·⊤∣h+card({a·⊤∣a+card({b·⊤∣b+card({c·⊤∣c+card({d·⊤∣d+card( {e·⊤∣e+card({f·⊤∣f+card({g·⊤∣g+card({h·⊤∣h+card({1+a+b+c+d+e+f+g+h})})})})})})})})})})})})})})})})}",
//				"E ∈ {a·⊤∣a+card({b·⊤∣b+card({c·⊤∣c+card({d·⊤∣d+card( {e·⊤∣e+card({f·⊤∣f+card({g·⊤∣g+card({h·⊤∣h+card({a·⊤∣a+card({b·⊤∣b+card({c·⊤∣c+card({d·⊤∣d+card( {e·⊤∣e+card({f·⊤∣f+card({g·⊤∣g+card({h·⊤∣h+card({1+a+b+c+d+e+f+g+h})})})})})})})})})})})})})})})})}", true);
	}*/
	/*
	public void testD1() {
		ITypeEnvironment te = FastFactory.mTypeEnvironment(new String[] {"a"}, new Type[] {CPROD(INT, INT)});
		doTest( "a = b ∧ a ∈ S",
				"a=b", true, te);
	}*/
	
	public void testIn() {
		ITypeEnvironment te = mTypeEnvironment(mList("S"), mList(REL(BOOL,INT)));
		doTest( "a↦1 ∈ S",
				"∃x·a↦x ∈ S∧x=1", false, te);
	}
	
	public void testIn2() {
		ITypeEnvironment te = mTypeEnvironment(mList("S"), mList(REL(CPROD(BOOL, INT),INT)));
		doTest( "a↦1↦2 ∈ S",
				"∃x1,x2·a↦x1↦x2∈S ∧ x1=1 ∧ x2=2", false, te);
	}
	
	public void testIn3() {
		ITypeEnvironment te = mTypeEnvironment(mList("S"), mList(POW(CPROD(CPROD(BOOL, INT),INT))));
		doTest( "a↦f(10)∈S",
				"∃x·a↦x∈S ∧ x=f(10)", true, te);
	}

	public void testIn4() {
		Type A = ff.makeGivenType("A");
		Type B = ff.makeGivenType("B");
		ITypeEnvironment te = mTypeEnvironment(mList("f"), mList(REL(A, B)));
		doTest( "f(a)  ∈ S",
				"∃x·x∈S ∧ x=f(a)", true, te);
	}
	
	public void testIn5() {
		Type A = ff.makeGivenType("A");
		Type B = ff.makeGivenType("B");
		Type C = ff.makeGivenType("C");
		ITypeEnvironment te = mTypeEnvironment(mList("f"), mList(REL(A, CPROD(B, C))));
		doTest( "f(a)  ∈ S",
				"∃x1,x2·x1↦x2∈S ∧ x1↦x2=f(a)", true, te);
	}

	public void testPerf() {
		Predicate pred = parse("E∈S∪X↠T∪Y", FastFactory.mTypeEnvironment(
				new String[]{"S", "T"}, new Type[]{INT_SET, POW(BOOL)}));
		
		System.out.println("start");
		for(int i = 0; i < 1000; i++) {
			Translator.reduceToPredicateCalulus(pred, ff);
		}
		System.out.println("end");
	}
	
}
