/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.pp.loader;

import static org.eventb.internal.pp.core.elements.terms.Util.mSort;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Type;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.Util;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.predicate.AbstractContext;
import org.eventb.internal.pp.loader.predicate.TermBuilder;
import org.junit.Test;

/**
 * This class tests that the sorts created by the term builder are indeed the
 * expected sorts. Are tested here :
 * <ul>
 * <li>arithmetic sort for all arithmetic type</li>
 * <li>carrier set sorts, same sort for same carrier set, different sort for
 * different carrier sets</li>
 * <li>boolean sorts...</li>
 * </ul>
 * 
 * @author François Terrier
 * 
 */
public class TestSorts extends AbstractPPTest {

	private static FormulaFactory ff = FormulaFactory.getDefault();

	private static Type S = ff.makeGivenType("S");
	private static Type T = ff.makeGivenType("T");
	private static Type INT = ff.makeIntegerType();

	private static FreeIdentifier a = ff.makeFreeIdentifier("a", null, S);
	private static FreeIdentifier b = ff.makeFreeIdentifier("b", null, T);
	private static FreeIdentifier k = ff.makeFreeIdentifier("k", null, INT);

	private static BoundIdentDecl dxS = ff.makeBoundIdentDecl("x", null, S);
	private static BoundIdentDecl dyT = ff.makeBoundIdentDecl("y", null, T);
	
	private static BoundIdentifier b0S = ff.makeBoundIdentifier(0, null, S);
	private static BoundIdentifier b0T = ff.makeBoundIdentifier(0, null, T);
	private static BoundIdentifier b1S = ff.makeBoundIdentifier(1, null, S);

	private void doTest(Expression expression, Sort expected, BoundIdentDecl... decls) {
		final TermBuilder builder = new TermBuilder(new AbstractContext());
		builder.pushDecls(decls);
		final TermSignature term = builder.buildTerm(expression);
		assertEquals(expression.toString(), expected, term.getSort());
	}

	private void doTest(String image, Sort expected, BoundIdentDecl... decls) {
		final Expression expr = Util.parseExpression(image);
		expr.typeCheck(ff.makeTypeEnvironment());
		assertTrue(expr.isTypeChecked());
		doTest(expr, expected, decls);
	}

    @Test
	public void testSorts() {
		doTest(a, mSort(S));
		doTest(b, mSort(T));
		doTest(k, NAT);
		doTest("k + 1", NAT);
		doTest("k ∗ 1", NAT);
		doTest("k ÷ 1", NAT);
		doTest("k mod 1", NAT);
		doTest("k ^ 1", NAT);
		doTest("k − 1", NAT);
		doTest("− k", NAT);
		doTest("0", NAT);
		
		doTest(b0S, mSort(S), dxS);
		doTest(b0T, mSort(T), dxS, dyT);
		doTest(b1S, mSort(S), dxS, dyT);
	}
}
