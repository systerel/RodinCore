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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.ast.extension.TypeCheckMediator;
import org.eventb.internal.core.ast.extension.TypeMediator;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * @author "Nicolas Beauger"
 * @since 2.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class ExtendedExpression extends Expression implements IExtendedFormula {

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
		synthesizeType(ff, null);
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
	protected void synthesizeType(FormulaFactory ff, Type givenType) {
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

		Type resultType = extension.getType(new TypeMediator(ff), this);
		if (resultType == null) {
			return;
		}
		setFinalType(resultType, givenType);
	}

	public Expression[] getChildExpressions() {
		return childExpressions.clone();
	}

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
				isTerminalNode());
		final Type resultType = extension.typeCheck(mediator, this);
		setTemporaryType(resultType, result);
	}

	private boolean isTerminalNode() {
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
		
		return wdMed.addChildrenWD(extensionWD, this);
	}

	@Override
	protected FormulaFactory getFactory() {
		return ff;
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
			if (flatten && extension.getKind().isFlattenable()
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
	protected void getPositions(IFormulaFilter filter, IntStack indexes,
			List<IPosition> positions) {

		if (filter.select(this)) {
			positions.add(new Position(indexes));
		}

		indexes.push(0);
		for (Expression child : childExpressions) {
			child.getPositions(filter, indexes, positions);
			indexes.incrementTop();
		}
		for (Predicate child : childPredicates) {
			child.getPositions(filter, indexes, positions);
			indexes.incrementTop();
		}
		indexes.pop();
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
		// TODO Auto-generated method stub
		return super.isATypeExpression();
	}
	
	@Override
	public Type toType(FormulaFactory factory)
			throws InvalidExpressionException {
		// TODO Auto-generated method stub
		return super.toType(factory);
	}
	
}
