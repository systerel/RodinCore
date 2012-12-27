/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.xprover.tests;

import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.xprover.XProverInput;

/**
 * Simple class demonstrating how a reasoner input can be extended.
 * 
 * @author Laurent Voisin
 */
public class ExtendedInput extends XProverInput {

	private static final String PARAM_KEY = "p";

	private final String error;
	
	public final String param;
	
	public ExtendedInput(boolean restricted, long timeOutDelay, String param) {
		super(restricted, timeOutDelay);
		this.param = param;
		this.error = validate();
	}

	protected ExtendedInput(IReasonerInputReader reader) throws SerializeException {
		super(reader);
		this.param = reader.getString(PARAM_KEY);
		this.error = validate();
	}

	private String validate() {
		if ("success".equals(param) || "failure".equals(param)) {
			return null;
		} else {
			return "Illegal parameter: " + param;
		}
	}
	
	@Override
	public String getError() {
		return error != null ? error : super.getError();
	}

	@Override
	public boolean hasError() {
		return error != null || super.hasError();
	}

	@Override
	protected void serialize(IReasonerInputWriter writer) throws SerializeException {
		super.serialize(writer);
		writer.putString(PARAM_KEY, param);
	}

}
