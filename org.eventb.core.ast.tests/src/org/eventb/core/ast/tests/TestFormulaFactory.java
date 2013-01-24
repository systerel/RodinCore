/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.FormulaFactory.getInstance;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.TestGenParser.MONEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit tests for factory methods which are not fully tested elsewhere.
 * 
 * @author Laurent Voisin
 */
public class TestFormulaFactory extends AbstractTests {

	private static final GivenType tS = ff.makeGivenType("S");
	private static final GivenType tT = ff.makeGivenType("T");

	private static final String BAD_NAME = "bad-name";

	private static final String PRED_VAR_NAME = PredicateVariable.LEADING_SYMBOL
			+ "P";

	/**
	 * Ensures that method isValidIdentifierName() takes into account the
	 * version of the mathematical language supported by the formula factory
	 * instance.
	 */
	@Test 
	public void testValidIdentifierName() throws Exception {
		final String validName = "foo";
		assertTrue(ffV1.isValidIdentifierName(validName));
		assertTrue(ff.isValidIdentifierName(validName));
		assertTrue(LIST_FAC.isValidIdentifierName(validName));

		final String nameInV1Only = "partition";
		assertTrue(ffV1.isValidIdentifierName(nameInV1Only));
		assertFalse(ff.isValidIdentifierName(nameInV1Only));
		assertFalse(LIST_FAC.isValidIdentifierName(nameInV1Only));

		final String typeConstructorName = "List";
		assertTrue(ffV1.isValidIdentifierName(typeConstructorName));
		assertTrue(ff.isValidIdentifierName(typeConstructorName));
		assertFalse(LIST_FAC.isValidIdentifierName(typeConstructorName));

		final String valueConstructorName = "cons";
		assertTrue(ffV1.isValidIdentifierName(valueConstructorName));
		assertTrue(ff.isValidIdentifierName(valueConstructorName));
		assertFalse(LIST_FAC.isValidIdentifierName(valueConstructorName));

		final String destructorName = "head";
		assertTrue(ffV1.isValidIdentifierName(destructorName));
		assertTrue(ff.isValidIdentifierName(destructorName));
		assertFalse(LIST_FAC.isValidIdentifierName(destructorName));
	}

	/*----------------------------------------------------------------
	 *  CONSTRUCTION OF TYPE OBJECTS
	 *----------------------------------------------------------------*/

	@Test(expected = IllegalArgumentException.class)
	public void givenType_InvalidIdentifierName() {
		ff.makeGivenType(BAD_NAME);
	}

	@Test(expected = IllegalArgumentException.class)
	public void parametricType_UnknownExtension() {
		ff.makeParametricType(mList(tS), new UnknownExtension());
	}

	@Test(expected = IllegalArgumentException.class)
	public void parametricType_InvalidExtension() {
		ff.makeParametricType(mList(tS), EXT_LIST);
	}

	@Test(expected = IllegalArgumentException.class)
	public void parametricType_NotATypeConstructor() {
		final FormulaFactory extFac = getInstance();
		extFac.makeParametricType(mList(tS), MONEY);
	}

	@Ignore("Known bug")
	@Test(expected = IllegalArgumentException.class)
	public void parametricType_WrongNumberOfParameter() {
		LIST_FAC.makeParametricType(mList(tS, tT), EXT_LIST);
	}

	@Test(expected = NullPointerException.class)
	public void parametricType_NullParameters() {
		final Type[] typeParams = null;
		LIST_FAC.makeParametricType(typeParams, EXT_LIST);
	}

	@Test(expected = NullPointerException.class)
	public void parametricType_NullInParameter() {
		LIST_FAC.makeParametricType(new Type[] { null }, EXT_LIST);
	}

	@Test
	public void parametricType_ArrayParameter() {
		final Type[] typeParams = { tS };
		assertArrayProtected(LIST_FAC.makeParametricType(typeParams, EXT_LIST),
				typeParams);
	}

	/**
	 * Common implementation for testing methods that should detect a violation
	 * of an assertion. The issue with such tests is that
	 * <code>AssertionError</code> is also used internally by JUnit, so we
	 * cannot use the usual test pattern for tests that raise an assertion: the
	 * <code>fail()</code> method raises itself <code>AssertionError</code>,
	 * which makes the test always succeed.
	 * <p>
	 * To use this code, just instantiate this class, provide code for method
	 * {@link #test()} and call the {@link #run()} method.
	 * </p>
	 */
	abstract static class FailedAssertionChecker {

		private boolean failed = true;

		public void run() {
			try {
				test();
			} catch (AssertionError e) {
				failed = false;
			}
			if (failed) {
				fail("Test should have violated an assertion.");
			}
		}

		/**
		 * Put the code that should violate an assertion here.
		 */
		protected abstract void test() throws AssertionError;

	}

	/**
	 * Ensures that the name of a free identifier is checked for validity when
	 * the identifier is built without a type.
	 */
	@Test 
	public void testFreeIdentifierUntyped() {
		new FailedAssertionChecker() {
			@Override
			protected void test() throws AssertionError {
				ff.makeFreeIdentifier(BAD_NAME, null);
			}
		}.run();
	}

	/**
	 * Ensures that the name of a free identifier cannot look like a predicate
	 * variable.
	 */
	@Test 
	public void testFreeIdentifierPredicateVariable() {
		new FailedAssertionChecker() {
			@Override
			protected void test() throws AssertionError {
				ff.makeFreeIdentifier(PRED_VAR_NAME, null);
			}
		}.run();
	}

	/**
	 * Ensures that the name of a free identifier is checked for validity when
	 * the identifier is built with a type.
	 */
	@Test 
	public void testFreeIdentifierTyped() {
		new FailedAssertionChecker() {
			@Override
			protected void test() throws AssertionError {
				ff.makeFreeIdentifier(BAD_NAME, null, INT_TYPE);
			}
		}.run();
	}

	/**
	 * Ensures that the name of a bound identifier declaration is checked for
	 * validity when the declaration is built without a type.
	 */
	@Test 
	public void testBoundIdentDeclUntyped() {
		new FailedAssertionChecker() {
			@Override
			protected void test() throws AssertionError {
				ff.makeBoundIdentDecl(BAD_NAME, null);
			}
		}.run();
	}

	/**
	 * Ensures that the name of a bound identifier declaration cannot look like
	 * a predicate variable.
	 */
	@Test 
	public void testBoundIdentDeclPredicateVariable() {
		new FailedAssertionChecker() {
			@Override
			protected void test() throws AssertionError {
				ff.makeBoundIdentDecl(PRED_VAR_NAME, null);
			}
		}.run();
	}

	/**
	 * Ensures that the name of a bound identifier declaration is checked for
	 * validity when the declaration is built with a type.
	 */
	@Test 
	public void testBoundIdentDeclTyped() {
		new FailedAssertionChecker() {
			@Override
			protected void test() throws AssertionError {
				ff.makeBoundIdentDecl(BAD_NAME, null, INT_TYPE);
			}
		}.run();
	}

	/**
	 * Ensures that the name of a predicate variable is checked for validity
	 * when the variable is built.
	 */
	@Test 
	public void testPredicateVariable() {
		new FailedAssertionChecker() {
			@Override
			protected void test() throws AssertionError {
				final String name = PredicateVariable.LEADING_SYMBOL + BAD_NAME;
				ff.makePredicateVariable(name, null);
			}
		}.run();
	}

	/**
	 * Ensures that the name of a predicate variable is checked for the leading
	 * character when the variable is built.
	 */
	@Test 
	public void testPredicateVariableLeader() {
		new FailedAssertionChecker() {
			@Override
			protected void test() throws AssertionError {
				ff.makePredicateVariable("P", null);
			}
		}.run();
	}

	/**
	 * Verifies that object construction is protected against array mutation.
	 * The test consists in verifying that the behavior of
	 * <code>toString()</code> is not impacted by changing the array.
	 * 
	 * @param obj
	 *            an object which was constructed with the given array
	 * @param array
	 *            a non-empty array
	 */
	private static final void assertArrayProtected(Object obj, Object[] array) {
		final String expected = obj.toString();
		final Object save = array[0];
		array[0] = null;
		final String actual = obj.toString();
		array[0] = save;
		assertEquals(expected, actual);
	}

	/**
	 * Instances of this class must never be used to construct a formula
	 * factory.
	 */
	private static final class UnknownExtension implements
			IExpressionExtension, IPredicateExtension {

		public UnknownExtension() {
			// Do nothing
		}

		@Override
		public String getSyntaxSymbol() {
			throw new AssertionError("Must never be called");
		}

		@Override
		public Predicate getWDPredicate(IExtendedFormula formula,
				IWDMediator wdMediator) {
			throw new AssertionError("Must never be called");
		}

		@Override
		public boolean conjoinChildrenWD() {
			throw new AssertionError("Must never be called");
		}

		@Override
		public String getId() {
			return "Unknown id";
		}

		@Override
		public String getGroupId() {
			throw new AssertionError("Must never be called");
		}

		@Override
		public IExtensionKind getKind() {
			throw new AssertionError("Must never be called");
		}

		@Override
		public Object getOrigin() {
			throw new AssertionError("Must never be called");
		}

		@Override
		public void addCompatibilities(ICompatibilityMediator mediator) {
			throw new AssertionError("Must never be called");
		}

		@Override
		public void addPriorities(IPriorityMediator mediator) {
			throw new AssertionError("Must never be called");
		}

		@Override
		public void typeCheck(ExtendedPredicate predicate,
				ITypeCheckMediator tcMediator) {
			throw new AssertionError("Must never be called");
		}

		@Override
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			throw new AssertionError("Must never be called");
		}

		@Override
		public boolean verifyType(Type proposedType, Expression[] childExprs,
				Predicate[] childPreds) {
			throw new AssertionError("Must never be called");
		}

		@Override
		public Type typeCheck(ExtendedExpression expression,
				ITypeCheckMediator tcMediator) {
			throw new AssertionError("Must never be called");
		}

		@Override
		public boolean isATypeConstructor() {
			throw new AssertionError("Must never be called");
		}

	}

}
