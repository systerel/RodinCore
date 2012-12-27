/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.expander.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.eventb.core.ast.Formula.KPARTITION;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mMultiplePredicate;
import static org.eventb.core.ast.tests.FastFactory.mSetExtension;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.expanders.Expanders;
import org.eventb.core.ast.tests.AbstractTests;
import org.junit.Test;

public class PartitionExpanderTests extends AbstractTests {

	private static final Type S = ff.makeGivenType("S");

	private static final FreeIdentifier s0 = mFreeIdentifier("s0", POW(S));
	private static final FreeIdentifier s1 = mFreeIdentifier("s1", POW(S));
	private static final FreeIdentifier s2 = mFreeIdentifier("s2", POW(S));
	private static final FreeIdentifier s3 = mFreeIdentifier("s3", POW(S));
	private static final FreeIdentifier s4 = mFreeIdentifier("s4", POW(S));

	private static final FreeIdentifier x1 = mFreeIdentifier("x1", S);
	private static final FreeIdentifier x2 = mFreeIdentifier("x2", S);

	private static MultiplePredicate partition(Expression... children) {
		return mMultiplePredicate(KPARTITION, children);
	}

	@Test 
	public void testPartition() {
		assertExpansion(partition(s0), "s0 = ∅");
		assertExpansion(partition(s0, s1), "s0 = s1");
		assertExpansion(partition(s0, s1, s2), "s0 = s1 ∪ s2 ∧ s1 ∩ s2 = ∅");
		assertExpansion(partition(s0, s1, s2, s3), //
				"s0 = s1 ∪ s2 ∪ s3" //
						+ "∧ s1 ∩ s2 = ∅" //
						+ "∧ s1 ∩ s3 = ∅" //
						+ "∧ s2 ∩ s3 = ∅" //
		);
		assertExpansion(partition(s0, s1, s2, s3, s4), //
				"s0 = s1 ∪ s2 ∪ s3 ∪ s4" //
						+ "∧ s1 ∩ s2 = ∅" //
						+ "∧ s1 ∩ s3 = ∅" //
						+ "∧ s1 ∩ s4 = ∅" //
						+ "∧ s2 ∩ s3 = ∅" //
						+ "∧ s2 ∩ s4 = ∅" //
						+ "∧ s3 ∩ s4 = ∅" //
		);
	}

	@Test 
	public void testPartitionForEnumerated() {
		assertExpansion(partition(s0), "s0 = ∅");
		assertExpansion(partition(s0, mSetExtension(x1)), "s0 = {x1}");
		assertExpansion(partition(s0, mSetExtension(x1), mSetExtension(x2)), //
				"s0 = {x1, x2} ∧ ¬x1 = x2");
		assertExpansion(partition(s0, mSetExtension(x1), s2), //
		"s0 = {x1} ∪ s2 ∧ ¬x1 ∈ s2");
		assertExpansion(partition(s0, s1, mSetExtension(x2)), //
				"s0 = s1 ∪ {x2} ∧ ¬x2 ∈ s1");
	}

	private static void assertExpansion(MultiplePredicate input,
			String expectedString) {
		final ITypeEnvironment tenv = inferTypeEnvironment(input);
		final Predicate expected = parseAndTypeCheck(tenv, expectedString);
		final Predicate actual = Expanders.expandPARTITION(input, ff);
		assertEquals(expected, actual);
	}

	private static ITypeEnvironment inferTypeEnvironment(Formula<?> input) {
		final ITypeCheckResult result = input.typeCheck(mTypeEnvironment());
		return result.getInferredEnvironment();
	}

	private static Predicate parseAndTypeCheck(ITypeEnvironment tenv,
			String input) {
		final Predicate pred = parsePredicate(input);
		pred.typeCheck(tenv);
		assertTrue(pred.isTypeChecked());
		return pred;
	}

}
