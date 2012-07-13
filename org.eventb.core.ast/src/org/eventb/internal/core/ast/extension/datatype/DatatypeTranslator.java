/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.extension.datatype;

import static java.util.Collections.singletonList;
import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.CONVERSE;
import static org.eventb.core.ast.Formula.DPROD;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.KRAN;
import static org.eventb.core.ast.Formula.TBIJ;
import static org.eventb.core.ast.Formula.TINJ;
import static org.eventb.core.ast.Formula.TSUR;
import static org.eventb.internal.core.ast.extension.datatype.Datatype.makeTypeInst;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.datatype.IArgument;
import org.eventb.core.ast.extension.datatype.IArgumentType;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.ast.extension.datatype.ITypeParameter;
import org.eventb.internal.core.ast.MathExtensionTranslator;
import org.eventb.internal.core.ast.extension.TypeMediator;

/**
 * This class creates a translation of general cases of datatypes which is
 * mathematically defined as follows: <br>
 * let the parameterized datatype DT for a given set of datatype parameters
 * <tt>	T1, ... Tn </tt> defined as follows:
 * 
 * <pre>
 *  DT(T1,...,Tn) ::=
 *  		c1(d11: α11,..., d1k1 : α1k1)
 *          c2(d21: α21,..., d2k2 : α2k2)
 *          .
 *          .
 *          .
 *          cm(dm1: αm1,..., dmkm : αmkm)
 * </pre>
 * 
 * which for each instance of it, we create a fresh identifier τ corresponding
 * to the parametric type constructor, and the following predicates:
 * 
 * <pre>
 * (A) each ci is translated to a fresh ɣi with ɣi ∈ αi1 × ... × αiki ↣ τ
 * (B) ran(ɣi) ∪ ... ∪ ran(ɣm) = τ
 * (C) each dij is translated to a fresh δij with δij  ∈ ran(ɣi) ↠ αij
 * (D) δi1 ⊗ ... ⊗ δiki = ɣi∼
 * 
 * </pre>
 * 
 * Moreover, there are two special cases where these predicates are slightly
 * modified:
 * <ul>
 * <li>when a constructor ɣi as no destructor, the singleton "{ɣi}" replaces
 * "ran(ɣi)" in (B)</li>
 * <li>when there is only one constructor, (A) becomes ɣi ∈ αi1 × ... × αiki ⤖ τ
 * and the predicate C becomes superfluous</li>
 * </ul>
 * 
 * @author "Thomas Muller"
 */
public class DatatypeTranslator extends MathExtensionTranslator {

	private static final String DT_PREFIX = "DT_";

	private final ParametricType parametricType;
	private final IExpressionExtension extension;
	private final IDatatype datatype;
	private final List<ITypeParameter> dtParams;
	private TypeMediation mediation;

	private GivenType tau;
	private List<Predicate> aS = new ArrayList<Predicate>();
	private Predicate b;
	private List<Predicate> cS = new ArrayList<Predicate>();
	private List<Predicate> dS = new ArrayList<Predicate>();

	public DatatypeTranslator(FormulaFactory factory, Set<String> usedNames,
			ParametricType parametricType) {
		super(factory, usedNames);
		this.parametricType = parametricType;
		extension = parametricType.getExprExtension();
		final Object origin = extension.getOrigin();
		assert (origin instanceof IDatatype);
		datatype = (IDatatype) origin;
		dtParams = datatype.getTypeParameters();
		computeTranslation();
	}

	private void computeTranslation() {
		final IExpressionExtension dtCons = datatype.getTypeConstructor();
		tau = solveGivenType(DT_PREFIX + dtCons.getSyntaxSymbol());
		mediation = new TypeMediation(factory, parametricType, dtParams, tau);
		final Set<IExpressionExtension> constructors = datatype
				.getConstructors();
		boolean onlyOneConstructor = constructors.size() == 1;
		for (IExpressionExtension cons : constructors) {
			final List<IArgument> destructors = datatype.getArguments(cons);
			aS.add(makeAPredicate(cons, destructors, onlyOneConstructor));
			cS.addAll(makeBPredicates(cons, destructors));
			if (destructors.size() > 0) {
				dS.add(makeDPredicates(cons, destructors));
			}
		}
		b = makeBPredicate(constructors);
	}

	private Predicate makeAPredicate(IExpressionExtension ct,
			List<IArgument> destructors, boolean oneConstructor) {
		final String consSymbol = ct.getSyntaxSymbol();
		final Expression tauExpr = tau.toExpression(factory);
		Type type = tau;
		Expression expr = tauExpr;
		if (!destructors.isEmpty()) {
			final Type prodType = makeDestructorTypesProduct(destructors);
			final Expression prodTypeExpr = prodType.toExpression(factory);
			final int function;
			if (oneConstructor)
				function = TBIJ; // special case: only one datatype constructor
			else
				function = TINJ; // general case
			expr = mBinExpr(function, prodTypeExpr, tauExpr);
			type = expr.getType().getBaseType();
		}
		final FreeIdentifier gammaI = solveIdentifier(consSymbol, type);
		consReplacements.put(ct, gammaI);
		return mRelPred(IN, gammaI, expr);
	}

	private Type makeDestructorTypesProduct(List<IArgument> destructors) {
		final TypeInstantiation inst = makeTypeInst(parametricType, dtParams);
		final TypeMediator mediator = new TypeMediator(factory);
		if (destructors.size() == 1) {
			return destructors.get(0).getType().toType(mediator, inst);
		}
		IArgumentType destType = destructors.get(0).getType();
		Type productType = mediation.handleDatatype(destType);
		for (int i = 1; i < destructors.size(); i++) {
			final Type dType;
			destType = destructors.get(i).getType();
			dType = mediation.handleDatatype(destType);
			productType = factory.makeProductType(productType, dType);
		}
		return productType;
	}

	private List<Predicate> makeBPredicates(IExpressionExtension constructor,
			List<IArgument> destructors) {
		final List<Predicate> result = new ArrayList<Predicate>();
		for (IArgument destructor : destructors) {
			final String destSymbol = destructor.getDestructor();
			final UnaryExpression ranExpr = mUnaryExpr(KRAN,
					consReplacements.get(constructor));
			final Type dType = mediation.handleDatatype(destructor.getType());
			final Expression dTypeExpr = dType.toExpression(factory);
			final Expression surj = mBinExpr(TSUR, ranExpr, dTypeExpr);
			final Type deltaType = surj.getType().getBaseType();
			final FreeIdentifier deltaI = solveIdentifier(destSymbol, deltaType);
			destReplacements.put(destSymbol, deltaI);
			result.add(mRelPred(IN, deltaI, surj));
		}
		return result;
	}

	private Predicate makeBPredicate(Set<IExpressionExtension> constructors) {
		final List<Expression> ranGammas = new ArrayList<Expression>();
		for (IExpressionExtension cons : constructors) {
			if (datatype.getArguments(cons).size() == 0) {
				ranGammas.add(mSingleton(consReplacements.get(cons)));
				continue;
			}
			ranGammas.add(mUnaryExpr(KRAN, consReplacements.get(cons)));
		}
		final Expression tauExpr = tau.toExpression(factory);
		final Expression expr = (ranGammas.size() == 1) ? ranGammas.get(0)
				: mAssocExpr(BUNION, ranGammas);
		return mRelPred(EQUAL, expr, tauExpr);
	}

	private Predicate makeDPredicates(IExpressionExtension constructor,
			List<IArgument> destructors) {
		final int nbDestructors = destructors.size();
		IArgument destructor = destructors.get(0);
		Expression deltaI = destReplacements.get(destructor.getDestructor());
		Expression dProd = deltaI;
		if (nbDestructors > 1) {
			for (int i = 1; i < nbDestructors; i++) {
				destructor = destructors.get(i);
				deltaI = destReplacements.get(destructor.getDestructor());
				dProd = mBinExpr(DPROD, dProd, deltaI);
			}
		}
		final Expression gammaI = consReplacements.get(constructor);
		final UnaryExpression conv = mUnaryExpr(CONVERSE, gammaI);
		return mRelPred(EQUAL, dProd, conv);
	}

	public List<Predicate> getAPredicates() {
		return aS;
	}

	public Predicate getBPredicate() {
		return b;
	}

	public List<Predicate> getCPredicates() {
		return cS;
	}

	public List<Predicate> getDPredicates() {
		return dS;
	}

	private Expression mSingleton(Expression expression) {
		return factory.makeSetExtension(singletonList(expression), null);
	}

	private BinaryExpression mBinExpr(final int tag, final Expression e1,
			final Expression e2) {
		return factory.makeBinaryExpression(tag, e1, e2, null);
	}

	private RelationalPredicate mRelPred(int tag, Expression e1,
			final Expression e2) {
		return factory.makeRelationalPredicate(tag, e1, e2, null);
	}

	private UnaryExpression mUnaryExpr(int tag, Expression constructor) {
		return factory.makeUnaryExpression(tag, constructor, null);
	}

	private AssociativeExpression mAssocExpr(int tag,
			final List<Expression> children) {
		return factory.makeAssociativeExpression(tag, children, null);
	}

	/**
	 * A mediation class that returns an instantiation of the type parameters if
	 * the parametric type is different of the current type being translated,
	 * else the "tau" replacement type.
	 */
	private static class TypeMediation {

		final TypeInstantiation instantiation;
		final TypeMediator mediator;
		final ParametricType type;
		private Type tau;

		public TypeMediation(FormulaFactory factory, ParametricType type,
				List<ITypeParameter> typeParams, Type tau) {
			this.type = type;
			this.instantiation = makeTypeInst(type, typeParams);
			this.mediator = new TypeMediator(factory);
			this.tau = tau;
		}

		public Type handleDatatype(IArgumentType destType) {
			final Type dType = destType.toType(mediator, instantiation);
			return (dType.equals(type)) ? tau : dType;
		}

	}

}
