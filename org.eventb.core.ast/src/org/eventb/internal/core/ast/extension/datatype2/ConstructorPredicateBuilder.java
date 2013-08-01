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

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.datatype2.IConstructorArgument;

/**
 * Utility class for building predicates about constructors.
 * 
 * @author Laurent Voisin
 */
public final class ConstructorPredicateBuilder {

	// Returns the predicate "# args . dtValue = constructor(args)"
	public static final Predicate makeInConstructorDomain(Expression dtValue,
			ConstructorExtension constructor) {
		final ConstructorPredicateBuilder builder //
		= new ConstructorPredicateBuilder(constructor);
		return builder.makePredicate(dtValue);
	}

	private static final String PARAM_PREFIX = "p";
	private static final Predicate[] NO_PRED = new Predicate[0];

	private final Datatype2 datatype;
	private final ConstructorExtension constructor;
	private final IConstructorArgument[] arguments;
	private final int nbArgs;
	private final BoundIdentDecl[] bids;
	private final Expression[] bis;

	private FormulaFactory ff;

	private ConstructorPredicateBuilder(ConstructorExtension constructor) {
		this.datatype = constructor.getOrigin();
		this.constructor = constructor;
		this.arguments = constructor.getArguments();
		this.nbArgs = arguments.length;
		this.bids = new BoundIdentDecl[nbArgs];
		this.bis = new BoundIdentifier[nbArgs];
	}

	private Predicate makePredicate(Expression dtValue) {
		ff = dtValue.getFactory();
		final Type dtType = dtValue.getType();
		makeIdentifiers(dtType);
		final Expression constr = ff.makeExtendedExpression(constructor, bis,
				NO_PRED, null, dtType);
		final RelationalPredicate eqDtConstr = ff.makeRelationalPredicate(
				EQUAL, dtValue.shiftBoundIdentifiers(nbArgs), constr, null);
		return ff.makeQuantifiedPredicate(EXISTS, bids, eqDtConstr, null);
	}

	public void makeIdentifiers(Type dtType) {
		final TypeSubstitution inst = datatype.getTypeInstantiation(dtType);
		for (int i = 0; i < nbArgs; i++) {
			final String bidName = makeBoundName(i);
			final Type argType = arguments[i].getType(inst);
			bids[i] = ff.makeBoundIdentDecl(bidName, null, argType);
			bis[i] = ff.makeBoundIdentifier(nbArgs - i - 1, null, argType);
		}
	}

	private String makeBoundName(int index) {
		final IConstructorArgument arg = arguments[index];
		if (arg.isDestructor()) {
			return arg.asDestructor().getName() + index;
		}
		return PARAM_PREFIX + index;
	}

}
