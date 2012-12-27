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
package org.eventb.internal.core.pog;

import java.text.MessageFormat;

import org.eclipse.osgi.util.NLS;

public final class Messages {

	private static final String BUNDLE_NAME = "org.eventb.internal.core.pog.messages";//$NON-NLS-1$

	// internal error messages
	
	public static String pog_multipleRefinementError;
	public static String pog_immutableHypothesisViolation;
	public static String pog_mutableHypothesisViolation;
	
	
	// build
	public static String build_cleaning;
	public static String build_runningPO;
	public static String build_extracting;
	
	// progress messages
	public static String progress_ContextAxioms;
	
	public static String progress_MachineInvariants;
	public static String progress_MachineEvents;
	public static String progress_MachineVariant;
	
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