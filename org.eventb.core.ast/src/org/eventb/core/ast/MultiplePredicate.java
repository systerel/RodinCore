/*******************************************************************************
 * Copyright (c) 2009, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.core.ast.AssociativeHelper.equalsHelper;
import static org.eventb.core.ast.AssociativeHelper.getSyntaxTreeHelper;
import static org.eventb.core.ast.AssociativeHelper.isLegibleList;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.FindingAccumulator;
import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;
import org.eventb.internal.core.typecheck.TypeVariable;

/**
 * MultiplePredicate is the class for all predicates taking a variable number of
 * expression arguments in an event-B formula.
 * <p>
 * It can have several children which can only be Expression objects. Can only
 * accept {KPARTITION}.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 1.0
 */
public class MultiplePredicate extends Predicate {

	// The children of this multiple predicate.
	// Is never null and contains at least one element by construction.
	protected final Expression[] children;

	// offset of the corresponding interval in Formula
	protected static final int firstTag = FIRST_MULTIPLE_PREDICATE;
	protected static String[] tags = {
		"partition" // KPARTITION
	};
	// For testing purposes
	public static final int TAGS_LENGTH = tags.length;

	protected MultiplePredicate(Expression[] children, int tag,
			SourceLocation location, FormulaFactory factory) {
		super(tag, location, combineHashCodes(children));
		this.children = children.clone();

		checkPreconditions();
		setPredicateVariableCache(this.children);
		synthesizeType(factory);
	}

	protected MultiplePredicate(Collection<Expression> children, int tag,
			SourceLocation location, FormulaFactory factory) {
		super(tag, location, combineHashCodes(children));
		Expression[] temp = new Expression[children.size()];
		this.children = children.toArray(temp);

		checkPreconditions();
		setPredicateVariableCache(this.children);
		synthesizeType(factory);
	}

	private void checkPreconditions() {
		assert getTag() == KPARTITION;
		assert children != null && children.length != 0;
	}

	@Override
	protected void synthesizeType(FormulaFactory ff) {
		IdentListMerger freeIdentMerger = mergeFreeIdentifiers(children);
		this.freeIdents = freeIdentMerger.getFreeMergedArray();
	
		IdentListMerger boundIdentMerger = mergeBoundIdentifiers(children);
		this.boundIdents = boundIdentMerger.getBoundMergedArray();
	
		if (freeIdentMerger.containsError() || boundIdentMerger.containsError()) {
			// Incompatible type environments, don't bother going further.
			return;
		}
	
		final Type childType = children[0].getType();
		if (!(childType instanceof PowerSetType)) {
			return;
		}
		for (int i = 1; i < children.length; i++) {
			if (!childType.equals(children[i].getType())) {
				return;
			}
		}
		typeChecked = true;
	}

	/**
	 * Returns the children of this node.
	 * 
	 * @return the children of this node, neither <code>null</code> nor empty
	 */
	public Expression[] getChildren() {
		return children.clone();
	}

	@Override
	protected void toString(StringBuilder builder, boolean isRightChild,
			int parentTag, String[] boundNames, boolean withTypes) {
		builder.append(tags[getTag()-firstTag]);
		builder.append('(');
		String sep = "";
		for (Expression child : children) {
			builder.append(sep);
			sep = ",";
			child.toString(builder, false, getTag(), boundNames, withTypes);
		}
		builder.append(')');
	}

	@Override
	protected void toStringFullyParenthesized(StringBuilder builder, String[] boundNames) {
		builder.append(tags[getTag()-firstTag]);
		builder.append('(');
		String sep = "";
		for (Expression child : children) {
			builder.append(sep);
			sep = ",";
			child.toStringFullyParenthesized(builder, boundNames);
		}
		builder.append(')');
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		return getSyntaxTreeHelper(boundNames, tabs, children,
				tags[getTag()-firstTag], "", this.getClass().getSimpleName());
	}

	@Override
	protected void isLegible(LegibilityResult result,
			BoundIdentDecl[] quantifiedIdents) {
		isLegibleList(children, result, quantifiedIdents);
	}

	@Override
	protected boolean equals(Formula<?> other, boolean withAlphaConversion) {
		if (this.getTag() != other.getTag()) {
			return false;
		}
		return equalsHelper(children, ((MultiplePredicate) other).children,
				withAlphaConversion);
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		final TypeVariable alpha = result.newFreshVariable(null);
		final Type childType = result.makePowerSetType(alpha);
		for (Expression child : children) {
			child.typeCheck(result, quantifiedIdentifiers);
			result.unify(child.getType(), childType, this);
		}
	}

	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		boolean success = true;
		for (Expression child: children) {
			success &= child.solveType(unifier);
		}
		return success;
	}

	@Override
	protected void collectFreeIdentifiers(
			LinkedHashSet<FreeIdentifier> freeIdentSet) {
		for (Expression child: children) {
			child.collectFreeIdentifiers(freeIdentSet);
		}
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames,
			int offset) {
		for (Expression child: children) {
			child.collectNamesAbove(names, boundNames, offset);
		}
	}

	@Override
	protected Predicate bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		boolean changed = false;
		Expression[] newChildren = new Expression[children.length];
		for (int i = 0; i < children.length; i++) {
			newChildren[i] = children[i].bindTheseIdents(binding, offset, factory);
			changed |= newChildren[i] != children[i];
		}
		if (!changed) {
			return this;
		}
		return factory.makeMultiplePredicate(getTag(), newChildren,	getSourceLocation());
	}

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = visitor.enterKPARTITION(this);
	
		for (int i = 0; goOn && i < children.length; i++) {
			if (i != 0) {
				goOn = visitor.continueKPARTITION(this);
			}
			if (goOn) {
				goOn = children[i].accept(visitor);
			}
		}
		
		return visitor.exitKPARTITION(this);
	}

	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitMultiplePredicate(this);
	}

	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return getWDConjunction(formulaFactory, children);
	}

	@Override
	public Predicate rewrite(IFormulaRewriter rewriter) {
		final int length = children.length;
		final FormulaFactory ff = rewriter.getFactory();
		final SourceLocation sloc = getSourceLocation();

		final Expression[] newChildren = new Expression[length];
		boolean changed = false;
		for (int i = 0; i < length; i++) {
			final Expression child = children[i];
			final Expression newChild = child.rewrite(rewriter);
			newChildren[i] = newChild;
			changed |= newChild != child;
		}
		final MultiplePredicate before;
		if (!changed) {
			before = this;
		} else {
			before = ff.makeMultiplePredicate(getTag(), newChildren, sloc);
		}
		return checkReplacement(rewriter.rewrite(before));
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		for (Expression child: children) {
			child.addGivenTypes(set);
		}
	}

	@Override
	protected final <F> void inspect(FindingAccumulator<F> acc) {
		acc.inspect(this);
		acc.enterChildren();
		for (Expression child: children) {
			child.inspect(acc);
			acc.nextChild();
		}
		acc.leaveChildren();
	}

	@Override
	protected Formula<?> getChild(int index) {
		if (index < children.length) {
			return children[index];
		}
		return null;
	}

	@Override
	protected IPosition getDescendantPos(SourceLocation sloc, IntStack indexes) {
		indexes.push(0);
		for (Expression child: children) {
			IPosition pos = child.getPosition(sloc, indexes);
			if (pos != null)
				return pos;
			indexes.incrementTop();
		}
		indexes.pop();
		return new Position(indexes);
	}

	@Override
	protected Predicate rewriteChild(int index, SingleRewriter rewriter) {
		if (index < 0 || children.length <= index)
			throw new IllegalArgumentException(
					"Position is outside the formula");
		Expression[] newChildren = children.clone();
		newChildren[index] = rewriter.rewrite(children[index]);
		return rewriter.factory.makeMultiplePredicate(getTag(), newChildren,
				getSourceLocation());
	}
	
}
