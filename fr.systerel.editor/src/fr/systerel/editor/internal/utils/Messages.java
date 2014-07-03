/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.utils;

import org.eclipse.osgi.util.NLS;

public final class Messages {

	private static final String BUNDLE_NAME = "fr.systerel.editor.internal.utils.messages"; //$NON-NLS-1$

	public static String preferencepage_editionMode;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

}
