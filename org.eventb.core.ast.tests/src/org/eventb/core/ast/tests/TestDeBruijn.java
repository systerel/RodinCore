/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language v2
 *******************************************************************************/ 
package org.eventb.core.ast.tests;

import static org.junit.Assert.assertEquals;

import org.eventb.core.ast.Predicate;
import org.junit.Test;


/**
 * Test the equivalence of various ways of expressing the same quantified
 * expression.
 * 
 * @author franz
 */
public class TestDeBruijn extends AbstractTests {
	
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
	@Test 
	public void testDeBruijn() {
		for (TestItem item : testItems) {
			for (int i = 0; i < item.size() - 1; i++) {
				Predicate form1 = parsePredicate(item.get(i));
				Predicate form2 = parsePredicate(item.get(i + 1));
				assertEquals("\nFirst input: " + item.get(i) + "\nFirst tree: "
						+ form1.getSyntaxTree() + "\nSecond input: "
						+ item.get(i + 1) + "\nSecond tree: "
						+ form2.getSyntaxTree(), form1, form2);
			}
		}
	}

}
