/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added abstract test class
 *     Systerel - mathematical language v2
 *     Systerel - added support for predicate variables
 *     Systerel - added mathematical extensions
 *     Systerel - added specialization
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static java.util.Collections.singletonList;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.FREE_IDENT;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.KID_GEN;
import static org.eventb.core.ast.Formula.KPARTITION;
import static org.eventb.core.ast.Formula.KPRJ1_GEN;
import static org.eventb.core.ast.Formula.KPRJ2_GEN;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.MAPSTO;
import static org.eventb.core.ast.Formula.MINUS;
import static org.eventb.core.ast.Formula.NOT;
import static org.eventb.core.ast.Formula.PLUS;
import static org.eventb.core.ast.Formula.POW;
import static org.eventb.core.ast.Formula.QUNION;
import static org.eventb.core.ast.Formula.TRUE;
import static org.eventb.core.ast.QuantifiedExpression.Form.Explicit;
import static org.eventb.core.ast.tests.AbstractTests.LIST_DT;
import static org.eventb.core.ast.tests.AbstractTests.parseExpression;
import static org.eventb.core.ast.tests.AbstractTests.parseType;
import static org.eventb.core.ast.tests.InjectedDatatypeExtension.injectExtension;
import static org.eventb.core.ast.tests.TestGenParser.EXT_PRIME;
import static org.eventb.core.ast.tests.TestGenParser.MONEY;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IInferredTypeEnvironment;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.internal.core.typecheck.InferredTypeEnvironment;

/**
 * Provides simplistic methods for creating new formulae without too much
 * typing.
 * <p>
 * The methods provided are essentially geared towards unit test development,
 * making it less painful to build test formulae with a keyboard.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class FastFactory {

	public static final Predicate[] NO_PREDICATE = new Predicate[0];
	public static final Expression[] NO_EXPRESSION = new Expression[0];
	public static final Type[] NO_TYPES = new Type[0];

	private static final Set<IFormulaExtension> EXTNS = new HashSet<IFormulaExtension>(
			Arrays.asList(EXT_PRIME, MONEY));
	static {
		EXTNS.addAll(LIST_DT.getExtensions());
	}

	public static FormulaFactory ff = FormulaFactory.getDefault();

	public static FormulaFactory ff_extns = FormulaFactory.getInstance(EXTNS);

	public static FormulaFactory mDatatypeFactory(FormulaFactory initial,
			String... datatypeImages) {
		FormulaFactory fac = initial;
		for (final String datatypeImage : datatypeImages) {
			fac = mDatatypeFactory(fac, datatypeImage);
		}
		return fac;
	}

	public static FormulaFactory mDatatypeFactory(FormulaFactory initial,
			String datatypeImage) {
		final IDatatypeExtension dtExt = injectExtension(datatypeImage);
		final IDatatype datatype = initial.makeDatatype(dtExt);
		final Set<IFormulaExtension> exts = initial.getExtensions();
		exts.addAll(datatype.getExtensions());
		return FormulaFactory.getInstance(exts);
	}

	public static AssociativeExpression mAssociativeExpression(
			Expression... children) {
		return mAssociativeExpression(PLUS, children);
	}

	public static AssociativeExpression mAssociativeExpression(int tag,
			Expression... children) {
		return ff.makeAssociativeExpression(tag, children, null);
	}

	public static AssociativePredicate mAssociativePredicate(int tag,
			Predicate... children) {
		return ff.makeAssociativePredicate(tag, children, null);
	}

	public static AssociativePredicate mAssociativePredicate(
			Predicate... children) {
		return mAssociativePredicate(LAND, children);
	}

	public static AtomicExpression mAtomicExpression() {
		return mAtomicExpression(TRUE);
	}

	public static AtomicExpression mAtomicExpression(int tag) {
		return ff.makeAtomicExpression(tag, null);
	}

	public static AtomicExpression mEmptySet(Type type) {
		return type.getFactory().makeEmptySet(type, null);
	}

	public static AtomicExpression mPrj1(Type type) {
		return ff.makeAtomicExpression(KPRJ1_GEN, null, type);
	}

	public static AtomicExpression mPrj2(Type type) {
		return ff.makeAtomicExpression(KPRJ2_GEN, null, type);
	}

	public static AtomicExpression mId(Type type) {
		return ff.makeAtomicExpression(KID_GEN, null, type);
	}

	public static BecomesEqualTo mBecomesEqualTo(FreeIdentifier ident,
			Expression value) {
		return ff.makeBecomesEqualTo(ident, value, null);
	}

	public static BecomesEqualTo mBecomesEqualTo(FreeIdentifier[] lhs,
			Expression[] rhs) {
		return ff.makeBecomesEqualTo(lhs, rhs, null);
	}

	public static BecomesMemberOf mBecomesMemberOf(FreeIdentifier lhs,
			Expression rhs) {
		return ff.makeBecomesMemberOf(lhs, rhs, null);
	}

	public static BecomesSuchThat mBecomesSuchThat(FreeIdentifier[] lhs,
			BoundIdentDecl[] primed, Predicate rhs) {
		return ff.makeBecomesSuchThat(lhs, primed, rhs, null);
	}

	public static BecomesSuchThat mBecomesSuchThat(FreeIdentifier[] lhs,
			Predicate rhs) {
		BoundIdentDecl[] primed = new BoundIdentDecl[lhs.length];
		for (int i = 0; i < lhs.length; i++) {
			primed[i] = lhs[i].asDecl();
		}
		return mBecomesSuchThat(lhs, primed, rhs);
	}

	public static BinaryExpression mBinaryExpression(Expression left,
			Expression right) {
		return mBinaryExpression(MINUS, left, right);
	}

	public static BinaryExpression mBinaryExpression(int tag, Expression left,
			Expression right) {
		return ff.makeBinaryExpression(tag, left, right, null);
	}

	public static BinaryExpression mBinaryExpression(int tag, Expression left,
			Expression right, FormulaFactory fac) {
		return fac.makeBinaryExpression(tag, left, right, null);
	}

	public static BinaryPredicate mBinaryPredicate(int tag, Predicate left,
			Predicate right) {
		return ff.makeBinaryPredicate(tag, left, right, null);
	}

	public static BinaryPredicate mBinaryPredicate(Predicate left,
			Predicate right) {
		return mBinaryPredicate(LIMP, left, right);
	}

	public static BoolExpression mBoolExpression(Predicate pred) {
		return ff.makeBoolExpression(pred, null);
	}

	public static BoundIdentDecl mBoundIdentDecl(String name) {
		return mBoundIdentDecl(name, null);
	}

	public static BoundIdentDecl mBoundIdentDecl(String name, Type type) {
		final FormulaFactory fac = type != null ? type.getFactory() : ff;
		return fac.makeBoundIdentDecl(name, null, type);
	}

	public static BoundIdentifier mBoundIdentifier(int index) {
		return mBoundIdentifier(index, null);
	}

	public static BoundIdentifier mBoundIdentifier(int index, Type type) {
		final FormulaFactory fac = type != null ? type.getFactory() : ff;
		return fac.makeBoundIdentifier(index, null, type);
	}

	public static FreeIdentifier mFreeIdentifier(String name) {
		return mFreeIdentifier(name, null);
	}

	public static FreeIdentifier mFreeIdentifier(String name, Type type) {
		final FormulaFactory fac = type != null ? type.getFactory() : ff;
		return fac.makeFreeIdentifier(name, null, type);
	}

	public static FreeIdentifier mFreeIdentifier(String name, Type type,
			FormulaFactory fac) {
		return fac.makeFreeIdentifier(name, null, type);
	}

	public static IntegerLiteral mIntegerLiteral() {
		return ff.makeIntegerLiteral(BigInteger.ZERO, null);
	}

	public static IntegerLiteral mIntegerLiteral(long value) {
		return ff.makeIntegerLiteral(BigInteger.valueOf(value), null);
	}

	public static IntegerLiteral mIntegerLiteral(long value, FormulaFactory fac) {
		return fac.makeIntegerLiteral(BigInteger.valueOf(value), null);
	}

	public static <T> T[] mList(T... objs) {
		return objs;
	}

	public static LiteralPredicate mLiteralPredicate(FormulaFactory fac, int tag) {
		return fac.makeLiteralPredicate(tag, null);
	}

	public static LiteralPredicate mLiteralPredicate(int tag) {
		return mLiteralPredicate(ff, tag);
	}

	public static LiteralPredicate mLiteralPredicate(FormulaFactory fac) {
		return mLiteralPredicate(fac, BTRUE);
	}

	public static LiteralPredicate mLiteralPredicate() {
		return mLiteralPredicate(BTRUE);
	}

	// Builds left-associative maplet chains
	public static BinaryExpression mMaplet(Expression left, Expression right,
			Expression... others) {
		BinaryExpression maplet;
		maplet = mBinaryExpression(MAPSTO, left, right);
		for (final Expression other : others) {
			maplet = mBinaryExpression(MAPSTO, maplet, other);
		}
		return maplet;
	}

	public static QuantifiedExpression mQuantifiedExpression(
			BoundIdentDecl[] boundIdents, Predicate pred, Expression expr) {
		return mQuantifiedExpression(QUNION, Explicit, boundIdents, pred, expr);
	}

	public static QuantifiedExpression mQuantifiedExpression(int tag,
			QuantifiedExpression.Form form, BoundIdentDecl[] boundIdents,
			Predicate pred, Expression expr) {
		return ff.makeQuantifiedExpression(tag, boundIdents, pred, expr, null,
				form);
	}

	public static QuantifiedPredicate mQuantifiedPredicate(
			BoundIdentDecl[] boundIdents, Predicate pred) {
		return mQuantifiedPredicate(FORALL, boundIdents, pred);
	}

	public static QuantifiedPredicate mQuantifiedPredicate(int tag,
			BoundIdentDecl[] boundIdents, Predicate pred) {
		return ff.makeQuantifiedPredicate(tag, boundIdents, pred, null);
	}

	public static RelationalPredicate mRelationalPredicate(Expression left,
			Expression right) {
		return mRelationalPredicate(EQUAL, left, right);
	}

	public static RelationalPredicate mRelationalPredicate(int tag,
			Expression left, Expression right) {
		return ff.makeRelationalPredicate(tag, left, right, null);
	}

	public static SetExtension mSetExtension(Expression... members) {
		return ff.makeSetExtension(members, null);
	}

	public static SimplePredicate mSimplePredicate(Expression expr) {
		return ff.makeSimplePredicate(KFINITE, expr, null);
	}

	public static ITypeEnvironmentBuilder mTypeEnvironment() {
		return ff.makeTypeEnvironment();
	}

	public static IInferredTypeEnvironment mInferredTypeEnvironment(
			ITypeEnvironment initialTypEnv) {
		InferredTypeEnvironment inferredTypeEnv = new org.eventb.internal.core.typecheck.InferredTypeEnvironment(
				initialTypEnv);
		return inferredTypeEnv;
	}

	private static final Pattern typenvPairSeparator = Pattern.compile(";");
	private static final Pattern typenvPairPattern = Pattern
			.compile("^([^=]*)=([^=]*)$");
	
	
	public static ITypeEnvironmentBuilder addToTypeEnvironment(
			ITypeEnvironmentBuilder typeEnv, String typeEnvImage){
		if (typeEnvImage.length() == 0) {
			return typeEnv;
		}
		FormulaFactory factory = typeEnv.getFormulaFactory();
		for (final String pairImage : typenvPairSeparator.split(typeEnvImage)) {
			final Matcher m = typenvPairPattern.matcher(pairImage);
			if (!m.matches()) {
				throw new IllegalArgumentException(
						"Invalid type environment pair: " + pairImage);
			}
			final Expression expr = parseExpression(m.group(1), factory);
			if (expr.getTag() != FREE_IDENT) {
				throw new IllegalArgumentException(
						"Invalid type environment pair: " + pairImage);
			}
			final Type type = parseType(m.group(2), factory);
			typeEnv.addName(expr.toString(), type);
		}
		return typeEnv;
	}
	
	/**
	 * Generates the type environment specified by the given string. The string
	 * contains pairs of form <code>ident=type</code> separated by semicolons.
	 * <p>
	 * Example of valid parameters are:
	 * <ul>
	 * <li><code>""</code></li>
	 * <li><code>"x=S"</code></li>
	 * <li><code>"x=S; y=T"</code></li>
	 * <li><code>"x=S; r=S↔S"</code></li>
	 * </ul>
	 * </p>
	 * 
	 * @param typeEnvImage
	 *            image of the type environment to generate
	 * @param factory
	 *            the formula factory to use for building the result
	 * @return the type environment described by the given string
	 */
	public static ITypeEnvironmentBuilder mTypeEnvironment(String typeEnvImage,
			FormulaFactory factory) {
		final ITypeEnvironmentBuilder result = factory.makeTypeEnvironment();
		addToTypeEnvironment(result, typeEnvImage);
		return result;
	}

	public static ITypeEnvironmentBuilder mTypeEnvironment(String[] names, Type[] types) {
		assert names.length == types.length;
		ITypeEnvironmentBuilder result = ff.makeTypeEnvironment();
		for (int i = 0; i < names.length; i++) {
			result.addName(names[i], types[i]);
		}
		return result;
	}

	public static IInferredTypeEnvironment mInferredTypeEnvironment(
			ITypeEnvironment initialTypEnv, Object... objs) {
		assert (objs.length & 1) == 0;
		IInferredTypeEnvironment result = mInferredTypeEnvironment(initialTypEnv);
		for (int i = 0; i < objs.length; i += 2) {
			result.addName((String) objs[i], (Type) objs[i + 1]);
		}
		return result;
	}

	public static ISpecialization mTypeSpecialization(ITypeEnvironment te,
			String typeSpecialization) {
		final SpecializationBuilder builder = new SpecializationBuilder(te);
		builder.addTypeSpecializations(typeSpecialization);
		return builder.getResult();
	}

	public static ISpecialization mSpecialization(ITypeEnvironment te,
			String specialization) {
		final SpecializationBuilder builder = new SpecializationBuilder(te);
		builder.addSpecialization(specialization);
		return builder.getResult();
	}

	public static UnaryExpression mUnaryExpression(Expression child) {
		return mUnaryExpression(POW, child);
	}

	public static UnaryExpression mUnaryExpression(int tag, Expression child) {
		return ff.makeUnaryExpression(tag, child, null);
	}

	public static UnaryPredicate mUnaryPredicate(int tag, Predicate child) {
		return ff.makeUnaryPredicate(tag, child, null);
	}

	public static UnaryPredicate mUnaryPredicate(Predicate child) {
		return mUnaryPredicate(NOT, child);
	}

	public static MultiplePredicate mMultiplePredicate(int tag,
			Expression... expressions) {
		return ff.makeMultiplePredicate(tag, expressions, null);
	}

	public static MultiplePredicate mMultiplePredicate(
			Expression... expressions) {
		return mMultiplePredicate(KPARTITION, expressions);
	}

	public static PredicateVariable mPredicateVariable(String name) {
		return ff.makePredicateVariable(name, null);
	}

	public static ExtendedPredicate mExtendedPredicate(Expression e) {
		return ff_extns.makeExtendedPredicate(EXT_PRIME, Collections.singleton(e),
				Collections.<Predicate> emptySet(), null);
	}

	public static ExtendedExpression mExtendedExpression(
			Expression... expressions) {
		return ff_extns
				.makeExtendedExpression(MONEY, expressions, NO_PREDICATE, null);
	}

	public static ExtendedExpression mListCons(Expression... expressions) {
		final Type eType;
		if (expressions.length == 0) {
			eType = null;
		} else {
			eType = expressions[0].getType();
		}
		return mListCons(eType, expressions);
	}

	public static ExtendedExpression mListCons(Type eType,
			Expression... expressions) {
		final Type listType;
		if (eType == null) {
			listType = null;
		} else {
			listType = ff_extns.makeParametricType(singletonList(eType),
					LIST_DT.getTypeConstructor());
		}
		ExtendedExpression result = ff_extns.makeExtendedExpression(
				LIST_DT.getConstructor("NIL"), NO_EXPRESSION, NO_PREDICATE,
				null, listType);
		for (int i = expressions.length - 1; i >= 0; i--) {
			final Expression[] exprs = new Expression[] { expressions[i],
					result };
			result = ff_extns.makeExtendedExpression(LIST_DT.getConstructor("CONS"),
					exprs, NO_PREDICATE, null, listType);
		}
		return result;
	}
}
