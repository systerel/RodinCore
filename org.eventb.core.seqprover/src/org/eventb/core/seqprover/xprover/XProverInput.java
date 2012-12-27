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
package org.eventb.core.seqprover.xprover;

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * Implements a simple reasoner input for a reasoner that takes either the
 * selected hypotheses or all visible hypotheses, and is run with a timeout.
 * <p>
 * Clients may extend this class to add more input parameters for their
 * reasoner.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 1.0
 */
public class XProverInput implements IReasonerInput {
	
	private static final String INVALID_DELAY = "Invalid time out delay";  //$NON-NLS-1
	private static final String ARG_KEY = "arg";  //$NON-NLS-1
	private static final char RESTRICTED_FLAG = 'R';

	public final boolean restricted;
	public final long timeOutDelay;
	final String error;
	
	/**
	 * Creates a new input given the resoner parameters.
	 * 
	 * @param restricted
	 *            <code>true</code> iff only selected hypotheses should be
	 *            considered by the reasoner
	 * @param timeOutDelay
	 *            delay after which the reasoner is cancelled, must be
	 *            non-negative. A zero value denotes an infinite delay
	 */
	public XProverInput(boolean restricted, long timeOutDelay) {
		if (timeOutDelay < 0) {
			this.restricted = false;
			this.timeOutDelay = -1;
			this.error = INVALID_DELAY;
			return;
		}
		this.restricted = restricted;
		this.timeOutDelay = timeOutDelay;
		this.error = null;
	}

	/**
	 * Creates a new input from a serialized proof.
	 * <p>
	 * This method should be overriden by sub-classes that implement additional
	 * reasoner parameters in the following way:
	 * <pre>
	 * protected XProverInput(IReasonerInputReader reader) throws SerializeException {
	 *     super(reader);
	 *     // de-serialize additional parameters here
	 * }
	 * </pre>
	 * </p>
	 * 
	 * @param reader
	 *            the reader used to read the serialized proof
	 * 
	 * @throws SerializeException
	 *             in case of trouble reading the serialized proof
	 * @see #serialize(IReasonerInputWriter)
	 */
	protected XProverInput(IReasonerInputReader reader) throws SerializeException {
		String arg = reader.getString(ARG_KEY);
		if (arg.charAt(0) == RESTRICTED_FLAG) {
			restricted = true;
			arg = arg.substring(1);
		} else {
			restricted = false;
		}
		timeOutDelay = Long.parseLong(arg);
		error = timeOutDelay < 0 ? INVALID_DELAY : null;
	}
	
	/**
	 * Serializes this input.
	 * <p>
	 * This method should be overriden by sub-classes that implement additional
	 * reasoner parameters in the following way:
	 * <pre>
	 * protected void serialize(IReasonerInputWriter writer) throws SerializeException {
	 *     super(writer);
	 *     // serialize additional parameters here
	 * }
	 * </pre>
	 * </p>
	 * 
	 * @param writer
	 *    the writer to use for serializing this input
	 * @throws SerializeException
	 *             in case of trouble reading the serialized proof
	 * @see #XProverInput(IReasonerInputReader)
	 */
	protected void serialize(IReasonerInputWriter writer) throws SerializeException {
		String argString = Long.toString(timeOutDelay);
		if (restricted) {
			argString = RESTRICTED_FLAG + argString;  //$NON-NLS-1
		}
		writer.putString(ARG_KEY, argString);
	}
	
	public boolean hasError() {
		return error != null;
	}

	public String getError() {
		return error;
	}

	public void applyHints(ReplayHints hints) {
		// Nothing to do
	}
	
}