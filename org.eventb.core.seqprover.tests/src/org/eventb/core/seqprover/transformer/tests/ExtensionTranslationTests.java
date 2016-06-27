/*******************************************************************************
 * Copyright (c) 2014, 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.transformer.tests;

import static java.util.Collections.singletonList;
import static org.eventb.core.ast.tests.DatatypeParser.parse;
import static org.eventb.core.seqprover.tests.TestLib.mTypeEnvironment;
import static org.eventb.core.seqprover.transformer.SimpleSequents.translateExtensions;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.eventb.core.ast.DefaultSimpleVisitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.eventb.core.seqprover.transformer.SimpleSequents;
import org.junit.Assert;
import org.junit.Test;

/**
 * Acceptance tests for
 * {@link SimpleSequents#translateExtensions(ISimpleSequent)}.
 * 
 * @author Josselin Dolhen
 */
public class ExtensionTranslationTests extends AbstractTransformerTests {

	private static final String LIST_SPEC = //
			"List[S] ::= nil || cons[head: S; tail: List]";

	private static final IDatatype LIST_DT = parse(ff, LIST_SPEC);

	private static final String LIST_AXIOMS = //
			"List∈ℤ  List_Type ;;" //
					+ "cons∈ℤ × List_Type ↣ List_Type ;;"
					+ "head∈ran(cons) ↠ ℤ ;;" //
					+ "tail∈ran(cons) ↠ List_Type ;;" + "head ⊗ tail=cons∼ ;;"
					+ "partition(List_Type,{nil},ran(cons)) ;;"
					+ "∀S·partition(List[S],{nil},cons[S × List[S]])";

	@Test
	public void simpleSequent() {
		assertSequentTranslation("", //
				"a = empty |- 2 = 1", //
				LIST_AXIOMS + ";; a = empty |- 2 = 1");
	}

	@Test
	public void constructorInHyp() {
		assertSequentTranslation("", //
				"cons(1, nil) = b |- 2 = 1", //
				LIST_AXIOMS + ";; cons(1 ↦ nil) = b |- 2 = 1");
	}

	/**
	 * Ensures that a hypothesis which contains a non-translatable operator gets
	 * removed by the translation to the plain mathematical language.
	 */
	@Test
	public void untranslatableInHyp() {
		assertSequentTranslation("", //
				"(1 weird 2) = nil |- 3 = 4", //
				"|- 3 = 4");
	}

	/**
	 * Ensures that a goal which contains a non-translatable operator gets
	 * removed by the translation to the plain mathematical language.
	 */
	@Test
	public void untranslatableInGoal() {
		assertSequentTranslation("", //
				"1 = 2 |- (3 weird 4) = nil", //
				"1 = 2 |- ⊥");
	}

	private void assertSequentTranslation(String typeEnvStr,
			String sequentImage, String expectedImage) {

		final ISimpleSequent srcSequent = makeSrcSequent(typeEnvStr,
				sequentImage);
		final ISimpleSequent actual = translateExtensions(srcSequent);
		final FormulaFactory trgFac = actual.getFormulaFactory();
		final ITypeEnvironmentBuilder trgTypenv = actual.getTypeEnvironment()
				.makeBuilder();
		assertTrue(trgFac.getExtensions().isEmpty());
		assertTrue(trgTypenv.getFormulaFactory().getExtensions().isEmpty());

		for (final ITrackedPredicate tracked : actual.getPredicates()) {
			final Predicate pred = tracked.getPredicate();
			assertNoExtendedPredExpr(pred);
		}

		final ISimpleSequent expected = getSimpleSequent(trgTypenv,
				expectedImage);
		assertEquals(expected, actual);
	}

	/*
	 * Builds the sequent with a factory containing the <code>List</code>
	 * datatype and the <code>empty</code> operator.
	 */
	private ISimpleSequent makeSrcSequent(String typeEnvStr, String seqImage) {
		final Set<IFormulaExtension> extensions;
		extensions = LIST_DT.getFactory().getExtensions();
		extensions.add(Empty.INSTANCE);
		extensions.add(Weird.INSTANCE);
		final FormulaFactory srcFac = FormulaFactory.getInstance(extensions);
		final ITypeEnvironmentBuilder srcTypenv;
		srcTypenv = mTypeEnvironment(typeEnvStr, srcFac);
		return getSimpleSequent(srcTypenv, seqImage);
	}

	/**
	 * Checks that the given predicate does not contain any extended expression
	 * or extended predicate.
	 */
	private void assertNoExtendedPredExpr(final Predicate pred) {
		pred.accept(new DefaultSimpleVisitor() {

			@Override
			public void visitExtendedExpression(ExtendedExpression expression) {
				Assert.fail(
						"The sequent should not contain any extended expression");
			}

			@Override
			public void visitExtendedPredicate(ExtendedPredicate predicate) {
				Assert.fail(
						"The sequent should not contain any extended predicate");
			}

		});
	}

	/**
	 * Denotes an empty list of integers.
	 */
	private static class Empty implements IExpressionExtension {

		public static final Empty INSTANCE = new Empty();

		private Empty() {
			// singleton
		}

		private final String symbol = "empty";

		@Override
		public String getSyntaxSymbol() {
			return symbol;
		}

		@Override
		public Predicate getWDPredicate(IExtendedFormula formula,
				IWDMediator wdMediator) {
			return wdMediator.makeTrueWD();
		}

		@Override
		public boolean conjoinChildrenWD() {
			return true;
		}

		@Override
		public String getId() {
			return symbol;
		}

		@Override
		public String getGroupId() {
			return symbol;
		}

		@Override
		public Object getOrigin() {
			return null;
		}

		@Override
		public void addCompatibilities(ICompatibilityMediator mediator) {
			// no compatibility
		}

		@Override
		public void addPriorities(IPriorityMediator mediator) {
			// no priority
		}

		@Override
		public IExtensionKind getKind() {
			return IFormulaExtension.ATOMIC_EXPRESSION;
		}

		@Override
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			return mediator.makeParametricType(LIST_DT.getTypeConstructor(),
					singletonList((Type) mediator.makeIntegerType()));
		}

		@Override
		public boolean verifyType(Type proposedType, Expression[] childExprs,
				Predicate[] childPreds) {
			if (!(proposedType instanceof ParametricType)) {
				return false;
			}
			final ParametricType type = (ParametricType) proposedType;
			final Type[] typeParams = type.getTypeParameters();
			for (int i = 0; i < typeParams.length; i++) {
				if (!(typeParams[i] instanceof IntegerType)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public Type typeCheck(ExtendedExpression expression,
				ITypeCheckMediator tcMediator) {
			return tcMediator.makeParametricType(LIST_DT.getTypeConstructor(),
					singletonList((Type) tcMediator.makeIntegerType()));
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
		}

	}

	/**
	 * Denotes a weird operator that is not WD-strict and takes two integers and
	 * produces a list of integers.
	 * 
	 * The fact that this operator is not WD-strict entails that it cannot be
	 * translated and that it would maintain a dependency on the List datatype.
	 * Consequently, any hypothesis or goal that contain it shall be voided.
	 */
	private static class Weird implements IExpressionExtension {

		public static final Weird INSTANCE = new Weird();

		private Weird() {
			// singleton
		}

		private final String symbol = "weird";

		@Override
		public String getSyntaxSymbol() {
			return symbol;
		}

		@Override
		public Predicate getWDPredicate(IExtendedFormula formula,
				IWDMediator wdMediator) {
			return wdMediator.makeTrueWD();
		}

		@Override
		public boolean conjoinChildrenWD() {
			// non strict operator
			return false;
		}

		@Override
		public String getId() {
			return symbol;
		}

		@Override
		public String getGroupId() {
			return symbol;
		}

		@Override
		public Object getOrigin() {
			return null;
		}

		@Override
		public void addCompatibilities(ICompatibilityMediator mediator) {
			// no compatibility
		}

		@Override
		public void addPriorities(IPriorityMediator mediator) {
			// no priority
		}

		@Override
		public IExtensionKind getKind() {
			return BINARY_INFIX_EXPRESSION;
		}

		@Override
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			return mediator.makeParametricType(LIST_DT.getTypeConstructor(),
					singletonList((Type) mediator.makeIntegerType()));
		}

		@Override
		public boolean verifyType(Type proposedType, Expression[] childExprs,
				Predicate[] childPreds) {
			if (!(childExprs[0].getType() instanceof IntegerType)) {
				return false;
			}
			if (!(childExprs[1].getType() instanceof IntegerType)) {
				return false;
			}

			if (!(proposedType instanceof ParametricType)) {
				return false;
			}
			final ParametricType type = (ParametricType) proposedType;
			final Type[] typeParams = type.getTypeParameters();
			if (typeParams.length != 1) {
				return false;
			}
			return typeParams[0] instanceof IntegerType;
		}

		@Override
		public Type typeCheck(ExtendedExpression expression,
				ITypeCheckMediator tcMediator) {
			return tcMediator.makeParametricType(LIST_DT.getTypeConstructor(),
					singletonList((Type) tcMediator.makeIntegerType()));
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
		}

	}

}
