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
 * This class represents a predicate meta-variable in an event-B formula. Only
 * one AST tag corresponds to this node. For technical reasons (predicates and
 * expressions are distinct syntactic categories), predicate meta-variable's
 * attribute <code>name</code> starts with a leading symbol '$'.
 * 
 * @author Thomas Muller
 * @since 1.2
 * @noextend This class is not intended to be subclassed by clients.
 */
public class PredicateVariable extends Predicate {

	/**
	 * Unique tag identifying a predicate variable AST node.
	 * 
	 * @see Formula#PREDICATE_VARIABLE
	 */
	public static final int tag = PREDICATE_VARIABLE;

	/**
	 * Leading symbol used to distinguish predicate meta-variables from
	 * predicates and expressions.
	 */
	public static final String LEADING_SYMBOL = "$";

	// The name of the PredicateVariable including the leading symbol '$'
	private final String name;

	protected PredicateVariable(String name, SourceLocation location,
			FormulaFactory ff) {
		super(tag, location, name.hashCode());
		assert name != null;
		assert name.startsWith(LEADING_SYMBOL);
		this.name = name;
		setPredicateVariableCache(this);
		synthesizeType(ff);
	}

	public String getName() {
		return this.name;
	}

	@Override
	protected void synthesizeType(FormulaFactory ff) {
		this.freeIdents = NO_FREE_IDENT;
		this.boundIdents = NO_BOUND_IDENT;
		typeChecked = true;
	}

	@Override
	protected void isLegible(LegibilityResult result,
			BoundIdentDecl[] quantifiedIdents) {
		// Nothing to do, this sub-formula is always legible.
	}

	@Override
	protected boolean equals(Formula<?> other, boolean withAlphaConversion) {
		if (this.getTag() != other.getTag()) {
			return false;
		}
		return name.equals(((PredicateVariable) other).name);
	}

	@Override
	protected void typeCheck(TypeCheckResult result,
			BoundIdentDecl[] quantifiedIdentifiers) {
		// Always well-typed
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		return tabs + this.getClass().getSimpleName() + " [" + this.name + "]"
				+ "\n";
	}

	@Override
	protected void collectFreeIdentifiers(
			LinkedHashSet<FreeIdentifier> freeIdentSet) {
		// Nothing to collect
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames,
			int offset) {
		// Nothing to collect
	}

	@Override
	protected Predicate bindTheseIdents(Map<String, Integer> binding,
			int offset, FormulaFactory factory) {
		return this;
	}

	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return formulaFactory.makeLiteralPredicate(BTRUE, null);
	}

	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		return true;
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		// Nothing to add
	}

	@Override
	protected void getPositions(IFormulaFilter filter, IntStack indexes,
			List<IPosition> positions) {
		if (!(filter instanceof IFormulaFilter2)) {
			throw new IllegalArgumentException(
					"The given filter shall support predicate variables");
		}
		if (((IFormulaFilter2) filter).select(this)) {
			positions.add(new Position(indexes));
		}
	}

	@Override
	protected Formula<?> getChild(int index) {
		return null;
	}

	@Override
	protected IPosition getDescendantPos(SourceLocation sloc, IntStack indexes) {
		return new Position(indexes);
	}

	@Override
	protected Predicate rewriteChild(int index, SingleRewriter rewriter) {
		throw new IllegalArgumentException("Position is outside the formula");
	}

	@Override
	public boolean accept(IVisitor visitor) {
		if (!(visitor instanceof IVisitor2)) {
			throw new IllegalArgumentException(
					"The given visitor shall support predicate variables");
		}
		return ((IVisitor2) visitor).visitPREDICATE_VARIABLE(this);
	}

	@Override
	public void accept(ISimpleVisitor visitor) {
		if (!(visitor instanceof ISimpleVisitor2)) {
			throw new IllegalArgumentException(
					"The given visitor shall support predicate variables");
		}
		((ISimpleVisitor2) visitor).visitPredicateVariable(this);
	}

	@Override
	public Predicate rewrite(IFormulaRewriter rewriter) {
		if (!(rewriter instanceof IFormulaRewriter2)) {
			throw new IllegalArgumentException(
					"The given rewriter shall support predicate variables");
		}
		return ((IFormulaRewriter2) rewriter).rewrite(this);
	}
}
