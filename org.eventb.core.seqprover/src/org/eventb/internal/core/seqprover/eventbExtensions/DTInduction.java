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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
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
 * Makes a distinct case on an inductive datatypes.
 * 
 * Antecedents are created for each constructor.
 * 
 * @author Nicolas Beauger
 */
public class DTInduction extends DTReasoner {

	private static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".dtInduction";
	private static final String DISPLAY_NAME = "dt induc";

	public DTInduction() {
		super(REASONER_ID, DISPLAY_NAME);
	}

	@ProverRule("DATATYPE_INDUCTION")
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
		
		for (FreeIdentifier param : params) {
			if (param.getType().equals(type)) {
				final Map<FreeIdentifier, Expression> subst = Collections
						.<FreeIdentifier, Expression> singletonMap(ident, param);
				newHyps.add(goal.substituteFreeIdents(subst));
			}
		}
		return newHyps;
	}

}
