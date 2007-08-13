/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor.editpage;

public class ElementRelUISpecRegistry extends AbstractElementRelUISpecRegistry {

	private static IElementRelUISpecRegistry instance = null;

	private ElementRelUISpecRegistry() {
		// Singleton: private to hide contructor
	}

	public static IElementRelUISpecRegistry getDefault() {
		if (instance == null)
			instance = new ElementRelUISpecRegistry();
		return instance;
	}

}
