package org.eventb.core.ast.tests;

import java.util.HashSet;

import junit.framework.TestCase;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.QuantifiedUtil;


/**
 * Unit test for ident renaming. 
 * 
 * @author Laurent Voisin
 */
public class TestIdentRenaming extends TestCase {
	
	static FormulaFactory ff = FormulaFactory.getDefault(); 
	
	private static class TestItem {
		String[] originals;
		String[] used;
		String[] expected;
		
		TestItem(String[] originals, String[] used, String[] expected) {
			this.originals = originals;
			this.used = used;
			this.expected = expected;
		}
		
		private BoundIdentDecl[] makeBoundIdentDecls() {
			final int length = originals.length;
			BoundIdentDecl[] result = new BoundIdentDecl[length];
			for (int i = 0; i < originals.length; i++) {
				result[i] = ff.makeBoundIdentDecl(originals[i], null);
			}
			return result;
		}
		
		private HashSet<String> makeUsedSet() {
			final int length = used.length;
			HashSet<String> result = new HashSet<String>(length * 4 / 3);
			for (String name : used) {
				result.add(name);
			}
			return result;
		}
		
		void doTest() {
			BoundIdentDecl[] decls = makeBoundIdentDecls();
			HashSet<String> usedSet = makeUsedSet();
			String[] actual = QuantifiedUtil.resolveIdents(decls, usedSet);
			assertEquals(expected.length, actual.length);
			for (int i = 0; i < expected.length; i++) {
				assertEquals(expected[i], actual[i]);
			}
		}
	}

	public static String[] L(String... names) {
		return names;
	}
	
	TestItem[] items = new TestItem[] {
			new TestItem(
					L("x'"),
					L("x"),
					L("x'")
			), new TestItem(
					L("x'"),
					L("x", "x'"),
					L("x0'")
			), new TestItem(
					L("x1'"),
					L("x1"),
					L("x1'")
			), new TestItem(
					L("x1'"),
					L("x1", "x1'"),
					L("x2'")
			), new TestItem(
					L("x"),
					L(),
					L("x")
			), new TestItem(
					L("x"),
					L("x1"),
					L("x")
			), new TestItem(
					L("x"),
					L("x", "x1"),
					L("x0")
			), new TestItem(
					L("x"),
					L("x", "x0", "x1"),
					L("x2")
			), new TestItem(
					L("x1"),
					L(),
					L("x1")
			), new TestItem(
					L("x1"),
					L("x1"),
					L("x2")
			), new TestItem(
					L("x1x"),
					L(),
					L("x1x")
			), new TestItem(
					L("x1x"),
					L("x1x"),
					L("x1x0")
			),
	};
	
	/**
	 * Ensures that identifier renaming is done properly.
	 */
	public void testRenaming() {
		for (TestItem item : items) {
			item.doTest();
		}
	}
	
}
