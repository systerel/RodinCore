/*******************************************************************************
 * Copyright (c) 2009, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;

/**
 * Facade class for total domain rewriting tactic. Implements a cache to avoid
 * recomputing the same substitutions several times.
 * 
 * @author "Nicolas Beauger"
 * @since 1.1
 */
public class TotalDomFacade {

	private static class SubstitutionCache {

		private IProverSequent key;
		private TotalDomSubstitutions substitutions;

		public synchronized TotalDomSubstitutions get(IProverSequent sequent) {
			if (sequent != key) {
				substitutions = new TotalDomSubstitutions(sequent);
				substitutions.computeSubstitutions();
				key = sequent;
			}
			return substitutions;
		}
	}

	private static final SubstitutionCache cache = new SubstitutionCache();

	public static ITactic getTactic(Predicate hyp, IPosition position,
			Expression substitute) {
		return BasicTactics.reasonerTac(new TotalDomRewrites(),
				new TotalDomRewrites.Input(hyp, position, substitute));
	}

	public static Set<Expression> getSubstitutions(IProverSequent sequent,
			Expression expression) {
		final TotalDomSubstitutions substitutions = cache.get(sequent);
		return substitutions.get(expression);
	}

}
