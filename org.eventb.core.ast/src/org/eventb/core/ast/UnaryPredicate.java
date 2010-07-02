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

import static org.eventb.internal.core.parser.BMath.NOT_PRED;
import static org.eventb.internal.core.parser.SubParsers.NOT_PARSER;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.parser.BMath;
import org.eventb.internal.core.parser.GenParser.OverrideException;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * UnaryPredicate is the base class for all unary predicates in an event-B formula.
 * <p>
 * It can accept tags {NOT}.
 * </p>
 * 
 * @author François Terrier
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class UnaryPredicate extends Predicate {
	
	protected final Predicate child;

	// offset in the corresponding tag interval
	protected static final int firstTag = FIRST_UNARY_PREDICATE;
	protected static final String[] tags = {
		"\u00ac" // NOT
	};
	// For testing purposes
	public static final int TAGS_LENGTH = tags.length;

	private static final String NOT_ID = "Not";

	/**
	 * @since 2.0
	 */
	public static void init(BMath grammar) {
		try {		
			grammar.addOperator("\u00ac", NOT_ID, NOT_PRED, NOT_PARSER);
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected UnaryPredicate(Predicate child, int tag, SourceLocation location,
			FormulaFactory ff) {

		super(tag, location, child.hashCode());
		this.child = child;
		
		assert tag >= firstTag && tag < firstTag+tags.length;
		assert child != null;
		
		setPredicateVariableCache(this.child);
		synthesizeType(ff);
	}

	@Override
	protected void synthesizeType(FormulaFactory ff) {
		this.freeIdents = child.freeIdents;
		this.boundIdents = child.boundIdents;
		
		if (! child.isTypeChecked())
			return;
		typeChecked = true;
	}
	
	/**
	 * Returns the child of this node.
	 * 
	 * @return the child predicate of this node
	 */
	public Predicate getChild() {
		return child;
	}
	
	protected String getTagOperator() {
		return tags[getTag()-firstTag];
	}
	
	@Override
	protected boolean equals(Formula<?> other, boolean withAlphaConversion) {
		if (this.getTag() != other.getTag()) {
			return false;
		}
		UnaryPredicate temp = (UnaryPredicate) other;
		return child.equals(temp.child, withAlphaConversion);
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		child.typeCheck(result, quantifiedIdentifiers);
	}
	
	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		return child.solveType(unifier);
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		return tabs + this.getClass().getSimpleName() + " [" + getTagOperator()	+ "]\n"
		+ child.getSyntaxTree(boundNames, tabs + "\t");
	}

	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		child.isLegible(result, quantifiedIdents);
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
		Predicate newChild = child.bindTheseIdents(binding, offset, factory);
		if (newChild == child) {
			return this;
		}
		return factory.makeUnaryPredicate(getTag(), newChild, getSourceLocation());
	}

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = true;

		switch (getTag()) {
		case NOT: goOn = visitor.enterNOT(this); break;
		default:  assert false;
		}

		if (goOn) goOn = child.accept(visitor);
		
		switch (getTag()) {
		case NOT: return visitor.exitNOT(this);
		default:  return true;
		}
	}

	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitUnaryPredicate(this);		
	}

	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return child.getWDPredicateRaw(formulaFactory);
	}

	@Override
	public Predicate rewrite(IFormulaRewriter rewriter) {
		final Predicate newChild = child.rewrite(rewriter);
		final UnaryPredicate before;
		if (newChild == child) {
			before = this;
		} else {
			before = rewriter.getFactory().makeUnaryPredicate(getTag(),
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
		Predicate newChild = rewriter.rewrite(child);
		return rewriter.factory.makeUnaryPredicate(getTag(), newChild,
				getSourceLocation());
	}

}
