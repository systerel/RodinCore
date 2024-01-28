/*******************************************************************************
 * Copyright (c) 2024 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveMembershipL2;

/**
 * Unit tests for the rm level L2 reasoner {@link RemoveMembershipL2}.
 * 
 * @author Laurent Voisin
 */
public class RemoveMembershipL2Tests extends RemoveMembershipTests {

	public RemoveMembershipL2Tests() {
		super(new RemoveMembershipL2());
	}

}
