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
package org.eventb.rubin.tests;

import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.rubin.tests.AbstractPPTests.ff;
import static org.eventb.rubin.tests.ParserTests.mSequent;
import static org.eventb.rubin.tests.ProblemStatus.INVALID;

import org.eventb.core.ast.Predicate;
import org.eventb.rubin.Sequent;

/**
 * Represents a simple problem, that is a sequent whose status is known.
 * 
 * @author Laurent Voisin.
 */
public class Problem implements IProblem {

	public static IProblem mProblem(ProblemStatus status, String name,
			String... predImages) {
		final Sequent sequent = mSequent(name, predImages);
		return new Problem(status, sequent);
	}

	// The known statusity
	private final ProblemStatus status;

	// The regular sequent
	private final Sequent sequent;

	public Problem(ProblemStatus status, Sequent sequent) {
		this.status = status;
		this.sequent = sequent;
	}

	@Override
	public String name() {
		return sequent.getName();
	}

	@Override
	public Sequent sequent() {
		return sequent;
	}

	@Override
	public ProblemStatus status() {
		return status;
	}

	@Override
	public IProblem invalidVariant() {
		final Predicate[] hyps = sequent.getHypotheses();
		final Predicate[] preds = new Predicate[hyps.length + 1];
		System.arraycopy(hyps, 0, preds, 0, hyps.length);
		preds[hyps.length] = sequent.getGoal();
		final Sequent sequent = new Sequent("invalid " + name(), preds,
				falsePred());
		return new Problem(INVALID, sequent);
	}

	private Predicate falsePred() {
		return ff.makeLiteralPredicate(BFALSE, null);
	}

}
