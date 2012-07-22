/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored tests
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.QuantifiedUtil.resolveIdents;
import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.FormulaFactory;

/**
 * Unit tests for identifier renaming.
 * 
 * @author Laurent Voisin
 */
public class TestIdentRenaming extends AbstractTests {

	/**
	 * Ensures that identifier renaming is done properly.
	 */
	public void testRenaming() {
		doTest(L("x'"),//
				L("x"),//
				L("x'"));
		doTest(L("x'"),//
				L("x", "x'"),//
				L("x0'"));
		doTest(L("x1'"),//
				L("x1"),//
				L("x1'"));
		doTest(L("x1'"),//
				L("x1", "x1'"),//
				L("x2'"));
		doTest(L("x"),//
				L(),//
				L("x"));
		doTest(L("x"),//
				L("x1"),//
				L("x"));
		doTest(L("x"),//
				L("x", "x1"),//
				L("x0"));
		doTest(L("x"),//
				L("x", "x0", "x1"),//
				L("x2"));
		doTest(L("x1"),//
				L(),//
				L("x1"));
		doTest(L("x1"),//
				L("x1"),//
				L("x2"));
		doTest(L("x1x"),//
				L(),//
				L("x1x"));
		doTest(L("x1x"),//
				L("x1x"),//
				L("x1x0"));
		doTest(L("x", "y"),//
				L("x", "y"),//
				L("x0", "y0"));
		doTest(L("x", "x"),//
				L("x", "x1"),//
				L("x0", "x2"));
	}

	/**
	 * Ensures that identifier renaming is done properly with mathematical
	 * extensions.
	 */
	public void testRenamingLanguage() {
		doTest(LIST_FAC,//
				L("x", "List", "nil"),//
				L("x", "nil0"),//
				L("x0", "List0", "nil1"));
	}

	private static String[] L(String... names) {
		return names;
	}

	private static void doTest(String[] originals, String[] used,
			String[] expected) {
		doTest(ff, originals, used, expected);
	}

	private static void doTest(FormulaFactory fac, String[] originals,
			String[] used, String[] expected) {
		final BoundIdentDecl[] decls = makeBoundIdentDecls(originals);
		final Set<String> usedSet = makeUsedSet(used);
		final String[] actual = resolveIdents(decls, usedSet, fac);
		assertArrayEquals(expected, actual);
	}

	private static BoundIdentDecl[] makeBoundIdentDecls(String[] names) {
		final int length = names.length;
		final BoundIdentDecl[] result = new BoundIdentDecl[length];
		for (int i = 0; i < length; i++) {
			result[i] = ff.makeBoundIdentDecl(names[i], null);
		}
		return result;
	}

	private static Set<String> makeUsedSet(String[] used) {
		return new HashSet<String>(Arrays.asList(used));
	}

}
