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
package org.eventb.internal.core.seqprover.eventbExtensions.genmp;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;

/**
 * Represents the application of a substitute to some predicate.
 * 
 * @author Josselin Dolhen
 */
public class SubstAppli {

	private final Substitute substitute;
	private final IPosition position;

	public SubstAppli(Substitute substitute, IPosition position) {
		this.substitute = substitute;
		this.position = position;
	}

	public Predicate apply(Predicate pred) {
		assert (pred.getSubFormula(position).equals(substitute.toReplace()));
		return pred.rewriteSubFormula(position, substitute.substitute());
	}

	public boolean fromGoal() {
		return substitute.fromGoal();
	}

	public Predicate origin() {
		return substitute.origin();
	}

	@Override
	public String toString() {
		return substitute + " at pos " + position;
	}

}