/*******************************************************************************
 * Copyright (c) 2025 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.datatype;

import static java.util.stream.IntStream.range;
import static org.eventb.core.ast.Formula.CPROD;
import static org.eventb.core.ast.Formula.POW;

import java.util.HashMap;
import java.util.Map;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeVisitor;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.datatype.ITypeInstantiation;

/**
 * Implementation of a set substitution used by {@link DatatypeTranslator} for
 * building axioms.
 * <p>
 * This class is similar to {@link SetSubstitution}, but performs datatype
 * translation at the same time as instantiation.
 *
 * @author Laurent Voisin
 */
public class TranslatingSetSubstitution implements ITypeVisitor {

	protected static final Predicate[] NO_PREDS = new Predicate[0];

	private final DatatypeTranslation translation;
	private final ITypeInstantiation srcInst;
	private final Map<String, Expression> subst;
	private final FormulaFactory factory;

	// Fields used during recursion to simulate a different signature
	Type inputSrcType; // Additional input
	Expression result; // Current result

	/**
	 * Creates a set substitution that allows replacing type parameters with the
	 * given expressions. This is similar to {@link SetSubstitution} but can operate
	 * during datatype translation.
	 * 
	 * @param translation a datatype translation
	 * @param srcInst     a compatible source type instantiation
	 * @param trgSetCons  the expression to substitute for recursive calls
	 * @param trgSets     the expression to substitute for formal type parameters
	 * 
	 * @return a substitution for replacing the datatype and its parameters with the
	 *         given expressions
	 */
	public static TranslatingSetSubstitution makeTranslatingSetSubstitution(DatatypeTranslation translation,
			ITypeInstantiation srcInst, Expression trgSetCons, Expression[] trgSets) {

		final Datatype datatype = (Datatype) srcInst.getOrigin();
		final TypeConstructorExtension tcons = datatype.getTypeConstructor();
		final Map<String, Expression> map = new HashMap<>();
		map.put(tcons.getName(), trgSetCons);
		final String[] formalNames = tcons.getFormalNames();
		final int nbParams = formalNames.length;
		assert trgSets.length == nbParams;
		for (int i = 0; i < nbParams; i++) {
			map.put(formalNames[i], trgSets[i]);
		}
		return new TranslatingSetSubstitution(translation, srcInst, map);
	}

	public TranslatingSetSubstitution(DatatypeTranslation translation, ITypeInstantiation srcInst,
			Map<String, Expression> subst) {

		this.translation = translation;
		this.srcInst = srcInst;
		this.subst = subst;
		this.factory = translation.getTargetFormulaFactory();
	}

	/**
	 * Returns the expression for the set associated to the given constructor
	 * argument.
	 * 
	 * @param arg some constructor argument
	 * @return the set for this argument
	 */
	public Expression getSet(ConstructorArgument arg) {
		final Type srcPattern = arg.getFormalType();
		final Type srcType = arg.getType(srcInst);
		return substitute(srcPattern, srcType);
	}

	/*
	 * All recursive calls during visit should go through this method which sets an
	 * additional input (the instantiated pattern) and returns the result.
	 */
	private Expression substitute(Type srcPattern, Type srcType) {
		inputSrcType = srcType;
		srcPattern.accept(this);
		return result;
	}

	private Expression[] substitute(Type[] srcPatterns, Type[] srcTypes) {
		return range(0, srcPatterns.length) //
				.mapToObj(i -> substitute(srcPatterns[i], srcTypes[i])) //
				.toArray(Expression[]::new);
	}

	@Override
	public void visit(BooleanType srcPattern) {
		result = justTranslate(srcPattern);
	}

	@Override
	public void visit(GivenType srcPattern) {
		var name = srcPattern.getName();
		var expr = subst.get(name);
		result = expr != null ? expr : justTranslate(srcPattern);
	}

	@Override
	public void visit(IntegerType srcPattern) {
		result = justTranslate(srcPattern);
	}

	@Override
	public void visit(ParametricType srcPattern) {
		var srcType = (ParametricType) inputSrcType;
		var params = srcPattern.getTypeParameters();
		var srcParams = srcType.getTypeParameters();
		var childExprs = substitute(params, srcParams);
		result = translate(srcType, childExprs);
	}

	private Expression translate(ParametricType srcType, Expression[] childExprs) {
		var ext = srcType.getExprExtension();
		if (ext.getOrigin() instanceof IDatatype) {
			return translation.translate(srcType, childExprs);
		} else {
			var trgType = translation.translate(srcType);
			return factory.makeExtendedExpression(ext, childExprs, NO_PREDS, null, trgType);
		}
	}

	@Override
	public void visit(PowerSetType srcPattern) {
		var srcType = (PowerSetType) inputSrcType;
		var base = substitute(srcPattern.getBaseType(), srcType.getBaseType());
		result = factory.makeUnaryExpression(POW, base, null);
	}

	@Override
	public void visit(ProductType srcPattern) {
		var srcType = (ProductType) inputSrcType;
		var left = substitute(srcPattern.getLeft(), srcType.getLeft());
		var right = substitute(srcPattern.getRight(), srcType.getRight());
		result = factory.makeBinaryExpression(CPROD, left, right, null);
	}

	private Expression justTranslate(Type type) {
		return type.translate(factory).toExpression();
	}

}
