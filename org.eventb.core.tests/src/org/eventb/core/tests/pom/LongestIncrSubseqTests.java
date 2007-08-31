package org.eventb.core.tests.pom;

import junit.framework.TestCase;

import org.eventb.internal.core.pom.LongestIncrSubseq;

/**
 * Ensures that the {@link LongestIncrSubseq} class works properly.
 * 
 * @author Laurent Voisin
 */
public class LongestIncrSubseqTests extends TestCase {

	private static int[] L(int... args) {
		return args;
	}

	private static void assertEquals(int[] exp, int[] act) {
		final int length = exp.length;
		if (length != act.length) {
			fail(exp, act);
		}
		for (int i = 0; i < length; i++) {
			if (exp[i] != act[i]) {
				fail(exp, act);
			}
		}
	}

	private static void fail(int[] exp, int[] act) {
		fail("expected: " + toString(exp) + " but was: " + toString(act));
	}

	private static String toString(int[] seq) {
		final StringBuilder b = new StringBuilder();
		char sep = '[';
		for (final int value: seq) {
			b.append(sep);
			b.append(value);
			sep = ',';
		}
		b.append(']');
		return b.toString();
	}

	private static void doTest(int[] seq, int[] exp) {
		LongestIncrSubseq obj = new LongestIncrSubseq(seq);
		assertEquals(exp, obj.result());
	}

	public void testEmpty() {
		doTest(L(), L());
	}

	public void test1() {
		doTest(L(5), L(5));
	}

	public void test2() {
		doTest(L(5,6), L(5,6));
		doTest(L(6,5), L(5));
	}

	public void test3() {
		doTest(L(4,5,6), L(4,5,6));
		doTest(L(4,6,5), L(4,5));
		doTest(L(5,4,6), L(4,6));
		doTest(L(6,4,5), L(4,5));
		doTest(L(5,6,4), L(5,6));
		doTest(L(6,5,4), L(4));
	}

	public void test4() {
		doTest(L(3,4,5,6), L(3,4,5,6));
		doTest(L(3,4,6,5), L(3,4,5));
		doTest(L(3,5,4,6), L(3,4,6));
		doTest(L(3,5,6,4), L(3,5,6));
		doTest(L(3,6,4,5), L(3,4,5));
		doTest(L(3,6,5,4), L(3,4));

		doTest(L(4,3,5,6), L(3,5,6));
		doTest(L(4,3,6,5), L(3,5));
		doTest(L(5,3,4,6), L(3,4,6));
		doTest(L(5,3,6,4), L(3,4));
		doTest(L(6,3,4,5), L(3,4,5));
		doTest(L(6,3,5,4), L(3,4));

		doTest(L(4,5,3,6), L(4,5,6));
		doTest(L(4,6,3,5), L(3,5));
		doTest(L(5,4,3,6), L(3,6));
		doTest(L(5,6,3,4), L(3,4));
		doTest(L(6,4,3,5), L(3,5));
		doTest(L(6,5,3,4), L(3,4));

		doTest(L(4,5,6,3), L(4,5,6));
		doTest(L(4,6,5,3), L(4,5));
		doTest(L(5,4,6,3), L(4,6));
		doTest(L(5,6,4,3), L(5,6));
		doTest(L(6,4,5,3), L(4,5));
		doTest(L(6,5,4,3), L(3));
	}

}
