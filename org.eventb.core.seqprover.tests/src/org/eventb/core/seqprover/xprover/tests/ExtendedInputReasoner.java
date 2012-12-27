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

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.xprover.XProverCall;
import org.eventb.core.seqprover.xprover.XProverReasoner;

/**
 * Simple class demonstrating how to write a reasoner with an extended input.
 * 
 * @author Laurent Voisin
 */
@SuppressWarnings("deprecation")
public class ExtendedInputReasoner extends XProverReasoner {

	static final String SUCCESS_MSG = "Success";

	private static class ProverCall extends XProverCall {
		
		private final String param;

		private boolean valid;

		protected ProverCall(Iterable<Predicate> hypotheses, Predicate goal,
				IProofMonitor pm, String param) {
			super(hypotheses, goal, pm);
			this.param = param;
		}

		@Override
		public void cleanup() {
			// Nothing to do
		}

		@Override
		public String displayMessage() {
			return SUCCESS_MSG;
		}

		@Override
		public boolean isValid() {
			return valid;
		}

		@Override
		public void run() {
			if ("success".equals(param)) {
				valid = true;
			} else {
				valid = false;
			}
		}
		
	}
	
	@Override
	public XProverCall newProverCall(IReasonerInput input,
			Iterable<Predicate> hypotheses, Predicate goal, IProofMonitor pm) {
		
		ExtendedInput xInput = (ExtendedInput) input;
		return new ProverCall(hypotheses, goal, pm, xInput.param);
	}

	public String getReasonerID() {
		return "org.eventb.xprover.core.tests.extended";
	}

	@Override
	public ExtendedInput deserializeInput(IReasonerInputReader reader) throws SerializeException {
		return new ExtendedInput(reader);
	}

}
