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

public class AttributeRelUISpecRegistry extends
		AbstractAttributeRelUISpecRegistry {

	public static IAttributeRelUISpecRegistry instance = null;

	private AttributeRelUISpecRegistry() {
		// Singleton: private to hide contructor
	}

	public static IAttributeRelUISpecRegistry getDefault() {
		if (instance == null)
			instance = new AttributeRelUISpecRegistry();
		return instance;
	}

}
