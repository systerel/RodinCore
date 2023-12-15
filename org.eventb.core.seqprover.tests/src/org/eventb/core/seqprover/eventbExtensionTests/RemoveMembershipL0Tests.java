/*******************************************************************************
 * Copyright (c) 2010, 2023 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveMembershipL0;

/**
 * Unit tests for the rm reasoner {@link RemoveMembershipL0} at level 0.
 * 
 * @author htson
 */
public class RemoveMembershipL0Tests extends RemoveMembershipTests {

	public RemoveMembershipL0Tests() {
		super(new RemoveMembershipL0());
	}
	
}
