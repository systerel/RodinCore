/*******************************************************************************
 * Copyright (c) 2022 Université de Lorraine and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Université de Lorraine - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import org.eventb.internal.core.seqprover.eventbExtensions.genmp.AbstractGenMP;
import org.eventb.internal.core.seqprover.eventbExtensions.genmp.GeneralizedModusPonensL4;

/**
 * Unit tests for the reasoner GeneralizedModusPonensL4.
 *
 * @author Guillaume Verdier
 */
public class GeneralizedModusPonensL4Tests extends GeneralizedModusPonensL3Tests {

	// The reasoner for testing.
	private static final AbstractGenMP GenMP_L4 = new GeneralizedModusPonensL4();

	public GeneralizedModusPonensL4Tests() {
		this(GenMP_L4);
	}

	protected GeneralizedModusPonensL4Tests(AbstractGenMP rewriter) {
		super(rewriter);
	}

}
