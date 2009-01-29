/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ide;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eventb.ui.EventBUIPlugin;

/**
 * The main plugin class for Event-B UI.
 */
public class EventBIDEPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eventb.ide";

	
	/**
	 * The identifier of the modelling perspective (value
	 * <code>"org.eventb.ide.perspective.eventb"</code>).
	 */
	public static final String EVENTB_PERSPECTIVE_ID = PLUGIN_ID
			+ ".perspective.eventb";


	/**
	 * The identifier of the proving perspective (value
	 * <code>"org.eventb.ide.perspective.proving"</code>).
	 */
	public static final String PROVING_PERSPECTIVE_ID = EventBUIPlugin.PLUGIN_ID
			+ ".perspective.proving";


	
}