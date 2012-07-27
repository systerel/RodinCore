/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tactics.tests;

import org.eventb.core.seqprover.eventbExtensions.AutoTactics;

/**
 * @author Emmanuel Billaud
 */
public class MembershipGoalTacTests extends AbstractTacticTests {

	public MembershipGoalTacTests() {
		super(new AutoTactics.MembershipGoalAutoTac(),
				"org.eventb.core.seqprover.mbGoalTac");
	}

}
