/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - generalised getPositions() into inspect()
 *     Systerel - added child indexes
 *******************************************************************************/
package org.eventb.core.ast;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.eventb.internal.core.ast.FindingAccumulator;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * Common implementation for event-B assignments.
 * <p>
 * There are various kinds of assignments which are implemented in sub-classes
 * of this class. The commonality between these assignments is that they are
 * formed of two parts: a left-hand side and a right hand-side. The left-hand side,
 * that is a list of free identifiers, is implemented in this class, while the
 * right-hand side is implemented in subclasses.
 * </p>
 * <p>
 * This class is not intended to be subclassed by clients.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class Assignment extends Formula<Assignment> {

	protected final FreeIdentifier[] assignedIdents;
	
	/**
	 * Creates a new assignment with the given arguments.
	 * 
	 * @param tag node tag of this expression
	 * @param location source location of this expression
	 * @param hashCode combined hash code for children
	 * @param assignedIdent free identifier that constitute the left-hand side
	 */
	protected Assignment(int tag, SourceLocation location, int hashCode, 
			FreeIdentifier assignedIdent) {
		
		super(tag, location, combineHashCodes(assignedIdent.hashCode(), hashCode));
		this.assignedIdents = new FreeIdentifier[] {assignedIdent};
	}

	/**
	 * Creates a new assignment with the given arguments.
	 * 
	 * @param tag node tag of this expression
	 * @param location source location of this expression
	 * @param hashCode combined hash code for children
	 * @param assignedIdents array of free identifiers that constitute the left-hand side
	 */
	protected Assignment(int tag, SourceLocation location, int hashCode, 
			FreeIdentifier[] assignedIdents) {
		
		super(tag, location, combineHashCodes(combineHashCodes(assignedIdents), hashCode));
		this.assignedIdents = assignedIdents.clone();
	}

	/**
	 * Creates a new assignment with the given arguments.
	 * 
	 * @param tag node tag of this expression
	 * @param location source location of this expression
	 * @param hashCode combined hash code for children
	 * @param assignedIdents array of free identifiers that constitute the left-hand side
	 */
	protected Assignment(int tag, SourceLocation location, int hashCode,
			Collection<FreeIdentifier> assignedIdents) {
		
		super(
				tag,
				location,
				combineHashCodes(
						combineHashCodes((Collection<? extends Expression>) assignedIdents),
						hashCode));
		this.assignedIdents = assignedIdents.toArray(new FreeIdentifier[assignedIdents.size()]);
	}

	
	protected final void appendAssignedIdents(StringBuilder result) {
		boolean comma = false;
		for (FreeIdentifier ident : assignedIdents) {
			if (comma)
				result.append(',');
			comma = true;
			result.append(ident.getName());
		}
	}
	
	@Override
	protected final Assignment bindTheseIdents(Map<String, Integer> binding, int offset,
			FormulaFactory factory) {
		// Should never happen
		assert false;
		return this;
	}

	@Override
	protected final boolean solveType(TypeUnifier unifier) {
		if (isTypeChecked()) {
			return true;
		}
		boolean success = solveChildrenTypes(unifier);
		for (FreeIdentifier ident: assignedIdents) {
			success &= ident.solveType(unifier);
		}
		if (success) {
			synthesizeType(unifier.getFormulaFactory());
		}
		return isTypeChecked();
	}

	// Calls recursively solveType on each child of this node and
	// returns true if all calls where successful.
	protected abstract boolean solveChildrenTypes(TypeUnifier unifier);

	/**
	 * Return the left-hand side of this assignment.
	 * 
	 * @return an array of the free identifiers that make up the left-hand side
	 *         of this assignment
	 */
	public final FreeIdentifier[] getAssignedIdentifiers() {
		return assignedIdents.clone();
	}
	
	protected final String getSyntaxTreeLHS(String[] boundNames, String tabs) {
		StringBuilder builder = new StringBuilder();
		for (FreeIdentifier ident: assignedIdents) {
			builder.append(ident.getSyntaxTree(boundNames, tabs));
		}
		return builder.toString();
	}
		
	@Override
	protected final Assignment getTypedThis() {
		return this;
	}

	protected final boolean hasSameAssignedIdentifiers(Assignment other) {
		return Arrays.equals(assignedIdents, other.assignedIdents);
	}

	@Override
	public final Assignment rewrite(IFormulaRewriter rewriter) {
		throw new UnsupportedOperationException("Assignments cannot be rewritten");
	}
	
	@Override
	protected final Assignment rewrite(ITypedFormulaRewriter rewriter) {
		throw new UnsupportedOperationException("Assignments cannot be rewritten");
	}
	
	/**
	 * Returns the (flattened) feasibility predicate of this assignment. An
	 * exception is thrown if this assignment was not type checked.
	 * 
	 * @param formulaFactory
	 *            factory to use for creating the predicate
	 * @return Returns the feasibility predicate
	 */
	public final Predicate getFISPredicate(FormulaFactory formulaFactory) {
		assert isTypeChecked();
		return getFISPredicateRaw(formulaFactory).flatten(formulaFactory);
	}
	
	protected abstract Predicate getFISPredicateRaw(FormulaFactory formulaFactory);
	
	/**
	 * Returns the (flattened) before-after predicate of this assignment. An
	 * exception is thrown if this assignment was not type checked.
	 * 
	 * @param formulaFactory
	 *            factory to use for creating the predicate
	 * @return Returns the before-after predicate of this assignment
	 */
	public final Predicate getBAPredicate(FormulaFactory formulaFactory) {
		assert isTypeChecked();
		return getBAPredicateRaw(formulaFactory).flatten(formulaFactory);
	}
	
	protected abstract Predicate getBAPredicateRaw(FormulaFactory formulaFactory);

	
	/**
	 * Returns an array of the free identifiers that occur on the right-hand
	 * side of this assignment. The free identifiers are extracted using
	 * {@link Formula#getFreeIdentifiers()} applied to all formulas that are
	 * part of the right-hand side of this assignment.
	 * 
	 * @return all free identifiers that occur in the right-hand side of this
	 *         assignment.
	 */
	public abstract FreeIdentifier[] getUsedIdentifiers();
	
	protected abstract void synthesizeType(FormulaFactory ff);

	@Override
	protected final <F> void inspect(FindingAccumulator<F> acc) {
		throw new UnsupportedOperationException(
				"Assignments cannot be rewritten");
	}

	@Override
	public final Formula<?> getChild(int index) {
		throw new UnsupportedOperationException(
				"Assignments cannot be rewritten");
	}

	@Override
	public final int getChildCount() {
		throw new UnsupportedOperationException(
				"Assignments cannot be rewritten");
	}

	@Override
	protected final IPosition getDescendantPos(SourceLocation sloc,
			IntStack indexes) {
		throw new UnsupportedOperationException(
				"Assignments cannot be rewritten");
	}

	@Override
	protected final Assignment rewriteChild(int index, SingleRewriter rewriter) {
		throw new UnsupportedOperationException(
				"Assignments cannot be rewritten");
	}

	@Override
	protected final Assignment getCheckedReplacement(SingleRewriter rewriter) {
		throw new UnsupportedOperationException(
				"Assignments cannot be rewritten");
	}

	/**
	 * @since 2.6
	 */
	@Override
	public Assignment specialize(ISpecialization specialisation) {
		throw new UnsupportedOperationException(
				"Assignments cannot be specialized");
	}

}
