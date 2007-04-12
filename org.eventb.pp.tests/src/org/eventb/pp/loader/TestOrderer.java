package org.eventb.pp.loader;

import static org.eventb.pp.Util.cCons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.internal.pp.core.elements.ILiteral;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.loader.predicate.INormalizedFormula;
import org.eventb.internal.pp.loader.predicate.PredicateBuilder;
import org.eventb.pp.Util;


public class TestOrderer extends TestCase {
	private static FormulaFactory ff = FormulaFactory.getDefault();
	
	class TestPair {
		List<String> predicate;
		
		TestPair (String... predicate) {
			this.predicate = Arrays.asList(predicate);
		}
	}
	
	private static Term a = cCons("a");
	private static Term b = cCons("b");
	private static Term c = cCons("c");
	private static Term d = cCons("d");
	private static Term e = cCons("e");
	private static Term n = cCons("n");
	private static Term D = cCons("D");
	private static Term M = cCons("M");
	private static Term P = cCons("P");
	private static Term Q = cCons("Q");
	private static Term R = cCons("R");
	private static Term S = cCons("S");
	private static Term SS = cCons("SS");
	private static Term T = cCons("T");
	private static Term U = cCons("U");
	private static Term one = cCons("1");
	private static Term TRUE = cCons("TRUE");

	private static ITypeEnvironment env = ff.makeTypeEnvironment();
	static {
		env.addName("x0", ff.makeGivenType("A"));
		env.addName("x1", ff.makeGivenType("B"));
		env.addName("a", ff.makeGivenType("S"));
//		env.addName("b", ff.makeGivenType("S"));
		
//		env.addName("d", ff.makeIntegerType());
		env.addName("e", ff.makeBooleanType());
		env.addName("f", ff.makePowerSetType(ff.makeProductType(ff.makeGivenType("A"), ff.makeGivenType("B"))));
		env.addName("n", ff.makeIntegerType());
		env.addName("N", ff.makePowerSetType(ff.makeIntegerType()));
		env.addName("S", ff.makePowerSetType(ff.makeGivenType("S")));
		env.addName("P", ff.makePowerSetType(ff.makeGivenType("B")));
		env.addName("Q", ff.makePowerSetType(ff.makeGivenType("A")));
		env.addName("R", ff.makePowerSetType(ff.makeGivenType("A")));
		env.addName("U", ff.makePowerSetType(ff.makeGivenType("U")));
		env.addName("M", ff.makePowerSetType(ff.makeProductType(ff.makeProductType(ff.makeGivenType("B"),ff.makeGivenType("A")),ff.makeGivenType("A"))));
		env.addName("SS", ff.makePowerSetType(ff.makeGivenType("S")));
		env.addName("T", ff.makePowerSetType(ff.makeProductType(ff.makeIntegerType(), ff.makeIntegerType())));
		env.addName("TT", ff.makePowerSetType(ff.makeProductType(ff.makeProductType(ff.makeIntegerType(),ff.makeIntegerType()), ff.makeIntegerType())));
	}
	
	private static List<ILiteral> noLit = new ArrayList<ILiteral>();
	
	TestPair[] testOrdering = new TestPair[] {
			new TestPair(
					"a ∈ S ∨ d ∈ U","d ∈ U ∨ a ∈ S"
			),
			new TestPair(
					"a ∈ S ∨ ¬(a ∈ S)","¬(a ∈ S) ∨ a ∈ S"
			),
			new TestPair(
					"a = b ∨ a ∈ S", "a ∈ S ∨ a = b"
			),
//			new TestPair(
//					"a = b ∨ n = 1", "n = 1 ∨ a = b"
//			),
			new TestPair(
					"a = b ∨ ¬(a = b)", "¬(a = b) ∨ a = b"
			),
			new TestPair(
					"a = b ∨ ¬(a ∈ S)", "¬(a ∈ S) ∨ a = b"
			),
			new TestPair(
					"¬(a = b) ∨ a ∈ S", "a ∈ S ∨ ¬(a = b)"
			),
//			new TestPair(
//					"n < 1 ∨ a = b", "a = b ∨ n < 1"
//			),
			new TestPair(
					"¬(a ∈ S ∨ d ∈ U) ∨ a = b", "a = b ∨ ¬(a ∈ S ∨ d ∈ U)"
			),
			new TestPair(
					"¬(a ∈ S ∨ d ∈ U) ∨ b ∈ S", "b ∈ S ∨ ¬(a ∈ S ∨ d ∈ U)"
			),
			new TestPair(
					"(∀x·x ∈ S) ∨ (∀x·x ∈ U)", "(∀x·x ∈ U) ∨ (∀x·x ∈ S)"
			),
			new TestPair(
					"a ∈ S ∨ (∀x·x ∈ U)", "(∀x·x ∈ U) ∨ a ∈ S"
			),
			new TestPair(
					"a = b ∨ (∀x·x ∈ U)", "(∀x·x ∈ U) ∨ a = b"
			),
	};
	
	TestPair[] testTermOrdering = new TestPair[] {
			
	};
	
	public void doTest(List<String> strPredicate) {
		INormalizedFormula formula = null;
		PredicateBuilder builder = new PredicateBuilder();
		int index = 0;
		for (String predicate : strPredicate) {
			builder.build(Util.parsePredicate(predicate, env),false);
			INormalizedFormula newFormula = builder.getContext().getResults().get(index++);
			if (formula == null) {
				formula = newFormula;
				continue;
			}
			assertEquals(formula, newFormula);
		}
		
	}
	
	
	public void testOrdering() {
		for (TestPair test : testOrdering) {
			doTest(test.predicate);
		}
	}

}
