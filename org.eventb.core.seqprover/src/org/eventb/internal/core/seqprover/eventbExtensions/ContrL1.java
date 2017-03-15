/*******************************************************************************
 * Copyright (c) 2017 Systerel and others.
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
import static org.eventb.core.seqprover.ProverFactory.makeHideHypAction;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;

/**
 * Generates a proof rule to contradict the goal and falsify a given hypothesis
 * simultaneously.
 * <p>
 * In case no hypothesis is given, the reasoner assumes the given hypothesis is
 * 'true'.
 * </p>
 * 
 * This new level fixes #765 by hiding the hypothesis to falsify, rather than
 * just deselecting it.
 * 
 * @author Laurent Voisin
 */
public class ContrL1 extends Contr {

	@Override
	public String getReasonerID() {
		return Contr.REASONER_ID + "L1";
	}

	@Override
	protected IHypAction makeHypAction(Predicate pred) {
		return makeHideHypAction(singleton(pred));
	}

}
