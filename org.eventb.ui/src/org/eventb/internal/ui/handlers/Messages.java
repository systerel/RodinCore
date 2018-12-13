/*******************************************************************************
 * Copyright (c) 2018 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.handlers;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eventb.internal.ui.handlers.messages";

	// Proof Command Messages
	public static String proof_checkExternalProvers_title;
	public static String proof_checkExternalProvers_ok_message;
	public static String proof_checkExternalProvers_error_message;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// no instantiation
	}

}
