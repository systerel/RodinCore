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

//FIXME should not use AssociativeHelper (else rename Associative into ...)
import static org.eventb.core.ast.AssociativeHelper.equalsHelper;
import static org.eventb.core.ast.AssociativeHelper.getSyntaxTreeHelper;
import static org.eventb.core.ast.extension.IArity.MAX_ARITY;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.EXPRESSION;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.PREDICATE;
import static org.eventb.core.ast.extension.IOperatorProperties.Notation.PREFIX;
import static org.eventb.internal.core.ast.extension.OperatorProperties.makeOperProps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IOperatorProperties;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.internal.core.ast.FindingAccumulator;
import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.extension.Arity;
import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.ast.extension.KindMediator;
import org.eventb.internal.core.ast.extension.TypeCheckMediator;
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
public class ExtendedPredicate extends Predicate implements IExtendedFormula {

	private static enum ExtendedPredicateParsers implements
	IPropertyParserInfo<ExtendedPredicate> {

		// the arity given here stands for 'any fixed arity in 1 .. MAX_ARITY'
		PARENTHESIZED_PREDICATE(makeOperProps(PREFIX, PREDICATE, new Arity(1,
				MAX_ARITY), EXPRESSION, false)) {

			@Override
			public IParserPrinter<ExtendedPredicate> makeParser(int kind,
					int tag) {
				return new SubParsers.ExtendedPredParen(kind, tag);
			}

		},
		;

		private final IOperatorProperties operProps;

		private ExtendedPredicateParsers(IOperatorProperties operProps) {
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
			for (IPropertyParserInfo<? extends Formula<?>> parserInfo : ExtendedPredicateParsers
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
	private final IPredicateExtension extension;
	private final FormulaFactory ff;

	
	public ExtendedPredicate(int tag, Expression[] expressions,
			Predicate[] predicates, SourceLocation location,
			FormulaFactory ff, IPredicateExtension extension) {
		super(tag, location, combineHashCodes(combineHashCodes(expressions),
				combineHashCodes(predicates)));
		this.childExpressions = expressions.clone();
		this.childPredicates = predicates.clone();
		this.extension = extension;
		this.ff = ff;
		checkPreconditions();
		setPredicateVariableCache(getChildren());
		synthesizeType(ff);
	}

	private void checkPreconditions() {
		final IExtensionKind kind = extension.getKind();
		assert kind.getProperties().getFormulaType() == PREDICATE;
		assert kind.checkPreconditions(childExpressions, childPredicates);
	}

	@Override
	protected void synthesizeType(FormulaFactory factory) {
		final Formula<?>[] children = getChildren();
		IdentListMerger freeIdentMerger = mergeFreeIdentifiers(children);
		this.freeIdents = freeIdentMerger.getFreeMergedArray();
	
		IdentListMerger boundIdentMerger = mergeBoundIdentifiers(children);
		this.boundIdents = boundIdentMerger.getBoundMergedArray();
	
		if (freeIdentMerger.containsError() || boundIdentMerger.containsError()) {
			// Incompatible type environments, don't bother going further.
			return;
		}
		
		for (Formula<?> child: children) {
			if (! child.isTypeChecked()) {
				return;
			}
		}
		typeChecked = true;
	}

	@Override
	public Expression[] getChildExpressions() {
		return childExpressions; // FIXME clone
	}

	@Override
	public Predicate[] getChildPredicates() {
		return childPredicates; // FIXME clone
	}

	@Override
	public IPredicateExtension getExtension() {
		return extension;
	}
	
	private Formula<?>[] getChildren() {
		return ExtensionHelper.concat(childExpressions, childPredicates);
	}

	@Override
	protected boolean equals(Formula<?> other, boolean withAlphaConversion) {
		if (this.getTag() != other.getTag()) {
			return false;
		}
		return equalsHelper(childExpressions,
						((ExtendedPredicate) other).childExpressions,
						withAlphaConversion)
				&& equalsHelper(childPredicates,
						((ExtendedPredicate) other).childPredicates,
						withAlphaConversion);
	}

	@Override
	protected void typeCheck(TypeCheckResult result,
			BoundIdentDecl[] quantifiedIdentifiers) {
		for (Formula<?> child : getChildren()) {
			child.typeCheck(result, quantifiedIdentifiers);
		}
		extension.typeCheck(this, new TypeCheckMediator(result, this, false));
	}

	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		return ExtensionHelper.solveTypes(unifier, childExpressions,
				childPredicates);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void toString(IToStringMediator mediator) {
		final IOperatorProperties properties = extension.getKind()
		.getProperties();
		final IParserPrinter<? extends Formula<?>> parser = ff.getGrammar()
				.getParser(properties, mediator.getKind(), getTag());
		final IParserPrinter<ExtendedPredicate> extParser = (IParserPrinter<ExtendedPredicate>) parser;
		extParser.toString(mediator, this);
	}

	@Override
	protected int getKind(KindMediator mediator) {
		return mediator.getKind(extension.getSyntaxSymbol());
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		return getSyntaxTreeHelper(boundNames, tabs, getChildren(), extension
				.getSyntaxSymbol(), "", this.getClass()
				.getSimpleName());
	}

	@Override
	protected void isLegible(LegibilityResult result,
			BoundIdentDecl[] quantifiedIdents) {
		ExtensionHelper.isLegible(childExpressions, childPredicates, result,
				quantifiedIdents);
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

	// FIXME copy/paste from ExtendedExpression
	@Override
	protected Predicate bindTheseIdents(Map<String, Integer> binding,
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
		return factory.makeExtendedPredicate(extension, newChildExpressions,
				newChildPredicates, getSourceLocation());
	}

	// TODO everywhere, we consider expressions first, then predicates
	// this might not always be convenient, for instance in the visitor 
	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = visitor.enterExtendedPredicate(this);

		for (int i = 0; goOn && i < childExpressions.length; i++) {
			if (i != 0) {
				goOn = visitor.continueExtendedPredicate(this);
			}
			if (goOn) {
				goOn = childExpressions[i].accept(visitor);
			}
		}
		for (int i = 0; goOn && i < childPredicates.length; i++) {
			goOn = visitor.continueExtendedPredicate(this);
			if (goOn) {
				goOn = childPredicates[i].accept(visitor);
			}
		}

		return visitor.exitExtendedPredicate(this);
	}

	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitExtendedPredicate(this);
	}

	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		final WDMediator wdMed = new WDMediator(formulaFactory);
		final Predicate extensionWD = extension.getWDPredicate(this, wdMed);
		
		if (extension.conjoinChildrenWD()) {
			return wdMed.addChildrenWD(extensionWD, this);
		} else {
			return extensionWD;
		}
	}

	@Override
	public Predicate rewrite(IFormulaRewriter rewriter) {
		final boolean flatten = rewriter.autoFlatteningMode();
		final ArrayList<Expression> newChildExpressions = new ArrayList<Expression>(
				childExpressions.length);
		boolean changed = false;
		for (Expression child : childExpressions) {
			Expression newChild = child.rewrite(rewriter);
			newChildExpressions.add(newChild);
			changed |= newChild != child;
		}
		final ArrayList<Predicate> newChildPredicates = new ArrayList<Predicate>(
				childPredicates.length + 11);
		for (Predicate child : childPredicates) {
			Predicate newChild = child.rewrite(rewriter);
			if (flatten && extension.getKind().getProperties().isAssociative()
					&& getTag() == newChild.getTag()) {
				final Predicate[] grandChildren = ((ExtendedPredicate) newChild).childPredicates;
				newChildPredicates.addAll(Arrays.asList(grandChildren));
				changed = true;
			} else {
				newChildPredicates.add(newChild);
				changed |= newChild != child;
			}
		}
		final ExtendedPredicate before;
		if (!changed) {
			before = this;
		} else {
			before = rewriter.getFactory().makeExtendedPredicate(extension,
					newChildExpressions, newChildPredicates,
					getSourceLocation());
		}
		return checkReplacement(rewriter.rewrite(before));
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		ExtensionHelper.addGivenTypes(set, childExpressions, childPredicates);
	}

	// FIXME duplicate code with ExtendedExpression; problem: filter.select(this)
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
	protected Predicate rewriteChild(int index, SingleRewriter rewriter) {
		if (index < 0
				|| childExpressions.length + childPredicates.length <= index)
			throw new IllegalArgumentException(
					"Position is outside the formula");
		if (index < childExpressions.length) {
			Expression[] newChildExpressions = childExpressions.clone();
			newChildExpressions[index] = rewriter
					.rewrite(childExpressions[index]);
			return rewriter.factory.makeExtendedPredicate(extension,
					newChildExpressions, childPredicates.clone(),
					getSourceLocation());
		} else {
			index = index - childExpressions.length;
			Predicate[] newChildPredicates = childPredicates.clone();
			newChildPredicates[index] = rewriter
					.rewrite(childPredicates[index]);
			return rewriter.factory.makeExtendedPredicate(extension,
					childExpressions.clone(), newChildPredicates,
					getSourceLocation());
		}
	}

}
