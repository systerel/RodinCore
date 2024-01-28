/*******************************************************************************
 * Copyright (c) 2007, 2024 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.seqprover.SequentProver;

public class RemoveMembershipL0 extends RemoveMembership {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".rm";

	public RemoveMembershipL0() {
		super(Level.L0);
	}

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

}
