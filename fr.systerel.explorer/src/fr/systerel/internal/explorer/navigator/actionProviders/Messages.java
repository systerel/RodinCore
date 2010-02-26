/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/
package fr.systerel.internal.explorer.navigator.actionProviders;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "fr.systerel.internal.explorer.navigator.actionProviders.messages"; //$NON-NLS-1$
	
	// Actions
	public static String actions_replayUndischarged_text;
	public static String actions_replayUndischarged_tooltip;

	// Dialogs
	public static String dialogs_replayingProofs;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// not intended to be instantiated
	}
}
