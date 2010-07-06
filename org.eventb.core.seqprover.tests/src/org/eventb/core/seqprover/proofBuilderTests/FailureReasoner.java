/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.proofBuilderTests;


/**
 * @author "Nicolas Beauger"
 * 
 */
public class FailureReasoner extends AbstractFakeReasoner {

	private static final String REASONER_ID = "org.eventb.core.seqprover.tests.failureReasoner";
	private static final int DEFAULT_VERSION = 2;
	private static final boolean DEFAULT_SUCCESS = false;

	public FailureReasoner() {
		super(DEFAULT_VERSION, DEFAULT_SUCCESS);
	}
	
	public FailureReasoner(int version, boolean success) {
		super(version, success);
	}

	public String getReasonerID() {
		return REASONER_ID;
	}
}
