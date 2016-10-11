/*******************************************************************************
 * Copyright (c) 2014, 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.extension;

import java.util.Arrays;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IFormulaExtension;

/**
 * A class which allows to uniquely identify an operator occurrence by
 * identifying its arguments and return type.
 * 
 * @author Thomas Muller
 * @author Laurent Voisin
 */
public abstract class ExtensionSignature {

	/**
	 * Returns the signature of a parametric type occurrence.
	 * 
	 * @param src
	 *            some parametric type
	 * @return the signature of the parametric type
	 */
	public static ExpressionExtSignature getSignature(ParametricType src) {
		return new ExpressionExtSignature(src);
	}

	/**
	 * Returns the signature of an expression extension occurrence.
	 * 
	 * @param src
	 *            some extended expression
	 * @return the signature of the extension at the root of the given formula
	 */
	public static ExpressionExtSignature getSignature(ExtendedExpression src) {
		return new ExpressionExtSignature(src);
	}

	/**
	 * Returns the signature of a predicate extension occurrence.
	 * 
	 * @param src
	 *            some extended predicate
	 * @return the signature of the extension at the root of the given formula
	 */
	public static PredicateExtSignature getSignature(ExtendedPredicate src) {
		return new PredicateExtSignature(src);
	}

	private static Type[] getChildTypes(IExtendedFormula src) {
		final Expression[] childExprs = src.getChildExpressions();
		final int length = childExprs.length;
		final Type[] childTypes = new Type[length];
		for (int i = 0; i < length; i++) {
			childTypes[i] = childExprs[i].getType();
		}
		return childTypes;
	}

	private static Type[] makePowerSetTypes(Type[] src) {
		if (src.length == 0) {
			return src;
		}
		final FormulaFactory fac = src[0].getFactory();
		final Type[] result = new Type[src.length];
		for (int i = 0; i < src.length; i++) {
			result[i] = fac.makePowerSetType(src[i]);
		}
		return result;
	}

	private static final int PRIME = 31;

	// The formula factory for this signature, i.e., the factory containing the
	// extension.
	protected final FormulaFactory factory;

	// The extension definition corresponding to this signature
	protected final IFormulaExtension extension;

	// Number of child predicates
	private final int numberOfPredicates;

	// Type of child expressions
	private final Type[] childTypes;

	protected ExtensionSignature(IExtendedFormula src) {
		this.factory = ((Formula<?>) src).getFactory();
		this.extension = src.getExtension();
		this.numberOfPredicates = src.getChildPredicates().length;
		this.childTypes = getChildTypes(src);
	}

	protected ExtensionSignature(ParametricType src) {
		this.factory = src.getFactory();
		this.extension = src.getExprExtension();
		this.numberOfPredicates = 0;
		this.childTypes = makePowerSetTypes(src.getTypeParameters());
	}

	// For testing purposes
	protected ExtensionSignature(FormulaFactory factory,
			IFormulaExtension extension, int numberOfPredicates,
			Type[] childTypes) {
		this.factory = factory;
		this.extension = extension;
		this.numberOfPredicates = numberOfPredicates;
		this.childTypes = childTypes;
	}

	/**
	 * Returns the symbol associated to this signature. This symbol might not be
	 * a valid identifier.
	 * 
	 * @return the symbol associated to this signature
	 */
	public String getSymbol() {
		return extension.getSyntaxSymbol();
	}

	/**
	 * Returns whether the extension with this signature is a type constructor.
	 * 
	 * @return whether this signature corresponds to a type constructor
	 */
	public abstract boolean isATypeConstructor();

	/**
	 * Returns whether this signature is atomic, that is denotes an operator
	 * that does not have any child.
	 * 
	 * @return whether this signature is atomic
	 */
	public boolean isAtomic() {
		return numberOfPredicates == 0 && childTypes.length == 0;
	}

	/**
	 * Returns the type of a function that could be used to replace an
	 * occurrence of the extension with this signature.
	 * 
	 * @return the type of a replacement function
	 */
	public Type getFunctionalType(FunctionalTypeBuilder builder) {
		return builder.makeFunctionalType(childTypes, numberOfPredicates,
				getReturnType());
	}

	/**
	 * Returns the type of a relation that could be used to replace an
	 * occurrence of the extension with this signature. To be used in the
	 * special case of type constructors.
	 * 
	 * @return the type of a replacement relation
	 */
	public Type getRelationalType(FunctionalTypeBuilder builder) {
		assert isATypeConstructor();
		return builder.makeRelationalType(childTypes, getReturnType());
	}

	// What is the range type of the operator ?
	protected abstract Type getReturnType();

	@Override
	public int hashCode() {
		int result = 1;
		result = PRIME * result + extension.hashCode();
		result = PRIME * result + numberOfPredicates;
		result = PRIME * result + Arrays.hashCode(childTypes);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ExtensionSignature other = (ExtensionSignature) obj;
		return this.extension.equals(other.extension)
				&& this.numberOfPredicates == other.numberOfPredicates
				&& Arrays.equals(this.childTypes, other.childTypes);
	}

	public static class PredicateExtSignature extends ExtensionSignature {

		public PredicateExtSignature(ExtendedPredicate src) {
			super(src);
		}

		// For testing purposes
		public PredicateExtSignature(FormulaFactory factory,
				IFormulaExtension extension, int numberOfPredicates,
				Type[] childTypes) {
			super(factory, extension, numberOfPredicates, childTypes);
		}

		@Override
		protected Type getReturnType() {
			return factory.makeBooleanType();
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
		}

	}

	public static class ExpressionExtSignature extends ExtensionSignature {

		// Type of the resulting expression
		private final Type returnType;

		public ExpressionExtSignature(ExtendedExpression src) {
			super(src);
			if (src.getType() == null) {
				throw new NullPointerException();
			}
			this.returnType = src.getType();
		}

		public ExpressionExtSignature(ParametricType src) {
			super(src);
			this.returnType = factory.makePowerSetType(src);
		}

		// For testing purposes
		public ExpressionExtSignature(FormulaFactory factory,
				IFormulaExtension extension, Type returnType,
				int numberOfPredicates, Type[] childTypes) {
			super(factory, extension, numberOfPredicates, childTypes);
			if (returnType == null) {
				throw new NullPointerException();
			}
			this.returnType = returnType;
		}

		@Override
		protected Type getReturnType() {
			return returnType;
		}

		@Override
		public boolean equals(Object obj) {
			if (!super.equals(obj)) {
				return false;
			}
			final ExpressionExtSignature other = (ExpressionExtSignature) obj;
			return returnType.equals(other.returnType);
		}

		@Override
		public int hashCode() {
			int result = super.hashCode();
			result = PRIME * result + returnType.hashCode();
			return result;
		}

		@Override
		public boolean isATypeConstructor() {
			return ((IExpressionExtension) extension).isATypeConstructor();
		}

	}

}
