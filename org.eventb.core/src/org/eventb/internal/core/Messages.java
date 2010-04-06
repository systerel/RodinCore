/*******************************************************************************
 * Copyright (c) 2006-2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core;

import java.text.MessageFormat;

import org.eclipse.osgi.util.NLS;

public final class Messages {

	private static final String BUNDLE_NAME = "org.eventb.internal.core.messages";//$NON-NLS-1$

	// All messages below take one parameter: the handle to the element that
	// caused the error
	public static String database_SCAssignmentParseFailure;
	public static String database_SCAssignmentTCFailure;
	public static String database_SCExpressionParseFailure;
	public static String database_SCExpressionTCFailure;
	public static String database_SCIdentifierNameParseFailure;
	public static String database_SCIdentifierTypeParseFailure;
	public static String database_SCPredicateParseFailure;
	public static String database_SCPredicateTCFailure;
	public static String database_SCRefinesEventTypeFailure;
	public static String database_SCRefinesMachineTypeFailure;
	public static String database_SCExtendsContextTypeFailure;
	public static String database_SCSeesContextTypeFailure;
	public static String database_SCMachineMultipleVariantFailure;
	public static String database_SCMachineMultipleRefinesFailure;
	public static String database_SequentMultipleHypothesisFailure;
	public static String database_SequentMultipleGoalFailure;

	public static String database_MachineMultipleRefinesFailure;
	public static String database_MachineMultipleVariantFailure;
	
	public static String database_POPredicateSelectionHintFailure;
	public static String database_POIntervalSelectionHintFailure;
	
	public static String database_EventInvalidConvergenceFailure;
	
	public static String tool_ImmutableStateModificationFailure;
	public static String tool_MutableStateNotUnmodifiableFailure;
	
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