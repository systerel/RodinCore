/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.reasonerInputs;

import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;

/**
 * @since 1.0
 */
public abstract class EmptyInputReasoner implements IReasoner {

	private static EmptyInput emptyReasonerInput = new EmptyInput();

	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer) {
		// Nothing to serialize
	}

	public IReasonerInput deserializeInput(IReasonerInputReader reader) {
		return emptyReasonerInput;
	}

}
