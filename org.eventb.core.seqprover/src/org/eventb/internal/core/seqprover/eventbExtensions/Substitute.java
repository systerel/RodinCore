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
package org.eventb.internal.core.seqprover.eventbExtensions;

import org.eventb.core.ast.Predicate;

/**
 *  This class represents one substitution of a predicate into the sequent by
 *  ⊤ or ⊥ in the GenMP reasoner.
 *
 * @author Josselin Dolhen
 *
 */
public class Substitute {

	// the predicate to be re-written
	private final Predicate origin;
	// the sub-predicate to be replaced
	private final Predicate hypOrGoal;
	// the substitute of the predicate to be replace (⊤ or ⊥)
	private final Predicate substitute;

	public Substitute(Predicate origin, Predicate hypOrGoal, Predicate substitute) {
		super();
		this.origin = origin;
		this.hypOrGoal = hypOrGoal;
		this.substitute = substitute;
	}

	public Predicate origin() {
		return origin;
	}

	public Predicate hypOrGoal() {
		return hypOrGoal;
	}

	public Predicate substitute() {
		return substitute;
	}

	boolean fromGoal() {
		return hypOrGoal == null;
	}

}
