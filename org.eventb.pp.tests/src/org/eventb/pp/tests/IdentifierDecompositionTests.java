package org.eventb.pp.tests;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.pp.Translator;

/**
 * Ensures that identifier decomposition behaves properly.
 * 
 * @author Laurent Voisin
 * @see org.eventb.pp.Translator#decomposeIdentifiers(Predicate, FormulaFactory)
 */
public class IdentifierDecompositionTests extends AbstractTranslationTests {
	
	protected static final ITypeEnvironment te;
	static {
		te = ff.makeTypeEnvironment();
		te.addGivenSet("S");
		te.addGivenSet("T");
		te.addGivenSet("U");
		te.addGivenSet("V");
	}

	private void dotest(String inputString, String expectedString) {
		final Predicate input = parse(inputString, te);
		final Predicate expected = parse(expectedString, te);
		final Predicate actual = Translator.decomposeIdentifiers(input, ff);
		assertEquals("Wrong identifier decomposition", expected, actual);
	}

	/**
	 * Ensures that a free identifier which hides a maplet is decomposed, when
	 * occurring outside of any quantified construct.
	 */
	public final void testDecomposeFreeOutside1() {
		dotest("x ∈ S×T", "∀x2,x1 · x = x1↦x2 ⇒ x1↦x2 ∈ S×T");
	}

	/**
	 * Ensures that a free identifier which hides several maplet is fully
	 * decomposed, when occurring outside of any quantified construct.
	 */
	public final void testDecomposeFreeOutside2() {
		dotest("x ∈ S×(T×U)",
				"∀x3,x2,x1 · x = x1↦(x2↦x3) ⇒ x1↦(x2↦x3) ∈ S×(T×U)");
	}

	/**
	 * Ensures that two free identifiers which hide a maplet are decomposed,
	 * when occurring outside of any quantified construct.
	 */
	public final void testDecomposeFreeOutside3() {
		dotest("x ∈ S×T ∧ y ∈ U×V",
				"∀y2,y1,x2,x1 · x=x1↦x2 ∧ y=y1↦y2 ⇒ "
				+ "x1↦x2 ∈ S×T ∧ y1↦y2 ∈ U×V");
	}

	/**
	 * Ensures that a free identifier which hides a maplet is decomposed, when
	 * occurring inside a quantified predicate.
	 */
	public final void testDecomposeFreeInQPred() {
		dotest("∀z · z ∈ BOOL ⇒ x ∈ S×T",
				"∀x2,x1 · x = x1↦x2 ⇒ (∀z · z ∈ BOOL ⇒ x1↦x2 ∈ S×T)");
	}

	/**
	 * Ensures that a free identifier which hides a maplet is decomposed, when
	 * occurring inside a quantified expression.
	 */
	public final void testDecomposeFreeInQExpr() {
		dotest("finite({z ∣ z ∈ BOOL ∧ x ∈ S×T})",
				"∀x2,x1 · x = x1↦x2 ⇒ finite({z ∣ z ∈ BOOL ∧ x1↦x2 ∈ S×T})");
	}

	/**
	 * Ensures that a bound identifier which hides a maplet is decomposed, when
	 * occurring outside of any other quantified construct.
	 */
	public final void testDecomposeBoundOutside1() {
		dotest("∃x · x ∈ S×T", "∃x2,x1 · x1↦x2 ∈ S×T");
	}

	/**
	 * Ensures that two bound identifiers which hide a maplet are decomposed, when
	 * occurring outside of any other quantified construct.
	 */
	public final void testDecomposeBoundOutside2() {
		dotest("∃x,y · x ∈ S×T ∧ y ∈ U×V", 
				"∃x2,x1,y2,y1 · x1↦x2 ∈ S×T ∧ y1↦y2 ∈ U×V");
	}

	/**
	 * Ensures that a bound identifier which hides a maplet is decomposed, when
	 * occurring outside of any other quantified construct and as first
	 * declaration in its own quantifier.
	 */
	public final void testDecomposeBoundOutsideFirst() {
		dotest("∃x,y,z · x ∈ S×T ∧ y ∈ BOOL ∧ z ∈ BOOL",
				"∃x2,x1,y,z · x1↦x2 ∈ S×T ∧ y ∈ BOOL ∧ z ∈ BOOL");
	}

	/**
	 * Ensures that a bound identifier which hides a maplet is decomposed, when
	 * occurring outside of any other quantified construct and as last
	 * declaration in its own quantifier.
	 */
	public final void testDecomposeBoundOutsideLast() {
		dotest("∃y,z,x · x ∈ S×T ∧ y ∈ BOOL ∧ z ∈ BOOL",
				"∃y,z,x2,x1 · x1↦x2 ∈ S×T ∧ y ∈ BOOL ∧ z ∈ BOOL");
	}

	/**
	 * Ensures that a bound identifier which hides a maplet is decomposed, when
	 * occurring inside another quantified construct.
	 */
	public final void testDecomposeBoundInside1() {
		dotest("∃a·a ∈ ℤ ∧ (∃x·x ∈ S×T ∧ 0 ≤ a) ∧ 1 ≤ a",
				"∃a·a ∈ ℤ ∧ (∃x2,x1·x1↦x2 ∈ S×T ∧ 0 ≤ a) ∧ 1 ≤ a");
	}

	/**
	 * Ensures that a bound identifier which hides a maplet is decomposed, when
	 * occurring inside two other nested quantified constructs.
	 */
	public final void testDecomposeBoundInside2() {
		dotest("∃a·a ∈ ℤ ∧ (∃b·b ∈ ℤ ∧ (∃x·x ∈ S×T ∧ a ≤ b) ∧ b ≤ a) ∧ 1 ≤ a",
				"∃a·a ∈ ℤ ∧ (∃b·b ∈ ℤ ∧ (∃x2,x1·x1↦x2 ∈ S×T ∧ a ≤ b) ∧ b ≤ a) ∧ 1 ≤ a");
	}

	/**
	 * Ensures that a free and a bound identifier which hide a maplet are both
	 * decomposed.
	 */
	public final void testDecomposeFreeBound() {
		dotest("∃x·x ∈ S×T ∧ y ∈ U×V",
				"∀y2,y1·y = y1↦y2 ⇒ (∃x2,x1·x1↦x2 ∈ S×T ∧ y1↦y2 ∈ U×V)");
	}

}
