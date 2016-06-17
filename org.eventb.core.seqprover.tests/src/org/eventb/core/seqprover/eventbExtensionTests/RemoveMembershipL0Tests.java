/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveMembership;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveMembership.RMLevel;

/**
 * Unit tests for the rm reasoner {@link RemoveMembership}
 * 
 * @author htson
 */
public class RemoveMembershipL0Tests extends RemoveMembershipTests {

	private static final String REASONER_ID = "org.eventb.core.seqprover.rm";

	public RemoveMembershipL0Tests() {
		super(REASONER_ID, RMLevel.L0);
	}
	
}
