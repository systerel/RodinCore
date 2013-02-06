/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.core.ast.AssociativeHelper.getSyntaxTreeHelper;
import static org.eventb.core.ast.ExtensionHelper.makeParserPrinter;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.PREDICATE;
import static org.eventb.core.ast.extension.IOperatorProperties.Notation.PREFIX;
import static org.eventb.internal.core.ast.extension.ArityCoverage.ANY;
import static org.eventb.internal.core.ast.extension.ArityCoverage.ONE_OR_MORE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.internal.core.ast.FindingAccumulator;
import org.eventb.internal.core.ast.ITypeCheckingRewriter;
import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.extension.ArityCoverage;
import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.ast.extension.KindMediator;
import org.eventb.internal.core.ast.extension.OperatorCoverage;
import org.eventb.internal.core.ast.extension.TypeCheckMediator;
import org.eventb.internal.core.parser.ExtendedGrammar;
import org.eventb.internal.core.parser.GenParser.OverrideException;
import org.eventb.internal.core.parser.IOperatorInfo;
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

		// the arity given here stands for 'any fixed arity in 0 .. MAX_ARITY
		// for both predicates and expressions, but globally 1 argument or more'
		PARENTHESIZED_PREDICATE(PREFIX, PREDICATE, ANY, ANY, ONE_OR_MORE, false) {

			@Override
			public IParserPrinter<ExtendedPredicate> makeParser(int kind,
					int tag) {
				return new SubParsers.ExtendedPredParen(kind, tag);
			}

		},
		;

		private final OperatorCoverage operProps;

		private ExtendedPredicateParsers(Notation notation, FormulaType formulaType,
				ArityCoverage exprArity, ArityCoverage predArity,
				ArityCoverage globalArity, boolean isAssociative) {
			this.operProps = new OperatorCoverage(notation, formulaType,
					exprArity, predArity, globalArity, isAssociative);
		}

		protected abstract  IParserPrinter<ExtendedPredicate> makeParser(
				int kind, int tag);

		@Override
		public OperatorCoverage getOperatorCoverage() {
			return operProps;
		}
		
		@Override
		public IOperatorInfo<ExtendedPredicate> makeOpInfo(final String image,
				final int tag, final String opId, final String groupId) {
			return new IOperatorInfo<ExtendedPredicate>() {
				
				@Override
				public IParserPrinter<ExtendedPredicate> makeParser(int kind) {
					return ExtendedPredicateParsers.this.makeParser(kind, tag);
				}

				@Override
				public boolean isSpaced() {
					return false;
				}
				
				@Override
				public String getImage() {
					return image;
				}
				
				@Override
				public String getId() {
					return opId;
				}
				
				@Override
				public String getGroupId() {
					return groupId;
				}
			};
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

	/**
	 * Must never be called directly: use the factory method instead.
	 * 
	 * @see FormulaFactory#makeExtendedPredicate(IPredicateExtension,
	 *      Expression[], Predicate[], SourceLocation)
	 * @see FormulaFactory#makeExtendedPredicate(IPredicateExtension,
	 *      java.util.Collection, java.util.Collection, SourceLocation)
	 */
	protected ExtendedPredicate(int tag, Expression[] expressions,
			Predicate[] predicates, SourceLocation location,
			FormulaFactory ff, IPredicateExtension extension) {
		super(tag, ff, location, combineHashCodes(expressions, predicates));
		this.childExpressions = expressions;
		this.childPredicates = predicates;
		this.extension = extension;
		checkPreconditions();
		setPredicateVariableCache(getChildren());
		synthesizeType(ff);
	}

	private void checkPreconditions() {
		final IExtensionKind kind = extension.getKind();
		assert kind.getProperties().getFormulaType() == PREDICATE;
		if (!kind.checkPreconditions(childExpressions, childPredicates)) {
			throw new IllegalArgumentException("Incorrect kind of children");
		}
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
		return childExpressions.clone();
	}

	@Override
	public Predicate[] getChildPredicates() {
		return childPredicates.clone();
	}

	@Override
	public IPredicateExtension getExtension() {
		return extension;
	}
	
	private Formula<?>[] getChildren() {
		return ExtensionHelper.concat(childExpressions, childPredicates);
	}

	@Override
	protected boolean equalsInternal(Formula<?> formula) {
		final ExtendedPredicate other = (ExtendedPredicate) formula;
		return Arrays.equals(childExpressions, other.childExpressions)
				&& Arrays.equals(childPredicates, other.childPredicates);
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
	protected void solveChildrenTypes(TypeUnifier unifier) {
		ExtensionHelper.solveTypes(unifier, childExpressions, childPredicates);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void toString(IToStringMediator mediator) {
		final IParserPrinter<? extends Formula<?>> parser = makeParserPrinter(
				getTag(), extension, mediator);
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
	protected void isLegible(LegibilityResult result) {
		ExtensionHelper.isLegible(childExpressions, childPredicates, result);
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
	protected Predicate rewrite(ITypeCheckingRewriter rewriter) {
		final boolean flatten = rewriter.autoFlatteningMode()
				&& extension.getKind().getProperties().isAssociative();
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
			if (flatten && this.getTag() == newChild.getTag()) {
				final Predicate[] grandChildren = ((ExtendedPredicate) newChild).childPredicates;
				newChildPredicates.addAll(Arrays.asList(grandChildren));
				changed = true;
			} else {
				newChildPredicates.add(newChild);
				changed |= newChild != child;
			}
		}
		final Expression[] newChildExprs;
		final Predicate[] newChildPreds;
		if (!changed) {
			newChildExprs = childExpressions;
			newChildPreds = childPredicates;
		} else {
			newChildExprs = newChildExpressions
					.toArray(new Expression[newChildExpressions.size()]);
			newChildPreds = newChildPredicates
					.toArray(new Predicate[newChildPredicates.size()]);
		}
		return rewriter.rewrite(this, changed, newChildExprs, newChildPreds);
	}

	@Override
	protected final <F> void inspect(FindingAccumulator<F> acc) {
		acc.inspect(this);
		ExtensionHelper.inspectChildren(acc, childExpressions, childPredicates);
	}

	@Override
	public Formula<?> getChild(int index) {
		checkChildIndex(index);
		return ExtensionHelper.getChild(childExpressions, childPredicates,
				index);
	}

	@Override
	public int getChildCount() {
		return ExtensionHelper.getChildCount(childExpressions, childPredicates);
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

	@Override
	public boolean isWDStrict() {
		return extension.conjoinChildrenWD();
	}

}
