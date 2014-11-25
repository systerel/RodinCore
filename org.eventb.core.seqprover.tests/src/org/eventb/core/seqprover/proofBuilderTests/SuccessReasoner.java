/*******************************************************************************
 * Copyright (c) 2009, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.proofBuilderTests;

import org.eventb.core.seqprover.IProofRule.IAntecedent;

/**
 * @author "Nicolas Beauger"
 * 
 */
public class SuccessReasoner extends AbstractFakeReasoner {

	public static final String REASONER_ID = "org.eventb.core.seqprover.tests.successReasoner";
	private static final int DEFAULT_VERSION = 2;
	private static final boolean DEFAULT_SUCCESS = true;

	public SuccessReasoner() {
		super(DEFAULT_VERSION, DEFAULT_SUCCESS);
	}

	public SuccessReasoner(int version, boolean success,
			IAntecedent... antecedents) {
		super(version, success, antecedents);
	}

	public String getReasonerID() {
		return REASONER_ID;
	}
}
