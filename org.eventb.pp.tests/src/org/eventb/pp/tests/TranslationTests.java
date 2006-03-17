package org.eventb.pp.tests;

import static org.eventb.pp.tests.FastFactory.mAssociativePredicate;
import static org.eventb.pp.tests.FastFactory.mLiteralPredicate;
import junit.framework.TestCase;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.internal.pp.translator.Translator;

/**
 * Ensures that the translator from set-theory to predicate calculus works
 * correctly.
 * 
 * 
 * @author Matthias Konrad
 */

public class TranslationTests extends TestCase {

	private static FormulaFactory ff = FormulaFactory.getDefault();

	// Types used in these tests
	private static IntegerType INT = ff.makeIntegerType();
	private static BooleanType BOOL = ff.makeBooleanType();

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
	
	private void doTest(Predicate input, Predicate expected) {
		assertTrue("Input is not typed", input.isTypeChecked());
		assertTrue("Expected result is not typed", expected.isTypeChecked());
		Predicate actual = Translator.translate(input, ff);
		assertEquals("Unexpected result of translation", expected, actual);
	}

}
