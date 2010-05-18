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

/**
 * BoolExpression represents the bool keyword of an event-B formula.
 * <p>
 * Can only accept {KBOOL}.
 * </p>
 * 
 * @author François Terrier
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class BoolExpression extends Expression {

	// child
	private final Predicate child;
	
	protected BoolExpression(Predicate child, int tag, SourceLocation location,
			FormulaFactory ff) {
		
		super(tag, location, child.hashCode());
		assert tag == KBOOL;
		this.child = child;
		setPredicateVariableCache(this.child);
		synthesizeType(ff, null);
	}

	@Override
	protected void synthesizeType(FormulaFactory ff, Type givenType) {
		this.freeIdents = child.freeIdents;
		this.boundIdents = child.boundIdents;
		
		if (! child.isTypeChecked()) {
			return;
		}
		
		setFinalType(ff.makeBooleanType(), givenType);
	}

	/**
	 * Returns the predicate child of this node, that is the predicate whose
	 * truth value is transformed into a boolean expression by this operator.
	 * 
	 * @return the predicate child
	 */
	public Predicate getPredicate() {
		return child;
	}
	
	@Override
	protected void toString(StringBuilder builder, boolean isRightChild,
			int parentTag, String[] boundNames, boolean withTypes) {

		builder.append("bool(");
		child.toString(builder, false, getTag(), boundNames, withTypes);
		builder.append(')');
	}

	@Override
	protected void toStringFullyParenthesized(StringBuilder builder,
			String[] boundNames) {

		builder.append("bool(");
		child.toStringFullyParenthesized(builder, boundNames);
		builder.append(')');
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
		return hasSameType(other)
				&& child.equals(((BoolExpression) other).child, withAlphaConversion);
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		child.typeCheck(result, quantifiedIdentifiers);
		setTemporaryType(result.makeBooleanType(), result);
	}
	
	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		return child.solveType(unifier);
	}
	
	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		final String typeName = getType()!=null?" [type: "+getType().toString()+"]":"";
		return tabs + this.getClass().getSimpleName() + " [bool]" + typeName
				+ "\n" + child.getSyntaxTree(boundNames, tabs + "\t");
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
	protected Expression bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		Predicate newChild = child.bindTheseIdents(binding, offset, factory);
		if (newChild == child) {
			return this;
		}
		return factory.makeBoolExpression(newChild, getSourceLocation());
	}

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = true;

		switch (getTag()) {
		case KBOOL: goOn = visitor.enterKBOOL(this); break;
		default:    assert false;
		}

		if (goOn) goOn = child.accept(visitor);
		
		switch (getTag()) {
		case KBOOL: return visitor.exitKBOOL(this);
		default:    return true;
		}
	}

	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitBoolExpression(this);
	}

	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return child.getWDPredicateRaw(formulaFactory);
	}

	@Override
	public Expression rewrite(IFormulaRewriter rewriter) {
		final Predicate newChild = child.rewrite(rewriter);
		final BoolExpression before;
		if (newChild == child) {
			before = this;
		} else {
			before = rewriter.getFactory().makeBoolExpression(newChild,
					getSourceLocation());
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
	protected Expression rewriteChild(int index, SingleRewriter rewriter) {
		if (index != 0) 
			throw new IllegalArgumentException("Position is outside the formula");
		Predicate newChild = rewriter.rewrite(child);
		return rewriter.factory.makeBoolExpression(newChild, getSourceLocation());
	}

}
