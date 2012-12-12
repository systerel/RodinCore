/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 ******************************************************************************/
package org.eventb.core.ast.tests;

import static junit.framework.Assert.assertEquals;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.NOTIN;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentDecl;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mEmptySet;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedPredicate;
import static org.eventb.core.ast.tests.FastFactory.mRelationalPredicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.junit.Before;
import org.junit.Test;

public class DocTests extends AbstractTests {

	private Predicate pred;

	private static  List<String> serialized = Arrays.asList(
			"(∅ ⦂ ℙ(S))=(∅ ⦂ ℙ(S))∧(∃x⦂T·x∈(∅ ⦂ ℙ(T)))∧x∉(∅ ⦂ ℙ(S×T))", //
			"x", //
			"S×T");

	@Before
	public void setUp() throws Exception {
		final Type ty_S = ff.makeGivenType("S");
		final Type ty_T = ff.makeGivenType("T");
		final Expression empty_S = mEmptySet(POW(ty_S));
		final Expression empty_T = mEmptySet(POW(ty_T));
		final Expression empty_ST = mEmptySet(POW(CPROD(ty_S, ty_T)));
		final FreeIdentifier id_x = mFreeIdentifier("x", CPROD(ty_S, ty_T));
		final BoundIdentDecl bid_x = mBoundIdentDecl("x", ty_T);
		final BoundIdentifier b0 = mBoundIdentifier(0, ty_T);
		final Predicate in = mRelationalPredicate(IN, b0, empty_T);

		final Collection<Predicate> ps = new ArrayList<Predicate>();
		ps.add(mRelationalPredicate(EQUAL, empty_S, empty_S));
		ps.add(mQuantifiedPredicate(EXISTS, mList(bid_x), in));
		ps.add(mRelationalPredicate(NOTIN, id_x, empty_ST));
		pred = ff.makeAssociativePredicate(LAND, ps, null);
		assert pred.isTypeChecked();
	}

	/**
	 * Code shown in the Rodin Wiki about Mathematical Formula Serialization.
	 */
	@Test 
	public void testSerialization() {
		final Formula<?> foo = pred;
		final List<String> strings = new ArrayList<String>();

		// Serialize predicate
		assert foo.isTypeChecked();
		strings.add(foo.toStringWithTypes());

		// Serialize type environment
		for (FreeIdentifier i : foo.getFreeIdentifiers()) {
			strings.add(i.getName());
			strings.add(i.getType().toString());
		}

		assertEquals(serialized, strings);
	}
	
	/**
	 * Code shown in the Rodin Wiki about Mathematical Formula Serialization.
	 */
	@Test 
	public void testDeserialization() throws Exception {
		final List<String> strings = serialized;
		
		// De-serialize type environment
		final ITypeEnvironmentBuilder typenv = ff.makeTypeEnvironment();
		int size = strings.size();
		for (int i = 1; i < size; i += 2) {
			final String name = strings.get(i);
			final Type type = parseType(strings.get(i + 1));
			typenv.addName(name, type);
		}

		// De-serialize predicate
		final Predicate bar = parsePredicate(strings.get(0));
		typeCheck(bar, typenv);
		assertEquals(pred, bar);
	}

}
