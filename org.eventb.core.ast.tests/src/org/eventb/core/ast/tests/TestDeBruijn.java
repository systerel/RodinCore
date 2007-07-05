/*
 * Created on 07-jul-2005
 *
 */
package org.eventb.core.ast.tests;

import junit.framework.TestCase;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;

/**
 * Test the equivalence of various ways of expressing the same quantified
 * expression.
 * 
 * @author franz
 */
public class TestDeBruijn extends TestCase {
	private FormulaFactory formulaFactory;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		formulaFactory = FormulaFactory.getDefault();
	}
	
	private class TestItem {
		String[] inputs;
		
		TestItem(String... inputs) {
			this.inputs = inputs;
		}
		
		int size() {
			return inputs.length;
		}
		
		String get(int index) {
			return inputs[index];
		}
	}

	TestItem[] testItems = new TestItem[]{
			new TestItem(
					"finite(\u03bb x \u21a6 (y \u21a6 s) \u00b7 \u22a5 \u2223 z)",
					"finite({ x, y, s \u00b7 \u22a5 \u2223 (x \u21a6 (y \u21a6 s)) \u21a6 z})"
			),
			new TestItem(
					"finite(\u03bb x\u21a6(y\u21a6s)\u00b7\u22a5\u2223 1)",
					"finite({x, y, s\u00b7\u22a5\u2223(x\u21a6(y\u21a6s))\u21a6 1})",
					"finite({(x\u21a6(y\u21a6s))\u21a6 1\u2223\u22a5})"
			),
			new TestItem(
					"finite(\u22c3x,y\u00b7\u22a5\u2223x+y)",
					"finite(\u22c3x+y\u2223\u22a5)"
			),
			new TestItem(
					"finite(\u22c2x,y\u00b7\u22a5\u2223x+y)",
					"finite(\u22c2x+y\u2223\u22a5)"
			),
			new TestItem(
					"finite({x,y\u00b7\u22a5\u2223x+y})",
					"finite({x+y\u2223\u22a5})"
			),
	};
	
	/**
	 * Main test routine. 
	 */
	public void testDeBruijn() {
		for (TestItem item : testItems) {
			for (int i = 0; i < item.size() - 1; i++) {
				Formula<?> form1 = formulaFactory.parsePredicate(item.get(i)).getParsedPredicate();
				Formula<?> form2 = formulaFactory.parsePredicate(item.get(i + 1))
						.getParsedPredicate();
				assertEquals("\nFirst input: " + item.get(i) + "\nFirst tree: "
						+ form1.getSyntaxTree() + "\nSecond input: "
						+ item.get(i + 1) + "\nSecond tree: "
						+ form2.getSyntaxTree(), form1, form2);
			}
		}
	}

}
