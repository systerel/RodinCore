/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.datatype2.IConstructorArgument;
import org.eventb.core.ast.extension.datatype2.IConstructorExtension;
import org.eventb.core.ast.extension.datatype2.IDatatype2;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;

/**
 * Abstract class for reasoners about datatypes.
 * 
 * @author Nicolas Beauger
 * 
 */
public abstract class DTReasoner extends AbstractManualInference {

	protected static final Predicate[] NO_PRED = new Predicate[0];

	public static boolean hasDatatypeType(FreeIdentifier ident) {
		final Type type = ident.getType();
		if (!(type instanceof ParametricType)) {
			return false;
		}
		final ParametricType prmType = (ParametricType) type;
		final IExpressionExtension ext = prmType.getExprExtension();
		return ext.getOrigin() instanceof IDatatype2;
	}

	private final String reasonerId;
	private final String displayName;

	public DTReasoner(String reasonerId, String displayName) {
		this.reasonerId = reasonerId;
		this.displayName = displayName;
	}

	@Override
	public String getReasonerID() {
		return reasonerId;
	}

	@Override
	protected String getDisplayName() {
		return displayName;
	}

	@Override
	protected IAntecedent[] getAntecedents(IProverSequent seq, Predicate pred,
			IPosition position) {
		if (pred != null) {
			return null;
		}
		final Predicate goal = seq.goal();
		final Formula<?> subFormula = goal.getSubFormula(position);
		if (subFormula == null || subFormula.getTag() != Formula.FREE_IDENT) {
			return null;
		}
		final FreeIdentifier ident = (FreeIdentifier) subFormula;
		if (!hasDatatypeType(ident)) {
			return null;
		}

		final ParametricType prmType = (ParametricType) ident.getType();
		final IExpressionExtension ext = prmType.getExprExtension();
		final IDatatype2 dt = (IDatatype2) ext.getOrigin();
		return makeAntecedents(seq, ident, prmType, dt);
	}

	protected abstract Set<Predicate> makeNewHyps(FreeIdentifier ident,
			IExpressionExtension constr, ParametricType type,
			FreeIdentifier[] params, Predicate goal, FormulaFactory ff);

	protected static Predicate makeIdentEqualsConstr(FreeIdentifier ident,
			IExpressionExtension constr, ParametricType type,
			FreeIdentifier[] params, FormulaFactory ff) {
		final Expression constInst = ff.makeExtendedExpression(constr, params,
				NO_PRED, null, type);
		final Predicate newHyp = ff.makeRelationalPredicate(Formula.EQUAL,
				ident, constInst, null);
		return newHyp;
	}

	private IAntecedent[] makeAntecedents(IProverSequent seq,
			FreeIdentifier ident, ParametricType type, IDatatype2 dt) {
		final List<IAntecedent> antecedents = new ArrayList<IAntecedent>();
		final FormulaFactory ff = seq.getFormulaFactory();
		final ITypeEnvironment env = seq.typeEnvironment();
		for (IConstructorExtension constr : dt.getConstructors()) {
			final IConstructorArgument[] arguments = constr.getArguments();
			final Type[] argTypes = constr.getArgumentTypes(type);

			final FreeIdentifier[] params = makeFreshIdents(arguments,
					argTypes, ff, env);
			final Set<Predicate> newHyps = makeNewHyps(ident, constr, type,
					params, seq.goal(), ff);
			antecedents.add(ProverFactory.makeAntecedent(seq.goal(), newHyps,
					params, null));
		}
		return antecedents.toArray(new IAntecedent[antecedents.size()]);
	}

	private static FreeIdentifier[] makeFreshIdents(
			IConstructorArgument[] arguments, Type[] argTypes,
			FormulaFactory ff, ITypeEnvironment env) {
		assert arguments.length == argTypes.length;
		final int size = arguments.length;
		final FreeIdentifier[] idents = new FreeIdentifier[size];
		for (int i = 0; i < size; i++) {
			final IConstructorArgument arg = arguments[i];
			final String argName = makeFreshName(arg, i, env);
			final Type argType = argTypes[i];
			idents[i] = ff.makeFreeIdentifier(argName, null, argType);
		}
		return idents;
	}

	private static String makeFreshName(IConstructorArgument arg, int i,
			ITypeEnvironment env) {
		final String prefix = "p_";
		final String suffix;
		if (arg.isDestructor()) {
			suffix = arg.asDestructor().getName();
		} else {
			suffix = Integer.toString(i);
		}
		// proposed argName changes each time => no need to add to env
		final String argName = genFreshParamName(env, prefix + suffix);
		return argName;
	}

	private static String genFreshParamName(ITypeEnvironment typeEnv,
			String baseName) {
		int i = 0;
		String identName = baseName;
		while (typeEnv.contains(identName)) {
			identName = baseName + i;
			i++;
		}
		return identName;
	}

}