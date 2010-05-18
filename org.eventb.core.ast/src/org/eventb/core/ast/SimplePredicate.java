/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added accept for ISimpleVisitor
 *     Systerel - fixed bug in synthesizeType()
 *     Systerel - added support for predicate variables
 *******************************************************************************/
package org.eventb.core.ast;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;
import org.eventb.internal.core.typecheck.TypeVariable;

/**
 * SimplePredicate represents a predicate builds from a single expression in an
 * event-B formula.
 * <p>
 * It can accept tag {KFINITE}.
 * </p>
 * 
 * @author François Terrier
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class SimplePredicate extends Predicate {
	
	// child
	private final Expression child;
	
	// offset in the corresponding tag interval
	private static final int firstTag = FIRST_SIMPLE_PREDICATE;
	private static final String[] tags = {
		"finite" // KFINITE
	};
	
	protected SimplePredicate(Expression child, int tag,
			SourceLocation location, FormulaFactory ff) {
		
		super(tag, location, child.hashCode());
		this.child = child;
		
		assert tag >= firstTag && tag < firstTag+tags.length;
		
		setPredicateVariableCache(this.child);
		synthesizeType(ff);
	}

	@Override
	protected void synthesizeType(FormulaFactory ff) {
		this.freeIdents = child.freeIdents;
		this.boundIdents = child.boundIdents;
		
		if (! child.isTypeChecked()) {
			return;
		}
		final Type type = child.getType();
		if (type == null || type.getBaseType() == null) {
			return;
		}
		typeChecked = true;
	}
	
	/**
	 * Returns the child expression of this node.
	 * 
	 * @return the expression of this node 
	 */
	public Expression getExpression() {
		return child;
	}

	@Override
	protected void toString(StringBuilder builder, boolean isRightChild, int parentTag,
			String[] boundNames, boolean withTypes) {

		builder.append(tags[getTag()-firstTag]);
		builder.append('(');
		child.toString(builder, false, getTag(), boundNames, withTypes);
		builder.append(')');
	}

	@Override
	protected void toStringFullyParenthesized(StringBuilder builder, String[] boundNames) {
		builder.append(tags[getTag()-firstTag]);
		builder.append('(');
		child.toStringFullyParenthesized(builder, boundNames);
		builder.append(')');
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		return tabs + this.getClass().getSimpleName() + " ["
				+ tags[getTag() - firstTag] + "]\n"
				+ child.getSyntaxTree(boundNames, tabs + "\t");
	}

	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		child.isLegible(result, quantifiedIdents);
	}
	
	@Override
	protected boolean equals(Formula<?> other, boolean withAlphaConversion) {
		if (this.getTag() != other.getTag()) {
			return false;
		}
		SimplePredicate temp = (SimplePredicate) other;
		return child.equals(temp.child, withAlphaConversion);
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		TypeVariable alpha = result.newFreshVariable(null);
		child.typeCheck(result, quantifiedIdentifiers);
		result.unify(child.getType(), result.makePowerSetType(alpha), this);
	}
	
	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		return child.solveType(unifier);
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		child.collectFreeIdentifiers(freeIdentSet);
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		child.collectNamesAbove(names, boundNames, offset);
	}
	
	@Override
	protected Predicate bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		Expression newChild = child.bindTheseIdents(binding, offset, factory);
		if (newChild == child) {
			return this;
		}
		return factory.makeSimplePredicate(getTag(), newChild, getSourceLocation());
	}
	
	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = true;

		switch (getTag()) {
		case KFINITE: goOn = visitor.enterKFINITE(this); break;
		default:      assert false;
		}

		if (goOn) goOn = child.accept(visitor);
		
		switch (getTag()) {
		case KFINITE: return visitor.exitKFINITE(this);
		default:      return true;
		}
	}

	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitSimplePredicate(this);
	}

	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return child.getWDPredicateRaw(formulaFactory);
	}

	@Override
	public Predicate rewrite(IFormulaRewriter rewriter) {
		final Expression newChild = child.rewrite(rewriter);
		final SimplePredicate before;
		if (newChild == child) {
			before = this;
		} else {
			before = rewriter.getFactory().makeSimplePredicate(getTag(),
					newChild, getSourceLocation());
		}
		return checkReplacement(rewriter.rewrite(before));
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		child.addGivenTypes(set);
	}

	@Override
	protected void getPositions(IFormulaFilter filter, IntStack indexes,
			List<IPosition> positions) {
		
		if (filter.select(this)) {
			positions.add(new Position(indexes));
		}

		indexes.push(0);
		child.getPositions(filter, indexes, positions);
		indexes.pop();
	}

	@Override
	protected Formula<?> getChild(int index) {
		if (index == 0) {
			return child;
		}
		return null;
	}

	@Override
	protected IPosition getDescendantPos(SourceLocation sloc, IntStack indexes) {
		indexes.push(0);
		IPosition pos = child.getPosition(sloc, indexes);
		if (pos != null)
			return pos;
		indexes.pop();
		return new Position(indexes);
	}

	@Override
	protected Predicate rewriteChild(int index, SingleRewriter rewriter) {
		if (index != 0) 
			throw new IllegalArgumentException("Position is outside the formula");
		Expression newChild = rewriter.rewrite(child);
		return rewriter.factory.makeSimplePredicate(getTag(), newChild,
				getSourceLocation());
	}

}
