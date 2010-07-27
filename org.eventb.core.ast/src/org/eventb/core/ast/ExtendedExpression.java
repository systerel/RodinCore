/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

// FIXME should not use AssociativeHelper
import static org.eventb.core.ast.AssociativeHelper.equalsHelper;
import static org.eventb.core.ast.AssociativeHelper.getSyntaxTreeHelper;
import static org.eventb.core.ast.extension.IOperatorProperties.BINARY;
import static org.eventb.core.ast.extension.IOperatorProperties.MULTARY_2;
import static org.eventb.core.ast.extension.IOperatorProperties.NULLARY;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.EXPRESSION;
import static org.eventb.core.ast.extension.IOperatorProperties.Notation.INFIX;
import static org.eventb.core.ast.extension.IOperatorProperties.Notation.PREFIX;
import static org.eventb.internal.core.ast.extension.OperatorProperties.makeOperProps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IOperatorProperties;
import org.eventb.core.ast.extension.IOperatorProperties.Arity;
import org.eventb.internal.core.ast.FindingAccumulator;
import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.ast.extension.KindMediator;
import org.eventb.internal.core.ast.extension.TypeCheckMediator;
import org.eventb.internal.core.ast.extension.TypeMediator;
import org.eventb.internal.core.parser.ExtendedGrammar;
import org.eventb.internal.core.parser.GenParser.OverrideException;
import org.eventb.internal.core.parser.IParserPrinter;
import org.eventb.internal.core.parser.IPropertyParserInfo;
import org.eventb.internal.core.parser.SubParsers;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * @author "Nicolas Beauger"
 * @since 2.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class ExtendedExpression extends Expression implements IExtendedFormula {

	private static enum ExtendedExpressionParsers implements
			IPropertyParserInfo<ExtendedExpression> {

		EXTENDED_ATOMIC_EXPRESSION(makeOperProps(PREFIX, EXPRESSION, NULLARY,
				EXPRESSION, false)) {

			@Override
			public IParserPrinter<ExtendedExpression> makeParser(int kind,
					int tag) {
				return new SubParsers.ExtendedAtomicExpressionParser(kind, tag);
			}

		},

		EXTENDED_BINARY_EXPRESSION(makeOperProps(INFIX, EXPRESSION, BINARY,
				EXPRESSION, false)) {

			@Override
			public IParserPrinter<ExtendedExpression> makeParser(int kind,
					int tag) {
				return new SubParsers.ExtendedBinaryExpressionInfix(kind, tag);
			}
		},

		EXTENDED_ASSOCIATIVE_EXPRESSION(makeOperProps(INFIX, EXPRESSION,
				MULTARY_2, EXPRESSION, true)) {

			@Override
			public IParserPrinter<ExtendedExpression> makeParser(int kind,
					int tag) {
				return new SubParsers.ExtendedAssociativeExpressionInfix(kind,
						tag);
			}
		},

		// the arity given here stands for 'any fixed arity in 1 .. MAX_ARITY'
		PARENTHESIZED_EXPRESSION(makeOperProps(PREFIX, EXPRESSION, new Arity(1,
				IOperatorProperties.MAX_ARITY), EXPRESSION, false)) {

			@Override
			public IParserPrinter<ExtendedExpression> makeParser(int kind,
					int tag) {
				return new SubParsers.ExtendedExprParen(kind, tag);
			}
		},

		;

		private final IOperatorProperties operProps;

		private ExtendedExpressionParsers(IOperatorProperties operProps) {
			this.operProps = operProps;
		}

		@Override
		public IOperatorProperties getProperties() {
			return operProps;
		}

	}

	/**
	 * @since 2.0
	 */
	public static void init(ExtendedGrammar grammar) {
		try {
			for (IPropertyParserInfo<? extends Formula<?>> parserInfo : ExtendedExpressionParsers
					.values()) {
				grammar.addParser(parserInfo);
			}
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private final Expression[] childExpressions;
	private final Predicate[] childPredicates;
	private final IExpressionExtension extension;
	private final FormulaFactory ff;

	protected ExtendedExpression(int tag, Expression[] expressions,
			Predicate[] predicates, SourceLocation location,
			FormulaFactory ff, IExpressionExtension extension, Type type) {
		super(tag, location, combineHashCodes(combineHashCodes(expressions),
				combineHashCodes(predicates)));
		this.childExpressions = expressions.clone();
		this.childPredicates = predicates.clone();
		this.extension = extension;
		this.ff = ff;
		checkPreconditions();
		setPredicateVariableCache(getChildren());
		synthesizeType(ff, type);
	}

	private void checkPreconditions() {
		assert extension.getKind().checkPreconditions(childExpressions,
				childPredicates);
	}

	private Formula<?>[] getChildren() {
		return ExtensionHelper.concat(childExpressions, childPredicates);
	}

	private boolean isFirstChildTypechecked() {
		if (childExpressions.length > 0) {
			return childExpressions[0].isTypeChecked();
		}
		if (childPredicates.length > 0) {
			return childPredicates[0].isTypeChecked();
		}
		// atomic expression: consider children type checked
		return true;
	}

	@Override
	protected void synthesizeType(FormulaFactory factory, Type givenType) {
		IdentListMerger freeIdentMerger = mergeFreeIdentifiers(getChildren());
		this.freeIdents = freeIdentMerger.getFreeMergedArray();

		IdentListMerger boundIdentMerger = mergeBoundIdentifiers(getChildren());
		this.boundIdents = boundIdentMerger.getBoundMergedArray();

		if (freeIdentMerger.containsError() || boundIdentMerger.containsError()) {
			// Incompatible type environments, don't bother going further.
			return;
		}

		// Fast exit if first child is not typed
		// (the most common case where type synthesis can't be done)
		if (!isFirstChildTypechecked()) {
			return;
		}

		final Type resultType = extension.synthesizeType(this,
				givenType, new TypeMediator(factory));
		if (resultType == null) {
			return;
		}
		setFinalType(resultType, givenType);
	}

	@Override
	public Expression[] getChildExpressions() {
		return childExpressions.clone();
	}

	@Override
	public Predicate[] getChildPredicates() {
		return childPredicates.clone();
	}

	@Override
	protected boolean equals(Formula<?> other, boolean withAlphaConversion) {
		if (this.getTag() != other.getTag()) {
			return false;
		}
		return hasSameType(other)
				&& equalsHelper(childExpressions,
						((ExtendedExpression) other).childExpressions,
						withAlphaConversion)
				&& equalsHelper(childPredicates,
						((ExtendedExpression) other).childPredicates,
						withAlphaConversion);
	}

	@Override
	protected void typeCheck(TypeCheckResult result,
			BoundIdentDecl[] quantifiedIdentifiers) {
		for (Formula<?> child : getChildren()) {
			child.typeCheck(result, quantifiedIdentifiers);
		}
		final TypeCheckMediator mediator = new TypeCheckMediator(result, this,
				isAtomic());
		final Type resultType = extension.typeCheck(mediator, this);
		setTemporaryType(resultType, result);
	}

	public boolean isAtomic() {
		return childExpressions.length == 0 && childPredicates.length == 0;
	}

	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		return ExtensionHelper.solveTypes(unifier, childExpressions,
				childPredicates);
	}

	@Override
	protected void isLegible(LegibilityResult result,
			BoundIdentDecl[] quantifiedIdents) {
		ExtensionHelper.isLegible(childExpressions, childPredicates, result,
				quantifiedIdents);
	}

	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		final WDMediator wdMed = new WDMediator(formulaFactory);
		final Predicate extensionWD = extension.getWDPredicate(wdMed, this);
		
		if (extension.conjoinChildrenWD()) {
			return wdMed.addChildrenWD(extensionWD, this);
		} else {
			return extensionWD;
		}
	}

	@Override
	protected FormulaFactory getFactory() {
		return ff;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void toString(IToStringMediator mediator) {
		final IOperatorProperties properties = extension.getKind()
				.getProperties();
		final IParserPrinter<? extends Formula<?>> parser = ff.getGrammar()
				.getParser(properties, mediator.getKind(), getTag());
		final IParserPrinter<ExtendedExpression> extParser = (IParserPrinter<ExtendedExpression>) parser;
		extParser.toString(mediator, this);
	}

	@Override
	protected int getKind(KindMediator mediator) {
		return mediator.getKind(extension.getSyntaxSymbol());
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		// only called for test purposes => ask it from extension ?
		return getSyntaxTreeHelper(boundNames, tabs, getChildren(), extension
				.getSyntaxSymbol(), getTypeName(), this
				.getClass().getSimpleName());
	}

	@Override
	protected void collectFreeIdentifiers(
			LinkedHashSet<FreeIdentifier> freeIdentSet) {
		ExtensionHelper.collectFreeIdentifiers(freeIdentSet, childExpressions,
				childPredicates);
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames,
			int offset) {
		ExtensionHelper.collectNamesAbove(names, boundNames, offset,
				childExpressions, childPredicates);
	}

	@Override
	protected Expression bindTheseIdents(Map<String, Integer> binding,
			int offset, FormulaFactory factory) {
		boolean changed = false;
		Expression[] newChildExpressions = new Expression[childExpressions.length];
		for (int i = 0; i < childExpressions.length; i++) {
			newChildExpressions[i] = childExpressions[i].bindTheseIdents(
					binding, offset, factory);
			changed |= newChildExpressions[i] != childExpressions[i];
		}
		Predicate[] newChildPredicates = new Predicate[childPredicates.length];
		for (int i = 0; i < childPredicates.length; i++) {
			newChildPredicates[i] = childPredicates[i].bindTheseIdents(binding,
					offset, factory);
			changed |= newChildPredicates[i] != childPredicates[i];
		}
		if (!changed) {
			return this;
		}
		return factory.makeExtendedExpression(extension, newChildExpressions,
				newChildPredicates, getSourceLocation());
	}

	// TODO everywhere, we consider expressions first, then predicates
	// this might not always be convenient, for instance in the visitor
	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = visitor.enterExtendedExpression(this);

		for (int i = 0; goOn && i < childExpressions.length; i++) {
			if (i != 0) {
				goOn = visitor.continueExtendedExpression(this);
			}
			if (goOn) {
				goOn = childExpressions[i].accept(visitor);
			}
		}
		for (int i = 0; goOn && i < childPredicates.length; i++) {
			goOn = visitor.continueExtendedExpression(this);
			if (goOn) {
				goOn = childPredicates[i].accept(visitor);
			}
		}

		return visitor.exitExtendedExpression(this);
	}

	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitExtendedExpression(this);
	}

	@Override
	public Expression rewrite(IFormulaRewriter rewriter) {
		final boolean flatten = rewriter.autoFlatteningMode();
		final ArrayList<Expression> newChildExpressions = new ArrayList<Expression>(
				childExpressions.length + 11);
		boolean changed = false;
		for (Expression child : childExpressions) {
			Expression newChild = child.rewrite(rewriter);
			if (flatten && extension.getKind().getProperties().isAssociative()
					&& getTag() == newChild.getTag()) {
				final Expression[] grandChildren = ((ExtendedExpression) newChild).childExpressions;
				newChildExpressions.addAll(Arrays.asList(grandChildren));
				changed = true;
			} else {
				newChildExpressions.add(newChild);
				changed |= newChild != child;
			}
		}
		final ArrayList<Predicate> newChildPredicates = new ArrayList<Predicate>(
				childPredicates.length);
		for (Predicate child : childPredicates) {
			Predicate newChild = child.rewrite(rewriter);
			newChildPredicates.add(newChild);
			changed |= newChild != child;
		}
		final ExtendedExpression before;
		if (!changed) {
			before = this;
		} else {
			// FIXME should check preconditions about new children
			// (flattening could break preconditions) 
			before = rewriter.getFactory().makeExtendedExpression(extension,
					newChildExpressions, newChildPredicates,
					getSourceLocation());
		}
		return checkReplacement(rewriter.rewrite(before));
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		ExtensionHelper.addGivenTypes(set, childExpressions, childPredicates);
	}

	// FIXME duplicate code with ExtendedPredicate; problem: filter.select(this)
	@Override
	protected final <F> void inspect(FindingAccumulator<F> acc) {
		acc.inspect(this);
		acc.enterChildren();
		for (Expression child: childExpressions) {
			child.inspect(acc);
			acc.nextChild();
		}
		for (Predicate child: childPredicates) {
			child.inspect(acc);
			acc.nextChild();
		}
		acc.leaveChildren();
	}

	@Override
	protected Formula<?> getChild(int index) {
		return ExtensionHelper.getFormula(childExpressions, childPredicates,
				index);
	}

	@Override
	protected IPosition getDescendantPos(SourceLocation sloc, IntStack indexes) {
		return ExtensionHelper.getDescendantPos(childExpressions,
				childPredicates, sloc, indexes);
	}

	@Override
	protected Expression rewriteChild(int index, SingleRewriter rewriter) {
		if (index < 0
				|| childExpressions.length + childPredicates.length <= index)
			throw new IllegalArgumentException(
					"Position is outside the formula");
		if (index < childExpressions.length) {
			Expression[] newChildExpressions = childExpressions.clone();
			newChildExpressions[index] = rewriter
					.rewrite(childExpressions[index]);
			return rewriter.factory.makeExtendedExpression(extension,
					newChildExpressions, childPredicates.clone(),
					getSourceLocation());
		} else {
			index = index - childExpressions.length;
			Predicate[] newChildPredicates = childPredicates.clone();
			newChildPredicates[index] = rewriter
					.rewrite(childPredicates[index]);
			return rewriter.factory.makeExtendedExpression(extension,
					childExpressions.clone(), newChildPredicates,
					getSourceLocation());
		}
	}

	@Override
	public boolean isATypeExpression() {
		if (!extension.isATypeConstructor()) {
			return false;
		}
		if (childPredicates.length != 0) {
			return false;
		}
		for (Expression child : childExpressions) {
			if (!child.isATypeExpression()) {
				return false;
			}
		}
		return true;
	}
	
	@Deprecated
	@Override
	public Type toType(FormulaFactory factory)
			throws InvalidExpressionException {
		if (!extension.isATypeConstructor()) {
			throw new InvalidExpressionException();
		}
		final List<Type> typeParams = new ArrayList<Type>();
		for(Expression child : childExpressions) {
			typeParams.add(child.toType());
		}
		return factory.makeGenericType(typeParams, extension);
	}
	
	@Override
	public Type toType() throws InvalidExpressionException {
		return toType(getFactory());
	}
}
