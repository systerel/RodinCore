/*******************************************************************************
 * Copyright (c) 2024 UPEC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     UPEC - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.eventb.internal.core.seqprover.eventbExtensions.EqHe.Level.L2;

import org.eventb.internal.core.seqprover.eventbExtensions.EqHe.Level;

public class HeL2Tests extends HeL1Tests {

	public HeL2Tests() {
		super(L2);
	}

	protected HeL2Tests(Level level) {
		super(level);
	}

}
