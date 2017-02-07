/*******************************************************************************
 * Copyright (c) 2005, 2017 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - add type visitor
 *     Systerel - store factory used to build a type
 *******************************************************************************/
package org.eventb.core.ast;

import java.util.HashSet;
import java.util.Set;

import org.eventb.internal.core.ast.Specialization;
import org.eventb.internal.core.ast.TypeRewriter;
import org.eventb.internal.core.ast.TypeTranslatabilityChecker;
import org.eventb.internal.core.typecheck.TypeVariable;

/**
 * Common protocol for event-B types.
 * <p>
 * In event-B, once type-checked, every expression has an associated type. This
 * type statically determines the set of all values the expression can take.
 * Hence, the type of an expression corresponds to the largest set to which the
 * expression can belong.
 * </p>
 * <p>
 * For implementation reasons, types are not represented as expressions but have
 * their own set of classes.
 * </p>
 * <p>
 * This class is not intended to be subclassed or instantiated directly by
 * clients. To create a new type, clients should use the methods provided by the
 * {@link FormulaFactory} class.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class Type {

	// Lazy cache for the equivalent expression
	private Expression expr = null;

	// Factory used to build the equivalent expression
	private final FormulaFactory fac;
	
	// True if this type doesn't contain any type variable
	private final boolean solved; 

	/**
	 * @since 3.0
	 */
	protected Type(FormulaFactory fac, boolean solved) {
		this(fac, solved, null);
	}
	
	/**
	 * @since 3.0
	 */
	protected Type(FormulaFactory fac, boolean solved, Expression expr) {
		this.fac = fac;
		this.solved = solved;
		this.expr = expr;
	}

	// Build the expression that denotes the set corresponding to this type
	protected abstract Expression buildExpression(FormulaFactory factory);

	// Build the string image of this type
	protected abstract void buildString(StringBuilder buffer);

    /**
	 * Indicates whether the given object denotes the same type as this one.
	 * <p>
	 * Comparison is done using deep equality, not reference equality.
	 * </p>
	 * 
	 * @param obj
	 *            the object with which to compare
	 * @return <code>true</code> if this type denotes the same type as the obj
	 *         argument, <code>false</code> otherwise
	 */
	@Override
	public abstract boolean equals(Object obj);
	
	@Override
	public abstract int hashCode();
	
	/**
	 * Returns whether this type is non-generic (doesn't contain any type variables).
	 * 
	 * @return <code>true</code> iff this type is non-generic
	 */
	public boolean isSolved() {
		return solved;
	}

	/**
	 * Returns the formula factory used to build this type.
	 * 
	 * @return the formula factory used to build this type
	 * @since 3.0
	 */
	public FormulaFactory getFactory() {
		return fac;
	}

	/**
	 * Ensures that the formula factory of each of the given types is the same
	 * as the formula factory of this type. Throws an
	 * {@link IllegalArgumentException} otherwise.
	 * 
	 * @param types
	 *            types to check
	 * @throws IllegalArgumentException
	 *             if any of the given types has a different formula factory
	 * @since 3.0
	 */
	protected void ensureSameFactory(Type[] types) {
		for (final Type type : types) {
			ensureSameFactory(type);
		}
	}

	/**
	 * Ensures that the formula factory of each of the given types is the same
	 * as the formula factory of this type. Throws an
	 * {@link IllegalArgumentException} otherwise.
	 * 
	 * @param left
	 *            type to check
	 * @param right
	 *            type to check
	 * @throws IllegalArgumentException
	 *             if any of the given types has a different formula factory
	 * @since 3.0
	 */
	protected void ensureSameFactory(Type left, Type right) {
		ensureSameFactory(left);
		ensureSameFactory(right);
	}

	/**
	 * Ensures that the formula factory of the given type is the same as the
	 * formula factory of this type. Throws an {@link IllegalArgumentException}
	 * otherwise.
	 * 
	 * @param other
	 *            type to check
	 * @throws IllegalArgumentException
	 *             if any of the given types has a different formula factory
	 * @since 3.0
	 */
	protected void ensureSameFactory(Type other) {
		final FormulaFactory otherFactory = other.getFactory();
		if (this.fac != otherFactory) {
			throw new IllegalArgumentException("The type " + other
					+ " has an incompatible factory: " + otherFactory
					+ " instead of: " + this.fac);
		}
	}

	/**
	 * Returns the expression that denotes the set corresponding to this type.
	 * 
	 * @return the set corresponding to this type
	 * @since 3.0
	 */
	public Expression toExpression() {
		if (expr == null) {
			expr = buildExpression(fac);
		}
		return expr;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buildString(buffer);
		return buffer.toString();
	}

	/**
	 * Returns the source type of this type, if any.
	 * <p>
	 * If this type looks like <code>ℙ(alpha × beta)</code>, this method will
	 * return <code>alpha</code>, otherwise <code>null</code>.
	 * </p>
	 * 
	 * @return the source type of this type, or <code>null</code>
	 */
	public Type getSource() {
		if (this instanceof PowerSetType) {
			Type baseType = ((PowerSetType) this).getBaseType();
			if (baseType instanceof ProductType) {
				return ((ProductType) baseType).getLeft();
			}
		}
		return null;
	}

	/**
	 * Returns the target type of this type, if any.
	 * <p>
	 * If this type looks like <code>ℙ(alpha × beta)</code>, this method will
	 * return <code>beta</code>, otherwise <code>null</code>.
	 * </p>
	 * 
	 * @return the target type of this type, or <code>null</code>
	 */
	public Type getTarget() {
		if (this instanceof PowerSetType) {
			Type baseType = ((PowerSetType) this).getBaseType();
			if (baseType instanceof ProductType) {
				return ((ProductType) baseType).getRight();
			}
		}
		return null;
	}

	/**
	 * Returns whether this type is a relational type.
	 * <p>
	 * This type is relational if it looks like <code>ℙ(alpha × beta)</code>.
	 * </p>
	 * 
	 * @return <code>true</code> iff this type is a relational type.
	 */
	protected boolean isRelational() {
		if (this instanceof PowerSetType) {
			Type baseType = ((PowerSetType) this).getBaseType();
			if (baseType instanceof ProductType) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the base type of this type, if any.
	 * <p>
	 * If this type looks like <code>ℙ(alpha)</code>, this method will
	 * return <code>alpha</code>, otherwise <code>null</code>.
	 * </p>
	 * 
	 * @return the base type of this type, or <code>null</code>.
	 */
	public Type getBaseType() {
		return null;
	}

	protected abstract void addGivenTypes(Set<GivenType> set);

	
	/**
	 * Returns a set of all given types which are used in this type.
	 * <p>
	 * Since 3.0 this type does not need anymore to be solved for using this
	 * method. If the type is not solved the method will return given types only
	 * for solved parts of the type. The type must be unsolved only during the
	 * type checking phase.
	 * </p>
	 * 
	 * @return a set containing all given types which are used in this formula
	 *         types
	 * @since 2.6
	 */
	public final Set<GivenType> getGivenTypes() {
		final HashSet<GivenType> result = new HashSet<GivenType>();
		this.addGivenTypes(result);
		return result;
	}
	
	/**
	 * Returns the type obtained by applying the given specialization to this
	 * type. Applying a specialization to a type has the effect of replacing all
	 * given types occurring in this type by their substitute as defined by the
	 * given specialization.
	 * <p>
	 * The given specialization may change as a side-effect of calling this
	 * method. Please see the documentation of {@link ISpecialization} for
	 * details.
	 * </p>
	 * <p>
	 * If the specialization does not have any effect on this type (i.e., no
	 * given type gets substituted), this method returns this type rather than a
	 * copy of it.
	 * </p>
	 * 
	 * @param specialization
	 *            the specialization to apply
	 * @return a specialization of this type or <code>this</code> if unchanged
	 * @throws IllegalArgumentException
	 *             if the given specialization is not compatible with this type,
	 *             that is the specialization contains a substitution for an
	 *             identifier with the same name as a given type in this type,
	 *             but with a different type
	 * @since 2.6
	 */
	public final Type specialize(ISpecialization specialization) {
		return ((Specialization) specialization).specialize(this);
	}

	/**
	 * Accepts the visit of this type with the given simple visitor.
	 * 
	 * @param visitor
	 *            the visitor to call back during traversal
	 * @since 2.7
	 */
	public abstract void accept(ITypeVisitor visitor);

	/**
	 * Tells whether this type can be translated to the given factory.
	 * <p>
	 * This type is translatable if and only if:
	 * <ul>
	 * <li>All the given types it contains are compatible with the given factory
	 * (their names are not reserved keywords);</li>
	 * <li>All parametric types it contains are supported by the given factory.</li>
	 * </ul>
	 * </p>
	 * 
	 * @return <code>true</code> iff this type can be translated to the given
	 *         factory
	 * @see #translate(FormulaFactory)
	 * @since 3.0
	 */
	public boolean isTranslatable(FormulaFactory ff) {
		return TypeTranslatabilityChecker.isTranslatable(this, ff);
	}

	/**
	 * Returns a copy of this type built with the given formula factory.
	 * <p>
	 * If the factory of this type and the given factory are equal, then this
	 * type is returned. Otherwise a new type object is built.
	 * </p>
	 * <p>
	 * Clients must ensure that this type can be translated to the given
	 * factory, prior to calling this method. If it is not translatable, an
	 * exception is raised.
	 * </p>
	 * <p>
	 * This operation is not supported by {@link TypeVariable}
	 * </p>
	 * 
	 * @param ff
	 *            the factory to use to build a copy of this type
	 * @return a type equivalent to this type and built with the given factory
	 * @throws IllegalArgumentException
	 *             if this type cannot be translated to the given factory
	 * @throws UnsupportedOperationException
	 *             if this type contains a type variable
	 * @see #isTranslatable(FormulaFactory)
	 * @since 3.0
	 */
	public Type translate(FormulaFactory ff) {
		if (fac == ff) {
			return this;
		}
		final TypeRewriter rewriter = new TypeRewriter(ff);
		return rewriter.rewrite(this);
	}

}
