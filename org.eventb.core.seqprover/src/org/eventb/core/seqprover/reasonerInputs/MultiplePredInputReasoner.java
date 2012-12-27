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
import org.eventb.core.seqprover.SerializeException;

/**
 * @since 1.0
 */
public abstract class MultiplePredInputReasoner implements IReasoner {

	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		return new MultiplePredInput(reader);
	}

	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer)
			throws SerializeException {
		MultiplePredInput mInput = (MultiplePredInput) input;
		mInput.serialize(writer);
	}

}
