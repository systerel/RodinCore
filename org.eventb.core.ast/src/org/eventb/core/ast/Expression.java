package org.eventb.core.ast;

import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeEnvironment;
import org.eventb.internal.core.typecheck.TypeUnifier;


/**
 * Common class for event-B expressions.
 * 
 * TODO: document Expression.
 * 
 * @author Laurent Voisin
 */
public abstract class Expression extends Formula<Expression> {

	protected Type type = null;

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
	
	protected final boolean finalizeType(boolean childrenOK, TypeUnifier unifier) {
		if (childrenOK == false) {
			type = null;
			return false;
		}
		if (type.isSolved()) {
			return true;
		}
		type = unifier.solve(type);
		if (type.isSolved()) {
			return true;
		}
		type = null;
		return false;
	}

	/**
	 * Returns the type of this expression.
	 * 
	 * @return the type of this expression. <code>null</code> if this 
	 * expression is ill-typed or typecheck has not been done yet
	 */
	public Type getType() {
		return type;
	}
	
	@Override
	protected Expression getTypedThis() {
		return this;
	}

	// Helper function for getSyntaxTree()
	protected final String getTypeName() {
		return type != null ? " [type: " + type + "]" : "";
	}

	protected boolean hasSameType(Formula other) {
		// By construction, other is also an expression
		Expression otherExpr = (Expression) other;
		return type == null ? otherExpr.type == null : type.equals(otherExpr.type);
	}
	
	@Override
	public boolean isTypeChecked() {
		return type != null;
	}

	/**
	 * Sets a type for this expression. This method should only be used by
	 * method {@link Formula#typeCheck(TypeEnvironment)}.
	 * <p>
	 * If the formula has already been type-checked, we just verify that the
	 * given type is unifiable with the already set type.
	 * </p>
	 * 
	 * @param type
	 *            the type of the formula
	 */
	protected void setType(Type type, TypeCheckResult result) {
		if (this.type == null) {
			this.type = type;
		} else if (result == null) {
			// Internal setting of type, outside of type-check
			assert this.type.equals(type);
		} else {
			result.unify(this.type, type, getSourceLocation());
		}
	}

}
