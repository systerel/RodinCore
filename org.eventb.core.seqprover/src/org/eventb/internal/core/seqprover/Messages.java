/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added reasonerDesc_unknown
 *     Systerel - added tactic messages
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.osgi.util.NLS;
import org.eventb.core.seqprover.ITactic;

public final class Messages {

	private static final String BUNDLE_NAME = "org.eventb.internal.core.seqprover.messages";//$NON-NLS-1$

	// All messages below take no parameter
	public static String xprover_interrupted = "Prover interrupted";
	public static String xprover_exception = "Internal error, see log file";
	public static String xprover_timeout = "Timeout";
	public static String xprover_failed = "Failed";
	
	public static String tactic_nodeClosed;
	public static String tactic_attemptFailed;
	public static String tactic_failed;
	public static String tactic_cancelled;
	public static String tactic_illegalOneOrMore;
	
	// Messages with parameter(s)
	public static String reasonerDesc_unknown;
	
	public static String tactic_illegalOne;
	public static String tactic_illegalOne(List<ITactic> tactics) {
		return bind(tactic_illegalOne, tactics);
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * Bind the given message's substitution locations with the given string values.
	 * 
	 * @param message the message to be manipulated
	 * @param bindings An array of objects to be inserted into the message
	 * @return the manipulated String
	 */
	public static String bind(String message, Object... bindings) {
		return MessageFormat.format(message, bindings);
	}
	
	private Messages() {
		// Do not instantiate
	}
}