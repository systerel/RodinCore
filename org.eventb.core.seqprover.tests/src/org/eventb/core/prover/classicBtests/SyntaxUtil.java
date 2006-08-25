/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.prover.classicBtests;

import java.math.BigInteger;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.Type;

/**
 * @author Stefan Hallerstede
 *
 */
public class SyntaxUtil {
	
	public static FormulaFactory factory = FormulaFactory.getDefault();
	
	public static String[] mList(String... names) {
		return names;
	}

	public static Type[] mList(Type... types) {
		return types;
	}

	public static IntegerType INTEGER = factory.makeIntegerType();
	public static BooleanType BOOL = factory.makeBooleanType();

	public static Type POW(Type base) {
		return factory.makePowerSetType(base);
	}

	public static Type CPROD(Type left, Type right) {
		return factory.makeProductType(left, right);
	}
	
	public static ITypeEnvironment mTypeEnvironment(String[] names, Type[] types) {
		assert names.length == types.length;
		ITypeEnvironment result = factory.makeTypeEnvironment();
		for (int i = 0; i < names.length; i++) {
			result.addName(names[i], types[i]);
		}
		return result;
	}

	public static FreeIdentifier[] mi(FreeIdentifier...freeIdentifiers) {
		return freeIdentifiers;
	}
	
	public static Integer[] mj(Integer...integers) {
		return integers;
	}
	
	public static Expression[] me(Expression...expressions) {
		return expressions;
	}
	
	public static Predicate[] mp(Predicate...predicates) {
		return predicates;
	}
	
	public static Predicate eq(Expression l, Expression r) {
		return factory.makeRelationalPredicate(Formula.EQUAL, l, r, null);
	}
	
	public static Predicate lt(Expression l, Expression r) {
		return factory.makeRelationalPredicate(Formula.LT, l, r, null);
	}
	
	public static QuantifiedPredicate forall(BoundIdentDecl[] bd, Predicate pr) {
		return factory.makeQuantifiedPredicate(Formula.FORALL, bd, pr, null);
	}
	
	public static QuantifiedPredicate exists(BoundIdentDecl[] bd, Predicate pr) {
		return factory.makeQuantifiedPredicate(Formula.EXISTS, bd, pr, null);
	}
	
	public static BoundIdentDecl[] BD(String...strings) {
		BoundIdentDecl[] bd = new BoundIdentDecl[strings.length];
		for(int i=0; i<strings.length; i++)
			bd[i] = factory.makeBoundIdentDecl(strings[i], null);
		return bd;
	}
	
	public static BoundIdentifier bd(int i) {
		return factory.makeBoundIdentifier(i, null);
	}
	
	public static Expression apply(Expression l, Expression r) {
		return factory.makeBinaryExpression(Formula.FUNIMAGE, l, r, null);
	}
	
	public static Expression num(int i) {
		return factory.makeIntegerLiteral(BigInteger.valueOf(i), null);
	}
	
	public static Expression plus(Expression...expressions) {
		return factory.makeAssociativeExpression(Formula.PLUS, expressions, null);
	}
	
	public static Expression minus(Expression l, Expression r) {
		return factory.makeBinaryExpression(Formula.MINUS, l, r, null);
	}
	
	public static Predicate in(Expression l, Expression r) {
		return factory.makeRelationalPredicate(Formula.IN, l, r, null);
	}
	
	public static Expression fun(BoundIdentDecl[] d, Predicate p, Expression e) {
		return factory.makeQuantifiedExpression(Formula.CSET, d, p, e, null, QuantifiedExpression.Form.Lambda);
	}
	
	public static Expression set(BoundIdentDecl[] d, Predicate p, Expression e) {
		return factory.makeQuantifiedExpression(Formula.CSET, d, p, e, null, QuantifiedExpression.Form.Explicit);
	}
	
	public static Predicate limp(Predicate l, Predicate r) {
		return factory.makeBinaryPredicate(Formula.LIMP, l, r, null);
	}
	
	public static Expression maplet(Expression l, Expression r) {
		return factory.makeBinaryExpression(Formula.MAPSTO, l, r,null);
	}
	
	public static FreeIdentifier id_x = factory.makeFreeIdentifier("x", null);
	public static FreeIdentifier id_y = factory.makeFreeIdentifier("y", null);
	public static FreeIdentifier id_A = factory.makeFreeIdentifier("A", null);
	public static FreeIdentifier id_f = factory.makeFreeIdentifier("f", null);
	
}
