/*******************************************************************************
 * Copyright (c) 2010, 2023 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     UPEC - refactored to use new test methods
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveMembershipL1;

/**
 * Unit tests for the rm level L1 reasoner {@link RemoveMembershipL1}.
 * 
 * @author htson
 */
public class RemoveMembershipL1Tests extends RemoveMembershipTests {

	public RemoveMembershipL1Tests() {
		super(new RemoveMembershipL1());
	}

}
