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
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * @since 1.0
 */
public class SingleStringInput implements IReasonerInput{
	
	private String string;
	private String error;
	
	public SingleStringInput(String string){
		assert string != null;
		this.string = string;
		this.error = null;
	}
	
	@Override
	public final boolean hasError(){
		return (error != null);
	}
	
	/**
	 * @return Returns the error.
	 */
	@Override
	public final String getError() {
		return error;
	}

	/**
	 * @return Returns the string.
	 */
	public final String getString() {
		return string;
	}

	public SingleStringInput(IReasonerInputReader reader)
			throws SerializeException {

		this(reader.getString("singleString"));
	}
	
	public void serialize(IReasonerInputWriter writer) throws SerializeException {
		assert ! hasError();
		assert string != null;
		writer.putString("singleString",string);
	}

	@Override
	public void applyHints(ReplayHints hints) {	
	}

}
