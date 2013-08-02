/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.datatype;

import static org.eventb.core.ast.Formula.CPROD;
import static org.eventb.core.ast.Formula.POW;

import java.util.HashMap;
import java.util.Map;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeVisitor;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.ISetInstantiation;
import org.eventb.core.ast.extension.IExpressionExtension;

/**
 * Represents a set built from a datatype type constructor, where type
 * parameters are associated with some arbitrary sets. Instances are intended to
 * be used for performing set substitution on datatype extensions.
 * 
 * Implementation note: When substituting types, we could have first translated
 * the type to an expression and then perform the substitution on that
 * expression. However, this would perform a substitution that changes types and
 * factory, which is not publicly available (it is a protected method in class
 * Formula). Hence, substitution is instead performed by traversing the type
 * using a visitor.
 * 
 * @author Laurent Voisin
 */
public class SetSubstitution implements ISetInstantiation, ITypeVisitor {

	/**
	 * Returns a substitution appropriate for substituting sets. The actual set
	 * parameters are inferred from the proposed set, if possible.
	 * 
	 * @param datatype
	 *            a datatype
	 * @param proposedSet
	 *            a proposed instance of a datatype set
	 * @return a substitution that can be applied to constructor arguments
	 * @throws IllegalArgumentException
	 *             if the proposed set is invalid
	 */
	public static SetSubstitution makeSubstitution(Datatype datatype,
			Expression proposedSet) {
		if (!(proposedSet instanceof ExtendedExpression)) {
			throw new IllegalArgumentException("Not an extended expression: "
					+ proposedSet);
		}
		final ExtendedExpression instance = (ExtendedExpression) proposedSet;
		final IExpressionExtension ext = instance.getExtension();
		if (!(ext instanceof TypeConstructorExtension)) {
			throw new IllegalArgumentException(
					"Not built with a type constructor: " + proposedSet);
		}
		if (!datatype.equals(ext.getOrigin())) {
			throw new IllegalArgumentException(
					"Built with a type constructor of another datatype: "
							+ proposedSet);
		}
		return new SetSubstitution(instance);
	}

	private static final Predicate[] NO_PREDS = new Predicate[0];

	// The datatype set
	private final Datatype datatype;

	// The datatype set
	private final ExtendedExpression set;

	// The formula factory to use for building expressions
	private final FormulaFactory factory;

	// A map from type parameter names and datatype name to their instance
	private final Map<String, Expression> map = new HashMap<String, Expression>();

	// Current result during visit of a type
	private Expression result;

	public SetSubstitution(ExtendedExpression set) {
		this.set = set;
		this.factory = set.getFactory();
		final TypeConstructorExtension tcons = (TypeConstructorExtension) set
				.getExtension();
		this.datatype = tcons.getOrigin();
		map.put(tcons.getName(), set);
		final String[] formalNames = tcons.getFormalNames();
		final int nbParams = formalNames.length;
		final Expression[] actuals = set.getChildExpressions();
		assert actuals.length == nbParams;
		for (int i = 0; i < nbParams; i++) {
			map.put(formalNames[i], actuals[i]);
		}
	}

	@Override
	public Datatype getOrigin() {
		return datatype;
	}

	@Override
	public ExtendedExpression getInstanceSet() {
		return set;
	}

	public Expression substitute(Type argType) {
		argType.accept(this);
		return result;
	}

	public Expression[] substitute(Type[] argTypes) {
		final int length = argTypes.length;
		final Expression[] exprs = new Expression[length];
		for (int i = 0; i < length; i++) {
			exprs[i] = substitute(argTypes[i]);
		}
		return exprs;
	}

	private Expression defaultTranslate(Type type) {
		return type.toExpression().translate(factory);
	}

	@Override
	public void visit(BooleanType type) {
		result = defaultTranslate(type);
	}

	@Override
	public void visit(GivenType type) {
		final String name = type.getName();
		final Expression repl = map.get(name);
		if (repl == null) {
			result = defaultTranslate(type);
		} else {
			result = repl;
		}
	}

	@Override
	public void visit(IntegerType type) {
		result = defaultTranslate(type);
	}

	@Override
	public void visit(ParametricType type) {
		final IExpressionExtension ext = type.getExprExtension();
		final Type[] tparams = type.getTypeParameters();
		final Expression[] eparams = substitute(tparams);
		result = factory.makeExtendedExpression(ext, eparams, NO_PREDS, null);
	}

	@Override
	public void visit(PowerSetType type) {
		final Expression expr = substitute(type.getBaseType());
		result = factory.makeUnaryExpression(POW, expr, null);
	}

	@Override
	public void visit(ProductType type) {
		final Expression left = substitute(type.getLeft());
		final Expression right = substitute(type.getRight());
		result = factory.makeBinaryExpression(CPROD, left, right, null);
	}

}
