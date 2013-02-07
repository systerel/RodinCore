/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - add given sets to free identifier cache
 *     Systerel - store factory used to build a formula
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.core.ast.QuantifiedHelper.sameType;
import static org.eventb.internal.core.ast.GivenTypeHelper.getGivenTypeIdentifiers;
import static org.eventb.internal.core.ast.IdentListMerger.makeMerger;

import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * Common class for event-B expressions.
 * 
 * TODO: document Expression.
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class Expression extends Formula<Expression> {

	private Type type = null;

	/**
	 * Creates a new expression with the specified tag and source location.
	 * 
	 * @param tag
	 *            node tag of this expression
	 * @param ff
	 *            the formula factory used to build this expression
	 * @param location
	 *            source location of this expression
	 * @param hashCode
	 *            combined hash code for children
	 * @since 3.0
	 */
	protected Expression(int tag, FormulaFactory ff,
			SourceLocation location, int hashCode) {
		super(tag, ff, location, hashCode);
	}
	
	protected abstract void synthesizeType(FormulaFactory ff, Type givenType);

	/**
	 * Adds the free identifiers for the GivenTypes occurring in the given type
	 * into the free identifier cache. Returns whether these given types are
	 * compatible with the current contents of the cache.
	 * <p>
	 * Must be called only by <code>synthesizeType()</code>.
	 * </p>
	 * 
	 * @param aType
	 *            the type we are about to set on this formula
	 * @return <code>true</code> iff the identifier cache is still type-checked
	 * @since 3.0
	 */
	protected final boolean mergeGivenTypes(Type aType) {
		final FreeIdentifier[] newIdents = getGivenTypeIdentifiers(aType);
		if (newIdents.length == 0) {
			// Nothing new
			return true;
		}
		final IdentListMerger merger = makeMerger(freeIdents, newIdents);
		this.freeIdents = merger.getFreeMergedArray();
		return !merger.containsError();
	}

	protected final void setFinalType(Type synType, Type proposedType) {
		assert synType != null;
		assert synType.isSolved();
		if (proposedType != null && proposedType.equals(synType)) {
			// We prefer the type coming from the client
			synType = proposedType;
		}
		type = synType;
		typeChecked = true;
	}

	/**
	 * Returns the type of this expression if it is type-checked, or
	 * <code>null</code> otherwise. Once the type of this expression is known,
	 * it will never change.
	 * 
	 * @return the type of this expression or <code>null</code>
	 * @see #isTypeChecked()
	 */
	public final Type getType() {
		return type;
	}
	
	@Override
	protected final Expression getTypedThis() {
		return this;
	}

	// Helper function for getSyntaxTree()
	protected final String getTypeName() {
		return type != null ? " [type: " + type + "]" : "";
	}

	@Override
	protected final boolean equalsInternal(Formula<?> formula) {
		final Expression other = (Expression) formula;
		return sameType(type, other.type) && equalsInternalExpr(other);
	}

	 // Just verify equality of children, types have already been checked.
	abstract boolean equalsInternalExpr(Expression other);

	/**
	 * Sets a temporary type for this expression. This method should only be
	 * used by method {@link Formula#typeCheck(ITypeEnvironment)}.
	 * <p>
	 * If the formula has already been type-checked, we just verify that the
	 * given type is unifiable with the already set type.
	 * </p>
	 * 
	 * @param type
	 *            the type of the formula
	 */
	protected final void setTemporaryType(Type type, TypeCheckResult result) {
		if (this.type == null) {
			this.type = type;
		} else {
			result.unify(this.type, type, this);
		}
	}
	
	/**
	 * Returns whether this expression denotes a type.
	 * 
	 * @return <code>true</code> iff this expression denotes a type.
	 */
	public boolean isATypeExpression() {
		// default case, involved subclasses will override this.
		return false;
	}
	
	
	/**
	 * Returns the type corresponding to this type-checked expression.
	 * <p>
	 * The returned type is <em>not</em> the type of this expression.
	 * </p>
	 * 
	 * @param factory
	 *            factory to use for building the result; it is guaranteed to
	 *            recognize all extensions contained in this expression
	 * @return the type represented by this expression
	 * @throws InvalidExpressionException
	 *             when this expression doesn't denote a type
	 * @see Type#toExpression()
	 * @see #isATypeExpression()
	 */
	protected Type toType(FormulaFactory factory) throws InvalidExpressionException {
		throw new InvalidExpressionException();
	}
	
	/**
	 * @since 2.0
	 */
	public final Type toType() throws InvalidExpressionException {
		return toType(getFactory());
	}
	
	/**
	 * Statically type-checks this expression, whose expected type is known.
	 * <p>
	 * Returns the {@link TypeCheckResult} containing all the informations about
	 * the type-check run.
	 * </p>
	 * 
	 * @param environment
	 *            an initial type environment
	 * @param expectedType
	 *            expected type of this expression
	 * @return the result of the type checker
	 * @throws IllegalStateException
	 *             if this formula is not well-formed
	 * @throws IllegalArgumentException
	 *             if the given type or type environment has been built with a
	 *             different formula factory
	 * @see #isWellFormed()
	 */
	public final ITypeCheckResult typeCheck(
			ITypeEnvironment environment,
			Type expectedType) {
		if (!isWellFormed()) {
			throw new IllegalStateException(
					"Cannot typecheck ill-formed expression: " + this);
		}
		ensureSameFactory(environment);
		ensureSameFactory(expectedType);
		TypeCheckResult result = new TypeCheckResult(environment.makeSnapshot());
		boolean wasTypeChecked = isTypeChecked();
		typeCheck(result, NO_BOUND_IDENT_DECL);
		result.unify(getType(), expectedType, this);
		result.analyzeType(expectedType, this);
		result.solveTypeVariables();
		if (! wasTypeChecked) {
			solveType(result.getUnifier());
		}
		// Ensure that we are consistent:
		// Success reported implies that this node is type-checked and bears
		// the given expected type
		assert !result.isSuccess()
				|| (typeChecked && getType().equals(expectedType));
		return result;
	}

	@Override
	protected final void solveType(TypeUnifier unifier) {
		if (isTypeChecked() || type == null) {
			// Already done or shared node, already solved (and failed).
			return;
		}
		Type inferredType = unifier.solve(type);
		type = null;
		solveChildrenTypes(unifier);
		if (inferredType != null && inferredType.isSolved()) {
			synthesizeType(unifier.getFormulaFactory(), inferredType);
		} else {
			synthesizeType(unifier.getFormulaFactory(), null);
		}
	}

	/**
	 * @since 3.0
	 */
	// Calls recursively solveType on each child of this node.
	protected abstract void solveChildrenTypes(TypeUnifier unifier);

	@Override
	protected final Expression getCheckedReplacement(SingleRewriter rewriter) {
		return rewriter.getExpression(this);
	}

}
