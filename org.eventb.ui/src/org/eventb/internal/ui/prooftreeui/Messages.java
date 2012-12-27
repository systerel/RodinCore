/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prooftreeui;

import static org.eclipse.osgi.util.NLS.initializeMessages;

import java.text.MessageFormat;

public class Messages {
	private static final String BUNDLE_NAME = "org.eventb.internal.ui.prooftreeui.messages"; //$NON-NLS-1$
	public static String EditProofTreeNodeComment_title;
	public static String RuleDetailsProvider_addedfreeidentifiers_title;
	public static String RuleDetailsProvider_antecedent_title;
	public static String RuleDetailsProvider_deselect_title;
	public static String RuleDetailsProvider_forwardinference_title;
	public static String RuleDetailsProvider_goal;
	public static String RuleDetailsProvider_hide_title;
	public static String RuleDetailsProvider_inputsequent_title;
	public static String RuleDetailsProvider_instantiationcase_with;
	public static String RuleDetailsProvider_rule_title;
	public static String RuleDetailsProvider_select_title;
	public static String RuleDetailsProvider_show_title;
	static {
		// initialize resource bundle
		initializeMessages(BUNDLE_NAME, Messages.class);
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
