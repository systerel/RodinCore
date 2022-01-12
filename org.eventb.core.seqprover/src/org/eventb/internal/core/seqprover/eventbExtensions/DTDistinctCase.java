/*******************************************************************************
 * Copyright (c) 2010, 2022 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     Université de Lorraine - additional hypotheses for set membership
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;

/**
 * Makes a distinct case on simple (non inductive) datatypes.
 * 
 * Antecedents are created for each constructor.
 * 
 * @author Nicolas Beauger
 */
public class DTDistinctCase extends DTReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".dtDistinctCase";
	private static final String DISPLAY_NAME = "dt dc";
	
	public DTDistinctCase() {
		super(REASONER_ID, DISPLAY_NAME);
	}

	@ProverRule("DATATYPE_DISTINCT_CASE")
	@Override
	protected Set<Predicate> makeNewHyps(FreeIdentifier ident,
			IExpressionExtension constr, ParametricType type,
			FreeIdentifier[] params, Expression[] paramSets, Predicate goal, FormulaFactory ff) {
		final Set<Predicate> newHyps = new LinkedHashSet<Predicate>();
		if (paramSets != null) {
			assert params.length == paramSets.length;
			for (int i = 0; i < params.length; ++i) {
				newHyps.add(ff.makeRelationalPredicate(Formula.IN, params[i], paramSets[i], null));
			}
		}
		newHyps.add(makeIdentEqualsConstr(ident, constr, type, params, ff));
		return newHyps;
	}

}
