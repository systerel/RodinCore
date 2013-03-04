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
package org.eventb.internal.core.ast.extension.datatype2;

import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.internal.core.ast.extension.datatype2.DatatypeHelper.computeGroup;
import static org.eventb.internal.core.ast.extension.datatype2.DatatypeHelper.computeId;
import static org.eventb.internal.core.ast.extension.datatype2.DatatypeHelper.computeKind;
import static org.eventb.internal.core.ast.extension.datatype2.TypeSubstitution.makeSubstitution;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.core.ast.extension.datatype2.IConstructorExtension;
import org.eventb.core.ast.extension.datatype2.IDestructorExtension;

/**
 * This class represents an extension used as destructor for a datatype. The
 * destructor child represents the datatype to destruct, then its child type
 * represent the datatype type in the
 * {@link #verifyType(Type, Expression[], Predicate[])},
 * {@link #synthesizeType(Expression[], Predicate[], ITypeMediator)} and
 * {@link #typeCheck(ExtendedExpression, ITypeCheckMediator)} methods.
 * 
 * @author Vincent Monfort
 */
public class DestructorExtension implements IDestructorExtension {

	private static final String PARAM_PREFIX = "p";

	private static final Predicate[] NO_PRED = new Predicate[0];

	private final Datatype2 origin;
	private final ConstructorExtension constructor;
	private final String name;
	private final DatatypeArgument argument;
	private final String id;
	private final IExtensionKind kind;
	private final String groupId;

	public DestructorExtension(Datatype2 origin,
			ConstructorExtension constructor, String name,
			DatatypeArgument argument) {
		this.origin = origin;
		this.constructor = constructor;
		this.name = name;
		this.id = computeId(name);
		this.argument = argument;
		int nbArgs = 1; // one argument (of type datatype)
		this.kind = computeKind(nbArgs);
		this.groupId = computeGroup(nbArgs);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IConstructorExtension getConstructor() {
		return constructor;
	}

	// formula is "destr(dt)"
	// WD is "# params . dt = constr(params)"
	@Override
	public Predicate getWDPredicate(IExtendedFormula formula,
			IWDMediator wdMediator) {

		if (origin.getConstructors().length == 1) {
			// if there is only one constructor the WD is true
			return wdMediator.makeTrueWD();
		}

		final FormulaFactory ff = wdMediator.getFormulaFactory();
		final IDestructorExtension[] constrArgs = constructor.getArguments();
		final int argSize = constrArgs.length;
		final Expression child = formula.getChildExpressions()[0];

		final ParametricType datatypeType = (ParametricType) child.getType();

		final Type[] argTypes = constructor.getArgumentTypes(datatypeType
				.translate(ff));
		assert argTypes.length == argSize;

		final List<BoundIdentDecl> bids = new ArrayList<BoundIdentDecl>(argSize);
		final Expression[] params = buildParams(bids, constrArgs, argTypes, ff);

		final Expression dt = child.shiftBoundIdentifiers(argSize);
		return makeWD(dt, bids, params, ff);
	}

	private static Expression[] buildParams(List<BoundIdentDecl> bids,
			IDestructorExtension[] constrArgs, Type[] argTypes,
			FormulaFactory ff) {
		final int argSize = constrArgs.length;
		final Expression[] params = new Expression[argSize];
		for (int i = 0; i < argSize; i++) {
			final IDestructorExtension arg = constrArgs[i];
			final String prmName = makeParamName(arg, i);

			final Type argType = argTypes[i];
			bids.add(ff.makeBoundIdentDecl(prmName, null, argType));
			params[i] = ff.makeBoundIdentifier(argSize - 1 - i, null, argType);
		}
		return params;
	}

	private static String makeParamName(IDestructorExtension arg, int index) {
		final String prmName;
		if (arg != null) {
			prmName = arg.getName() + index;
		} else {
			prmName = PARAM_PREFIX + index;
		}
		return prmName;
	}

	private Predicate makeWD(Expression dt, List<BoundIdentDecl> bids,
			Expression[] params, FormulaFactory ff) {
		final ExtendedExpression constr = ff.makeExtendedExpression(
				constructor, params, NO_PRED, null, dt.getType());
		final RelationalPredicate eqDtConstr = ff.makeRelationalPredicate(
				EQUAL, dt, constr, null);
		return ff.makeQuantifiedPredicate(EXISTS, bids, eqDtConstr, null);
	}

	@Override
	public String getSyntaxSymbol() {
		return name;
	}

	@Override
	public IExtensionKind getKind() {
		return kind;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getGroupId() {
		return groupId;
	}

	@Override
	public void addPriorities(IPriorityMediator mediator) {
		// no priority
	}

	@Override
	public void addCompatibilities(ICompatibilityMediator mediator) {
		// no compatibility
	}

	@Override
	public Type synthesizeType(Expression[] childExprs, Predicate[] childPreds,
			ITypeMediator mediator) {
		final Type childType = childExprs[0].getType();
		final TypeSubstitution subst = makeSubstitution(origin, childType);
		if (subst == null) {
			return null;
		}
		return subst.rewrite(argument.getType());
	}

	@Override
	public boolean verifyType(Type proposedType, Expression[] childExprs,
			Predicate[] childPreds) {
		assert childExprs.length == 1;
		assert childPreds.length == 0;
		final Type childType = childExprs[0].getType();
		final TypeSubstitution subst = makeSubstitution(origin, childType);
		if (subst == null) {
			return false;
		}
		final Type expected = subst.rewrite(argument.getType());
		return expected.equals(proposedType);
	}

	@Override
	public Type typeCheck(ExtendedExpression expression,
			ITypeCheckMediator tcMediator) {
		final Type childType = expression.getChildExpressions()[0].getType();
		final TypeSubstitution subst = makeSubstitution(origin, tcMediator);
		tcMediator.sameType(childType, subst.getInstance());
		return subst.rewrite(argument.getType());
	}

	@Override
	public boolean conjoinChildrenWD() {
		return true;
	}

	@Override
	public boolean isATypeConstructor() {
		return false;
	}

	@Override
	public Datatype2 getOrigin() {
		return origin;
	}

}
