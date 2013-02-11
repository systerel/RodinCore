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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;

/**
 * @author Nicolas Beauger
 * 
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
			FreeIdentifier[] params, Predicate goal, FormulaFactory ff) {
		final Set<Predicate> newHyps = new LinkedHashSet<Predicate>();
		
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
