/*******************************************************************************
 * Copyright (c) 2006, 2024 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.reasonerExtensionTests;

public class TrueGoal extends org.eventb.internal.core.seqprover.eventbExtensions.TrueGoal {
	
	public static final String REASONER_ID = "org.eventb.core.seqprover.tests.trueGoal";
	
	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

}
