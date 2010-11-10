/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static java.util.Collections.singleton;

import java.util.Set;

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
			FreeIdentifier[] params, Predicate goal, FormulaFactory ff) {
		return singleton(makeIdentEqualsConstr(ident, constr, type,
				params, ff));
	}

}
