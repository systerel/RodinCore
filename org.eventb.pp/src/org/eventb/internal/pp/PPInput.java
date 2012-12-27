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
package org.eventb.internal.pp;

import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.xprover.XProverInput;

/**
 * Implementation of {@link XProverInput} for PP.
 *
 * @author François Terrier
 *
 */
public class PPInput extends XProverInput {

	private static final String MAX_STEPS = "maxSteps";
	private int maxSteps;

	public PPInput(boolean restricted, long timeOutDelay, int maxSteps) {
		super(restricted, timeOutDelay);
		
		this.maxSteps = maxSteps;
	}
	

	public PPInput(IReasonerInputReader reader) throws SerializeException {
		super(reader);
		
		maxSteps = Integer.valueOf(reader.getString(MAX_STEPS));
	}


	@Override
	protected void serialize(IReasonerInputWriter writer)
			throws SerializeException {
		super.serialize(writer);
		
		writer.putString(MAX_STEPS, Integer.toString(maxSteps));
	}


	/**
	 * Return the maximum number of steps PP will take
	 * before stopping.
	 * 
	 * @return the maximum number of steps PP will take
	 * before stopping
	 */
	public int getMaxSteps() {
		return maxSteps;
	}

}
