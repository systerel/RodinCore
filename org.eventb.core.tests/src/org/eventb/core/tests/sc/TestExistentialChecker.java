/*******************************************************************************
 * Copyright (c) 2025 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.sc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.SourceLocation;
import org.eventb.internal.core.sc.ExistentialChecker;
import org.junit.Test;

/**
 * Unit tests for the {@link ExistentialChecker} class.
 */
public class TestExistentialChecker {

	private static final FormulaFactory ff = FormulaFactory.getDefault();

	@Test
	public void testBecomesEqualTo() {
		assertAssignmentLocations("x ≔ TRUE");
		assertAssignmentLocations("x ≔ bool(∃y· y=1 ⇒ x=2)", 13, 21);
		assertAssignmentLocations("x,y ≔ bool(∃z· z=1 ⇒ x=2), bool(∃z· z=1 ⇒ y=2)", 15, 23, 36, 44);
	}

	@Test
	public void testBecomesMemberOf() {
		assertAssignmentLocations("x :∈ BOOL");
		assertAssignmentLocations("x :∈ {y ∣ ∃z· z=1 ⇒ y=2}", 14, 22);
	}

	@Test
	public void testSuchThat() {
		assertAssignmentLocations("x :∣ x'=2");
		assertAssignmentLocations("x :∣ (∃z· z=1 ⇒ x=2) ∨ x'=3", 10, 18);
	}

	@Test
	public void testPredicate() {
		assertPredicateLocations("x=2");
		assertPredicateLocations("∃z· z=1 ⇒ x=2", 4, 12);
		assertPredicateLocations("(∃z· z=1 ⇒ x=2) ∨ x=3", 5, 13);
		assertPredicateLocations("(∃y· y=1 ⇒ (∃z· z=2 ⇒ x=3)) ∨ x=4", 5, 25, 16, 24);
	}

	@Test
	public void testExpression() {
		assertExpressionLocations("2");
		assertExpressionLocations("bool(∃z· z=1 ⇒ x=2)", 9, 17);
	}

	private static void assertAssignmentLocations(String image, int... bounds) {
		var assignement = ff.parseAssignment(image, null).getParsedAssignment();
		assertLocations(assignement, bounds);
	}

	private static void assertPredicateLocations(String image, int... bounds) {
		var predicate = ff.parsePredicate(image, null).getParsedPredicate();
		assertLocations(predicate, bounds);
	}

	private static void assertExpressionLocations(String image, int... bounds) {
		var expression = ff.parseExpression(image, null).getParsedExpression();
		assertLocations(expression, bounds);
	}

	private static void assertLocations(Formula<?> formula, int... bounds) {
		if (bounds.length % 2 != 0) {
			fail("should have an even number of bounds");
		}
		List<SourceLocation> expected = new ArrayList<>();
		for (int i = 0; i < bounds.length; i += 2) {
			expected.add(new SourceLocation(bounds[i], bounds[i + 1]));
		}

		assertEquals(expected, ExistentialChecker.check(formula));
	}

}
