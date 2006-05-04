package org.eventb.pp.tests;

import static org.eventb.pp.tests.FastFactory.mList;
import static org.eventb.pp.tests.FastFactory.mTypeEnvironment;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.pp.Translator;

public class ReorganisationTests extends AbstractTranslationTests {
	protected static final Type S = ff.makeGivenType("S");
	protected static final Type T = ff.makeGivenType("T");
	protected static final Type U = ff.makeGivenType("U");
	protected static final Type V = ff.makeGivenType("V");
	protected static final Type X = ff.makeGivenType("X");
	protected static final Type Y = ff.makeGivenType("Y");
	protected static final ITypeEnvironment defaultTe;
	static {
		defaultTe = ff.makeTypeEnvironment();
		defaultTe.addGivenSet("S");
		defaultTe.addGivenSet("T");
		defaultTe.addGivenSet("U");
		defaultTe.addGivenSet("V");
	}

	private static void doTest(String input, String expected, boolean transformExpected, ITypeEnvironment te) {
		Predicate pinput = parse(input, te);
		Predicate pexpected = parse(expected, te);
		if(transformExpected) {
			pexpected = Translator.reduceToPredicateCalulus(pexpected, ff);
		}
		doTest(pinput, pexpected);
	}
	
	private static void doTest(Predicate input, Predicate expected) {
		assertTrue("Input is not typed: " + input, input.isTypeChecked());
		assertTrue("Expected result is not typed: " + expected, 
				expected.isTypeChecked());

		Predicate actual = Translator.reduceToPredicateCalulus(input, ff);

		assertTrue("Actual result is not typed: " + actual,
				actual.isTypeChecked());
		assertTrue("Result not in goal: " + actual, Translator.isInGoal(actual));
		assertEquals("Unexpected result of translation", expected, actual);
	}
	
	public void testFuncExtraction() {
		
		doTest( "1 < f(10)",
				"∀x·(∃y·y=10∧y↦x∈f) ⇒ 1<x",
				false,
				mTypeEnvironment());
	}
	
	public void testFuncExtraction2() {
		
		doTest( "1 < f(10+f(20))",
				"1 < f(10+f(20))",
				true,
				mTypeEnvironment());
	}
	
	public void testFuncExtraction3() {
		
		doTest( "f(10+f(f(10+f(f(10+f(f(10+f(f(10+f(20)))))))))) < f(10+f(f(10+f(f(10+f(f(10+f(f(10+f(20))))))))))",
				"f(10+f(f(10+f(f(10+f(f(10+f(f(10+f(20)))))))))) < f(10+f(f(10+f(f(10+f(f(10+f(f(10+f(20))))))))))",
				true,
				mTypeEnvironment());
	}
	
	public void testCardinality2() {
		
		doTest( "card({1}) > 1",
				"∀x·x=card({1})⇒x>1", 
				true,
				mTypeEnvironment());
	}
	
	public void testCardinality3() {
		
		doTest( "card({card({1})}) = card({2})",
				"card({card({1})}) = card({2})", 
				true,
				mTypeEnvironment());
	}
	
	public void testCardinality4() {
		
		doTest( "∀s·card(S)>card({t·t>s∧t<card({t,t})∣t})",
				"∀s·card(S)>card({t·t>s∧t<card({t,t})∣t})", 
				true, 
				mTypeEnvironment(mList("S"), mList(INT_SET)));
	}
	
}
