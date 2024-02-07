/*******************************************************************************
 * Copyright (c) 2023, 2024 Université de Lorraine and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Université de Lorraine - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.eventb.internal.core.seqprover.eventbExtensions.EqHe.Level.L1;

import org.eventb.internal.core.seqprover.eventbExtensions.EqHe.Level;

public class EhL1Tests extends EhTests {

	public EhL1Tests() {
		super(L1);
	}

	protected EhL1Tests(Level level) {
		super(level);
	}

}
