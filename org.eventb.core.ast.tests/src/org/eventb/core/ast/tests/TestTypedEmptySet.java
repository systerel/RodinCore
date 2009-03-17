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
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.FastFactory.mAssociativeExpression;
import static org.eventb.core.ast.tests.FastFactory.mAssociativePredicate;
import static org.eventb.core.ast.tests.FastFactory.mBecomesEqualTo;
import static org.eventb.core.ast.tests.FastFactory.mBecomesMemberOf;
import static org.eventb.core.ast.tests.FastFactory.mBecomesSuchThat;
import static org.eventb.core.ast.tests.FastFactory.mBinaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mBinaryPredicate;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentDecl;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mEmptySet;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mMaplet;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedExpression;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedPredicate;
import static org.eventb.core.ast.tests.FastFactory.mRelationalPredicate;
import static org.eventb.core.ast.tests.FastFactory.mSetExtension;
import static org.eventb.core.ast.tests.FastFactory.mSimplePredicate;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import static org.eventb.core.ast.tests.FastFactory.mUnaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mUnaryPredicate;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;

public class TestTypedEmptySet extends AbstractTests {

	// Types used in these tests
	private static GivenType ty_S = ff.makeGivenType("S");
	private static GivenType ty_T = ff.makeGivenType("T");
	private static GivenType ty_U = ff.makeGivenType("U");
	private static GivenType ty_V = ff.makeGivenType("V");

	private static Type POW(Type base) {
		return ff.makePowerSetType(base);
	}

	private static Type CPROD(Type left, Type right) {
		return ff.makeProductType(left, right);
	}
	
	private static Type REL(Type left, Type right) {
		return ff.makeRelationalType(left, right);
	}
	
	ITypeEnvironment env;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		env = mTypeEnvironment();
	}
	
	/**
	 * Main test routine for expressions containing empty sets.
	 * 
	 * Tests have been entered in the same order as the type-checker
	 * specification in the Rodin Deliverable D7 "Event-B Language".
	 * 
	 * Only tests where an empty set can occur have been retained.
	 */
	public void testExpressions () {

		AtomicExpression eS = mEmptySet(POW(ty_S));
		AtomicExpression eT = mEmptySet(POW(ty_T));
		AtomicExpression ePS = mEmptySet(POW(POW(ty_S)));
		AtomicExpression eST = mEmptySet(REL(ty_S, ty_T));
		AtomicExpression eSU = mEmptySet(REL(ty_S, ty_U));
		AtomicExpression eTU = mEmptySet(REL(ty_T, ty_U));
		AtomicExpression eUV = mEmptySet(REL(ty_U, ty_V));
		AtomicExpression ePST = mEmptySet(REL(POW(ty_S), ty_T));

		BoundIdentDecl bd_x = mBoundIdentDecl("x", POW(ty_S));
		BoundIdentifier b0S = mBoundIdentifier(0, POW(ty_S));

		//--------------------
		//  Binary expressions
		//--------------------
		doTest(mBinaryExpression(Formula.FUNIMAGE, ePST, eS), ty_T);

		doTest(mBinaryExpression(Formula.RELIMAGE, eST, eS), POW(ty_T));
		
		doTest(mBinaryExpression(Formula.MAPSTO, eS, eT), CPROD(POW(ty_S), POW(ty_T)));

		doTest(mBinaryExpression(Formula.REL,   eS, eT), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.TREL,  eS, eT), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.SREL,  eS, eT), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.STREL, eS, eT), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.PFUN,  eS, eT), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.TFUN,  eS, eT), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.PINJ,  eS, eT), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.TINJ,  eS, eT), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.PSUR,  eS, eT), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.TSUR,  eS, eT), POW(REL(ty_S, ty_T)));
		doTest(mBinaryExpression(Formula.TBIJ,  eS, eT), POW(REL(ty_S, ty_T)));

		doTest(mAssociativeExpression(Formula.BUNION, eS, eS),     POW(ty_S));
		doTest(mAssociativeExpression(Formula.BUNION, eS, eS, eS), POW(ty_S));
		doTest(mAssociativeExpression(Formula.BINTER, eS, eS),     POW(ty_S));
		doTest(mAssociativeExpression(Formula.BINTER, eS, eS, eS), POW(ty_S));
		doTest(mBinaryExpression(Formula.SETMINUS, eS, eS), POW(ty_S));
		
		doTest(mBinaryExpression(Formula.CPROD, eS, eT), REL(ty_S, ty_T));
		
		doTest(mBinaryExpression(Formula.DPROD, eST, eSU), REL(ty_S, CPROD(ty_T, ty_U)));
		
		doTest(mBinaryExpression(Formula.PPROD, eST, eUV), 
				REL(CPROD(ty_S, ty_U), CPROD(ty_T, ty_V)));
		
		doTest(mAssociativeExpression(Formula.BCOMP, eTU, eST),      REL(ty_S, ty_U));
		doTest(mAssociativeExpression(Formula.BCOMP, eUV, eTU, eST), REL(ty_S, ty_V));
		
		doTest(mAssociativeExpression(Formula.FCOMP, eST, eTU),      REL(ty_S, ty_U));
		doTest(mAssociativeExpression(Formula.FCOMP, eST, eTU, eUV), REL(ty_S, ty_V));
		
		doTest(mAssociativeExpression(Formula.OVR, eST, eST),      REL(ty_S, ty_T));
		doTest(mAssociativeExpression(Formula.OVR, eST, eST, eST), REL(ty_S, ty_T));
		
		doTest(mBinaryExpression(Formula.DOMRES, eS, eST), REL(ty_S, ty_T));
		doTest(mBinaryExpression(Formula.DOMSUB, eS, eST), REL(ty_S, ty_T));

		doTest(mBinaryExpression(Formula.RANRES, eST, eT), REL(ty_S, ty_T));
		doTest(mBinaryExpression(Formula.RANSUB, eST, eT), REL(ty_S, ty_T));
		
		//-------------------
		//  Unary expressions
		//-------------------
		doTest(mUnaryExpression(Formula.CONVERSE, eST), REL(ty_T, ty_S));
		
		// doTest(mUnaryExpression(Formula.CARD, id_A), INT);

		doTest(mUnaryExpression(Formula.POW,  eS), POW(POW(ty_S)));
		doTest(mUnaryExpression(Formula.POW1, eS), POW(POW(ty_S)));
		
		doTest(mUnaryExpression(Formula.KUNION, ePS), POW(ty_S));
		doTest(mUnaryExpression(Formula.KINTER, ePS), POW(ty_S));
		
		doTest(mUnaryExpression(Formula.KDOM, eST), POW(ty_S));
		
		doTest(mUnaryExpression(Formula.KRAN, eST), POW(ty_T));
		
		doTest(mUnaryExpression(Formula.KPRJ1, eST), REL(CPROD(ty_S, ty_T), ty_S));

		doTest(mUnaryExpression(Formula.KPRJ2, eST), REL(CPROD(ty_S, ty_T), ty_T));
		
		doTest(mUnaryExpression(Formula.KID, eS), REL(ty_S, ty_S));

		
		//--------------------
		//  Lambda expressions
		//--------------------
		doTest(mQuantifiedExpression(Formula.CSET, QuantifiedExpression.Form.Lambda,
						mList(bd_x),
						mRelationalPredicate(Formula.EQUAL, b0S, eS),
						mMaplet(b0S, eT)),
				REL(POW(ty_S), POW(ty_T)));
		
		//------------------------
		//  Quantified expressions
		//------------------------
		doTest(mQuantifiedExpression(Formula.QUNION, QuantifiedExpression.Form.Explicit,
						mList(bd_x),
						mRelationalPredicate(Formula.EQUAL, b0S, eS),
						mSetExtension(eT)),
				POW(POW(ty_T)));
	
		//---------------
		//  Set extension
		//---------------
		doTest(mSetExtension(eS), POW(POW(ty_S)));

		//--------------------
		//  Atomic expressions
		//--------------------
		doTest(eS, POW(ty_S));
	}
	
	private void doTest(Expression expr, Type expected) {
		assertTrue("Input is not typed", expr.isTypeChecked());
		assertEquals("Bad type", expected, expr.getType());
		final String image = expr.toStringWithTypes();
		Expression actual = parseExpression(image);
		typeCheck(actual, env);
		assertEquals("Typed string is a different expression", expr, actual);
	}

	/**
	 * Main test routine for predicates.
	 * 
	 * Tests have been entered in the same order as the type-checker
	 * specification in the Rodin Deliverable D7 "Event-B Language".
	 * 
	 * Only tests where an empty set can occur have been retained.
	 */
	public void testPredicates () {
		
		AtomicExpression eS = mEmptySet(POW(ty_S));
		AtomicExpression ePS = mEmptySet(POW(POW(ty_S)));
		
		RelationalPredicate atom = mRelationalPredicate(Formula.EQUAL, eS, eS);
		BoundIdentDecl bd_x = mBoundIdentDecl("x", POW(ty_S));
		BoundIdentifier b0S = mBoundIdentifier(0, POW(ty_S));

		//--------------------
		//  Binary predicates
		//--------------------
		doTest(mBinaryPredicate(Formula.LIMP, atom, atom));
		doTest(mBinaryPredicate(Formula.LEQV, atom, atom));
		
		doTest(mAssociativePredicate(Formula.LAND, atom, atom));
		doTest(mAssociativePredicate(Formula.LAND, atom, atom, atom));
		doTest(mAssociativePredicate(Formula.LOR, atom, atom));
		doTest(mAssociativePredicate(Formula.LOR, atom, atom, atom));

		//-----------------
		//  Unary predicate
		//-----------------
		doTest(mUnaryPredicate(Formula.NOT, atom));
		
		//-----------------------
		//  Quantified predicates
		//-----------------------
		doTest(mQuantifiedPredicate(Formula.FORALL, mList(bd_x),
				mRelationalPredicate(Formula.EQUAL, b0S, eS)));
		
		doTest(mQuantifiedPredicate(Formula.EXISTS, mList(bd_x),
				mRelationalPredicate(Formula.EQUAL, b0S, eS)));
		
		//--------------------
		//  Simple predicates
		//--------------------
		doTest(mSimplePredicate(eS));
		
		//-----------------------
		//  Relational predicates
		//-----------------------
		doTest(mRelationalPredicate(Formula.EQUAL, eS, eS));
		doTest(mRelationalPredicate(Formula.NOTEQUAL, eS, eS));

		doTest(mRelationalPredicate(Formula.IN, eS, ePS));
		doTest(mRelationalPredicate(Formula.NOTIN, eS, ePS));

		doTest(mRelationalPredicate(Formula.SUBSET, eS, eS));
		doTest(mRelationalPredicate(Formula.NOTSUBSET, eS, eS));
		doTest(mRelationalPredicate(Formula.SUBSETEQ, eS, eS));
		doTest(mRelationalPredicate(Formula.NOTSUBSETEQ, eS, eS));

		//-------------------------------------
		//  Ensure no capture of given set name
		//-------------------------------------
		BoundIdentDecl bd_S = mBoundIdentDecl("S", POW(ty_S));
		doTest(mQuantifiedPredicate(Formula.EXISTS, mList(bd_S),
				mRelationalPredicate(Formula.EQUAL, b0S, eS)));
	}
	
	private void doTest(Predicate pred) {
		assertTrue("Input is not typed", pred.isTypeChecked());
		final String image = pred.toStringWithTypes();
		final Predicate actual = parsePredicate(image);
		typeCheck(actual, env);
		assertEquals("Typed string is a different predicate", pred, actual);
	}

	/**
	 * Main test routine for assignments.
	 * 
	 * Tests have been entered in the same order as the type-checker
	 * specification in the Rodin Deliverable D7 "Event-B Language".
	 */
	public void testAssignments () {
		
		AtomicExpression eS = mEmptySet(POW(ty_S));
		AtomicExpression eT = mEmptySet(POW(ty_T));
		AtomicExpression eU = mEmptySet(POW(ty_U));
		AtomicExpression ePS = mEmptySet(POW(POW(ty_S)));
		
		
		FreeIdentifier id_x = mFreeIdentifier("x", POW(ty_S));
		FreeIdentifier id_y = mFreeIdentifier("y", POW(ty_T));
		FreeIdentifier id_z = mFreeIdentifier("z", POW(ty_U));

		BoundIdentDecl bd_x = mBoundIdentDecl("x'", POW(ty_S));
		BoundIdentDecl bd_y = mBoundIdentDecl("y'", POW(ty_T));
		BoundIdentDecl bd_z = mBoundIdentDecl("z'", POW(ty_U));
		
		BoundIdentifier b0S = mBoundIdentifier(0, POW(ty_S));
		BoundIdentifier b0T = mBoundIdentifier(0, POW(ty_T));
		BoundIdentifier b0U = mBoundIdentifier(0, POW(ty_U));
		BoundIdentifier b1S = mBoundIdentifier(1, POW(ty_S));
		BoundIdentifier b1T = mBoundIdentifier(1, POW(ty_T));
		BoundIdentifier b2S = mBoundIdentifier(2, POW(ty_S));

		doTest(mBecomesEqualTo(id_x, eS));
		doTest(mBecomesEqualTo(mList(id_x, id_y), mList(eS, eT)));
		doTest(mBecomesEqualTo(mList(id_x, id_y, id_z), mList(eS, eT, eU)));
		
		doTest(mBecomesMemberOf(id_x, ePS));
		
		doTest(mBecomesSuchThat(mList(id_x), mList(bd_x), 
				mRelationalPredicate(Formula.EQUAL, b0S, eS)
		));
		doTest(mBecomesSuchThat(mList(id_x, id_y), mList(bd_x, bd_y),
				mRelationalPredicate(Formula.EQUAL,
						mMaplet(b1S,  b0T),
						mMaplet(eS, eT))
		));
		doTest(mBecomesSuchThat(mList(id_x, id_y, id_z), mList(bd_x, bd_y, bd_z),
				mRelationalPredicate(Formula.EQUAL,
						mMaplet(b2S, mMaplet(b1T, b0U)),
						mMaplet(eS,  mMaplet(eT,  eU)))
		));
	}

	private void doTest(Assignment assign) {
		assertTrue("Input is not typed", assign.isTypeChecked());
		final String image = assign.toStringWithTypes();
		final Assignment actual = parseAssignment(image);
		typeCheck(actual, env);
		assertEquals("Typed string is a different predicate", assign, actual);
	}

}
