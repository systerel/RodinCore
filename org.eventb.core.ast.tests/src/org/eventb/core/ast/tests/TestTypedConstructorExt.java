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
package org.eventb.core.ast.tests;

import static java.util.Arrays.asList;
import static org.eventb.core.ast.Formula.RELIMAGE;
import static org.eventb.core.ast.tests.extension.Extensions.EITHER_DT;
import static org.eventb.core.ast.tests.extension.Extensions.EITHER_FAC;
import static org.eventb.core.ast.tests.extension.Extensions.EXTS_FAC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.function.Supplier;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.ITypeAnnotation;
import org.eventb.core.ast.tests.extension.Extensions.FSet;
import org.eventb.core.ast.tests.extension.Extensions.Real;
import org.eventb.core.ast.tests.extension.Extensions.Return;
import org.eventb.core.ast.tests.extension.Extensions.Union2;
import org.eventb.internal.core.ast.extension.TypeAnnotation;
import org.junit.Test;

/**
 * Unit tests for type synthesis happening at node construction for extended
 * expressions.
 * <p>
 * The purpose is to test the behavior of the
 * {@link FormulaFactory#makeExtendedExpression} methods in various contexts:
 * <ul>
 * <li>without any type information,</li>
 * <li>with a type,</li>
 * <li>with a type annotation.</li>
 * </ul>
 * <p>
 * We also check how the expression type-checker behaves afterward.
 * <p>
 * Implementation note: It is important to use suppliers for arguments, because
 * the type-check may modify them as a side-effect (i.e., set their type).
 *
 * @author Laurent Voisin
 */
public class TestTypedConstructorExt extends AbstractTests {

	static final FormulaFactory eff = EXTS_FAC.withExtensions(EITHER_FAC.getExtensions());

	private static final Type tBOOL = eff.makeBooleanType();
	private static final Type tS = eff.makeGivenType("S");
	private static final Type tT = eff.makeGivenType("T");
	private static final Type tPowT = eff.makePowerSetType(tT);

	/**
	 * An extension can be created without a type (child is not typed).
	 */
	@Test
	public void singleChildNoType() {
		Supplier<Expression> id = () -> eff.makeFreeIdentifier("x", null);
		Type type = eff.makePowerSetType(eff.makeParametricType(FSet.EXT, tS));

		make(FSet.EXT, id).hasType(null);
		make(FSet.EXT, id).typeChecksTo(null);
		make(FSet.EXT, id).typeChecksWith(type);

		make(FSet.EXT, id).withType(type).raises("Invalid type ℙ(FIN(S)) for expression FIN(x)");

		make(FSet.EXT, id).withTypeAnnot(type).hasType(null);
		make(FSet.EXT, id).withTypeAnnot(type).typeChecksTo(type);
		make(FSet.EXT, id).withTypeAnnot(type).typeChecksWith(type);
	}

	/**
	 * An extension can be created without a type (second child is not typed).
	 */
	@Test
	public void secondChildNoType() {
		Type type = eff.makePowerSetType(tBOOL);
		Supplier<Expression> id1 = () -> eff.makeFreeIdentifier("x", null, type);
		Supplier<Expression> id2 = () -> eff.makeFreeIdentifier("y", null);

		make(Union2.EXT, id1, id2).hasType(null);
		make(Union2.EXT, id1, id2).typeChecksTo(type);
		make(Union2.EXT, id1, id2).typeChecksWith(type);

		make(Union2.EXT, id1, id2).withType(type).raises("Invalid type ℙ(BOOL) for expression union2(x,y)");

		make(Union2.EXT, id1, id2).withTypeAnnot(type).hasType(null);
		make(Union2.EXT, id1, id2).withTypeAnnot(type).typeChecksTo(type);
		make(Union2.EXT, id1, id2).withTypeAnnot(type).typeChecksWith(type);
	}

	/**
	 * An extension can be created without a type (conflicting child types).
	 */
	@Test
	public void typeConflict() {
		Type t1 = eff.makePowerSetType(tBOOL);
		Type t2 = eff.makePowerSetType(eff.makeIntegerType());
		Supplier<Expression> id1 = () -> eff.makeFreeIdentifier("x", null, t1);
		Supplier<Expression> id2 = () -> eff.makeFreeIdentifier("y", null, t2);

		make(Union2.EXT, id1, id2).hasType(null);
		make(Union2.EXT, id1, id2).typeChecksTo(null);
		make(Union2.EXT, id1, id2).typeCheckFailsWith(t1);
		make(Union2.EXT, id1, id2).typeCheckFailsWith(t2);

		make(Union2.EXT, id1, id2).withType(t1).raises("Invalid type ℙ(BOOL) for expression union2(x,y)");
		make(Union2.EXT, id1, id2).withType(t2).raises("Invalid type ℙ(ℤ) for expression union2(x,y)");

		make(Union2.EXT, id1, id2).withTypeAnnot(t1).hasType(null);
		make(Union2.EXT, id1, id2).withTypeAnnot(t1).typeChecksTo(null);
		make(Union2.EXT, id1, id2).withTypeAnnot(t1).typeCheckFailsWith(t1);

		make(Union2.EXT, id1, id2).withTypeAnnot(t2).hasType(null);
		make(Union2.EXT, id1, id2).withTypeAnnot(t2).typeChecksTo(null);
		make(Union2.EXT, id1, id2).withTypeAnnot(t1).typeCheckFailsWith(t2);
	}

	/**
	 * An extension can be created without a type (conflicting child type
	 * environment). Identifier "x" is of type "ℙ(S)" in the first child, and type
	 * "ℙ(T)" in the second child.
	 */
	@Test
	public void typenvConflict() {
		Supplier<Expression> expr1 = () -> makeSetRel(eff, "r", "x");
		Supplier<Expression> expr2 = () -> eff.makeFreeIdentifier("x", null, tPowT);
		assertEquals(tPowT, expr1.get().getType());
		assertEquals(tPowT, expr2.get().getType());

		make(Union2.EXT, expr1, expr2).hasType(null);
		make(Union2.EXT, expr1, expr2).typeChecksTo(null);
		make(Union2.EXT, expr1, expr2).typeCheckFailsWith(tPowT);

		make(Union2.EXT, expr1, expr2).withType(tPowT).raises("Invalid type ℙ(T) for expression union2(r[{x}],x)");

		make(Union2.EXT, expr1, expr2).withTypeAnnot(tPowT).hasType(null);
		make(Union2.EXT, expr1, expr2).withTypeAnnot(tPowT).typeChecksTo(null);
		make(Union2.EXT, expr1, expr2).withTypeAnnot(tPowT).typeCheckFailsWith(tPowT);
	}

	/**
	 * An extension can be created without a type (conflicting child type
	 * environment). Both children have the same type, but the second child
	 * conflicts with a given set buried in the first child.
	 */
	@Test
	public void givenSetConflict() {
		Supplier<Expression> expr1 = () -> makeSetRel(eff, "r", "x");
		Supplier<Expression> expr2 = () -> eff.makeFreeIdentifier("S", null, tPowT);
		assertEquals(tPowT, expr1.get().getType());
		assertEquals(tPowT, expr2.get().getType());

		make(Union2.EXT, expr1, expr2).hasType(null);
		make(Union2.EXT, expr1, expr2).typeChecksTo(null);
		make(Union2.EXT, expr1, expr2).typeCheckFailsWith(tPowT);

		make(Union2.EXT, expr1, expr2).withType(tPowT).raises("Invalid type ℙ(T) for expression union2(r[{x}],S)");

		make(Union2.EXT, expr1, expr2).withTypeAnnot(tPowT).hasType(null);
		make(Union2.EXT, expr1, expr2).withTypeAnnot(tPowT).typeChecksTo(null);
		make(Union2.EXT, expr1, expr2).withTypeAnnot(tPowT).typeCheckFailsWith(tPowT);
	}

	/**
	 * An atomic extension can synthesize a valid type.
	 */
	@Test
	public void autoType() {
		Type type = eff.makePowerSetType(eff.makeParametricType(Real.EXT));

		make(Real.EXT).hasType(type);
		make(Real.EXT).typeChecksTo(type);
		make(Real.EXT).typeChecksWith(type);

		make(Real.EXT).withType(type).hasType(type);
		make(Real.EXT).withType(type).typeChecksTo(type);
		make(Real.EXT).withType(type).typeChecksWith(type);

		make(Real.EXT).withTypeAnnot(type).hasType(type);
		make(Real.EXT).withTypeAnnot(type).typeChecksTo(type);
		make(Real.EXT).withTypeAnnot(type).typeChecksWith(type);
	}

	/**
	 * An extension can be typed from the outside.
	 */
	@Test
	public void noAutoType() {
		Supplier<Expression> id = () -> eff.makeFreeIdentifier("x", null, tT);
		Type type = eff.makeParametricType(EITHER_DT.getTypeConstructor(), asList(tS, tT));

		make(Return.EXT, id).hasType(null);
		make(Return.EXT, id).typeChecksTo(null);
		make(Return.EXT, id).typeChecksWith(type);

		make(Return.EXT, id).withType(type).hasType(type);
		make(Return.EXT, id).withType(type).typeChecksTo(type);
		make(Return.EXT, id).withType(type).typeChecksWith(type);

		make(Return.EXT, id).withTypeAnnot(type).hasType(type);
		make(Return.EXT, id).withTypeAnnot(type).typeChecksTo(type);
		make(Return.EXT, id).withTypeAnnot(type).typeChecksWith(type);
	}

	/**
	 * An extension can be typed from the outside, but not type-checked because of a
	 * name conflict with the type.
	 */
	@Test
	public void autoTypeConflict() {
		Supplier<Expression> id = () -> eff.makeFreeIdentifier("S", null, tT);
		Type type = eff.makeParametricType(EITHER_DT.getTypeConstructor(), asList(tS, tT));

		make(Return.EXT, id).hasType(null);
		make(Return.EXT, id).typeChecksTo(null);
		make(Return.EXT, id).typeCheckFailsWith(type);

		make(Return.EXT, id).withType(type).raises("Invalid type either(S,T) for expression return(S)");

		make(Return.EXT, id).withTypeAnnot(type).hasType(null);
		make(Return.EXT, id).withTypeAnnot(type).typeChecksTo(null);
		make(Return.EXT, id).withTypeAnnot(type).typeCheckFailsWith(type);
	}

	/**
	 * Returns the formula "rel[{value}]" where "rel" has the type "S ↔ T".
	 */
	private static Expression makeSetRel(FormulaFactory fac, String relName, String valueName) {
		Type trel = fac.makeRelationalType(tS, tT);
		Expression rel = fac.makeFreeIdentifier(relName, null, trel);
		Expression value = fac.makeFreeIdentifier(valueName, null, tS);
		Expression single = fac.makeSetExtension(value, null);
		return fac.makeBinaryExpression(RELIMAGE, rel, single, null);
	}

	@SafeVarargs
	public static ExtendedExpressionChecker make(IExpressionExtension extension,
			Supplier<Expression>... childrenBuilder) {
		return new ExtendedExpressionChecker(extension, childrenBuilder);
	}

	private static class ExtendedExpressionChecker {

		private final IExpressionExtension extension;
		private final Supplier<Expression>[] childrenBuilder;

		private Type type;
		private ITypeAnnotation typeAnnotation;

		public ExtendedExpressionChecker(IExpressionExtension extension, Supplier<Expression>[] childrenBuilder) {
			this.extension = extension;
			this.childrenBuilder = childrenBuilder;
		}

		public ExtendedExpressionChecker withType(Type givenType) {
			this.type = givenType;
			return this;
		}

		public ExtendedExpressionChecker withTypeAnnot(Type givenType) {
			this.typeAnnotation = new TypeAnnotation(givenType);
			return this;
		}

		public void hasType(Type expected) {
			Expression expr = make();
			if (expected == null) {
				assertFalse(expr.isTypeChecked());
				assertNull(expr.getType());
			} else {
				assertTrue(expr.isTypeChecked());
				assertEquals(expected, expr.getType());
			}
			IdentsChecker.check(expr);
		}

		public void raises(String message) {
			try {
				make();
			} catch (IllegalArgumentException exc) {
				assertEquals(message, exc.getMessage());
				return;
			}
			fail("Should have raised an IllegalArgumentException");
		}

		public void typeChecksTo(Type expected) {
			typeCheck(null, expected);
		}

		public void typeChecksWith(Type expected) {
			typeCheck(expected, expected);
		}

		public void typeCheckFailsWith(Type givenType) {
			typeCheck(givenType, null);
		}

		private Expression make() {
			Expression[] children = Arrays.stream(this.childrenBuilder).map(Supplier::get).toArray(Expression[]::new);
			if (type != null) {
				return eff.makeExtendedExpression(extension, children, NO_PREDS, null, type);
			}
			if (typeAnnotation != null) {
				return eff.makeExtendedExpression(extension, children, NO_PREDS, null, typeAnnotation);
			}
			return eff.makeExtendedExpression(extension, children, NO_PREDS, null);
		}

		private void typeCheck(Type givenType, Type expected) {
			Expression expr = make();
			var typenv = eff.makeTypeEnvironment();
			var tcResult = givenType == null ? expr.typeCheck(typenv) : expr.typeCheck(typenv, givenType);
			if (expected == null) {
				assertFalse(tcResult.isSuccess());
				assertFalse(expr.isTypeChecked());
				assertNull(expr.getType());
			} else {
				if (!tcResult.isSuccess()) {
					fail("unexpected type-check error: " + tcResult.toString());
				}
				assertTrue(expr.isTypeChecked());
				assertEquals(expected, expr.getType());
			}
			IdentsChecker.check(expr);
		}
	}
}
