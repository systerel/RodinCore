/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.reasonerInputs;

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * @since 1.0
 */
public class EmptyInput implements IReasonerInput {

	public EmptyInput() {
		// Nothing to initialize
	};
	
	@Override
	public boolean hasError() {
		return false;
	}

	@Override
	public String getError() {
		return null;
	}

	@Override
	public void applyHints(ReplayHints hints) {	
		// Nothing to do.
	}
	
}
