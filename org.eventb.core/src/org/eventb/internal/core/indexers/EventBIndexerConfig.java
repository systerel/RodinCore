/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.indexers;

import org.eclipse.core.runtime.Platform;
import org.eventb.core.EventBPlugin;

public class EventBIndexerConfig {

	private EventBIndexerConfig() {
		// private constructor : zeroton
	}

	public static void configurePluginDebugOptions() {
		if (EventBPlugin.getDefault().isDebugging()) {
			String option =
					Platform.getDebugOption("fr.systerel.eventb.indexer/debug");
			if (option != null)
				EventBIndexer.DEBUG = option.equalsIgnoreCase("true"); //$NON-NLS-1$
		}

	}

}
