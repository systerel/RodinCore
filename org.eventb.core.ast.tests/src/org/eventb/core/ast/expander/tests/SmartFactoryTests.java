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
import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.ast.Formula.BINTER;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LOR;
import static org.eventb.core.ast.Formula.NOT;
import static org.eventb.core.ast.tests.FastFactory.mAssociativeExpression;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mLiteralPredicate;
import static org.eventb.core.ast.tests.FastFactory.mRelationalPredicate;
import static org.eventb.core.ast.tests.FastFactory.mSetExtension;
import static org.eventb.core.ast.tests.FastFactory.mSimplePredicate;
import static org.eventb.core.ast.tests.FastFactory.mUnaryPredicate;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.expanders.Expanders;
import org.eventb.core.ast.expanders.ISmartFactory;
import org.eventb.core.ast.tests.AbstractTests;
import org.junit.Test;

public class SmartFactoryTests extends AbstractTests {

	private static final Type tS = ff.makeGivenType("S");

	private static final FreeIdentifier S = mFreeIdentifier("S", POW(tS));
	private static final FreeIdentifier s0 = mFreeIdentifier("s0", POW(tS));
	private static final FreeIdentifier s1 = mFreeIdentifier("s1", POW(tS));
	private static final FreeIdentifier s2 = mFreeIdentifier("s2", POW(tS));

	private static final FreeIdentifier x0 = mFreeIdentifier("x0", tS);
	private static final FreeIdentifier x1 = mFreeIdentifier("x1", tS);
	private static final FreeIdentifier x2 = mFreeIdentifier("x2", tS);

	private static final Predicate P0 = mSimplePredicate(s0);
	private static final Predicate P1 = mSimplePredicate(s1);

	final ISmartFactory x = Expanders.getSmartFactory(ff);

	private static final Expression ext(Expression... members) {
		return mSetExtension(members);
	}

	private static final Expression union(Expression... children) {
		return mAssociativeExpression(BUNION, children);
	}

	@Test 
	public void testUnion() {
		final Type type = s0.getType();
		assertEquals(x.emptySet(type), x.union(type));
		assertEquals(s0, x.union(type, s0));
		assertEquals(union(s0, s1), //
				x.union(type, s0, s1));
	}

	@Test 
	public void testUnionExtensionSets() {
		final Type type = s0.getType();
		assertEquals(ext(x0), x.union(type, ext(x0)));
		assertEquals(ext(x0, x1), x.union(type, ext(x0, x1)));

		assertEquals(ext(x0, x1), //
				x.union(type, ext(x0), ext(x1)));
		assertEquals(ext(x0, x1, x2), //
				x.union(type, ext(x0), ext(x1, x2)));
		assertEquals(ext(x0, x1, x2), //
				x.union(type, ext(x0), ext(x1), ext(x2)));

		assertEquals(ext(x0), //
				x.union(type, ext(x0), ext(x0)));
		assertEquals(ext(x0, x1), //
				x.union(type, ext(x0), ext(x1, x0)));

		assertEquals(union(s0, ext(x1)), //
				x.union(type, s0, ext(x1)));
		assertEquals(union(ext(x0), s1), //
				x.union(type, ext(x0), s1));

		assertEquals(union(s0, ext(x1), ext(x2)), //
				x.union(type, s0, ext(x1), ext(x2)));
		assertEquals(union(ext(x0), s1, ext(x2)), //
				x.union(type, ext(x0), s1, ext(x2)));
		assertEquals(union(ext(x0), ext(x1), s2), //
				x.union(type, ext(x0), ext(x1), s2));
	}

	@Test 
	public void testInter() {
		final Type type = s0.getType();
		assertEquals(S, x.inter(type));
		assertEquals(s0, x.inter(type, s0));
		assertEquals(mAssociativeExpression(BINTER, s0, s1), //
				x.inter(type, s0, s1));
	}

	@Test 
	public void testLand() {
		List<Predicate> list = new ArrayList<Predicate>();
		assertEquals(mLiteralPredicate(BTRUE), x.land(list));
		list.add(P0);
		assertEquals(P0, x.land(list));
		list.add(P1);
		assertEquals(ff.makeAssociativePredicate(LAND, list, null), //
				x.land(list));
	}

	@Test 
	public void testLor() {
		List<Predicate> list = new ArrayList<Predicate>();
		assertEquals(mLiteralPredicate(BFALSE), x.lor(list));
		list.add(P0);
		assertEquals(P0, x.lor(list));
		list.add(P1);
		assertEquals(ff.makeAssociativePredicate(LOR, list, null), //
				x.lor(list));
	}

	@Test 
	public void testNot() {
		assertEquals(mUnaryPredicate(NOT, P0), x.not(P0));
		assertEquals(P0, x.not(mUnaryPredicate(NOT, P0)));
	}

	@Test 
	public void testDisjoint() {
		final Type type = s1.getType();
		assertEquals(x.equals(x.inter(type, s1, s2), x.emptySet(type)), //
				x.disjoint(s1, s2));
		assertEquals(x.not(x.in(x1, s2)), //
				x.disjoint(ext(x1), s2));
		assertEquals(x.not(x.in(x2, s1)), //
				x.disjoint(s1, ext(x2)));
		assertEquals(x.not(x.equals(x1, x2)), //
				x.disjoint(ext(x1), ext(x2)));
	}
	
	@Test 
	public void testIn() {
		// Nominal case
		assertEquals(mRelationalPredicate(IN, x0, s0), x.in(x0, s0));

		// Set is empty
		assertEquals(mLiteralPredicate(BFALSE), x.in(x0, ext()));
		assertEquals(mLiteralPredicate(BFALSE), x.in(x0, x.emptySet(POW(tS))));
		
		// Set is a singleton
		assertEquals(x.equals(x0, x1), x.in(x0, ext(x1)));
		
		// Set is an extension, but not a singleton
		final Expression s = ext(x1, x2);
		assertEquals(mRelationalPredicate(IN, x0, s), x.in(x0, s));
		
		// Set is a type
		assertEquals(mLiteralPredicate(BTRUE), x.in(x0, S));
	}

}
