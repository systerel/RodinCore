/*******************************************************************************
 * Copyright (c) 2017 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import org.eventb.internal.core.seqprover.eventbExtensions.genmp.AbstractGenMP;
import org.eventb.internal.core.seqprover.eventbExtensions.genmp.GeneralizedModusPonensL3;

/**
 * Unit tests for the reasoner GeneralizedModusPonensL3.
 * 
 * The tests are actually within GeneralizedModusPonensTests.
 * 
 * @author Laurent Voisin
 */
public class GeneralizedModusPonensL3Tests extends GeneralizedModusPonensL2Tests {

	// The reasoner for testing.
	private static final AbstractGenMP GenMP_L3 = new GeneralizedModusPonensL3();

	public GeneralizedModusPonensL3Tests() {
		this(GenMP_L3);
	}

	protected GeneralizedModusPonensL3Tests(AbstractGenMP rewriter) {
		super(rewriter);
	}
}
