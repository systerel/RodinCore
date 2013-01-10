/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
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
	 * @param tag node tag of this expression
	 * @param location source location of this expression
	 * @param hashCode combined hash code for children
	 */
	protected Expression(int tag, SourceLocation location, int hashCode) {
		super(tag, location, hashCode);
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
	 * @param factory
	 *            the formula factory to use for building the identifiers
	 * @return <code>true</code> iff the identifier cache is still type-checked
	 * @since 3.0
	 */
	protected final boolean mergeGivenTypes(Type aType, FormulaFactory factory) {
		final FreeIdentifier[] newIdents = getGivenTypeIdentifiers(aType,
				factory);
		if (newIdents.length == 0) {
			// Nothing new
			return true;
		}
		final IdentListMerger merger = makeMerger(freeIdents, newIdents);
		this.freeIdents = merger.getFreeMergedArray();
		return !merger.containsError();
	}

	protected final void setFinalType(Type synType, Type givenType) {
		assert synType != null;
		assert synType.isSolved();
		assert givenType == null || givenType.equals(synType);
		type = synType;
		typeChecked = true;
	}

	/**
	 * Returns the type of this expression.
	 * 
	 * @return the type of this expression. <code>null</code> if this 
	 * expression is ill-typed or typecheck has not been done yet
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
	 * Sets the type at the given type value. Must be only used to set temporary
	 * a type before a call to {@link #setFinalType(Type, Type)} method. If not
	 * the type must be set to precedent value at the end of the temporary treatment.
	 * 
	 * This method should only be used by method
	 * {@link FreeIdentifier#synthesizeType(FormulaFactory, Type)}.
	 * 
	 * @param type
	 *            the given type to set
	 * @return the precedent value type
	 * @since 3.0
	 */
	protected final Type setTemporaryType(Type type){
		Type old_type = this.type;
		this.type = type;
		return old_type;
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
	 * @see Type#toExpression(FormulaFactory)
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
	 */
	public final ITypeCheckResult typeCheck(
			ITypeEnvironment environment,
			Type expectedType) {
		
		TypeCheckResult result = new TypeCheckResult(environment.makeSnapshot());
		boolean wasTypeChecked = isTypeChecked();
		typeCheck(result, NO_BOUND_IDENT_DECL);
		result.unify(getType(), expectedType, this);
		result.solveTypeVariables();
		if (! wasTypeChecked) {
			solveType(result.getUnifier());
		}
		return result;
	}

	@Override
	protected final boolean solveType(TypeUnifier unifier) {
		if (isTypeChecked()) {
			return true;
		}
		if (type == null) {
			// Shared node, already solved (and failed).
			return false;
		}
		Type inferredType = unifier.solve(type);
		type = null;
		boolean success = inferredType != null && inferredType.isSolved();
		success &= solveChildrenTypes(unifier);
		if (success) {
			synthesizeType(unifier.getFormulaFactory(), inferredType);
		}else{
			synthesizeType(unifier.getFormulaFactory(), null);
		}
		return isTypeChecked();
	}

	// Calls recursively solveType on each child of this node and
	// returns true if all calls where successful.
	protected abstract boolean solveChildrenTypes(TypeUnifier unifier);

	@Override
	protected final Expression getCheckedReplacement(SingleRewriter rewriter) {
		return rewriter.getExpression(this);
	}

}
