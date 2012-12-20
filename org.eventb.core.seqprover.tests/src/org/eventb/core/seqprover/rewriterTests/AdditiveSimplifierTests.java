/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.rewriterTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AdditiveSimplifier;
import org.junit.Test;

/**
 * Unit tests for the additive formula simplifier.
 * 
 * @author Laurent Voisin
 */
public class AdditiveSimplifierTests {

	final FormulaFactory ff = FormulaFactory.getDefault();

	private IPosition mPos(String image) {
		return FormulaFactory.makePosition(image);
	}

	private Expression parseExpr(String input, ITypeEnvironmentBuilder typenv) {
		final Expression expr = ff.parseExpression(input, LanguageVersion.V2,
				this).getParsedExpression();
		final ITypeCheckResult res = expr.typeCheck(typenv);
		typenv.addAll(res.getInferredEnvironment());
		assertTrue(expr.isTypeChecked());
		return expr;
	}

	private Predicate parsePred(String input, ITypeEnvironmentBuilder typenv) {
		final Predicate pred = ff.parsePredicate(input, LanguageVersion.V2,
				this).getParsedPredicate();
		final ITypeCheckResult res = pred.typeCheck(typenv);
		typenv.addAll(res.getInferredEnvironment());
		assertTrue(pred.isTypeChecked());
		return pred;
	}

	private void assertSimplifiedE(String input, String expString,
			String... positions) {

		final ITypeEnvironmentBuilder typenv = ff.makeTypeEnvironment();
		final Expression expr = parseExpr(input, typenv);
		final Expression expected = parseExpr(expString, typenv);
		final int len = positions.length;
		final IPosition[] ps = new IPosition[len];
		for (int i = 0; i < len; i++) {
			ps[i] = mPos(positions[i]);
		}
		final Expression actual = AdditiveSimplifier.simplify(expr, ps, ff);
		assertEquals(expected, actual);
	}

	private void assertSimplifiedP(String input, String expString,
			String... positions) {

		final ITypeEnvironmentBuilder typenv = ff.makeTypeEnvironment();
		final RelationalPredicate pred = (RelationalPredicate) parsePred(input,
				typenv);
		final Predicate expected = parsePred(expString, typenv);
		final int len = positions.length;
		final IPosition[] ps = new IPosition[len];
		for (int i = 0; i < len; i++) {
			ps[i] = mPos(positions[i]);
		}
		final Predicate actual = AdditiveSimplifier.simplify(pred, ps, ff);
		assertEquals(expected, actual);
	}

	// --------------------------------------------------------------------------
	//
	// Simple tests where some children of an additive operator are removed
	//
	// --------------------------------------------------------------------------

	@Test
	public void addFirst() throws Exception {
		assertSimplifiedE("a + b + c", "b + c", "0");
	}

	@Test
	public void addMiddle() throws Exception {
		assertSimplifiedE("a + b + c", "a + c", "1");
	}

	@Test
	public void addLast() throws Exception {
		assertSimplifiedE("a + b + c", "a + b", "2");
	}

	@Test
	public void addAllButFirst() throws Exception {
		assertSimplifiedE("a + b + c", "a", "1", "2");
	}

	@Test
	public void addAllButMiddle() throws Exception {
		assertSimplifiedE("a + b + c", "b", "0", "2");
	}

	@Test
	public void addAllButLast() throws Exception {
		assertSimplifiedE("a + b + c", "c", "0", "1");
	}

	@Test
	public void addAll() throws Exception {
		assertSimplifiedE("a + b + c", "0", "0", "1", "2");
	}

	@Test
	public void subLeft() throws Exception {
		assertSimplifiedE("a − b", "−b", "0");
	}

	@Test
	public void subRight() throws Exception {
		assertSimplifiedE("a − b", "a", "1");
	}

	@Test
	public void subAll() throws Exception {
		assertSimplifiedE("a − b", "0", "0", "1");
	}

	// --------------------------------------------------------------------------
	//
	// More elaborate tests checking cumulative simplifications.
	//
	// --------------------------------------------------------------------------

	@Test
	public void unminusFirstChildOfAdd() throws Exception {
		assertSimplifiedE("a − b + c", "−b + c", "0.0");
	}

	@Test
	public void unminusSecondChildOfAdd() throws Exception {
		assertSimplifiedE("a + (b − c) + d", "a − c + d", "1.0");
	}

	@Test
	public void unminusThirdChildOfAdd() throws Exception {
		assertSimplifiedE("a + b + (c − d) + e", "a + b − d + e", "2.0");
	}

	@Test
	public void unminusLastChildOfAdd() throws Exception {
		assertSimplifiedE("a + b + (c − d)", "a + b − d", "2.0");
	}

	@Test
	public void unminusLeftOfSub() throws Exception {
		assertSimplifiedE("a − b − c", "−b − c", "0.0");
	}

	@Test
	public void unminusRightOfSub() throws Exception {
		assertSimplifiedE("a − (b − c)", "a + c", "1.0");
	}

	@Test
	public void unminusInSubInAdd() throws Exception {
		assertSimplifiedE("a + b − (c − d)", "a + b + d", "1.0");
	}

	@Test
	public void unminusDouble() throws Exception {
		assertSimplifiedE("a − (b − c)", "c", "0", "1.0");
	}

	// --------------------------------------------------------------------------
	//
	// Simple tests for all relational predicates
	//
	// --------------------------------------------------------------------------

	@Test
	public void equals() throws Exception {
		assertSimplifiedP("a + b = c + b", "a = c", "0.1", "1.1");
	}

	@Test
	public void notEquals() throws Exception {
		assertSimplifiedP("a + b ≠ c + b", "a ≠ c", "0.1", "1.1");
	}

	@Test
	public void lt() throws Exception {
		assertSimplifiedP("a + b < c + b", "a < c", "0.1", "1.1");
	}

	@Test
	public void le() throws Exception {
		assertSimplifiedP("a + b ≤ c + b", "a ≤ c", "0.1", "1.1");
	}

	@Test
	public void gt() throws Exception {
		assertSimplifiedP("a + b > c + b", "a > c", "0.1", "1.1");
	}

	@Test
	public void ge() throws Exception {
		assertSimplifiedP("a + b ≥ c + b", "a ≥ c", "0.1", "1.1");
	}

	// --------------------------------------------------------------------------
	//
	// Ensures that expression simplifications are properly propagated at the
	// predicate level.
	//
	// --------------------------------------------------------------------------

	@Test
	public void relComplex() throws Exception {
		assertSimplifiedP("a − b = a", "−b = 0", "0.0", "1");
	}

}
