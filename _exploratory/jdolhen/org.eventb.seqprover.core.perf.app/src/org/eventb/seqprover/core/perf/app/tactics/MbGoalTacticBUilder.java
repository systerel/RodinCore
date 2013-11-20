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
package org.eventb.seqprover.core.perf.app.tactics;

import org.eventb.core.seqprover.ITacticDescriptor;

/**
 * For running the tactic MembershipGoal from the Sequent Prover.
 * 
 * @author Josselin Dolhen
 */
public class MbGoalTacticBUilder extends TacticBuilder {

	public MbGoalTacticBUilder() {
		super("Membership in goal");
	}

	@Override
	public ITacticDescriptor makeTactic(boolean restricted) {
		return REGISTRY
				.getTacticDescriptor("org.eventb.core.seqprover.mbGoalTac");
	}

}
