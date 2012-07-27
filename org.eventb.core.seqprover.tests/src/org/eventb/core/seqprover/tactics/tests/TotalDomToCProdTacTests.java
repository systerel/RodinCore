/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tactics.tests;

import static org.eventb.core.seqprover.tactics.tests.TreeShape.empty;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.totalDom;

import org.eventb.core.ast.Expression;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.junit.Test;

/**
 * Unit tests for the tactic TotalDomToCProdTac, which substitutes a domain by a
 * Cartesian product in the goal. The goal must be as follow :  <code>x↦y∈dom(g)</code>
 * 
 * @author Emmanuel Billaud
 */
public class TotalDomToCProdTacTests extends AbstractTacticTests {

	public TotalDomToCProdTacTests() {
		super(new AutoTactics.TotalDomToCProdAutoTac(),
				"org.eventb.core.seqprover.totalDomToCProdTac");
	}

	/**
	 * Assert that auto-tactic succeeds once when the relation is :
	 * <ul>
	 * <li>a total relation : </li>
	 * <li>a total surjective relation : </li>
	 * <li>a total function : →</li>
	 * <li>a total injection : ↣</li>
	 * <li>a total surjection : ↠</li>
	 * <li>a bijection : ⤖</li>
	 * </ul>
	 */
	@Test
	public void applyOnce() {
		final String[] ops = new String[] { "", "", "→", "↣", "↠", "⤖" };
		final Expression expr = parseExpression("ℤ×ℤ");
		final TreeShape shape = totalDom(null, "1", expr, empty);
		for (String op : ops) {
			assertSuccess(";H; ;S; g∈ℤ×ℤ " + op + " ℤ  |- x ↦ y∈dom(g)", shape);
		}
	}

	/**
	 * Assert that auto-tactic fails when the operator op in the sequent
	 * <code>g∈ℤ×ℤ op ℤ ⊦ x ↦ y∈dom(g)</code> is :
	 * <ul>
	 * <li>a relation : ↔</li>
	 * <li>a surjective relation : </li>
	 * <li>a partial function : ⇸</li>
	 * <li>a partial injection : ⤔</li>
	 * <li>a partial surjection : ⤀</li>
	 * </ul>
	 */
	@Test
	public void fails() {
		final String[] ops = new String[] { "↔", "", "⇸", "⤔", "⤀" };
		for (String op : ops) {
			assertFailure(";H; ;S; g∈ℤ×ℤ " + op + " ℤ  |- x↦y∈dom(g)");
		}
	}

	/**
	 * Assert that the auto-tactic fails when the goal is incorrect :
	 * <ul>
	 * <li>it is not an inclusion</li>
	 * <li>left member of the inclusion is not a mapplet</li>
	 * <li>right member of the inclusion is not a domain</li>
	 * </ul>
	 */
	@Test
	public void failsOnGoal() {
		assertFailure(";H; ;S; g∈ℤ×ℤ×ℤ⤖ℤ |- x ↦ y∉dom(g)");
		assertFailure(";H; ;S; g∈ℤ×ℤ×ℤ⤖ℤ |- f∈dom(g)");
		assertFailure(";H; ;S; g∈ℤ×ℤ×ℤ⤖ℤ |- u ↦ v∈ℤ×ℤ");
	}

	/**
	 * Assert that auto-tactic fails when :
	 * <ul>
	 * <li>g is not explicitly set as a relation</li>
	 * <li>dom(g) cannot be substituted by a Cartesian product</li>
	 * </ul>
	 * 
	 */
	@Test
	public void failsOnHypothesis() {
		assertFailure(" ;H; ;S; A=ℤ×ℤ⤖ℤ ;; g∈A ;; dom(g)=ℤ×ℤ |- x ↦ y∈dom(g)");
		assertFailure(" ;H; ;S; B=ℤ×ℤ ;; h∈B⤖ℤ |- z ↦ t∈dom(h)");
	}

}