/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
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
import static org.eventb.core.ast.FormulaFactory.makePosition;
import static org.eventb.core.ast.tests.FastFactory.mAssociativeExpression;
import static org.eventb.core.ast.tests.FastFactory.mAssociativePredicate;
import static org.eventb.core.ast.tests.FastFactory.mAtomicExpression;
import static org.eventb.core.ast.tests.FastFactory.mBinaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mBinaryPredicate;
import static org.eventb.core.ast.tests.FastFactory.mBoolExpression;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentDecl;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mIntegerLiteral;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mLiteralPredicate;
import static org.eventb.core.ast.tests.FastFactory.mMultiplePredicate;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedExpression;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedPredicate;
import static org.eventb.core.ast.tests.FastFactory.mRelationalPredicate;
import static org.eventb.core.ast.tests.FastFactory.mSimplePredicate;
import static org.eventb.core.ast.tests.FastFactory.mUnaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mUnaryPredicate;
import static org.eventb.internal.core.parser.BMath.StandardGroup.ATOMIC_PRED;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultInspector;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IFormulaInspector;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.IntegerType;
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
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;

/**
 * Ensures that finding accumulators work as expected for addition of new
 * findings. Also ensures that skipping nodes during inspections is properly
 * implemented. The other aspects of formula inspection are actually tested in
 * class {@link TestSubFormulas}.
 */
public class TestFormulaInspector extends TestCase {

	private static final IFormulaInspector<String> inspector = new DefaultInspector<String>() {

		@Override
		public void inspect(BoundIdentifier identifier,
				IAccumulator<String> accumulator) {
			accumulator.add(asList("a", "b", "c"));
		}

		@Override
		public void inspect(IntegerLiteral literal,
				IAccumulator<String> accumulator) {
			accumulator.add(new String[] { "1", "2" });
		}

		@Override
		public void inspect(LiteralPredicate predicate,
				IAccumulator<String> accumulator) {
			accumulator.add("simple");
		}

	};

	private static void assertFindings(Formula<?> formula, String... expected) {
		final List<String> actual = formula.inspect(inspector);
		assertEquals(asList(expected), actual);
	}

	/**
	 * Ensures that the add method for one finding works.
	 */
	public void testSimpleAdd() throws Exception {
		assertFindings(mLiteralPredicate(), "simple");
	}

	/**
	 * Ensures that the add method for an array of findings works.
	 */
	public void testArrayAdd() throws Exception {
		assertFindings(mIntegerLiteral(), "1", "2");
	}

	/**
	 * Ensures that the add method for a list of findings works.
	 */
	public void testListAdd() throws Exception {
		assertFindings(mBoundIdentifier(0), "a", "b", "c");
	}

	private static class Skipper implements IFormulaInspector<IPosition> {

		private final IPosition skipChildrenPos;
		private final IPosition skipAllPos;

		public Skipper(IPosition skipChildrenPos, IPosition skipAllPos) {
			this.skipChildrenPos = skipChildrenPos;
			this.skipAllPos = skipAllPos;
		}

		// Common inspection method
		private void doInspect(IAccumulator<IPosition> accumulator) {
			final IPosition pos = accumulator.getCurrentPosition();
			accumulator.add(pos);
			if (pos.equals(skipChildrenPos)) {
				accumulator.skipChildren();
			}
			if (pos.equals(skipAllPos)) {
				accumulator.skipAll();
			}
		}

		@Override
		public void inspect(AssociativeExpression expression,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(AssociativePredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(AtomicExpression expression,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(BinaryExpression expression,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(BinaryPredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(BoolExpression expression,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(BoundIdentDecl decl,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(BoundIdentifier identifier,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(ExtendedExpression expression,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(ExtendedPredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(FreeIdentifier identifier,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(IntegerLiteral literal,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(LiteralPredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(MultiplePredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(PredicateVariable predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(QuantifiedExpression expression,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(QuantifiedPredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(RelationalPredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(SetExtension expression,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(SimplePredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(UnaryExpression expression,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

		@Override
		public void inspect(UnaryPredicate predicate,
				IAccumulator<IPosition> accumulator) {
			doInspect(accumulator);
		}

	}

	private static class Trace {
		private final List<IPosition> positions;

		public Trace(List<IPosition> positions) {
			this.positions = new LinkedList<IPosition>(positions);
		}

		public void removeChildren(IPosition pos) {
			if (pos.isRoot()) {
				positions.clear();
				positions.add(pos);
				return;
			}
			final IPosition nextSibling = pos.getNextSibling();
			final Iterator<IPosition> iter = positions.iterator();
			while (iter.hasNext()) {
				final IPosition cur = iter.next();
				if (pos.compareTo(cur) < 0 && cur.compareTo(nextSibling) < 0) {
					iter.remove();
				}
			}
		}

		public void removeAfter(IPosition pos) {
			final Iterator<IPosition> iter = positions.iterator();
			while (iter.hasNext()) {
				final IPosition cur = iter.next();
				if (pos.compareTo(cur) < 0) {
					iter.remove();
				}
			}
		}

		public void assertEquals(List<IPosition> actual) {
			Assert.assertEquals(positions, actual);
		}
	}

	private static void assertTrace(Formula<?> formula, String posImage) {
		final Skipper noSkip = new Skipper(null, null);
		final List<IPosition> fullTrace = formula.inspect(noSkip);
		final IPosition pos = makePosition(posImage);

		assertTrace(formula, fullTrace, pos, null);
		assertTrace(formula, fullTrace, null, pos);
		assertTrace(formula, fullTrace, pos, pos);
	}

	private static void assertTrace(Formula<?> formula,
			List<IPosition> fullTrace, IPosition skipChildrenPos,
			IPosition skipAllPos) {
		final Trace expected = new Trace(fullTrace);
		if (skipChildrenPos != null) {
			expected.removeChildren(skipChildrenPos);
		}
		if (skipAllPos != null) {
			expected.removeAfter(skipAllPos);
		}
		final Skipper skipper = new Skipper(skipChildrenPos, skipAllPos);
		final List<IPosition> actual = formula.inspect(skipper);
		expected.assertEquals(actual);
	}

	private static Expression eA = mAtomicExpression();
	private static Expression eB = mAtomicExpression();
	private static Expression eC = mAtomicExpression();
	private static Expression eD = mAtomicExpression();

	private static Predicate pA = mLiteralPredicate();
	private static Predicate pB = mLiteralPredicate();
	private static Predicate pC = mLiteralPredicate();
	private static Predicate pD = mLiteralPredicate();

	private static BoundIdentDecl b_x = mBoundIdentDecl("x");
	public static final FormulaFactory ff = FormulaFactory.getDefault();
	protected static final IntegerLiteral ONE = ff.makeIntegerLiteral(BigInteger.ONE, null);

	private static final IPredicateExtension EXT_PRIME = new IPredicateExtension() {
		private static final String SYMBOL = "prime";
		private static final String ID = "Ext Prime";
		
		@Override
		public Predicate getWDPredicate(IExtendedFormula formula,
				IWDMediator wdMediator) {
			return wdMediator.makeTrueWD();
		}
		
		@Override
		public String getSyntaxSymbol() {
			return SYMBOL;
		}
		
		@Override
		public IExtensionKind getKind() {
			return PARENTHESIZED_UNARY_PREDICATE;
		}
		
		@Override
		public String getId() {
			return ID;
		}
		
		@Override
		public String getGroupId() {
			return ATOMIC_PRED.getId();
		}
		
		@Override
		public void addPriorities(IPriorityMediator mediator) {
			// no priority
		}
		
		@Override
		public void addCompatibilities(ICompatibilityMediator mediator) {
			// no compatibility			
		}
		
		@Override
		public void typeCheck(ExtendedPredicate predicate,
				ITypeCheckMediator tcMediator) {
			final Expression child = predicate.getChildExpressions()[0];
			final Type childType = tcMediator.makePowerSetType(tcMediator.makeIntegerType());
			tcMediator.sameType(child.getType(), childType);
		}

		@Override
		public boolean conjoinChildrenWD() {
			return true;
		}

		@Override
		public Object getOrigin() {
			return null;
		}
	};

	private static final FormulaFactory PRIME_FAC = FormulaFactory
			.getInstance(Collections.<IFormulaExtension> singleton(EXT_PRIME));
	
	private static class Emax implements IExpressionExtension {
		private static final String SYNTAX_SYMBOL = "emax";
		private static final String OPERATOR_ID = "org.eventb.core.ast.tests.extMax";
		private final boolean conjoinChildrenWD;

		public Emax(boolean conjoinChildrenWD) {
			this.conjoinChildrenWD = conjoinChildrenWD;
		}

		@Override
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			return childExprs[0].getType();
		}

		@Override
		public boolean verifyType(Type proposedType,
				Expression[] childExprs, Predicate[] childPreds) {
			for (Expression child : childExprs) {
				final Type childType = child.getType();
				if (!(childType instanceof IntegerType)) {
					return false;
				}
			}
			return true;
		}
		
		@Override
		public Type typeCheck(ExtendedExpression expression,
				ITypeCheckMediator tcMediator) {
			final Expression[] children = expression.getChildExpressions();
			final Type resultType = tcMediator.makeIntegerType();
			for (int i = 0; i < children.length; i++) {
				tcMediator.sameType(children[i].getType(), resultType);
			}
			return resultType;
		}

		@Override
		public void addCompatibilities(ICompatibilityMediator mediator) {
			mediator.addCompatibility(getId(), getId());
		}

		@Override
		public void addPriorities(IPriorityMediator mediator) {
			// no priority to add
		}

		@Override
		public String getGroupId() {
			return "Arithmetic";
		}

		@Override
		public String getId() {
			return OPERATOR_ID + (conjoinChildrenWD ? ".conj" : ".noConj");
		}

		@Override
		public IExtensionKind getKind() {
			return PARENTHESIZED_BINARY_EXPRESSION;
		}

		@Override
		public String getSyntaxSymbol() {
			return SYNTAX_SYMBOL;
		}

		// BTRUE if the first child is an integer literal
		// else BFALSE 
		@Override
		public Predicate getWDPredicate(IExtendedFormula formula,
				IWDMediator wdMediator) {
			final Expression firstChild = formula.getChildExpressions()[0];
			
			final FormulaFactory factory = wdMediator.getFormulaFactory();
			if (firstChild.getTag() == Formula.INTLIT) {
				return factory.makeLiteralPredicate(Formula.BTRUE, null);
			} else {
				return factory.makeLiteralPredicate(Formula.BFALSE, null);
			}
		}

		@Override
		public boolean conjoinChildrenWD() {
			return conjoinChildrenWD;
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
		}

		@Override
		public Object getOrigin() {
			return null;
		}

	}
	
	private static final IExpressionExtension EMAX = new Emax(true);

	private static IntegerType INTEGER = ff.makeIntegerType();
	private static final Set<Predicate> NO_PREDICATE = Collections.emptySet();
	private static final FreeIdentifier FRID_A = ff.makeFreeIdentifier("A", null, INTEGER);
	private static final FreeIdentifier FRID_B = ff.makeFreeIdentifier("B", null, INTEGER);
	final FormulaFactory extFac = FormulaFactory.getInstance(Collections
			.<IFormulaExtension> singleton(EMAX));

	/**
	 * Ensures that skip methods work as expected on associative expressions
	 */
	public void testAssociativeExpression() throws Exception {
		final Expression child = mAssociativeExpression(eB, eC);
		final Expression expression = mAssociativeExpression(eA, child, eD);
		assertTrace(expression, "1");
	}

	/**
	 * Ensures that skip methods work as expected on associative predicates.
	 */
	public void testAssociativePredicate() throws Exception {
		final Predicate child = mAssociativePredicate(pB, pC);
		final Predicate predicate = mAssociativePredicate(pA, child, pD);
		assertTrace(predicate, "1");
	}

	/**
	 * Ensures that skip methods work as expected on binary expressions
	 */
	public void testBinaryExpression() throws Exception {
		final Expression grandChild = mBinaryExpression(eB, eC);
		final Expression child = mBinaryExpression(eA, grandChild);
		final Expression expression = mBinaryExpression(child, eD);
		assertTrace(expression, "0.1");
	}

	/**
	 * Ensures that skip methods work as expected on binary predicates
	 */
	public void testBinaryPredicate() throws Exception {
		final Predicate grandChild = mBinaryPredicate(pB, pC);
		final Predicate child = mBinaryPredicate(pA, grandChild);
		final Predicate predicate = mBinaryPredicate(child, pD);
		assertTrace(predicate, "0.1");
	}

	/**
	 * Ensures that skip methods work as expected on relational predicates
	 */
	public void testRelationalPredicate() throws Exception {
		final Predicate predicate = mRelationalPredicate(eA, eB);
		assertTrace(predicate, "0");
	}

	/**
	 * Ensures that skip methods work as expected on extended predicates
	 */
	public void testExtendedPredicate() throws Exception {
		final Predicate predicate = PRIME_FAC.makeExtendedPredicate(EXT_PRIME,
				Arrays.<Expression> asList(ONE),
				Collections.<Predicate> emptySet(), null);
		assertTrace(predicate, "0");
	}

	/**
	 * Ensures that skip methods work as expected on extended expression
	 */
	public void testExtendedExpression() throws Exception {
		final Expression expression = extFac.makeExtendedExpression(EMAX, Arrays
				.<Expression> asList(
						FRID_A,
						FRID_B),
				NO_PREDICATE, null);
		assertTrace(expression, "0");
	}

	/**
	 * Ensures that skip methods work as expected on unary predicates
	 */
	public void testUnaryPredicate() throws Exception {
		final UnaryPredicate predicate = mUnaryPredicate(pA);
		assertTrace(predicate, "");
	}

	/**
	 * Ensures that skip methods work as expected on unary expressions
	 */
	public void testUnaryExpression() throws Exception {
		final Expression expression = mUnaryExpression(eA);
		assertTrace(expression, "");
	}

	/**
	 * Ensures that skip methods work as expected on quantified predicates
	 */
	public void testQuantifiedPredicate() throws Exception {
		final QuantifiedPredicate predicate = mQuantifiedPredicate(mList(b_x),
				pA);
		assertTrace(predicate, "");
	}

	/**
	 * Ensures that skip methods work as expected on quantified expressions
	 */
	public void testQuantifiedExpression() throws Exception {
		final Expression expression = mQuantifiedExpression(mList(b_x), pA, eA);
		assertTrace(expression, "");
	}

	/**
	 * Ensures that skip methods work as expected on simple predicates
	 */
	public void testSimplePredicate() throws Exception {
		final SimplePredicate predicate = mSimplePredicate(eA);
		assertTrace(predicate, "");
	}

	/**
	 * Ensures that skip methods work as expected on multiple predicates
	 */
	public void testMultiplePredicate() throws Exception {
		final MultiplePredicate predicate = mMultiplePredicate(eA, eB, eC);
		assertTrace(predicate, "1");
	}

	/**
	 * Ensures that skip methods work as expected on literal predicates
	 */
	public void testLiteralPredicate() throws Exception {
		assertTrace(pA, "");
	}

	/**
	 * Ensures that skip methods work as expected on bool expressions
	 */
	public void testBoolExpression() throws Exception {
		final Expression expression = mBoolExpression(pA);
		assertTrace(expression, "");
	}

}
