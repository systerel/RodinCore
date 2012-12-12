/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added abstract test class
 *     Systerel - mathematical language v2
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static junit.framework.Assert.assertEquals;
import static org.eventb.core.ast.tests.FastFactory.mList;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.junit.Before;
import org.junit.Test;

/**
 * Some unit tests for formulas difficult to parse due to LL(1) conflicts.
 * Tries to verify that it works properly in weird cases.
 * 
 * @author franz
 */
public class TestConflictResolver extends AbstractTests {
	
	private TestItem[] testItems;
	
	private class TestItem {
		final String input;
		final Formula<?> expectedTree;
		final LanguageVersion[] versions;
		
		TestItem(String input, Formula<?> expectedTree,
				LanguageVersion... versions) {
			this.expectedTree = expectedTree;
			this.input = input;
			if (versions.length == 0) {
				this.versions = LanguageVersion.values();
			} else {
				this.versions = versions;
			}
		}
	}
	
	@Before
	public void setUp() throws Exception {

		final FreeIdentifier id_x = ff.makeFreeIdentifier("x", null);
		final FreeIdentifier id_y = ff.makeFreeIdentifier("y", null);
		final FreeIdentifier id_z = ff.makeFreeIdentifier("z", null);

		final BoundIdentDecl b_x = ff.makeBoundIdentDecl("x", null);
		final BoundIdentDecl b_x2 = ff.makeBoundIdentDecl("x2", null);
		final BoundIdentDecl b_y = ff.makeBoundIdentDecl("y", null);
		
		testItems = new TestItem[] {
			new TestItem(
				"finite({x,x,x})",
				ff.makeSimplePredicate(Formula.KFINITE,ff.makeSetExtension(mList(id_x, id_x, id_x),null),null)
			),
			new TestItem(
				"{x,y\u00b7\u22a5\u2223z}=a",
				ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET, mList(b_x, b_y), ff.makeLiteralPredicate(Formula.BFALSE,null),id_z,null, QuantifiedExpression.Form.Explicit),ff.makeFreeIdentifier("a",null),null)
			),
			new TestItem(
				"{x\u2223\u22a5}=a",
				ff.makeRelationalPredicate(Formula.EQUAL,ff.makeQuantifiedExpression(Formula.CSET, mList(b_x), ff.makeLiteralPredicate(Formula.BFALSE,null),ff.makeBoundIdentifier(0,null),null,QuantifiedExpression.Form.Implicit),ff.makeFreeIdentifier("a",null),null)
			),
			new TestItem(
				"{{x\u2223\u22a5}\u2216x2\u2223\u22a5}=a",
				ff.makeRelationalPredicate(
					Formula.EQUAL,
					ff.makeQuantifiedExpression(
						Formula.CSET,
						mList(b_x2),
						ff.makeLiteralPredicate(Formula.BFALSE,null),
						ff.makeBinaryExpression(
							Formula.SETMINUS,
							ff.makeQuantifiedExpression(
								Formula.CSET,
								mList(b_x),
								ff.makeLiteralPredicate(Formula.BFALSE,null),
								ff.makeBoundIdentifier(0,null),null,QuantifiedExpression.Form.Implicit),
							ff.makeBoundIdentifier(0,null), null),null,QuantifiedExpression.Form.Implicit),
					ff.makeFreeIdentifier("a",null),null)
			),
			new TestItem(
				"finite(\u22c3 x, y \u00b7\u22a5\u2223z)",
				ff.makeSimplePredicate(Formula.KFINITE,ff.makeQuantifiedExpression(Formula.QUNION, mList(b_x, b_y), ff.makeLiteralPredicate(Formula.BFALSE,null),id_z,null,QuantifiedExpression.Form.Explicit),null)
			),
			new TestItem(
				"finite(\u22c2 x, y \u00b7\u22a5\u2223z)",
				ff.makeSimplePredicate(Formula.KFINITE,ff.makeQuantifiedExpression(Formula.QINTER, mList(b_x, b_y), ff.makeLiteralPredicate(Formula.BFALSE,null),id_z,null,QuantifiedExpression.Form.Explicit),null)
			),
			new TestItem(
				"finite(\u22c2x\u2223\u22a5)",
				ff.makeSimplePredicate(Formula.KFINITE,ff.makeQuantifiedExpression(Formula.QINTER, mList(b_x),ff.makeLiteralPredicate(Formula.BFALSE,null),ff.makeBoundIdentifier(0,null),null,QuantifiedExpression.Form.Implicit),null)
			),
			new TestItem(
				"finite(\u22c3x\u2223\u22a5)",
				ff.makeSimplePredicate(Formula.KFINITE,ff.makeQuantifiedExpression(Formula.QUNION, mList(b_x),ff.makeLiteralPredicate(Formula.BFALSE,null),ff.makeBoundIdentifier(0,null),null,QuantifiedExpression.Form.Implicit),null)
			),
			
			// Tests with superfluous parentheses.
			new TestItem(
				"(x=y)",
				ff.makeRelationalPredicate(Formula.EQUAL, id_x, id_y, null)
			),
			new TestItem(
				"((x)=(y))",
				ff.makeRelationalPredicate(Formula.EQUAL, id_x, id_y, null)
			),
			new TestItem(
				"((x=y))",
				ff.makeRelationalPredicate(Formula.EQUAL, id_x, id_y, null)
			),
			new TestItem(
				"((((x))=((y))))",
				ff.makeRelationalPredicate(Formula.EQUAL, id_x, 	id_y, null)
				),
			new TestItem(
				"(x=y) \u2227 (y=y)",
				ff.makeAssociativePredicate(
					Formula.LAND, mList(
						ff.makeRelationalPredicate(Formula.EQUAL, id_x, id_y, null),
						ff.makeRelationalPredicate(Formula.EQUAL, id_y, 	id_y, null)
					), null
				)
			),

			// tests with the empty set written in extension
			new TestItem(
				"x = {}",
				ff.makeRelationalPredicate(
					Formula.EQUAL,
					id_x,
					ff.makeSetExtension(new Expression[] {}, null), null)
			),
			new TestItem(
					"x = { }",
					ff.makeRelationalPredicate(
						Formula.EQUAL,
						id_x,
						ff.makeSetExtension(new Expression[] {}, null), null)
				),
		};
	}
	
	
	/**
	 * Main test routine.
	 */
	@Test 
	public void testConflict() {
		for (TestItem item : testItems) {
			for (LanguageVersion version: item.versions) {
				final Predicate actual = parsePredicate(item.input, version);
				assertEquals("\nTest failed on: " + item.input
						+ "\nTree expected: " + item.expectedTree.getSyntaxTree()
						+ "\nTree received: "
						+ actual.getSyntaxTree(),
						item.expectedTree,
						actual);
			}
		}
	}
	
}
