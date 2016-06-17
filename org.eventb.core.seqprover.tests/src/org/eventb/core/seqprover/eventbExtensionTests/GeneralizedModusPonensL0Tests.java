/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
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
import org.eventb.internal.core.seqprover.eventbExtensions.genmp.GeneralizedModusPonens;

/**
 * Unit tests for the reasoner GeneralizedModusPonens.
 * 
 * @author Emmanuel Billaud
 */
public class GeneralizedModusPonensL0Tests extends GeneralizedModusPonensTests {

	// The reasoner for testing.
	private static final AbstractGenMP GenMP_L0 = new GeneralizedModusPonens();

	public GeneralizedModusPonensL0Tests() {
		this(GenMP_L0);
	}

	protected GeneralizedModusPonensL0Tests(AbstractGenMP rewriter) {
		super(rewriter);
	}

}
